package com.xinbochuang.template.admin.controller;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.xinbochuang.template.admin.domain.Flow;
import com.xinbochuang.template.admin.domain.TsFlow;
import com.xinbochuang.template.admin.domain.TsFlowDetail;
import com.xinbochuang.template.admin.service.IFlowService;
import com.xinbochuang.template.admin.service.IOrderTypeService;
import com.xinbochuang.template.admin.service.TsFlowDetailService;
import com.xinbochuang.template.admin.service.TsFlowService;
import com.xinbochuang.template.common.controller.BaseController;
import com.xinbochuang.template.common.utils.FtpParams;
import com.xinbochuang.template.common.utils.FtpUtils;
import com.xinbochuang.template.common.utils.StringUtils;
import com.xinbochuang.template.framework.web.domain.AjaxResult;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.rpc.ParameterMode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author 黄晓鹏
 * @date 2020-10-29
 */
@RestController
@RequestMapping("/flow")
public class FlowController extends BaseController {

    @Resource
    private IFlowService iFlowService;

    @Resource
    private TsFlowDetailService tsFlowDetailService;

    @Autowired
    private FtpParams ftpParams;

    @Autowired
    private IOrderTypeService orderTypeService;

    @Resource
    private TsFlowService tsFlowService;

    /**
     * 根据产品标识获取工单信息
     *
     * @param id 产品标识
     * @return 工单信息
     */
    @GetMapping("/getInfo/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return AjaxResult.data(iFlowService.getOne(Wrappers.<Flow>lambdaQuery().eq(Flow::getProductNumber, id)));
    }

    @GetMapping("/initData")
    public void initData() {
        System.err.println("执行静态定时任务时间: " + ftpParams.getLocalPath() + "   " + LocalDateTime.now());
//        String path = FtpUtils.downloadFile(ftpParams, "JIKEBAOZHANG.csv");
        String path="/Users/jessie/Documents/workspace/upload/excel/JIKEBAOZHANG.csv";
        List<Flow> flows = readFromCSV(path);
        List<String> allhasList = new ArrayList<>();
        for (Flow flow : flows) {
            allhasList.add(flow.getProductNumber());
        }
        QueryWrapper<Flow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_number", allhasList);
        List<Flow> list = iFlowService.list(queryWrapper);
        List<String> hasList = new ArrayList<>();
        for (Flow flow : list) {
            hasList.add(flow.getProductNumber());
        }
        for (String proNum : hasList) {
            for (Flow flow : flows) {
                if (proNum.equals(flow.getProductNumber())) {
                    flows.remove(flow);
                    break;
                }
            }
        }
        int size = 0;
        System.err.println("执行静态定时任务时间: " + JSONUtil.toJsonStr(flows.get(0)));
        for (int i = 0; i < Math.ceil(flows.size() / 1000) + 1; i++) {
            List<Flow> flowList = new ArrayList<>();
            if ((i + 1) * 1000 > flows.size()) {
                flowList = flows.subList(i * 1000, flows.size());
            } else {
                flowList = flows.subList(i * 1000, (i + 1) * 1000);
            }
            size += flowList.size();
            iFlowService.saveBatch(flowList);
        }
        System.out.println("=========" + size);
        String writePath = writeIntoCSV(flows);
        FtpUtils.uploadFile(ftpParams, writePath, "JIKEBAOZHANGSCAN.csv");
    }
    public List<Flow> readFromCSV(String filePath) {
        Date date = new Date();
//        String yyyyMMddHHmmss = DateUtil.format(date, "yyyyMMddHHmmss");
//        int id=Integer.valueOf(yyyyMMddHHmmss);
        List<String> hasids = new ArrayList<>();
        CsvReader reader = null;
        List<Flow> result = new ArrayList<>();
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(filePath, '|', Charset.forName("GBK"));

            // 读取标题
            reader.readHeaders();
            int index = 0;
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
                index++;
//                System.out.println("======"+index+"====="+reader.get(6));
                if (index == 1) {
                    continue;
                }
                if (hasids.contains(reader.get(6))) {
                    continue;
                }
                hasids.add(reader.get(6));
                //读取指定名字的列
                Flow flow = new Flow();
//                flow.setId(id+index);
                flow.setCity(reader.get(0));
                flow.setCounty(reader.get(1));
                flow.setFlowType(reader.get(2).replace("开通流程式", "").replace("开通", ""));
                flow.setFlowNo(reader.get(3));
                flow.setCustomName(reader.get(4));
                flow.setCustomNo(reader.get(5));
                flow.setProductNumber(reader.get(6));
                flow.setCustomServiceLevel(reader.get(7));
                flow.setBusinessLevel(reader.get(8));
                flow.setBandWidth(reader.get(9));
                flow.setBusinessName(reader.get(10));
                flow.setBusinessPhone(reader.get(11));
                flow.setDispatchName(reader.get(12));
                flow.setCreateTime(new Date());

                result.add(flow);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return result;
    }


