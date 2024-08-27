/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.plugin.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.jvm.classfile.ClassFile;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.support.IteratorEnumerationAdapter;
import sun.misc.Resource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * 为插件提供类加载器。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-06-29
 */
public class PluginClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    private final UrlClassPath ucp;
    private final AccessControlContext acc;

    /**
     * 向插件类加载器中添加一个 URL。
     *
     * @param parent 表示父类加载器的 {@link ClassLoader}。
     */
    public PluginClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        this.ucp = new UrlClassPath();
        this.acc = AccessController.getContext();
    }

    /**
     * 向插件类加载器中添加一个 URL。
     *
     * @param url 表示待添加的 {@link URL}。
     */
    public void add(URL url) {
        this.ucp.addURL(url);
    }

    @Override
    public URL[] getURLs() {
        return this.ucp.getURLs();
    }

    @Override
    public URL findResource(String name) {
        return this.ucp.findResource(name).orElse(null);
    }

    @Override
    public Enumeration<URL> findResources(String name) {
        List<URL> urls = this.ucp.findResources(name);
        return new IteratorEnumerationAdapter<>(urls.iterator());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final Class<?> result;
        try {
            result = AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>) () -> findClass0(name),
                    this.acc);
        } catch (PrivilegedActionException e) {
            // 因为 findClass0 方法仅会抛出 ClassNotFoundException，所以需要将 PrivilegedActionException 进行转换。
            throw ObjectUtils.<ClassNotFoundException>cast(e.getException());
        }
        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;
    }

    private Class<?> findClass0(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(ClassFile.FILE_EXTENSION);
        Optional<Resource> opRes = this.ucp.getResource(path);
        if (opRes.isPresent()) {
            return this.defineClass(name, opRes.get());
        } else {
            return null;
        }
    }

    private Class<?> defineClass(String name, Resource res) throws ClassNotFoundException {
        try {
            return this.defineClassFromUrlClassLoader(name, res);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    private Class<?> defineClassFromUrlClassLoader(String name, Resource res) throws IOException {
        Method method =
                ReflectionUtils.getDeclaredMethod(URLClassLoader.class, "defineClass", String.class, Resource.class);
        return cast(ReflectionUtils.invoke(this, method, name, res));
    }
}

