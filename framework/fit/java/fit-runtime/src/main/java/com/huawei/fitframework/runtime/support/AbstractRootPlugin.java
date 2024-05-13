/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.runtime.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.aop.AopInterceptor;
import com.huawei.fitframework.broker.DynamicRouter;
import com.huawei.fitframework.broker.FitExceptionCreator;
import com.huawei.fitframework.broker.FitableFactory;
import com.huawei.fitframework.broker.LoadBalancer;
import com.huawei.fitframework.broker.SerializationService;
import com.huawei.fitframework.broker.TargetLocator;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.RouterFactory;
import com.huawei.fitframework.broker.client.support.DefaultBrokerClient;
import com.huawei.fitframework.broker.client.support.DefaultInvokerFactory;
import com.huawei.fitframework.broker.client.support.DefaultRouterFactory;
import com.huawei.fitframework.broker.serialization.DefaultSerializationService;
import com.huawei.fitframework.broker.server.Dispatcher;
import com.huawei.fitframework.broker.server.support.DefaultDispatcher;
import com.huawei.fitframework.broker.support.DefaultDynamicRouter;
import com.huawei.fitframework.broker.support.DefaultFitExceptionCreator;
import com.huawei.fitframework.broker.support.DefaultFitableFactory;
import com.huawei.fitframework.broker.support.DefaultGenericableFactory;
import com.huawei.fitframework.broker.support.DefaultLoadBalancer;
import com.huawei.fitframework.broker.support.DefaultTargetLocator;
import com.huawei.fitframework.conf.runtime.ApplicationConfig;
import com.huawei.fitframework.conf.runtime.DefaultApplication;
import com.huawei.fitframework.conf.runtime.DefaultMatata;
import com.huawei.fitframework.conf.runtime.DefaultWorker;
import com.huawei.fitframework.conf.runtime.MatataConfig;
import com.huawei.fitframework.conf.runtime.WorkerConfig;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginCategory;
import com.huawei.fitframework.plugin.PluginComparators;
import com.huawei.fitframework.plugin.RootPlugin;
import com.huawei.fitframework.plugin.support.AbstractPlugin;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为根插件提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2023-02-07
 */
public abstract class AbstractRootPlugin extends AbstractPlugin implements RootPlugin {
    private static final Logger log = Logger.get(AbstractRootPlugin.class);

    private static final String FRAMEWORK_CONFIG_PREFIX = "fitframework";
    private static final String FIT_RUNTIME_BEAN_NAME = "fitRuntime";
    private static final String AOP_INTERCEPTOR_BEAN_NAME = "aopInterceptor";
    private static final String SERIALIZATION_SERVICE_BEAN_NAME = "serializationService";
    private static final String DYNAMIC_ROUTER_BEAN_NAME = "dynamicRouter";
    private static final String GENERICABLE_FACTORY_BEAN_NAME = "genericableFactory";
    private static final String TARGET_LOCATOR_BEAN_NAME = "targetLocator";
    private static final String LOAD_BALANCER_BEAN_BANE = "loadBalancer";
    private static final String FIT_EXCEPTION_CREATOR_BEAN_NAME = "fitExceptionCreator";
    private static final String FITABLE_FACTORY_BEAN_NAME = "fitableFactory";
    private static final String INVOKER_FACTORY_BEAN_NAME = "invokerFactory";
    private static final String LOCAL_GENERICABLE_REPOSITORY_NAME = "localGenericableRepository";
    private static final String ROUTER_FACTORY_BEAN_NAME = "routerFactory";
    private static final String BROKER_CLIENT_BEAN_NAME = "brokerClient";
    private static final String DISPATCHER_BEAN_NAME = "dispatcher";
    private static final String MATATA_CONFIG_BEAN_NAME = "matataConfig";
    private static final String WORKER_CONFIG_BEAN_NAME = "workerConfig";
    private static final String APPLICATION_CONFIG = "applicationConfig";

    private MatataConfig matata;
    private WorkerConfig worker;

    @Override
    public final Plugin parent() {
        return null;
    }

    @Override
    protected String getBuiltinConfigPrefix() {
        return FRAMEWORK_CONFIG_PREFIX;
    }

    @Override
    protected void registerSystemBeans() {
        this.container().registry().register(this.runtime(), FIT_RUNTIME_BEAN_NAME);
        this.registerConfigBeans();
        this.registerAopBeans();
        this.registerBrokerBeans();
        super.registerSystemBeans();
    }

