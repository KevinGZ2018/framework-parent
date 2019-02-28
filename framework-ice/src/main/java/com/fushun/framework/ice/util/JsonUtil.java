package com.fushun.framework.ice.util;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fushun.framework.ice.bean.IceBeanJsonPropertyFilter;
import com.fushun.framework.util.util.ExceptionUtil;
import net.sf.cglib.proxy.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;


public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static HashMap<Class<?>, Enhancer> iceObjectToJsonMap = new HashMap<Class<?>, Enhancer>();


    public static String iceClassToJson(Object obj) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//			new SimpleFilterProvider().
//			mapper.setFilterProvider()
            mapper.getSerializationConfig().getAnnotationIntrospector().allIntrospectors();
            IceBeanJsonPropertyFilter iceBeanJsonPropertyFilter = new IceBeanJsonPropertyFilter();
            mapper.setFilterProvider(new SimpleFilterProvider().setDefaultFilter(iceBeanJsonPropertyFilter));
            mapper.setAnnotationIntrospector(new AnnotationIntrospector() {
                private static final long serialVersionUID = 1L;

                @Override
                public Version version() {
                    return null;
                }

                //让其走拦截类，不然他需要注解
                @Override
                public Object findFilterId(Annotated ann) {
                    return "1";
                }
            });
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.debug(ExceptionUtil.getPrintStackTrace(e));
        }
        return null;
    }
}
