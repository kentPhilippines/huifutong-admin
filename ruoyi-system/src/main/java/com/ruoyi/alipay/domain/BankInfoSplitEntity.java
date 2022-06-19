package com.ruoyi.alipay.domain;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author water
 */


public class BankInfoSplitEntity extends BaseEntity {

    public static void main(String[] args) {
        BankInfoSplitEntity bankInfoSplitEntity = BankInfoSplitEntity.of("山东农商银行;5326;张兰英;5616;16:10;2700.00;3551.01;");
        System.out.println(bankInfoSplitEntity.getMsg());
        System.out.println(JSON.toJSONString(bankInfoSplitEntity));
        System.out.println(JSONObject.toJSONString(bankInfoSplitEntity));
        System.out.println(JSONUtil.toJsonStr(bankInfoSplitEntity));
    }


    public String getMsg()
    {
        String format = "模板校验成功，请确认以下解析信息,银行卡:%s，自己卡尾号:%s，对方户名:%s，对方卡尾号:%s，交易时间:%s,转账金额:%s,余额:%s";
        return String.format(format,getBankName(),getMyselfTailNumber(),getCounterpartyAccountName(),getCounterpartyTailNumber(),getTransactionDate(),getTransactionDate(),getBalance());
    }
    /**
     * 根据解析后的字符串构建对象
     * 银行卡，自己卡尾号，对方户名，对方卡尾号，交易时间,转账金额,余额
     * example:  山东农商银行;5326;张兰英;5616;16:10;2700.00;3551.01;
     * @param extractString
     * @return
     */
    public static BankInfoSplitEntity of(String extractString){
        BankInfoSplitEntity entity = new BankInfoSplitEntity();
        List<String> stringList = Arrays.asList(extractString.split(";"));
        if(stringList.size()!=7)
        {
            throw new BusinessException("解析数据异常,元素大于或小于7："+extractString);
        }
        entity.setBankName(stringList.get(0));
        entity.setMyselfTailNumber(stringList.get(1));
        entity.setCounterpartyAccountName(stringList.get(2));
        entity.setCounterpartyTailNumber(stringList.get(3));
        entity.setTransactionDate(stringList.get(4));
        entity.setTransactionAmount(stringList.get(5));
        entity.setBalance(stringList.get(6));
//        entity.setMsg(stringList);

        return entity;
    }

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
