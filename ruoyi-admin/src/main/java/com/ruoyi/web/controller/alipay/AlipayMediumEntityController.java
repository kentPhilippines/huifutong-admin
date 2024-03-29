package com.ruoyi.web.controller.alipay;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.alipay.domain.*;
import com.ruoyi.alipay.domain.util.DesUtil;
import com.ruoyi.alipay.service.IAlipayFileListEntityService;
import com.ruoyi.alipay.service.IAlipayMediumEntityService;
import com.ruoyi.alipay.service.IAlipayUserInfoService;
import com.ruoyi.alipay.service.IAlipayUserRateEntityService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.StaticConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.DictionaryUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.ImportBankVerifyDto;
import com.ruoyi.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 收款媒介列Controller
 *
 * @author kiwi
 * @date 2020-03-17
 */
@Controller
@RequestMapping("/alipay/medium")
public class AlipayMediumEntityController extends BaseController {
    private String prefix = "alipay/medium";
    private String code_prefix = "alipay/file";

    @Autowired
    private IAlipayMediumEntityService alipayMediumEntityService;
    @Autowired
    private IAlipayUserInfoService alipayUserInfoService;

    @Autowired
    private IAlipayFileListEntityService alipayFileListEntityService;

    @Autowired
    private IAlipayUserRateEntityService alipayUserRateEntityService;

    @GetMapping()
    public String medium() {
        return prefix + "/medium";
    }

    /**
     * 查询收款媒介列列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AlipayMediumEntity alipayMediumEntity) {
        if (StringUtils.isNotBlank(alipayMediumEntity.getMediumNumber())) {
            alipayMediumEntity.setMediumNumberJiami(DesUtil.encryptHex(alipayMediumEntity.getMediumNumber()));
        }
        startPage();
        List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);

        //根据用户查询出相关的通道费率  如果包含小额BANK_WIT_S  就返回给前端
        List ids = list.stream().map(AlipayMediumEntity::getQrcodeId).distinct().collect(Collectors.toList());
        List<AlipayUserRateEntity> userRateEntities = alipayUserRateEntityService.findRates(String.join(",", ids), "BANK_WIT_S");
        List smallUserIds = userRateEntities.stream().map(AlipayUserRateEntity::getUserId).collect(Collectors.toList());
        list.stream().filter(entity -> smallUserIds.contains(entity.getQrcodeId())).forEach(entity -> entity.setSmall(true));
        //解密卡号
        /*list.stream().filter(entity -> !NumberUtils.isDigits(entity.getMediumNumber())).forEach(e->{
            e.setMediumNumber(DesUtil.decryptStr(e.getMediumNumber()));
        });*/


        if (StrUtil.isNotEmpty(alipayMediumEntity.getQrcodeId())) {
            AlipayMediumEntity mediumEntity = null;
            //有标识就查询历史数据
            if (alipayMediumEntity.getParams().containsKey("isHistoryQuery") && alipayMediumEntity.getParams().get("isHistoryQuery").toString().equals("1")) {
                mediumEntity = alipayMediumEntityService.findBankSumBak(alipayMediumEntity.getQrcodeId());
            } else {
                mediumEntity = alipayMediumEntityService.findBankSum(alipayMediumEntity.getQrcodeId());
            }
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
        return prefix + "/add";
    }


    /**
     * 单个媒介修改金额页面
     */
    @GetMapping("/editAmount/{id}")
    public String editAmount(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayMediumEntity alipayMediumEntity = alipayMediumEntityService.selectAlipayMediumEntityById(Long.valueOf(id));
        mmap.put("alipayMediumEntity", alipayMediumEntity);
        return prefix + "/editAmount";
    }

    /**
     * 单个媒介修改金额页面
     */
    @GetMapping("/editAmountByBank")
    public String editAmountByBank(ModelMap mmap) {
        //AlipayMediumEntity alipayMediumEntity = alipayMediumEntityService.selectAlipayMediumEntityById(Long.valueOf(id));
        mmap.put("alipayMediumEntity", new AlipayMediumEntity());
        mmap.put("banks", alipayMediumEntityService.findAllBankNames());
        return prefix + "/editAmountByBank";
    }

