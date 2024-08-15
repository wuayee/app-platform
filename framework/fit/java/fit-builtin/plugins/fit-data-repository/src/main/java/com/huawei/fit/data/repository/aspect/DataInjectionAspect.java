/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.aspect;

import static com.huawei.fitframework.inspection.Validation.between;
import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.data.repository.DataRepository;
import com.huawei.fit.data.repository.ValidationService;
import com.huawei.fit.data.repository.annotation.DataInjection;
import com.huawei.fit.data.repository.annotation.Parameter;
import com.huawei.fit.data.repository.annotation.ReturnValue;
import com.huawei.fit.data.repository.entity.CacheKeyMetadata;
import com.huawei.fit.data.repository.entity.Metadata;
import com.huawei.fit.data.repository.entity.MetadataType;
import com.huawei.fit.data.repository.exception.CapacityOverflowException;
import com.huawei.fit.data.repository.support.CachedDataNotFoundException;
import com.huawei.fit.data.repository.support.DataBusRepository;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.annotation.Around;
import com.huawei.fitframework.aop.annotation.Aspect;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.loadbalance.WorkerFilter;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.conf.runtime.SerializationFormat;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.UuidUtils;
import com.huawei.fitframework.value.ValueFetcher;
import com.huawei.fitframework.value.ValueSetter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 表示数据注入的切面定义。
 *
 * @author 季聿阶
 * @since 2024-01-21
 */
@Aspect(scope = Scope.GLOBAL)
@Component
public class DataInjectionAspect {
    private static final String STAR = "*";
    private static final Logger log = Logger.get(DataInjectionAspect.class);

    private final DataRepository dataRepository;
    private final ValueFetcher valueFetcher;
    private final ValueSetter valueSetter;
    private final String workerId;
    private final BrokerClient brokerClient;
    private final ValidationService validationService;

    public DataInjectionAspect(DataRepository dataRepository, ValueFetcher valueFetcher, ValueSetter valueSetter,
            WorkerConfig workerConfig, BrokerClient brokerClient, ValidationService validationService) {
        this.dataRepository = notNull(dataRepository, "The data repository cannot be null.");
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
        this.valueSetter = notNull(valueSetter, "The value setter cannot be null.");
        this.workerId = notBlank(workerConfig.id(), "The worker id cannot be blank.");
        this.brokerClient = notNull(brokerClient, "The broker client cannot be blank.");
        this.validationService = notNull(validationService, "The validation service cannot be null.");
    }