    /**
     * Write into CSV
     *
     * @param strList 对应CSV中的一行记录
     */
    public String writeIntoCSV(List<Flow> strList) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String localname = dtf2.format(LocalDateTime.now());
        File localFile = new File(ftpParams.getLocalPath() + "/" + localname + "_JIKEBAOZHANGSCAN.csv");
        CsvWriter csvWriter = null;

        try {
            localFile.createNewFile();
            // 创建CSV写对象
            csvWriter = new CsvWriter(localFile.getAbsolutePath(), '|', Charset.forName("UTF-8"));
            // 写标题
            for (Flow flow : strList) {
                String[] writeLine = new String[2];
                // 集合转数组
                writeLine[0] = flow.getProductNumber();
                writeLine[1] = ftpParams.getFirstPath() + flow.getProductNumber();
                csvWriter.writeRecord(writeLine);
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != csvWriter) {
                csvWriter.close();
            }
        }
        return localFile.getAbsolutePath();
    }

    /**
     * 根据宽带账号查询装机信息
     * @author xueli
     * @date 2021-8-18
     * @param kdzh
     * @return
     */
    @RequestMapping("/getTsFlowInfo/{kdzh}")
    public AjaxResult getTsFlowInfo(@PathVariable String kdzh){
        //根据宽带账号在库里面查
        TsFlow flow = tsFlowService.getOne(Wrappers.<TsFlow>lambdaQuery().eq(TsFlow::getPhone, kdzh));
        if(flow != null){
            if(!"99".equals(flow.getState())){
                return AjaxResult.error("您当前有一个投诉工单未归档");
            }
            flow.setUrlTime(new Date());//设置打开时间
            return AjaxResult.data(flow);
        }

        String webServerUrl= "http://10.227.222.133:8089/ZhzyWebService/services/IRMS?wsdl";
        Service service = new Service();
        Call call = null;
        try {
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(webServerUrl);
            call.setOperationName("BroadbandQuery");
            call.setReturnType(XMLType.XSD_STRING);
            call.addParameter("customer_account", XMLType.XSD_STRING, ParameterMode.IN);
            String result = (String) call.invoke(new Object[]{kdzh});
            System.out.println("综资接口返回的数据：" + result);
            if(result == null || result.isEmpty()){
                System.out.println("综资接口返回为空");
            }
            Map<Object,Object> map = getElement(result);
            return AjaxResult.data(insertDataFlow(map,kdzh));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存数据
     * @author xueli
     * @date 2021-8-18
     * @param map
     * @return
     */
    public TsFlow insertDataFlow(Map<Object,Object> map,String phone){
        TsFlow tsFlow = new TsFlow();
        if(map != null && !map.isEmpty()){
            tsFlow.setKdzh(map.get("customer_account").toString());
            tsFlow.setPhone(phone);
            tsFlow.setTitle(appendTitle(map));
            tsFlow.setCity(map.get("city_name").toString());
            tsFlow.setCounty(map.get("county_name").toString());
            tsFlow.setHousing(map.get("zh_label").toString());
            tsFlow.setAddress(map.get("full_addr").toString());
            tsFlow.setTerritory(map.get("Type").toString());
            tsFlow.setMaintainteamName(map.get("maintainteam_name").toString());
            tsFlow.setTeamId(map.get("team_id").toString());
            tsFlow.setRoute(map.get("Route").toString());
            tsFlow.setBusinessType(map.get("business_type").toString());
            tsFlow.setReSubstation(map.get("re_substation").toString());
        }
        tsFlowService.save(tsFlow);
        return tsFlow;
    }

    /**
     * 拼接主题
     * @author xueli
     * @date 2021-8-20
     * @param map
     * @return
     */
    public String appendTitle(Map<Object,Object> map){
        StringBuilder builder = new StringBuilder();
        builder.append("三星级客户投诉1次下派,处理时限:32.00小时").
                append("-宽带装机地址(").
                append(map.get("full_addr")+")").
                append("-投诉问题(家宽业务→网络质量→家庭宽带→全局流转→功能使用→网络连接掉线→全局流转)");
        return builder.toString();
    }

    /**
     * 验证二维码的有效性
     * @author xueli
     * @date 2021-8-16
     * @param id
     * @return
     */
    @RequestMapping("/checkStatus")
    public AjaxResult checkStatus(@PathVariable String id){
        //在二维码库中查询此二维码信息
        return AjaxResult.error("此二维码已失效！");
    }

    /**
     * 查询投诉工单类型
     * @author xueli
     * @date 2021-8-13
     * @return
     */
    @RequestMapping("/getOrdersData")
    @ResponseBody
    public List<Map<String,Object>> getOrdersTypeData(){
        return orderTypeService.findTsOrdersTree("0");
    }

    /**
     * 根据pid查询子数据
     * @param pid
     * @return
     */
    @RequestMapping("/getSelectData")
    @ResponseBody
    public List<Map<String,Object>> getSelecData(@RequestParam(value = "pid") String pid){
        return orderTypeService.findSelectChildByPid(pid);
    }

    /**
     * 保存投诉工单并发送到Emos
     * @author xueli
     * @date 2021-8-16
     * @param tsFlowDetail
     * @return
     */
    @RequestMapping("/add")
    public AjaxResult addAndEmos(@RequestBody TsFlowDetail tsFlowDetail){
        tsFlowDetail.setSubTime(new Date());
        QueryWrapper<TsFlow> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(TsFlow::getKdzh,tsFlowDetail.getKdzh());
        TsFlow tsFlow = tsFlowService.getOne(queryWrapper);
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendEoms(tsFlow,tsFlowDetail);
                }
            }).start();
            return toAjax(tsFlowDetailService.save(tsFlowDetail));
        }catch (Exception e){
            e.printStackTrace();
            return AjaxResult.error("操作失败！");
        }
    }


    /**
     * 构造数据发送到Eoms
     * @author xueli
     * @date 2021-8-18
     * @param tsFlow
     * @param tsFlowDetail
     */
    public void sendEoms(TsFlow tsFlow,TsFlowDetail tsFlowDetail){
        String eomsUrl = "http://10.236.139.148:8100/eoms/services/createSHBHByInter";//测试地址
        //String eomsUrl = "http://10.227.222.245:8100/eoms/services/createSHBHByInter";//正式地址
        Service service = new Service();
        Call call = null;
        try {
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(eomsUrl);
            call.setOperationName("createSHBHByInter");
            call.setReturnType(XMLType.XSD_STRING);
            //参数
            call.addParameter("sheetType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serviceType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serialNo", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serSupplier", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serCaller", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callerPwd", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callTime", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("attachRef", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opPerson", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opCorp", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDepart", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opContact", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opTime", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDetail", XMLType.XSD_STRING, ParameterMode.IN);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int sheetType = 20; //工单类型
            int serviceType = 10; //服务类别
            String serialNo = String.valueOf(System.currentTimeMillis());//流水号
            String callerPwd = "";          //密码
            String callTime = dtf.format(LocalDateTime.now());
            String opPerson = "二维码建单";  //操作人
            String opCorp = "鑫博创";       //操作人公司
            String opDepart = "部门";       //操作人单位
            String opContact = "13999999999";         //操作人联系电话
            String opTime = dtf.format(LocalDateTime.now());

            //属性值
            String result = "";
            String detail=
                    "<opDetail>" +
                        "<recordInfo>" +
                            "<fieldInfo><fieldChName>主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>"+tsFlow.getTitle()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉分类</fieldChName><fieldEnName>complaintType</fieldEnName><fieldContent>"+tsFlowDetail.getTsType()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>客户姓名</fieldChName><fieldEnName>customerName</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>联系电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>"+tsFlow.getPhone()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>主叫号码</fieldChName><fieldEnName>CallerNo</fieldEnName><fieldContent>"+tsFlowDetail.getMobile()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>被叫号码</fieldChName><fieldEnName>CalledNo</fieldEnName><fieldContent>"+tsFlowDetail.getMobile()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉受理地市</fieldChName><fieldEnName>startDealCity</fieldEnName><fieldContent>"+getCode(tsFlow.getCity())+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>"+tsFlow.getCity()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>归属区县</fieldChName><fieldEnName>faultDistrtId</fieldEnName><fieldContent>"+tsFlow.getCounty()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>"+tsFlowDetail.getSubTime()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>"+tsFlow.getPhone()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent>"+tsFlowDetail.getSubTime()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>"+tsFlow.getAddress()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>"+tsFlowDetail.getContent()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>区域属性</fieldChName><fieldEnName>AreaParam</fieldEnName><fieldContent>"+tsFlow.getTerritory()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>宽带帐号</fieldChName><fieldEnName>lanAccount</fieldEnName><fieldContent>"+tsFlow.getKdzh()+"</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>代维单位</fieldChName><fieldEnName>repairCompany</fieldEnName><fieldContent>"+tsFlow.getMaintainteamName()+"</fieldContent></fieldInfo>" +
                        "</recordInfo>" +
                    "</opDetail>";
//            System.out.println("request:"+detail);

            System.out.println(detail);
            result = (String) call.invoke(new Object[]{sheetType,serviceType,serialNo,"EOMS","RCXT",callerPwd,callTime,null,opPerson,opCorp,opDepart,opContact,opTime,detail});
            System.out.println("result:" + result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("向Eoms推送数据错误!");
        }
    }

    //返回地市编码
    public String getCode(String cityName){
        String code = "";
        if("银川".equals(cityName)){
            code = "001";
        }else if("石嘴山".equals(cityName)){
            code = "002";
        }else if("吴忠".equals(cityName)){
            code = "003";
        }else if("固原".equals(cityName)){
            code = "004";
        }else{
            code = "005";
        }
        return code;
    }

    /**
     * 解析xml
     * @param xml
     * @return
     * @throws Exception
     */
    public static Map<Object, Object> getElement(String xml ) throws Exception{
        Map<Object, Object> mapXML = new HashMap<Object, Object>();
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        //读取文件 转换成Document
        Document doc = DocumentHelper.parseText(xml);
        // 获取根节点
        Element rootElt = doc.getRootElement();
        // 拿到根节点的名称
        System.out.println("根节点：" + rootElt.getName());
        //获取根节点下的所有子节点
        Iterator it = rootElt.elementIterator();
        //遍历
        while (it.hasNext()) {
            Element element = (Element) it.next();
            System.out.println(element.getName()+"------>>>"+element.getStringValue());
            mapXML.put(element.getName(),element.getStringValue());
        }
        return mapXML;
    }

    /**
     * 查询
     * @param kdzh
     * @return
     */
    @RequestMapping("/searchTs")
    public AjaxResult searchTs(@RequestParam(value = "kdzh") String kdzh,@RequestParam(value = "phone") String phone){
        if(StringUtils.isEmpty(kdzh) && StringUtils.isEmpty(phone)){
            return AjaxResult.data(null);
        }
        TsFlow tsFlow = tsFlowService.findTsFlowByKdzh(kdzh,phone);
        return AjaxResult.data(tsFlow);
    }

    /**
     * 禁用、启用
     * @param status
     * @return
     */
    @RequestMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(@RequestParam(value = "id") String id,@RequestParam(value = "status") String status){
        TsFlow flow = tsFlowService.getOne(Wrappers.<TsFlow>lambdaQuery().eq(TsFlow::getId, id));
        flow.setStatus(status);
        tsFlowService.updateById(flow);
        return AjaxResult.ok();
    }

    public static void main(String[] args) throws Exception {

        String eomsUrl = "http://10.236.139.148:8100/eoms/services/createSHBHByInter";//测试地址
        //String eomsUrl = "http://10.227.222.245:8100/eoms/services/createSHBHByInter";//正式地址
        Service service = new Service();
        Call call = null;
        try {
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(eomsUrl);
            call.setOperationName("createSHBHByInter");
            call.setReturnType(XMLType.XSD_STRING);
            //参数
            call.addParameter("sheetType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serviceType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serialNo", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serSupplier", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serCaller", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callerPwd", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callTime", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("attachRef", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opPerson", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opCorp", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDepart", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opContact", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opTime", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDetail", XMLType.XSD_STRING, ParameterMode.IN);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int sheetType = 20; //工单类型
            int serviceType = 10; //服务类别
            String serialNo = String.valueOf(System.currentTimeMillis());//流水号
            String callerPwd = "";          //密码
            String callTime = dtf.format(LocalDateTime.now());
            String opPerson = "二维码建单";  //操作人
            String opCorp = "鑫博创";       //操作人公司
            String opDepart = "部门";       //操作人单位
            String opContact = "13999999999";         //操作人联系电话
            String opTime = dtf.format(LocalDateTime.now());

            //属性值
            String result = "";
            String detail=
            "<opDetail>" +
                        "<recordInfo>" +
                            "<fieldInfo><fieldChName>主题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>三星级客户投诉1次下派,处理时限:32.00小时-宽带装机地址(银川|贺兰县|习岗镇|意湖路|月湖名邸|西B区|17号楼|1单元|402)-投诉问题(家宽业务→网络质量→家庭宽带→全局流转→功能使用→网络连接掉线→全局流转)</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉分类</fieldChName><fieldEnName>complaintType</fieldEnName><fieldContent>100021</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>客户姓名</fieldChName><fieldEnName>customerName</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>联系电话</fieldChName><fieldEnName>customPhone</fieldEnName><fieldContent>13995381286</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>主叫号码</fieldChName><fieldEnName>CallerNo</fieldEnName><fieldContent>13995381286</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>被叫号码</fieldChName><fieldEnName>CalledNo</fieldEnName><fieldContent>13995381286</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>customLevel</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉受理地市</fieldChName><fieldEnName>startDealCity</fieldEnName><fieldContent>001</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>用户归属地</fieldChName><fieldEnName>customAttribution</fieldEnName><fieldContent>银川</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>归属区县</fieldChName><fieldEnName>faultDistrtId</fieldEnName><fieldContent>金凤区</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉时间</fieldChName><fieldEnName>complaintTime</fieldEnName><fieldContent>2021-9-1 10:52:26</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>故障号码</fieldChName><fieldEnName>complaintNum</fieldEnName><fieldContent>13995381286</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>故障时间</fieldChName><fieldEnName>faultTime</fieldEnName><fieldContent>2021-9-1 10:26:21</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>故障地点</fieldChName><fieldEnName>faultSite</fieldEnName><fieldContent>银川|贺兰县|习岗镇|意湖路|月湖名邸|西B区|17号楼|1单元|402</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaintDesc</fieldEnName><fieldContent>投诉内容123123</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>区域属性</fieldChName><fieldEnName>AreaParam</fieldEnName><fieldContent>城市</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>宽带帐号</fieldChName><fieldEnName>lanAccount</fieldEnName><fieldContent>k95113995381286</fieldContent></fieldInfo>" +
                            "<fieldInfo><fieldChName>代维单位</fieldChName><fieldEnName>repairCompany</fieldEnName><fieldContent>中移铁通中心支局</fieldContent></fieldInfo>" +
                        "</recordInfo>" +
            "</opDetail>";
            System.out.println(detail);
            result = (String) call.invoke(new Object[]{sheetType,serviceType,serialNo,"EOMS","RCXT",callerPwd,callTime,null,opPerson,opCorp,opDepart,opContact,opTime,detail});
            System.out.println("result:" + result);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("向Eoms推送数据错误!");
        }
    }
}

