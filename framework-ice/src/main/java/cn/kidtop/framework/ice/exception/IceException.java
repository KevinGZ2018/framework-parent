package cn.kidtop.framework.ice.exception;

import cn.kidtop.framework.base.BaseCustomizeMessageExceptionEnum;
import cn.kidtop.framework.base.BaseExceptionEnum;
import cn.kidtop.framework.exception.BaseException;

/**
 * @author wangfushun
 * @version 1.0
 * @creation 2018年12月16日18时22分
 */
public class IceException extends BaseException {

    public IceException(BaseExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    public IceException(String message, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        super(message, baseCustomizeMessageExceptionEnum);
    }

    public IceException(String message, Throwable cause, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        super(message, cause, baseCustomizeMessageExceptionEnum);
    }

    public IceException(Throwable cause, BaseExceptionEnum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    @Override
    protected long getBaseNo() {
        return 4;
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
