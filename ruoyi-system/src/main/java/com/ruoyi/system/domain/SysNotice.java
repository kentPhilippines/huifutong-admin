package com.ruoyi.system.domain;

import javax.validation.constraints.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 通知公告表 sys_notice
 * 
 * @author ruoyi
 */
public class SysNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    /** 支付url */
    private String payDocUrl;

    /** 交易接口网关 */
    private String payGateway;

    /** 回调ip信息 */
    private String callBackIp;

    /** 产品页面展示*/
    private String productInfo;

    /**
     * 角色组
     */
    private Long[] roleIds;

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle)
    {
        this.noticeTitle = noticeTitle;
    }

    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    public String getNoticeTitle()
    {
        return noticeTitle;
    }

    public void setNoticeType(String noticeType)
    {
        this.noticeType = noticeType;
    }

    public String getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public String getPayDocUrl() {
        return payDocUrl;
    }

    public void setPayDocUrl(String payDocUrl) {
        this.payDocUrl = payDocUrl;
    }

    public String getPayGateway() {
        return payGateway;
    }

    public void setPayGateway(String payGateway) {
        this.payGateway = payGateway;
    }

    public String getCallBackIp() {
        return callBackIp;
    }

    public void setCallBackIp(String callBackIp) {
        this.callBackIp = callBackIp;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId())
            .append("noticeTitle", getNoticeTitle())
            .append("noticeType", getNoticeType())
            .append("noticeContent", getNoticeContent())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("payDocUrl", getPayDocUrl())
            .append("payGateway", getPayDocUrl())
            .append("callBackIp", getPayDocUrl())
            .append("productInfo", getPayDocUrl())
            .toString();
    }
}
