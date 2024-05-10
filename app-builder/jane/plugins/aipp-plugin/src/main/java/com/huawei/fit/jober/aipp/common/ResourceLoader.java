/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 用于加载resource目录下的资源
 *
 * @author x00576283
 * @since 2024/1/12
 */
public class ResourceLoader {
    /**
     * 加载指定path的文件内容
     *
     * @param plugin 资源加载器
     * @param path 文件路径
     * @return 文件内容
     * @throws IOException 文件的io异常
     */
    public static String loadFileData(Plugin plugin, String path) throws IOException {
        List<Resource> resources = Arrays.asList(plugin.resolverOfResources().resolve(path));
        if (resources.isEmpty()) {
            throw new IOException("component resource not found.");
        }
        String file = Utils.readInputStream(resources.get(0).read());
        if (StringUtils.isBlank(file)) {
            throw new IOException(path + " component file is empty.");
        }

        // 校验json
        if (!JsonUtils.isValidJson(file)) {
            throw new IOException(path + " component file is invalid json.");
        }
        return file;
    }
}
