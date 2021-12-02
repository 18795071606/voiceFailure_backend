package com.xinbochuang.template.admin.controller;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 投诉工单Eoms取数据
 * @author xueli
 * @date 2021-8-26
 */
@Component
@EnableScheduling
public class TsOrderSyncDataController {

    @Scheduled(cron = "0 0/1 * * * ?")
    public void syncData(){
        String EomsUrl = "";
        Service service = new Service();
        Call call = null;
        try {

        }catch (Exception e){

        }
    }
}
