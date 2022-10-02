package com.ruoyi.alipay.domain;

import com.ruoyi.alipay.domain.util.DesUtil;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 收款媒介列对象 alipay_medium
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Data
public class AlipayMediumEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private  String bankSumAmountsys = "0";//银行卡系统余额汇总
    private  String bankSumAmountnow = "0";//银行卡 当前余额
    private  String openSumBankAmountsys = "0";//系统开启 业务余额汇总
    private  String openSumBankAmountnow = "0" ;//银行卡 开启   实时余额汇总
    private  String startFund ;
    private  String deposit ;
    private  String fund ;
    private  String freezeBalance ;
    private  String payInfo ;
    private  String cre ;
    private BigDecimal toDayDeal;//当日入款
    private BigDecimal sumDayDeal;//累计入款
    private BigDecimal toDayWit;//当日出款
    private BigDecimal sumAmounlimit;//日总结单限制
    private BigDecimal sumDayWit;//累计出款
    private BigDecimal startAmount;//起始收款金额



    private BigDecimal maxAmount;//最大收款金额
    private BigDecimal yseToday;//起始收款金额
    private String startTime;//接单开始时间  格式    hh:mm:ss
    private String endTime;//接单结束 时间
    private Integer isClickPay;// 收款户名验证是否验证户名   1 验证    0 不验证
    private Long sc;//接单间隔秒数
    private Integer todayCount;//当日交易笔数
    private Integer sumCount;//累计交易笔数
    private Integer countLimit;//日交易限制笔数
    private Integer autoWit;//1 开启。0 关闭 默认 关闭  自动出款

    private Integer coolDownTime;//接单冷却时间

    private Integer todayCountWit;//当日出款笔数
    private Integer sumCountWit;//累计出款笔数
    public Integer getAutoWit() {
        return autoWit;
    }
    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
    public void setAutoWit(Integer autoWit) {
        this.autoWit = autoWit;
    }
    public Integer getTodayCountWit() {
        return todayCountWit;
    }

    public void setTodayCountWit(Integer todayCountWit) {
        this.todayCountWit = todayCountWit;
    }

    public Integer getSumCountWit() {
        return sumCountWit;
    }

    public void setSumCountWit(Integer sumCountWit) {
        this.sumCountWit = sumCountWit;
    }
    public Integer getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(Integer todayCount) {
        this.todayCount = todayCount;
    }

    public Integer getSumCount() {
        return sumCount;
    }

    public void setSumCount(Integer sumCount) {
        this.sumCount = sumCount;
    }

    public Integer getCountLimit() {
        return countLimit;
    }

    public void setCountLimit(Integer countLimit) {
        this.countLimit = countLimit;
    }
    /*
            是否是小额
             */
    private boolean small=false;
    public boolean isSmall() {
        return small;
    }

    public void setSmall(boolean small) {
        this.small = small;
    }
    public BigDecimal getSumAmounlimit() {
        return sumAmounlimit;
    }

    public void setSumAmounlimit(BigDecimal sumAmounlimit) {
        this.sumAmounlimit = sumAmounlimit;
    }

    public BigDecimal getYseToday() {
        return yseToday;
    }

    public void setYseToday(BigDecimal yseToday) {
        this.yseToday = yseToday;
    }

    public Long getSc() {
        return sc;
    }

    public void setSc(Long sc) {
        this.sc = sc;
    }

    public Integer getIsClickPay() {
        return isClickPay;
    }

    public void setIsClickPay(Integer isClickPay) {
        this.isClickPay = isClickPay;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public BigDecimal getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(BigDecimal startAmount) {
        this.startAmount = startAmount;
    }

    public BigDecimal getSumDayWit() {
        return sumDayWit;
    }

    public void setSumDayWit(BigDecimal sumDayWit) {
        this.sumDayWit = sumDayWit;
    }

    public BigDecimal getToDayWit() {
        return toDayWit;
    }

    public void setToDayWit(BigDecimal toDayWit) {
        this.toDayWit = toDayWit;
    }

    public BigDecimal getSumDayDeal() {
        return sumDayDeal;
    }

    public void setSumDayDeal(BigDecimal sumDayDeal) {
        this.sumDayDeal = sumDayDeal;
    }

    public BigDecimal getToDayDeal() {
        return toDayDeal;
    }

    public void setToDayDeal(BigDecimal toDayDeal) {
        this.toDayDeal = toDayDeal;
    }






    public String getCre() {
        return cre;
    }

    public void setCre(String cre) {
        this.cre = cre;
    }

    private  Integer isRed;

    public Integer getIsRed() {
        return isRed;
    }

    public void setIsRed(Integer isRed) {
        this.isRed = isRed;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getFreezeBalance() {
        return freezeBalance;
    }

    public void setFreezeBalance(String freezeBalance) {
        this.freezeBalance = freezeBalance;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getStartFund() {
        return startFund;
    }

    public void setStartFund(String startFund) {
        this.startFund = startFund;
    }

    public String getBankSumAmountnow() {
        return bankSumAmountnow;
    }

    public void setBankSumAmountnow(String bankSumAmountnow) {
        this.bankSumAmountnow = bankSumAmountnow;
    }

    public String getOpenSumBankAmountnow() {
        return openSumBankAmountnow;
    }

    public void setOpenSumBankAmountnow(String openSumBankAmountnow) {
        this.openSumBankAmountnow = openSumBankAmountnow;
    }

    public String getOpenSumBankAmountsys() {
        return openSumBankAmountsys;
    }

    public void setOpenSumBankAmountsys(String openSumBankAmountsys) {
        this.openSumBankAmountsys = openSumBankAmountsys;
    }

    public String getBankSumAmountsys() {
        return bankSumAmountsys;
    }

    public void setBankSumAmountsys(String bankSumAmountsys) {
        this.bankSumAmountsys = bankSumAmountsys;
    }

    private  String amounttype;

    public String getAmounttype() {
        return amounttype;
    }

    public void setAmounttype(String amounttype) {
        this.amounttype = amounttype;
    }

    /**
     * 数据id
     */
    private Long id;

    /**
     * 收款媒介id(本地编号)
     */
    private String mediumId;

    /**
     * 收款媒介登录账号
     */
    private String mediumNumber;

    public String getMediumNumberJiami() {
        return mediumNumberJiami;
    }

    public void setMediumNumberJiami(String mediumNumberJiami) {
        this.mediumNumberJiami = mediumNumberJiami;
    }

    //加密卡号
    private String mediumNumberJiami;
    private String error;
    private String witAmount;

    public String getWitAmount() {
        return witAmount;
    }

    public void setWitAmount(String witAmount) {
        this.witAmount = witAmount;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * 收款媒介持有人
     */
    @Excel(name = "收款媒介持有人")
    private String mediumHolder;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String mediumNote;

    /**
     * 收款媒介绑定手机号
     */
    @Excel(name = "收款媒介绑定手机号")
    private String mediumPhone;
    private String black;

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    /**
     * 收款媒介所属商户
     */
    @Excel(name = "收款媒介所属商户")
    private String qrcodeId;

    /**
     * 收款媒介标识:alipay(支付宝),weichar(微信),card(银行卡),other(暂未开放)
     */
    @Excel(name = "收款媒介标识:alipay(支付宝),weichar(微信),card(银行卡),other(暂未开放)")
    private String code;

    /**
     * 数据修改时间
     */
    @Excel(name = "数据修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /**
     * 状态:1可使用；0不可使用
     */
    @Excel(name = "状态:1可使用；0不可使用")
    private Integer status;

    /**
     * 是否逻辑删除：1删除2可用
     */
    @Excel(name = "是否逻辑删除：1删除2可用")
    private String isDeal;


    private String bankcode;                    //R 为 入款    W  为出款
    private String account;                     //银行账户    如中国工商银行
    private String mountNow;                    // 当前媒介实际金额
    private String mountSystem;                 //当前媒介系统金额
    private String mountLimit;                  //当前媒介限制金额   系统默认一万
    private String attr;                        //收款媒介供应链标识
    private String notfiyMask;                  //回调标识


    public String getNotfiyMask() {
        return notfiyMask;
    }

    public void setNotfiyMask(String notfiyMask) {
        this.notfiyMask = notfiyMask;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getMountLimit() {
        return mountLimit;
    }

    public void setMountLimit(String mountLimit) {
        this.mountLimit = mountLimit;
    }

    public String getMountSystem() {
        return mountSystem;
    }

    public void setMountSystem(String mountSystem) {
        this.mountSystem = mountSystem;
    }

    public String getMountNow() {
        return mountNow;
    }

    public void setMountNow(String mountNow) {
        this.mountNow = mountNow;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMediumId(String mediumId) {
        this.mediumId = mediumId;
    }

    public String getMediumId() {
        return mediumId;
    }

    public void setMediumNumber(String mediumNumber) {
        this.mediumNumber = mediumNumber;
    }

    public String getMediumNumber() {
        return DesUtil.decryptStr(mediumNumber) ;
    }

    public void setMediumHolder(String mediumHolder) {
        this.mediumHolder = mediumHolder;
    }

    public String getMediumHolder() {
        return mediumHolder;
    }

    public void setMediumNote(String mediumNote) {
        this.mediumNote = mediumNote;
    }

    public String getMediumNote() {
        return mediumNote;
    }

    public void setMediumPhone(String mediumPhone) {
        this.mediumPhone = mediumPhone;
    }

    public String getMediumPhone() {
        return mediumPhone;
    }

    public void setQrcodeId(String qrcodeId) {
        this.qrcodeId = qrcodeId;
    }

    public String getQrcodeId() {
        return qrcodeId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
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

    public void setIsDeal(String isDeal) {
        this.isDeal = isDeal;
    }

    public String getIsDeal() {
        return isDeal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mediumId", getMediumId())
                .append("mediumNumber", getMediumNumber())
                .append("mediumHolder", getMediumHolder())
                .append("mediumNote", getMediumNote())
                .append("mediumPhone", getMediumPhone())
                .append("qrcodeId", getQrcodeId())
                .append("code", getCode())
                .append("createTime", getCreateTime())
                .append("submitTime", getSubmitTime())
                .append("status", getStatus())
                .append("isDeal", getIsDeal())
                .toString();
    }
}
