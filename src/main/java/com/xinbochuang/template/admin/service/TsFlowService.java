package com.xinbochuang.template.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinbochuang.template.admin.domain.TsFlow;
import com.xinbochuang.template.admin.domain.TsOrderType;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xueli
 * @date 2021-8-13
 */
public interface TsFlowService extends IService<TsFlow> {

    void insertTsFlowByZJ(List<String> list);

    TsFlow findTsFlowByKdzh(String kdzh, String phone);
}
