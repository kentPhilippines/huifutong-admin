package com.ruoyi.alipay.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AlipayUserMedium {
    private String mediumNumber;
    private String mediumHolder;
    private String account;
    private BigDecimal businessBalance;
    private BigDecimal referBalance;
    private BigDecimal toDayDeal;
    private BigDecimal sumDayDeal;
    private BigDecimal toDayWit;
    private BigDecimal sumDayWit;

}
