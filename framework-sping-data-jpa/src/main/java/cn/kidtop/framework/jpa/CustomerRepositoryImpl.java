/**
 *
 */
package cn.kidtop.framework.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 自定义Repository
 *
 * @param <F>
 * @param <ID>
 * @author zpcsa
 */
@NoRepositoryBean
public class CustomerRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements
        CustomerRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomerRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                  EntityManager entityManager) {
        super(entityInformation, entityManager);

        // Keep the EntityManager around to used from the newly introduced methods.
        this.entityManager = entityManager;
    }


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
    @SuppressWarnings("unchecked")
    public <F> Page<F> getPageCountNativeSQL(String countQueryStr, String sqlQueryStr, Map<String, Object> map,
                                             Pageable pageable, Class<F> resultClass, boolean isCache) {

        long count = this.getPageCountNativeSQL(countQueryStr, map);
        List<F> list = new ArrayList<F>();
        Page<F> page = new PageImpl<F>(list, pageable, 0);
        if (count > 0) {
            Query query = null;
            if (StringUtils.isEmpty(resultClass)) {
                query = this.entityManager.createNativeQuery(sqlQueryStr);
            } else {
                query = this.entityManager.createNativeQuery(sqlQueryStr, resultClass);
            }
            this.setQueryParameter(query, map);

            query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
            page = new PageImpl<F>(query.getResultList(), pageable, count);
            this.clearEntityCache(this.entityManager, isCache);
        }

        return page;
    }

    /**
     * 执行NativeSQL sql返回list
     *
     * @param sql
     * @param resultClass
     * @return
     * @author zhoup
     */
    public <F> List<F> executeNativeSql(String sql, Map<String, Object> paramMap, Class<F> resultClass, boolean isCache) {
        Query query = this.entityManager.createNativeQuery(sql, resultClass);
        this.setQueryParameter(query, paramMap);
        @SuppressWarnings("unchecked")
        List<F> list = query.getResultList();
        clearEntityCache(entityManager, isCache);
        return list;
        // return null;
    }

    /**
     * 设置query参数
     *
     * @param query
     * @param map
     * @author fushun
     * @version VS1.3
     * @creation 2016年5月6日
     */
    private void setQueryParameter(Query query, Map<String, Object> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        for (Entry<String, Object> entry : map.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 清除 缓存
     *
     * @param entityManager
     * @param isCache
     * @author jerry
     */
    public void clearEntityCache(EntityManager entityManager, boolean isCache) {
        if (isCache) {
            entityManager.clear();
        }
    }

    /**
     * NativeSQL 查询统计
     *
     * @param sql
     * @param map
     * @return
     */
    private long getPageCountNativeSQL(String sql, Map<String, Object> map) {

        sql = "select count(*) from ( " + sql + " ) superi_ep_tmp";
        Query query = this.entityManager.createNativeQuery(sql);
        this.setQueryParameter(query, map);

        Object object = query.getSingleResult();
        if (object == null) {
            object = 0;
        }
        return Long.valueOf(String.valueOf(object));
    }

    /**
     * 执行NativeSQL sql返回list 可清除缓存
     *
     * @param queryStr    sql语句
     * @param map         sql参数值
     * @param pageable    分页条件
     * @param resultClass 返回类型
     * @param isCache
     * @return
     */
    public <F> Page<F> getPageCountNativeSQL(String queryStr,
                                             Map<String, Object> map, Pageable pageable, Class<F> resultClass, boolean isCache) {
        return getPageCountNativeSQL(queryStr, queryStr, map, pageable, resultClass, isCache);
    }

    /**
     * 获取统计条数
     *
     * @param countHql
     * @param map
     * @return
     * @date: 2017-09-05 14:48:04
     * @author:wangfushun
     * @version 1.0
     */
    public long getPageCount(String countHql, Map<String, Object> map) {
        Query query = entityManager.createQuery(countHql);
        setQueryParameter(query, map);
        long count = 0;
        @SuppressWarnings("unchecked")
        List<Object> objects = query.getResultList();
        if (objects.size() == 1) {
            count = Long.valueOf(String.valueOf(objects.get(0)));
        }
        return count;
    }

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
    public <F> F getValue(String countHql, Map<String, Object> map) {
        Query query = entityManager.createQuery(countHql);
        setQueryParameter(query, map);
        @SuppressWarnings("unchecked")
        List<F> objects = query.getResultList();
        if (objects.size() == 1) {
            return objects.get(0);
        }
        return null;
    }


    /**
     * 获取分页数据
     * <p> 使用hql语句
     *
     * @param countHqlQueryStr 需要自己写 统计 方式
     * @param hqlQueryStr
     * @param map              queryParameter
     * @param pageable         分页
     * @param resultClass
     * @return
     */
    public <F> Page<F> getPageCount(String countHqlQueryStr, String hqlQueryStr,
                                    Map<String, Object> map, Pageable pageable, Class<F> resultClass) {


        long count = getPageCount(countHqlQueryStr, map);
        TypedQuery<F> query = entityManager.createQuery(hqlQueryStr, resultClass);

        setQueryParameter(query, map);

        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());

        Page<F> page = new PageImpl<F>(query.getResultList(), pageable, count);
        return page;
    }

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
    public <F> Page<F> getPageCount(String hqlQueryStr, Map<String, Object> map, Pageable pageable, Class<F> resultClass) {
        String countQueryStr = replaceJpaCountSql(hqlQueryStr);
        return getPageCount(countQueryStr, hqlQueryStr, map, pageable, resultClass);
    }

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
    public <F> List<F> getList(String hqlQueryStr, Map<String, Object> map, Class<F> resultClass) {
        TypedQuery<F> query = entityManager.createQuery(hqlQueryStr, resultClass);
        setQueryParameter(query, map);
        return query.getResultList();
    }

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
    public <F> List<F> getList(String hqlQueryStr, Map<String, Object> map, Pageable pageable, Class<F> resultClass) {
        TypedQuery<F> query = entityManager.createQuery(hqlQueryStr, resultClass);
        setQueryParameter(query, map);
        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
        return query.getResultList();
    }

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
    @SuppressWarnings("unchecked")
    public <F> F getNativeSingleResult(String sqlQueryStr, Map<String, Object> map, Class<F> resultClass) {
        Query query = entityManager.createNativeQuery(sqlQueryStr, resultClass);
        setQueryParameter(query, map);
        return (F) query.getSingleResult();
    }

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
    @SuppressWarnings({"unchecked", "hiding"})
    public <F> List<F> getNativeList(String sqlQueryStr, Map<String, Object> map, Class<F> resultClass) {
        Query query = entityManager.createNativeQuery(sqlQueryStr, resultClass);
        setQueryParameter(query, map);
        return query.getResultList();
    }

    ;

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
    @SuppressWarnings("unchecked")
    public <F> List<F> getNativeList(String sqlQueryStr, Map<String, Object> map, Pageable pageable, Class<F> resultClass) {
        Query query = entityManager.createNativeQuery(sqlQueryStr, resultClass);
        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
        setQueryParameter(query, map);
        return query.getResultList();
    }

    ;

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
    public List<?> getNativeList(String sqlQueryStr, Map<String, Object> map, Pageable pageable) {
        Query query = entityManager.createNativeQuery(sqlQueryStr);
        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
        setQueryParameter(query, map);
        return query.getResultList();
    }

    /**
     * 返回数据库的一个字段方式sql
     * <p>其他返回使用其他方法
     *
     * @param sqlQueryStr
     * @param map
     * @return
     * @date: 2017-09-16 17:15:54
     * @author:wangfushun
     * @version 1.0
     */
    public List<?> getNativeList(String sqlQueryStr, Map<String, Object> map) {
        Query query = entityManager.createNativeQuery(sqlQueryStr);
        setQueryParameter(query, map);
        return query.getResultList();
    }

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
    public <F> F getNativeValue(String countsql, Map<String, Object> map) {
        Query query = entityManager.createNativeQuery(countsql);
        setQueryParameter(query, map);
        @SuppressWarnings("unchecked")
        List<F> objects = query.getResultList();
        if (objects.size() == 1) {
            return objects.get(0);
        }
        return null;
    }

    /**
     * 替换hql查询语句为统计的查询语句
     *
     * @param sql
     * @return
     * @date: 2017-09-05 14:43:11
     * @author:wangfushun
     * @version 1.0
     */
    private String replaceJpaCountSql(String sql) {
        String countSql = "select count(*) " + sql.substring(sql.indexOf("from"));
        countSql = countSql.replaceAll(" fetch ", " ");
        return countSql;
    }
}
