/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.kidtop.framework.elasticsearch;

import cn.kidtop.framework.elasticsearch.enumeration.BaseEElasticSearchInnerName;
import cn.kidtop.framework.elasticsearch.enumeration.BaseElasticSearchIndexEnum;
import cn.kidtop.framework.elasticsearch.enumeration.BaseElasticSearchIndexTypeEnum;
import cn.kidtop.framework.elasticsearch.exception.ElasticSearchException;
import cn.kidtop.framework.util.BeanUtils;
import cn.kidtop.framework.util.CollectionUtils;
import cn.kidtop.framework.util.StringUtils;
import org.elasticsearch.action.ActionWriteResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.fetch.innerhits.InnerHitsBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ES查询服务
 *
 * @author zhoup
 * @date 2016年5月21日
 */
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    /**
     * 索引是否存在
     *
     * @param id
     * @return
     * @author zhoup
     */
    public boolean isIndexExist(String id) {
        try {
            if (ElasticSearchUtil.getClient().admin().indices().prepareExists(id).execute().actionGet().isExists()) {
                return true;
            }
        } catch (Exception exception) {
            if (exception instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("isIndexExist: index error", exception);
        }

        return false;
    }

    /**
     * 创建和更新文档
     *
     * @param index
     * @param type
     * @param id
     * @param jsonData
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月31日
     * @records <p>使用upsert 更新  fushun 2016年8月18日</p>
     */
    public ActionWriteResponse createOrUpdateDocument(BaseElasticSearchIndexEnum index,
                                                      BaseElasticSearchIndexTypeEnum type, String id, Map<String, Object> jsonData) {

        IndexRequest indexRequest = new IndexRequest(index.getCode(), type.getCode(), id).source(jsonData);
        UpdateRequest updateRequest = new UpdateRequest(index.getCode(), type.getCode(), id)
                .doc(jsonData)
                .upsert(indexRequest);
        UpdateResponse response = null;
        try {
            response = ElasticSearchUtil.getClient().update(updateRequest).get();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (StringUtils.isEmpty(response.getId())) {
            throw new ElasticSearchException(ElasticSearchException.Enum.UPDATE_DOCUMENT_EXCEPTION);
        }

        return response;
    }

    /**
     * 创建文档
     *
     * @param index
     * @param type
     * @param id
     * @param jsonData
     * @return
     * @author zhoup
     */
    public IndexResponse createDocument(BaseElasticSearchIndexEnum index,
                                        BaseElasticSearchIndexTypeEnum type, String id, Map<String, Object> jsonData) {
        IndexResponse response = null;
        try {
            response = ElasticSearchUtil.getClient()
                    .prepareIndex(index.getCode(), type.getCode(), id)
                    .setSource(jsonData).get();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (!response.isCreated()) {
            throw new ElasticSearchException(ElasticSearchException.Enum.CREATE_DOCUMENT_EXCEPTION);
        }

        return response;
    }

    /**
     * 更新文档
     *
     * @param index
     * @param type
     * @param id
     * @param jsonData
     * @return
     * @author zhoup
     */
    public UpdateResponse updateDocument(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                         String id, Map<String, Object> jsonData) {
        UpdateResponse response = null;
        try {
            response = ElasticSearchUtil.getClient().prepareUpdate(index.getCode(), type.getCode(), id)
                    .setDoc(jsonData).execute().get();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (StringUtils.isEmpty(response.getId())) {
            throw new ElasticSearchException(ElasticSearchException.Enum.UPDATE_DOCUMENT_EXCEPTION);
        }

        return response;
    }

    /**
     * 创建和更新文档,parent-child模式
     *
     * @param index
     * @param type
     * @param id
     * @param jsonData
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月31日
     * @records <p>使用upsert 更新  fushun 2016年8月18日</p>
     */
    public ActionWriteResponse createOrUpdateDocument(BaseElasticSearchIndexEnum index,
                                                      BaseElasticSearchIndexTypeEnum type, String id, String parentId, Map<String, Object> jsonData) {

        IndexRequest indexRequest = new IndexRequest(index.getCode(), type.getCode(), id).source(jsonData);
        UpdateRequest updateRequest = new UpdateRequest(index.getCode(), type.getCode(), id)
                .doc(jsonData)
                .upsert(indexRequest).parent(parentId);
        UpdateResponse response = null;

        try {
            response = ElasticSearchUtil.getClient().update(updateRequest).get();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (StringUtils.isEmpty(response.getId())) {
            throw new ElasticSearchException(ElasticSearchException.Enum.UPDATE_DOCUMENT_EXCEPTION);
        }

        return response;
    }

    /**
     * 创建子文档,parent-child模式
     *
     * @param index
     * @param type
     * @param id
     * @param parentId
     * @param jsonData
     * @return
     * @author zhoup
     */
    public IndexResponse createDocument(BaseElasticSearchIndexEnum index,
                                        BaseElasticSearchIndexTypeEnum type, String id, String parentId, Map<String, Object> jsonData) {
        IndexResponse response = null;
        try {
            response = ElasticSearchUtil.getClient()
                    .prepareIndex(index.getCode(), type.getCode(), id).setParent(parentId)
                    .setSource(jsonData).get();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (!response.isCreated()) {
            throw new ElasticSearchException(ElasticSearchException.Enum.CREATE_DOCUMENT_EXCEPTION);
        }

        return response;
    }

    /**
     * 更新子文档,parent-child模式
     *
     * @param index
     * @param type
     * @param id
     * @param jsonData
     * @return
     * @author zhoup
     */
    public UpdateResponse updateDocument(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                         String id, String parentId, Map<String, Object> jsonData) {
        UpdateResponse response = null;
        try {
            response = ElasticSearchUtil.getClient().prepareUpdate(index.getCode(), type.getCode(), id).setParent(parentId)
                    .setDoc(jsonData).execute().get();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (StringUtils.isEmpty(response.getId())) {
            throw new ElasticSearchException(ElasticSearchException.Enum.UPDATE_DOCUMENT_EXCEPTION);
        }

        return response;
    }


    /**
     * 移除文档
     *
     * @param index
     * @param type
     * @param id
     * @return
     * @author zhoup
     */
    public DeleteResponse removeDocument(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                         String id) {
        DeleteResponse response = null;
        try {
            response = ElasticSearchUtil.getClient().prepareDelete(index.getCode(), type.getCode(), id).execute()
                    .actionGet();
        } catch (Exception e) {
            throw new ElasticSearchException(e, ElasticSearchException.Enum.UNKONW);
        }
        if (response.isFound() == false) {
            throw new ElasticSearchException(ElasticSearchException.Enum.DELETE_DOCUMENT_EXCEPTION);
        }
        return response;
    }

    /**
     * 查找符合索引
     *
     * @param requests
     * @return
     * @author zhoup
     * @records <p>没有使用  fushun 2016年8月17日</p>
     */
    @Deprecated
    public MultiGetResponse findByMultipleIndexs(List<ElasticSearchRequest> requests) {
        try {
            MultiGetRequestBuilder builder = ElasticSearchUtil.getClient().prepareMultiGet();
            for (ElasticSearchRequest _request : requests) {
                builder.add(_request.getIndex(), _request.getType(), _request.getId());
            }
            return builder.get();
        } catch (Exception e) {
            if (e instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("findByMultipleIndexs", e);
        }
        return null;
    }

    /**
     * 获取所有数据
     *
     * @param multiGetResponse
     * @return
     * @author zhoup
     * @records <p>没有使用  fushun 2016年8月17日</p>
     */
    @Deprecated
    public List<String> getAlldata(MultiGetResponse multiGetResponse) {
        List<String> data = new ArrayList<>();
        try {
            for (MultiGetItemResponse itemResponse : multiGetResponse) {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists()) {
                    String json = response.getSourceAsString();
                    data.add(json);
                }
            }
            return data;
        } catch (Exception e) {
            if (e instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("", e);
        }
        return null;
    }

    /**
     * 根据主键和 parentId 获取文档
     *
     * @param index
     * @param type
     * @param id
     * @param parentId
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public GetResponse findDocumentByIndex(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                           String id, String parentId) {
        try {
            GetResponse getResponse = ElasticSearchUtil.getClient().prepareGet(index.getCode(), type.getCode(), id).setParent(parentId)
                    .get();
            return getResponse;
        } catch (Exception e) {
            if (e instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("", e);
        }
        return null;
    }

    /**
     * 根据主键Id 获取文档
     *
     * @param index
     * @param type
     * @param id
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public GetResponse findDocumentByIndex(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type, String id) {
        try {
            GetResponse getResponse = ElasticSearchUtil.getClient().prepareGet(index.getCode(), type.getCode(), id).get();
            return getResponse;
        } catch (Exception e) {
            if (e instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("", e);
        }
        return null;
    }


    /**
     * 获取查询query
     *
     * @param queryBuilder
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    private SearchRequestBuilder getSearchRequestBuilder(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type, QueryBuilder queryBuilder) throws Exception {
        return ElasticSearchUtil.getClient().prepareSearch(index.getCode())
                .setTypes(type.getCode())
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder);
    }


    /**
     * queryBuilder 查询文档
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type, QueryBuilder queryBuilder) {
        return findDocumentByQuery(index, type, queryBuilder, null, null);
    }

    /**
     * queryBuilder 查询文档
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @param fieldList
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type, QueryBuilder queryBuilder, List<String> fieldList) {
        return findDocumentByQuery(index, type, queryBuilder, fieldList, null);
    }

    /**
     * queryBuilder 查询文档
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                              QueryBuilder queryBuilder, List<String> fieldList, List<SortBuilder> sortBuilders) {
        logger.info("全文检索查询条件:{}", queryBuilder.toString());
        try {
            SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(index, type, queryBuilder);

            if (!CollectionUtils.isEmpty(fieldList) && fieldList.size() > 0) {
                searchRequestBuilder.addFields(fieldList.toArray(new String[fieldList.size()]));
            }
            if (sortBuilders != null && sortBuilders.size() > 0) {
                for (SortBuilder sortBuilder : sortBuilders) {
                    searchRequestBuilder.addSort(sortBuilder);
                }
            }

            SearchResponse response = searchRequestBuilder
                    .execute()
                    .actionGet();
            return response;
        } catch (Exception e) {
            if (e instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("", e);
        }
        return null;
    }


    /**
     * queryBuilder 查询文档
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                              QueryBuilder queryBuilder, int pageNo, int pageSize) {
        return findDocumentByQuery(index, type, queryBuilder, pageNo, pageSize, null);
    }

    /**
     * inner_hits查询
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @param innerHit
     * @param pageNo
     * @param pageSize
     * @return
     * @author zhoup
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                              QueryBuilder queryBuilder, BaseEElasticSearchInnerName innerName, InnerHitsBuilder.InnerHit innerHit, int pageNo, int pageSize) {
        return findDocumentByQuery(index, type, queryBuilder, innerName, innerHit, pageNo, pageSize, null, null);
    }


    /**
     * queryBuilder 查询文档
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                              QueryBuilder queryBuilder, int pageNo, int pageSize, List<String> fieldList) {
        return findDocumentByQuery(index, type, queryBuilder, null, null, pageNo, pageSize, fieldList, null);
    }


    /**
     * queryBuilder 查询文档  带排序
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param fieldList
     * @param sortBuilders
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                              QueryBuilder queryBuilder, int pageNo, int pageSize, List<String> fieldList, List<SortBuilder> sortBuilders) {
        return findDocumentByQuery(index, type, queryBuilder, null, null, pageNo, pageSize, fieldList, sortBuilders);
    }

    /**
     * inner_hits查询
     *
     * @param index
     * @param type
     * @param queryBuilder
     * @param innerHit
     * @param pageNo
     * @param pageSize
     * @return
     * @author zhoup
     */
    public SearchResponse findDocumentByQuery(BaseElasticSearchIndexEnum index, BaseElasticSearchIndexTypeEnum type,
                                              QueryBuilder queryBuilder, BaseEElasticSearchInnerName innerName, InnerHitsBuilder.InnerHit innerHit,
                                              int pageNo, int pageSize, List<String> fieldList, List<SortBuilder> sortBuilders) {
        int form = pageNo * pageSize;
        try {

            SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(index, type, queryBuilder);
            //返回格式
            if (!BeanUtils.isEmpty(innerHit)) {
                searchRequestBuilder.addInnerHit(innerName.getCode(), innerHit);
            }

            if (form > 0) {
                searchRequestBuilder.setFrom(form);
            } else {
                searchRequestBuilder.setFrom(0);
            }
            searchRequestBuilder.setSize(pageSize);

            if (!CollectionUtils.isEmpty(fieldList) && fieldList.size() > 0) {
                searchRequestBuilder.addFields(fieldList.toArray(new String[fieldList.size()]));
            }

            if (sortBuilders != null && sortBuilders.size() > 0) {
                for (SortBuilder sortBuilder : sortBuilders) {
                    searchRequestBuilder.addSort(sortBuilder);
                }
            }

//			searchRequestBuilder.addSort(SortBuilder)

            logger.info("全文检索查询条件:{}", searchRequestBuilder.toString());

            SearchResponse response = searchRequestBuilder
                    .execute()
                    .actionGet();

            return response;
        } catch (Exception e) {
            if (e instanceof NoNodeAvailableException) {
                throw new NoNodeAvailableException("");
            }
            logger.error("", e);
        }
        return null;
    }

}
