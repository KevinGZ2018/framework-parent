package com.fushun.framework.elasticsearch;

import com.fushun.framework.elasticsearch.enumeration.BaseElasticSearchIndexEnum;
import com.fushun.framework.elasticsearch.enumeration.BaseElasticSearchIndexTypeEnum;

/**
 * ES请求
 *
 * @author zhoup
 * @date 2016年5月21日
 */
public class ElasticSearchRequest {

    BaseElasticSearchIndexEnum index;
    BaseElasticSearchIndexTypeEnum type;
    String id;

    public String getIndex() {
        return index.getCode();
    }

    public void setIndex(BaseElasticSearchIndexEnum index) {
        this.index = index;
    }

    public String getType() {
        return type.getCode();
    }

    public void setType(BaseElasticSearchIndexTypeEnum type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
