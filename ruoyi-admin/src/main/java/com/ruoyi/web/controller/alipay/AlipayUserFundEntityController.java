package com.ruoyi.web.controller.alipay;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.ruoyi.alipay.domain.AlipayAmountEntity;
import com.ruoyi.alipay.domain.AlipayUserFundEntity;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.service.IAlipayAmountEntityService;
import com.ruoyi.alipay.service.IAlipayUserFundEntityService;
import com.ruoyi.alipay.service.IAlipayUserInfoService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.DeductStatusEnum;
import com.ruoyi.common.enums.RefundDeductType;
import com.ruoyi.common.utils.GenerateOrderNo;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户资金账户Controller
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Controller
@RequestMapping("/alipay/fund")
public class AlipayUserFundEntityController extends BaseController {
    private String prefix = "alipay/fund";

    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;
    @Autowired
    private DictionaryUtils dictionaryUtils;
    @Autowired
    private IAlipayAmountEntityService alipayAmountEntityService;
    @Autowired
    private SysPasswordService passwordService;


    @GetMapping()
    @RequiresPermissions("alipay:fund:view")
    public String fund() {
        return prefix + "/fund";
    }

    @GetMapping("/bak")
    @RequiresPermissions("alipay:fund:view")
    public String fundBak() {
        return prefix + "/fundBak";
    }

