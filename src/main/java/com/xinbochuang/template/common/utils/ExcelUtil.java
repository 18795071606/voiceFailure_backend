package com.xinbochuang.template.common.utils;

import cn.afterturn.easypoi.csv.CsvExportUtil;
import cn.afterturn.easypoi.csv.CsvImportUtil;
import cn.afterturn.easypoi.csv.entity.CsvExportParams;
import cn.afterturn.easypoi.csv.entity.CsvImportParams;
import cn.afterturn.easypoi.handler.inter.IReadHandler;
import com.xinbochuang.template.admin.domain.Flow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄晓鹏
 * @date 2020-10-30 10:26
 */
public class ExcelUtil {

    /**
     * 导入cvs
     */
    public static List<Flow> importCvs(String path) throws FileNotFoundException {
        List<Flow> list = new ArrayList<>();
        CsvImportParams params = new CsvImportParams(CsvImportParams.GBK);
        params.setTitleRows(1);
        CsvImportUtil.importCsv(new FileInputStream(
                        new File(path)),
                Flow.class, params, new IReadHandler() {
                    @Override
                    public void handler(Object o) {
                        list.add((Flow) o);
                    }

                    @Override
                    public void doAfterAll() {

                    }
                });
        return list;
    }

    /**
     * 导出cvs
     */
    public static void exportCvs(List<Flow> list, String path) throws IOException {
        CsvExportParams params = new CsvExportParams();
        File saveFile = new File("D:/excel/");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(path);
        CsvExportUtil.exportCsv(params, Flow.class, list, fos);
        fos.flush();
        fos.close();
    }

}
