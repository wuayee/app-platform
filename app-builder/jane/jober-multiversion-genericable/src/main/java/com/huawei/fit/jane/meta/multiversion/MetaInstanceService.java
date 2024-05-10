/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.meta.multiversion;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Genericable;

/**
 * 元数据实例服务
 *
 * @author 陈镕希 c00572808
 * @since 2023-12-08
 */
public interface MetaInstanceService {
    /**
     * 创建meta实例。
     *
     * @param versionId meta版本唯一标识的 {@link String}。
     * @param instanceDeclarationInfo meta实例信息的 {@link InstanceDeclarationInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws com.huawei.fit.jober.common.JoberGenericableException 当调用过程发生异常。
     * @throws com.huawei.fit.jober.common.BadRequestException 当调用过程发生错误请求异常。
     * @throws com.huawei.fit.jober.common.TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws com.huawei.fit.jober.common.ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "933aad4f2a694afa9b85dc294bb5b9d0")
    Instance createMetaInstance(String versionId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context);

    /**
     * 更新meta实例。
     *
     * @param versionId meta版本唯一标识的 {@link String}。
     * @param instanceId meta实例唯一标识的 {@link String}。
     * @param instanceDeclarationInfo meta实例信息的 {@link InstanceDeclarationInfo}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws com.huawei.fit.jober.common.JoberGenericableException 当调用过程发生异常。
     * @throws com.huawei.fit.jober.common.BadRequestException 当调用过程发生错误请求异常。
     * @throws com.huawei.fit.jober.common.TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws com.huawei.fit.jober.common.ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "5f346f89a16741a2b4999b82fb37f25d")
    void patchMetaInstance(String versionId, String instanceId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context);

    /**
     * 删除meta实例。
     *
     * @param versionId meta版本唯一标识的 {@link String}。
     * @param instanceId meta实例唯一标识的 {@link String}。
     * @param context 操作人相关上下文的 {@link OperationContext}。
     * @throws com.huawei.fit.jober.common.JoberGenericableException 当调用过程发生异常。
     * @throws com.huawei.fit.jober.common.BadRequestException 当调用过程发生错误请求异常。
     * @throws com.huawei.fit.jober.common.TooManyRequestException 当调用过程发生请求超出限制异常。
     * @throws com.huawei.fit.jober.common.ServerInternalException 当调用过程发生服务器内部异常。
     */
    @Genericable(id = "be9f237d34d645ab9690fff1a344a01b")
    void deleteMetaInstance(String versionId, String instanceId, OperationContext context);

    /**
     * 查询meta实例。
     *
     * @param versionId 表示实例所属meta唯一标识的 {@link String}。
     * @param filter 表示meta实例过滤器的 {@link MetaInstanceFilter}。
     * @param offset 表示查询到的meta版本的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的meta版本的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Instance}{@code >}。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b89ef4")
    RangedResultSet<Instance> list(String versionId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context);

    /**
     * 获取 meta 唯一标识。
     *
     * @return 表示 meta 唯一标识的 {@link String}。
     */
    @Genericable(id = "fcf1745068eb47559af543a037b89ef3")
    String getMetaId(String id);
}
