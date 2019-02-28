package com.fushun.framework.ice.exception;

import com.fushun.framework.util.exception.base.BaseExceptionEnum;

public class ProcessInfoException extends IceException {

    public ProcessInfoException(Throwable cause, BaseExceptionEnum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    public static enum Enum implements BaseExceptionEnum {
        PROCESS_INFO_EXCEPTION(10002L, "线程错误"),;

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
