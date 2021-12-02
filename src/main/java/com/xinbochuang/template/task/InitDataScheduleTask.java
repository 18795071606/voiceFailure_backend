package com.xinbochuang.template.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.xinbochuang.template.admin.domain.Flow;
import com.xinbochuang.template.admin.service.IFlowService;
import com.xinbochuang.template.common.utils.FtpParams;
import com.xinbochuang.template.common.utils.FtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling
public class InitDataScheduleTask {
    @Autowired
    private FtpParams ftpParams;
    @Autowired
    private IFlowService iFlowService;
    //3.添加定时任务
    @Scheduled(cron = "0 30 * * * ?")
//    @Scheduled(cron = "* 0/2 * * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: "+ftpParams.getLocalPath()+"   " + LocalDateTime.now());
        String path = FtpUtils.downloadFile(ftpParams, "JIKEBAOZHANG.csv");
//        String path="/Users/jessie/Documents/workspace/upload/excel/JIKEBAOZHANG.csv";
        List<Flow> flows = readFromCSV(path);
        List<String> allhasList=new ArrayList<>();
        for(Flow flow:flows){
            allhasList.add(flow.getProductNumber());
        }
        QueryWrapper<Flow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("product_number",allhasList);
        List<Flow> list = iFlowService.list(queryWrapper);
        List<String> hasList=new ArrayList<>();
        for(Flow flow:list){
            hasList.add(flow.getProductNumber());
        }
        for(String proNum:hasList){
            for(Flow flow:flows){
                if(proNum.equals(flow.getProductNumber())){
                    flows.remove(flow);
                    break;
                }
            }
        }
        int size=0;
        System.err.println("执行静态定时任务时间: "+ JSONUtil.toJsonStr(flows.get(0)));
        for(int i=0;i<Math.ceil(flows.size()/1000)+1;i++){
            List<Flow> flowList=new ArrayList<>();
            if((i+1)*1000>flows.size()){
                flowList=flows.subList(i*1000,flows.size());
            }else{
                flowList=flows.subList(i*1000,(i+1)*1000);
            }
            size+=flowList.size();
            iFlowService.saveBatch(flowList);
        }
        System.out.println("========="+size);
        String writePath = writeIntoCSV(flows);
        FtpUtils.uploadFile(ftpParams,writePath,"JIKEBAOZHANGSCAN.csv");


    }
    public List<Flow> readFromCSV(String filePath) {
        Date date = new Date();
//        String yyyyMMddHHmmss = DateUtil.format(date, "yyyyMMddHHmmss");
//        int id=Integer.valueOf(yyyyMMddHHmmss);
        List<String> hasids=new ArrayList<>();
        CsvReader reader = null;
        List<Flow> result = new ArrayList<>();
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(filePath, '|', Charset.forName("GBK"));

            // 读取标题
            reader.readHeaders();
            int index=0;
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
                index++;
//                System.out.println("======"+index+"====="+reader.get(6));
                if(index==1){
                    continue;
                }
                if(hasids.contains(reader.get(6))){
                    continue;
                }
                hasids.add(reader.get(6));
                //读取指定名字的列
                Flow flow=new Flow();
//                flow.setId(id+index);
                flow.setCity(reader.get(0));
                flow.setCounty(reader.get(1));
                flow.setFlowType(reader.get(2).replace("开通流程式","").replace("开通",""));
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
     * @param strList   对应CSV中的一行记录
     */
    public String writeIntoCSV( List<Flow> strList) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String localname= dtf2.format(LocalDateTime.now());
        File localFile = new File(ftpParams.getLocalPath() + "/" + localname+"_JIKEBAOZHANGSCAN.csv");
        CsvWriter csvWriter = null;

        try {
            localFile.createNewFile();
            // 创建CSV写对象
            csvWriter = new CsvWriter(localFile.getAbsolutePath(), '|', Charset.forName("UTF-8"));
            // 写标题
            for (Flow flow : strList) {
                String[] writeLine = new String[2];
                // 集合转数组
                writeLine[0]=flow.getProductNumber();
                writeLine[1]=ftpParams.getFirstPath()+flow.getProductNumber();
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

}
