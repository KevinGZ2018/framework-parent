package cn.com.kidtop.elasticsearch;

import cn.kidtop.framework.elasticsearch.ElasticSearchService;
import cn.kidtop.framework.elasticsearch.ElasticSearchUtil;
import cn.kidtop.framework.elasticsearch.enumeration.BaseElasticSearchIndexEnum;
import cn.kidtop.framework.elasticsearch.enumeration.BaseElasticSearchIndexTypeEnum;
import mockit.Expectations;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//import mockit.NonStrictExpectations;
//import cn.kidtop.elasticsearch.cmp.TestCMP;

/**
 * 测试类
 *
 * @author zhoup
 * @date 2016年5月21日
 */
@ActiveProfiles("unit")
@RunWith(SpringJUnit4ClassRunner.class)
public class ElasticSearchCRUDTest {

    @InjectMocks
    ElasticSearchService elastiSearchService = null;

    @Mock
    TransportClient transportClient;

    @Mock
    IndexRequestBuilder indexRequestBuilder;

    @Mock
    IndexResponse indexResponse;

    @Mock
    GetResponse getResponse;

    @Mock
    UpdateResponse updateResponse;

    @Mock
    GetRequestBuilder getRequestBuilder;

    @Mock
    UpdateRequestBuilder updateRequestBuilder;

    @Mock
    SearchRequestBuilder searchRequestBuilder;

    @Mock
    ListenableActionFuture<SearchResponse> listenableActionFutureSearchResponse;

    @Mock
    ListenableActionFuture<UpdateResponse> listenableActionFutureUpdateResponse;

    @Mock
    SearchResponse searchResponse;

    @Mock
    SearchHits searchHits;

    @Mock
    DeleteRequestBuilder deleteRequestBuilder;

    @Mock
    ListenableActionFuture<DeleteResponse> listenableActionFutureDeleteResponse;

    @Mock
    DeleteResponse deleteResponse;

    @Test
    public void createIndex() throws IOException {
        new Expectations(ElasticSearchUtil.class, transportClient, indexRequestBuilder, indexResponse) {
            {
                ElasticSearchUtil.getClient();
                result = transportClient;

                transportClient.prepareIndex(anyString, anyString, anyString);
                result = indexRequestBuilder;

                indexRequestBuilder.setSource((Map<String, Object>) any);
                result = indexRequestBuilder;

                indexRequestBuilder.get();
                result = indexResponse;

                indexResponse.isCreated();
                result = true;

                indexResponse.isContextEmpty();
                result = false;

            }
        };
        Map<String, Object> jsonBuilder = new HashMap<String, Object>();
        jsonBuilder.put("name", "测试");
        IndexResponse indexResponse = elastiSearchService.createDocument(EIndex.SuperisongApp, EIndexType.SuperisongAppProduct, "573d227b71cfe4006c1fb813121", jsonBuilder);
        Assert.assertSame(indexResponse.isContextEmpty(), false);
    }

    @Test
    public void findDocumentByIndex() {
        new Expectations(ElasticSearchUtil.class, transportClient, getRequestBuilder, getResponse) {
            {
                ElasticSearchUtil.getClient();
                result = transportClient;

                transportClient.prepareGet(anyString, anyString, anyString);
                result = getRequestBuilder;

                getRequestBuilder.get();
                result = getResponse;

                getResponse.isExists();
                result = true;
            }
        };
        GetResponse response = elastiSearchService.findDocumentByIndex(EIndex.SuperisongApp, EIndexType.SuperisongAppProduct, "573d227b71cfe4006c1fb813");
        Map<String, Object> source = response.getSource();
        System.out.println("------------------------------");
        System.out.println("Index: " + response.getIndex());
        System.out.println("Type: " + response.getType());
        System.out.println("Id: " + response.getId());
        System.out.println("Version: " + response.getVersion());
        System.out.println("getFields: " + response.getFields());
        System.out.println(source);
        System.out.println("------------------------------");
        Assert.assertSame(response.isExists(), true);
    }

