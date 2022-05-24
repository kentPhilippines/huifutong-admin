package com.ruoyi.web.controller.alipay;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.UserInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.alipay.domain.AlipayCorrelation;
import com.ruoyi.alipay.domain.AlipayUserFundEntity;
import com.ruoyi.alipay.domain.AlipayUserInfo;
import com.ruoyi.alipay.domain.AlipayUserRateEntity;
import com.ruoyi.alipay.service.*;
import com.ruoyi.alipay.vo.UserCountBean;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.result.Result;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.GoogleUtils;
import com.ruoyi.framework.util.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/merchant/agent")
@Controller
public class AgentContorller extends BaseController {
    private static final String AMOUNT_TYPE_APP = "3";//商户主动申请补单订单类型
    private String prefix = "merchant/agent";
    private String prefixRate = "merchant/rate";
    private String prefixFransfer = "merchant/transfer";

    @Autowired
    private SysPasswordService passwordService;
    @Autowired
    private GoogleUtils googleUtils;

    @Autowired
    private IAlipayUserInfoService userInfoService;

    @Autowired
    private IAlipayCorrelationService correlationService;

    @Autowired
    private IAlipayUserFundEntityService alipayUserFundEntityService;

    @Autowired
    private IAlipayUserRateEntityService userRateEntityService;

    //@RequiresPermissions("system:dept:view")
    @GetMapping()
    public String dept() {
        return prefix + "/agent";
    }
    /**
     * 查询补单记录
     */
    @PostMapping("/list")
    @ResponseBody
    public List<AlipayCorrelation> list(AlipayCorrelation alipayCorrelation) {
//        alipayAmountEntity.setUserId(ShiroUtils.getSysUser().getMerchantId());//商户交易账号
//        alipayAmountEntity.setAmountType(AMOUNT_TYPE_APP);//商户补单
        startPage();
        if(StringUtils.isEmpty(alipayCorrelation.getParentName())) {
            alipayCorrelation.setParentName(ShiroUtils.getSysUser().getLoginName());
        }
        List<AlipayCorrelation> list = correlationService.selectAlipayCorrelationList(alipayCorrelation);
        list.stream().map(entity->{
            //if(entity.getDistance()!=null && ( entity.getDistance()==0 || entity.getParentName().equals(alipayCorrelation.getParentName())))
            if(entity.getDistance()!=null && entity.getDistance()==0 )
            {
                entity.setParentName(null);
            }
            String userId = entity.getChildrenName();

            List<AlipayUserRateEntity> chargeRates= userRateEntityService.findMerchantChargeRate(userId);
            entity.setChargeRates(chargeRates);
            List<AlipayUserRateEntity> witRates = userRateEntityService.findMerchantWithdralRate(userId);
            entity.setWithdralRates(witRates);

            AlipayUserFundEntity fundEntity= alipayUserFundEntityService.findAlipayUserFundByUserId(userId);
            Optional.ofNullable(fundEntity).ifPresent(fund->{
                if (fund.getTodayProfit() != null) {
                    entity.setTodayProfit(BigDecimal.valueOf(fund.getTodayProfit()));
                } else {
                    entity.setTodayProfit(BigDecimal.ZERO);
                }
                if (fund.getAccountBalance() != null) {
                    entity.setAccountBalance(BigDecimal.valueOf(fund.getAccountBalance()));
                } else {
                    entity.setAccountBalance(BigDecimal.ZERO);
                }
            });

            return entity;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * <p>查询自己的子账户</p>
     * 	手机端专用
     * @return
     */
    @GetMapping("/findLowerLevelAccountDetailsInfoByPage")
    @ResponseBody
    public Result findLowerLevelAccountDetailsInfoByPage(@RequestParam(required = false)String pageSize,
                                                         @RequestParam(required = false)String pageNum,
                                                         @RequestParam(required = false)String userName,
                                                         HttpServletRequest request) {
        return  null;
        /*UserInfo user2 = sessionUtil.getUser(request);
        if (StrUtil.isBlank(user2.getUserId())) {
            return Result.buildFail();
        }
        UserInfo user = new UserInfo();
        if (StrUtil.isNotBlank(userName)) {
            user.setUserId(userName);
        }
        user.setAgent(user2.getUserId());
        PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
        List<UserInfo> userList = userInfoService.findSunAccount(user);
        UserRate userRate = null;
        UserRate userRateW = null;
        UserFund userfund = null;
        for (UserInfo qrUser : userList) {
            findOnline(qrUser);
            userRate = userRateService.findUserRateR(qrUser.getUserId());
            userRateW = userRateService.findUserRateW(qrUser.getUserId());
            qrUser.setFee(userRate.getFee() + "");
            qrUser.setCardFee(userRateW.getFee() + "");
            userfund = userFundService.findUserInfoByUserId(qrUser.getUserId());
            qrUser.setRechargeNumber(userfund.getAccountBalance());
            qrUser.setCashBalance(userfund.getTodayProfit());
        }
        PageInfo<UserInfo> pageInfo = new PageInfo<UserInfo>(userList);
        PageResult<UserInfo> pageR = new PageResult<UserInfo>();
        pageR.setContent(pageInfo.getList());
        pageR.setPageNum(pageInfo.getPageNum());
        pageR.setTotal(pageInfo.getTotal());
        pageR.setTotalPage(pageInfo.getPages());
        return Result.buildSuccessResult(pageR);*/
    }
    @GetMapping("/findAgentCount")
    @ResponseBody
    public Result findAgentCount(HttpServletRequest request) {
        /*UserInfo user2 = sessionUtil.getUser(request);
        if (ObjectUtil.isNull(user2)) {
            throw new OtherErrors("当前用户未登录");
        }*/
        String userId = ShiroUtils.getSysUser().getLoginName();

        AlipayUserFundEntity findUserByAccount = alipayUserFundEntityService.findAlipayUserFundByUserId(userId);
        UserCountBean findMoreCount = findMyDate(findUserByAccount.getUserId());
        findMoreCount.setMoreDealProfit(findUserByAccount.getTodayAgentProfit().toString());
        return Result.buildSuccessResult(findMoreCount);
    }

    /**
     * <p>根据我的账户id，查询我的个人数据情况</p>
     * @param id
     */
    private UserCountBean findMyDate(@NotNull String userId) {
        UserCountBean bean = correlationService.findMyDateAgen(userId);
        UserCountBean bean1 = correlationService.findDealDate(userId);
        if(ObjectUtil.isNotNull(bean1)) {
            bean.setMoreAmountRunR(ObjectUtil.isNull(bean1.getMoreAmountRunR()) ? new BigDecimal("0") : bean1.getMoreAmountRunR());
            bean.setMoreAmountRunW(ObjectUtil.isNull(bean1.getMoreAmountRunW()) ? new BigDecimal("0") : bean1.getMoreAmountRunW());
            bean.setMoreDealCount(ObjectUtil.isNull(bean1.getMoreDealCount()) ? 0 : bean1.getMoreDealCount());
        } else {
            bean.setMoreAmountRunR(new BigDecimal("0"));
            bean.setMoreAmountRunW(new BigDecimal("0"));
            bean.setMoreDealCount(0);
        }
        return bean;
    }
    /*UserInfo findOnline(UserInfo user) {
        if (Common.User.USER_IS_AGENT.toString().equals(user.getIsAgent())) {
            int[][] dataArray = correlationServiceImpl.findOnline(user.getUserId());
            user.setOnline(dataArray[1][0]+"");
            user.setToDayOrderCount(dataArray[0][0]);
            user.setAgentCount(dataArray[2][0]+"");
        }
        return user;
    }*/

}
