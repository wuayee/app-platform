/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.FitableInfoDto;

import java.util.List;
import java.util.Map;

/**
 * genericable相关服务
 *
 * @author 孙怡菲 s00664640
 * @since 2024-04-24
 */
public interface GenericableManageService {
    List<FitableInfoDto> getFitablesByGenerableId(String genericableId, int pageNum, int pageSize);

    List<Map<String, Object>> executeInspirationFitable(String fitableId, String appId, String appType, OperationContext operationContext);
}