    @Autowired
    private IAlipayUserInfoService alipayUserInfoService;
    /**
     * 商户	1
     * 卡商	2
     * 渠道	3
     * 查询用户资金账户列表
     */
    @PostMapping("/list")
    @RequiresPermissions("fund:alipay:list")
    @ResponseBody
    public TableDataInfo list(AlipayUserFundEntity alipayUserFundEntity) {
        startPage1();
        List<AlipayUserFundEntity> list = alipayUserFundEntityService
                .selectAlipayUserFundEntityList(alipayUserFundEntity);
        AlipayUserFundEntity userFundCardEntity = null;

        List<AlipayUserInfo> userUserAllToBankNot = alipayUserInfoService.findUserUserAllToBankNot();
        ConcurrentHashMap<String, AlipayUserInfo> userCollect = userUserAllToBankNot.stream().collect(Collectors.toConcurrentMap(AlipayUserInfo::getUserId, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
        for(AlipayUserFundEntity fundEntity     : list          )   {
            AlipayUserInfo alipayUserInfo = userCollect.get(fundEntity.getUserId());
            if(null != alipayUserInfo ){
                Integer switchs = alipayUserInfo.getSwitchs();//1  开启
                Integer receiveOrderState = alipayUserInfo.getReceiveOrderState();// 2 暂停接单
                Integer remitOrderState = alipayUserInfo.getRemitOrderState();//2 暂停接单
                String payInfo = "";
                if( 1 != switchs ){
                    payInfo += "主关闭:";
                }
                if( 1  == receiveOrderState ){
                    payInfo += "接单开启:";
                } else {
                    payInfo += "接单关闭:";
                }
                if( 1   == remitOrderState ){
                    payInfo += "代付开启";
                } else {
                    payInfo += "代付关闭";
                }
                fundEntity.setPayInfo(payInfo);
            }
        }
        if (null != alipayUserFundEntity.getUserType() && "2".equals(alipayUserFundEntity.getUserType().toString())) {
            userFundCardEntity = alipayUserFundEntityService.findSumFundForCardDealer();
            list.add(0, userFundCardEntity);
        }else if(null != alipayUserFundEntity.getUserType() && "1".equals(alipayUserFundEntity.getUserType().toString()))
        {
            AlipayUserFundEntity userFundEntity = alipayUserFundEntityService.findSumFundForMerchant();
            list.add(0, userFundEntity);
        }

        return getDataTable(list);
    }

    /**
     * 查询用户资金账户列表
     */
    @PostMapping("/listBak")
    @RequiresPermissions("fund:alipay:list")
    @ResponseBody
    public TableDataInfo listBak(AlipayUserFundEntity alipayUserFundEntity) {
        startPage1();
        List<AlipayUserFundEntity> list = alipayUserFundEntityService
                .findFundBakList(alipayUserFundEntity);
        return getDataTable(list);
    }

    /**
     * 导出用户资金账户列表
     */
    @Log(title = "用户资金账户", businessType = BusinessType.EXPORT)
    @RequiresPermissions("alipay:fund:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AlipayUserFundEntity alipayUserFundEntity) {
        List<AlipayUserFundEntity> list = alipayUserFundEntityService
                .selectAlipayUserFundEntityList(alipayUserFundEntity);
        ExcelUtil<AlipayUserFundEntity> util = new ExcelUtil<AlipayUserFundEntity>(AlipayUserFundEntity.class);
        return util.exportExcel(list, "fund");
    }

    /**
     * 导出用户资金账户列表
     */
    @Log(title = "备份资金账户", businessType = BusinessType.EXPORT)
    @PostMapping("/exportBak")
    @RequiresPermissions("alipay:fund:export")
    @ResponseBody
    public AjaxResult exportBak(AlipayUserFundEntity alipayUserFundEntity) {
        List<AlipayUserFundEntity> list = alipayUserFundEntityService
                .findFundBakList(alipayUserFundEntity);
        ExcelUtil<AlipayUserFundEntity> util = new ExcelUtil<AlipayUserFundEntity>(AlipayUserFundEntity.class);
        return util.exportExcel(list, "bak");
    }

    /*最新的资金账户处理逻辑*/

    /**
     * 新增加款页面显示
     */
    @GetMapping("/refund/{userId}")
//    @RequiresPermissions("fund:refund:add")
    @RequiresPermissions(value = {"fund:refund:add","fund:refundCardDealer:add"},logical = Logical.OR)
    public String refund(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        mmap.put("userFund", userFundEntity);

        AlipayUserInfo merchantInfoByUserId = alipayUserInfoService.findMerchantInfoByUserId(userId);
        if(2 == merchantInfoByUserId.getUserType()){
            mmap.put("isQr", "yes");
        }

        return prefix + "/add";
    }

    /**
     * 新增加款页面显示
     */
    @GetMapping("/addFreezeUrl/{userId}")
    @RequiresPermissions("fund:refund:addFreezeFlag")
    public String addFreezeUrl(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        List<AlipayUserFundEntity> alipayUserFundEntities = alipayUserFundEntityService.selectAlipayUserFundEntityList(userFundEntity);
        mmap.put("userFund", alipayUserFundEntities.get(0));
        return prefix + "/addFreeze";
    }
    @GetMapping("/deposit/{userId}")
    @RequiresPermissions("fund:refund:deposit")
    public String deposit(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        List<AlipayUserFundEntity> alipayUserFundEntities = alipayUserFundEntityService.selectAlipayUserFundEntityList(userFundEntity);
        AlipayUserFundEntity first = CollUtil.getFirst(alipayUserFundEntities);
     double a =    first.getAccountBalance() - first.getSumProfit();
        first.setAccountBalance(a);


        mmap.put("userFund",first);
        return prefix + "/deposit";
    }

    @GetMapping("/deleteFreezeUrl/{userId}")
    @RequiresPermissions("fund:refund:deleteFreezeFlag")
    public String deleteFreezeUrl(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        List<AlipayUserFundEntity> alipayUserFundEntities = alipayUserFundEntityService.selectAlipayUserFundEntityList(userFundEntity);
        mmap.put("userFund", alipayUserFundEntities.get(0));
        return prefix + "/deleteFreeze";
    }

    /**
     * 增加授权页面
     */
    @GetMapping("/addQuotaUrl/{userId}")
    @RequiresPermissions("fund:refund:addQuotaFlag")
    public String addQuotaUrl(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        List<AlipayUserFundEntity> alipayUserFundEntities = alipayUserFundEntityService.selectAlipayUserFundEntityList(userFundEntity);
        mmap.put("userFund", alipayUserFundEntities.get(0));
        return prefix + "/addQuota";
    }

    /**
     * 减少授权页面
     * @param userId
     * @param mmap
     * @return
     */
    @GetMapping("/deleteQuotaUrl/{userId}")
    @RequiresPermissions("fund:refund:deleteQuotaFlag")
    public String deleteQuotaUrl(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        List<AlipayUserFundEntity> alipayUserFundEntities = alipayUserFundEntityService.selectAlipayUserFundEntityList(userFundEntity);
        mmap.put("userFund", alipayUserFundEntities.get(0));
        return prefix + "/deleteQuota";
    }

    /**
     * 加款保存用户加款记录
     */
    @Log(title = "用户资金账户加款", businessType = BusinessType.UPDATE)
    @PostMapping("/refund")
    @ResponseBody
    public AjaxResult refundSave(AlipayAmountEntity alipayAmountEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        alipayAmountEntity.setAccname(currentUser.getLoginName());
        String password = alipayAmountEntity.getParams().get("password").toString();
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), password, currentUser.getSalt());
        if (!currentUser.getPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        return toAjax(alipayAmountEntityService.insertAlipayAmountEntity(alipayAmountEntity));
    }

    /**
     * 加款保存用户加款记录
     */
    @Log(title = "用户资金账户解冻", businessType = BusinessType.UPDATE)
    @PostMapping("/addFreeze")
    @RequiresPermissions("fund:refund:deleteFreezeFlag")
    @ResponseBody
    public AjaxResult addFreeze(AlipayAmountEntity alipayAmountEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        alipayAmountEntity.setAccname(currentUser.getLoginName());
        String password = alipayAmountEntity.getParams().get("password").toString();
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), password, currentUser.getSalt());
        if (!currentUser.getPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        AlipayUserFundEntity alipayUserFundByUserId = alipayUserFundEntityService.findAlipayUserFundByUserId(alipayAmountEntity.getUserId());
        Double freezeBalance = alipayUserFundByUserId.getFreezeBalance();
        if (freezeBalance == 0) {
            return error("当前账户冻结资金不足，请确认好后提交");
        }
        return toAjax(alipayAmountEntityService.insertAlipayAmountFreeze(alipayAmountEntity));
    }
    @Log(title = "修改卡商押金", businessType = BusinessType.UPDATE)
    @PostMapping("/updateDeposit")
    @RequiresPermissions("fund:refund:deposit")
    @ResponseBody
    public AjaxResult updateDeposit(AlipayUserFundEntity userFundEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        String password = userFundEntity.getParams().get("password").toString();
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), password, currentUser.getSalt());
        if (!currentUser.getPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        if(StrUtil.isEmpty(userFundEntity.getUserId())||   null ==  userFundEntity.getDeposit()){
            return AjaxResult.error("参数为空");
        }
        AlipayUserFundEntity fund  = new AlipayUserFundEntity();
        fund.setUserId(userFundEntity.getUserId());
        fund.setDeposit(userFundEntity.getDeposit());
        return toAjax(   alipayUserFundEntityService.updateAlipayUserFundEntity(fund) );
    }
    /**
     * 加款保存用户加款记录
     */
    @Log(title = "增加用户授权额度", businessType = BusinessType.UPDATE)
    @PostMapping("/addQuota")
    @ResponseBody
    public AjaxResult addQuota(AlipayAmountEntity alipayAmountEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        alipayAmountEntity.setAccname(currentUser.getLoginName());
        String password = alipayAmountEntity.getParams().get("password").toString();
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), password, currentUser.getSalt());
        if (!currentUser.getPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        return toAjax(alipayAmountEntityService.insertAlipayAmountQuota(alipayAmountEntity));
    }


    /**
     * 新增减款页面显示
     */
    @GetMapping("/deduct/{userId}")
    public String deduct(@PathVariable("userId") String userId, ModelMap mmap) {
        AlipayUserFundEntity userFundEntity = new AlipayUserFundEntity();
        userFundEntity.setUserId(userId);
        mmap.put("userFund", userFundEntity);
        AlipayUserInfo merchantInfoByUserId = alipayUserInfoService.findMerchantInfoByUserId(userId);
        if(2 == merchantInfoByUserId.getUserType()){
            mmap.put("isQr", "yes");
        }
        return prefix + "/deduct";
    }


    /**
     * 减款保存用户减款记录
     */
    @Log(title = "加减款记录", businessType = BusinessType.UPDATE)
    @PostMapping("/deduct")
    @ResponseBody
    public AjaxResult deductSave(AlipayAmountEntity alipayAmountEntity) {
        // 获取当前的用户
        SysUser currentUser = ShiroUtils.getSysUser();
        alipayAmountEntity.setAccname(currentUser.getLoginName());
        String password = alipayAmountEntity.getParams().get("password").toString();
        String verify = passwordService.encryptPassword(currentUser.getLoginName(), password, currentUser.getSalt());
        if (!currentUser.getPassword().equals(verify)) {
            return AjaxResult.error("密码验证失败");
        }
        //获取alipay处理接口URL
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);
        String urlPath = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_SERVICE_API_KEY, StaticConstants.ALIPAY_SERVICE_API_VALUE_5);
        Map<String, Object> mapParam = Collections.synchronizedMap(Maps.newHashMap());
        mapParam.put("userId", alipayAmountEntity.getUserId());
        mapParam.put("amount", alipayAmountEntity.getAmount());
        mapParam.put("type", alipayAmountEntity.getType());
        mapParam.put("dealDescribe", alipayAmountEntity.getDealDescribe());
        if (alipayAmountEntity.getAmountType().toString().equals(RefundDeductType.DEDUCT_FREEZE_TYPE.getCode().toString())) {
            mapParam.put("amountType", RefundDeductType.DEDUCT_FREEZE_TYPE.getCode());//账户冻结
            AlipayUserFundEntity alipayUserFundByUserId = alipayUserFundEntityService.findAlipayUserFundByUserId(alipayAmountEntity.getUserId());
            Double accountBalance = alipayUserFundByUserId.getAccountBalance();
            if (accountBalance < alipayAmountEntity.getAmount()) {
                return error("当前账户余额不足，不支持账户冻结");
            }
        } else if (alipayAmountEntity.getAmountType().toString().equals(RefundDeductType.DELETE_QUOTA_TYPE.getCode().toString())) {//减少授权额度
            mapParam.put("amountType", RefundDeductType.DELETE_QUOTA_TYPE.getCode());//账户冻结
        } else {
            mapParam.put("amountType", RefundDeductType.DEDUCT_TYPE.getCode());//减款
        }
        mapParam.put("accname", currentUser.getLoginName());//申请人
        mapParam.put("orderStatus", DeductStatusEnum.DEDUCT_STATUS_PROCESS.getCode());
        mapParam.put("orderId", GenerateOrderNo.getInstance().Generate(StaticConstants.PERFIX_DEDUCT));
        return HttpUtils.adminRequest2Gateway(mapParam, ipPort + urlPath);
    }

}
