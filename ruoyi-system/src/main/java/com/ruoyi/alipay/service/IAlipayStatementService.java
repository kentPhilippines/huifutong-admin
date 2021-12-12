package com.ruoyi.alipay.service;

import com.ruoyi.alipay.domain.AlipayStatement;
import java.util.List;

/**
 * 对账Service接口
 * 
 * @author ruoyi
 * @date 2021-12-11
 */
public interface IAlipayStatementService 
{
    public List<AlipayStatement> selectTotalData();
    /**
     * 查询对账
     * 
     * @param id 对账ID
     * @return 对账
     */
    public AlipayStatement selectAlipayStatementById(Long id);

    /**
     * 查询对账列表
     * 
     * @param alipayStatement 对账
     * @return 对账集合
     */
    public List<AlipayStatement> selectAlipayStatementList(AlipayStatement alipayStatement);

    /**
     * 新增对账
     * 
     * @param alipayStatement 对账
     * @return 结果
     */
    public int insertAlipayStatement(AlipayStatement alipayStatement);

    /**
     * 修改对账
     * 
     * @param alipayStatement 对账
     * @return 结果
     */
    public int updateAlipayStatement(AlipayStatement alipayStatement);

    /**
     * 批量删除对账
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAlipayStatementByIds(String ids);

    /**
     * 删除对账信息
     * 
     * @param id 对账ID
     * @return 结果
     */
    public int deleteAlipayStatementById(Long id);
}
