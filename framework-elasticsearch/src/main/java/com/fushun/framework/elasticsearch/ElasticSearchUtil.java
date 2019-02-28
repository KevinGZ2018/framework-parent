package com.fushun.framework.elasticsearch;

import com.fushun.framework.util.exception.base.SpringContextUtil;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * ES工具类
 *
 * @author zhoup
 * @date 2016年5月21日
 */
public class ElasticSearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchUtil.class);

    private static ElasticSearchConfig elasticSearchConfig = SpringContextUtil.getBean(ElasticSearchConfig.class);

    private static volatile TransportClient transportClient = null;

    private static TransportClient getTransportClient(String cluster, String host, int port) throws UnknownHostException {
        if (transportClient == null) {
            try {
                synchronized (ElasticSearchUtil.class) {
                    cluster = cluster == null ? elasticSearchConfig.getCLUSTER_NAME() : cluster;
                    host = host == null ? elasticSearchConfig.getELASTIC_SERVER_HOST() : host;
                    port = port == 0 ? elasticSearchConfig.getELASTIC_SERVER_PORT() : port;

                    Settings settings = Settings.settingsBuilder().put(ElasticConstants.CLUSTER_NAME, cluster).build();
                    transportClient = TransportClient.builder().settings(settings).build()
                            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
                    return transportClient;
                }
            } catch (Exception e) {
                logger.error("getTransportClient", e);
            }
        }
        return transportClient;
    }

    public static Client getClient() {
        try {
            return getTransportClient(elasticSearchConfig.getCLUSTER_NAME(),
                    elasticSearchConfig.getELASTIC_SERVER_HOST(), elasticSearchConfig.getELASTIC_INDEX_PORT());
        } catch (Exception e) {
            logger.error("getClient", e);
        }
        return null;
    }

    public List<SearchResponse> getAllMultiResponseHits(MultiSearchResponse MultiSearchResponse) {
        try {
            List<SearchResponse> result = new ArrayList<SearchResponse>();
            for (MultiSearchResponse.Item item : MultiSearchResponse.getResponses()) {
                SearchResponse response = item.getResponse();
                result.add(response);
            }
            return result;
        } catch (Exception e) {
            logger.error("", e);
        }

        return null;
    }

}
