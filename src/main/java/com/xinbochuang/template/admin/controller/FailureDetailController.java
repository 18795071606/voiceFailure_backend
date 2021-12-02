package com.xinbochuang.template.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinbochuang.template.admin.domain.FailureDetail;
import com.xinbochuang.template.admin.domain.Flow;
import com.xinbochuang.template.admin.service.IFailureDetailService;
import com.xinbochuang.template.admin.service.IFlowService;
import com.xinbochuang.template.common.controller.BaseController;
import com.xinbochuang.template.framework.web.domain.AjaxResult;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.xml.rpc.ParameterMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 黄晓鹏
 * @date 2020-10-29
 */
@RestController
@RequestMapping("/failure-detail")
public class FailureDetailController extends BaseController {

    @Resource
    private IFailureDetailService failureDetailService;

    @Resource
    private IFlowService iFlowService;
    @Value("${Eoms.baseUrl}")
    private String baseUrl;

    /**
     * 插入报障信息
     *
     * @param detail 报障信息
     * @return 结果
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody FailureDetail detail) {
        detail.setCreateTime(new Date());
        QueryWrapper<Flow> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(Flow::getProductNumber,detail.getProductNumber());
        Flow flow=iFlowService.getOne(queryWrapper);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendEoms(flow,detail);
            }
        }).start();
        return toAjax(failureDetailService.save(detail));
    }

    public void sendEoms(Flow flow,FailureDetail failureDetail) {
        Service service = new Service();
        Call call = null;
        try {
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(baseUrl);
            call.setOperationName("newWorkSheet");
            call.setReturnType(XMLType.XSD_STRING);
            call.addParameter("serSupplier", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serCaller", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callerPwd", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callTime", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("attachRef", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDetail", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("sheetType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serviceType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serialNo", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opPerson", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opCorp", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDepart", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opContact", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opTime", XMLType.XSD_STRING, ParameterMode.IN);

            int sheetType=56;
            int serviceType = 999;
            String serialNo="20170101010101";
            String opPerson="张三";
            String opCorp="公司";
            String opDepart="部门";
            String opContact="1399999999";
            String attachRef="";
            String result = "";
            String detail="";
            if(flow==null){
                detail="<opDetail><recordInfo>" +
                        "<fieldInfo><fieldChName>工单标题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>【集客投诉自报障】用户自报障</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>投诉分类</fieldChName><fieldEnName>type</fieldEnName><fieldContent>"+808080+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaindesc</fieldEnName><fieldContent>"+failureDetail.getDetail()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>CRM流水号</fieldChName><fieldEnName>crmnum</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户编号</fieldChName><fieldEnName>custnum</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户名称</fieldChName><fieldEnName>custname</fieldEnName><fieldContent>"+failureDetail.getProductNumber()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户地址</fieldChName><fieldEnName>custadd</fieldEnName><fieldContent>"+failureDetail.getAddress()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>custlevel</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户服务等级</fieldChName><fieldEnName>custservicelevel</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户经理</fieldChName><fieldEnName>custmanger</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户经理联系电话</fieldChName><fieldEnName>custmangerphone</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>投诉处理时限</fieldChName><fieldEnName>BaseDealOutTime</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>故障及投诉类型</fieldChName><fieldEnName>complaintype</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务类型</fieldChName><fieldEnName>servicetype</fieldEnName><fieldContent>999</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务标识(产品实例标识)</fieldChName><fieldEnName>servicenum</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A所属省、自治区、直辖市、特别行政区</fieldChName><fieldEnName>serviceprovince_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A所属地市</fieldChName><fieldEnName>servicecity_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A所属区县</fieldChName><fieldEnName>servicecounty_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A地址</fieldChName><fieldEnName>serviceadd_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A的客户技术联系人</fieldChName><fieldEnName>servicepeople_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A的客户技术联系人电话</fieldChName><fieldEnName>servicephone_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z所属省、自治区、直辖市、特别行政区</fieldChName><fieldEnName>serviceprovince_z</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z所属地市</fieldChName><fieldEnName>servicecity_z</fieldEnName><fieldContent>"+failureDetail.getCity()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z所属区县</fieldChName><fieldEnName>servicecounty_z</fieldEnName><fieldContent>"+failureDetail.getCounty()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z地址</fieldChName><fieldEnName>serviceadd_z</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z的客户技术联系人</fieldChName><fieldEnName>servicepeople_z</fieldEnName><fieldContent>无</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z的客户技术联系人电话</fieldChName><fieldEnName>servicephone_z</fieldEnName><fieldContent>"+failureDetail.getPhone()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务开通日期</fieldChName><fieldEnName>serviceavailabledate</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "</recordInfo></opDetail>";
            }else{
                 detail="<opDetail><recordInfo>" +
                        "<fieldInfo><fieldChName>工单标题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>【集客投诉自报障】用户自报障</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>投诉分类</fieldChName><fieldEnName>type</fieldEnName><fieldContent>"+808080+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaindesc</fieldEnName><fieldContent>"+failureDetail.getDetail()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>CRM流水号</fieldChName><fieldEnName>crmnum</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户编号</fieldChName><fieldEnName>custnum</fieldEnName><fieldContent>"+flow.getCustomNo()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户名称</fieldChName><fieldEnName>custname</fieldEnName><fieldContent>"+flow.getCustomName()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户地址</fieldChName><fieldEnName>custadd</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>custlevel</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户服务等级</fieldChName><fieldEnName>custservicelevel</fieldEnName><fieldContent>"+flow.getCustomServiceLevel()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户经理</fieldChName><fieldEnName>custmanger</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>客户经理联系电话</fieldChName><fieldEnName>custmangerphone</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>投诉处理时限</fieldChName><fieldEnName>BaseDealOutTime</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>故障及投诉类型</fieldChName><fieldEnName>complaintype</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务类型</fieldChName><fieldEnName>servicetype</fieldEnName><fieldContent>999</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务标识(产品实例标识)</fieldChName><fieldEnName>servicenum</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A所属省、自治区、直辖市、特别行政区</fieldChName><fieldEnName>serviceprovince_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A所属地市</fieldChName><fieldEnName>servicecity_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A所属区县</fieldChName><fieldEnName>servicecounty_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A地址</fieldChName><fieldEnName>serviceadd_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A的客户技术联系人</fieldChName><fieldEnName>servicepeople_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点A的客户技术联系人电话</fieldChName><fieldEnName>servicephone_a</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z所属省、自治区、直辖市、特别行政区</fieldChName><fieldEnName>serviceprovince_z</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z所属地市</fieldChName><fieldEnName>servicecity_z</fieldEnName><fieldContent>"+flow.getCity()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z所属区县</fieldChName><fieldEnName>servicecounty_z</fieldEnName><fieldContent>"+flow.getCounty()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z地址</fieldChName><fieldEnName>serviceadd_z</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z的客户技术联系人</fieldChName><fieldEnName>servicepeople_z</fieldEnName><fieldContent>无</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务端点Z的客户技术联系人电话</fieldChName><fieldEnName>servicephone_z</fieldEnName><fieldContent>"+failureDetail.getPhone()+"</fieldContent></fieldInfo>" +
                        "<fieldInfo><fieldChName>业务开通日期</fieldChName><fieldEnName>serviceavailabledate</fieldEnName><fieldContent></fieldContent></fieldInfo>" +
                        "</recordInfo></opDetail>";
            }


            DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String localname= dtf2.format(LocalDateTime.now());
            System.out.println("request:"+detail);
            result = (String) call.invoke(new Object[]{"EOMSZXTEST", "EOMS", "123", localname, attachRef,detail,sheetType,serviceType,serialNo,	opPerson,opCorp,opDepart,opContact,	localname});
            System.out.println("result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/test")
    public AjaxResult test() {
        Service service = new Service();
        Call call = null;
        try {
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(baseUrl);
//			call.setOperationName("queryWorkSheet");
            call.setOperationName("newWorkSheet");
            call.setReturnType(XMLType.XSD_STRING);
            call.addParameter("serSupplier", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serCaller", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callerPwd", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("callTime", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("attachRef", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDetail", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("sheetType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serviceType", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("serialNo", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opPerson", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opCorp", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opDepart", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opContact", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("opTime", XMLType.XSD_STRING, ParameterMode.IN);

            int sheetType=56;
            int serviceType = 999;
            String serialNo="20170101010101";
            String opPerson="张三";
            String opCorp="公司";
            String opDepart="部门";
            String opContact="1399999999";
            String opTime="2014-2-26 16:00:00";



//			String attachRef="<attachRef><attachInfo><attachName>7980d458a021769</attachName><attachURL>ftp://bpp:Eoms123#$@10.236.139.60/bpp/attaches/nxeoms/NX_TTM_HBH/2017-06-17/7980d458a021769</attachURL><attachLength>100</attachLength></attachInfo></attachRef>";
//			String attachRef="<attachRef><attachInfo><attachName>gc.log</attachName><attachURL>ftp://bpp:Eoms123#$@10.236.139.60/bpp/gc.log</attachURL><attachLength>100</attachLength></attachInfo></attachRef>";
            String attachRef="";
            String result = "";
            //String detail = readTxtFile("D:\\workspace\\szn\\eoms\\EOMSV4.3\\src\\interfaces\\com\\ultrapower\\wfinterface\\services\\gdc\\eoms派单报文.xml");
//            String detail = readTxtFile("D:\\hanyang\\MyEclispe6.5\\workspace-eoms\\EOMSV4.3\\src\\interfaces\\com\\ultrapower\\wfinterface\\services\\gdc\\eoms派单报文.xml");
            String detail="<opDetail><recordInfo><fieldInfo><fieldChName>工单标题</fieldChName><fieldEnName>title</fieldEnName><fieldContent>四星级客户投诉1次下派,处理时限:12.00小时-受理号码(13469529006)-联系电话(134****9006)-处理时限(12.00小时)-故障地市(石嘴山)-故障区县(0951)-详细地址(石嘴山舍 予 园幼儿园  )-投诉问题(集团业务→网络质量→专线专网→互联网专线→功能使用→专线中断→全局流转)</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉分类</fieldChName><fieldEnName>type</fieldEnName><fieldContent>03041502030400</fieldContent></fieldInfo><fieldInfo><fieldChName>投诉内容</fieldChName><fieldEnName>complaindesc</fieldEnName><fieldContent>#同步要素#故障地市/区县:宁夏→石嘴山→大武口故障详细地址:石嘴山舍 予 园幼儿园  客户要求:客户要求尽快处理并回复，表示现在整个楼的网都不能使用，客户都没有办法正常办公了。是否提取:#同步要素#</fieldContent></fieldInfo><fieldInfo><fieldChName>CRM流水号</fieldChName><fieldEnName>crmnum</fieldEnName><fieldContent>20200922143945X785449329</fieldContent></fieldInfo><fieldInfo><fieldChName>客户编号</fieldChName><fieldEnName>custnum</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>客户名称</fieldChName><fieldEnName>custname</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>客户地址</fieldChName><fieldEnName>custadd</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>客户级别</fieldChName><fieldEnName>custlevel</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>客户服务等级</fieldChName><fieldEnName>custservicelevel</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>客户经理</fieldChName><fieldEnName>custmanger</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>客户经理联系电话</fieldChName><fieldEnName>custmangerphone</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>投诉处理时限</fieldChName><fieldEnName>BaseDealOutTime</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>故障及投诉类型</fieldChName><fieldEnName>complaintype</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务类型</fieldChName><fieldEnName>servicetype</fieldEnName><fieldContent>999</fieldContent></fieldInfo><fieldInfo><fieldChName>业务标识(产品实例标识)</fieldChName><fieldEnName>servicenum</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点A所属省、自治区、直辖市、特别行政区</fieldChName><fieldEnName>serviceprovince_a</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点A所属地市</fieldChName><fieldEnName>servicecity_a</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点A所属区县</fieldChName><fieldEnName>servicecounty_a</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点A地址</fieldChName><fieldEnName>serviceadd_a</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点A的客户技术联系人</fieldChName><fieldEnName>servicepeople_a</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点A的客户技术联系人电话</fieldChName><fieldEnName>servicephone_a</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点Z所属省、自治区、直辖市、特别行政区</fieldChName><fieldEnName>serviceprovince_z</fieldEnName><fieldContent></fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点Z所属地市</fieldChName><fieldEnName>servicecity_z</fieldEnName><fieldContent>石嘴山</fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点Z所属区县</fieldChName><fieldEnName>servicecounty_z</fieldEnName><fieldContent>大武口</fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点Z地址</fieldChName><fieldEnName>serviceadd_z</fieldEnName><fieldContent>石嘴山舍 予 园幼儿园  </fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点Z的客户技术联系人</fieldChName><fieldEnName>servicepeople_z</fieldEnName><fieldContent>李*</fieldContent></fieldInfo><fieldInfo><fieldChName>业务端点Z的客户技术联系人电话</fieldChName><fieldEnName>servicephone_z</fieldEnName><fieldContent>13469529006</fieldContent></fieldInfo><fieldInfo><fieldChName>业务开通日期</fieldChName><fieldEnName>serviceavailabledate</fieldEnName><fieldContent></fieldContent></fieldInfo></recordInfo></opDetail>";

//			result = (String) call.invoke(new Object[]{"ID-DUC-20160922-10006","EOMS111111", "alarm", "", "2014-2-26 16:00:00", "<opDetail><recordInfo><fieldInfo><fieldChName>CREATEUSER</fieldChName><fieldEnName>CREATEUSER</fieldEnName><fieldContent>shizenan</fieldContent></fieldInfo><fieldInfo><fieldChName>TTHBASESN</fieldChName><fieldEnName>TTHBASESN</fieldEnName><fieldContent>ID-051-161031-00538</fieldContent></fieldInfo><fieldInfo><fieldChName>NetUnitName</fieldChName><fieldEnName>NetUnitName</fieldEnName><fieldContent>1</fieldContent></fieldInfo><fieldInfo><fieldChName>INC_City</fieldChName><fieldEnName>INC_City</fieldEnName><fieldContent>1</fieldContent></fieldInfo><fieldInfo><fieldChName>BaseAcceptOutTime</fieldChName><fieldEnName>BaseAcceptOutTime</fieldEnName><fieldContent>1</fieldContent></fieldInfo><fieldInfo><fieldChName>BaseDealOutTime</fieldChName><fieldEnName>BaseDealOutTime</fieldEnName><fieldContent>1</fieldContent></fieldInfo><fieldInfo><fieldChName>INC_HappenTime</fieldChName><fieldEnName>INC_HappenTime</fieldEnName><fieldContent>1</fieldContent></fieldInfo><fieldInfo><fieldChName>INC_County</fieldChName><fieldEnName>INC_County</fieldEnName><fieldContent>1</fieldContent></fieldInfo></recordInfo></opDetail>"});
//			result = (String) call.invoke(new Object[]{"BOMC", "EOMS", "123", "2014-2-26 16:00:00", "","<opDetail><recordInfo><fieldInfo><fieldChName>建单人</fieldChName><fieldEnName>CREATEUSER</fieldEnName><fieldContent>Demo</fieldContent></fieldInfo><fieldInfo><fieldChName>客户编号</fieldChName><fieldEnName>custnum</fieldEnName><fieldContent>998877</fieldContent></fieldInfo><fieldInfo><fieldChName>客户地址</fieldChName><fieldEnName>CUSTADD</fieldEnName><fieldContent>一二三四abc</fieldContent></fieldInfo><fieldInfo><fieldChName>客户经理联系电话</fieldChName><fieldEnName>custmangerphone</fieldEnName><fieldContent>13999999999</fieldContent></fieldInfo></recordInfo></opDetail>"});
//			result = (String) call.invoke(new Object[]{"BOMC", "EOMS", "123", "2014-2-26 16:00:00", attachRef,detail,"99"});
            result = (String) call.invoke(new Object[]{"EOMSZXTEST", "EOMS", "123", "2014-2-26 16:00:00", attachRef,detail,	sheetType,serviceType,serialNo,	opPerson,opCorp,opDepart,opContact,	opTime	});
            System.out.println("result:" + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return toAjax(true);
    }

}