    @Around(pointcut = "@annotation(com.huawei.fit.data.repository.annotation.DataInjection)")
    private Object aroundDataInjectionMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        DataInjection dataInjection = joinPoint.getMethod().getDeclaredAnnotation(DataInjection.class);
        List<CacheKeyMetadata> toDeleteCaches = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(dataInjection.parameters())) {
            for (Parameter parameter : dataInjection.parameters()) {
                if (parameter == null) {
                    continue;
                }
                toDeleteCaches.addAll(this.injectParameter(parameter, args));
            }
        }
        Object proceedResult;
        try {
            proceedResult = joinPoint.proceed();
        } catch (Exception ex) {
            this.handleException(toDeleteCaches, ex);
            throw ex;
        }
        for (ReturnValue returnValue : dataInjection.returnValue()) {
            proceedResult = this.injectReturnValue(returnValue, proceedResult);
        }
        toDeleteCaches.forEach(this::deleteCache);
        return proceedResult;
    }

    private void handleException(List<CacheKeyMetadata> toDeleteCaches, Exception ex) {
        if (ex instanceof CapacityOverflowException) {
            return;
        }
        for (CacheKeyMetadata cache : toDeleteCaches) {
            try {
                this.deleteCache(cache);
            } catch (Exception deleteCacheException) {
                ex.addSuppressed(deleteCacheException);
            }
        }
    }

    private List<CacheKeyMetadata> injectParameter(Parameter parameter, Object[] args) {
        between(parameter.index(),
                0,
                args.length - 1,
                "The index of parameter is out of range. [index={0}, argsNum={1}]",
                parameter.index(),
                args.length);
        String path = parameter.path();
        Validation.equals(path.indexOf(STAR), path.lastIndexOf(STAR), "Too many '*' in path. [path={0}]", path);
        Object arg = args[parameter.index()];
        Object valueIdInfo = this.valueFetcher.fetch(arg, path);
        if (valueIdInfo instanceof List) {
            if (!path.contains(STAR)) {
                throw new IllegalStateException(StringUtils.format("Not supported path. [path={0}]", path));
            }
            List<Object> list = cast(valueIdInfo);
            List<CacheKeyMetadata> metadataList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String replace = replacePath(path, i);
                this.getCacheKeyMetadata(replace, list.get(i), arg).ifPresent(metadataList::add);
            }
            return metadataList;
        } else {
            Optional<CacheKeyMetadata> cacheKeyMetadata = this.getCacheKeyMetadata(path, valueIdInfo, arg);
            return cacheKeyMetadata.map(Collections::singletonList).orElse(Collections.emptyList());
        }
    }

    private Optional<CacheKeyMetadata> getCacheKeyMetadata(String path, Object valueIdInfo, Object arg) {
        String actualValueIdInfo;
        if (valueIdInfo == null) {
            return Optional.empty();
        } else if (valueIdInfo instanceof byte[]) {
            actualValueIdInfo = new String((byte[]) valueIdInfo, StandardCharsets.UTF_8);
        } else if (valueIdInfo instanceof String) {
            actualValueIdInfo = (String) valueIdInfo;
        } else {
            throw new IllegalStateException(StringUtils.format("Not supported value type to inject. [type={0}]",
                    valueIdInfo.getClass().getName()));
        }
        int index = actualValueIdInfo.indexOf("@");
        if (index < 0) {
            throw new IllegalStateException("Illegal value cached id pattern.");
        }
        String actualValueId = actualValueIdInfo.substring(0, index);
        String targetWorkerId = actualValueIdInfo.substring(index + 1);
        String originalTargetWorkerId = "";
        boolean isDatabusUsedForRemote = false;
        // 如果 DataBus 服务已经打开，而且本机、目标机的 worker ID 均含有 Pod 标识符，则通过查看 Pod 是否相等，判断是否应该发起
        // 跨 Pod 的 FIT 请求，还是从本地 DataBus 直接读取数据内容。
        if (DataBusRepository.INSTANCE.checkRunningWithRetry() && targetWorkerId.indexOf("@") > 0
                && workerId.indexOf("@") > 0) {
            String targetPodName = targetWorkerId.substring(0, targetWorkerId.indexOf("@"));
            String localPodName = workerId.substring(0, workerId.indexOf("@"));
            // 相同 Pod 时直接从本地读取数据。
            if (targetPodName.equals(localPodName)) {
                originalTargetWorkerId = targetWorkerId;
                targetWorkerId = workerId;
                isDatabusUsedForRemote = true;
                log.info("Same Pod, using local databus retrieval. [id={}]", actualValueId);
            } else {
                log.info("Different Pods, Using FIT remote retrieval. [id={}]", actualValueId);
            }
        } else {
            log.info("DataBus unavailable. Using FIT remote retrieval. [id={}]", actualValueId);
        }

        Object actualValue = null;
        try {
            actualValue = this.getActualValue(actualValueId, targetWorkerId);
        } catch (FitException e) {
            // 如果使用了 DataBus 获取异地数据失败，则再次尝试向异地进程直接发送 FIT 数据请求。
            // 这样做的好处是避免 DataBus 断连又重连后，远端数据被缓存在对端进程内部而无法被 DataBus 获取。
            if (isDatabusUsedForRemote && e.getCause() instanceof CachedDataNotFoundException) {
                log.warn("DataBus remote retrieval failed, downgrade to FIT retrival. [id={}]", actualValueId);
                actualValue = this.getActualValue(actualValueId, originalTargetWorkerId);
            } else {
                // 如果是使用 DataBus 获取本地数据未果，则直接重抛出而不必重试
                throw e;
            }
        }
        this.valueSetter.set(arg, path, actualValue);
        return Optional.of(new CacheKeyMetadata(actualValueId, targetWorkerId));
    }

    private Object getActualValue(String actualValueId, String targetWorkerId) {
        Metadata metadata = cast(this.invokeRepositoryMethod("com.huawei.fit.bigdata.cache.read.meta",
                targetWorkerId,
                actualValueId));
        if (!StringUtils.equals(this.workerId, targetWorkerId)) {
            this.validationService.validateCapacity(metadata.getLength());
        }
        if (Objects.equals(metadata.getType(), MetadataType.STRING.code())) {
            return this.invokeRepositoryMethod("com.huawei.fit.bigdata.cache.read.str", targetWorkerId, actualValueId);
        } else if (Objects.equals(metadata.getType(), MetadataType.BYTES.code())) {
            return this.invokeRepositoryMethod("com.huawei.fit.bigdata.cache.read.bytes",
                    targetWorkerId,
                    actualValueId);
        } else {
            throw new IllegalStateException(StringUtils.format(
                    "Not supported metadata type to get from remote. [type={0}]",
                    metadata.getType()));
        }
    }

    private Object injectReturnValue(ReturnValue returnValue, Object result) {
        if (result == null) {
            return null;
        }
        Object actualValue = this.valueFetcher.fetch(result, returnValue.path());
        if (actualValue == null) {
            return result;
        }
        String path = returnValue.path();
        Validation.equals(path.indexOf(STAR), path.lastIndexOf(STAR), "Too many '*' in path. [path={0}]", path);
        if (actualValue instanceof List) {
            List<Object> list = cast(actualValue);
            if (!path.contains(STAR)) {
                throw new IllegalStateException(StringUtils.format("Not supported path. [path={0}]", path));
            }
            for (int i = 0; i < list.size(); i++) {
                String replacedPath = replacePath(path, i);
                this.save(replacedPath, result, list.get(i));
            }
        } else {
            this.save(path, result, actualValue);
        }
        return result;
    }

    private static String replacePath(String path, int i) {
        return path.replace(STAR, String.valueOf(i));
    }

    private void save(String path, Object result, Object actualValue) {
        String id = UuidUtils.randomUuidString();
        String replacedValue = id + "@" + this.workerId;
        if (actualValue instanceof byte[]) {
            this.valueSetter.set(result, path, replacedValue.getBytes(StandardCharsets.UTF_8));
        } else if (actualValue instanceof String) {
            this.valueSetter.set(result, path, replacedValue);
        } else {
            throw new IllegalStateException(StringUtils.format("Not supported value type to inject. [type={0}]",
                    actualValue.getClass().getName()));
        }
        this.dataRepository.save(id, actualValue);
    }

    private void deleteCache(CacheKeyMetadata cacheKeyMetadata) {
        this.invokeRepositoryMethod("com.huawei.fit.bigdata.cache.delete",
                cacheKeyMetadata.getWorkerId(),
                cacheKeyMetadata.getCacheKeyId());
    }

    private Object invokeRepositoryMethod(String genericableId, String targetWorkerId, String actualValueId) {
        return this.brokerClient.getRouter(DataRepository.class, genericableId)
                .route(new FitableIdFilter("local-worker"))
                .filter(new WorkerFilter(targetWorkerId))
                .format(SerializationFormat.CBOR)
                .invoke(actualValueId);
    }
}
