package com.ruoyi.alipay.service;

import com.ruoyi.alipay.domain.AlipayUserRateEntity;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;

import java.util.List;

/**
 * 用户产品费率Service接口
 *
 * @author kiwi
 * @date 2020-03-18
 */
public interface IAlipayUserRateEntityService {
    /**
     * 查询用户产品费率
     *
     * @param id 用户产品费率ID
     * @return 用户产品费率
     */
    AlipayUserRateEntity selectAlipayUserRateEntityById(Long id);

    /**
     * 查询用户产品费率列表
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return 用户产品费率集合
     */
    List<AlipayUserRateEntity> selectAlipayUserRateEntityList(AlipayUserRateEntity alipayUserRateEntity);

    /**
     * 新增用户产品费率
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return 结果
     */
    int insertAlipayUserRateEntity(AlipayUserRateEntity alipayUserRateEntity);

    /**
     * 修改用户产品费率
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return 结果
     */
    int updateAlipayUserRateEntity(AlipayUserRateEntity alipayUserRateEntity);


    @DataSource(DataSourceType.ALIPAY_SLAVE)
    List<AlipayUserRateEntity> getAndRefreshAlipayMerchantRateCache(String feeType);

    /**
     * 批量删除用户产品费率
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteAlipayUserRateEntityByIds(String ids);

    /**
     * 查询码商的费率
     *
     * @param alipayUserRateEntity
     * @return
     */
    List<AlipayUserRateEntity> selectUserRateEntityList_qr(AlipayUserRateEntity alipayUserRateEntity);

    /**
     * @param alipayUserRateEntity
     * @return
     */
    int insertAlipayUserRateEntity_qr(AlipayUserRateEntity alipayUserRateEntity);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    int changeStatusOfDecimal(String id, String userId, String feeType, Integer deci);

    /**
     * 费率开关
     *
     * @param id
     * @param userId
     * @param feeType
     * @param switchs
     * @return
     */
    int changeStatus(String id, String userId, String feeType, String switchs);

    AlipayUserRateEntity checkUniqueRate(AlipayUserRateEntity alipayUserRateEntity);

    /**
     * <p>查询自己代理用的的费率情况</p>
     *
     * @param merchantId
     * @param rate
     * @return
     */
    List<AlipayUserRateEntity> findAgentRateLiat(String merchantId, AlipayUserRateEntity rate);

    /**
     * 检查当前费率是否有重复配置，和  当前费率是否有配置渠道费率
     *
     * @param alipayUserRateEntity
     * @return
     */
    Boolean clickFee(AlipayUserRateEntity alipayUserRateEntity);

    Boolean isAgentFee(AlipayUserRateEntity alipayUserRateEntity);

    List<AlipayUserRateEntity> findRates(String ids);

     List<AlipayUserRateEntity> findRates(String ids,String payType);
    /**
     * 根据商户账号和产品类型查询当前开启的费率
     *
     * @param userId   商户账号
     * @param rechange 产品类型
     * @return
     */
    AlipayUserRateEntity findRateByType(String userId, String rechange);


    /**
     * 查询当前卡商唯一开启的   支出费率
     *
     * @param userId
     * @return
     */
    AlipayUserRateEntity findWitRate(String userId);



    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayUserRateEntity> findMerchantWithdralRate(String userId);

    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    List<AlipayUserRateEntity> findMerchantChargeRate(String userId);

    AlipayUserRateEntity findUserByChannel(String orderAccount, String product, String orderQrUser);



    AlipayUserRateEntity findBankcardRate(String orderQrUser, String product, int i);

    void delectUser(String userId);

    void delectChannel(String userId);
}
