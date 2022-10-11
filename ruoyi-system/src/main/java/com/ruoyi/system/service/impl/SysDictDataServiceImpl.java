package com.ruoyi.system.service.impl;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.service.ISysDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class SysDictDataServiceImpl implements ISysDictDataService {
    private static final String prefixKey = "MERCHANT_BLACK_RULE";
    @Value("${otc.appName}")
    private String appName;
    @Resource
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        return dictDataMapper.selectDictDataByType(dictType);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 通过字典ID删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    @Override
    public int deleteDictDataById(Long dictCode) {

        refreshAllCacheData();
        return dictDataMapper.deleteDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据
     *
     * @param ids 需要删除的数据
     * @return 结果
     */
    @Override
    public int deleteDictDataByIds(String ids) {

        refreshAllCacheData();
        return dictDataMapper.deleteDictDataByIds(Convert.toStrArray(ids));
    }

    /**
     * 新增保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData dictData) {
        refreshAllCacheData();
        return dictDataMapper.insertDictData(dictData);
    }

    private void refreshAllCacheData()
    {
        List<SysDictData> dicts = dictDataMapper.selectDictDataByType("MERCHANT_BLACK_RULE");
        Map<String, Set<String>> keyAndValues = dicts.stream().collect(Collectors.groupingBy(SysDictData::getDictLabel,Collectors.mapping(SysDictData::getDictValue,Collectors.toSet())));

        redisTemplate.opsForValue().set(prefixKey, JSONUtil.toJsonStr(keyAndValues));
        //alipayMessageRegCache.put(appName, alipayMessageRegMapper.selectAll());
        log.info("{} load {} rows AlipayMessageReg success!", prefixKey  , keyAndValues.values().size());
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData dictData) {
        refreshAllCacheData();

        return dictDataMapper.updateDictData(dictData);
    }

    /**
     * 修改保存字典数据信息
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @return 结果
     */
    public List<String> selectDictValueByKye(String dictType, String dictLabel) {
        List<String> value = dictDataMapper.findDictValueByKye(dictType, dictLabel);
        return value;
    }
}
