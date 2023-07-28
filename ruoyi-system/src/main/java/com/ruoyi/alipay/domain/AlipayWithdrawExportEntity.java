package com.ruoyi.alipay.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 会员提现记录对象 alipay_withdraw
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Data
public class AlipayWithdrawExportEntity  {
    @Excel(name = "手机号/卡号")
    private String bankNo;

    @Excel(name = "名字/收款方银行账户名称")
    private String accname;

    @Excel(name = "付款金额")
    private Double amount;

    @Excel(name = "转账类型（支付宝/银行卡）")
    private String bankcode;

    @Excel(name = "订单备注")
    private String remark;

    @Excel(name = "机构名称（仅银行卡输入）")
    private String name;
}
