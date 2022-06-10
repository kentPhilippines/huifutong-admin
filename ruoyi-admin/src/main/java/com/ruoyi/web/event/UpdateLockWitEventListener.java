package com.ruoyi.web.event;

import com.ruoyi.framework.websocket.WebSocketServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class UpdateLockWitEventListener {

    @EventListener(value = UpdateLockWitEvent.class)
    public void listen(UpdateLockWitEvent event) throws IOException {
        WebSocketServer.sendInfo(event.getSource(),null);
        log.info("listener1线程:{}", Thread.currentThread().getThreadGroup() + Thread.currentThread().getName());
        log.info("event:{}", event);
    }
}
