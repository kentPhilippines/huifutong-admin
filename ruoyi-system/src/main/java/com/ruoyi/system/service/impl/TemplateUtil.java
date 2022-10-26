package com.ruoyi.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.enums.TransactionTypeEnum;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.domain.TemplateInfoSplitEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateUtil {
    public static final String NUM="^\\d*$";


    public static AlipayMessageReg insertTemplate(TemplateInfoSplitEntity templateInfoSplitEntity) {
        String regex = templateInfoSplitEntity.getSourceMsg();
        String originText = regex;
        regex = regex.replace("[", "\\[").replace("]", "\\]")
                .replace("(", "\\(").replace(")", "\\)");
        String bankName = templateInfoSplitEntity.getBankName();
        String typeDetail = templateInfoSplitEntity.getTypeDetail();
        String transactionType = templateInfoSplitEntity.getTransactionType();
        String blackKey = templateInfoSplitEntity.getBlackKey();
        Integer tailType = templateInfoSplitEntity.getTailType();
        String reTail = "(.*)";
        if (null != tailType && tailType == 1) {
            reTail = "\\*{1,}(.*)";
        } else if (null != tailType && tailType == 2) {
            reTail = "\\d{1,}\\*{1,}(.*)";
        }
        String flexConfig = templateInfoSplitEntity.getFlexConfig();
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

        String[] split2 = flexConfig.split("@");
        List<String> collect = Arrays.stream(split2).filter(tmp -> StringUtils.isNoneBlank(tmp)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
            for (String s : collect) {
                indexData.put(originText.indexOf(s), "flex" + s);
                regex = regex.replace(s, re);
            }
        }

        String template = "bankIndex;myselfTailNumberIndex;counterpartyAccountNameIndex;counterpartyTailNumberIndex;transactionDateIndex;transactionAmountIndex;balanceIndex;";

        List<Integer> sortList = indexData.keySet().stream()
                .filter(tmp -> null != tmp && tmp >= 0)
                .sorted(Comparator.comparing(Integer::intValue))
                .collect(Collectors.toList());
        int index = 0;
        for (Integer sortIndex : sortList) {
            index++;
            String a = indexData.get(sortIndex);
            if (!a.contains("flex")) {
                template = template.replace(a, "$" + index);
            }
        }
        String[] split = regexStr.split("=");
        int i = 0;
        for (String s : split) {
            i++;
            if (i == 2) {
                String[] split1 = s.split(";");
                regex = regex.replace(split1[0], split1.length == 1 ? reTail : split1[1]);
            } else {
                String[] split1 = s.split(";");
                regex = regex.replace(split1[0], split1.length == 1 ? re : split1[1]);
            }
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
//        alipayMessageReg.setBlackKey(blackKey);
        alipayMessageReg.setTransactionType(transactionType);
        alipayMessageReg.setTransactionTypeDetail(typeDetail);
        alipayMessageReg.setRemark1(remark1);
        alipayMessageReg.setRemark2(remark2);
        alipayMessageReg.setTemplateFlag("1");
        alipayMessageReg.setBankName(bankName);

        String[] split4 = s.split(";");
        String myselfTail = StringUtils.equals(split4[1], "@") ? "" : split4[1].trim();
        String transactionAmount = StringUtils.equals(split4[5], "@") ? "" : split4[5].trim();
        String balance = StringUtils.equals(split4[6], "@") ? "" : split4[6].trim();
        if (!StrUtil.isBlank(myselfTail)&&!Pattern.matches(NUM,myselfTail)){
            throw new BusinessException("尾号解析出错:"+myselfTail);
        }


        if (!StrUtil.isBlank(balance)){
            String balanceStr=balance
                    .replace(",", "")
                    .replace("+", "")
                    .replace("-", "")
                    .replace(".","");
            if (!Pattern.matches(NUM,balanceStr)) {
                throw new BusinessException("余额解析出错:"+balanceStr);
            }
        }

        if (!StrUtil.isBlank(transactionAmount)){
            String transactionAmountStr=transactionAmount
                    .replace(",", "")
                    .replace("+", "")
                    .replace("-", "")
                    .replace(".","");
            if (!Pattern.matches(NUM,transactionAmountStr)) {
                throw new BusinessException("转账金额解析出错:"+transactionAmountStr);
            }
        }

        if (match && StringUtils.isNotBlank(s)) {
            return alipayMessageReg;
        }

        return null;
    }

    public static void main(String[] args) {
        String regexStr = "营口银行=623177000001****752=@;([\\u4e00-\\u9fa5]{2,5})=@=30日13:43=3,018.00=51.94";
        String regex = "您的账户623177000001****752于30日13:43在总行清算中心转帐支出3,018.00元,余额51.94元。本机构吸收的本外币存款依照《存款保险条例》受到保护【营口银行】".replace("[", "\\[").replace("]", "\\]")
                .replace("(", "\\(").replace(")", "\\)");
        String[] split = regexStr.split("=");
        int i = 0;
        for (String s : split) {
            i++;
            if (i == 2) {
                String[] split1 = s.split(";");
                regex = regex.replace(split1[0], split1.length == 1 ? "\\d{1,}\\*{1,}(.*)" : split1[1]);
            } else {
                String[] split1 = s.split(";");
                regex = regex.replace(split1[0], split1.length == 1 ? "(.*)" : split1[1]);
            }
        }
    }
}
