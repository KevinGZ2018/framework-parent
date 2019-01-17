package cn.kidtop.framework.ice.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * iceBean属性转换 过滤
 * ice optional 属性，在没有设置值的时候，get请求会出现异常，所以在此拦截异常
 * Created by Administrator on 2017/6/27.
 */
public class IceBeanJsonPropertyFilter extends SimpleBeanPropertyFilter {

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
//        super.serializeAsField(pojo, jgen, provider, writer);
        try {
            super.serializeAsField(pojo, jgen, provider, writer);
        } catch (InvocationTargetException e) {
            Field methodField = writer.getClass().getDeclaredField("_accessorMethod");
            Field pojoField = null;
            try {
                methodField.setAccessible(true);
                Object writer_accessorMethodValue = methodField.get(writer);
                methodField.set(writer, null);

                Field writerField = writer.getClass().getDeclaredField("_field");

                pojoField = pojo.getClass().getDeclaredField(writer.getName());
                writerField.setAccessible(true);
                writerField.set(writer, pojoField);
                writerField.setAccessible(false);
                pojoField.setAccessible(true);
                super.serializeAsField(pojo, jgen, provider, writer);
                methodField.set(writer, writer_accessorMethodValue);
                pojoField.setAccessible(false);
                methodField.setAccessible(false);
            } catch (Exception e2) {
                //保障，但是有一些问题
//                System.out.println(ExceptionUtil.getPrintStackTrace(e2));
                pojoField = pojo.getClass().getDeclaredField(writer.getName());
                pojoField.setAccessible(true);
                Object obj = pojoField.get(pojo);
                pojoField.setAccessible(false);
                jgen.writeFieldName(writer.getName());
                if (obj == null) {
                    NullSerializer nullSerializer = NullSerializer.instance;
                    nullSerializer.serialize(null, jgen, provider);
                    return;
                }
                if (writer instanceof BeanPropertyWriter) {
                    ((BeanPropertyWriter) writer).getSerializer().serialize(obj, jgen, provider);
                }
            }
        }
    }

//    @Override
//    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
//        super.serializeAsElement(elementValue, jgen, provider, writer);
//    }
}
