/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.support;

import com.huawei.fitframework.jvm.classfile.ClassFile;
import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.protocol.jar.JarCache;
import com.huawei.fitframework.protocol.jar.JarLocation;
import com.huawei.fitframework.util.LockUtils;
import com.huawei.fitframework.util.StringUtils;

import sun.misc.Resource;
import sun.net.www.ParseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表示 {@link URL} 的类路径。
 *
 * @author 季聿阶 j00559309
 * @since 2023-09-01
 */
public class UrlClassPath {
    private final List<URL> path = new ArrayList<>();
    private final Map<String, List<URL>> packageUrls = new ConcurrentHashMap<>();

    private final Object lock = LockUtils.newSynchronizedLock();

    /**
     * 向当前 URL 类路径中添加一个指定的 URL。
     *
     * @param url 表示指定的待添加的 {@link URL}。
     */
    public synchronized void addURL(URL url) {
        synchronized (this.lock) {
            if (url == null || this.path.contains(url)) {
                return;
            }
            this.path.add(url);
            try {
                Jar jar = JarCache.instance().get(JarLocation.parse(url));
                Set<String> packages = this.getPackages(jar);
                packages.forEach(sp -> this.packageUrls.computeIfAbsent(sp, key -> new ArrayList<>()).add(url));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to add url.", e);
            }
        }
    }

    private Set<String> getPackages(Jar jar) {
        return jar.entries()
                .stream()
                .map(this::getPackage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Optional<String> getPackage(Jar.Entry entry) {
        String name = entry.name();
        if (name.startsWith("META-INF") || name.startsWith("FIT-INF")) {
            return Optional.empty();
        }
        if (!name.endsWith(ClassFile.FILE_EXTENSION)) {
            return Optional.empty();
        }
        int index = name.lastIndexOf('/');
        String sp = index < 0 ? StringUtils.EMPTY : name.substring(0, index).replace('/', '.');
        return Optional.of(sp);
    }

    /**
     * 获取当前 URL 类路径中所有的 URL 对象。
     *
     * @return 表示当前 URL 类路径中所有的 URL 对象的 {@link URL}{@code []}。
     */
    public URL[] getURLs() {
        synchronized (this.lock) {
            return this.path.toArray(new URL[0]);
        }
    }

    /**
     * 在当前 URL 类路径中搜索指定名字的资源。
     *
     * @param name 表示指定资源名字的 {@link String}。
     * @return 表示指定名字资源的 {@link Optional}{@code <}{@link URL}{@code >}。
     */
    public Optional<URL> findResource(String name) {
        return this.getResource(name).map(Resource::getURL);
    }

    /**
     * 在当前 URL 类路径中搜索指定名字的资源。
     *
     * @param name 表示指定资源名字的 {@link String}。
     * @return 表示指定名字资源的 {@link Optional}{@code <}{@link Resource}{@code >}。
     */
    public Optional<Resource> getResource(String name) {
        if (StringUtils.isBlank(name)) {
            return Optional.empty();
        }
        List<URL> toSearchUrls = this.getToSearchUrls(name);
        return toSearchUrls.stream()
                .map(url -> this.getResource(url, name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    /**
     * 在当前 URL 类路径中搜索指定名字的资源列表。
     *
     * @param name 表示指定资源名字的 {@link String}。
     * @return 表示指定名字资源列表的 {@link List}{@code <}{@link URL}{@code >}。
     */
    public List<URL> findResources(String name) {
        return this.getResources(name).stream().map(Resource::getURL).collect(Collectors.toList());
    }

    /**
     * 在当前 URL 类路径中搜索指定名字的资源列表。
     *
     * @param name 表示指定资源名字的 {@link String}。
     * @return 表示指定名字资源列表的 {@link List}{@code <}{@link Resource}{@code >}。
     */
    public List<Resource> getResources(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }
        return this.getToSearchUrls(name)
                .stream()
                .map(url -> this.getResource(url, name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<URL> getToSearchUrls(String name) {
        synchronized (this.lock) {
            if (name.endsWith(ClassFile.FILE_EXTENSION)) {
                String classPackage = this.getClassPackage(name);
                List<URL> urls = this.packageUrls.get(classPackage);
                return urls == null ? new ArrayList<>() : new ArrayList<>(urls);
            } else {
                return new ArrayList<>(this.path);
            }
        }
    }

    private String getClassPackage(String className) {
        int index = className.lastIndexOf('/');
        return index < 0 ? StringUtils.EMPTY : className.substring(0, index).replace('/', '.');
    }

    private Optional<Resource> getResource(URL url, String name) {
        URL baseUrl;
        String file = url.getFile();
        if (file != null && file.endsWith("/")) {
            baseUrl = url;
        } else {
            try {
                baseUrl = new URL("jar", "", -1, url + "!/");
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Failed to create base url.", e);
            }
        }
        final URL actual;
        try {
            actual = new URL(baseUrl, ParseUtil.encodePath(name, false));
        } catch (MalformedURLException e) {
            throw new IllegalStateException(StringUtils.format("Failed to get resource. [url={0}, name={1}]",
                    url.toExternalForm(),
                    name));
        }
        final URLConnection uc;
        try {
            uc = actual.openConnection();
            // 仅仅为了检测资源是否存在，因此尝试打开链接，不做任何处理。
            try (InputStream ignored = uc.getInputStream()) {}
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(new UrlResource(name, actual, url, uc));
    }

    private static class UrlResource extends Resource {
        private final String name;
        private final URL url;
        private final URL codeSourceUrl;
        private final URLConnection urlConnection;

        public UrlResource(String name, URL url, URL codeSourceUrl, URLConnection urlConnection) {
            this.name = name;
            this.url = url;
            this.codeSourceUrl = codeSourceUrl;
            this.urlConnection = urlConnection;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public URL getURL() {
            return this.url;
        }

        @Override
        public URL getCodeSourceURL() {
            return this.codeSourceUrl;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.urlConnection.getInputStream();
        }

        @Override
        public int getContentLength() {
            return this.urlConnection.getContentLength();
        }
    }
}
