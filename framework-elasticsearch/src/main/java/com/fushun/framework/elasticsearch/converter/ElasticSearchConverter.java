package com.fushun.framework.elasticsearch.converter;

import java.util.Map;

/**
 * ElasticSerach转换
 *
 * @param <T>
 * @author zhoup
 * @date 2016年8月14日
 */
public interface ElasticSearchConverter<T> {

    /**
     * 转换为XContentBuilder对象
     *
     * @param domain
     * @param xContentBuilder
     * @return
     * @author zhoup
     */
    void convertFromCMP(T domain, Map<String, Object> xContentBuilder);


}
