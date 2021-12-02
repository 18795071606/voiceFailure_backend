package com.xinbochuang.template.admin.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xinbochuang.template.common.controller.BaseController;
import com.xinbochuang.template.framework.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄晓鹏
 * @date 2020-10-28 20:24
 */
@RestController
public class CommonController extends BaseController {

    @Value("${baidu.api_key}")
    private String apiKey;

    @Value("${baidu.secret_key}")
    private String secretKey;

    @Value("${baidu.grant_type}")
    private String grantType;

    @Value("${baidu.url}")
    private String url;

    @GetMapping("/getToken")
    private AjaxResult getToken() {
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", grantType);
        params.put("client_id", apiKey);
        params.put("client_secret", secretKey);
        String result = HttpUtil.post(url, params);
        JSONObject obj = JSONUtil.parseObj(result);
        if (obj.get("access_token") != null) {
            String token = obj.get("access_token").toString();
            return AjaxResult.data(token);
        }
        return AjaxResult.error(obj.get("error_description").toString());
    }
}
