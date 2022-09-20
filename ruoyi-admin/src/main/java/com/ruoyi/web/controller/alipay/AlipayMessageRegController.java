package com.ruoyi.web.controller.alipay;

import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
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
        String source = alipayMessageReg.getSourceMsg();
        if (alipayMessageReg.getBankName().equals("微信银行")) {
            source = alipayMessageReg.getSourceMsg()
                    .replaceAll("\\r", "")
                    .replaceAll("\\n", "");
        }
        alipayMessageReg.setSourceMsg(source);
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
            alipayMessageReg = TemplateUtil.insertTemplate(TemplateInfoSplitEntity.of(templateInfoSplitEntity));
            if (null == alipayMessageReg) {
                return AjaxResult.error("解析为空");
            }
            alipayMessageReg.setCreatedDate(new Date());
            alipayMessageReg.setCreateBy(loginName);
            int i = alipayMessageRegService.insertAlipayMessageReg(alipayMessageReg);
            return toAjax(i);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }

    }

    @ResponseBody
    @PostMapping("/checkTemplate")
    public AjaxResult checkTemplate(TemplateInfoSplitEntity templateInfoSplitEntity) {
        AlipayMessageReg alipayMessageReg = new AlipayMessageReg();
        try {
            if (null == alipayMessageReg) {
                return AjaxResult.error("参数为空,请稍后再试" + JSONUtil.toJsonStr(alipayMessageReg));
            }
            alipayMessageReg.setBankName(templateInfoSplitEntity.getBankName());
            String originText = templateInfoSplitEntity.getSourceMsg();
            List<AlipayMessageReg> alipayMessageRegs = alipayMessageRegService.selectAlipayMessageRegList(alipayMessageReg);
            if (CollUtil.isNotEmpty(alipayMessageRegs)) {
                for (AlipayMessageReg messageReg : alipayMessageRegs) {
                    String regex = messageReg.getRegex();
                    String template = messageReg.getTemplate();
                    String templateFlag = messageReg.getTemplateFlag();
                    boolean match = ReUtil.isMatch(regex, originText);
                    if (match){
                        String s = ReUtil.extractMulti(regex, originText, template);
                        if (StringUtils.isNotBlank(s)){
                            String msg1="已找到合适模板,原始短信->"+originText+"#";
                            String msg2="匹配正则->"+regex+"#";
                            String msg3="匹配模板->"+template+"#";
                            String ms=templateFlag.equals("1")?"开启":"关闭";
                            String msg4="模板状态->"+ms+"#";
                            String msg5="模板解析结果->"+s;
                            return AjaxResult.success(msg1+msg2+msg3+msg4+msg5);
                        }
                    }
                }
            }
            return AjaxResult.error("未找到合适模板,请添加模板");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }
    }
}