    @Test
    public void updateDocument() throws IOException, ExecutionException, InterruptedException {
        new Expectations(ElasticSearchUtil.class, transportClient, updateRequestBuilder, listenableActionFutureUpdateResponse, updateResponse, searchRequestBuilder, listenableActionFutureSearchResponse, searchResponse, searchHits) {
            {
                ElasticSearchUtil.getClient();
                result = transportClient;

                transportClient.prepareUpdate(anyString, anyString, anyString);
                result = updateRequestBuilder;

                updateRequestBuilder.setDoc((Map<String, Object>) any);
                result = updateRequestBuilder;

                updateRequestBuilder.execute();
                result = listenableActionFutureUpdateResponse;

                listenableActionFutureUpdateResponse.get();
                result = updateResponse;

                updateResponse.getId();
                result = "1";

                transportClient.prepareSearch(anyString);
                result = searchRequestBuilder;

                searchRequestBuilder.setTypes((String[]) any);
                result = searchRequestBuilder;

                searchRequestBuilder.setSearchType((SearchType) any);
                result = searchRequestBuilder;

                searchRequestBuilder.setQuery((QueryBuilder) any);
                result = searchRequestBuilder;

                searchRequestBuilder.execute();
                result = listenableActionFutureSearchResponse;

                listenableActionFutureSearchResponse.actionGet();
                result = searchResponse;

                searchResponse.getHits();
                result = searchHits;

                searchHits.totalHits();
                result = 1;
            }
        };

        Map<String, Object> jsonBuilder = new HashMap<String, Object>();
        jsonBuilder.put("name", "超爱送伍川麻辣凤爪210g/袋");
        UpdateResponse updateResponse = elastiSearchService.updateDocument(EIndex.SuperisongApp, EIndexType.SuperisongAppProduct, "573d227b71cfe4006c1fb813", jsonBuilder);

        TermQueryBuilder productKeyWordQueryBuilder = QueryBuilders.termQuery("name", "超爱送");

        SearchResponse response = elastiSearchService.findDocumentByQuery(EIndex.SuperisongApp, EIndexType.SuperisongAppProduct, productKeyWordQueryBuilder);
        Assert.assertSame(response.getHits().totalHits() > 0, true);
    }

    @Test
    public void removeDocument() throws IOException {
        new Expectations(ElasticSearchUtil.class, transportClient, deleteRequestBuilder, listenableActionFutureDeleteResponse, deleteResponse) {
            {
                ElasticSearchUtil.getClient();
                result = transportClient;

                transportClient.prepareDelete(anyString, anyString, anyString);
                result = deleteRequestBuilder;

                deleteRequestBuilder.execute();
                result = listenableActionFutureDeleteResponse;

                listenableActionFutureDeleteResponse.actionGet();
                result = deleteResponse;

                deleteResponse.isFound();
                result = true;

            }
        };
        DeleteResponse deleteResponse = elastiSearchService.removeDocument(EIndex.SuperisongApp, EIndexType.SuperisongAppProduct, "573d227b71cfe4006c1fb813121");
        Assert.assertSame(deleteResponse.isFound(), true);
    }

    /**
     * 多条件匹配查询
     *
     * @author zhoup
     */
    @Test
    public void findDocumentByQuery() {
        new Expectations(ElasticSearchUtil.class, transportClient, searchRequestBuilder, listenableActionFutureSearchResponse, searchResponse, searchHits) {
            {
                ElasticSearchUtil.getClient();
                result = transportClient;

                transportClient.prepareSearch(anyString);
                result = searchRequestBuilder;

                searchRequestBuilder.setTypes((String[]) any);
                result = searchRequestBuilder;

                searchRequestBuilder.setSearchType((SearchType) any);
                result = searchRequestBuilder;

                searchRequestBuilder.setQuery((QueryBuilder) any);
                result = searchRequestBuilder;

                searchRequestBuilder.execute();
                result = listenableActionFutureSearchResponse;

                listenableActionFutureSearchResponse.actionGet();
                result = searchResponse;

                searchResponse.getHits();
                result = searchHits;

                searchHits.totalHits();
                result = 1;
            }
        };
        //arg[1]查询条件，arg[2]...为查询字段
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("统一", "name", "lableId");
        //匹配查询
        QueryBuilder qb1 = QueryBuilders.matchQuery("shopId", "55da893600b04a63498e4fc0");
        QueryBuilder qb2 = QueryBuilders.boolQuery().must(qb1).must(queryBuilder);

        SearchResponse response = elastiSearchService.findDocumentByQuery(EIndex.SuperisongApp, EIndexType.SuperisongAppProduct, qb2, 0, 0);
        Assert.assertSame(response.getHits().totalHits() > 0, true);
    }

    private enum EIndex implements BaseElasticSearchIndexEnum {
        SuperisongApp("superisong_app", "S端索引");

        private String code;

        private String text;

        EIndex(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
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

    private enum EIndexType implements BaseElasticSearchIndexTypeEnum {
        SuperisongAppProduct("superisong_app_product", "S端商超商品类型");

        private String code;

        private String text;

        EIndexType(String code, String text) {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
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
