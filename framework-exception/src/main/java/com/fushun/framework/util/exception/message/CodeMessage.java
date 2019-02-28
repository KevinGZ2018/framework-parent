package com.fushun.framework.util.exception.message;

public interface CodeMessage {

    public String getMessageByCodeNo(Long code);

    public String getMessageForRedis(Long code);
}
