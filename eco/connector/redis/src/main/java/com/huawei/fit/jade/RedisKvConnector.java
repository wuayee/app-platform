/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jade;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.rag.store.connector.KvConnector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.InvalidURIException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis键值数据库连接器。
 *
 * @since 2024-05-07
 */
public class RedisKvConnector implements KvConnector {
    private static final String OK = "OK";

    private Jedis jedis = null;

    /**
     * 根据传入的连接参数构建 {@link RedisKvConnector} 实例。
     *
     * @param host host
     * @param port port
     * @param userName userName
     * @param passwd passwd
     */
    public RedisKvConnector(String host, int port, String userName, String passwd) {
        Jedis client = new Jedis(host, port);
        String res = OK;

        if (StringUtils.isNotEmpty(passwd)) {
            if (StringUtils.isNotEmpty(userName)) {
                res = client.auth(userName, passwd);
            } else {
                res = client.auth(passwd);
            }
        }

        if (!OK.equals(res)) {
            throw new InvalidURIException("Authentication failed.");
        }

        if (!"PONG".equals(client.ping())) {
            throw new InvalidURIException("Connection failed.");
        }

        this.jedis = client;
    }

    /**
     * 根据传入的键在指定的namespace进行查询。
     *
     * @param key 表示要查询的键的 {@link String}。
     * @param namespace 表示namespace名称的 {@link String}。
     * @return 返回查询到的值。
     */
    @Override
    public String get(String key, String namespace) {
        jedis.select(Integer.parseInt(namespace));
        return jedis.get(key);
    }

    /**
     * 将传入的键值对插入到指定的namespace中。
     *
     * @param kvPairs 表示键值对的 {@link Map} {@code <} {@link String}, {@link String} {@code >}。
     * @param namespace 表示namespace名称的 {@link String}。
     */
    @Override
    public void put(Map<String, String> kvPairs, String namespace) {
        jedis.select(Integer.parseInt(namespace));
        Transaction transaction = jedis.multi();
        kvPairs.forEach(transaction::set);
        List<Object> res = transaction.exec();

        for (Object status : res) {
            if (!OK.toString().equals(status)) {
                throw new IllegalArgumentException(status.toString());
            }
        }
    }

    /**
     * 在指定的namespace中删除传入的键。
     *
     * @param key 表示要删除的键的 {@link String}。
     * @param namespace 表示namespace名称的 {@link String}。
     * @return 返回删除的结果，失败时有相应的错误码。
     */
    @Override
    public Boolean delete(String key, String namespace) {
        jedis.select(Integer.parseInt(namespace));
        Long resp = jedis.del(key);
        return resp == 1;
    }

    /**
     * 获取指定namespace的所有的键。
     *
     * @param namespace 表示namespace名称的 {@link String}。
     * @return 返回键的集合。
     */
    @Override
    public Set<String> keys(String namespace) {
        jedis.select(Integer.parseInt(namespace));
        return jedis.keys("*");
    }

    /**
     * 关闭数据库连接。
     */
    @Override
    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }
}
