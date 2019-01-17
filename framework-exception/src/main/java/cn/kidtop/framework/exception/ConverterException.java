package cn.kidtop.framework.exception;

/**
 * 支付通知异常
 *
 * @author fushun
 * @version dev706
 * @creation 2017年6月2日
 */
public class ConverterException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ConverterException(BusinessExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    public ConverterException(Throwable cause, BusinessExceptionEnum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    public enum Enum implements BusinessExceptionEnum {
        BASECODE_EXCEPTION(10002L, "基础错误"),
        CONVERTER_EXCEPTION(10003L, "转换错误");

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
