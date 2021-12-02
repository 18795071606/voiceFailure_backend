package com.xinbochuang.template.utils;


import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    public static <T> List<T> readFromCSV(Character separator, String filePath) {
        CsvReader reader = null;
        List<T> result = new ArrayList<>();
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(filePath, separator, Charset.forName("UTF-8"));

            // 读取标题
            reader.readHeaders();
            // 逐条读取记录，直至读完
            while (reader.readRecord()) {
                //读取指定名字的列
                String waybillNo = reader.get("waybillNo");
                String orderChannelCode = reader.get("orderChannelCode");
                String orderCreateTime = reader.get("orderCreateTime");
                String orderLogisticsCode = reader.get("orderLogisticsCode");
                String recipientProvName = reader.get("recipientProvName");
                String recipientCityName = reader.get("recipientCityName");
                String recipientMobile = reader.get("recipientMobile");
                String recipientAddress = reader.get("recipientAddress");
                String recipientName = reader.get("recipientName");

                List<String> list = new ArrayList<>();
                list.add(waybillNo);
                list.add(orderChannelCode);
                list.add(orderCreateTime);
                list.add(orderLogisticsCode);
                list.add(recipientProvName);
                list.add(recipientCityName);
                list.add(recipientMobile);
                list.add(recipientAddress);
                list.add(recipientName);
                result.add((T) list);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return result;
    }

    /**
     * Write into CSV
     *
     * @param separator 分隔符
     * @param filePath  文件路径
     * @param strList   对应CSV中的一行记录
     */
    public static void writeIntoCSV(Character separator, String filePath, List<List<String>> strList) {
        CsvWriter csvWriter = null;
        try {
            // 创建CSV写对象
            csvWriter = new CsvWriter(filePath, separator, Charset.forName("UTF-8"));
            // 写标题
            String[] headers = {"waybillNo", "orderChannelCode", "orderCreateTime", "orderLogisticsCode", "recipientProvName", "recipientCityName", "recipientMobile", "recipientAddress", "recipientName"};
            csvWriter.writeRecord(headers);

            for (List<String> list : strList) {
                String[] writeLine = new String[list.size()];
                // 集合转数组
                list.toArray(writeLine);
                csvWriter.writeRecord(writeLine);
            }

            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != csvWriter) {
                csvWriter.close();
            }
        }
    }

}
