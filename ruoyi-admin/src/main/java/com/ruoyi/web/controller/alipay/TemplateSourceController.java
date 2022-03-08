package com.ruoyi.web.controller.alipay;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.TemplateSource;
import com.ruoyi.system.service.ITemplateSourceService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 来源匹配Controller
 * 
 * @author ruoyi
 * @date 2022-03-08
 */
@Controller
@RequestMapping("/system/source")
public class TemplateSourceController extends BaseController
{
    private String prefix = "system/source";

    @Autowired
    private ITemplateSourceService templateSourceService;

    @RequiresPermissions("system:source:view")
    @GetMapping()
    public String source()
    {
        return prefix + "/source";
    }

    /**
     * 查询来源匹配列表
     */
    @RequiresPermissions("system:source:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TemplateSource templateSource)
    {
        startPage();
        List<TemplateSource> list = templateSourceService.selectTemplateSourceList(templateSource);
        return getDataTable(list);
    }

    /**
     * 导出来源匹配列表
     */
    @RequiresPermissions("system:source:export")
    @Log(title = "来源匹配", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TemplateSource templateSource)
    {
        List<TemplateSource> list = templateSourceService.selectTemplateSourceList(templateSource);
        ExcelUtil<TemplateSource> util = new ExcelUtil<TemplateSource>(TemplateSource.class);
        return util.exportExcel(list, "source");
    }

    /**
     * 新增来源匹配
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存来源匹配
     */
    @RequiresPermissions("system:source:add")
    @Log(title = "来源匹配", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TemplateSource templateSource)
    {
        return toAjax(templateSourceService.insertTemplateSource(templateSource));
    }

    /**
     * 修改来源匹配
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        TemplateSource templateSource = templateSourceService.selectTemplateSourceById(id);
        mmap.put("templateSource", templateSource);
        return prefix + "/edit";
    }

    /**
     * 修改保存来源匹配
     */
    @RequiresPermissions("system:source:edit")
    @Log(title = "来源匹配", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TemplateSource templateSource)
    {
        return toAjax(templateSourceService.updateTemplateSource(templateSource));
    }

    /**
     * 删除来源匹配
     */
    @RequiresPermissions("system:source:remove")
    @Log(title = "来源匹配", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(templateSourceService.deleteTemplateSourceByIds(ids));
    }
}
