/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.dto.FitableInfoDto;
import com.huawei.fit.jober.aipp.service.GenericableManageService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GenericableManageServiceImpl
 *
 * @author 孙怡菲
 * @since 2024-04-24
 */
@Component
public class GenericableManageServiceImpl implements GenericableManageService {
    private static final Logger log = Logger.get(GenericableManageServiceImpl.class);

    private final BrokerClient client;

    public GenericableManageServiceImpl(BrokerClient client) {
        this.client = client;
    }

    @Override
    public List<FitableInfoDto> getFitablesByGenerableId(String genericableId, int pageNum, int pageSize) {
        if (StringUtils.equals(genericableId, "68dc66a6185cf64c801e55c97fc500e4")) {
            FitableInfoDto memoryAfterResume = FitableInfoDto.builder()
                .name("获取简历之后的历史记录")
                .fitableId("MemoryAfterResume")
                .hash("9afdb86b-6fd1-d448-8e33-717587a5fd36")
                .build();

            FitableInfoDto memoryAfterTzPromptWordSplice = FitableInfoDto.builder()
                .name("天舟AI历史记录管理工具")
                .fitableId("com.huawei.fit.jober.aipp.fitable.MemoryAfterTzPromptWordSplice")
                .hash("99ef2ebb-3161-4279-b7f2-dcb030fafbeb")
                .build();
            return Arrays.asList(memoryAfterResume, memoryAfterTzPromptWordSplice);
        }
        if (StringUtils.equals(genericableId, "d01041a73e00ac46bedde08d02c6818e")) {
            return Collections.singletonList(FitableInfoDto.builder()
                .name("获取面试问题记录")
                .fitableId("GetQAFromLog")
                .hash("595928ca-c47d-824f-9672-1d5381dfa2ac")
                .build());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> executeInspirationFitable(String fitableId, String appId, String appType,
        OperationContext operationContext) {
        final String genericableId = "d01041a73e00ac46bedde08d02c6818e";
        List<Map<String, Object>> res;
        try {
            res = this.client.getRouter(genericableId)
                .route(new FitableIdFilter(fitableId))
                .invoke(new HashMap<>(), appId, appType, operationContext);
        } catch (FitException e) {
            log.error("Error occurred when running inspiration fitable, error: {}", e.getMessage());
            throw new AippException(AippErrCode.EXECUTE_INSPIRATION_FITABLE_FAILED);
        }
        return res;
    }
}
