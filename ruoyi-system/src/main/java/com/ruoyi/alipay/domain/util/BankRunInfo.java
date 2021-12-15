package com.ruoyi.alipay.domain.util;


import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行卡交易明细流水   补全自动对账
 */
@Data
public class BankRunInfo  extends BaseEntity {
    @Excel(name = "订单号")
    private String orderId;//订单号
    @Excel(name = "银行明细")
    private String bankNo;//卡号
    @Excel(name = "帐变类型",readConverterExp = "1=收入,2=支出")
    private  String runType; //帐变类型   1 收入   4 支出
    @Excel(name = "用户")
    private String userId;
    @Excel(name = "核对数")
    private BigDecimal number;//核数
    @Excel(name = "小组余额")
    private BigDecimal amount;//小组余额    =   滚动资金 + 佣金
    @Excel(name = "订单金额")
    private BigDecimal dealAmount;//订单金额
    @Excel(name = "银行卡余额")
    private BigDecimal bankNow;//银行卡余额， 这个数据可能为空
    @Excel(name = "业务余额")
    private BigDecimal bAmount;//业务余额
    @Excel(name = "业务余额差额")
    private BigDecimal suAmount;//差额
    @Excel(name = "原始短信")
    private String msg;//原始短信
    @Excel(name = "结算摘要")
    private String settl;//结算信息     如果为入款则 存放 付款人信息   如果为出款则存放  出款详细数据

    @Excel(name = "结算时间")
    private Date submitTime;//时间
    @Excel(name = "创建时间")
    private Date createTime;//时间
}
