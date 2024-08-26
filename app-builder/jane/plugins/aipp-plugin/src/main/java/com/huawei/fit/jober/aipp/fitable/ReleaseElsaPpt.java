/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.elsa.enums.DocumentType;
import com.huawei.fit.elsa.generable.GraphExposeService;
import com.huawei.fit.elsa.generable.entity.GraphParam;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.init.ElsaPptInitiator;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.util.UUIDUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用elsa，根据businessData中的数据，生成ppt
 *
 * @author 孙怡菲
 * @since 2024/05/10
 */
@Component
@Slf4j
public class ReleaseElsaPpt implements FlowableService {
    private static final String COVER_PAGE_TITLE_ID = "proo1j";

    private static final String COVER_PAGE_AUTHOR_ID = "ms3jyp";

    private static final String COVER_PAGE_DATE_ID = "4x8zg6";

    private static final String CONTENT_PAGE_TITLE_ID = "9gtktv";

    private static final String CONTENT_PAGE_CONTENT_ID = "gzglqq";

    private static final int CONTENT_PAGE_BEGIN_INDEX = 3;

    private final MetaInstanceService metaInstanceService;

    private final GraphExposeService elsaClient;

    private final String elsaKey;

    private final String endpoint;

    public ReleaseElsaPpt(MetaInstanceService metaInstanceService, GraphExposeService elsaClient,
            @Value("${elsa.elsaKey}") String elsaKey, @Value("${elsa.endpoint}") String endpoint) {
        this.metaInstanceService = metaInstanceService;
        this.elsaClient = elsaClient;
        this.elsaKey = elsaKey;
        this.endpoint = endpoint;
    }

    private static String updateShapeTextData(JSONObject shape, String newText) {
        JSONObject text = ObjectUtils.cast(shape.getJSONArray("text").get(0));
        text.getJSONObject("attributes").put("id", UUIDUtil.uuid());
        (ObjectUtils.<JSONObject>cast(text.getJSONArray("children").get(0))).put("data", newText);
        return text.toString();
    }

    private static void updateIds(Map<String, String> idMap, JSONObject page, String newGraphId) {
        String pageNewId = UUIDUtil.uuid();
        idMap.put(page.getString("id"), pageNewId);
        page.put("id", pageNewId);
        page.put("graphId", newGraphId);
        page.put("container", pageNewId);
        JSONArray shapes = page.getJSONArray("shapes");
        shapes.stream().map(ObjectUtils::<JSONObject>cast).forEach(shape -> {
            String id = shape.getString("id");
            String newId = UUIDUtil.uuid();
            idMap.put(id, newId);
            shape.put("id", newId);
        });
        shapes.stream().map(ObjectUtils::<JSONObject>cast).forEach(shape -> {
            String container = shape.getString("container");
            shape.put("container", idMap.get(container));
        });
    }

