package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AlipayMessageRegMapper;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.service.IAlipayMessageRegService;
import com.ruoyi.common.core.text.Convert;

/**
 * 短信正则模板Service业务层处理
 *
 * @author ruoyi
 * @date 2021-12-22
 */
@Service
@Slf4j
public class AlipayMessageRegServiceImpl implements IAlipayMessageRegService {
    @Autowired
    private AlipayMessageRegMapper alipayMessageRegMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
   /* @CreateCache(name="allRegs_",cacheType = CacheType.REMOTE)
    private Cache<String, List<AlipayMessageReg>> alipayMessageRegCache;*/

    private static final String prefixKey = "allRegs_";
    @Value("${otc.appName}")
    private String appName;

    /**
     * 查询短信正则模板
     *
     * @param id 短信正则模板ID
     * @return 短信正则模板
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public AlipayMessageReg selectAlipayMessageRegById(Long id) {
        return alipayMessageRegMapper.selectAlipayMessageRegById(id);
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public void refreshAllCache() {
        redisTemplate.opsForValue().set(prefixKey + appName, JSONUtil.toJsonStr(alipayMessageRegMapper.selectAll()));
        //alipayMessageRegCache.put(appName, alipayMessageRegMapper.selectAll());
        log.info("{} load {} rows AlipayMessageReg success!", prefixKey + appName, JSONUtil.parseArray(redisTemplate.opsForValue().get(prefixKey + appName)).size());
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMessageReg> selectAll() {
        return alipayMessageRegMapper.selectAll();
    }

    /**
     * 查询短信正则模板列表
     *
     * @param alipayMessageReg 短信正则模板
     * @return 短信正则模板
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public List<AlipayMessageReg> selectAlipayMessageRegList(AlipayMessageReg alipayMessageReg) {
        return alipayMessageRegMapper.selectAlipayMessageRegList(alipayMessageReg);
    }

    /**
     * 新增短信正则模板
     *
     * @param alipayMessageReg 短信正则模板
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int insertAlipayMessageReg(AlipayMessageReg alipayMessageReg) {
        int i = alipayMessageRegMapper.insertAlipayMessageReg(alipayMessageReg);
        refreshAllCache();
        return i;
    }

    /**
     * 修改短信正则模板
     *
     * @param alipayMessageReg 短信正则模板
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int updateAlipayMessageReg(AlipayMessageReg alipayMessageReg) {
        int i = alipayMessageRegMapper.updateAlipayMessageReg(alipayMessageReg);
        refreshAllCache();
        return i;
    }

    /**
     * 删除短信正则模板对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayMessageRegByIds(String ids) {
        int i = alipayMessageRegMapper.deleteAlipayMessageRegByIds(Convert.toStrArray(ids));
        refreshAllCache();
        return i;
    }

    /**
     * 删除短信正则模板信息
     *
     * @param id 短信正则模板ID
     * @return 结果
     */
    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public int deleteAlipayMessageRegById(Long id) {
        int i = alipayMessageRegMapper.deleteAlipayMessageRegById(id);
        refreshAllCache();
        return i;
    }

    @Override
    @DataSource(value = DataSourceType.ALIPAY_SLAVE)
    public BankInfoSplitEntity validate(AlipayMessageReg alipayMessageReg) {
        if (!alipayMessageReg.getTemplateFlag().equals("1")){
            return null ;
        }
        String pattern = alipayMessageReg.getRegex();
        String content = alipayMessageReg.getSourceMsg();
        /**
         * 银行卡，自己卡尾号，对方户名，对方卡尾号，交易时间,转账金额,余额
         */
        String extractStr = ReUtil.extractMulti(pattern, content, alipayMessageReg.getTemplate());
        boolean isMatch = ReUtil.isMatch(pattern, content);
        if (!isMatch) {
            throw new BusinessException("模板与内容不匹配，请检查数据。");
        }
        return BankInfoSplitEntity.of(extractStr);
    }
}
