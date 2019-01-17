package cn.kidtop.framework.elasticsearch.exception;


import cn.kidtop.framework.base.BaseCustomizeMessageExceptionEnum;
import cn.kidtop.framework.base.BaseExceptionEnum;
import cn.kidtop.framework.exception.BaseException;

public class ElasticSearchException extends BaseException {
    private static final long serialVersionUID = -6947877146278453705L;

    public ElasticSearchException(BaseExceptionEnum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    public ElasticSearchException(String message, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        super(message, baseCustomizeMessageExceptionEnum);
    }

    public ElasticSearchException(String message, Throwable cause, BaseCustomizeMessageExceptionEnum baseCustomizeMessageExceptionEnum) {
        super(message, cause, baseCustomizeMessageExceptionEnum);
    }

    public ElasticSearchException(Throwable cause, BaseExceptionEnum baseExceptionEnum) {
        super(cause, baseExceptionEnum);
    }

    @Override
    protected long getBaseNo() {
        return 2;
    }

    public static enum Enum implements BaseExceptionEnum {
        ELASTIC_SEARCH_EXCEPTION(2L, "搜索错误"),
        CREATE_DOCUMENT_EXCEPTION(3L, "创建文档错误"),
        UPDATE_DOCUMENT_EXCEPTION(4L, "更新文档错误"),
        DELETE_DOCUMENT_EXCEPTION(5L, "删除文档错误"),
        UNKONW(6L, "未知错误"),;

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
