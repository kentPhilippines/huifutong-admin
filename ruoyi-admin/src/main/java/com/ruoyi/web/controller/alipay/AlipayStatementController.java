package com.ruoyi.web.controller.alipay;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.alipay.domain.AlipayStatement;
import com.ruoyi.alipay.service.IAlipayStatementService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 对账Controller
 *
 * @author ruoyi
 * @date 2021-12-11
 */
@Controller
@RequestMapping("/system/statement")
public class AlipayStatementController extends BaseController {
    private String prefix = "system/statement";

    @Autowired
    private IAlipayStatementService alipayStatementService;

    @RequiresPermissions("system:statement:view")
    @GetMapping()
    public String statement() {
        return prefix + "/statement";
    }

    /**
     * 查询对账列表
     */
    @RequiresPermissions("system:statement:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AlipayStatement alipayStatement) {
        startPage();
        List<AlipayStatement> list = alipayStatementService.selectAlipayStatementList(alipayStatement);
        return getDataTable(list);
    }

    /**
     * 导出对账列表
     */
    @RequiresPermissions("system:statement:export")
    @Log(title = "对账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AlipayStatement alipayStatement) {
        List<AlipayStatement> list = alipayStatementService.selectAlipayStatementList(alipayStatement);
        ExcelUtil<AlipayStatement> util = new ExcelUtil<AlipayStatement>(AlipayStatement.class);
        return util.exportExcel(list, "statement");
    }

    /**
     * 新增对账
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存对账
     */
    @RequiresPermissions("system:statement:add")
    @Log(title = "对账", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AlipayStatement alipayStatement) {
        return toAjax(alipayStatementService.insertAlipayStatement(alipayStatement));
    }

    /**
     * 修改对账
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayStatement alipayStatement = alipayStatementService.selectAlipayStatementById(id);
        mmap.put("alipayStatement", alipayStatement);
        return prefix + "/edit";
    }

    /**
     * 修改保存对账
     */
    @RequiresPermissions("system:statement:edit")
    @Log(title = "对账", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AlipayStatement alipayStatement) {
        return toAjax(alipayStatementService.updateAlipayStatement(alipayStatement));
    }

    /**
     * 删除对账
     */
    @RequiresPermissions("system:statement:remove")
    @Log(title = "对账", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(alipayStatementService.deleteAlipayStatementByIds(ids));
    }
}
