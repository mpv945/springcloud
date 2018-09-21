package org.haijun.study.documentation.execl;

import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.haijun.study.documentation.excel.ExcelUtils;
import org.haijun.study.documentation.excel.MultiLineHeadExcelModel;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * excel 导出测试
 */
public class ExcelUtilsTest {

    /**
     * 测试读取和写入
     */
    @Test
    public void testRead()
    {
        //assertTrue( true );
        // 读取excel 测试
        try {
            List<List<String>>  data1 = ExcelUtils.readNoDataType(new File("D:\\mapingUrl.xls"),ExcelTypeEnum.XLS,1);
            //writeDataTypeCheck(new ArrayList<Integer>());
            //System.out.println(data1);
            Table table3 = new Table(3);
            table3.setClazz(MultiLineHeadExcelModel.class);
            ExcelUtils.writeListStrToFile(new File("D:\\test.xlsx"),
                    data1,"测试数据",ExcelTypeEnum.XLSX ,true,table3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
