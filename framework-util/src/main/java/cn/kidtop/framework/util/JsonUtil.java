package cn.kidtop.framework.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.*;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static HashMap<Class<?>, Enhancer> iceObjectToJsonMap = new HashMap<Class<?>, Enhancer>();

    /**
     * jsonStr转换为 HashMap<String,Object>类型
     *
     * @param jsonStr
     * @return
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> jsonToHashMap(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            map = mapper.readValue(jsonStr, HashMap.class);
        } catch (Exception e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return map;
    }

    /**
     * json转为继承Map的对象类型
     *
     * @param jsonStr
     * @param T          继承Map的对象类型
     * @param keyClas    Map.Key class类型
     * @param valueClass Map.value class类型
     * @return
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    public static <K, V, T extends Map<K, V>> T jsonToMap(String jsonStr, Class<T> T, Class<K> keyClas, Class<V> valueClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {

            MapType mapType = MapType.construct(T, SimpleType.construct(keyClas), SimpleType.construct(valueClass));
            return mapper.readValue(jsonStr, mapType);
        } catch (JsonParseException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        } catch (JsonMappingException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    /**
     * json转换为T对象类型
     *
     * @param json
     * @param cl
     * @return
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    public static <T> T jsonToClass(String json, Class<T> cl) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        try {
            return mapper.readValue(json, cl);
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    /**
     * json转换为对象类型
     * 返回 包装的类型 Map&ltString, AppFirstOrderRuleDTO&gt
     *
     * @param jsonStr
     * @param valueTypeRef 转为的对象类型，是一种包装的类型
     *                     <p>例如：TypeReference&ltMap&ltString, AppFirstOrderRuleDTO&gt&gt ref = new TypeReference&ltMap&ltString, AppFirstOrderRuleDTO&gt&gt() { };
     * @return 返回 包装的类型 Map&ltString, AppFirstOrderRuleDTO&gt
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    public static <T> T jsonToClass(String jsonStr, TypeReference<T> valueTypeRef) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }

        return null;
    }

    /**
     * 对象转为json
     *
     * @param obj
     * @return
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    public static String classToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    public static String toJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String str = mapper.writeValueAsString(obj);
            return str;
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    /**
     * 对象转化为HashMap
     * @param obj
     * @return 对象的属性名为HashMap.Key，对象的属性值为HashMap.Value
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    public static HashMap<String, Object> classToHashMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String str = mapper.writeValueAsString(obj);
            return mapper.readValue(str, HashMap.class);
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }


    /**
     * @param map    转换为Class
     * @param classs
     * @return
     * @author fushun
     * @version V2.3全文检索
     * @creation 2016年8月17日
     * @records <p>  fushun 2016年8月17日</p>
     */
    public static <T> T hashMapToClass(Map<String, Object> map, Class<T> classs) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String str = mapper.writeValueAsString(map);
            return mapper.readValue(str, classs);
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    /**
     * json转换为对应的数组
     *
     * @param obj
     * @param classs
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月6日
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] jsonToArray(String obj, Class<T> classs) {
        if (StringUtils.isEmpty(obj)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();

        ArrayType valueType = TypeFactory.defaultInstance().constructArrayType(classs);
        try {
            Object pb = mapper.readValue(obj, valueType);
            return (T[]) pb;
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    /**
     * Json 转换为hashMap
     *
     * @param obj
     * @return
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月8日
     */
    @SuppressWarnings("unchecked")
    public static List<HashMap<String, Object>> jsonToList(String obj) {
        if (StringUtils.isEmpty(obj)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        MapType j = MapType.construct(HashMap.class, SimpleType.construct(String.class), SimpleType.construct(Object.class));
        CollectionType collectionType = CollectionType.construct(List.class, j);
        try {
            Object pb = mapper.readValue(obj, collectionType);
            return (List<HashMap<String, Object>>) pb;
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    /**
     * 转换为K,V为指定对象类型的HashMap
     * <p>
     * json转换为List<HashMap<K,V>>
     *
     * @param jsonStr    json字符串
     * @param keyCls     HashMap.Key 类型
     * @param valueClass HashMap.Value 类型
     * @return
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> List<HashMap<K, V>> jsonToList(String jsonStr, Class<K> keyCls, Class<V> valueClass) {
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        MapType mapType = MapType.construct(HashMap.class, SimpleType.construct(keyCls), SimpleType.construct(valueClass));
        CollectionType collectionType = CollectionType.construct(List.class, mapType);
        try {
            Object pb = mapper.readValue(jsonStr, collectionType);
            return (List<HashMap<K, V>>) pb;
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }

    /**
     * 转换为List<T>
     *
     * @param jsonStr
     * @param cls     对象类型
     * @return
     * @author fushun
     * @version dev706
     * @creation 2017年6月7日
     * @records <p>  fushun 2017年6月7日</p>
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> jsonToList(String jsonStr, Class<T> cls) {
        if (StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaType j = SimpleType.construct(cls);
        CollectionType collectionType = CollectionType.construct(List.class, j);
        try {
            Object pb = mapper.readValue(jsonStr, collectionType);
            return (List<T>) pb;
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }


    @SuppressWarnings({"unused", "unchecked"})
    public static void main(String[] args) {

//		String json="{\"0\":{\"orderObj\":{\"amount\":24,\"deliveryTime\":\"0\",\"remark\":\"\",\"isViewed\":1,\"orderNo\":\"2015102700000333\",\"appointmentTime\":\"\",\"userAttribute\":{\"score\":1,\"userId\":\"5628e62d60b28da5c9ac2eb1\",\"username\":\"18523827123\"},\"shopId\":[{\"__type\":\"Pointer\",\"className\":\"Shop\",\"objectId\":\"5625b7b660b2d1400144bd4e\"}],\"activityInfo\":{\"activityId\":{\"__type\":\"Pointer\",\"className\":\"Activity\",\"objectId\":\"55f63a5a60b2ad8a0ae1a96a\"},\"status\":1},\"type\":3,\"tip\":0,\"deliveryAddress\":{\"address\":\"重庆市渝北区空港大道656号\",\"updatedAt\":\"2015-10-24T12:20:17.195Z\",\"isDefault\":\"1\",\"name\":\"晓\",\"objectId\":\"5628e62d00b07d3622f0d307\",\"createdAt\":\"2015-10-22T13:35:41.922Z\",\"title\":\"公司\",\"userId\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\"5628e62d60b28da5c9ac2eb1\"},\"detailAddress\":\"2栋1-2\",\"phoneNumber\":\"18523827123\",\"geoPoint\":{\"__type\":\"GeoPoint\",\"latitude\":29.7460444,\"longitude\":106.6450168}},\"systemCancelOrder\":0,\"isCommented\":1,\"taskStatus\":1,\"userId\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\"5628e62d60b28da5c9ac2eb1\"},\"taskTime\":60,\"productAttribute\":[{\"productName\":\"红牛维生素功能饮料250ml/罐\",\"productLibraryId\":\"5625c23500b0cfba0787c3f9\",\"imageUrl\":\"http://ac-zibyemqg.clouddn.com/Gbghum6BMhzrIuxbHzbKAlR961tHBhvzUaU7ltLc.png\",\"productNumber\":4,\"productPrice\":6}],\"shopAttribute\":[{\"shopName\":\"一站便利店尚阳康城店\",\"shopId\":\"5625b7b660b2d1400144bd4e\",\"score\":0,\"shopAdminMobilePhone\":\"18623106325\"}],\"geoPoint\":{\"__type\":\"GeoPoint\",\"latitude\":29.7460444,\"longitude\":106.6450168},\"systemCancelComment\":1,\"orderStatus\":4,\"objectId\":\"562f815d00b0ee7fdadcb625\",\"createdAt\":\"2015-10-27T13:51:25.769Z\",\"updatedAt\":\"2015-10-27T15:14:20.598Z\"},\"shopObj\":{\"objectId\":\"5625b7b660b2d1400144bd4e\"},\"queueKey\":\"completeCouponQueue562f815d00b0ee7fdadcb625\"},\"1\":null}";
//		jsonToHashMap(json);
//		HashMap<String, Object> str= jsonToHashMap("[{\"name\":\"净含量\",\"value\":\"275ml/瓶\"}]");
//		String[] str= jsonToHashMap("[{\"name\":\"净含量\",\"value\":\"275ml/瓶\"}]");
//		List<HashMap<String, Object>> str=jsonToList("[{\"name\":\"净含量\",\"value\":\"275ml/瓶\"}]");
//		System.out.println("");
        String json = "{\"x1:4028813d554439710155443992ee0002\":{\"id\":\"4028813d554439710155443992ee0002\",\"count\":1,\"firstOrderConfigId\":\"4028813d554439710155443992e00001\",\"createdAt\":1465728471000,\"money\":1,\"probability\":1,\"status\":2,\"updatedAt\":1465728471000,\"useCount\":0,\"minProbability\":1,\"maxProbability\":1},\"x5:4028813d554439710155443992f40003\":{\"id\":\"4028813d554439710155443992f40003\",\"count\":1,\"firstOrderConfigId\":\"4028813d554439710155443992e00001\",\"createdAt\":1465728471000,\"money\":5,\"probability\":1,\"status\":2,\"updatedAt\":1465728471000,\"useCount\":0,\"minProbability\":2,\"maxProbability\":2}}";
        HashMap<String, Object> map = jsonToMap(json, HashMap.class, String.class, Object.class);

//		BaseDTO baseDTO=new BaseDTO();
//		baseDTO.setCreatedAt(new Date());
//		baseDTO.setUpdatedAt(new Date());
//		json=classToJson(baseDTO);
//		baseDTO=jsonToClass(json, BaseDTO.class);
//		 json="["+json+"]";
//		 json="[{\"id\":\"\",\"createdAt\":1465728471000,\"updatedAt\":1465728471000}]";
//		 List<BaseDTO> list= jsonToList(json, BaseDTO.class);
//		 List<HashMap<String, Object>> list2=jsonToList(json);
        System.out.println("");
    }


}
