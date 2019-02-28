package com.fushun.framework.elasticsearch;


import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
    private String CLUSTER_NAME = "";
    private String ELASTIC_SERVER_HOST = "";
    private int ELASTIC_SERVER_PORT = 0;
    private int ELASTIC_INDEX_PORT = 0;

    public String getCLUSTER_NAME() {
        return CLUSTER_NAME;
    }

    public void setCLUSTER_NAME(String cLUSTER_NAME) {
        CLUSTER_NAME = cLUSTER_NAME;
    }

    public String getELASTIC_SERVER_HOST() {
        return ELASTIC_SERVER_HOST;
    }

    public void setELASTIC_SERVER_HOST(String eLASTIC_SERVER_HOST) {
        ELASTIC_SERVER_HOST = eLASTIC_SERVER_HOST;
    }

    public int getELASTIC_SERVER_PORT() {
        return ELASTIC_SERVER_PORT;
    }

    public void setELASTIC_SERVER_PORT(int eLASTIC_SERVER_PORT) {
        ELASTIC_SERVER_PORT = eLASTIC_SERVER_PORT;
    }

    public int getELASTIC_INDEX_PORT() {
        return ELASTIC_INDEX_PORT;
    }

    public void setELASTIC_INDEX_PORT(int eLASTIC_INDEX_PORT) {
        ELASTIC_INDEX_PORT = eLASTIC_INDEX_PORT;
    }


}
