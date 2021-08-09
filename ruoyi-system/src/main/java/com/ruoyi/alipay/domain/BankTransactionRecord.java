package com.ruoyi.alipay.domain;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * @author water
 */


public class BankTransactionRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 总收入
     */
    @Excel(name = "总收入")
    private String totalIncome;
    /**
     * 总支出
     */
    @Excel(name = "总支出")
    private String totalExpenditure;
    /**
     * 当前余额
     */
    @Excel(name = "当前余额")
    private String currentBalance;
    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private String userId;
    /**
     * 银行卡号
     */
    private String bankId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getTotalExpenditure() {
        return totalExpenditure;
    }

    public void setTotalExpenditure(String totalExpenditure) {
        this.totalExpenditure = totalExpenditure;
    }

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
    }
}
