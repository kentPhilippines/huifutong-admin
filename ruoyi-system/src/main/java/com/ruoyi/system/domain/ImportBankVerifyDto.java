package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

@Data
public class ImportBankVerifyDto {


    public String getBankName() {
        return bankName.trim();
    }


    @Excel(name = "银行名称", cellType = Excel.ColumnType.STRING, prompt = "银行名称")
    private String bankName;
    @Excel(name = "状态(1是开，0是关)", cellType = Excel.ColumnType.NUMERIC, prompt = "状态(1是开，0是关)")
    private Integer status;


}
