package com.ruoyi.alipay.service.impl;


import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.alipay.domain.BankTransactionRecord;
import com.ruoyi.alipay.domain.util.BankRunInfo;
import com.ruoyi.alipay.mapper.BankInfoSplitEntityMapper;
import com.ruoyi.alipay.service.IBankInfoSplitResultService;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收款媒介列Service业务层处理
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Service
public class BankInfoSplitEntityServiceImpl implements IBankInfoSplitResultService {
    @Resource
    private BankInfoSplitEntityMapper bankInfoSplitEntityMapper;


    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<BankInfoSplitEntity> selectBankInfoSplitResult(BankInfoSplitEntity bankInfoSplitEntity) {
        return this.bankInfoSplitEntityMapper.selectBankInfoSplitEntity(bankInfoSplitEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<BankTransactionRecord> selectBankTransactionRecord(BankInfoSplitEntity bankInfoSplitEntity) {
        return this.bankInfoSplitEntityMapper.selectBankTransactionRecord(bankInfoSplitEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<BankRunInfo> findBankRunInfo(BankRunInfo bankSplitEntity) {
        return bankInfoSplitEntityMapper.findBankRunInfo(bankSplitEntity);
    }
}
