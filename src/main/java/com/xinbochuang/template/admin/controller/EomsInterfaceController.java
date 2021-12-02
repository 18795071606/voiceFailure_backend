package com.xinbochuang.template.admin.controller;

import com.xinbochuang.template.admin.domain.TsFlow;
import com.xinbochuang.template.admin.service.TsFlowService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/syncApi")
public class EomsInterfaceController{

    @Resource
    private TsFlowService tsFlowService;

    /**
     * Eoms投诉工单闭环后，置状态
     * @author xueli
     * @date 2021-9-3
     * @param lanAccount
     * @param state
     * @return
     */
    @RequestMapping("/orderDeath")
    @ResponseBody
    public Map<String,Object> SyncDataEoms(String lanAccount, String state){
        Map<String,Object> resultMap = new HashMap<>();
        if(lanAccount == null || lanAccount.isEmpty()){
            resultMap.put("code","500");
            resultMap.put("status",false);
            resultMap.put("message","宽带账号不能为空！");
        }
       TsFlow tsFlow = tsFlowService.findTsFlowByKdzh(lanAccount,"");
       tsFlow.setState(state);
       boolean flag = tsFlowService.updateById(tsFlow);
        if(flag){
            resultMap.put("code","200");
            resultMap.put("status",true);
            resultMap.put("message","状态修改成功！");
        }
        return resultMap;
    }

}
