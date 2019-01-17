/**
 *
 */
package cn.kidtop.framework.exception;

import cn.kidtop.framework.base.BaseExceptionEnum;

public class SystemException extends BaseException {

    public SystemException(BaseExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    @Override
    protected long getBaseNo() {
        return 6;
    }


    public static enum Enum implements BaseExceptionEnum {
        REQUEST_EXCEPTION(1L, "请求错误"),
        RESPONSE_EXCEPTION(2L, "输出错误"),
        PERSIST_EXCEPTION(3L, "unable to inject id"),
        JMS_EXCEPTION(4L, "jms错误"),
        DATE_EXCEPTION(5L, "日期错误"),
        JAXB_EXCEPTION(6L, "jaxb错误"),
        TYPE_MISMATCH_EXCEPTION(7L, "类型匹配错误"),
        ENCRYPTION_ERROR_EXCEPTION(8L, "加密异常"),
        SERVICE_INVOCATION_ERROR_EXCEPTION(9L, "服务调用错误"),
        REFLECTION_EXCEPTION(10L, "反射错误"),
        DEEPCLONE_EXCEPTION(11L, "深度克隆错误"),
        PROPERTIE_LOAD_EXCEPTION(12L, "属性加载异常"),
        INCONSISTENCE_EXCEPTION(13L, "不一致异常"),
        TEMPLATE_PARSE_EXCEPTION(14L, "模板解析异常"),
        FILE_EXPORT_EXCEPTION(15L, "文件导出异常"),
        FUNCTION_NOT_SUPPORTED_EXCEPTION(16L, "function不支持异常"),
        COMPRESS_EXCEPTION(17L, "function不支持异常"),
        DATA_PARSE_EXCEPTION(18L, "日期解析异常"),
        UNSUPPORTED_CHARSET_EXCEPTION(19L, "function不支持异常"),
        NETWORK_EXCEPTION(20L, "网络异常,请稍后重试"),


        CUSTOMIZE_MESSAGE_EXCEPTION(999L, "自定义错误信息内容"),;


        private Long code;

        private String text;

        Enum(Long code, String text) {
            this.code = code;
            this.text = text;
        }

        public Long getIntCode() {
            return getCode();
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
