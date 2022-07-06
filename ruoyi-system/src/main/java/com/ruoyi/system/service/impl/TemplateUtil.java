package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ReUtil;
import com.ruoyi.common.enums.TransactionTypeEnum;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.domain.TemplateInfoSplitEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class TemplateUtil {
    private static final String CHINESE_REG ="([\\u4e00-\\u9fa5]*)";
    private static final String ALL_REG ="(.*)";
    public static int getSpecialIndex(String n, String tem, String text) {
        Integer x = StringUtils.isBlank(n) ? 1 : Integer.valueOf(n);
        Matcher matcher = Pattern.compile(tem).matcher(text);
        int index = 0;
        while (matcher.find()) {
            index++;
            if (index == x.intValue()) {
                break;
            }
        }
        return matcher.start();
    }

    /**
     * 客服运营人员走不同校验
     *
     * @param tem
     * @param text
     * @return
     */
    public static int duplicateTimes(String tem, String text, Boolean isDevelop) {
        if (!isDevelop || StringUtils.isBlank(tem)) {
            return 1;
        }
        Matcher matcher = Pattern.compile(tem).matcher(text);
        int index = 0;
        while (matcher.find()) {
            index++;
        }
        return index;
    }


    public static AlipayMessageReg insertTemplate(TemplateInfoSplitEntity templateInfoSplitEntity, boolean isDevelop) {
        String regex = templateInfoSplitEntity.getOriginText();
        String originText = regex;
        regex = regex.replace("[", "\\[").replace("]", "\\]")
                .replace("(", "\\(").replace(")", "\\)");
        String bankName = templateInfoSplitEntity.getBankName();
        String counterpartyTailNumber = templateInfoSplitEntity.getCounterpartyTailNumber();
        String myselfTailNumber = templateInfoSplitEntity.getMyselfTailNumber();
        String counterpartyAccountName = templateInfoSplitEntity.getCounterpartyAccountName();
        String transactionAmount = templateInfoSplitEntity.getTransactionAmount();
        String typeDetail = templateInfoSplitEntity.getTypeDetail();
        String transactionDate = templateInfoSplitEntity.getTransactionDate();
        String transactionType = templateInfoSplitEntity.getTransactionType();
        String balance = templateInfoSplitEntity.getBalance();
        String wildcard = templateInfoSplitEntity.getWildcard();
        String wildcardSize = templateInfoSplitEntity.getWildcardSize();
        String wildcard2 = templateInfoSplitEntity.getWildcard2();
        String wildcard2Size = templateInfoSplitEntity.getWildcard2Size();
        String blackKey = templateInfoSplitEntity.getBlackKey();
        String spiltTail = templateInfoSplitEntity.getSpiltTail();
        String sourcePhone = templateInfoSplitEntity.getSourcePhone();
        String remark1 = templateInfoSplitEntity.getRemark1();
        String remark2 = templateInfoSplitEntity.getRemark2();
        String re = "(.*)";
        Integer bankIndex = null;
        Integer counterpartyTailNumberIndex = null;
        Integer myselfTailNumberIndex = null;
        Integer counterpartyAccountNameIndex = null;
        Integer transactionDateIndex = null;
        Integer transactionAmountIndex = null;
        Integer balanceIndex = null;
        Integer wildcardIndex = null;
        Integer wildcard2Index = null;
        if (StringUtils.isNotBlank(bankName)) {
            bankIndex = originText.indexOf(bankName);
            regex = regex.replace(bankName, re);
        }
        if (StringUtils.isNotBlank(counterpartyTailNumber)) {
            if (duplicateTimes(counterpartyTailNumber, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            counterpartyTailNumberIndex = regex.indexOf(counterpartyTailNumber);
            regex = regex.replace(counterpartyTailNumber, re);
        }
        if (StringUtils.isNotBlank(myselfTailNumber)) {
            if (duplicateTimes(myselfTailNumber, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            myselfTailNumberIndex = originText.indexOf(myselfTailNumber);
            regex = regex.replace(myselfTailNumber, re);
        }
        if (StringUtils.isNotBlank(counterpartyAccountName)) {
            if (duplicateTimes(counterpartyAccountName, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            counterpartyAccountNameIndex = originText.indexOf(counterpartyAccountName);
            regex = regex.replace(counterpartyAccountName, CHINESE_REG);//姓名只可能是中文
        }
        if (StringUtils.isNotBlank(transactionDate)) {
            if (duplicateTimes(transactionDate, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            transactionDateIndex = originText.indexOf(transactionDate);
            regex = regex.replace(transactionDate, re);
        }
        if (StringUtils.isNotBlank(transactionAmount)) {
            if (duplicateTimes(transactionAmount, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            transactionAmountIndex = originText.indexOf(transactionAmount);
            regex = regex.replace(transactionAmount, re);
        }
        if (StringUtils.isNotBlank(balance)) {
            if (duplicateTimes(balance, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            balanceIndex = originText.indexOf(balance);
            regex = regex.replace(balance, re);
        }
        if (StringUtils.isNotBlank(wildcard)) {
            if (duplicateTimes(wildcard, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            wildcardIndex = getSpecialIndex(wildcardSize, wildcard, originText);
            regex = regex.replace(wildcard, re);
        }
        if (StringUtils.isNotBlank(wildcard2)) {
            if (duplicateTimes(wildcard2, originText, isDevelop) > 1) {
                throw new BusinessException("操作失败，请联系技术添加");
            }
            wildcard2Index = getSpecialIndex(wildcard2Size, wildcard2, wildcard2Size);
            regex = regex.replace(wildcard2, re);
        }
        String template = "bankIndex;myselfTailNumberIndex;counterpartyAccountNameIndex;counterpartyTailNumberIndex;transactionDateIndex;transactionAmountIndex;balanceIndex;";
        Map<Integer, String> data = new HashMap<>();
        data.put(bankIndex, "bankIndex");
        data.put(counterpartyTailNumberIndex, "counterpartyTailNumberIndex");
        data.put(myselfTailNumberIndex, "myselfTailNumberIndex");
        data.put(counterpartyAccountNameIndex, "counterpartyAccountNameIndex");
        data.put(transactionDateIndex, "transactionDateIndex");
        data.put(transactionAmountIndex, "transactionAmountIndex");
        data.put(balanceIndex, "balanceIndex");
        data.put(wildcardIndex, "wildcardIndex");
        data.put(wildcard2Index, "wildcard2Index");
        List<Integer> str = new ArrayList<>();
        str.add(bankIndex);
        str.add(counterpartyTailNumberIndex);
        str.add(myselfTailNumberIndex);
        str.add(counterpartyAccountNameIndex);
        str.add(transactionDateIndex);
        str.add(transactionAmountIndex);
        str.add(balanceIndex);
        str.add(wildcardIndex);
        str.add(wildcard2Index);
        List<Integer> sortList = str.stream()
                .filter(tmp -> null != tmp && tmp > 0)
                .sorted(Comparator.comparing(Integer::intValue))
                .collect(Collectors.toList());
        int index = 1;
        for (Integer integer : sortList) {
            String a = data.get(integer);
            template = template.replace(a, "$" + index++);
        }
        template = template
                .replace("bankIndex", "@")
                .replace("myselfTailNumberIndex", "@")
                .replace("counterpartyAccountNameIndex", "@")
                .replace("counterpartyTailNumberIndex", "@")
                .replace("transactionDateIndex", "@")
                .replace("transactionAmountIndex", "@")
                .replace("balanceIndex", "@");
        boolean match = ReUtil.isMatch(regex, originText);
        String s = ReUtil.extractMulti(regex, originText, template);
        System.out.println(template);
        System.out.println(regex);
        System.out.println(bankName);
        System.out.println(typeDetail);
        System.out.println(transactionType);
        System.out.println(s);
        System.out.println(match);
        AlipayMessageReg alipayMessageReg = new AlipayMessageReg();
        if (transactionType.equals("收入")) {
            transactionType = TransactionTypeEnum.income.name();
        }
        if (transactionType.equals("支出")) {
            transactionType = TransactionTypeEnum.expenditure.name();

        }
        if (transactionType.equals("资金回退")) {
            transactionType = TransactionTypeEnum.fundback.name();
        }
        alipayMessageReg.setBankName(bankName);
        alipayMessageReg.setRegex(regex);
        alipayMessageReg.setSourcePhone(sourcePhone);
        alipayMessageReg.setSourceMsg(originText);
        alipayMessageReg.setTemplate(template);
        alipayMessageReg.setTailSplit(spiltTail);
        alipayMessageReg.setBlackKey(blackKey);
        alipayMessageReg.setTransactionType(transactionType);
        alipayMessageReg.setTransactionTypeDetail(typeDetail);
        alipayMessageReg.setRemark1(remark1);
        alipayMessageReg.setRemark2(remark2);
        alipayMessageReg.setTemplateFlag("1");
        alipayMessageReg.setBankName(bankName);
        if (match && StringUtils.isNotBlank(s)) {
            return alipayMessageReg;
        }
        return null;
    }

    public static void main(String[] args) {
        TemplateInfoSplitEntity templateInfoSplitEntity = new TemplateInfoSplitEntity();
        templateInfoSplitEntity.setBankName("山东农商银行");
        templateInfoSplitEntity.setOriginText("[山东农商银行]您尾号9907的账户在17:38发生（跨行实时汇出交易）人民币1600.00元，账户可用余额467.08元 ，备注：转账。");
        templateInfoSplitEntity.setMyselfTailNumber("9907");
        templateInfoSplitEntity.setTransactionDate("17:38");
        templateInfoSplitEntity.setTransactionAmount("1600.00");
        templateInfoSplitEntity.setBalance("467.08");
        templateInfoSplitEntity.setTypeDetail("跨行实时汇出交易");
        templateInfoSplitEntity.setTransactionType("支出");
        templateInfoSplitEntity.setTypeDetail("跨行实时汇出交易");
        templateInfoSplitEntity.setWildcard("备注：转账");
        insertTemplate(templateInfoSplitEntity, true);

//        BankInfoSplitEntity bankInfoSplitEntity = new BankInfoSplitEntity();
//        bankInfoSplitEntity.setBankName("云南农信");
//        bankInfoSplitEntity.setOriginText("尊敬的客户,您的账户*0251*于04月12日17:35发生转账转出人民币300.00元,余额22.24元.[本机构吸收的本外币存款依照存款保险条例受到保护]【云南农信】");
//        bankInfoSplitEntity.setMyselfTailNumber("*0251*");
//        bankInfoSplitEntity.setTransactionDate("04月12                                                                                                                                                                                                                                                                 日17:35");
//        bankInfoSplitEntity.setTransactionAmount("300.00");
//        bankInfoSplitEntity.setBalance("22.24");
//        bankInfoSplitEntity.setTypeDetail("转账转出");
//        bankInfoSplitEntity.setTransactionType("out");
//        insert(bankInfoSplitEntity);
    }
}
