/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.flow.graph.repo;

import com.huawei.fit.elsa.generable.GraphExposeService;
import com.huawei.fit.elsa.generable.entity.BatchResult;
import com.huawei.fit.elsa.generable.entity.DocumentVO;
import com.huawei.fit.elsa.generable.entity.PagingParam;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.flow.graph.client.ElsaClient;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphDefinition;
import com.huawei.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import com.huawei.fit.jane.flow.graph.entity.FlowSaveEntity;
import com.huawei.fit.jane.flow.graph.entity.elsa.GraphParam;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import com.huawei.fit.jane.flow.graph.entity.elsa.response.SaveFlowsResponse;

import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.model.RangedResultSet;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 调用elsa接口repo
 *
 * @author yangxiangyu
 * @since 2023/12/13
 */
@Alias("ElsaFlowGraphRepo")
@Component
public class ElsaFlowsGraphRepo implements FlowsGraphRepo {
    private final ElsaClient elsaClient;

    private final GraphExposeService graphExposeService;

    private final String endpoint;

    private final String elsaAccessKey;

    public ElsaFlowsGraphRepo(ElsaClient elsaClient, GraphExposeService graphExposeService,
            @Value("${elsa.endpoint}") String endpoint, @Value("${elsa.accessKey}") String elsaAccessKey) {
        this.elsaClient = elsaClient;
        this.graphExposeService = graphExposeService;
        this.endpoint = endpoint;
        this.elsaAccessKey = elsaAccessKey;
    }

    @Override
    public GetPageResponse getPages(String user, String cookie, String graphData) {
        return elsaClient.getPages(endpoint, user, cookie, graphData);
    }

    @Override
    public SaveFlowsResponse saveFlows(String user, String cookie, String graphData) {
        return elsaClient.saveFlows(endpoint, user, cookie, graphData);
    }

    @Override
    public int saveFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        return graphExposeService.save(this.convert(flowSaveEntity), context);
    }

    @Override
    public int upgradeFlows(GraphParam param) {
        return graphExposeService.upgrade(this.convert(param)).getCode();
    }

    @Override
    public String getFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        return graphExposeService.get(this.convert(flowSaveEntity), context);
    }

    @Override
    public int deleteFlow(FlowSaveEntity flowSaveEntity, OperationContext context) {
        return graphExposeService.delete(this.convert(flowSaveEntity), context);
    }

    @Override
    public RangedResultSet<FlowGraphDefinition> getFlowList(FlowGraphQueryParam queryParam, OperationContext context) {
        BatchResult<DocumentVO> result = graphExposeService.pagingDocumentsByIds(
                this.convert(queryParam));
        return RangedResultSet.create(result.getData().stream().map(this::convert).collect(Collectors.toList()),
                result.getCursor(), result.getSize(), result.getCount().intValue());
    }

    private FlowGraphDefinition convert(DocumentVO documentVO) {
        FlowGraphDefinition flowGraphDefinition = new FlowGraphDefinition();
        flowGraphDefinition.setFlowId(documentVO.getDocumentId());
        flowGraphDefinition.setVersion(documentVO.getVersion());
        // 先设置空值，未发现有消费的地方
        flowGraphDefinition.setTenant(null);
        // 外部status逻辑未消费此处status
        flowGraphDefinition.setStatus(null);
        // 外部会组装这个tags的值，这里不需要填写
        flowGraphDefinition.setTags(null);
        flowGraphDefinition.setName(documentVO.getText());
        // 列表返参应该不需要消费graphData
        flowGraphDefinition.setGraphData(null);
        flowGraphDefinition.setCreatedBy(documentVO.getCreateUser());
        flowGraphDefinition.setCreatedAt(null);
        flowGraphDefinition.setUpdatedBy(documentVO.getUpdateUser());
        flowGraphDefinition.setUpdatedAt(null);
        // 外部未使用previous字段
        flowGraphDefinition.setPrevious(null);
        flowGraphDefinition.setIsDeleted(false);
        return flowGraphDefinition;
    }

    private PagingParam convert(FlowGraphQueryParam queryParam) {
        PagingParam param = new PagingParam();
        param.setDocumentIds(queryParam.getFlowIds());
        param.setCreateUsers(Collections.singletonList(queryParam.getCreatUser()));
        param.setOffset(queryParam.getOffset());
        param.setLimit(queryParam.getLimit());
        return param;
    }

    private com.huawei.fit.elsa.generable.entity.GraphParam convert(FlowSaveEntity flowSaveEntity) {
        com.huawei.fit.elsa.generable.entity.GraphParam graphParam =
                new com.huawei.fit.elsa.generable.entity.GraphParam();
        graphParam.setGraphId(flowSaveEntity.getId());
        graphParam.setAccessKey(this.elsaAccessKey);
        graphParam.setVersion(flowSaveEntity.getVersion());
        graphParam.setJson(flowSaveEntity.getGraphData());
        return graphParam;
    }

    private com.huawei.fit.elsa.generable.entity.GraphParam convert(GraphParam graphParam) {
        com.huawei.fit.elsa.generable.entity.GraphParam result = new com.huawei.fit.elsa.generable.entity.GraphParam();
        result.setGraphId(graphParam.getGraphId());
        result.setAccessKey(this.elsaAccessKey);
        result.setVersion(graphParam.getVersion());
        result.setJson(graphParam.getJson());
        return result;
    }
}
