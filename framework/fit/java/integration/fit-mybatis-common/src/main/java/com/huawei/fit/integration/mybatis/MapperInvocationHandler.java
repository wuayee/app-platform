/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.integration.mybatis;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.transaction.DataAccessException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 为 {@code Mapper} 提供动态代理的执行处理程序。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-02
 */
final class MapperInvocationHandler implements InvocationHandler {
    private final SqlSessionFactory sessionFactory;
    private final Class<?> mapperClass;

    MapperInvocationHandler(SqlSessionFactory sessionFactory, Class<?> mapperClass) {
        this.sessionFactory = sessionFactory;
        this.mapperClass = mapperClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession session = null;
        try {
            session = this.sessionFactory.openSession(false);
            Object mapper = session.getMapper(this.mapperClass);
            Object result = method.invoke(mapper, args);
            session.commit();
            return result;
        } catch (PersistenceException e) {
            if (session != null) {
                session.rollback();
            }
            throw new DataAccessException(e);
        } catch (InvocationTargetException e) {
            session.rollback();
            Throwable cause = e.getCause();
            if (cause instanceof PersistenceException) {
                throw new DataAccessException(cause);
            } else {
                throw cause;
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    static <M> M proxy(SqlSessionFactory sessionFactory, Class<M> mapperClass) {
        MapperInvocationHandler handler = new MapperInvocationHandler(sessionFactory, mapperClass);
        return cast(Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[] {mapperClass}, handler));
    }
}
