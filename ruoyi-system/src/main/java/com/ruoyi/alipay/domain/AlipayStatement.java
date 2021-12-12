package com.ruoyi.alipay.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 对账对象 alipay_statement
 * 
 * @author ruoyi
 * @date 2021-12-11
 */
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
    private Long rollingFunds;

    /** 可取佣金 */
    @Excel(name = "可取佣金")
    private Long deductibleCommission;

    /** null */
    @Excel(name = "null")
    private Long deposit;

    /** null */
    @Excel(name = "null")
    private Long freeze;

    /** null */
    @Excel(name = "null")
    private Long businessBalance;

    /** 参考余额 */
    @Excel(name = "参考余额")
    private Long referBalance;

    /** 银行卡余额详细 */
    @Excel(name = "银行卡余额详细")
    private String cardBalanceDetail;

    /** 业务余额差额 */
    @Excel(name = "业务余额差额")
    private Long businessBalanceDiff;

    /** 参考余额差额 */
    @Excel(name = "参考余额差额")
    private Long referBalanceDiff;

    /** null */
    @Excel(name = "null", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dateTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }
    public void setUserType(String userType) 
    {
        this.userType = userType;
    }

    public String getUserType() 
    {
        return userType;
    }
    public void setRollingFunds(Long rollingFunds) 
    {
        this.rollingFunds = rollingFunds;
    }

    public Long getRollingFunds() 
    {
        return rollingFunds;
    }
    public void setDeductibleCommission(Long deductibleCommission) 
    {
        this.deductibleCommission = deductibleCommission;
    }

    public Long getDeductibleCommission() 
    {
        return deductibleCommission;
    }
    public void setDeposit(Long deposit) 
    {
        this.deposit = deposit;
    }

    public Long getDeposit() 
    {
        return deposit;
    }
    public void setFreeze(Long freeze) 
    {
        this.freeze = freeze;
    }

    public Long getFreeze() 
    {
        return freeze;
    }
    public void setBusinessBalance(Long businessBalance) 
    {
        this.businessBalance = businessBalance;
    }

    public Long getBusinessBalance() 
    {
        return businessBalance;
    }
    public void setReferBalance(Long referBalance) 
    {
        this.referBalance = referBalance;
    }

    public Long getReferBalance() 
    {
        return referBalance;
    }
    public void setCardBalanceDetail(String cardBalanceDetail) 
    {
        this.cardBalanceDetail = cardBalanceDetail;
    }

    public String getCardBalanceDetail() 
    {
        return cardBalanceDetail;
    }
    public void setBusinessBalanceDiff(Long businessBalanceDiff) 
    {
        this.businessBalanceDiff = businessBalanceDiff;
    }

    public Long getBusinessBalanceDiff() 
    {
        return businessBalanceDiff;
    }
    public void setReferBalanceDiff(Long referBalanceDiff) 
    {
        this.referBalanceDiff = referBalanceDiff;
    }

    public Long getReferBalanceDiff() 
    {
        return referBalanceDiff;
    }
    public void setDateTime(Date dateTime) 
    {
        this.dateTime = dateTime;
    }

    public Date getDateTime() 
    {
        return dateTime;
    }

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
