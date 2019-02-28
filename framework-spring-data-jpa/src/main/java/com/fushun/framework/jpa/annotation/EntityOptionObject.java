/**
 *
 */
package com.fushun.framework.jpa.annotation;


/**
 * leancloud对象
 *
 * @author zpcsa
 */
public interface EntityOptionObject<T> {


    /**
     * 保存leancloud对象
     */
    void saveObject(T t);

    /**
     * 移除leancloud对象
     *
     * @author zhoup
     */
    void deleteObject(T t);

    /**
     * 修改对象
     *
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月8日
     */
    void upateObject(T t);

    /**
     * 保存之后
     *
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月13日
     */
    void postSaveObject(T t);

    /**
     * 更新之后
     *
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月13日
     */
    void postUpdateObject(T t);

    /**
     * 删除之后
     *
     * @author fushun
     * @version VS1.3
     * @creation 2016年4月13日
     */
    void postDeleteObject(T t);


    void postLoadObject(T t);

}
