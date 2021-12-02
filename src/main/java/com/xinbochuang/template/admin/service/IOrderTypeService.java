package com.xinbochuang.template.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinbochuang.template.admin.domain.TsOrderType;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xueli
 * @date 2021-8-13
 */
public interface IOrderTypeService extends IService<TsOrderType> {

    List<TsOrderType> findTsOrdersType();

    List<Map<String, Object>> findTsOrdersTree(String pid);

    List<Map<String, Object>> findSelectChildByPid(String pid);
}