    /**
     * 同种媒介修改金额页面
     */
    @GetMapping("/editAmountByCode/{code}")
    public String editAmountByCode(@PathVariable("code") String code, ModelMap mmap) {
        mmap.put("code", code);
        return prefix + "/editAmountByCode";
    }

    @GetMapping("/edits/{ids}")
    public String edit(@PathVariable("ids") String ids, ModelMap mmap) {
        mmap.put("ids", ids);
        List<String> list = Arrays.asList(ids.split(","));
        List<AlipayMediumEntity> alipayMediumEntities = alipayMediumEntityService.findIds(list);
        StrBuilder strBuilder = StrBuilder.create();
        for (AlipayMediumEntity entity : alipayMediumEntities) {
            strBuilder.append("id:").append(entity.getId()).append(" 账户编号：").append(entity.getMediumNumber()).append(" ").append("原状态：");
            if (entity.getStatus() == 1) {
                strBuilder.append("开启");
            } else if (entity.getStatus() == 0) {
                strBuilder.append("关闭");
            }
            strBuilder.append(" ");
        }
        mmap.put("rete", strBuilder.toString());
        return prefix + "/edits";
    }

    @GetMapping("/removes/{ids}")
    public String ramoves(@PathVariable("ids") String ids, ModelMap mmap) {
        mmap.put("ids", ids);
        List<String> list = Arrays.asList(ids.split(","));
        List<AlipayMediumEntity> alipayMediumEntities = alipayMediumEntityService.findIds(list);
        StrBuilder strBuilder = StrBuilder.create();
        for (AlipayMediumEntity entity : alipayMediumEntities) {
            strBuilder.append("id:").append(entity.getId()).append(" 账户编号：").append(entity.getMediumNumber()).append(" ").append("原状态：");
            if (entity.getStatus() == 1) {
                strBuilder.append("开启");
            } else if (entity.getStatus() == 0) {
                strBuilder.append("关闭");
            }
            strBuilder.append(" ");
        }
        mmap.put("rete", strBuilder.toString());
        return prefix + "/removes";
    }

    @Log(title = "批量修改媒介状态", businessType = BusinessType.UPDATE)
    @PostMapping("/edits")
    @ResponseBody
    public AjaxResult editsSave(String ids, Integer status) {
        alipayMediumEntityService.batchUpdateMacthMore(ids, status);
        return AjaxResult.success();
    }

    @Log(title = "账号批量删除", businessType = BusinessType.UPDATE)
    @PostMapping("/removes")
    @ResponseBody
    public AjaxResult ramoves(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        list.stream().forEach(e -> {
            AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
            alipayMediumEntity.setId(Long.valueOf(e));
            SysUser currentUser = ShiroUtils.getSysUser();
            alipayMediumEntityService.deleteAlipayMediumEntityById(alipayMediumEntity, currentUser);
        });
        return toAjax(1);

    }

    /**
     * 修改银行名称
     */
    @GetMapping("/editBankName/{id}")
    public String editBankName(@PathVariable("id") Long id, ModelMap mmap) {
        AlipayMediumEntity alipayMediumEntity = alipayMediumEntityService.selectAlipayMediumEntityById(id);
        mmap.put("alipayMediumEntity", alipayMediumEntity);
        return prefix + "/editBankName";
    }

    /**
     * 修改姓名验证
     */
    @GetMapping("/editVerifyStatus")
    public String editVerifyStatus(ModelMap mmap) {
        mmap.put("alipayMediumEntity", new AlipayMediumEntity());
        mmap.put("banks", alipayMediumEntityService.findAllBankNames());
        return prefix + "/editVerifyStatus";
    }

    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate() {
        ExcelUtil<ImportBankVerifyDto> util = new ExcelUtil<ImportBankVerifyDto>(ImportBankVerifyDto.class);
        return util.importTemplateExcel("姓名验证数据");
    }

