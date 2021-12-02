package com.xinbochuang.template.common.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 黄晓鹏
 * @date 2020-11-02 16:04
 */
public class FtpUtils {

    /**
     * 上传文件
     *
     * @param params   配置信息
     * @param filePath 文件存放路径
     * @param filename 文件名
     * @return 结果
     */
    public static boolean uploadFile(FtpParams params, String filePath, String filename) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            // 连接FTP服务器
            ftp.connect(params.getHost(), params.getPort());
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            // 登录
            ftp.login(params.getAccount(), params.getPassword());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            //切换到上传目录
            if (!ftp.changeWorkingDirectory(params.getDir())) {
                //如果目录不存在创建目录
                String[] dirs = params.getDir().split("/");
                String tempPath = params.getDir();
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {
                        if (!ftp.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }

            }
            result = ftp.deleteFile(filename);
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
            FileInputStream input = new FileInputStream(new File(filePath));
            if (!ftp.storeFile(filename, input)) {
                return result;
            }
            input.close();
            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return result;
    }

    /**
     * 下载文件
     *
     * @param params     配置信息
     * @param fileName   下载文件名
     * @return 结果
     */
    public static String downloadFile(FtpParams params, String fileName) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String localname= dtf2.format(LocalDateTime.now())+fileName;
        try {
            int reply;
            ftp.connect(params.getHost(), params.getPort());
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            // 登录
            ftp.login(params.getAccount(), params.getPassword());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return "";
            }
            // 转移到FTP服务器目录
            ftp.changeWorkingDirectory(params.getDir());
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {

                    File localFile = new File(params.getLocalPath() + "/" + localname);

                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }

            ftp.logout();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return params.getLocalPath()+"/"+localname;
    }

}
