package com.fushun.framework.filestorage.qiniu;

/**
 * 七牛 配置
 *
 * @version 1.0
 * @date: 2017-09-15 16:04:47
 * @author:wangfushun
 */
public class QiNiuConfig {
    private static QiNiuConfig qiNiuConfig = new QiNiuConfig();
    /**
     * key
     */
    private String accessKey;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 存储空间
     */
    private String bucket;
    /**
     * 图片前缀url地址
     */
    private String imagePreUrl;
    /**
     * 图片瘦身后缀(没有大小)
     */
    private String imageAfterUrl2;
    /**
     * 图片瘦身后缀
     */
    private String imageAfterUrl;
    ;

    private QiNiuConfig() {
    }

    public static QiNiuConfig init() {
        return qiNiuConfig;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getImagePreUrl() {
        return imagePreUrl;
    }

    public void setImagePreUrl(String imagePreUrl) {
        this.imagePreUrl = imagePreUrl;
    }

    @Override
    public String toString() {
        return "QiNiuConfig [accessKey=" + accessKey + ", secretKey=" + secretKey + ", bucket=" + bucket
                + ", imagePreUrl=" + imagePreUrl + "]";
    }

    public String getImageAfterUrl() {
        return imageAfterUrl;
    }

    public void setImageAfterUrl(String imageAfterUrl) {
        this.imageAfterUrl = imageAfterUrl;
    }

    public String getImageAfterUrl2() {
        return imageAfterUrl2;
    }

    public void setImageAfterUrl2(String imageAfterUrl2) {
        this.imageAfterUrl2 = imageAfterUrl2;
    }


}
