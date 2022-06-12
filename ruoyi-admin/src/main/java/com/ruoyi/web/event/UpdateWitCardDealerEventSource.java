package com.ruoyi.web.event;

import com.ruoyi.common.core.domain.BaseProtocol;
import com.ruoyi.common.core.domain.CmdKey;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
//@Builder
/**
 * 修改出款卡商事件
 */
public class UpdateWitCardDealerEventSource extends BaseProtocol {

    private String operateUserId;//操作人
    private String lockWitStatus;//新的解锁状态
    private String orderId;//订单号
    private String message;

    public UpdateWitCardDealerEventSource() {
        setCmdKey(CmdKey.RELEASE_LOCKWIT);
    }

    public static UpdateWitCardDealerEventSource of(String orderId,  String operateUserId) {
        UpdateWitCardDealerEventSource source = new UpdateWitCardDealerEventSource();
        source.setOrderId(orderId);
        source.setOperateUserId(operateUserId);
        return source;
    }
    //推给前端的信息
    public String getMessage() {
        return String.format("%s已经修改出款卡商。订单号：%s", getOperateUserId(), getOrderId(), getLockWitStatus());
    }

    public static void main(String[] args) {
        //System.out.println(JSONUtil.toJsonStr(UpdateLockWitEventSource.builder()));;
    }
}
