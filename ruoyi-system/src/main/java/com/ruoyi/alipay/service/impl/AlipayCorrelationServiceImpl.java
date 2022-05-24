package com.ruoyi.alipay.service.impl;

import java.util.List;

import com.ruoyi.alipay.vo.UserCountBean;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.alipay.mapper.AlipayCorrelationMapper;
import com.ruoyi.alipay.domain.AlipayCorrelation;
import com.ruoyi.alipay.service.IAlipayCorrelationService;
import com.ruoyi.common.core.text.Convert;

import javax.validation.constraints.NotNull;

/**
 * 代理关系表Service业务层处理
 * 
 * @author ruoyi
 * @date 2020-03-17
 */
@Service
public class AlipayCorrelationServiceImpl implements IAlipayCorrelationService {
	@Autowired
	private AlipayCorrelationMapper alipayCorrelationMapper;
	/**
	 * 查询代理关系表
	 * @param id 代理关系表ID
	 * @return 代理关系表
	 */
	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public AlipayCorrelation selectAlipayCorrelationById(Long id) {
		return alipayCorrelationMapper.selectAlipayCorrelationById(id);
	}

	/**
	 * 查询代理关系表列表
	 * @param alipayCorrelation 代理关系表
	 * @return 代理关系表
	 */
	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public List<AlipayCorrelation> selectAlipayCorrelationList(AlipayCorrelation alipayCorrelation) {
		return alipayCorrelationMapper.selectAlipayCorrelationList(alipayCorrelation);
	}

	/**
	 * 新增代理关系表
	 * @param alipayCorrelation 代理关系表
	 * @return 结果
	 */
	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public int insertAlipayCorrelation(AlipayCorrelation alipayCorrelation) {
		alipayCorrelation.setCreateTime(DateUtils.getNowDate());
		return alipayCorrelationMapper.insertAlipayCorrelation(alipayCorrelation);
	}

	/**
	 * 修改代理关系表
	 * 
	 * @param alipayCorrelation 代理关系表
	 * @return 结果
	 */
	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public int updateAlipayCorrelation(AlipayCorrelation alipayCorrelation) {
		return alipayCorrelationMapper.updateAlipayCorrelation(alipayCorrelation);
	}

	/**
	 * 删除代理关系表对象
	 * @param ids 需要删除的数据ID
	 * @return 结果
	 */
	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public int deleteAlipayCorrelationByIds(String ids) {
		return alipayCorrelationMapper.deleteAlipayCorrelationByIds(Convert.toStrArray(ids));
	}

	/**
	 * 删除代理关系表信息
	 * 
	 * @param id 代理关系表ID
	 * @return 结果
	 */
	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public int deleteAlipayCorrelationById(Long id) {
		return alipayCorrelationMapper.deleteAlipayCorrelationById(id);
	}




	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public UserCountBean findMyDateAgen(String userId) {
		UserCountBean bean = alipayCorrelationMapper.findMyDateAgen(userId);
		return bean;
	}

	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public UserCountBean findDealDate(@NotNull String userId) {
		UserCountBean bean = alipayCorrelationMapper.findDealDate(userId);
		return bean;
	}

	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public int[][] findOnline(String userId) {
		int[][] a = new int[3][1];
		/*List<DataArray> dataArray = alipayCorrelationMapper.findOnline(userId);
		int count = alipayCorrelationMapper.findMyUserCount(userId);
		if (dataArray.size() != 0) {
			for (DataArray array : dataArray) {
				a[0][0] = array.getDataArray() + a[0][0];
			}
			a[1][0] = dataArray.size();
		} else {
			a[0][0] = 0;
			a[1][0] = 0;
		}
		a[2][0] = count;*/
		return a;
	}

	@Override
	@DataSource(value = DataSourceType.ALIPAY_SLAVE)
	public String findAgent(String qrcodeId) {
		return alipayCorrelationMapper.findAgent(qrcodeId);
	}
}
