package com.ruoyi.alipay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.AlipayChanelFee;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.domain.AlipayUserRateEntity;
import com.ruoyi.alipay.mapper.AlipayChanelFeeMapper;
import com.ruoyi.alipay.mapper.AlipayUserInfoMapper;
import com.ruoyi.alipay.mapper.AlipayUserRateEntityMapper;
import com.ruoyi.alipay.service.IAlipayUserRateEntityService;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户产品费率Service业务层处理
 *
 * @author kent
 * @date 2020-03-18
 */
@Service
public class AlipayUserRateEntityServiceImpl implements IAlipayUserRateEntityService {
    @Resource
    private AlipayUserRateEntityMapper alipayUserRateEntityMapper;
    @Resource
    private AlipayUserInfoMapper alipayUserInfoMapper;
    @Resource
    private AlipayChanelFeeMapper alipayChanelFeeDao;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Value("${otc.appName}")
    private String appName;


    private static final String prefixKey = ":CardDealerMapToMerchant:";
    /**
     * 查询用户产品费率
     *
     * @param id 用户产品费率ID
     * @return 用户产品费率
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayUserRateEntity selectAlipayUserRateEntityById(Long id) {
        return alipayUserRateEntityMapper.selectAlipayUserRateEntityById(id);
    }

    /**
     * 查询用户产品费率列表
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return 用户产品费率
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> selectAlipayUserRateEntityList(AlipayUserRateEntity alipayUserRateEntity) {
        return alipayUserRateEntityMapper.selectAlipayUserRateEntityList(alipayUserRateEntity);
    }

    /**
     * 新增用户产品费率
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayUserRateEntity(AlipayUserRateEntity alipayUserRateEntity) {
        alipayUserRateEntity.setCreateTime(DateUtils.getNowDate());
        alipayUserRateEntity.setUserType(1);
        if (alipayUserRateEntity.getFeeType() == 2) { //判断是否为代付
            alipayUserRateEntity.setSwitchs(0);
        }
        AlipayUserInfo result = alipayUserInfoMapper.checkAlipayUserIdUnique(alipayUserRateEntity.getUserId());
        if (result == null) {
            throw new BusinessException("商户用户不存在");
        }
        if (result.getSwitchs() == 2) {
            throw new BusinessException("此商户已被停用");
        }
        if (StringUtils.isNotEmpty(result.getAgent())) {
            //查询上级同类型费率
            AlipayUserRateEntity agentRate = alipayUserRateEntityMapper.findRateByUserIdAndType(result.getAgent(), alipayUserRateEntity.getFeeType(),alipayUserRateEntity.getChannelId(), alipayUserRateEntity.getPayTypr());
            if (agentRate == null) { //是否为空
                throw new BusinessException("此商户的上级代理未设置同类型的费率或此费率通道已关闭");
            }
            Double fee = agentRate.getFee();
            Double fee1 = alipayUserRateEntity.getFee();
            if (fee > fee1) {
                throw new BusinessException("下级通道费率不能大于上级的通道费率：" + agentRate.getFee());
            }
        }
        return alipayUserRateEntityMapper.insertAlipayUserRateEntity(alipayUserRateEntity);
    }

    /**
     * 修改用户产品费率
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.ALIPAY_SLAVE)
    public int updateAlipayUserRateEntity(AlipayUserRateEntity alipayUserRateEntity) {

        //redisTemplate.opsForValue().set(appName +prefixKey , JSONUtil.toJsonStr(cardDealerMapToMerchant()));
        int i = alipayUserRateEntityMapper.updateAlipayUserRateEntity(alipayUserRateEntity);

        return i;
    }
    @Override
    @DataSource(DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> getAndRefreshAlipayMerchantRateCache(String feeType)
    {
        String key = this.getClass().getName()+":getAndRefreshAlipayMerchantRateCache:"+feeType;
        /*String cacheString = redisTemplate.opsForValue().get(key);
        if( StringUtils.isNotEmpty(cacheString) )
        {
            return JSONUtil.parseArray(cacheString).toList(AlipayUserRateEntity.class);
        }*/
        List<AlipayUserRateEntity> list = alipayUserRateEntityMapper.findAllMerchantRateByFeeType(feeType);
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(list));
        return list;
    }

