package com.ruoyi.alipay.domain;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * @author water
 */


public class BankInfoSplitEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 数据id
     */
    private Long id;

    /**
     * 原始短信内容
     */
    @Excel(name = "原始短信")
    private String originText;
    /**
     * 银行或支付渠道名称
     * 建设银行，工商银行等
     */
    @Excel(name = "银行卡名称")
    private String bankName;
    /**
     * 业务类型
     * 存入，转账，转存等
     */
    @Excel(name = "交易具体类型")
    private String typeDetail;
    /**
     * 余额
     */
    @Excel(name = "余额")
    private String balance;
    /**
     * 交易时间
     */
    @Excel(name = "交易时间")
    private String transactionDate;
    /**
     * 交易金额
     */
    @Excel(name = "交易金额")
    private String transactionAmount;
    /**
     * 自己账户尾号
     */
    @Excel(name = "尾号")
    private String myselfTailNumber;
    /**
     * 对方账户尾号
     */
    @Excel(name = "对方尾号")
    private String counterpartyTailNumber;
    /**
     * 对方户名
     */
    @Excel(name = "对方户名")
    private String counterpartyAccountName;
    /**
     * 交易类型，收入，支出
     */
    @Excel(name = "交易类型")
    private String transactionType;
    /**
     * 电话号码
     */
    @Excel(name = "手机号")
    private String phoneId;
    /**
     * 电话号码+自己尾号的组装
     */
    private String checkKey;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private String userId;
    /**
     * 银行卡号
     */
    @Excel(name = "银行卡号")
    private String bankId;
    /**
     * 最终短信内容
     */
    private String resultText;

    /**
     * 数据合法性标识
     */
    private String mark;


    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTypeDetail() {
        return typeDetail;
    }

    public void setTypeDetail(String typeDetail) {
        this.typeDetail = typeDetail;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getMyselfTailNumber() {
        return myselfTailNumber;
    }

    public void setMyselfTailNumber(String myselfTailNumber) {
        this.myselfTailNumber = myselfTailNumber;
    }

    public String getCounterpartyTailNumber() {
        return counterpartyTailNumber;
    }

    public void setCounterpartyTailNumber(String counterpartyTailNumber) {
        this.counterpartyTailNumber = counterpartyTailNumber;
    }

    public String getCounterpartyAccountName() {
        return counterpartyAccountName;
    }

    public void setCounterpartyAccountName(String counterpartyAccountName) {
        this.counterpartyAccountName = counterpartyAccountName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getCheckKey() {
        return checkKey;
    }

    public void setCheckKey(String checkKey) {
        this.checkKey = checkKey;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
