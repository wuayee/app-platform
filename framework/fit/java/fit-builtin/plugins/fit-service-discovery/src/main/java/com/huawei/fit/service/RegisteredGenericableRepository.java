/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.service;

import static com.huawei.fitframework.inspection.Validation.notNegative;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.service.entity.FitableInfo;
import com.huawei.fit.service.entity.FitableMetaInstance;
import com.huawei.fit.service.entity.GenericableInfo;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.broker.ConfigurableFitable;
import com.huawei.fitframework.broker.ConfigurableGenericable;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.FitableFactory;
import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.GenericableFactory;
import com.huawei.fitframework.broker.GenericableMetadata;
import com.huawei.fitframework.broker.GenericableRepository;
import com.huawei.fitframework.broker.LocalGenericableRepository;
import com.huawei.fitframework.broker.UniqueGenericableId;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.ExecutePolicy;
import com.huawei.fitframework.schedule.Task;
import com.huawei.fitframework.schedule.ThreadPoolScheduler;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表示注册中心相关的 {@link Genericable} 仓库。
 * <p>该仓库中仅包含注册中心中注册的服务。</p>
 *
 * @author 邬涨财 w00575064
 * @author 季聿阶 j00559309
 * @since 2023-04-17
 */
@Order(Order.NEARLY_HIGH)
@Component
public class RegisteredGenericableRepository implements GenericableRepository {
    private static final Logger log = Logger.get(RegisteredGenericableRepository.class);
    private static final long INITIAL_DELAY = 10L;

    private final LocalGenericableRepository localRepository;
    private final GenericableFactory genericableFactory;
    private final FitableFactory fitableFactory;
    private final RegistryService registryService;

    private final List<String> environmentPrioritySequence;
    private final Map<UniqueGenericableId, ConfigurableGenericable> registeredGenericables = new ConcurrentHashMap<>();
    private volatile int preSignature = 0;

    /**
     * 构建注册中心注册的服务仓对象。
     *
     * @param localRepository 表示本地服务仓库的 {@link LocalGenericableRepository}。
     * @param genericableFactory 表示服务工厂的 {@link GenericableFactory}。
     * @param fitableFactory 表示服务实现工厂的 {@link FitableFactory}。
     * @param worker 表示进程配置的 {@link WorkerConfig}。
     * @param registryService 表示注册中心服务的 {@link RegistryService}。
     * @param pullPeriod 表示更新仓数据的周期时间的 {@code long}。
     */
    public RegisteredGenericableRepository(LocalGenericableRepository localRepository,
            GenericableFactory genericableFactory, FitableFactory fitableFactory, WorkerConfig worker,
            RegistryService registryService,
            @Value("${plugin.registry.genericable-repository.pull.period}") long pullPeriod) {
        this.localRepository = notNull(localRepository, "The local genericable repository cannot be null.");
        this.genericableFactory = notNull(genericableFactory, "The genericable factory cannot be null.");
        this.fitableFactory = notNull(fitableFactory, "The fitable factory cannot be null.");
        this.registryService = notNull(registryService, "The registry service cannot be null.");
        notNegative(pullPeriod, "The pullPeriod which used to update repository cannot less than zero.");
        this.environmentPrioritySequence = worker.environmentSequence();
        ThreadPoolScheduler scheduler = ThreadPoolScheduler.custom()
                .corePoolSize(1)
                .isDaemonThread(true)
                .threadPoolName("registry-genericable-repository")
                .build();
        scheduler.schedule(Task.builder()
                .runnable(this::updateRegisteredGenericables)
                .policy(ExecutePolicy.fixedDelay(pullPeriod * 1000))
                .uncaughtExceptionHandler((thread, cause) -> this.fallbackForUpdateRegisteredRepository(cause))
                .build(), INITIAL_DELAY * 1000);
        log.debug("Start scheduled task to sync registered genericables. [initialDelay={} seconds, period={} seconds]",
                INITIAL_DELAY,
                pullPeriod);
    }

    private void fallbackForUpdateRegisteredRepository(Throwable cause) {
        log.warn("Failed to update registered repository. [cause={}]", cause.getMessage());
        log.debug("Failed to update registered repository.", cause);
    }

    private void updateRegisteredGenericables() {
        Set<UniqueGenericableId> all = new HashSet<>(this.registeredGenericables.keySet());
        List<ConfigurableGenericable> runningGenericables = this.getRunningGenericables(all);
        runningGenericables.forEach(genericable -> {
            all.remove(genericable.toUniqueId());
            this.registeredGenericables.put(genericable.toUniqueId(), genericable);
        });
        all.forEach(this.registeredGenericables::remove);
        List<Genericable> values = new ArrayList<>(this.registeredGenericables.values());
        int sig = Objects.hashCode(values);
        if (this.preSignature != sig) {
            log.info("Registered genericables changed. [size={}, genericables={}]",
                    this.registeredGenericables.size(),
                    values);
            this.preSignature = sig;
        }
    }

