package com.ruoyi.web.controller.alipay;

import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.alipay.service.IAlipayFileListEntityService;
import com.ruoyi.alipay.service.IBankInfoSplitResultService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 收款媒介列Controller
 *
 * @author water
 */
@Controller
@RequestMapping("/alipay/BankSplitInfo")
public class AlipayBankSplitEntityController extends BaseController {
    private String prefix = "alipay/BankSplitInfo";

    @Autowired
    private IBankInfoSplitResultService bankInfoSplitResultService;

    @Autowired
    private IAlipayFileListEntityService alipayFileListEntityService;

    @GetMapping()
    public String medium() {
        return prefix + "/BankSplitInfo";
    }

    /**
     * 查询收款媒介列列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BankInfoSplitEntity bankSplitEntity) {
        startPage();
        List<BankInfoSplitEntity> list = bankInfoSplitResultService.selectBankInfoSplitResult(bankSplitEntity);
        return getDataTable(list);
    }

    /**
     * 导出收款媒介列列表
     */
    @Log(title = "收款媒介列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BankInfoSplitEntity bankSplitEntity) {
        List<BankInfoSplitEntity> list = bankInfoSplitResultService.selectBankInfoSplitResult(bankSplitEntity);
        ExcelUtil<BankInfoSplitEntity> util = new ExcelUtil<BankInfoSplitEntity>(BankInfoSplitEntity.class);
        return util.exportExcel(list, "bankInfoSpilt");
    }
}
