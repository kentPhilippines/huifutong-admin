package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 短信正则模板对象 alipay_message_reg
 * 
 * @author ruoyi
 * @date 2021-12-22
 */
public class AlipayMessageReg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 银行名称
 */
    @NotBlank(message="银行名称不能为空")
    @Excel(name = "银行名称")
    private String bankName;

    /** 银行短信来源号码，多个号码，用逗号隔开 */
    @Excel(name = "银行短信来源号码，多个号码，用逗号隔开")
    private String sourcePhone;

    /** 短信模板正则，每个（.*）表示需要获取的银行信息包含，银行卡，自己卡尾号，对方户名，对方卡尾号，交易时间,余额，转账金额，
 */
    @Excel(name = "短信模板正则，每个", readConverterExp = ".=*")
    private String regex;

    /** 短信模板正则,顺序固定$1_$2_$3_$4_$5_$6_$7_$8，模板固定8个 例如如果没有对方尾号，用pay占位符代替 */
    @Excel(name = "短信模板正则,顺序固定$1_$2_$3_$4_$5_$6_$7_$8，模板固定8个 例如如果没有对方尾号，用pay占位符代替")
    private String template;

    /** 具体的交易方式
 */
    @Excel(name = "具体的交易方式")
    private String transactionTypeDetail;

    /** 平台规定的交易方式，income(收入),expenditure(支出)，fundback（资金回退）
 */
    @Excel(name = "平台规定的交易方式，income(收入),expenditure(支出)，fundback", readConverterExp = "资=金回退")
    private String transactionType;

    /** 开关开启1，支持解析，开关关闭0，不支持解析
 */
    @Excel(name = "开关开启1，支持解析，开关关闭0，不支持解析")
    private String templateFlag;
    private String sourceMsg;


    public void setSourceMsg(String sourceMsg) {
        this.sourceMsg = sourceMsg;
    }

    public String getSourceMsg() {
        return sourceMsg;
    }

    /** 创建时间 */
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdDate;

    /** 备用1 */
    @Excel(name = "备用1")
    private String remark1;

    /** 备用2 */
    @Excel(name = "备用2")
    private String remark2;
    /**
     * 尾号切割符号,例如特殊尾号62***252580，需要截取保留后半部分
     */
    private String tailSplit;

    /** $column.columnComment */
    @Excel(name = "备用2", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;


    public void setTailSplit(String tailSplit) {
        this.tailSplit = tailSplit;
    }

    public String getTailSplit() {
        return tailSplit;
    }

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
    public void setRegex(String regex) 
    {
        this.regex = regex;
    }

    public String getRegex() 
    {
        return regex;
    }
    public void setTemplate(String template) 
    {
        this.template = template;
    }

    public String getTemplate() 
    {
        return template;
    }
    public void setTransactionTypeDetail(String transactionTypeDetail) 
    {
        this.transactionTypeDetail = transactionTypeDetail;
    }

    public String getTransactionTypeDetail() 
    {
        return transactionTypeDetail;
    }
    public void setTransactionType(String transactionType) 
    {
        this.transactionType = transactionType;
    }

    public String getTransactionType() 
    {
        return transactionType;
    }
    public void setTemplateFlag(String templateFlag) 
    {
        this.templateFlag = templateFlag;
    }

    public String getTemplateFlag() 
    {
        return templateFlag;
    }
    public void setCreatedDate(Date createdDate) 
    {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() 
    {
        return createdDate;
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
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("bankName", getBankName())
            .append("sourcePhone", getSourcePhone())
            .append("regex", getRegex())
            .append("template", getTemplate())
            .append("transactionTypeDetail", getTransactionTypeDetail())
            .append("transactionType", getTransactionType())
            .append("templateFlag", getTemplateFlag())
            .append("createdDate", getCreatedDate())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .append("updateDate", getUpdateDate())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
