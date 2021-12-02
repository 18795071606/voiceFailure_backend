package com.xinbochuang.template.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinbochuang.template.admin.domain.TsOrderType;
import com.xinbochuang.template.admin.mapper.OrderTypeMapper;
import com.xinbochuang.template.admin.service.IFlowService;
import com.xinbochuang.template.admin.service.IOrderTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xueli
 * @date 2021-8-13
 */
@Service
public class OrderTypeServiceImpl extends ServiceImpl<OrderTypeMapper, TsOrderType> implements IOrderTypeService {

    @Resource
    private OrderTypeMapper orderTypeMapper;

    @Override
    public List<TsOrderType> findTsOrdersType() {
        return baseMapper.findTsOrdersType();
    }

    /**
     * 递归查询工单投诉类型
     * @author xueli
     * @date 2021-8-13
     * @param pid
     * @return
     */
    @Override
    public List<Map<String, Object>> findTsOrdersTree(String pid) {
        //查询pid为0的
        List<Map<String,Object>> list = orderTypeMapper.findTsOrdersPre(pid);
        //子节点
        List<Map<String, Object>> children;
        for(Map<String,Object> m : list){
            children = findTsOrdersTree(m.get("id").toString());
            if(children != null && children.size() > 0){
                m.put("children",children);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> findSelectChildByPid(String pid) {
        return orderTypeMapper.findSelectChildByPid(pid);
    }
}
