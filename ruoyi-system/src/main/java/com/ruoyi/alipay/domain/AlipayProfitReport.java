package com.ruoyi.alipay.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 利润报表
 */
@Data
public class AlipayProfitReport {

    private String dateTime;

    /**
     * 商户交易总量
     */
    private BigDecimal merchantTradeTotal;

    /**
     * 自营渠道交易量
     */
    private BigDecimal selfChannelTradeTotal;

    /**
     * 外接渠道交易量
     */
    private BigDecimal externalChannelTradeTotal;


    /**
     * 四方营收  四方营收 = （代收服务费+代付服务费）-四方代理佣金-外接渠道交易成本-外接渠道代付成本
     */
    private BigDecimal fouthPartRevenue;

    /**
     * 代收服务费
     */
    private BigDecimal collectServiceFee;

    /**
     * 代付服务费
     */
    private BigDecimal payServiceFee;

    /**
     * 四方代理佣金
     */
    private BigDecimal fourthPartiesCommission;


    /**
     * 外接渠道交易成本
     */
    private BigDecimal externalChannelTransactionCost;

    /**
     * 返点码商
     */
    private BigDecimal cardSupplierRebate;

    /**
     * 返点技术
     */
    private BigDecimal itRebate;

    /**
     * 营业利润
     */
    private BigDecimal profit;


    /**
     * 四方营收  四方营收 = （代收服务费+代付服务费）-四方代理佣金-外接渠道交易成本-外接渠道代付成本
     */
    public BigDecimal getFouthPartRevenue() {
        fouthPartRevenue = payServiceFee.add(collectServiceFee).subtract(fourthPartiesCommission).subtract(externalChannelTransactionCost);
        return fouthPartRevenue;
    }

    /**
     * 四方营收-返点码商-返点技术
     * @return
     */
    public BigDecimal getProfit() {
        profit = getFouthPartRevenue().subtract(itRebate).subtract(cardSupplierRebate);
        return profit;
    }
}
