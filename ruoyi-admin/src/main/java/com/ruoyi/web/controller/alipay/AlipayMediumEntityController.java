package com.ruoyi.web.controller.alipay;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.service.IAlipayFileListEntityService;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.MapDataUtil;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.framework.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 收款媒介列Controller
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Controller
@RequestMapping("/alipay/medium")
public class AlipayMediumEntityController extends BaseController {
    private String prefix = "alipay/medium" ;
    private String code_prefix = "alipay/file" ;

    @Autowired
    private IAlipayMediumEntityService alipayMediumEntityService;

    @Autowired
    private IAlipayFileListEntityService alipayFileListEntityService;

    @GetMapping()
    public String medium() {
        return prefix + "/medium" ;
    }

    /**
     * 查询收款媒介列列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AlipayMediumEntity alipayMediumEntity) {
        startPage();
        List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);
        if(StrUtil.isNotEmpty(alipayMediumEntity.getQrcodeId())){
        AlipayMediumEntity mediumEntity =     alipayMediumEntityService.findBankSum(alipayMediumEntity.getQrcodeId());
        if (null != mediumEntity && CollUtil.isNotEmpty(list)) {
            for (int mark = 0; mark < 1; mark++) {
                list.get(mark).setBankSumAmountsys(mediumEntity.getBankSumAmountsys());
                list.get(mark).setBankSumAmountnow(mediumEntity.getBankSumAmountnow());
                list.get(mark).setOpenSumBankAmountsys(mediumEntity.getOpenSumBankAmountsys());
                list.get(mark).setOpenSumBankAmountnow(mediumEntity.getOpenSumBankAmountnow());
            }
        }
        }
        return getDataTable(list);
    }

    /**
     * 导出收款媒介列列表
     */
    @Log(title = "收款媒介列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AlipayMediumEntity alipayMediumEntity) {
        List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);
        ExcelUtil<AlipayMediumEntity> util = new ExcelUtil<AlipayMediumEntity>(AlipayMediumEntity.class);
        return util.exportExcel(list, "medium");
    }

    /**
     * 新增收款媒介列
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add" ;
    }

    /**
     * 单个媒介修改金额页面
     */
    @GetMapping("/editAmount/{id}")
    public String editAmount(@PathVariable("id") Long id, ModelMap mmap) {
        mmap.put("id", id);
        return prefix + "/editAmount" ;
    }

    /**
     * 同种媒介修改金额页面
     */
    @GetMapping("/editAmountByCode/{code}")
    public String editAmountByCode(@PathVariable("code") String code, ModelMap mmap) {
        mmap.put("code", code);
        return prefix + "/editAmountByCode" ;
    }


    /**
     * 新增保存收款媒介列
     */
    @Log(title = "收款媒介列", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AlipayMediumEntity alipayMediumEntity) {
        return toAjax(alipayMediumEntityService.insertAlipayMediumEntity(alipayMediumEntity));
    }

    /**
     * 修改收款媒介列
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayMediumEntity alipayMediumEntity = alipayMediumEntityService.selectAlipayMediumEntityById(id);
        mmap.put("alipayMediumEntity", alipayMediumEntity);
        return prefix + "/edit" ;
    }


    /**
     * 修改保存收款媒介列
     */
    @Log(title = "收款媒介列", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AlipayMediumEntity alipayMediumEntity) {
        return toAjax(alipayMediumEntityService.updateAlipayMediumEntity(alipayMediumEntity));
    }

    /**
     * 修改保存收款媒介列
     */
    @Log(title = "收款媒介列", businessType = BusinessType.UPDATE)
    @PostMapping("/editByCode")
    @ResponseBody
    public AjaxResult editSaveByCode(AlipayMediumEntity alipayMediumEntity) {
        return toAjax(alipayMediumEntityService.updateAlipayMediumEntityByCode(alipayMediumEntity));
    }

    /**
     * 删除收款媒介列
     */
    @Log(title = "收款媒介列", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(alipayMediumEntityService.deleteAlipayMediumEntityByIds(ids));
    }
    @Log(title = "剔除银行卡", businessType = BusinessType.UPDATE)
    @PostMapping("/removeQueue/{id}")
    @ResponseBody
    public AjaxResult removeQueue(@PathVariable("id")String id) {
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);//主服务器ip+port
        String url = ipPort + "/medium" + "/off-medium-qr";
        Map<String,Object> map = new HashMap();
        map.put("mediumNumber",id);
        String s = HttpUtil.post(url,  map );
        return toAjax(1);
    }
    @Log(title = "优先银行卡", businessType = BusinessType.UPDATE)
    @PostMapping("/first")
    @ResponseBody
    public AjaxResult first(AlipayMediumEntity alipayMediumEntity  ) {

        logger.info("银行卡："+alipayMediumEntity.getMediumNumber()+"分组号："+alipayMediumEntity.getMediumId()+",操作人："+ ShiroUtils.getLoginName());
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);//主服务器ip+port
        String url = ipPort + "/out/pushCard?cardInfo="+alipayMediumEntity.getMediumNumber()+"&userId="+alipayMediumEntity.getMediumId();
        String s = HttpUtil.get(url);
        return toAjax(1);
    }


    /**
     * 查看所属二维码列表
     */
    @GetMapping("/show/{userId}")
    public String showCodeList(@PathVariable("userId") String mediumId, ModelMap mmap) {
        AlipayFileListEntity alipayFileListEntity = new AlipayFileListEntity();
        alipayFileListEntity.setConcealId(mediumId);
        alipayFileListEntity.setIsDeal("2");
        List<AlipayFileListEntity> list = alipayFileListEntityService.selectAlipayFileListEntityList(alipayFileListEntity);
        mmap.put("codeList", list);
        return code_prefix + "/group_code_list" ;
    }
    @Autowired
    private DictionaryUtils dictionaryUtils;
    @GetMapping("/mediumQueue")
    public String mediumQueue() {
        return prefix + "/mediumQueue" ;
    }

    /**
     * 查询收款媒介列列表
     */
    @PostMapping("/listQueue")
    @ResponseBody
    public TableDataInfo listQueue(AlipayMediumEntity alipayMediumEntity) {
        List<MediumQueue> list = new ArrayList<MediumQueue>();
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);//主服务器ip+port
        String url = ipPort + "/out" + "/findQueue?cardInfo="+alipayMediumEntity.getQrcodeId();
        String s = HttpUtil.get(url);
        logger.info("【收到队列数据为："+s+"】");
        List<AlipayMediumEntity> listQueue = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONUtil.parseObj(s);
            Object result = jsonObject.get("result");
            JSONArray objects = JSONUtil.parseArray(result);
            logger.info("当前获取队列数据总条数："+objects.size());
            for (int i = 0; i < objects.size(); i++) {
                AlipayMediumEntity med = new AlipayMediumEntity();
                JSONObject queue = objects.getJSONObject(i);
                String bankId = queue.getStr("bankId");//卡号
                String score = queue.getStr("score");//排序
                String gourp = queue.getStr("gourp");//分组
                String bankName = queue.getStr("bankName");// 银行名字
                String userId = queue.getStr("userId");// 银行名字
                String bankAccount = queue.getStr("bankAccount");// 开户人姓名
                String amount = queue.getStr("amount");// 开户人姓名
                med.setMediumId(gourp);
                med.setMountLimit(score);
                med.setQrcodeId(userId);
                med.setMediumNote(bankName+":"+bankAccount+":参考余额:"+amount);
                med.setMediumNumber(bankId);
                listQueue.add(med);
            }

        }catch (Exception x){

        }
        return getDataTable(listQueue);
    }


    @PostMapping("/getBankcard")
    @ResponseBody
    public AjaxResult showCodeList(AlipayUserInfo userInfo) {
        AlipayMediumEntity  alipayMediumEntity = new AlipayMediumEntity();
        String userId = UUID.randomUUID().toString();
        if(StrUtil.isNotEmpty(userInfo.getUserId())){
            userId = userInfo.getUserId();
        }
        alipayMediumEntity.setQrcodeId(userId);
        List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);
        AlipayMediumEntity mediumEntity =     alipayMediumEntityService.findBankSum(userId);
        if (null != mediumEntity && CollUtil.isNotEmpty(list)) {
            for (int mark = 0; mark < 1; mark++) {
                list.get(mark).setBankSumAmountsys(mediumEntity.getBankSumAmountsys());
                list.get(mark).setBankSumAmountnow(mediumEntity.getBankSumAmountnow());
                list.get(mark).setOpenSumBankAmountsys(mediumEntity.getOpenSumBankAmountsys());
                list.get(mark).setOpenSumBankAmountnow(mediumEntity.getOpenSumBankAmountnow());
            }
        }
        return  AjaxResult.success(list);
    }

    /**
     * 码商状态修改（调用api）
     */
    @Log(title = "修改银行卡属性", businessType = BusinessType.UPDATE)
    @PostMapping("/updateBankcard")
    @ResponseBody
    public AjaxResult changeStatus(AlipayMediumEntity alipayMediumEntity) {
        logger.info("[当前处理商户关闭或者开启的管理员账号为：" + ShiroUtils.getSysUser().getLoginName() + "]");
        logger.info("[当前处理商户状态的参数为：" + alipayMediumEntity.getBlack().toString() + "]");
        AlipayMediumEntity mediumEntity = new AlipayMediumEntity();
        mediumEntity.setId(alipayMediumEntity.getId());
        mediumEntity.setBlack(alipayMediumEntity.getBlack());
        int i = alipayMediumEntityService.updateAlipayMediumEntity(mediumEntity);
        return toAjax(i);
    }

}
