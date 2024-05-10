/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.gateway;

import com.huawei.fit.jober.bff.client.elsa.reponse.GetPageResponse;
import com.huawei.fit.jober.bff.client.elsa.reponse.SaveFlowsResponse;

import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;

/**
 * 调用神笔马良接口代理类
 *
 * @author y00679285
 * @since 2023/10/10
 */
public interface ElsaClient {
    @Post(value = "{endpoint}/elsa-backend/documentation/paging")
    GetPageResponse getPages(@Var("endpoint") String endpoint, @Header("user") String user,
            @Header("cookie") String cookie, @JSONBody String data);

    @Post(value = "{endpoint}/elsa-backend/graph/save")
    SaveFlowsResponse saveFlows(@Var("endpoint") String endpoint, @Header("user") String user,
            @Header("cookie") String cookie, @JSONBody String data);
}
