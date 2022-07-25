package com.ruoyi.system.domain;


import cn.hutool.core.util.StrUtil;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author water
 */
public class TemplateInfoSplitEntity implements Serializable {
    //    @ApiModelProperty(value = "原始短信", example = "您尾号1856卡12月23日23:10手机银行收入(网转)1,000元，余额6,865.62元，对方户名：张亮，对方账户尾号：1932。【工商银行】")
    private String originText;

    //    @ApiModelProperty(value = "银行名称,必须填写", example = "工商银行")
    @NotBlank(message = "银行不能为空")
    private String bankName = "";

    //    @ApiModelProperty(value = "自己银行尾号", example = "1856")
    private String myselfTailNumber;

    //    @ApiModelProperty(value = "短信交易具体类型,填收入或者支出或者往账", example = "手机银行收入")
    private String typeDetail;

    //    @ApiModelProperty(value = "交易金额,必填", example = "1,000")
    @NotBlank(message = "交易金额不能为空")
    private String transactionAmount;

    //    @ApiModelProperty(value = "短信余额,无余额不填", example = "6,865.62")
    private String balance;

    //    @ApiModelProperty(value = "交易时间,根据实际时间添加,无时间不填", example = "12月23日23:10")
    private String transactionDate;

    //    @ApiModelProperty(value = "对方银行尾号,无对方银行尾号不填", example = "1932")
    private String counterpartyTailNumber;

    //    @ApiModelProperty(value = "对方户名,无对方户名不填", example = "张亮")
    private String counterpartyAccountName;

    //    @ApiModelProperty(value = "交易类型,只能填收入或者支出或者往账", example = "收入")
    @NotBlank(message = "交易类型不能为空")
    private String transactionType;


    //    @ApiModelProperty(value = "冲正拉黑关键字，拉黑时必填", example = "无")
    private String blackKey;

    //    @ApiModelProperty(value = "限制解析短信标识,不限制不填,限制解析填1", example = "无")
    private String spiltTail;

    //    @ApiModelProperty(value = "来源号码", example = "无")
    private String sourcePhone;

    //    @ApiModelProperty(value = "短信无银行名称时填卡商id,不常用", example = "无")
    private String remark1;

    //    @ApiModelProperty(value = "自己银行尾号程序解析错误时,强制走尾号正则,不常用,由技术判定是否添加", example = "无")
    private String remark2;

    //1银行名称2自己尾号3对方户名4对方尾号5时间6转账金额7余额
    //    @ApiModelProperty(value = "排序,替换字符串冲突的时候按照大的包含关系排在前面进行处理", example = "1,2,3,4,5,6,7")
    private String sortStr;

    //1银行名称2自己尾号3对方户名4对方尾号5时间6转账金额7余额
//    @ApiModelProperty(value = "模板，按照固定顺序填入固定值，无使用占位符@", example = "东营银行=591=,张岩=6947=07月15日12:53=300.00=0.00")
    private String template = "";


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
        if (StrUtil.isBlank(balance)) {
            return "@";
        }
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTransactionDate() {
        if (StrUtil.isBlank(transactionDate)) {
            return "@";
        }
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
        if (StrUtil.isBlank(myselfTailNumber)) {
            return "@";
        }
        return myselfTailNumber;
    }

    public void setMyselfTailNumber(String myselfTailNumber) {
        this.myselfTailNumber = myselfTailNumber;
    }

    public String getCounterpartyTailNumber() {
        if (StrUtil.isBlank(counterpartyTailNumber)) {
            return "@";
        }
        return counterpartyTailNumber;
    }

    public void setCounterpartyTailNumber(String counterpartyTailNumber) {
        this.counterpartyTailNumber = counterpartyTailNumber;
    }

    public String getCounterpartyAccountName() {
        if (StrUtil.isBlank(counterpartyAccountName)) {
            return "@";
        }
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

    public String getSortStr() {
        if (StrUtil.isBlank(sortStr)) {
            return "1,2,3,4,5,6,7";
        }
        return sortStr;
    }

    public void setSortStr(String sortStr) {
        this.sortStr = sortStr;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public static TemplateInfoSplitEntity of(TemplateInfoSplitEntity templateInfoSplitEntity) {
        String source=templateInfoSplitEntity.getOriginText();
        if (templateInfoSplitEntity.getBankName().equals("微信银行")) {
             source = templateInfoSplitEntity.getOriginText().replaceAll("\\r", "").replaceAll("\\n", "");
        }
        templateInfoSplitEntity.setOriginText(source);
        String connectStr = "=";
        String defaultConnect = ";";
        String regexSpecial = "([\\u4e00-\\u9fa5]{2,5})";
        String bankName = templateInfoSplitEntity.getBankName();
        String myselfTailNumber = templateInfoSplitEntity.getMyselfTailNumber();
        String counterpartyAccountName = templateInfoSplitEntity.getCounterpartyAccountName();
        String counterpartyTailNumber = templateInfoSplitEntity.getCounterpartyTailNumber();
        String transactionDate = templateInfoSplitEntity.getTransactionDate();
        String transactionAmount = templateInfoSplitEntity.getTransactionAmount();
        String balance = templateInfoSplitEntity.getBalance();
        String[] splitBankName = bankName.split(";");
        String[] splitTail = myselfTailNumber.split(";");
        String[] splitCountTail = counterpartyTailNumber.split(";");
        String[] splitTransactionDate = transactionDate.split(";");
        String[] splitTransactionAmount = transactionAmount.split(";");
        String[] splitBalance = balance.split(";");
        String template = splitBankName[0] + connectStr +
                splitTail[0] + connectStr +
                templateInfoSplitEntity.getCounterpartyAccountName() + connectStr +
                splitCountTail[0] + connectStr +
                splitTransactionDate[0] + connectStr +
                splitTransactionAmount[0] + connectStr +
                splitBalance[0];
        templateInfoSplitEntity.setTemplate(template);
        String sortStr = templateInfoSplitEntity.getSortStr();
        //1,2,3,4,5,6,7
        //1,2,4,3,5,6,7
        String[] sortStrSplit = sortStr.split(",");
        String regex = "";
        for (int i = 0; i < sortStrSplit.length; i++) {
            if (sortStrSplit[i].equals("1")) {
                regex = regex + bankName + connectStr;
            }
            if (sortStrSplit[i].equals("2")) {
                regex = regex + myselfTailNumber + connectStr;
            }
            if (sortStrSplit[i].equals("3")) {
                regex = regex + counterpartyAccountName +defaultConnect+regexSpecial+ connectStr;
            }
            if (sortStrSplit[i].equals("4")) {
                regex = regex + counterpartyTailNumber + connectStr;
            }
            if (sortStrSplit[i].equals("5")) {
                regex = regex + transactionDate + connectStr;
            }
            if (sortStrSplit[i].equals("6")) {
                regex = regex + transactionAmount + connectStr;
            }
            if (sortStrSplit[i].equals("7")){
                regex = regex + balance + connectStr;
            }
        }
        regex=regex.substring(0,regex.lastIndexOf("="));
        templateInfoSplitEntity.setSortStr(regex);
        return templateInfoSplitEntity;
    }
}
