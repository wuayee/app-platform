/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.io.virtualization;

/**
 * 为虚拟文件系统提供元素。
 *
 * @author 梁济时
 * @since 2022-08-01
 */
public interface VirtualFileSystemElement {
    /**
     * 获取文件系统元素的名称。
     *
     * @return 表示文件系统元素名称的 {@link String}。
     */
    String name();

    /**
     * 获取文件系统元素的路径。
     *
     * @return 表示文件系统元素路径的 {@link String}。
     */
    String path();
}
