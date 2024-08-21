/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import static modelengine.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MetaInstance 操作工具类
 *
 * @author 方誉州
 * @since 2024/6/14
 */
public class MetaInstanceUtils {
    private static final Logger log = Logger.get(MetaInstanceUtils.class);

    /**
     * 更新businessData中meta实例的信息
     *
     * @param service 使用的元数据实例服务{@link MetaInstanceService}
     * @param info Meta实例声明信息{@link InstanceDeclarationInfo}
     * @param businessData business data
     * @param context 操作上下文{@link OperationContext}
     */
    public static void persistInstance(MetaInstanceService service, InstanceDeclarationInfo info,
            Map<String, Object> businessData, OperationContext context) {
        String versionId = notBlank(ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY)),
                "Get blank meta version id");
        String instId = notBlank(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY)),
                "Get blank aipp instance id");
        service.patchMetaInstance(versionId, instId, info, context);
    }

    /**
     * 获取实例详情
     *
     * @param versionId aipp version id，唯一标识
     * @param instanceId 实例Id
     * @param context 操作上下文
     * @param metaInstanceService 使用的元数据实例服务{@link MetaInstanceService}
     * @return instance信息
     */
    public static Instance getInstanceDetail(String versionId, String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        RangedResultSet<Instance> instances = getInstances(versionId, instanceId, context, metaInstanceService);
        if (instances.getRange().getTotal() == 0) {
            log.error("versionId {} inst{} not found.", versionId, instanceId);
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "aipp inst not found. inst " + instanceId);
        }
        return instances.getResults().get(0);
    }

    /**
     * 获取批量实例信息
     *
     * @param versionId aipp version id，唯一标识
     * @param instanceId 实例Id
     * @param context 操作上下文
     * @param metaInstanceService 使用的元数据实例服务{@link MetaInstanceService}
     * @return 批量返回的Instance信息
     */
    public static RangedResultSet<Instance> getInstances(String versionId, String instanceId, OperationContext context,
            MetaInstanceService metaInstanceService) {
        MetaInstanceFilter filter = new MetaInstanceFilter();
        filter.setIds(Collections.singletonList(instanceId));
        return metaInstanceService.list(versionId, filter, 0, 1, context);
    }

    /**
     * 获取MetaList
     *
     * @param versionId 表示实例所属meta唯一标识
     * @param metaInstanceService 处理Meta请求的service
     * @param context 上下文
     * @return 结果
     * @throws AippException 抛出aipp的异常
     */
    public static List<Instance> getInstances(String versionId, MetaInstanceService metaInstanceService,
            OperationContext context) throws AippException {
        final int limitPerQuery = 10;
        MetaInstanceFilter filter = new MetaInstanceFilter();
        return MetaUtils.getAllFromRangedResult(limitPerQuery,
                        (offset) -> metaInstanceService.list(versionId, filter, offset, limitPerQuery, context))
                .collect(Collectors.toList());
    }
}
