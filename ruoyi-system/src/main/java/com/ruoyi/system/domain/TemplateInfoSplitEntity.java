package com.ruoyi.system.domain;



import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author water
 */
public class TemplateInfoSplitEntity implements Serializable {
    private String originText;

    private String bankName;

    private String myselfTailNumber;

    //@ApiModelProperty(value = "短信交易具体类型,填收入或者支出或者往账", example = "手机银行收入")
    private String typeDetail;

    //@ApiModelProperty(value = "交易金额,必填", example = "1,000")
    @NotBlank(message = "交易金额不能为空")
    private String transactionAmount;

    //@ApiModelProperty(value = "短信余额,无余额不填", example = "6,865.62")
    private String balance;

    //@ApiModelProperty(value = "交易时间,根据实际时间添加,无时间不填", example = "12月23日23:10")
    private String transactionDate;

    //@ApiModelProperty(value = "对方银行尾号,无对方银行尾号不填", example = "1932")
    private String counterpartyTailNumber;

    //@ApiModelProperty(value = "对方户名,无对方户名不填", example = "张亮")
    private String counterpartyAccountName;

    //@ApiModelProperty(value = "交易类型,只能填收入或者支出或者往账", example = "收入")
    private String transactionType;

    //@ApiModelProperty(value = "通配预留字段1,客服人员人员不需填写", example = "无")
    private String wildcard;

    //@ApiModelProperty(value = "通配预留字段2,客服人员人员不需填写", example = "无")
    private String wildcard2;

    //@ApiModelProperty(value = "冲正拉黑关键字，拉黑时必填", example = "无")
    private String blackKey;

    //@ApiModelProperty(value = "限制解析短信标识,不限制不填,限制解析填1", example = "无")
    private String spiltTail;

    //@ApiModelProperty(value = "来源号码", example = "无")
    private String sourcePhone;

    //@ApiModelProperty(value = "短信无银行名称时填卡商id,不常用", example = "无")
    private String remark1;

    //@ApiModelProperty(value = "自己银行尾号程序解析错误时,强制走尾号正则,不常用,由技术判定是否添加", example = "无")
    private String remark2;


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

    public String getWildcard() {
        return wildcard;
    }

    public void setWildcard(String wildcard) {
        this.wildcard = wildcard;
    }

    public String getWildcard2() {
        return wildcard2;
    }

    public void setWildcard2(String wildcard2) {
        this.wildcard2 = wildcard2;
    }

    public String getBlackKey() {
        return blackKey;
    }

    public void setBlackKey(String blackKey) {
        this.blackKey = blackKey;
    }

    public String getSpiltTail() {
        return spiltTail;
    }

    public void setSpiltTail(String spiltTail) {
        this.spiltTail = spiltTail;
    }

    public String getSourcePhone() {
        return sourcePhone;
    }

    public void setSourcePhone(String sourcePhone) {
        this.sourcePhone = sourcePhone;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }
}
