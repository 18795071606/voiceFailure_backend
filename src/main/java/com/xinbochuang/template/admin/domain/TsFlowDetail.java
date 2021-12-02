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
public class TsFlowDetail implements Serializable {

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
    private String mobile;

    /**
     * 投诉类型
     */
    private String tsType;

    /**
     * 投诉内容
     */
    private String content;

    /**
     * 打开url时间
     */
    private Date openTime;

    /**
     * 提交时间
     */
    private Date subTime;

}
