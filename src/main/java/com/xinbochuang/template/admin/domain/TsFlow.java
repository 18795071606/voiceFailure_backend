package com.xinbochuang.template.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xueli
 * @date 2021-8-13
 */
@Data
public class TsFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 宽带账号
     */
    private String kdzh;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 主题
     */
    private String title;

    /**
     * 地市
     */
    private String city;

    /**
     * 区县
     */
    private String county;

    /**
     * 小区
     */
    private String housing;

    /**
     * 有效标志
     */
    private String status;

    /**
     * 家庭住址
     */
    private String address;

    /*
     * 地域属性
     */
    private String territory;

    /**
     * 处理时限
     */
    private String dealTime;

    /**
     * 装维班组名称
     */
    private String maintainteamName;

    /**
     * 班组id
     */
    private String teamId;

    /**
     * 路由信息
     */
    private String route;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 支局
     */
    private String reSubstation;

    /**
     * 归档状态
     */
    private String state;

    /**
     * 临时字段：打开时间
     */
    @TableField(exist=false)
    private Date urlTime;

}
