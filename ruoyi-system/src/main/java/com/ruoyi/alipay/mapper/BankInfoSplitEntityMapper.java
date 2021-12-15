package com.ruoyi.alipay.mapper;

import com.ruoyi.alipay.domain.BankInfoSplitEntity;
import com.ruoyi.alipay.domain.BankTransactionRecord;
import com.ruoyi.alipay.domain.util.BankRunInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 */
public interface BankInfoSplitEntityMapper {


    /**
     * 新增截取银行流水信息数据
     *
     * @param bankInfoSplitEntity
     * @return 结果
     */
    List<BankInfoSplitEntity> selectBankInfoSplitEntity(BankInfoSplitEntity bankInfoSplitEntity);

    /**
     * 查询流水收入支出记录
     *
     * @param bankInfoSplitEntity
     * @return 结果
     */
    List<BankTransactionRecord> selectBankTransactionRecord(BankInfoSplitEntity bankInfoSplitEntity);





    @Select("<script>" +
            " SELECT  ado.orderId   as orderId ,   ado.orderQrUser as userId ," +
            "     ( CASE ado.orderType  when   4  then '出款' ELSE  '入款' end  ) as  runType , " +
            "(aro.amountNow  +ado.bankAmountNow ) as number , " +
            "              ( CASE ado.orderType  when   4  then   concat(aw.accname,concat(aw.bankNo,aw.bankName)) ELSE  adoa.dealDescribe  end  ) as  settl ,   " +
            "              aro.amountNow  as  amount , " +
            "   ado.dealAmount as dealAmount ,  " +
            "   ado.bankAmountNow as bankNow  , " +
            "   ado.orderQr as bankNo , " +
            "   ado.systemAmount as   bAmount, " +
            "   (ado.systemAmount - ado.bankAmountNow )  as  suAmount , " +
            "   ado.createTime    as createTime , " +
            "   ado.submitTime  as submitTime , " +
            "   ado.payInfo    as msg " +
            "               " +
            "              FROM  alipay_deal_order ado     " +
            "              LEFT JOIN   alipay_run_order aro  on aro.associatedId  = ado.orderId  " +
            "               LEFT JOIN  alipay_deal_order_app adoa   on adoa.orderId  = ado.associatedId  " +
            "                LEFT JOIN   alipay_withdraw aw   on aw.orderId  = ado.associatedId  " +
            "              WHERE  " +
            "          ado.orderStatus  = 2     " +
            " <if test = \"bankRun.userId != null and bankRun.userId != ''\">" +
            "   and ado.orderQrUser = #{bankRun.userId} " +
            " </if>" +
            " <if test = \"bankRun.bankNo != null and bankRun.bankNo != ''\">" +
            "   and ado.orderQr like  concat('%',  #{bankRun.bankNo} , '%')  " +
            " </if>" +
            " <if test = \"bankRun.runType != null and bankRun.runType != ''  \" >" +
            "   and ado.orderType = #{bankRun.runType} " +
            " </if>" +
            " and   " +
            "              ( aro.runOrderType  = 12 or  aro.runOrderType  = 34)  and    ado.createTime between #{bankRun.params.dayStart} and #{bankRun.params.dayEnd}    order by ado.submitTime desc " +

            " </script>")
    List<BankRunInfo> findBankRunInfo(@Param("bankRun") BankRunInfo bankSplitEntity);
}
