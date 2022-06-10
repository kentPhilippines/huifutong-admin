package com.ruoyi.web.event;

import com.ruoyi.common.core.domain.BaseProtocol;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UpdateLockWitEvent extends ApplicationEvent {

    private UpdateLockWitEventSource source;

    public UpdateLockWitEvent(UpdateLockWitEventSource source) {
        super(source);
        this.source = source;
    }



}
