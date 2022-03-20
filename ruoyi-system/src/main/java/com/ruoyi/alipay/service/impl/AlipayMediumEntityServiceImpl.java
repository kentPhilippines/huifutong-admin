package com.ruoyi.alipay.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.mapper.AlipayUserInfoMapper;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.system.domain.ImportBankVerifyDto;
import com.ruoyi.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alipay.mapper.AlipayMediumEntityMapper;
import com.ruoyi.alipay.domain.AlipayMediumEntity;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.DataSourceType;

import javax.annotation.Resource;

/**
 * 收款媒介列Service业务层处理
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Service
@Slf4j
public class AlipayMediumEntityServiceImpl implements IAlipayMediumEntityService {
    @Resource
    private AlipayMediumEntityMapper alipayMediumEntityMapper;

    @Resource
    private AlipayUserInfoMapper alipayUserInfoMapper;

    @Cached(name = "IAlipayMediumEntityService.selectAll", expire = 3600, cacheType = CacheType.REMOTE)
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMediumEntity> selectAll() {
        return alipayMediumEntityMapper.findAll();
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMediumEntity> selectByIds(List<String> ids) {
        return alipayMediumEntityMapper.selectAlipayMediumEntityByIds(ids);
    }


    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMediumEntity> selectByMediumIds(List<String> ids) {
        return alipayMediumEntityMapper.selectByMediumIds(ids);
    }


    /**
     * 查询收款媒介列
     *
     * @param id 收款媒介列ID
     * @return 收款媒介列
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayMediumEntity selectAlipayMediumEntityById(Long id) {
        return alipayMediumEntityMapper.selectAlipayMediumEntityById(id);
    }

    /**
     * 查询收款媒介列列表
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 收款媒介列
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMediumEntity> selectAlipayMediumEntityList(AlipayMediumEntity alipayMediumEntity) {
        return alipayMediumEntityMapper.selectAlipayMediumEntityList(alipayMediumEntity);
    }

    /**
     * 新增收款媒介列
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayMediumEntity(AlipayMediumEntity alipayMediumEntity) {
        alipayMediumEntity.setCreateTime(DateUtils.getNowDate());
        return alipayMediumEntityMapper.insertAlipayMediumEntity(alipayMediumEntity);
    }

    /**
     * 修改收款媒介列
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Override
    public int
    updateAlipayMediumEntity(AlipayMediumEntity alipayMediumEntity) {
        try {
            //尝试修改持卡商户  没有权限就拒绝  没有卡商也失败
            /*if (StringUtils.isNotEmpty(alipayMediumEntity.getQrcodeId())) {
                if (SecurityUtils.getSubject().hasRole("admin")) {
                    Optional.ofNullable(alipayUserInfoMapper.selectCardDealerInfoByUserId(alipayMediumEntity.getQrcodeId())).orElseThrow(() -> new Exception("卡商不存在"));
                } else {
                    throw new Exception("没有权限修改所属卡商");
                }
            }*/
            return alipayMediumEntityMapper.updateAlipayMediumEntity(alipayMediumEntity);
        } catch (Exception e) {
            log.error("修改媒介异常", e);
        }
        return -1;
    }

    /**
     * 修改收款媒介列
     *
     * @param alipayMediumEntity 收款媒介列by bankan
     * @return 结果
     */
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Override
    public int
    updateAlipayMediumEntityByBankName(AlipayMediumEntity alipayMediumEntity) {
        return alipayMediumEntityMapper.updateAlipayMediumEntityByBankName(alipayMediumEntity);
    }

    /**
     * 根据code修改上限金额
     *
     * @param alipayMediumEntity 收款媒介列
     * @return 结果
     */
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    @Override
    public int updateAlipayMediumEntityByCode(AlipayMediumEntity alipayMediumEntity) {
        return alipayMediumEntityMapper.updateAlipayMediumEntityByCode(alipayMediumEntity);
    }

    /**
     * 删除收款媒介列对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayMediumEntityByIds(String ids) {
        return alipayMediumEntityMapper.deleteAlipayMediumEntityByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除收款媒介列信息  软删除 改isdeal状态
     *
     * @param id 收款媒介列ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayMediumEntityById(AlipayMediumEntity alipayMediumEntity, SysUser sysUser) {
       /* AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
        alipayMediumEntity.setId(id);*/
        if (sysUser.getLoginName().equalsIgnoreCase("admin")) {
            return alipayMediumEntityMapper.deleteAlipayMediumEntityById(alipayMediumEntity.getId());
        }

        alipayMediumEntity.setIsDeal("1");//1 is deleted , 2 is normal
        return alipayMediumEntityMapper.updateAlipayMediumEntity(alipayMediumEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayMediumEntity findUserId(String med) {
        return alipayMediumEntityMapper.findUserId(med);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayMediumEntity findBankSum(String userId) {
        AlipayMediumEntity medium = new AlipayMediumEntity();
        List<AlipayMediumEntity> bankSum = alipayMediumEntityMapper.findBankSum(userId);
        for (AlipayMediumEntity med : bankSum) {
            if ("open".equals(med.getAmounttype())) {
                medium.setOpenSumBankAmountnow(med.getMountNow());
                medium.setOpenSumBankAmountsys(med.getMountSystem());
            }
            if ("all".equals(med.getAmounttype())) {
                medium.setBankSumAmountnow(med.getMountNow());
                medium.setBankSumAmountsys(med.getMountSystem());
            }
        }
        return medium;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMediumEntity> findOpenMed() {
        return alipayMediumEntityMapper.findOpenMed();
    }


    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<String> findAllBankNames() {
        return alipayMediumEntityMapper.findAllBankNames();
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMediumEntity> findMedSum() {
        return alipayMediumEntityMapper.findMedSum();
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public String importData(List<ImportBankVerifyDto> dataList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(dataList) || dataList.size() == 0) {
            throw new BusinessException("导入数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        List<AlipayMediumEntity> alipayMediumEntities = dataList.stream().filter(data -> {
                    return (data.getStatus() == 1 || data.getStatus() == 0) && StringUtils.isNotEmpty(data.getBankName());
                }
        ).map(importBankVerifyDto -> {
            AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
            alipayMediumEntity.setIsClickPay(importBankVerifyDto.getStatus());
            alipayMediumEntity.getParams().put("bankName", importBankVerifyDto.getBankName());
            return alipayMediumEntity;
        }).collect(Collectors.toList());

        alipayMediumEntities.forEach(alipayMediumEntity -> updateAlipayMediumEntityByBankName(alipayMediumEntity));
        return successMsg.toString();
    }
}
