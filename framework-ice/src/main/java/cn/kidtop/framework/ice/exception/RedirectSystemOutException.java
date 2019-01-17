package cn.kidtop.framework.ice.exception;


import cn.kidtop.framework.base.BaseExceptionEnum;

public class RedirectSystemOutException extends IceException {

    public RedirectSystemOutException(Throwable cause, Enum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    public static enum Enum implements BaseExceptionEnum {
        REDIRECT_SYSTEM_OUT_EXCEPTION(20002L, "io错误"),;

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
