package com.ruoyi.web.controller.alipay;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.TemplateInfoSplitEntity;
import com.ruoyi.system.service.impl.TemplateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AlipayMessageReg;
import com.ruoyi.system.service.IAlipayMessageRegService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 短信正则模板Controller
 *
 * @author ruoyi
 * @date 2021-12-22
 */
@Controller
@RequestMapping("/system/reg")
public class AlipayMessageRegController extends BaseController {
    private String prefix = "system/reg";

    @Autowired
    private IAlipayMessageRegService alipayMessageRegService;

    @RequiresPermissions("system:reg:view")
    @GetMapping()
    public String reg() {
        return prefix + "/reg";
    }

    /**
     * 查询短信正则模板列表
     */
    @RequiresPermissions("system:reg:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AlipayMessageReg alipayMessageReg) {
        startPage();
        List<AlipayMessageReg> list = alipayMessageRegService.selectAlipayMessageRegList(alipayMessageReg);
        return getDataTable(list);
    }

    /**
     * 导出短信正则模板列表
     */
    @RequiresPermissions("system:reg:export")
    @Log(title = "短信正则模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AlipayMessageReg alipayMessageReg) {
        List<AlipayMessageReg> list = alipayMessageRegService.selectAlipayMessageRegList(alipayMessageReg);
        ExcelUtil<AlipayMessageReg> util = new ExcelUtil<AlipayMessageReg>(AlipayMessageReg.class);
        return util.exportExcel(list, "reg");
    }

    /**
     * 新增短信正则模板
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存短信正则模板
     */
    @RequiresPermissions("system:reg:add")
    @Log(title = "短信正则模板", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AlipayMessageReg alipayMessageReg) {
        alipayMessageReg.setCreatedDate(new Date());
        alipayMessageReg.setUpdateBy(ShiroUtils.getSysUser().getLoginName());
        alipayMessageRegService.validate(alipayMessageReg);//校验不通过会丢异常
        return toAjax(alipayMessageRegService.insertAlipayMessageReg(alipayMessageReg));
    }

    @PostMapping("/validate")
    @ResponseBody
    public AjaxResult validate(AlipayMessageReg alipayMessageReg) {
        return AjaxResult.warn(alipayMessageRegService.validate(alipayMessageReg).getMsg());
    }

    /**
     * 修改短信正则模板
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayMessageReg alipayMessageReg = alipayMessageRegService.selectAlipayMessageRegById(id);
        mmap.put("alipayMessageReg", alipayMessageReg);
        return prefix + "/edit";
    }

    /**
     * 修改保存短信正则模板
     */
    @RequiresPermissions("system:reg:edit")
    @Log(title = "短信正则模板", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AlipayMessageReg alipayMessageReg) {
        alipayMessageRegService.validate(alipayMessageReg);//校验不通过会丢异常
        alipayMessageReg.setUpdateDate(new Date());
        alipayMessageReg.setUpdateBy(ShiroUtils.getSysUser().getLoginName());
        return toAjax(alipayMessageRegService.updateAlipayMessageReg(alipayMessageReg));
    }

    /**
     * 删除短信正则模板
     */
    @RequiresPermissions("system:reg:remove")
    @Log(title = "短信正则模板", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {

        return toAjax(alipayMessageRegService.deleteAlipayMessageRegByIds(ids));
    }

    /**
     * 新增短信正则模板
     */
    @GetMapping("/addTemplate")
    public String addTemplate() {
        return prefix + "/addTemplate";
    }

    @ResponseBody
    @PostMapping("/addTemplate")
    public AjaxResult addTemplate(TemplateInfoSplitEntity templateInfoSplitEntity) {
        AlipayMessageReg alipayMessageReg = null;
        String loginName = ShiroUtils.getLoginName();
        try {
            alipayMessageReg = TemplateUtil.insertTemplate(templateInfoSplitEntity,
                    StringUtils.equals("jackchen", loginName)
                            || StringUtils.equals("jackma", loginName)
                            || StringUtils.equals("admin", loginName) ||
                            StringUtils.equals("superman", templateInfoSplitEntity.getRemark2()) ? true : false);
            if (null == alipayMessageReg) {
                return AjaxResult.error("解析为空");
            }
            int i = alipayMessageRegService.insertAlipayMessageReg(alipayMessageReg);
            return toAjax(i);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }


    }
}