/*    *//**
     * 分为
     * 存款 -  顶代卡商/商户对应关系
     * 取款 -  顶代卡商/商户对应关系
     * @return
     *//*
    private Map<String,Map<String,List<String>>> cardDealerMapToMerchant()
    {
        Map<String,Map<String,List<String>>> feeTypeToRelation = Maps.newHashMap();
        Map<String,List<String>> cardDealerMapToMerchant = Maps.newHashMap();





        return feeTypeToRelation;
    }*/

    /**
     * 删除用户产品费率对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayUserRateEntityByIds(String ids) {
        AlipayUserRateEntity alipayUserRateEntity = alipayUserRateEntityMapper.selectAlipayUserRateEntityById(Long.valueOf(ids));
        if (alipayUserRateEntity.getSwitchs() == 1) {
            throw new BusinessException("开启状态下，不允许删除操作");
        }
        return alipayUserRateEntityMapper.deleteAlipayUserRateEntityByIds(Convert.toStrArray(ids));
    }

    //下面是码商的费率的逻辑处理
    /**
     * 码商的费率
     *
     * @param alipayUserRateEntity 用户产品费率
     * @return
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> selectUserRateEntityList_qr(AlipayUserRateEntity alipayUserRateEntity) {
        return alipayUserRateEntityMapper.selectUserRateEntityList_qr(alipayUserRateEntity);
    }


    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayUserRateEntity_qr(AlipayUserRateEntity alipayUserRateEntity) {
        alipayUserRateEntity.setCreateTime(DateUtils.getNowDate());
        alipayUserRateEntity.setUserType(2);
        if (alipayUserRateEntity.getFeeType() == 2) { //判断是否为代付
            alipayUserRateEntity.setSwitchs(0);
        }
        AlipayUserInfo result = alipayUserInfoMapper.checkAlipayUserIdUnique(alipayUserRateEntity.getUserId());
        if (result == null) {
            throw new BusinessException("码商用户不存在");
        }
        if (result.getSwitchs() == 2) {
            throw new BusinessException("此码商已被停用");
        }
        if (StringUtils.isNotEmpty(result.getAgent())) {
            //查询上级同类型费率
            AlipayUserRateEntity agentRate = alipayUserRateEntityMapper.findRateByUserIdAndType(result.getAgent(), alipayUserRateEntity.getFeeType(), alipayUserRateEntity.getChannelId(),alipayUserRateEntity.getPayTypr() );
            if (agentRate == null) { //是否为空
                throw new BusinessException("此码商的上级代理未设置同类型的费率或此费率通道已关闭");
            }
            if (agentRate.getFee().compareTo(alipayUserRateEntity.getFee()) < 0) {
                throw new BusinessException("下级通道费率不能大于上级的通道费率：" + agentRate.getFee());
            }
        }
        return alipayUserRateEntityMapper.insertAlipayUserRateEntity_qr(alipayUserRateEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int changeStatusOfDecimal(String id, String userId, String feeType, Integer deci) {

        AlipayUserInfo result = alipayUserInfoMapper.checkAlipayUserIdUnique(userId);
        if (result == null) {
            throw new BusinessException("用户不存在");
        }
        if (result.getSwitchs() == 2) {
            throw new BusinessException("此用户已被停用");
        }


        return alipayUserRateEntityMapper.updateStatusOfDecimal(id, deci);
    }
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int changeStatus(String id, String userId, String feeType, String switchs) {
        /*if ("2".equals(feeType) && "1".equals(switchs)) {
            List<AlipayUserRateEntity> list = alipayUserRateEntityMapper.selectListObjectEntityByUserId(userId, feeType);
            if (list.size() > 0) {
                throw new BusinessException("操作失败，用户不能同时开启两种代付费率");
            }
        }*/
        AlipayUserInfo result = alipayUserInfoMapper.checkAlipayUserIdUnique(userId);
        if (result == null) {
            throw new BusinessException("用户不存在");
        }
        if (result.getSwitchs() == 2) {
            throw new BusinessException("此用户已被停用");
        }
        if (switchs.equals("1")) {
            List<AlipayUserRateEntity> rate = alipayUserRateEntityMapper.clickPriorityOpen(id);
            if (CollUtil.isNotEmpty(rate) && rate.size() > 0) {
                throw new BusinessException("相同产品下，不允许开启优先级相同的费率，请调整费率后在重新开启");
            }
        }
        /*如果是开启  则关闭该产品其他渠道
        如果是关闭 则啥都不管
        /
         */
        /*if(switchs.equals("1")){
            //关闭该商户下  该产品所有的通道
          int a =   alipayUserRateEntityMapper.updateProduct(id,userId);
        }*/
        return alipayUserRateEntityMapper.updateStatus(id, switchs);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayUserRateEntity checkUniqueRate(AlipayUserRateEntity alipayUserRateEntity) {
        return alipayUserRateEntityMapper.checkUniqueRate(alipayUserRateEntity);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> findAgentRateLiat(String merchantId, AlipayUserRateEntity rate) {
        return alipayUserRateEntityMapper.findAgentRateLiat(merchantId, rate);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public Boolean clickFee(AlipayUserRateEntity alipayUserRateEntity) {
        if (StringUtils.isEmpty(alipayUserRateEntity.getChannelId()) || StringUtils.isEmpty(alipayUserRateEntity.getPayTypr()))
            throw new BusinessException("数据错误，联系开发人员处理");
        AlipayChanelFee channelBy = alipayChanelFeeDao.findChannelBy(alipayUserRateEntity.getChannelId(), alipayUserRateEntity.getPayTypr());
        if (ObjectUtil.isNull(channelBy)) {
            throw new BusinessException("当前修改完费率，渠道费率未配置，请联系技术配置渠道费率");
        }
        return true;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public Boolean isAgentFee(AlipayUserRateEntity alipayUserRateEntity) {
        AlipayUserInfo userInfo = alipayUserInfoMapper.selectMerhantInfoByUserId(alipayUserRateEntity.getUserId());//查询当前商户交易账户
        AlipayUserRateEntity agentFee = findAgentFee(userInfo, alipayUserRateEntity);
        if (ObjectUtil.isNull(agentFee)) {
            throw new BusinessException("当前上级代理费率未配置");
        }
        return true;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> findRates(String ids) {

        String[] split = ids.split(",");


        return alipayUserRateEntityMapper.findRates(split);
    }
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayUserRateEntity findWitRate(String userId) {

        //  "select * from alipay_user_rate where feeType = 2 and `switchs` = 1 and userId = #{userId} "
        return alipayUserRateEntityMapper.findWitRate(userId);
    }
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> findRates(String ids,String payType) {

        String[] split = ids.split(",");


        return alipayUserRateEntityMapper.findRatesAndPayType(split,payType);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayUserRateEntity findRateByType(String userId, String rechange) {
        return alipayUserRateEntityMapper.findRateByType(userId, rechange);
    }



    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> findMerchantWithdralRate(String userId) {

        return alipayUserRateEntityMapper.findMerchantWithdralRate(userId);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayUserRateEntity> findMerchantChargeRate(String userId) {
        return alipayUserRateEntityMapper.findMerchantChargeRate(userId);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayUserRateEntity findUserByChannel(String userId, String product, String channelId) {
        List<AlipayUserRateEntity> feeByProduct = alipayUserRateEntityMapper.findFeeByProduct(userId, product);
        return CollUtil.getFirst(feeByProduct);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayUserRateEntity findBankcardRate(String orderQrUser, String product, int i) {
        return alipayUserRateEntityMapper.findbankRate(orderQrUser, product, i);


    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public void delectUser(String userId) {
        alipayUserRateEntityMapper.delectUser(userId);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public void delectChannel(String userId) {
        alipayUserRateEntityMapper.delectChannel(userId);
    }


    /**
     * 无限级代理递归检测费率
     *
     * @param userInfo
     * @param alipayUserRateEntity
     * @return
     */
    AlipayUserRateEntity findAgentFee(AlipayUserInfo userInfo, AlipayUserRateEntity alipayUserRateEntity) {
        if (StrUtil.isNotBlank(userInfo.getAgent())) {//当前商户是否存在代理商，如果存在验证是否存在修改完代理商费率为配置的情况
            AlipayUserInfo userInfo1 = alipayUserInfoMapper.selectMerhantInfoByUserId(userInfo.getAgent());
            AlipayUserRateEntity agentRateEntity = alipayUserRateEntityMapper.findFee(userInfo1.getUserId(), alipayUserRateEntity.getChannelId(), alipayUserRateEntity.getPayTypr());
            if (ObjectUtil.isNull(agentRateEntity)) {
                return null;
            } else {
                if (StrUtil.isNotBlank(userInfo1.getAgent())) {
                    return agentRateEntity;
                } else {
                    return findAgentFee(userInfo1, alipayUserRateEntity);
                }
            }

        } else {
            return alipayUserRateEntity;
        }
    }


}
