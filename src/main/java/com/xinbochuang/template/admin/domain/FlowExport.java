package com.xinbochuang.template.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 黄晓鹏
 * @date 2020-11-02 14:52
 */
@Data
@TableName("flow")
public class FlowExport {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品标识
     */
    @Excel(name = "产品标识")
    private String productNumber;
    /**
     * 地址
     */
    @TableField(exist = false)
    @Excel(name = "地址")
    private String url;
}
