package com.ruoyi.web.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UpdateWitCardDealerEvent extends ApplicationEvent {

    private UpdateWitCardDealerEventSource source;

    public UpdateWitCardDealerEvent(UpdateWitCardDealerEventSource source) {
        super(source);
        this.source = source;
    }



}
