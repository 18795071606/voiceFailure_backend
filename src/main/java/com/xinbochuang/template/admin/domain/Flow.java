package com.xinbochuang.template.admin.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelIgnore;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 黄晓鹏
 * @date 2020-10-29
 */
@Data
public class Flow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 地市
     */
    @Excel(name = "地市")
    @ExcelIgnore
    private String city;

    /**
     * 区县
     */
    @Excel(name = "区县")
    @ExcelIgnore
    private String county;

    /**
     * 工单类型
     */
    @Excel(name = "工单类型")
    @ExcelIgnore
    private String flowType;

    /**
     * 工单号
     */
    @Excel(name = "工单编号")
    @ExcelIgnore
    private String flowNo;

    /**
     * 客户名称
     */
    @Excel(name = "客户名称")
    @ExcelIgnore
    private String customName;

    /**
     * 客户编号
     */
    @Excel(name = "客户编号")
    @ExcelIgnore
    private String customNo;

    /**
     * 产品标识
     */
    @Excel(name = "产品标识")
    private String productNumber;

    /**
     * 客户服务等级
     */
    @Excel(name = "客户服务等级")
    @ExcelIgnore
    private String customServiceLevel;

    /**
     * 业务报障等级
     */
    @Excel(name = "业务保障等级")
    @ExcelIgnore
    private String businessLevel;

    /**
     * 线路带宽
     */
    @Excel(name = "线路带宽")
    @ExcelIgnore
    private String bandWidth;

    /**
     * 业务联系人
     */
    @Excel(name = "业务联系人")
    @ExcelIgnore
    private String businessName;

    /**
     * 业务联系电话
     */
    @Excel(name = "业务联系电话")
    @ExcelIgnore
    private String businessPhone;

    /**
     * 派单人
     */
    @Excel(name = "派单人")
    @ExcelIgnore
    private String dispatchName;

    /**
     * 时间
     */
    private Date createTime;

    /**
     * 地址
     */
    @TableField(exist = false)
    @Excel(name = "地址")
    private String url;

}
