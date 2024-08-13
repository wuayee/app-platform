/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.resource.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.protocol.jar.JarEntryLocation;
import com.huawei.fitframework.resource.ResourceTree;
import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 为资源树提供基于目录的实现。
 *
 * @author 梁济时
 * @since 2023-02-10
 */
final class DirectoryResourceTree extends AbstractResourceTree {
    private final File directory;

    private URL location;

    DirectoryResourceTree(File directory) {
        this.directory = directory;
        buildTree(this.roots(), nullIf(directory.listFiles(), new File[0]));
    }

    private static void buildTree(ResourceNodeCollection collection, File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (ArrayUtils.isEmpty(children)) {
                    continue;
                }
                ResourceTreeDirectoryNode directoryNode = collection.mkdirs(file.getName());
                buildTree(directoryNode.children(), children);
            } else {
                FileNode node = new FileNode(collection, file);
                collection.add(node);
            }
        }
    }

    final File directory() {
        return this.directory;
    }

    @Override
    public URL location() throws MalformedURLException {
        if (this.location == null) {
            this.location = this.directory.toURI().toURL();
        }
        return this.location;
    }

    private static final class FileNode extends AbstractResourceNode implements ResourceTree.FileNode {
        private final ResourceNodeCollection collection;
        private final File file;

        private URL url;
        private String path;

        private FileNode(ResourceNodeCollection collection, File file) {
            this.collection = collection;
            this.file = file;
        }

        @Override
        public URL url() {
            if (this.url == null) {
                this.url = FileUtils.urlOf(this.file);
            }
            return this.url;
        }

        @Override
        public InputStream read() throws IOException {
            return new FileInputStream(this.file);
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

        @Nullable
        @Override
        public DirectoryNode directory() {
            return this.collection.directory();
        }

        @Override
        public String filename() {
            return this.file.getName();
        }

        @Override
        public String path() {
            if (this.path == null) {
                List<String> paths = new LinkedList<>();
                paths.add(this.filename());
                DirectoryNode directoryNode = this.directory();
                while (directoryNode != null) {
                    paths.add(0, directoryNode.name());
                    directoryNode = directoryNode.parent();
                }
                this.path = StringUtils.join(JarEntryLocation.ENTRY_PATH_SEPARATOR, paths);
            }
            return this.path;
        }

        @Override
        public String toString() {
            return this.file.getPath();
        }
    }

    @Override
    public String toString() {
        return this.directory.getPath();
    }
}