    //@Log(title = "用户管理", businessType = BusinessType.IMPORT)
    //@RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<ImportBankVerifyDto> util = new ExcelUtil<ImportBankVerifyDto>(ImportBankVerifyDto.class);
        List<ImportBankVerifyDto> dataList = util.importExcel(file.getInputStream());
        String operName = ShiroUtils.getSysUser().getLoginName();
        String message = alipayMediumEntityService.importData(dataList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 修改保存收款媒介列
     */
    @Log(title = "修改姓名验证", businessType = BusinessType.UPDATE)
    @PostMapping("/editVerifyStatus")
    @ResponseBody
    public AjaxResult editVerifyStatus(AlipayMediumEntity alipayMediumEntity) {
        return toAjax(alipayMediumEntityService.updateAlipayMediumEntityByBankName(alipayMediumEntity));
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
        return prefix + "/edit";
    }


    /**
     * 修改保存收款媒介列
     */
    @Log(title = "修改控制", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AlipayMediumEntity alipayMediumEntity) {
        return toAjax(alipayMediumEntityService.updateAlipayMediumEntity(alipayMediumEntity));
    }

    /**
     * 修改保存收款媒介列
     */
    @Log(title = "修改控制", businessType = BusinessType.UPDATE)
    @PostMapping("/editByBankName")
    @ResponseBody
    public AjaxResult editSaveByBankName(AlipayMediumEntity alipayMediumEntity) {
        return toAjax(alipayMediumEntityService.updateAlipayMediumEntityByBankName(alipayMediumEntity));
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
        AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
        alipayMediumEntity.setId(Long.valueOf(ids));
        SysUser currentUser = ShiroUtils.getSysUser();
        return toAjax(alipayMediumEntityService.deleteAlipayMediumEntityById(alipayMediumEntity, currentUser));
    }

    @Log(title = "剔除银行卡", businessType = BusinessType.UPDATE)
    @PostMapping("/removeQueue/{id}")
    @ResponseBody
    public AjaxResult removeQueue(@PathVariable("id") String id) {
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);//主服务器ip+port
        String url = ipPort + "/medium" + "/off-medium-qr";
        Map<String, Object> map = new HashMap();
        map.put("mediumNumber", id);
        String s = HttpUtil.post(url, map);
        return toAjax(1);
    }

