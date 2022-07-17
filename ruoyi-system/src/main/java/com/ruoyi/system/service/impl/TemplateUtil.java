package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ReUtil;
import com.ruoyi.common.enums.TransactionTypeEnum;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.domain.TemplateInfoSplitEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class TemplateUtil {


    public static AlipayMessageReg insertTemplate(TemplateInfoSplitEntity templateInfoSplitEntity) {
        String regex = templateInfoSplitEntity.getOriginText();
        String originText = regex;
        regex = regex.replace("[", "\\[").replace("]", "\\]")
                .replace("(", "\\(").replace(")", "\\)");
        String bankName = templateInfoSplitEntity.getBankName();
        String typeDetail = templateInfoSplitEntity.getTypeDetail();
        String transactionType = templateInfoSplitEntity.getTransactionType();
        String blackKey = templateInfoSplitEntity.getBlackKey();
        String spiltTail = templateInfoSplitEntity.getSpiltTail();
        String sourcePhone = templateInfoSplitEntity.getSourcePhone();
        String remark1 = templateInfoSplitEntity.getRemark1();
        String remark2 = templateInfoSplitEntity.getRemark2();
        String re = "(.*)";
        String templateStr = templateInfoSplitEntity.getTemplate();
        String regexStr = templateInfoSplitEntity.getSortStr();
        String[] user = templateStr.split("=");
        int length = user.length;
        Map<Integer, String> indexData = new HashMap<>();
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                indexData.put(originText.indexOf(user[0]), "bankIndex");
            }
            if (i == 1) {
                indexData.put(originText.indexOf(user[1]), "myselfTailNumberIndex");
            }
            if (i == 2) {
                indexData.put(originText.indexOf(user[2]), "counterpartyAccountNameIndex");
            }
            if (i == 3) {
                indexData.put(originText.indexOf(user[3]), "counterpartyTailNumberIndex");
            }
            if (i == 4) {
                indexData.put(originText.indexOf(user[4]), "transactionDateIndex");
            }
            if (i == 5) {
                indexData.put(originText.indexOf(user[5]), "transactionAmountIndex");
            }
            if (i == 6) {
                indexData.put(originText.indexOf(user[6]), "balanceIndex");
            }
        }
//        String template = "台州银行=2238=陈海飞=@=7月17日=585.00=4214.50";
//        templateInfoSplitEntity.setUserName(template);
//        templateInfoSplitEntity.setPwd("台州银行=2238=陈海飞;([\\u4e00-\\u9fa5]{2,5})=@=7月17日=585.00=4214.50");

        String template = "bankIndex;myselfTailNumberIndex;counterpartyAccountNameIndex;counterpartyTailNumberIndex;transactionDateIndex;transactionAmountIndex;balanceIndex;";

        List<Integer> sortList = indexData.keySet().stream()
                .filter(tmp -> null != tmp && tmp > 0)
                .sorted(Comparator.comparing(Integer::intValue))
                .collect(Collectors.toList());
        int index = 1;
        for (Integer sortIndex : sortList) {
            String a = indexData.get(sortIndex);
            template = template.replace(a, "$" + index++);
        }

        String[] split = regexStr.split("=");
        for (String s : split) {
            String[] split1 = s.split(";");
            regex = regex.replace(split1[0], split1.length == 1 ? re : split1[1]);
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
        System.out.println(regex);
        System.out.println(bankName);
        System.out.println(typeDetail);
        System.out.println(transactionType);
        System.out.println(template);
        System.out.println(regex);
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
    }
}
