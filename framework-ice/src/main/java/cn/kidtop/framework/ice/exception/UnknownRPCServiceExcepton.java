package cn.kidtop.framework.ice.exception;

import cn.kidtop.framework.base.BaseExceptionEnum;

public class UnknownRPCServiceExcepton extends IceException {

    public UnknownRPCServiceExcepton(Throwable cause, BaseExceptionEnum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    public UnknownRPCServiceExcepton(BaseExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    public static enum Enum implements BaseExceptionEnum {
        UNKNOWN_EXCEPTION(30002L, "类不存"),;

        private Long code;

        private String text;

        Enum(Long code, String text) {
            this.code = code;
            this.text = text;
        }

        public Long getCode() {
            return code;
        }

        public void setCode(Long code) {
            this.code = code;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }
}
