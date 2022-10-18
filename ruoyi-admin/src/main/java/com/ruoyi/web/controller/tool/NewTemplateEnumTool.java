package com.ruoyi.web.controller.tool;

import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.system.domain.AlipayMessageReg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author water
 */
public class NewTemplateEnumTool {
    public static Logger log = LoggerFactory.getLogger(NewTemplateEnumTool.class);




    public static boolean checkDate(AlipayMessageReg value, String text, BankInfoSplitEntity bankInfoSplitEntity, DateDetail dateDetail) {
        try {
            String regex = value.getRegex();
            boolean match = ReUtil.isMatch(regex, text);
            if (match) {
                String s = ReUtil.extractMulti(value.getRegex(), text, value.getTemplate());
                String[] split = s.split(";");
                String bankName = StringUtils.equals(split[0], "@") ? "" : split[0].trim();
                String counterpartyName = StringUtils.equals(split[2], "@") ? "" : split[2].trim();
                String counterpartyTail = StringUtils.equals(split[3], "@") ? "" : split[3].trim();
                String transactionTime = StringUtils.equals(split[4], "@") ? "" : split[4].trim();
                String transactionAmount = StringUtils.equals(split[5], "@") ? "" : split[5].trim();
                String balance = StringUtils.equals(split[6], "@") ? "" : split[6].trim();
                bankInfoSplitEntity.setBankName(bankName);
                if (StringUtils.isBlank(bankName) && StringUtils.isNotBlank(value.getBankName())) {
                    bankInfoSplitEntity.setBankName(value.getBankName());
                }
                bankInfoSplitEntity.setCounterpartyAccountName(counterpartyName);
                bankInfoSplitEntity.setCounterpartyTailNumber(counterpartyTail);
                bankInfoSplitEntity.setTransactionAmount(transactionAmount.replace(",", "").replace("+", ""));
                bankInfoSplitEntity.setBalance(balance.replace(",", ""));
                bankInfoSplitEntity.setTypeDetail(value.getTransactionTypeDetail());
                bankInfoSplitEntity.setTransactionType(value.getTransactionType());
                dateDetail.setTransactionType(value.getTransactionType());
                dateDetail.setText(text);
                bankInfoSplitEntity.setTransactionDate(BankInfoAnalysisAlgorithmTool.getDate(transactionTime, dateDetail));
                bankInfoSplitEntity.setOriginText(text);
                bankInfoSplitEntity.setResultText(JSON.toJSONString(bankInfoSplitEntity));
            }
        } catch (BusinessException bi) {
            return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
