package com.ruoyi.alipay.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 对账对象 alipay_statement
 * 
 * @author ruoyi
 * @date 2021-12-11
 */
@Data
public class AlipayStatement extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** null */
    private Long id;

    /** null */
    @Excel(name = "null")
    private String userId;

    /** null */
    @Excel(name = "null")
    private String userType;

    /** 滚动资金 */
    @Excel(name = "滚动资金")
    private BigDecimal rollingFunds;

    /** 可取佣金 */
    @Excel(name = "可取佣金")
    private BigDecimal deductibleCommission;

    /** null */
    @Excel(name = "null")
    private BigDecimal deposit;

    /** null */
    @Excel(name = "null")
    private BigDecimal freeze;

    /** null */
    @Excel(name = "null")
    private BigDecimal businessBalance;

    /** 参考余额 */
    @Excel(name = "参考余额")
    private BigDecimal referBalance;

    /** 银行卡余额详细 */
    @Excel(name = "银行卡余额详细")
    private String cardBalanceDetail;

    /** 业务余额差额 */
    @Excel(name = "业务余额差额")
    private BigDecimal businessBalanceDiff;

    /** 参考余额差额 */
    @Excel(name = "参考余额差额")
    private BigDecimal referBalanceDiff;

    /** null */
    @Excel(name = "null", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    private BigDecimal toDayDeal;
    private BigDecimal sumDayDeal;
    private BigDecimal toDayWit;
    private BigDecimal sumDayWit;



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("userType", getUserType())
            .append("rollingFunds", getRollingFunds())
            .append("deductibleCommission", getDeductibleCommission())
            .append("deposit", getDeposit())
            .append("freeze", getFreeze())
            .append("businessBalance", getBusinessBalance())
            .append("referBalance", getReferBalance())
            .append("cardBalanceDetail", getCardBalanceDetail())
            .append("businessBalanceDiff", getBusinessBalanceDiff())
            .append("referBalanceDiff", getReferBalanceDiff())
            .append("remark", getRemark())
            .append("dateTime", getDateTime())
            .toString();
    }
}
