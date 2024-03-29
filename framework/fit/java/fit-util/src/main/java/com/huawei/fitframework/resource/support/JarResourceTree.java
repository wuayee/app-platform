/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.resource.support;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.protocol.jar.JarEntryLocation;
import com.huawei.fitframework.resource.ResourceTree;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 为 {@link ResourceTree} 提供基于 JAR 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-02-10
 */
final class JarResourceTree extends AbstractResourceTree {
    private final Jar jar;

    JarResourceTree(Jar jar) {
        this.jar = jar;
        for (Jar.Entry entry : this.jar.entries()) {
            if (entry.directory()) {
                continue;
            }
            String[] path = StringUtils.split(entry.name(), JarEntryLocation.ENTRY_PATH_SEPARATOR);
            ResourceNodeCollection collection = super.roots();
            int index = path.length - 1;
            for (int i = 0; i < index; i++) {
                ResourceTreeDirectoryNode directory = collection.mkdirs(path[i]);
                collection = directory.children();
            }
            FileNode file = new FileNode(collection, path[index], entry);
            collection.add(file);
        }
    }

    final Jar jar() {
        return this.jar;
    }

    @Override
    public URL location() throws MalformedURLException {
        return this.jar.location().toUrl();
    }

    @Override
    public String toString() {
        return this.jar.toString();
    }

    private static final class FileNode extends AbstractResourceNode implements ResourceTree.FileNode {
        private final ResourceNodeCollection collection;
        private final String name;
        private final Jar.Entry entry;

        private FileNode(ResourceNodeCollection collection, String name, Jar.Entry entry) {
            this.collection = collection;
            this.name = name;
            this.entry = entry;
        }

        @Override
        public URL url() throws MalformedURLException {
            return this.entry.location().toUrl();
        }

        @Override
        public InputStream read() throws IOException {
            return this.entry.read();
        }

        @Nullable
        @Override
        public DirectoryNode directory() {
            return this.collection.directory();
        }

        @Nonnull
        @Override
        public ResourceTree tree() {
            return this.collection.tree();
        }

        @Override
        public String name() {
            return this.filename();
        }

        @Override
        public String filename() {
            return this.name;
        }

        @Override
        public String path() {
            return this.entry.name();
        }
    }
}
