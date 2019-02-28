/**
 *
 */
package com.fushun.framework.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 自定义Repository
 *
 * @param <T>
 * @param <ID>
 * @author zpcsa
 */
public interface CustomerRepository<T, ID extends Serializable> extends CrudRepository<T, ID>,
        JpaSpecificationExecutor<T> {

    /**
     * 执行NativeSQL sql返回list
     *
     * @param sql
     * @param resultClass
     * @return
     * @author zhoup
     */
    public <F> List<F> executeNativeSql(String sql, Map<String, Object> paramMap, Class<F> resultClass, boolean isCache);

    /**
     * 执行NativeSQL sql返回list 可清除缓存
     *
     * @param countQueryStr 统计sql语句
     * @param sqlQueryStr   sql语句
     * @param map           sql参数值
     * @param pageable      分页条件
     * @param resultClass   返回类型
     * @param isCache
     * @return
     * @author jerry
     */
    public <F> Page<F> getPageCountNativeSQL(String countQueryStr, String sqlQueryStr, Map<String, Object> map,
                                             Pageable pageable, Class<F> resultClass, boolean isCache);

    /**
     * 执行NativeSQL sql返回list 可清除缓存
     *
     * @param sqlQueryStr sql语句
     * @param map         sql参数值
     * @param pageable    分页条件
     * @param resultClass 返回类型
     * @param isCache
     * @return
     */
    public <F> Page<F> getPageCountNativeSQL(String sqlQueryStr, Map<String, Object> map, Pageable pageable,
                                             Class<F> resultClass, boolean isCache);

    /**
     * 返回单条数据
     *
     * @param sqlQueryStr 查询sql
     * @param map         查询参数
     * @param resultClass 返回对象Class
     * @return T
     * @date: 2017年09月06日21时53分
     * @author:wangfushun
     * @version 1.0
     */
    public <F> F getNativeSingleResult(String sqlQueryStr, Map<String, Object> map, Class<F> resultClass);

    /**
     * 获取返回列表数据
     *
     * @param sqlQueryStr
     * @param map
     * @param resultClass
     * @return
     * @date: 2017-09-07 16:18:01
     * @author:wangfushun
     * @version 1.0
     */
    public <F> List<F> getNativeList(String sqlQueryStr, Map<String, Object> map, Class<F> resultClass);

    /**
     * 获取返回列表数据
     * <p>指定 数据的开始和结束 ，即：总条数
     *
     * @param sqlQueryStr
     * @param map
     * @param pageable
     * @param resultClass
     * @return
     * @date: 2017-09-07 16:18:45
     * @author:wangfushun
     * @version 1.0
     */
    public <F> List<F> getNativeList(String sqlQueryStr, Map<String, Object> map, Pageable pageable, Class<F> resultClass);

    /**
     * 返回数据库的一个字段方式sql
     * <p>其他返回使用其他方法
     *
     * @param sqlQueryStr
     * @param map
     * @param pageable
     * @return
     * @date: 2017-09-16 17:15:54
     * @author:wangfushun
     * @version 1.0
     */
    public List<?> getNativeList(String sqlQueryStr, Map<String, Object> map, Pageable pageable);


    /**
     * 返回数据库的一个字段方式sql
     * <p>其他返回使用其他方法
     *
     * @param sqlQueryStr
     * @param map
     * @param pageable
     * @return
     * @date: 2017-09-16 17:15:54
     * @author:wangfushun
     * @version 1.0
     */
    public List<?> getNativeList(String sqlQueryStr, Map<String, Object> map);

    /**
     * 获取统计条数
     * <p>使用hql语句
     *
     * @param countHql
     * @param map
     * @return
     * @date: 2017-09-05 14:48:04
     * @author:wangfushun
     * @version 1.0
     */
    public long getPageCount(String countHql, Map<String, Object> map);

    /**
     * 获得一个值
     * <p> 返回一个聚合函数的值
     *
     * @param countsql
     * @param map
     * @return
     * @date: 2017-09-29 14:15:33
     * @author:wangfushun
     * @version 1.0
     */
    public <F> F getNativeValue(String countsql, Map<String, Object> map);

    /**
     * 获得一个值
     * <p> 返回一个聚合函数的值
     *
     * @param countHql
     * @param map
     * @return
     * @date: 2017-09-29 14:15:33
     * @author:wangfushun
     * @version 1.0
     */
    public <F> F getValue(String countHql, Map<String, Object> map);

    /**
     * 获取分页数据
     * <p> 使用hql语句
     *
     * @param countHqlQueryStr 需要自己写 统计 方式
     * @param HqlQueryStr
     * @param map              queryParameter
     * @param pageable         分页
     * @param resultClass
     * @return
     */
    public <F> Page<F> getPageCount(String countHqlQueryStr, String HqlQueryStr, Map<String, Object> map,
                                    Pageable pageable, Class<F> resultClass);

    /**
     * 查询 分页数据
     * <p> 使用hql查询语句
     *
     * @param hqlQueryStr
     * @param map         queryParameter
     * @param pageable    分页
     * @param resultClass
     * @return
     */
    public <F> Page<F> getPageCount(String hqlQueryStr, Map<String, Object> map, Pageable pageable, Class<F> resultClass);

    /**
     * hql查询返回列表
     *
     * @param hqlQueryStr 查询语句
     * @param map         查询语句参数
     * @param resultClass 返回Class
     * @return java.util.List<T>
     * @date: 2017年09月06日21时37分
     * @author:wangfushun
     * @version 1.0
     */
    public <F> List<F> getList(String hqlQueryStr, Map<String, Object> map, Class<F> resultClass);

    /**
     * hql查询返回列表
     * <p>限制返回条数</p>
     *
     * @param hqlQueryStr 查询语句
     * @param map         查询语句参数
     * @param pageable    分页对象
     * @param resultClass 返回Class
     * @return java.util.List<T>
     * @date: 2017年09月06日21时37分
     * @author:wangfushun
     * @version 1.0
     */
    public <F> List<F> getList(String hqlQueryStr, Map<String, Object> map, Pageable pageable, Class<F> resultClass);


}
