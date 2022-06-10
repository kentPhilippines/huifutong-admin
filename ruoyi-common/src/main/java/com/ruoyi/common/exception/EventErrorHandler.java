package com.ruoyi.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;


public class EventErrorHandler implements ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(EventErrorHandler.class);
    @Override
    public void handleError(Throwable throwable) {
        log.error("事件失败了, error message : " + throwable.getMessage());
    }
}
