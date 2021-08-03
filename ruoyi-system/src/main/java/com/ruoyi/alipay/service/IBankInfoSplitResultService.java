package com.ruoyi.alipay.service;


import com.ruoyi.alipay.domain.BankInfoSplitEntity;

import java.util.List;

/**
 * 收款媒介列Service接口
 *
 * @author kiwi
 * @date 2020-03-17
 */
public interface IBankInfoSplitResultService {

    /**
     * 新增银行信息截取数据
     *
     * @param bankInfoSplitEntity
     * @return 结果
     */
    public List<BankInfoSplitEntity> selectBankInfoSplitResult(BankInfoSplitEntity bankInfoSplitEntity);


}