    @Fitable("com.huawei.fit.jober.aipp.fitable.ReleaseElsaPpt")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("ReleaseElsaPpt businessData {}", businessData);
        OperationContext context = DataUtils.getOpContext(businessData);
        JSONObject graph = buildElsaPptGraph(businessData);
        saveElsaPpt(context, graph);
        String result = buildResult(context, graph);
        businessData.put(AippConst.INST_ELSA_PPT_RESULT_KEY, result);
        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_ELSA_PPT_RESULT_KEY, result).build();
        MetaInstanceUtils.persistInstance(
                metaInstanceService, info, businessData, DataUtils.getOpContext(businessData));
        return flowData;
    }


    private JSONObject buildElsaPptGraph(Map<String, Object> businessData) {
        String data = ObjectUtils.cast(businessData.get(AippConst.BS_PPT_JSON_RESULT));
        JSONObject pptJson = JSONObject.parseObject(data);
        return build(pptJson);
    }


    private String buildResult(OperationContext context, JSONObject graph) {
        Map<String, String> result = new HashMap<>();
        result.put("title", graph.getString("title") + ".elsa");
        result.put("url", buildUrl(endpoint, this.elsaKey, graph.getString("id")));
        return JSONObject.toJSONString(result);
    }

    private void saveElsaPpt(OperationContext context, JSONObject graph) {
        GraphParam graphParam = new GraphParam();
        graphParam.setGraphId(graph.getString("id"));
        graphParam.setJson(graph.toJSONString());
        graphParam.setVersion("1.0.0");
        graphParam.setAccessKey(elsaKey);
        graphParam.setType(DocumentType.PRESENTATION.name());

        log.info("saveElsaPpt graphParam. graphId:{}, tenant:{}, operator:{}",
                graphParam.getGraphId(),
                context.getTenantId(),
                context.getOperator());

        int saveResult = elsaClient.save(graphParam, context);
        log.info("saveElsaPpt result:{}", saveResult);
    }


    private String buildUrl(String endpoint, String tenantId, String graphId) {
        return endpoint + "/elsa/" + tenantId + "/presentation/detail/" + graphId + "?templateId=blank";
    }

    private JSONObject build(JSONObject pptData) {
        String title = pptData.getString("title");

        JSONObject graph = JSONObject.parseObject(getGraphData());
        JSONObject baseCoverPage = JSONObject.parseObject(getBaseCoverPageData());
        JSONObject baseContentPage = JSONObject.parseObject(getBaseContentPageData());
        JSONObject coverPage = JSONObject.parseObject(getCoverPageData());
        String graphId = UUIDUtil.uuid();
        graph.put("title", title);
        graph.put("id", graphId);
        JSONArray allPages = graph.getJSONArray("pages");
        allPages.add(baseCoverPage);
        allPages.add(baseContentPage);
        allPages.add(coverPage);

        Map<String, String> idMap = new HashMap<>();

        updateIds(idMap, baseCoverPage, graphId);
        updateIds(idMap, baseContentPage, graphId);

        updateCoverData(coverPage, pptData);
        updatePage(idMap, coverPage, graphId);

        JSONArray pages = pptData.getJSONArray("pages");
        for (int i = 0; i < pages.size(); i++) {
            Object page = pages.get(i);
            JSONObject jsonObject = ObjectUtils.cast(page);
            JSONObject contentPage = JSONObject.parseObject(getContentPageData());
            allPages.add(contentPage);
            updateContentData(idMap, contentPage, jsonObject, i + CONTENT_PAGE_BEGIN_INDEX);
            updatePage(idMap, contentPage, graphId);
        }
        return graph;
    }

    private void updateContentData(Map<String, String> idMap, JSONObject contentPage, JSONObject page, int index) {
        contentPage.put("index", index);
        JSONArray shapes = contentPage.getJSONArray("shapes");
        shapes.stream().map(ObjectUtils::<JSONObject>cast).forEach(shape -> {
            switch (shape.getString("id")) {
                case CONTENT_PAGE_TITLE_ID:
                    updateShapePlacedPropertiesData(
                            idMap, shape, updateShapeTextData(shape, page.getString("title")));
                    break;
                case CONTENT_PAGE_CONTENT_ID:
                    updateShapePlacedPropertiesData(idMap,
                            shape,
                            updateShapeTextData(shape, page.getString("content")));
                    break;
                default:
                    break;
            }
        });
    }

    private void updateShapePlacedPropertiesData(Map<String, String> idMap, JSONObject shape, String textData) {
        JSONObject placedProperties = shape.getJSONObject("placedProperties");
        placedProperties.put("container", idMap.get(placedProperties.getString("container")));
        placedProperties.put("text", JSONObject.parseObject(textData));
    }

    private void updateCoverData(JSONObject coverPage, JSONObject pptData) {
        JSONArray shapes = coverPage.getJSONArray("shapes");
        shapes.stream().map(ObjectUtils::<JSONObject>cast).forEach(shape -> {
            switch (shape.getString("id")) {
                case COVER_PAGE_TITLE_ID:
                    updateShapeTextData(shape, pptData.getString("title"));
                    break;
                case COVER_PAGE_AUTHOR_ID:
                    updateShapeTextData(shape, pptData.getString("author"));
                    break;
                case COVER_PAGE_DATE_ID:
                    updateShapeTextData(shape, pptData.getString("date"));
                    break;
                default:
                    break;
            }
        });
    }

    private void updatePage(Map<String, String> idMap, JSONObject page, String graphId) {
        updateIds(idMap, page, graphId);
        updateReferenceIds(idMap, page);
    }

    private void updateReferenceIds(Map<String, String> idMap, JSONObject page) {
        JSONArray shapes = page.getJSONArray("shapes");
        shapes.stream().map(ObjectUtils::<JSONObject>cast).forEach(shape -> {
            String referencePage = shape.getString("referencePage");
            if (referencePage != null) {
                shape.put("referencePage", idMap.get(referencePage));
            }
            JSONObject referenceData = shape.getJSONObject("referenceData");
            if (referenceData == null) {
                return;
            }
            JSONObject newReferenceData = new JSONObject();
            referenceData.forEach((k, v) -> {
                if ("placed".equals(k)) {
                    JSONArray placed = ObjectUtils.cast(v);
                    JSONArray newPlaced = new JSONArray();
                    placed.stream()
                            .map(ObjectUtils::<String>cast)
                            .forEach(shapeId -> newPlaced.add(idMap.get(shapeId)));
                    newReferenceData.put("placed", newPlaced);
                } else {
                    newReferenceData.put(idMap.get(k), v);
                }
            });
            shape.put("referenceData", newReferenceData);
        });
    }

    private String getCoverPageData() {
        return ElsaPptInitiator.ELSA_PPT_DATA.get(ElsaPptInitiator.COVER_PAGE_KEY);
    }

    private String getContentPageData() {
        return ElsaPptInitiator.ELSA_PPT_DATA.get(ElsaPptInitiator.CONTENT_PAGE_KEY);
    }

    private String getBaseContentPageData() {
        return ElsaPptInitiator.ELSA_PPT_DATA.get(ElsaPptInitiator.BASE_CONTENT_PAGE_KEY);
    }

    private String getBaseCoverPageData() {
        return ElsaPptInitiator.ELSA_PPT_DATA.get(ElsaPptInitiator.BASE_COVER_PAGE_KEY);
    }

    private String getGraphData() {
        return ElsaPptInitiator.ELSA_PPT_DATA.get(ElsaPptInitiator.GRAPH_KEY);
    }
}
