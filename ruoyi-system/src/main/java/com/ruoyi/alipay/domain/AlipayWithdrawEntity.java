package com.ruoyi.alipay.domain;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.alipay.domain.util.DesUtil;
import com.ruoyi.alipay.domain.util.DesUtil2;
import com.ruoyi.alipay.domain.util.EncryptHexUtil;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Calendar;
import java.util.Date;

/**
 * 会员提现记录对象 alipay_withdraw
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Data
public class AlipayWithdrawEntity extends BaseEntity {




    private String  dealDescribe;

    private String pushOrder;//推送字段  1 默认       3 上游驳回

    private String macthMsg;  ///撮合订单 解释
    private Integer macthStatus;  ///撮合订单 状态   1已撮合 未支付     2 已撮合 已支付
    private Integer macthLock;  /// 撮合锁定当前不可以进行任何操作，  默认不锁定 0    1 锁定
    private Integer moreMacth;  /// 是否可以多次撮合[是否挂起]， 0 不可以  1 可以      可以就是挂起
    private Integer macthCount;  ///  撮合次数
    private Integer watingTime;  ///  撮合次数

    /* BigDecimal usdt ,   //花费usdt
           price  ,    //汽油价格
           used,       //使用汽油数
           eth,        //花费eth
           priceUsdt;  //eth - usdt 汇率
   String hash;        //订单hash* */
    private String usdt;
    private String price;
    private String used;
    private String eth;
    private String priceUsdt;
    private String hash;
    @Excel(name = "是否结算eth手续费", readConverterExp = "0=未结算,1=已结算")
    private Integer ethFee;
    @Excel(name = "USDT-hash")
    private String txhash;
    private String USDTamount;//usdt 充值金额
    private String USDTRate;//usdt 充值金额
    private String sunCountAmount = "0";
    private String sunCountAmountFee = "0";
    private String sunCountActualAmount = "0";
    public String getSunCountActualAmount() {
        return sunCountActualAmount;
    }

    public void setSunCountActualAmount(String sunCountActualAmount) {
        this.sunCountActualAmount = sunCountActualAmount;
    }

    public String getSunCountAmountFee() {
        return sunCountAmountFee;
    }

    public void setSunCountAmountFee(String sunCountAmountFee) {
        this.sunCountAmountFee = sunCountAmountFee;
    }

    public String getSunCountAmount() {
        return sunCountAmount;
    }

    public void setSunCountAmount(String sunCountAmount) {
        this.sunCountAmount = sunCountAmount;
    }

    public String getUSDTRate() {
        return USDTRate;
    }

    public void setUSDTRate(String USDTRate) {
        this.USDTRate = USDTRate;
    }

    public String getUSDTamount() {
        return USDTamount;
    }

    public void setUSDTamount(String USDTamount) {
        this.USDTamount = USDTamount;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPriceUsdt() {
        return priceUsdt;
    }

    public void setPriceUsdt(String priceUsdt) {
        this.priceUsdt = priceUsdt;
    }

    public String getEth() {
        return eth;
    }

    public void setEth(String eth) {
        this.eth = eth;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private static final long serialVersionUID = 1L;
    private String bankcode;
    private String appOrderId;

    public String getAppOrderId() {
        return appOrderId;
    }

    public void setAppOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getBankcode() {
        return this.bankcode;
    }

    /**
     * 数据id(主键索引)
     */
    private Long id;
    /**
     * 会员提现单号
     */
    @Excel(name = "会员提现单号")
    private String orderId;
    /**
     * 会员id(唯一识别号)(index索引)
     */
    @Excel(name = "会员id(唯一识别号)(index索引)")
    private String userId;
    /**
     * 商户提现1，码商提现2
     */
    @Excel(name = "商户提现1，码商提现2")
    private String withdrawType;
    /**
     * 银行卡号
     */
    @Excel(name = "银行卡号")
    private String bankNo;
    /**
     * 提现人姓名
     */
    @Excel(name = "提现人姓名")
    private String accname;
    /**
     * 0预下单1处理中2成功3失败
     */
    @Excel(name = "订单状态", readConverterExp = "0=预下单,1=处理中,2=成功,3=失败")
    private String orderStatus;
    /**
     * 开户行姓名
     */
    @Excel(name = "开户行姓名")
    private String bankName;
    /**
     * 提现金额
     */
    @Excel(name = "提现金额")
    private Double amount;
    private String amount1;
    /**
     * 手续费
     */
    @Excel(name = "手续费")
    private Double fee;
    private String fee1;
    /**
     * 真实到账金额
     */
    @Excel(name = "真实到账金额")
    private Double actualAmount;
    private String actualAmount1;
    @Excel(name = "结算", readConverterExp = "0=未扣款结算,1=已扣款结算")
    private String payStatus;

    public String getPayStatus() {
        return payStatus;
    }
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    /**
     * 手机号
     */
    @Excel(name = "手机号")
    private String mobile;
    /**
     * 提现成功回调地址
     */
    @Excel(name = "提现成功回调地址")
    private String notify;
    /**
     * 数据修改时间
     */
    @Excel(name = "创建时间", width = 30, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
     * 数据修改时间
     */
    @Excel(name = "数据修改时间", width = 30, dateFormat = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date submitTime;

    /**
     * 1数据可用2数据无用
     */
    @Excel(name = "1数据可用2数据无用")
    private Integer status;

    public Integer getExported() {
        return exported;
    }

    public void setExported(Integer exported) {
        this.exported = exported;
    }

    @Excel(name = "0未导出1已导出")
    private Integer exported;

    /**
     * 备用字段添加业务使用
     */
    @Excel(name = "代付来源")
    private String retain1;

    /**
     * 备用字段添加业务使用
     */
    @Excel(name = "备用字段添加业务使用")
    private String retain2;

    /**
     * 审核人
     */
    @Excel(name = "后台申请人")
    private String apply;
    private String sgin;

    public String getActualAmount1() {
        return actualAmount1;
    }

    public void setActualAmount1(String actualAmount1) {
        this.actualAmount1 = actualAmount1;
    }

    public String getFee1() {
        return fee1;
    }

    public void setFee1(String fee1) {
        this.fee1 = fee1;
    }

    public String getAmount1() {
        return amount1;
    }

    public void setAmount1(String amount1) {
        this.amount1 = amount1;
    }

    public String getSgin() {
        return sgin;
    }

    public void setSgin(String sgin) {
        this.sgin = sgin;
    }

    /**
     * 审核人
     */
    @Excel(name = "审核人")
    private String approval;
    @Excel(name = "代付产品")
    private String witType;
    //代付渠道
    private String witChannel;
    @Excel(name = "实际代付渠道")
    private String channelId;//实际代付渠道

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getWitType() {
        return witType;
    }

    public void setWitType(String witType) {
        this.witType = witType;
    }

    @Excel(name = "货币类型")
    private String currency;

    public String getUsdt() {
        return usdt;
    }

    public void setUsdt(String usdt) {
        this.usdt = usdt;
    }

    public Integer getEthFee() {
        return ethFee;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setEthFee(Integer ethFee) {
        this.ethFee = ethFee;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    /**
     * 审核意见
     */
    @Excel(name = "审核意见")
    private String comment;
    /**
     * 请求参数
     */
    private String request;
    /**
     * 响应参数
     */
    private String response;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setWithdrawType(String withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String getWithdrawType() {
        return withdrawType;
    }

    public String getWitChannel() {
        return witChannel;
    }

    public void setWitChannel(String witChannel) {
        this.witChannel = witChannel;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankNo() {
        if(null != createTime){
            try {
                 DesUtil2.des.decryptStr(bankNo);
            }catch (Exception e) {
                //   System.exit(1);//敏感信息解密失败 直接服务器死机
                if( ! getDate (createTime)){
                    return  DesUtil.decryptStr(bankNo);
                }
            }
        }
        return DesUtil2.decryptStr( bankNo);
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public String getAccname() {
        return   DesUtil2.decryptStr(accname);
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() {
        if(StrUtil.isNotBlank(bankNo) && getDate (createTime) && StrUtil.isNotEmpty(sgin) ){
            Boolean click = EncryptHexUtil.click(DesUtil2.decryptStr(bankNo), DesUtil2.decryptStr(bankName), DesUtil2.decryptStr(accname), getAmount() + "", sgin);
            if(!click){
                return DesUtil2.decryptStr(bankName) + "  当前存在入侵  ";
            }
        }
        return DesUtil2.decryptStr(bankName);
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        try {
            return Double.valueOf(DesUtil2.decryptStr(amount1))  ;
        }catch (Exception e ){
            return  amount;
        }

    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
    public Double getFee() {
        try {
            return Double.valueOf(DesUtil2.decryptStr(fee1));
        }catch (Exception s ){
            return fee;

        }

    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Double getActualAmount() {
        try {
            return Double.valueOf(DesUtil2.decryptStr(actualAmount1));
        }catch (Exception e ){
            return actualAmount;
        }
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getNotify() {
        return notify;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setRetain1(String retain1) {
        this.retain1 = retain1;
    }

    public String getRetain1() {
        return retain1;
    }

    public void setRetain2(String retain2) {
        this.retain2 = retain2;
    }

    public String getRetain2() {
        return retain2;
    }

    public String getApply() {
        return apply;
    }

    public void setApply(String apply) {
        this.apply = apply;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("userId", getUserId())
                .append("withdrawType", getWithdrawType())
                .append("bankNo", getBankNo())
                .append("accname", getAccname())
                .append("orderStatus", getOrderStatus())
                .append("bankName", getBankName())
                .append("amount", getAmount())
                .append("fee", getFee())
                .append("actualAmount", getActualAmount())
                .append("mobile", getMobile())
                .append("notify", getNotify())
                .append("createTime", getCreateTime())
                .append("submitTime", getSubmitTime())
                .append("status", getStatus())
                .append("retain1", getRetain1())
                .append("retain2", getRetain2())
                .append("apply",getApply())
                .append("approval", getApproval())
                .append("comment", getComment())
                .toString();
    }
    private static  Boolean getDate(Date date1){
        //获取当前时间
        Calendar date = Calendar.getInstance();
        date.setTime(date1);
        //获取开始时间
        Calendar begin = Calendar.getInstance();
        begin.setTime(date1);
        //获取结束时间
        Calendar end = Calendar.getInstance();
        end.setTime(DateUtil.parseDate("2022-09-14 00:00:01"));
        if (date.after(end)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }
}
