package com.ruoyi.alipay.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.ruoyi.alipay.domain.AlipayMediumEntity;
import com.ruoyi.alipay.domain.util.DesUtil;
import com.ruoyi.alipay.mapper.AlipayMediumEntityMapper;
import com.ruoyi.alipay.mapper.AlipayUserInfoMapper;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.ImportBankVerifyDto;
import com.ruoyi.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

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
    public List<AlipayMediumEntity> findIds(List<String> ids) {
        return alipayMediumEntityMapper.findIds(ids);
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

        return alipayMediumEntityMapper.deleteAlipayMediumEntityById(alipayMediumEntity.getId());
        /*if (sysUser.getLoginName().equalsIgnoreCase("admin")) {
            return alipayMediumEntityMapper.deleteAlipayMediumEntityById(alipayMediumEntity.getId());
        }

        alipayMediumEntity.setIsDeal("1");//1 is deleted , 2 is normal
        return alipayMediumEntityMapper.updateAlipayMediumEntity(alipayMediumEntity);*/
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
    public AlipayMediumEntity findBankSumBak(String userId) {
        AlipayMediumEntity medium = new AlipayMediumEntity();
        List<AlipayMediumEntity> bankSum = alipayMediumEntityMapper.findBankSumBak(userId);
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

        StringBuilder successMsg = new StringBuilder("导入成功。");
        StringBuilder failMsg = new StringBuilder();

        dataList.stream().filter(data -> (data.getStatus() != 1 && data.getStatus() != 0)).forEach(data->failMsg.append(data.getBankName()+"-"+data.getStatus()+","));
        if(StringUtils.isNotEmpty(failMsg))
        {
            throw new BusinessException("导入状态异常："+failMsg.toString());
        }
        /*dataList.stream().filter(data -> (data.getStatus() != 1 && data.getStatus() != 0)).findAny().ifPresent(d -> {
            throw new RuntimeException(d.getBankName() + "状态输入异常:" + d.getStatus());
        });*/
        dataList.stream()
                .forEach(importBankVerifyDto -> {
                    AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
                    alipayMediumEntity.setIsClickPay(importBankVerifyDto.getStatus());
                    alipayMediumEntity.getParams().put("bankName", importBankVerifyDto.getBankName());
                    int i = updateAlipayMediumEntityByBankName(alipayMediumEntity);
                    if (i == 0) {
                        failMsg.append(importBankVerifyDto.getBankName()+",");
                        //throw new RuntimeException("此银行：" + importBankVerifyDto.getBankName() + "  不存在");
                    }
                    //return alipayMediumEntity;
                });
        if(StringUtils.isNotEmpty(failMsg))
        {
            throw new BusinessException(failMsg.toString()+"不存在，请确认数据");
        }


        return successMsg.toString();
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayMediumEntity findBankNo(String mediumNumber) {
      String bankNo =   DesUtil.encryptHex(mediumNumber);

      return   alipayMediumEntityMapper.findBankNo(mediumNumber,bankNo);

    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public void batchUpdateMacthMore(String ids, Integer status) {
        Arrays.stream(ids.split(",")).forEach(id->{
            alipayMediumEntityMapper.updateMacthMoreById(id,status);
        });
    }
}
