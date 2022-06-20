package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ReUtil;
import com.ruoyi.common.enums.TransactionTypeEnum;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.domain.TemplateInfoSplitEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
public class TemplateUtil {
    public static AlipayMessageReg insertTemplate(TemplateInfoSplitEntity templateInfoSplitEntity) {
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
        String wildcard2 = templateInfoSplitEntity.getWildcard2();
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
            counterpartyTailNumberIndex = regex.indexOf(counterpartyTailNumber);
            regex = regex.replace(counterpartyTailNumber, re);
        }
        if (StringUtils.isNotBlank(myselfTailNumber)) {
            myselfTailNumberIndex = originText.indexOf(myselfTailNumber);
            regex = regex.replace(myselfTailNumber, re);
        }
        if (StringUtils.isNotBlank(counterpartyAccountName)) {
            counterpartyAccountNameIndex = originText.indexOf(counterpartyAccountName);
            regex = regex.replace(counterpartyAccountName, re);
        }
        if (StringUtils.isNotBlank(transactionDate)) {
            transactionDateIndex = originText.indexOf(transactionDate);
            regex = regex.replace(transactionDate, re);
        }
        if (StringUtils.isNotBlank(transactionAmount)) {
            transactionAmountIndex = originText.indexOf(transactionAmount);
            regex = regex.replace(transactionAmount, re);
        }
        if (StringUtils.isNotBlank(balance)) {
            balanceIndex = originText.indexOf(balance);
            regex = regex.replace(balance, re);
        }
        if (StringUtils.isNotBlank(wildcard)) {
            wildcardIndex = originText.indexOf(wildcard);
            regex = regex.replace(wildcard, re);
        }
        if (StringUtils.isNotBlank(wildcard2)) {
            wildcard2Index = originText.indexOf(wildcard2);
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
        log.info(template);
        log.info(regex);
        log.info(bankName);
        log.info(typeDetail);
        log.info(transactionType);
        log.info(s);
        log.info(match+"");
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
        insertTemplate(templateInfoSplitEntity);

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
