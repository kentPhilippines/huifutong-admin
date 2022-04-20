package com.ruoyi.system.service;

import com.ruoyi.system.domain.AlipayMessageReg;
import java.util.List;

/**
 * 短信正则模板Service接口
 * 
 * @author ruoyi
 * @date 2021-12-22
 */
public interface IAlipayMessageRegService 
{
    public void refreshAllCache();
    public List<AlipayMessageReg> selectAll();
    /**
     * 查询短信正则模板
     * 
     * @param id 短信正则模板ID
     * @return 短信正则模板
     */
    public AlipayMessageReg selectAlipayMessageRegById(Long id);

    /**
     * 查询短信正则模板列表
     * 
     * @param alipayMessageReg 短信正则模板
     * @return 短信正则模板集合
     */
    public List<AlipayMessageReg> selectAlipayMessageRegList(AlipayMessageReg alipayMessageReg);

    /**
     * 新增短信正则模板
     * 
     * @param alipayMessageReg 短信正则模板
     * @return 结果
     */
    public int insertAlipayMessageReg(AlipayMessageReg alipayMessageReg);

    /**
     * 修改短信正则模板
     * 
     * @param alipayMessageReg 短信正则模板
     * @return 结果
     */
    public int updateAlipayMessageReg(AlipayMessageReg alipayMessageReg);

    /**
     * 批量删除短信正则模板
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayMessageRegByIds(String ids);

    /**
     * 删除短信正则模板信息
     * 
     * @param id 短信正则模板ID
     * @return 结果
     */
    public int deleteAlipayMessageRegById(Long id);
}
