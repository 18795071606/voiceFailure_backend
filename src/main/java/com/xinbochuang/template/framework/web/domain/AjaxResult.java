package com.xinbochuang.template.framework.web.domain;

import cn.hutool.http.HttpStatus;

import java.util.HashMap;

/**
 * 封装ajax返回数据
 *
 * @author 黄晓鹏
 * @date 2020-09-18 11:05
 */
public class AjaxResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 状态，是否成功
     */
    public static final String STATUS_TAG = "status";

    /**
     * 消息内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 数据条数
     */
    public static final String COUNT_TAG = "count";

    /**
     * 初始化一个新创建的对象
     */
    public AjaxResult() {
    }

    /**
     * 初始化一个新创建的对象
     *
     * @param code 状态码
     * @param msg  消息
     */
    public AjaxResult(int code, boolean status, String msg) {
        super.put(CODE_TAG, code);
        super.put(STATUS_TAG, status);
        super.put(MSG_TAG, msg);
    }

    /**
     * 返回默认的成功消息
     */
    public static AjaxResult ok() {
        return new AjaxResult(HttpStatus.HTTP_OK, true, "操作成功！");
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     */
    public static AjaxResult ok(String msg) {
        return new AjaxResult(HttpStatus.HTTP_OK, true, msg);
    }

    /**
     * 返回默认的错误消息
     */
    public static AjaxResult error() {
        return new AjaxResult(HttpStatus.HTTP_INTERNAL_ERROR, false, "操作失败！");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     */
    public static AjaxResult error(String msg) {
        return new AjaxResult(HttpStatus.HTTP_INTERNAL_ERROR, false, msg);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, false, msg);
    }

    /**
     * 返回数据
     *
     * @param data 数据对象
     */
    public static AjaxResult data(Object data) {
        AjaxResult ar = AjaxResult.ok();
        ar.put(DATA_TAG, data);
        return ar;
    }

    /**
     * 返回数据
     *
     * @param data  数据对象
     * @param count 数据条数
     */
    public static AjaxResult data(Object data, Integer count) {
        AjaxResult ar = AjaxResult.ok();
        ar.put(DATA_TAG, data);
        ar.put(COUNT_TAG, count);
        return ar;
    }
}
