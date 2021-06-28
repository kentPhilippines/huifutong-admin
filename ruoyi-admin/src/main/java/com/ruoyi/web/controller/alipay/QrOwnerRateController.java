package com.ruoyi.web.controller.alipay;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.alipay.domain.AlipayProductEntity;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.domain.AlipayUserRateEntity;
import com.ruoyi.alipay.service.IAlipayProductService;
import com.ruoyi.alipay.service.IAlipayUserFundEntityService;
import com.ruoyi.alipay.service.IAlipayUserInfoService;
import com.ruoyi.alipay.service.IAlipayUserRateEntityService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户产品费率Controller
 *
 * @author kiwi
 * @date 2020-03-18
 */
@Controller
@RequestMapping("/alipay/qr/rate")
public class QrOwnerRateController extends BaseController {
    private String prefix = "alipay/qrOwner/rate";

    @Autowired
    private IAlipayUserRateEntityService alipayUserRateEntityService;

    @Autowired
    private IAlipayUserInfoService alipayUserInfoService;
    @Autowired
    IAlipayProductService iAlipayProductService;

    @GetMapping()
    public String rate(ModelMap modelMap) {
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        modelMap.put("productList", list);
        return prefix + "/rate";
    }
    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;
    /**
     * 查询用户产品费率列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AlipayUserRateEntity alipayUserRateEntity) {

        List<AlipayUserInfo> userList = new ArrayList<>();
        List<String> userNameList = new ArrayList<>();
        if (StrUtil.isNotEmpty(alipayUserRateEntity.getAgentUserId())) {
            userList = alipayUserFundEntityService.findUserByAgent(alipayUserRateEntity.getAgentUserId());
        }
        if (userList.size() > 0) {
            for (AlipayUserInfo user : userList) {
                userNameList.add(user.getUserId());
            }
            alipayUserRateEntity.setAgentList(userNameList);
        }
        startPage();
        List<AlipayUserRateEntity> list = alipayUserRateEntityService.selectUserRateEntityList_qr(alipayUserRateEntity);
        return getDataTable(list);
    }

    /**
     * 新增用户产品费率
     */
    @GetMapping("/add")
    public String add(ModelMap modelMap) {
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        List<AlipayUserInfo> userinfo =  alipayUserInfoService.findUserUserAllToBankNot();
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        modelMap.put("productList", list);
        modelMap.put("userinfo", userinfo);
        return prefix + "/add";
    }

    /**
     * 新增保存用户产品费率
     */
    @Log(title = "码商费率", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AlipayUserRateEntity alipayUserRateEntity) {
        return toAjax(alipayUserRateEntityService.insertAlipayUserRateEntity_qr(alipayUserRateEntity));
    }

    /**
     * 修改用户产品费率
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayUserRateEntity alipayUserRateEntity = alipayUserRateEntityService.selectAlipayUserRateEntityById(id);
        AlipayProductEntity alipayProductEntity = new AlipayProductEntity();
        alipayProductEntity.setStatus(1);
        //查询产品类型下拉菜单
        List<AlipayProductEntity> list = iAlipayProductService.selectAlipayProductList(alipayProductEntity);
        mmap.put("alipayUserRateEntity", alipayUserRateEntity);
        mmap.put("productList", list);
        return prefix + "/edit";
    }

    /**
     * 修改保存用户产品费率
     */
    @Log(title = "码商费率", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AlipayUserRateEntity alipayUserRateEntity) {
        return toAjax(alipayUserRateEntityService.updateAlipayUserRateEntity(alipayUserRateEntity));
    }

    /**
     * 删除用户产品费率
     */
    @Log(title = "码商费率", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(alipayUserRateEntityService.deleteAlipayUserRateEntityByIds(ids));
    }

    /**
     * 码商费率状态更新
     */
    @Log(title = "码商费率", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult updateStatus(String id, String userId, String feeType, String switchs) {
        return toAjax(alipayUserRateEntityService.changeStatus(id, userId, feeType, switchs));
    }


}
