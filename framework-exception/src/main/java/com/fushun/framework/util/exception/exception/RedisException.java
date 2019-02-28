package com.fushun.framework.util.exception.exception;

import com.fushun.framework.util.exception.base.BaseCustomizeMessageExceptionEnum;
import com.fushun.framework.util.exception.base.BaseExceptionEnum;

/**
 * redis 错误异常
 *
 * @verson 1.0
 * @return a
 * @date: 2018年12月16日18时03分
 * @author:wangfushun
 */
public class RedisException extends BaseException {
    public static final long BASECODE_EXCEPTION = 180101;

    public RedisException(BaseExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    @Override
    protected long getBaseNo() {
        return 3;
    }


    public static enum Enum implements BaseExceptionEnum {
        BASECODE_EXCEPTION(2L, "基础错误"),;

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

    public static enum CustomizeMessageEnum implements BaseCustomizeMessageExceptionEnum {
        BASECODE_EXCEPTION(1L, "自定义错误信息异常");

        private Long code;

        private String text;

        CustomizeMessageEnum(Long code, String text) {
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
