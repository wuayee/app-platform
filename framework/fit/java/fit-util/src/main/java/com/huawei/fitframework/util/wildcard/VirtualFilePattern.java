/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard;

import com.huawei.fitframework.io.virtualization.VirtualDirectory;
import com.huawei.fitframework.io.virtualization.VirtualFile;

import java.util.List;

/**
 * 为虚拟文件提供匹配模式。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-04
 */
public interface VirtualFilePattern extends Pattern<String> {
    /**
     * 在目录中查找符合预期的文件。
     *
     * @param parent 表示父目录的 {@link VirtualDirectory}。父目录不计入匹配范围。
     * @return 表示匹配到的子目录的列表的 {@link List}{@code <}{@link VirtualFile}{@code >}。
     */
    List<VirtualFile> match(VirtualDirectory parent);
}
