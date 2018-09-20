package org.haijun.study.documentation.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * excel 模型映射 */
public class DemoRowModel extends BaseRowModel {

    // @ExcelProperty(index = 3)数字代表该字段与excel对应列号做映射
    @ExcelProperty(index = 0)
    private String bankLoanId;

    /*@ExcelProperty(index = 1)
    private Long customerId;*/

    /*@ExcelProperty(index = 2,format = "yyyy/MM/dd")
    private Date loanDate;
*/
    // @ExcelProperty(value = {"一级表头","二级表头"})用于解决不确切知道excel第几列和该字段映射，位置不固定，但表头的内容知道的情况。
    @ExcelProperty(value = {"一级表头","二级表头"})
    private BigDecimal sax;

    public String getBankLoanId() {
        return bankLoanId;
    }

    public void setBankLoanId(String bankLoanId) {
        this.bankLoanId = bankLoanId;
    }

    public BigDecimal getSax() {
        return sax;
    }

    public void setSax(BigDecimal sax) {
        this.sax = sax;
    }
}
