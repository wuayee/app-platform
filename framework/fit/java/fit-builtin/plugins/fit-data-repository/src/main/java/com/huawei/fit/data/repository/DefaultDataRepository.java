/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.data.repository.entity.Metadata;
import com.huawei.fit.data.repository.entity.MetadataType;
import com.huawei.fit.data.repository.support.CachedDataNotFoundException;
import com.huawei.fit.data.repository.support.DataBusRepository;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 表示数据仓库的数据传输策略。
 * <ol>
 *   <li>优先选择 DataBus 共享内存机制传输数据。</li>
 *   <li>当 DataBus 相关操作出现问题时，降级到本地数据缓存机制。</li>
 * </ol>
 *
 * @author 季聿阶 j00559309
 * @since 2024-01-21
 */
@Component
public class DefaultDataRepository implements DataRepository {
    private static final Logger log = Logger.get(DefaultDataRepository.class);

    private final long timeoutSeconds;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, Instant> expired = new ConcurrentHashMap<>();

    /**
     * 初始化本地数据缓存仓库和 DataBus 服务。
     *
     * @param timeoutSeconds 表示数据超时时间的 {@code long}，单位为秒。
     * @param initialDelay 表示定时器的初始延迟的 {@code long}，单位为秒。
     * @param fixedDelay 表示定时器的固定延迟的 {@code long}，单位为秒。
     * @param host 表示 DataBus 服务的地址 {@code String}。
     * @param port 表示 DataBus 服务端口号的 {@code int}。
     * @param isDataBusEnabled 表示 DataBus 服务是否可用的 {@code boolean}。
     */
    public DefaultDataRepository(@Value("${timeout-seconds}") long timeoutSeconds,
            @Value("${scheduler-initial-delay}") long initialDelay,
            @Value("${scheduler-fixed-delay}") long fixedDelay,
            @Value("${databus.host}") String host,
            @Value("${databus.port}") int port,
            @Value("${databus.enabled}") boolean isDataBusEnabled) {
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 3600;
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleWithFixedDelay(this::cleanCache, initialDelay, fixedDelay, TimeUnit.SECONDS);
        DataBusRepository.INSTANCE.init(host, port, isDataBusEnabled);
    }

    private void cleanCache() {
        Set<String> allKeys = new HashSet<>(this.cache.keySet());
        for (String key : allKeys) {
            Instant expiredTime = this.expired.get(key);
            if (expiredTime == null) {
                continue;
            }
            if (expiredTime.isBefore(Instant.now())) {
                this.cache.remove(key);
                this.expired.remove(key);
            }
        }
    }

    @Override
    @Fitable(id = "local-worker")
    public void save(String id, Object data) {
        notBlank(id, "The cache id to save cannot be blank.");
        // 如果 DataBus 没有连接，或者存储时出错，则降级到使用本地缓存。
        if (DataBusRepository.INSTANCE.save(id, data)) {
            return;
        }
        log.info("Using DataPlugin to store data. [id={}]", id);
        this.cache.put(id, data);
        Instant expiredTime = Instant.now().plus(this.timeoutSeconds, ChronoUnit.SECONDS);
        this.expired.put(id, expiredTime);
    }

    @Override
    @Fitable(id = "local-worker")
    public void delete(String id) {
        notBlank(id, "The cache id to remove cannot be blank.");
        DataBusRepository.INSTANCE.delete(id);
        this.cache.remove(id);
        this.expired.remove(id);
    }

    @Override
    @Fitable(id = "local-worker")
    public Metadata getMetadata(String id) {
        Optional<Metadata> result = DataBusRepository.INSTANCE.getMetadata(id);
        if (result.isPresent()) {
            return result.get();
        }

        log.info("Using data plugin to retrieve metadata. [id={}]", id);
        Object data = this.get(id);
        Metadata metadata = new Metadata();
        if (data == null) {
            throw new CachedDataNotFoundException(StringUtils.format("The cache data cannot be null. [id={0}]", id));
        } else if (data instanceof byte[]) {
            metadata.setType(MetadataType.BYTES.code());
            metadata.setLength(ObjectUtils.<byte[]>cast(data).length);
        } else if (data instanceof String) {
            metadata.setType(MetadataType.STRING.code());
            metadata.setLength(ObjectUtils.<String>cast(data).length() * 2);
        } else {
            throw new IllegalStateException(StringUtils.format("Not supported data type to get. [type={0}]",
                    data.getClass().getName()));
        }
        return metadata;
    }

    @Override
    @Fitable(id = "local-worker")
    public String getString(String id) {
        notBlank(id, "The cache id to get cannot be blank.");
        Optional<byte[]> result = DataBusRepository.INSTANCE.get(id);
        if (result.isPresent()) {
            return new String(result.get(), StandardCharsets.UTF_8);
        }
        log.info("Using data plugin to retrieve string. [id={}]", id);
        return ObjectUtils.cast(this.get(id));
    }

    @Override
    @Fitable(id = "local-worker")
    public byte[] getBytes(String id) {
        notBlank(id, "The cache id to get cannot be blank.");
        Optional<byte[]> result = DataBusRepository.INSTANCE.get(id);
        if (result.isPresent()) {
            return result.get();
        }
        log.info("Using data plugin to retrieve bytes. [id={}]", id);
        return ObjectUtils.cast(this.get(id));
    }

    private Object get(String id) {
        Object value = this.cache.get(id);
        if (value != null) {
            Instant expiredTime = Instant.now().plus(this.timeoutSeconds, ChronoUnit.SECONDS);
            this.expired.put(id, expiredTime);
        }
        return value;
    }
}