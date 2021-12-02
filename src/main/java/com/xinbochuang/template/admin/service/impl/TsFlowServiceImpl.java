package com.xinbochuang.template.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinbochuang.template.admin.domain.TsFlow;
import com.xinbochuang.template.admin.mapper.TsFlowMapper;
import com.xinbochuang.template.admin.service.TsFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author xueli
 * @date 2021-8-13
 */
@Service
public class TsFlowServiceImpl extends ServiceImpl<TsFlowMapper, TsFlow> implements TsFlowService {

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void insertTsFlowByZJ(List<String> list) {
        List<TsFlow> tsFlowList = null;
        if(list != null && !list.isEmpty()){
            for(String entry : list){
                TsFlow tsFlow = new TsFlow();
                String dataArr[] = entry.split("|");
                if(dataArr != null && dataArr.length > 0){
                   tsFlow.setKdzh(dataArr[0]);
                   tsFlow.setPhone(dataArr[1]);
                   tsFlow.setStatus(dataArr[4]);
                   tsFlow.setAddress(dataArr[5]);
                   tsFlowList.add(tsFlow);
               }
            }
            this.saveBatch(tsFlowList);
        }
    }

    @Override
    public TsFlow findTsFlowByKdzh(String kdzh, String phone) {
        return baseMapper.findTsFlowByKdzh(kdzh,phone);
    }
}
