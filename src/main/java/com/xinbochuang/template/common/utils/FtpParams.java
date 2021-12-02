package com.xinbochuang.template.common.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 黄晓鹏
 * @date 2020-11-02 18:17
 */
@Component
@ConfigurationProperties(prefix = "ftp")
@Data
public class FtpParams {

    private String host;

    private int port;

    private String account;

    private String password;

    private String dir;

    private String localPath;

    private String firstPath;

}