    @Log(title = "优先银行卡", businessType = BusinessType.UPDATE)
    @PostMapping("/first")
    @ResponseBody
    public AjaxResult first(AlipayMediumEntity alipayMediumEntity) {

        logger.info("银行卡：" + alipayMediumEntity.getMediumNumber() + "分组号：" + alipayMediumEntity.getMediumId() + ",操作人：" + ShiroUtils.getLoginName());
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);//主服务器ip+port
        String url = ipPort + "/out/pushCard?cardInfo=" + alipayMediumEntity.getMediumNumber() + "&userId=" + alipayMediumEntity.getMediumId();
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
        return code_prefix + "/group_code_list";
    }

    @Autowired
    private DictionaryUtils dictionaryUtils;

    @GetMapping("/mediumQueue")
    public String mediumQueue() {
        return prefix + "/mediumQueue";
    }

    /**
     * 查询收款媒介列列表
     */
    @PostMapping("/listQueue")
    @ResponseBody
    public TableDataInfo listQueue(AlipayMediumEntity alipayMediumEntity) {
        List<MediumQueue> list = new ArrayList<MediumQueue>();
        String ipPort = dictionaryUtils.getApiUrlPath(StaticConstants.ALIPAY_IP_URL_KEY, StaticConstants.ALIPAY_IP_URL_VALUE);//主服务器ip+port
        String url = ipPort + "/out" + "/findQueue?cardInfo=" + alipayMediumEntity.getQrcodeId();
        logger.info(url);
        String s = HttpUtil.get(url);
        logger.info("【收到队列数据为：" + s + "】");

        /**
         * {
         *     "success": true,
         *     "message": "请求成功",
         *     "result": [
         *         {
         *             "bankId": "6217858000121031584",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "wang168",
         *             "startFund": "通道配额",
         *             "fund": "5594.0000",
         *             "deposit": "10000.000000",
         *             "status": null,
         *             "freezeBalance": "502.00",
         *             "score": 20410.0,
         *             "bankName": "中国银行",
         *             "bankAccount": "杨帅武",
         *             "amount": "4386.770000"
         *         },
         *         {
         *             "bankId": "6217994730038848226",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "atai999",
         *             "startFund": "2000",
         *             "fund": "835.0000",
         *             "deposit": "30000.100000",
         *             "status": null,
         *             "freezeBalance": "0",
         *             "score": 20850.0,
         *             "bankName": "邮储银行",
         *             "bankAccount": "胡守勇",
         *             "amount": "0.230000"
         *         },
         *         {
         *             "bankId": "6217856100025745339",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "vip2022",
         *             "startFund": "通道配额",
         *             "fund": "9775.0000",
         *             "deposit": "18579.000000",
         *             "status": null,
         *             "freezeBalance": "9502.00",
         *             "score": 20910.0,
         *             "bankName": "中国银行",
         *             "bankAccount": "杨祖耀",
         *             "amount": "5558.840000"
         *         },
         *         {
         *             "bankId": "6230580000189787943",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "lhfa123",
         *             "startFund": "3000",
         *             "fund": "16346.0000",
         *             "deposit": "27617.100000",
         *             "status": null,
         *             "freezeBalance": "6000.00",
         *             "score": 20920.0,
         *             "bankName": "平安银行",
         *             "bankAccount": "莫小蝶",
         *             "amount": "5109.030000"
         *         },
         *         {
         *             "bankId": "6230521990022027675",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "vip2022",
         *             "startFund": "通道配额",
         *             "fund": "9775.0000",
         *             "deposit": "18579.000000",
         *             "status": null,
         *             "freezeBalance": "9502.00",
         *             "score": 20960.0,
         *             "bankName": "农业银行",
         *             "bankAccount": "杨祖耀",
         *             "amount": "0.000000"
         *         },
         *         {
         *             "bankId": "6235737000011723497",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "s75328",
         *             "startFund": "通道配额",
         *             "fund": "53.9200",
         *             "deposit": "7959.920000",
         *             "status": null,
         *             "freezeBalance": "502.00",
         *             "score": 20970.0,
         *             "bankName": "中国银行",
         *             "bankAccount": "谭绍平",
         *             "amount": "3030.000000"
         *         },
         *         {
         *             "bankId": "623059113303479836",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "wudi56789",
         *             "startFund": "4000",
         *             "fund": "32045.4300",
         *             "deposit": "50000.000000",
         *             "status": null,
         *             "freezeBalance": "7280.00",
         *             "score": 20990.0,
         *             "bankName": "河南农信",
         *             "bankAccount": "王占江",
         *             "amount": "8821.000000"
         *         },
         *         {
         *             "bankId": "6230486600000039377",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "dawin8888",
         *             "startFund": "4000",
         *             "fund": "30376.0700",
         *             "deposit": "60000.000000",
         *             "status": null,
         *             "freezeBalance": "15000.00",
         *             "score": 21000.0,
         *             "bankName": "四川银行",
         *             "bankAccount": "蒲水英",
         *             "amount": "10157.000000"
         *         },
         *         {
         *             "bankId": "6222620810017442068",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "ww2022",
         *             "startFund": "通道配额",
         *             "fund": "5420.0000",
         *             "deposit": "7496.000000",
         *             "status": null,
         *             "freezeBalance": "3350.00",
         *             "score": 21020.0,
         *             "bankName": "交通银行",
         *             "bankAccount": "徐卫武",
         *             "amount": "4070.310000"
         *         },
         *         {
         *             "bankId": "621452002000208795",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "l1391101",
         *             "startFund": "1500",
         *             "fund": "63540.0000",
         *             "deposit": "77748.000000",
         *             "status": null,
         *             "freezeBalance": "12488.00",
         *             "score": 21030.0,
         *             "bankName": "天津银行",
         *             "bankAccount": "李英楠",
         *             "amount": "8.000000"
         *         },
         *         {
         *             "bankId": "6215821770000006224",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "mn89562322",
         *             "startFund": "通道配额",
         *             "fund": "9248.2100",
         *             "deposit": "10225.000000",
         *             "status": null,
         *             "freezeBalance": "5800.00",
         *             "score": 21040.0,
         *             "bankName": "邮储银行",
         *             "bankAccount": "贾青青",
         *             "amount": "531.800000"
         *         },
         *         {
         *             "bankId": "6217995010013524251",
         *             "gourp": "QUEUE:REDIS:huifutong-bank",
         *             "userId": "hyh050607",
         *             "startFund": "通道配额",
         *             "fund": "5549.5600",
         *             "deposit": "10000.000000",
         *             "status": null,
         *             "freezeBalance": "3000.00",
         *             "score": 21050.0,
         *             "bankName": "邮储银行",
         *             "bankAccount": "和轶豪",
         *             "amount": "8361.830000"
         *         }
         *     ],
         *     "code": 1
         * }
         */

        List<AlipayMediumEntity> medList = alipayMediumEntityService.findOpenMed();

        ConcurrentHashMap<String, AlipayMediumEntity> collect = medList.stream().
                collect(Collectors.toConcurrentMap(AlipayMediumEntity::getMediumNumber, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));

        List<AlipayMediumEntity> listQueue = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONUtil.parseObj(s);
            Object result = jsonObject.get("result");
            JSONArray objects = JSONUtil.parseArray(result);
            logger.info("当前获取队列数据总条数：" + objects.size());
            List<AlipayUserInfo> userUserAllToBankNot = alipayUserInfoService.findUserUserAllToBankNot();
            ConcurrentHashMap<String, AlipayUserInfo> userCollect = userUserAllToBankNot.stream().
                    collect(Collectors.toConcurrentMap(AlipayUserInfo::getUserId, Function.identity(), (o1, o2)
                            -> o1, ConcurrentHashMap::new));

            for (int i = 0; i < objects.size(); i++) {
                AlipayMediumEntity med = new AlipayMediumEntity();
                JSONObject queue = objects.getJSONObject(i);
                String bankId = queue.getStr("bankId");//卡号
                String score = queue.getStr("score");//排序
                String gourp = queue.getStr("gourp");//分组
                String bankName = queue.getStr("bankName");// 银行名字
                String userId = queue.getStr("userId");// 银行名字
                String bankAccount = queue.getStr("bankAccount");// 开户人姓名
                String amount = queue.getStr("amount");// 参考余额
                String startFund = queue.getStr("startFund");// 起始接单金额
                String deposit = queue.getStr("deposit");// 押金
                String fund = queue.getStr("fund");// 滚动
                String freezeBalance = queue.getStr("freezeBalance");//接单账户冻结

                med = medList.stream().filter(e -> e.getMediumNumber().equals(bankId)).findFirst().get();
                //med = com.alibaba.fastjson.JSONObject.parseObject(JSONUtil.toJsonStr(queue),AlipayMediumEntity.class);

                med.setDeposit(deposit);
                med.setStartFund(startFund);
                med.setFund(fund);
                med.setFreezeBalance(freezeBalance);
                med.setMediumId(gourp);
                med.setMountLimit(score);
                med.setQrcodeId(userId);
                med.setMediumNote(bankName + ":" + bankAccount + ":参考余额:" + amount);
                med.setMediumNumber(bankId);
                AlipayMediumEntity mediumEntity = collect.get(bankId);
                try {
                    if (startFund.contains("配额")) {
                        startFund = "500";
                    }
                    if ((Double.valueOf(fund) - Double.valueOf(freezeBalance)) < Double.valueOf(startFund)) {
                        med.setIsRed(1);
                    }

                   /* if (Double.valueOf(fund) > (Double.valueOf(deposit) + 3000)) {
                        med.setIsRed(1);
                    }*/
                    if (Double.valueOf(fund) < Double.valueOf(freezeBalance)) {
                        med.setIsRed(1);
                    }
                    med.setCode("");
                    if (ObjectUtil.isNotNull(mediumEntity)) {
                        String error = mediumEntity.getError();
                        if ("1".equals(error)) {
                            med.setIsRed(1);
                            med.setCode("异常卡");
                        }
                        if (new BigDecimal(fund).compareTo(new BigDecimal(500)) < 0) {
                            med.setIsRed(1);
                            med.setCode("需要出款 - ");
                        }
                        BigDecimal toDayDeal = mediumEntity.getToDayDeal();
                        BigDecimal sumAmounlimit = mediumEntity.getSumAmounlimit();
                        if (sumAmounlimit.compareTo(toDayDeal) < 0) {
                            String mediumNote = med.getCode();
                            if (StrUtil.isEmpty(mediumNote)) {
                                mediumNote = "";
                            }
                            mediumNote += "  当前卡限制入款 - ";
                            med.setCode(mediumNote);
                            med.setIsRed(1);
                        }
                        med.setToDayDeal(toDayDeal);
                        med.setToDayWit(mediumEntity.getToDayWit());
                        String black = mediumEntity.getBlack();
                        if (null != mediumEntity.getStartAmount()) {
                            med.setStartFund(mediumEntity.getStartAmount().toString());
                        }
                        if ("0".equals(black)) {
                            String mediumNote = med.getCode();
                            if (StrUtil.isEmpty(mediumNote)) {
                                mediumNote = "";
                            }
                            mediumNote += " 不让接单 - ";
                            med.setCode(mediumNote);
                            med.setIsRed(1);
                        }
                        //冲正风控
                        if ("6".equals(error)) {
                            med.setCode(med.getCode() + " 风控");
                        }
                    }
                } catch (Exception c) {
                    logger.error("exception:", c);
                }
                if (CollectionUtil.isNotEmpty(userCollect)) {
                    AlipayUserInfo alipayUserInfo = userCollect.get(userId);
                    if (null != alipayUserInfo) {
                        Integer switchs = alipayUserInfo.getSwitchs();//1  开启
                        Integer receiveOrderState = alipayUserInfo.getReceiveOrderState();// 2 暂停接单
                        Integer remitOrderState = alipayUserInfo.getRemitOrderState();//2 暂停接单
                        String payInfo = "";
                        med.setCre(alipayUserInfo.getCredit().toString());
                        if (1 != switchs) {
                            payInfo += "主关闭:";
                            med.setIsRed(1);
                        }
                        if (1 == receiveOrderState) {
                            payInfo += "接单开启:";
                        } else {
                            payInfo += "接单关闭:";
                            med.setIsRed(1);
                        }
                        if (1 == remitOrderState) {
                            payInfo += "代付开启";
                        } else {
                            payInfo += "代付关闭";
                        }
                        med.setPayInfo(payInfo);
                    }
                }


                listQueue.add(med);
            }
        } catch (Exception x) {
        }
        return getDataTable(listQueue);

    }


    @PostMapping("/getBankcard")
    @ResponseBody
    public AjaxResult showCodeList(AlipayUserInfo userInfo) {
        AlipayMediumEntity alipayMediumEntity = new AlipayMediumEntity();
        String userId = UUID.randomUUID().toString();
        if (StrUtil.isNotEmpty(userInfo.getUserId())) {
            userId = userInfo.getUserId();
        }
        alipayMediumEntity.setQrcodeId(userId);
        List<AlipayMediumEntity> list = alipayMediumEntityService.selectAlipayMediumEntityList(alipayMediumEntity);
        AlipayMediumEntity mediumEntity = alipayMediumEntityService.findBankSum(userId);
        if (null != mediumEntity && CollUtil.isNotEmpty(list)) {
            for (int mark = 0; mark < 1; mark++) {
                list.get(mark).setBankSumAmountsys(mediumEntity.getBankSumAmountsys());
                list.get(mark).setBankSumAmountnow(mediumEntity.getBankSumAmountnow());
                list.get(mark).setOpenSumBankAmountsys(mediumEntity.getOpenSumBankAmountsys());
                list.get(mark).setOpenSumBankAmountnow(mediumEntity.getOpenSumBankAmountnow());
            }
        }
        return AjaxResult.success(list);
    }

    /**
     * 码商状态修改（调用api）
     */
    @Log(title = "修改银行卡属性", businessType = BusinessType.UPDATE)
    @PostMapping("/updateBankcard")
    @ResponseBody
    public AjaxResult changeStatus(AlipayMediumEntity alipayMediumEntity) {
        logger.info("[当前处理商户关闭或者开启的管理员账号为：" + ShiroUtils.getSysUser().getLoginName() + "]");
        AlipayMediumEntity mediumEntity = new AlipayMediumEntity();
        mediumEntity.setId(alipayMediumEntity.getId());
        mediumEntity.setBlack(alipayMediumEntity.getBlack());
        mediumEntity.setStatus(alipayMediumEntity.getStatus());
        mediumEntity.setIsClickPay(alipayMediumEntity.getIsClickPay());
        mediumEntity.setAutowitstatus(alipayMediumEntity.getAutowitstatus());
        int i = alipayMediumEntityService.updateAlipayMediumEntity(mediumEntity);
        return toAjax(i);
    }

    @Log(title = "回调户名验证", businessType = BusinessType.UPDATE)
    @PostMapping("/updateBankcardToName")
    @ResponseBody
    public AjaxResult changeIsNameStatus(AlipayMediumEntity alipayMediumEntity) {
        logger.info("[修改银行卡户名验证：" + ShiroUtils.getSysUser().getLoginName() + "]");
        AlipayMediumEntity mediumEntity = new AlipayMediumEntity();
        mediumEntity.setId(alipayMediumEntity.getId());
        mediumEntity.setIsClickPay(alipayMediumEntity.getIsClickPay());
        int i = alipayMediumEntityService.updateAlipayMediumEntity(mediumEntity);
        return toAjax(i);
    }

    @Log(title = "释放银行卡", businessType = BusinessType.UPDATE)
    @PostMapping("/openbank")
    @ResponseBody
    public AjaxResult openbank(AlipayMediumEntity alipayMediumEntity) {
        logger.info("[释放银行卡：" + ShiroUtils.getSysUser().getLoginName() + "]");
        AlipayMediumEntity mediumEntity = new AlipayMediumEntity();
        mediumEntity.setId(alipayMediumEntity.getId());
        mediumEntity.setError("0");
        int i = alipayMediumEntityService.updateAlipayMediumEntity(mediumEntity);
        return toAjax(i);
    }

    @Log(title = "处理异常卡", businessType = BusinessType.UPDATE)
    @PostMapping("/offbank")
    @ResponseBody
    public AjaxResult offbank(AlipayMediumEntity alipayMediumEntity) {
        logger.info("[处理异常卡：" + ShiroUtils.getSysUser().getLoginName() + "]");
        AlipayMediumEntity mediumEntity = new AlipayMediumEntity();
        mediumEntity.setId(alipayMediumEntity.getId());
        mediumEntity.setError("1");
        int i = alipayMediumEntityService.updateAlipayMediumEntity(mediumEntity);
        return toAjax(i);
    }


}
