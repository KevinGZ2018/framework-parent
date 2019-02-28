package com.fushun.framework.filestorage.qiniu;


import com.fushun.framework.util.exception.base.BaseExceptionEnum;
import com.fushun.framework.util.exception.exception.BaseException;

public class QiNiuExceptin extends BaseException {

    private static final long serialVersionUID = 1L;

    public QiNiuExceptin(Enum baseExceptionEnum) {
        super(baseExceptionEnum);
    }

    @Override
    protected long getBaseNo() {
        return 7;
    }


    public static enum Enum implements BaseExceptionEnum {
        UPLOAD_IMAGE_ERROR_EXCEPTION(2L, "上传图片错误"),
        GET_FILE_INFO_EXCEPTION(3L, "获取文件信息错误");

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
