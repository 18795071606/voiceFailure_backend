package com.xinbochuang.template;

import com.xinbochuang.template.admin.domain.Flow;
import com.xinbochuang.template.admin.service.IFlowService;
import com.xinbochuang.template.common.utils.ExcelUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class TemplateApplicationTests {

    @Resource
    private IFlowService flowService;

    @Test
    void contextLoads() {

    }

    @Test
    void testImport() throws FileNotFoundException {
        String path = "C:/Users/hxp/Desktop/xbc/test.csv";
        List<Flow> list = ExcelUtil.importCvs(path);
        for (Flow flow : list) {
            System.out.println(flow);
        }
    }

    @Test
    void testExport() throws IOException {
        String url = "https://travelhidden.aaay.xin/voiceFailure_web/view/index.html?";
        String path = "C:/Users/hxp/Desktop/export.csv";
        List<Flow> list = flowService.list();
        for (Flow flow : list) {
            flow.setUrl(url + flow.getProductNumber());
        }
        ExcelUtil.exportCvs(list, path);
    }

}