    private List<ConfigurableGenericable> getRunningGenericables(Set<UniqueGenericableId> uniqueGenericableIds) {
        List<FitableMetaInstance> instances =
                this.registryService.queryFitableMetas(this.buildQueryRunningFitablesParams(uniqueGenericableIds))
                        .stream()
                        .filter(this::isValid)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }
        for (String environment : this.environmentPrioritySequence) {
            List<FitableMetaInstance> filteredFitables = instances.stream()
                    .filter(instance -> instance.getEnvironments().contains(environment))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filteredFitables)) {
                return this.buildGenericables(filteredFitables);
            }
        }
        return Collections.emptyList();
    }

    private boolean isValid(FitableMetaInstance instance) {
        if (instance == null) {
            return false;
        }
        if (instance.getMeta() == null || instance.getMeta().getFitable() == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(instance.getEnvironments())) {
            return false;
        }
        FitableInfo fitable = instance.getMeta().getFitable();
        if (StringUtils.isBlank(fitable.getGenericableId())) {
            return false;
        }
        return StringUtils.isNotBlank(fitable.getFitableId());
    }

    private List<ConfigurableGenericable> buildGenericables(List<FitableMetaInstance> instances) {
        Map<UniqueGenericableId, ConfigurableGenericable> genericables = new HashMap<>();
        for (FitableMetaInstance instance : instances) {
            FitableInfo fitable = instance.getMeta().getFitable();
            String genericableVersion =
                    StringUtils.blankIf(fitable.getGenericableVersion(), GenericableMetadata.DEFAULT_VERSION);
            UniqueGenericableId id = UniqueGenericableId.create(fitable.getGenericableId(), genericableVersion);
            ConfigurableGenericable configurableGenericable =
                    genericables.computeIfAbsent(id, this.genericableFactory::create);
            String fitableVersion = StringUtils.blankIf(fitable.getFitableVersion(), FitableMetadata.DEFAULT_VERSION);
            ConfigurableFitable configurableFitable =
                    this.fitableFactory.create(fitable.getFitableId(), fitableVersion);
            configurableFitable.aliases(new HashSet<>(instance.getMeta().getAliases()));
            configurableFitable.tags(new HashSet<>(instance.getMeta().getTags()));
            configurableFitable.genericable(configurableGenericable);
            configurableGenericable.appendFitable(configurableFitable);
        }
        return new ArrayList<>(genericables.values());
    }

    private List<GenericableInfo> buildQueryRunningFitablesParams(Set<UniqueGenericableId> ids) {
        return ids.stream().map(this::buildGenericable).collect(Collectors.toList());
    }

    private GenericableInfo buildGenericable(UniqueGenericableId id) {
        GenericableInfo genericable = new GenericableInfo();
        genericable.setGenericableId(id.genericableId());
        genericable.setGenericableVersion(id.genericableVersion());
        return genericable;
    }

    @Override
    public String name() {
        return "registered";
    }

    @Override
    public Optional<Genericable> get(String id, String version) {
        Optional<Genericable> opFromRegistry = this.getFromRegistry(id, version);
        Optional<Genericable> opFromLocal = this.localRepository.get(id, version);
        if (!opFromRegistry.isPresent()) {
            return opFromLocal;
        } else if (!opFromLocal.isPresent()) {
            return opFromRegistry;
        } else {
            ConfigurableGenericable configurableGenericable = this.genericableFactory.create(id, version);
            opFromRegistry.get().fitables().forEach(fitable -> this.appendFitable(configurableGenericable, fitable));
            opFromLocal.get().fitables().forEach(fitable -> this.appendFitable(configurableGenericable, fitable));
            return Optional.of(configurableGenericable);
        }
    }

    private void appendFitable(ConfigurableGenericable configurableGenericable, Fitable fitable) {
        ConfigurableFitable configurableFitable = this.fitableFactory.create(fitable);
        configurableFitable.genericable(configurableGenericable);
        configurableGenericable.appendFitable(configurableFitable);
    }

    private Optional<Genericable> getFromRegistry(String id, String version) {
        UniqueGenericableId uniqueId = UniqueGenericableId.create(id, version);
        if (this.registeredGenericables.containsKey(uniqueId)) {
            return Optional.of(this.registeredGenericables.get(uniqueId));
        }
        List<ConfigurableGenericable> runningGenericables =
                this.getRunningGenericables(Collections.singleton(uniqueId));
        if (runningGenericables.isEmpty()) {
            return Optional.empty();
        }
        Validation.isTrue(runningGenericables.size() == 1, "Too many genericables. [id={0}]", uniqueId);
        this.registeredGenericables.put(uniqueId, runningGenericables.get(0));
        return Optional.of(runningGenericables.get(0));
    }
}
