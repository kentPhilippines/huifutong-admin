package com.ruoyi.alipay.vo;

import java.math.BigDecimal;

public class UserCountBean {
    private String agent;//直接代理
    private String userAgent;//直接会员
    private String userAgentCount;//代理商个数
    private String userCount;//会员个数
    private Integer moreAgent;//多级代理
    private BigDecimal moreAmountRunR;//入款
    private BigDecimal moreAmountRunW;//出款
    private Integer moreDealCount;//多级交易笔数
    private String moreDealProfit;//多级分润

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getUserAgent() {
        return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getUserAgentCount() {
		return userAgentCount;
	}
	public void setUserAgentCount(String userAgentCount) {
		this.userAgentCount = userAgentCount;
	}
	public String getUserCount() {
		return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public Integer getMoreAgent() {
        return moreAgent;
    }

    public void setMoreAgent(Integer moreAgent) {
        this.moreAgent = moreAgent;
    }

    public Integer getMoreDealCount() {
        return moreDealCount;
    }

    public void setMoreDealCount(Integer moreDealCount) {
        this.moreDealCount = moreDealCount;
    }

    public String getMoreDealProfit() {
        return moreDealProfit;
    }

    public void setMoreDealProfit(String moreDealProfit) {
        this.moreDealProfit = moreDealProfit;
    }

    public BigDecimal getMoreAmountRunR() {
        return moreAmountRunR;
    }

    public void setMoreAmountRunR(BigDecimal moreAmountRunR) {
        this.moreAmountRunR = moreAmountRunR;
    }

    public BigDecimal getMoreAmountRunW() {
        return moreAmountRunW;
    }

    public void setMoreAmountRunW(BigDecimal moreAmountRunW) {
        this.moreAmountRunW = moreAmountRunW;
    }
}
