package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.BankInfoSplitEntity;

import java.util.List;

/**
 *
 */
public interface BankInfoSplitEntityMapper {


    /**
     * 新增截取银行流水信息数据
     *
     * @param bankInfoSplitEntity
     * @return 结果
     */
    List<BankInfoSplitEntity> selectBankInfoSplitEntity(BankInfoSplitEntity bankInfoSplitEntity);
}
