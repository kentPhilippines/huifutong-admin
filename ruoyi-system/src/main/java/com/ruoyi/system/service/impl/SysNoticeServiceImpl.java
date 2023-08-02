package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.AlipayMessageRegMapper;
import com.ruoyi.system.mapper.SysDictDataMapper;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.ISysNoticeService;

import javax.annotation.Resource;

/**
 * 公告 服务层实现
 *
 * @author ruoyi
 * @date 2018-06-25
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {
    @Autowired
    private SysNoticeMapper noticeMapper;

    @Resource
    private SysDictTypeMapper sysDictTypeMapper;

    @Resource
    private SysDictDataMapper sysDictDataMapper;

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        SysNotice sysNotice = noticeMapper.selectNoticeById(noticeId);
        List<SysDictData> sysDictDatas = sysDictDataMapper.selectDictDataByType("NOTICE89757");
        if (CollectionUtil.isNotEmpty(sysDictDatas)) {
            for (SysDictData sysDictData : sysDictDatas) {
                if (sysNotice.getNoticeId().equals(sysDictData.getDictSort())) {
                    sysNotice.setCallBackIp(sysDictData.getDictLabel());
                    sysNotice.setPayGateway(sysDictData.getDictValue());
                    sysNotice.setProductInfo(sysDictData.getRemark());
                    sysNotice.setPayDocUrl(sysDictData.getCssClass());
                    break;
                }
            }
        }
        return sysNotice;
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        List<SysNotice> sysNotices = noticeMapper.selectNoticeList(notice);
        List<SysDictData> notice89757 = sysDictDataMapper.selectDictDataByType("NOTICE89757");
        if (CollectionUtil.isNotEmpty(notice89757)){
            for (SysNotice sysNotice : sysNotices) {
                for (SysDictData sysDictData : notice89757) {
                    if (sysNotice.getNoticeId().equals(sysDictData.getDictSort())){
                        sysNotice.setCallBackIp(sysDictData.getDictLabel());
                        sysNotice.setPayGateway(sysDictData.getDictValue());
                        sysNotice.setProductInfo(sysDictData.getRemark());
                        sysNotice.setPayDocUrl(sysDictData.getCssClass());
                        break;
                    }
                }
            }
        }
        return sysNotices;
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {
        Long[] roles = notice.getRoleIds();
        String roleIds = StringUtils.join(roles, ",");
        notice.setRemark(roleIds);
        int i = noticeMapper.insertNotice(notice);
        Long id = noticeMapper.selectKeyId();
        SysDictType notice89757 = sysDictTypeMapper.selectDictTypeByType("NOTICE89757");
        if (null == notice89757) {
            SysDictType sysDictType = new SysDictType();
            sysDictType.setDictName("公告信息89757");
            sysDictType.setStatus("0");
            sysDictType.setDictType("NOTICE89757");
            sysDictTypeMapper.insertDictType(sysDictType);
        }
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("NOTICE89757");
        sysDictData.setCssClass(notice.getPayDocUrl());
        sysDictData.setDictLabel(notice.getCallBackIp());
        sysDictData.setDictValue(notice.getPayGateway());
        sysDictData.setRemark(notice.getProductInfo());
        sysDictData.setDictSort(id);
        sysDictDataMapper.insertDictData(sysDictData);
        return i;
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        Long[] roles = notice.getRoleIds();
        String roleIds = StringUtils.join(roles, ",");
        if (StringUtils.isEmpty(roleIds)) {
            notice.setRemark("1");
        } else {
            notice.setRemark(roleIds);
        }

        int i = noticeMapper.updateNotice(notice);
        SysDictType notice89757 = sysDictTypeMapper.selectDictTypeByType("NOTICE89757");
        if (null == notice89757) {
            return 0;
        }
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictType("NOTICE89757");
        sysDictData.setCssClass(notice.getPayDocUrl());
        sysDictData.setDictLabel(notice.getCallBackIp());
        sysDictData.setDictValue(notice.getPayGateway());
        sysDictData.setRemark(notice.getProductInfo());
        SysDictData sysDictData1 = sysDictDataMapper.selectDictDataBySort("NOTICE89757",notice.getNoticeId());
        if (null==sysDictData1){
            Long id = noticeMapper.selectKeyId();
            sysDictData.setDictSort(id);
            sysDictDataMapper.insertDictData(sysDictData);
        }else {
            sysDictData.setDictSort(notice.getNoticeId());
            sysDictDataMapper.updateDictDataBySort(sysDictData);
        }
        return i;
    }

    /**
     * 删除公告对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(String ids) {
        int i = noticeMapper.deleteNoticeByIds(Convert.toStrArray(ids));
        sysDictDataMapper.deleteDictDataBySortIds(Convert.toStrArray(ids));
        return i;
    }

    @Override
    public List<SysNotice> selectNoticeListByRoleId(Long roleId) {
        return noticeMapper.selectNoticeListByRoleIdMapper(roleId);
    }

    @Override
    public List<SysNotice> selectNoticeListMoreByRoleId(SysNotice sysNotice) {
        return noticeMapper.selectNoticeMoreMapper(sysNotice);
    }
}
