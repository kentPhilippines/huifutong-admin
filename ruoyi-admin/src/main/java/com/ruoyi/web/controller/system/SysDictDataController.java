package com.ruoyi.web.controller.system;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.HUOBI;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.domain.SysDictType;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import com.ruoyi.web.feign.NotifyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {
    private String prefix = "system/dict/data";
    @Value("${otc.usdt.rate}")
    private String otcRate;
    @Autowired
    private NotifyService aliyunService;
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @RequiresPermissions("system:dict:view")
    @GetMapping()
    public String dictData() {
        return prefix + "/data";
    }

    private Map cache = new ConcurrentHashMap();

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:dict:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        return util.exportExcel(list, "字典数据");
    }

    /**
     * 新增字典类型
     */
    @GetMapping("/add/{dictType}")
    public String add(@PathVariable("dictType") String dictType, ModelMap mmap) {
        mmap.put("dictType", dictType);
        return prefix + "/add";
    }

    /**
     * 新增字典类型
     */
    @GetMapping("/addBlackConfig/{dictType}")
    public String addBlackConfig(@PathVariable("dictType") String dictType, ModelMap mmap) {
        mmap.put("dictType", dictType);
        return prefix + "/addBlackConfig";
    }

    @GetMapping("/addManage/{dictType}")
    public String addManage(@PathVariable("dictType") String dictType, ModelMap mmap) {
        mmap.put("dictType", dictType);
        return prefix + "/addManage";
    }


    /**
     * 新增保存字典类型
     */
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @RequiresPermissions("system:dict:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated SysDictData dict) {
        AjaxResult ajax = AjaxResult.success();
        try {
            if(dict.getDictType().startsWith("ALIYUN"))
            {
                String comment = dictTypeService.selectDictTypeByType(dict.getDictType()).getRemark();
                logger.info("aliyun addwhitelist :{},{}",comment,dict.getDictValue());
                aliyunService.addIpWhiteList(comment,dict.getDictValue(),dict.getDictValue());
            }
            dict.setCreateBy(ShiroUtils.getLoginName());
            ajax = toAjax(dictDataService.insertDictData(dict));
        }catch (Exception e)
        {
            e.printStackTrace();
            ajax = AjaxResult.error(e.getMessage());
        }

        return ajax;
    }


    /**
     * 修改字典类型
     */
    @GetMapping("/edit/{dictCode}")
    public String edit(@PathVariable("dictCode") Long dictCode, ModelMap mmap) {
        mmap.put("dict", dictDataService.selectDictDataById(dictCode));
        return prefix + "/edit";
    }

    /**
     * 修改字典类型
     */
    @GetMapping("/editBlackConfig/{dictCode}")
    public String editBlackConfig(@PathVariable("dictCode") Long dictCode, ModelMap mmap) {
        mmap.put("dict", dictDataService.selectDictDataById(dictCode));
        return prefix + "/editBlackConfig";
    }

    /**
     * 删除字典类型
     */
    @PostMapping("/removeBlackConfig/{dictCode}")
    @ResponseBody
    public AjaxResult removeBlackConfig(@PathVariable("dictCode") Long id, ModelMap mmap) {


        AjaxResult ajax = AjaxResult.success();
        try {
            SysDictData dictData = dictDataService.selectDictDataById(id);

            if(dictData.getDictType().startsWith("ALIYUN"))
            {
                SysDictType dictType =  dictTypeService.selectDictTypeByType(dictData.getDictType());
                aliyunService.deleteIpWhiteList(dictType.getRemark(),dictData.getDictValue(),dictData.getDictValue());
            }
            ajax = toAjax(dictDataService.deleteDictDataByIds(id+""));
        }catch (Exception e)
        {
            e.printStackTrace();
            ajax = AjaxResult.error(e.getMessage());
        }

        return ajax;
    }



    @GetMapping("/editManage/{dictCode}")
    public String editManage(@PathVariable("dictCode") Long dictCode, ModelMap mmap) {
        mmap.put("dict", dictDataService.selectDictDataById(dictCode));
        return prefix + "/editManage";
    }

    /**
     * 修改保存字典类型
     */
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @RequiresPermissions("system:dict:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated SysDictData dict) {
        dict.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(dictDataService.updateDictData(dict));
    }

    private String MARK = "_";
    private String RATE_KEY = "usdtrate" + MARK;

    @PostMapping("/list")
    @RequiresPermissions("system:dict:list")
    @ResponseBody
    public TableDataInfo list(SysDictData dictData) {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        if (dictData.getDictType().equals("CNY_USDT")) {
            for (SysDictData data : list) {
                data.setDictValueTime(getHUOBIRateFee());
            }
        }
        return getDataTable(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @RequiresPermissions("system:dict:remove")
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {


        return toAjax(dictDataService.deleteDictDataByIds(ids));
    }

    public String getHUOBIRateFee() {
        Set<String> set = cache.keySet();
        for (String key : set) {
            String[] split = key.split(MARK);
            String s = split[1];
            if (DateUtil.isExpired(DateUtil.parse(s).toJdkDate(), DateField.SECOND, 5, new Date())) {
                cache.remove(key);
            }
            ;
        }
        Object o = this.cache.get(RATE_KEY + DateUtils.getTime());
        if (null == o) {
            String rate = getRate("buy");
            this.cache.put(RATE_KEY + DateUtils.getTime(), rate);
            return rate;
        } else {
            return "" + o;
        }
    }

    @PostMapping("/listRateInHUOBI")
    @ResponseBody
    public TableDataInfo listRateInHUOBI() {
        /**
         * 获取火币网费率作为最低和最高的 费率交易标准
         */
        List<HUOBI> list = new ArrayList<HUOBI>();
        HUOBI bigsell = new HUOBI();
        bigsell.setId("1");
        bigsell.setRateType("大宗买入价格");
        bigsell.setPrice(getRate("sell"));
        bigsell.setCaeateTime(DateUtils.getTime());
        list.add(bigsell);
        HUOBI bigbuy = new HUOBI();
        bigbuy.setId("2");
        bigbuy.setRateType("大宗出售价格");
        bigbuy.setPrice(getRate("buy"));
        bigbuy.setCaeateTime(DateUtils.getTime());
        list.add(bigbuy);
        HUOBI smalls = new HUOBI();
        smalls.setId("3");
        smalls.setRateType("自选交易购买价格");
        smalls.setPrice(getRate("sell"));
        smalls.setCaeateTime(DateUtils.getTime());
        list.add(smalls);
        HUOBI smallb = new HUOBI();
        smallb.setId("4");
        smallb.setRateType("自选交易出售价格");
        smallb.setPrice(getRate("buy"));
        smallb.setCaeateTime(DateUtils.getTime());
        list.add(smallb);
        return getDataTable(list);
    }


    String getRate(String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", type);
        String post = null;
        try {
            post = HttpUtil.get(otcRate, data);
        } catch (Exception e) {
            logger.error("获取汇率失败", e);
            return null;
        }
        return post;
    }

    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "sell");
        String post = null;
        post = HttpUtil.get("http://127.0.0.1:8081/http/rate", data);
        System.out.println("出售价格：" + post);

        data.put("type", "sell");
        String post1 = null;
        post1 = HttpUtil.get("http://127.0.0.1:8081/http/rate", data);
        System.out.println("购买价格：" + post1);

    }
}
