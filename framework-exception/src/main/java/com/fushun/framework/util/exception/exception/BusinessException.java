package com.fushun.framework.util.exception.exception;


import com.fushun.framework.util.exception.base.BaseCustomizeMessageExceptionEnum;
import com.fushun.framework.util.exception.base.BaseExceptionEnum;

/**
 * @author Administrator
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BusinessException(BusinessExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    public BusinessException(String message, BusinessCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        super(message, baseCustomizeMessageExceptionEnum);
    }

    public BusinessException(String message, Throwable cause, BusinessCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        super(message, cause, baseCustomizeMessageExceptionEnum);
    }

    public BusinessException(Throwable cause, BusinessExceptionEnum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    @Override
    protected long getBaseNo() {
        return 1;
    }

    public enum Enum implements BusinessExceptionEnum {
        BASECODE_EXCEPTION(2L, "基础错误"),
        EXPRESSION_IS_NULL_EXCEPTION(3L, "表达式为NULL"),
        EXPRESSION_IS_NOT_NULL_EXCEPTION(4L, "表达式不为NULL"),
        EXPRESSION_IS_NOT_EQUAL_ZERO_EXCEPTION(5L, "表达式不为0"),
        EXPRESSION_IS_EQUAL_ZERO_EXCEPTION(6L, "表达式等于0"),
        EXPRESSION_IS_FALSE_EXCEPTION(7L, "表达式为false"),
        CREATED_QRCODE_ERROR_EXCEPTION(8L, "生成二维码错误！"),
        DATA_ERROR_EXCEPTION(9L, "数据异常"),
        PARAMS_EXCEPTION(10L, "参数异常"),;

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

    ;

    public enum CustomizeMessageEnum implements BusinessCustomizeMessageExceptionEnum {
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

    ;

    public interface BusinessExceptionEnum extends BaseExceptionEnum {
    }

    public interface BusinessCustomizeMessageExceptionEnum extends BaseCustomizeMessageExceptionEnum {
    }
}
