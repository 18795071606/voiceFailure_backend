package com.xinbochuang.template.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 黄晓鹏
 * @date 2020-10-29
 */
@Data
public class FailureDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String productNumber;

    private String phone;

    private String detail;

    private String address;

    private String position;

    private Date createTime;
    private String city;
    private String county;


}
