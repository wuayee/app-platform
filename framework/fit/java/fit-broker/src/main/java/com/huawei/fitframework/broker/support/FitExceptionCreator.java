/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.exception.Errors;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStoppedObserver;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.FitRuntimeStartedObserver;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.tlv.TlvUtils;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表示通过异常码、异常消息等属性创建 FIT 系统异常的构建器。
 *
 * @author 何天放 h00679269
 * @since 2024-05-07
 */
@Component
public class FitExceptionCreator implements PluginStartedObserver, PluginStoppedObserver, FitRuntimeStartedObserver {
    private static final Logger log = Logger.get(FitExceptionCreator.class);
    private static final String ERRORS_RESOURCE_FILE = "FIT-INF/errors.xml";

    private final BeanContainer container;
    private volatile Map<Integer, Class<? extends FitException>> errorCodes = new HashMap<>();

    FitExceptionCreator(BeanContainer container) {
        this.container = container;
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        this.updateErrorCodes();
    }

    @Override
    public void onPluginStopped(Plugin plugin) {
        this.updateErrorCodes();
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        this.updateErrorCodes();
    }

    /**
     * 创建 FIT 系统异常。
     *
     * @param exceptionInfo 表示异常元数据的 {@link ExceptionInfo}。
     * @return 表示所创建 FIT 系统异常的 {@link FitException}。
     */
    public FitException buildException(ExceptionInfo exceptionInfo) {
        Optional<Class<? extends FitException>> opClass = Errors.exceptionClass(exceptionInfo.code());
        FitException exception;
        if (opClass.isPresent()) {
            Constructor<? extends FitException> declaredConstructor =
                    ReflectionUtils.getDeclaredConstructor(opClass.get(), String.class);
            exception = ReflectionUtils.instantiate(declaredConstructor, exceptionInfo.message());
        } else {
            if (this.errorCodes.containsKey(exceptionInfo.code())) {
                Class<? extends FitException> responseExceptionClass = this.errorCodes.get(exceptionInfo.code());
                Constructor<? extends FitException> declaredConstructor =
                        ReflectionUtils.getDeclaredConstructor(responseExceptionClass, String.class);
                exception = ReflectionUtils.instantiate(declaredConstructor, exceptionInfo.message());
            } else {
                exception = new FitException(exceptionInfo.code(), exceptionInfo.message());
            }
        }
        exception.setProperties(exceptionInfo.properties());
        exception.associateFitable(exceptionInfo.genericableId(), exceptionInfo.fitableId());
        return exception;
    }

    private void updateErrorCodes() {
        try {
            this.errorCodes = this.getErrorCodesFromResources();
        } catch (IOException e) {
            throw new IllegalStateException(StringUtils.format("Failed to read errors from resources. [file={0}]",
                    ERRORS_RESOURCE_FILE), e);
        }
    }

    private Map<Integer, Class<? extends FitException>> getErrorCodesFromResources() throws IOException {
        Map<Integer, Class<? extends FitException>> exceptions = new HashMap<>();
        Enumeration<URL> resources = this.container.runtime().sharedClassLoader().getResources(ERRORS_RESOURCE_FILE);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try (InputStream in = url.openStream()) {
                exceptions.putAll(this.readInputStream(in));
            }
        }
        return exceptions;
    }

    private Map<Integer, Class<? extends FitException>> readInputStream(InputStream in) {
        Document xml = XmlUtils.load(in);
        Map<Integer, Class<? extends FitException>> exceptions = new HashMap<>();
        Element root = XmlUtils.child(xml, "errors");
        if (root == null) {
            return exceptions;
        }
        NodeList errorNodes = XmlUtils.filterByName(root.getChildNodes(), "error");
        for (int i = 0; i < errorNodes.getLength(); i++) {
            Node errorNode = errorNodes.item(i);
            Element errorElement = cast(errorNode);
            int code = Integer.parseInt(errorElement.getAttribute("code"));
            String className = errorElement.getAttribute("class");
            Class<? extends FitException> fitExceptionClass;
            try {
                fitExceptionClass = cast(this.container.runtime().sharedClassLoader().loadClass(className));
            } catch (ClassNotFoundException e) {
                log.warn("Failed to load exception class, use FitException instead. [code={}, class={}]",
                        code,
                        className);
                fitExceptionClass = FitException.class;
            }
            exceptions.put(code, fitExceptionClass);
        }
        return exceptions;
    }

    /**
     * 表示异常的信息。
     */
    public interface ExceptionInfo {
        /**
         * 获取服务的唯一标识。
         *
         * @return 表示服务唯一标识的 {@link String}。
         */
        String genericableId();

        /**
         * 获取服务实现的唯一标识。
         *
         * @return 表示服务实现唯一标识的 {@link String}。
         */
        String fitableId();

        /**
         * 获取异常状态码。
         *
         * @return 表示状态码的 {@code int}。
         */
        int code();

        /**
         * 获取异常的消息。
         *
         * @return 表示异常消息的 {@link String}。
         */
        String message();

        /**
         * 获取异常的属性集。
         *
         * @return 表示异常属性集的 {@link String}。
         */
        Map<String, String> properties();

        /**
         * 通过各个属性构建异常信息。
         *
         * @param genericableId 表示服务唯一标识的 {@link String}。
         * @param fitableId 表示服务实现唯一标识的 {@link String}。
         * @param code 表示状态码的 {@code int}。
         * @param message 表示异常消息的 {@link String}。
         * @param properties 表示异常属性集的 {@link String}。
         * @return 表示异常信息的 {@link ExceptionInfo}。
         */
        static ExceptionInfo create(String genericableId, String fitableId, int code, String message,
                Map<String, String> properties) {
            return new DefaultExceptionInfo(genericableId, fitableId, code, message, properties);
        }

        /**
         * 通过泛服务实现对象和返回值元数据构建异常信息。
         *
         * @param fitable 表示泛服务实现对象的 {@link Fitable}。
         * @param responseMetadata 表示返回值元数据的 {@link ResponseMetadata}。
         * @return 表示异常信息的 {@link ExceptionInfo}。
         */
        static ExceptionInfo fromFitableAndResponseMetadata(Fitable fitable, ResponseMetadata responseMetadata) {
            notNull(fitable, "The fitable cannot be null.");
            notNull(responseMetadata, "The response metadata cannot be null.");
            Map<String, String> properties = TlvUtils.getExceptionProperties(responseMetadata.tagValues());
            return new DefaultExceptionInfo(fitable.genericable().id(),
                    fitable.id(),
                    responseMetadata.code(),
                    responseMetadata.message(),
                    properties);
        }
    }

    private static class DefaultExceptionInfo implements ExceptionInfo {
        private final String genericableId;
        private final String fitableId;
        private final int code;
        private final String message;
        private final Map<String, String> properties;

        DefaultExceptionInfo(String genericableId, String fitableId, int code, String message,
                Map<String, String> properties) {
            this.genericableId = notBlank(genericableId, "The genericable id cannot be blank.");
            this.fitableId = notBlank(fitableId, "The fitable id cannot be blank.");
            this.code = code;
            this.message = StringUtils.blankIf(message, StringUtils.EMPTY);
            if (MapUtils.isEmpty(properties)) {
                this.properties = new HashMap<>();
            } else {
                this.properties = properties;
            }
        }

        @Override
        public String genericableId() {
            return this.genericableId;
        }

        @Override
        public String fitableId() {
            return this.fitableId;
        }

        @Override
        public int code() {
            return this.code;
        }

        @Override
        public String message() {
            return this.message;
        }

        @Override
        public Map<String, String> properties() {
            return this.properties;
        }
    }
}
