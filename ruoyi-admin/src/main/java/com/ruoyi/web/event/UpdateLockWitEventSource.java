package com.ruoyi.web.event;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.BaseProtocol;
import com.ruoyi.common.core.domain.CmdKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
//@Builder
/**
 * 解锁出款订单
 */
public class UpdateLockWitEventSource extends BaseProtocol {

    private String operateUserId;//操作人
    private String lockWitStatus;//新的解锁状态
    private String orderId;//订单号
    private String message;

    public UpdateLockWitEventSource() {
        setCmdKey(CmdKey.RELEASE_LOCKWIT);
    }

    public static UpdateLockWitEventSource of(String orderId,String lockWitStatus,String operateUserId) {
        UpdateLockWitEventSource updateLockWitEventSource = new UpdateLockWitEventSource();
        updateLockWitEventSource.setOrderId(orderId);
        updateLockWitEventSource.setLockWitStatus(lockWitStatus);
        updateLockWitEventSource.setOperateUserId(operateUserId);
        return updateLockWitEventSource;
    }
    //推给前端的信息
    public String getMessage() {
        //0是解锁订单状态
        if ( "0".equals(getLockWitStatus())) {
            return String.format("%s已经解锁出款订单%s", getOperateUserId(), getOrderId(), getLockWitStatus());
        } else {
            return "";
        }
    }

    public static void main(String[] args) {
        //System.out.println(JSONUtil.toJsonStr(UpdateLockWitEventSource.builder()));;
    }
}
