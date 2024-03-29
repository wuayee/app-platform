/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.filesystem.support;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.filesystem.FileChangedObserver;
import com.huawei.fitframework.filesystem.FileCreatedObserver;
import com.huawei.fitframework.filesystem.FileObservers;
import com.huawei.fitframework.filesystem.FileTreeVisitedObserver;
import com.huawei.fitframework.filesystem.FileTreeVisitingObserver;
import com.huawei.fitframework.filesystem.FileVisitedFailedObserver;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 表示普通文件的访问器。
 *
 * @author 季聿阶 j00559309
 * @since 2023-07-26
 */
public class RegularFileVisitor extends SimpleFileVisitor<Path> {
    private final Set<String> extensions;
    private final Map<File, FileTime> previousModifiedTimes;
    private final Map<File, FileTime> currentModifiedTimes;

    private final FileCreatedObserver createdObserver;
    private final FileChangedObserver changedObserver;
    private final FileVisitedFailedObserver visitedFailedObserver;
    private final FileTreeVisitingObserver treeVisitingObserver;
    private final FileTreeVisitedObserver treeVisitedObserver;

    RegularFileVisitor(Set<String> extensions, Map<File, FileTime> signatures, FileObservers observers) {
        this.extensions = getIfNull(extensions, HashSet::new);
        this.previousModifiedTimes = getIfNull(signatures, HashMap::new);
        this.currentModifiedTimes = new HashMap<>();
        FileObservers actual = getIfNull(observers, () -> FileObservers.builder().build());
        this.createdObserver = getIfNull(actual.created(), FileCreatedObserver::empty);
        this.changedObserver = getIfNull(actual.changed(), FileChangedObserver::empty);
        this.visitedFailedObserver = getIfNull(actual.visitedFailed(), FileVisitedFailedObserver::empty);
        this.treeVisitingObserver = getIfNull(actual.treeVisiting(), FileTreeVisitingObserver::empty);
        this.treeVisitedObserver = getIfNull(actual.treeVisited(), FileTreeVisitedObserver::empty);
    }

    Map<File, FileTime> getCurrentModifiedTimes() {
        return this.currentModifiedTimes;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        File actual = file.toFile();
        if (this.isFileInvalid(actual)) {
            return FileVisitResult.CONTINUE;
        }
        FileTime lastModifiedTime = attrs.lastModifiedTime();
        this.currentModifiedTimes.put(actual, lastModifiedTime);
        FileTime preLastModifiedTime = this.previousModifiedTimes.get(actual);
        if (preLastModifiedTime == null) {
            this.createdObserver.onFileCreated(actual);
            return FileVisitResult.CONTINUE;
        }
        if (!Objects.equals(preLastModifiedTime, lastModifiedTime)) {
            this.changedObserver.onFileChanged(actual);
        }
        return FileVisitResult.CONTINUE;
    }

    private boolean isFileInvalid(File actual) {
        if (this.extensions.contains(DefaultDirectoryMonitor.ALL)) {
            return false;
        }
        for (String extension : this.extensions) {
            if (StringUtils.endsWithIgnoreCase(actual.getName(), extension)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exception) {
        if (exception != null) {
            this.visitedFailedObserver.onFileVisitedFailed(file.toFile(), exception);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exception) {
        if (exception != null) {
            this.visitedFailedObserver.onFileVisitedFailed(dir.toFile(), exception);
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * 访问整个文件目录树之前触发的事件。
     *
     * @param dir 表示整个文件目录树的根的 {@link Path}。
     */
    public void preVisitFileTree(Path dir) {
        this.treeVisitingObserver.onFileTreeVisiting(dir.toFile());
    }

    /**
     * 访问完整个文件目录树后触发的事件。
     *
     * @param dir 表示整个文件目录树的根的 {@link Path}。
     */
    public void postVisitFileTree(Path dir) {
        this.treeVisitedObserver.onFileTreeVisited(dir.toFile());
    }
}
