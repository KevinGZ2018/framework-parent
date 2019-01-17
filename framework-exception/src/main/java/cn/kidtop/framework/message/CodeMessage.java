package cn.kidtop.framework.message;

public interface CodeMessage {

    public String getMessageByCodeNo(Long code);

    public String getMessageForRedis(Long code);
}
