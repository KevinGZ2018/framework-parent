package cn.kidtop.framework.zookeeper.exception;import cn.kidtop.framework.base.BaseCustomizeMessageExceptionEnum;import cn.kidtop.framework.base.BaseExceptionEnum;import cn.kidtop.framework.exception.BaseException;import cn.kidtop.framework.exception.BusinessException;/** * @author zhoup * @date 2016年6月17日 */public class ZkClientException extends BaseException {    public static final long ZK_CLIENT_FAILED = 180101;    private static final long serialVersionUID = 1L;    public ZkClientException(BaseExceptionEnum baseExceptionEnum) {        super(baseExceptionEnum);    }    public ZkClientException(String message, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {        super(message, baseCustomizeMessageExceptionEnum);    }    public ZkClientException(String message, Throwable cause, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {        super(message, cause, baseCustomizeMessageExceptionEnum);    }    public ZkClientException(Throwable cause, BaseExceptionEnum baseExceptionEnum) {        super(cause, baseExceptionEnum);    }    @Override    protected long getBaseNo() {        return 5;    }    public enum CustomizeMessageEnum implements BusinessException.BusinessCustomizeMessageExceptionEnum {        BASECODE_EXCEPTION(1L, "自定义错误信息异常");        private Long code;        private String text;        CustomizeMessageEnum(Long code, String text) {            this.code = code;            this.text = text;        }        public Long getCode() {            return code;        }        public void setCode(Long code) {            this.code = code;        }        public String getText() {            return text;        }        public void setText(String text) {            this.text = text;        }        @Override        public String toString() {            return text;        }    }}