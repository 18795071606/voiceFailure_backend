package com.xinbochuang.template.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinbochuang.template.admin.domain.TsOrderType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xueli
 * @date 2021-8-13
 */
public interface OrderTypeMapper extends BaseMapper<TsOrderType> {

    List<TsOrderType> findTsOrdersType();

    List<Map<String, Object>> findTsOrdersPre(@Param(value = "pid") String pid);

    List<Map<String, Object>> findSelectChildByPid(@Param(value = "pid") String pid);
}
