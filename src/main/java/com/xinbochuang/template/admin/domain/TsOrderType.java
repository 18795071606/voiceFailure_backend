package com.xinbochuang.template.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xueli
 * @date 2021-8-13
 */
@Data
public class TsOrderType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String pid;

    private String pids;

    private String name;

    private String code;

    private String orderGroup;

    private String ordering;

}
