package com.ruoyi.alipay.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * //银行卡号 ： 持卡人： 开户行：业务余额：参考余额：当日入款 当日出款。累计入款累计出款
 */

@Data
public class AlipayUserMedium {
    @JSONField(name="卡号")
    private String mediumNumber;
    @JSONField(name="持卡人")
    private String mediumHolder;
    @JSONField(name="开户行")
    private String account;
    @JSONField(name="业务余额")
    private BigDecimal businessBalance;
    @JSONField(name="参考余额")
    private BigDecimal referBalance;
    @JSONField(name="当日入款")
    private BigDecimal toDayDeal;
    @JSONField(name="累计入款")
    private BigDecimal sumDayDeal;
    @JSONField(name="当日出款")
    private BigDecimal toDayWit;
    @JSONField(name="累计出款")
    private BigDecimal sumDayWit;


}
