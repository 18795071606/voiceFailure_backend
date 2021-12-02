package com.xinbochuang.template.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinbochuang.template.admin.domain.TsFlow;
import com.xinbochuang.template.admin.domain.TsOrderType;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xueli
 * @date 2021-8-13
 */
public interface TsFlowMapper extends BaseMapper<TsFlow> {

    TsFlow findTsFlowByKdzh(@Param(value = "kdzh") String kdzh,@Param(value = "phone") String phone);
}
