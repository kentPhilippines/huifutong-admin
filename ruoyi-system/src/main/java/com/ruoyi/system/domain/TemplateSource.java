package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 来源匹配对象 template_source
 * 
 * @author ruoyi
 * @date 2022-03-08
 */
public class TemplateSource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据id(主键索引) */
    private Long id;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String bankName;

    /** 来源号码 */
    @Excel(name = "来源号码")
    private String sourcePhone;

    /** 1代表开，2代表关 */
    @Excel(name = "1代表开，2代表关")
    private String status;

    /** 备注1 */
    @Excel(name = "备注1")
    private String remark1;

    /** 备注2 */
    @Excel(name = "备注2")
    private String remark2;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setBankName(String bankName) 
    {
        this.bankName = bankName;
    }

    public String getBankName() 
    {
        return bankName;
    }
    public void setSourcePhone(String sourcePhone) 
    {
        this.sourcePhone = sourcePhone;
    }

    public String getSourcePhone() 
    {
        return sourcePhone;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setRemark1(String remark1) 
    {
        this.remark1 = remark1;
    }

    public String getRemark1() 
    {
        return remark1;
    }
    public void setRemark2(String remark2) 
    {
        this.remark2 = remark2;
    }

    public String getRemark2() 
    {
        return remark2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("bankName", getBankName())
            .append("sourcePhone", getSourcePhone())
            .append("status", getStatus())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .toString();
    }
}