    private void registerConfigBeans() {
        ApplicationConfig application = this.config().get("application", DefaultApplication.class);
        this.container().registry().register(application, APPLICATION_CONFIG);
        log.debug("Config 'application.*' is {}.", application);
        this.matata = this.config().get("matata", DefaultMatata.class);
        this.container().registry().register(this.matata, MATATA_CONFIG_BEAN_NAME);
        log.debug("Config 'matata.*' is {}.", this.matata);
        this.worker = this.config().get("worker", DefaultWorker.class);
        this.container().registry().register(this.worker, WORKER_CONFIG_BEAN_NAME);
        log.debug("Config 'worker.*' is {}.", this.worker);
    }

    private void registerAopBeans() {
        this.container().registry().register(new AopInterceptor(this.container()), AOP_INTERCEPTOR_BEAN_NAME);
    }

    private void registerBrokerBeans() {
        SerializationService serializationService = new DefaultSerializationService(this.container());
        this.container().registry().register(serializationService, SERIALIZATION_SERVICE_BEAN_NAME);
        DynamicRouter dynamicRouter = new DefaultDynamicRouter();
        this.container().registry().register(dynamicRouter, DYNAMIC_ROUTER_BEAN_NAME);
        DefaultGenericableFactory genericableFactory = new DefaultGenericableFactory(dynamicRouter);
        this.container().registry().register(genericableFactory, GENERICABLE_FACTORY_BEAN_NAME);
        TargetLocator targetLocator =
                new DefaultTargetLocator(this.container(), this.worker, this.matata.registry().availableServices());
        this.container().registry().register(targetLocator, TARGET_LOCATOR_BEAN_NAME);
        LoadBalancer loadBalancer = new DefaultLoadBalancer(this.container(), serializationService, targetLocator);
        this.container().registry().register(loadBalancer, LOAD_BALANCER_BEAN_BANE);
        FitExceptionCreator exceptionCreator = new DefaultFitExceptionCreator(this.container());
        this.container().registry().register(exceptionCreator, FIT_EXCEPTION_CREATOR_BEAN_NAME);
        FitableFactory fitableFactory = new DefaultFitableFactory(this.container(), loadBalancer);
        this.container().registry().register(fitableFactory, FITABLE_FACTORY_BEAN_NAME);
        DefaultInvokerFactory invokerFactory = new DefaultInvokerFactory(this.container(),
                genericableFactory,
                fitableFactory,
                this.config(),
                this.worker);
        this.container().registry().register(invokerFactory, INVOKER_FACTORY_BEAN_NAME);
        this.container()
                .registry()
                .register(invokerFactory.localGenericableRepository(), LOCAL_GENERICABLE_REPOSITORY_NAME);
        RouterFactory routerFactory = new DefaultRouterFactory(invokerFactory);
        this.container().registry().register(routerFactory, ROUTER_FACTORY_BEAN_NAME);
        BrokerClient brokerClient = new DefaultBrokerClient(routerFactory);
        this.container().registry().register(brokerClient, BROKER_CLIENT_BEAN_NAME);
        Dispatcher dispatcher = new DefaultDispatcher(this.container(), this.worker);
        this.container().registry().register(dispatcher, DISPATCHER_BEAN_NAME);
    }

    @Override
    protected void onInitialized() {
        this.loadPlugins();
        log.debug("Total {} plugins loaded.", this.children().size());
        super.onInitialized();
        this.obtainChildrenForStartup(PluginCategory.SYSTEM).forEach(Plugin::initialize);
    }

    @Override
    protected void onStarted() {
        this.obtainChildrenForStartup(PluginCategory.SYSTEM).forEach(Plugin::start);
        super.onStarted();
        this.obtainChildrenForStartup(PluginCategory.USER).forEach(Plugin::start);
    }

    /**
     * 加载插件。
     */
    protected abstract void loadPlugins();

    private List<Plugin> obtainChildrenForStartup(PluginCategory category) {
        return this.children()
                .stream()
                .filter(plugin -> Objects.equals(plugin.metadata().category(), category))
                .sorted(AbstractRootPlugin::sortPlugins)
                .collect(Collectors.toList());
    }

    private static int sortPlugins(Plugin plugin1, Plugin plugin2) {
        return PluginComparators.STARTUP.compare(plugin1, plugin2);
    }

    @Override
    public Plugin loadPlugin(URL plugin) {
        notNull(plugin, "The plugin file to load cannot be null.");
        return this.children().add(plugin);
    }

    @Override
    public Plugin unloadPlugin(URL plugin) {
        notNull(plugin, "The plugin file to unload cannot be null.");
        return this.children().remove(plugin);
    }
}
