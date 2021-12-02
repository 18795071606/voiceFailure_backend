package com.xinbochuang.template.common.controller;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinbochuang.template.common.domain.TableDataInfo;
import com.xinbochuang.template.framework.web.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;

/**
 * web层通用数据处理
 *
 * @author 黄晓鹏
 * @date 2020-09-10 11:51
 */
@Slf4j
public class BaseController {

    /**
     * 响应返回结果
     *
     * @param ok 是否成功
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean ok) {
        return ok ? AjaxResult.ok() : AjaxResult.error();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(IPage<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.HTTP_OK);
        rspData.setMsg("查询成功");
        rspData.setRows(list.getRecords());
        rspData.setTotal(list.getTotal());
        return rspData;
    }
}
