/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;
import com.huawei.fit.jober.aipp.domain.AppBuilderConfigProperty;
import com.huawei.fit.jober.aipp.domain.AppBuilderFlowGraph;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderConfigRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import com.huawei.fit.jober.aipp.repository.AppBuilderFormRepository;
import com.huawei.fit.jober.aipp.service.impl.AppBuilderAppServiceImpl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 姚江 yWX1299574
 * @since 2024-04-29
 */
@ExtendWith(MockitoExtension.class)
public class AppBuilderAppServiceImplTest {
    @Mock
    private AippFlowService aippFlowService;
    @Mock
    private AppBuilderAppRepository appRepository;

    @Mock
    private AppBuilderFlowGraphRepository flowGraphRepository;
    @Mock
    private AppBuilderConfigRepository configRepository;
    @Mock
    private AppBuilderFormRepository formRepository;
    @Mock
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    @Mock
    private AppBuilderFormPropertyRepository formPropertyRepository;

    private AppBuilderAppServiceImpl appBuilderAppService;

    private static final LocalDateTime TIME = LocalDateTime.of(2024, 5, 6, 15, 15, 15);

    @BeforeEach
    public void before() {
        AppBuilderAppFactory factory = new AppBuilderAppFactory(flowGraphRepository,
                configRepository,
                formRepository,
                configPropertyRepository,
                formPropertyRepository,
                appRepository);
        appBuilderAppService = new AppBuilderAppServiceImpl(factory, aippFlowService, appRepository);
    }

    private AppBuilderApp mockApp() {
        AppBuilderApp appBuilderApp = new AppBuilderApp(flowGraphRepository,
                configRepository,
                formRepository,
                configPropertyRepository,
                formPropertyRepository);
        appBuilderApp.setType("template");
        appBuilderApp.setName("Unit Test App");
        appBuilderApp.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");
        appBuilderApp.setId("45698235b3d24209aefd59eb7d1c3322");
        appBuilderApp.setAttributes(new HashMap<>());
        appBuilderApp.getAttributes().put("icon", "");
        appBuilderApp.getAttributes().put("app_type", "编程开发");
        appBuilderApp.getAttributes().put("greeting", "1");
        appBuilderApp.getAttributes().put("description", "1");
        appBuilderApp.setState("inactive");
        appBuilderApp.setCreateBy("yaojiang wx1299574");
        appBuilderApp.setUpdateBy("yaojiang wx1299574");
        appBuilderApp.setUpdateAt(TIME);
        appBuilderApp.setCreateAt(TIME);
        appBuilderApp.setVersion("1.0.0");
        appBuilderApp.setConfig(this.mockConfig());
        appBuilderApp.getConfig().setApp(appBuilderApp);
        appBuilderApp.setConfigId(appBuilderApp.getConfig().getId());
        appBuilderApp.setFlowGraph(this.mockGraph());
        appBuilderApp.setFlowGraphId(appBuilderApp.getFlowGraph().getId());
        return appBuilderApp;
    }

    private AppBuilderConfig mockConfig() {
        AppBuilderConfig config = new AppBuilderConfig(this.formRepository,
                this.formPropertyRepository,
                this.configPropertyRepository,
                this.appRepository);

        config.setAppId("45698235b3d24209aefd59eb7d1c3322");
        config.setId("24581235b3d24209aefd59eb7d1c3322");

        config.setUpdateAt(TIME);
        config.setCreateAt(TIME);
        config.setCreateBy("yaojiang wx1299574");
        config.setUpdateBy("yaojiang wx1299574");
        config.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");

        config.setForm(mockForm());
        config.setFormId(config.getForm().getId());
        config.setConfigProperties(mockConfigProperties());
        for (int i = 0; i< 8; i++) {
            AppBuilderConfigProperty configProperty = config.getConfigProperties().get(i);
            configProperty.setConfig(config);
            configProperty.setFormProperty(config.getForm().getFormProperties().get(i));
        }
        return config;
    }

    private AppBuilderForm mockForm() {
        AppBuilderForm form = new AppBuilderForm(this.formPropertyRepository);
        form.setId("cc65e235b3d24209aefd59eb7d1a5499");
        form.setName("表单");
        form.setTenantId("727d7157b3d24209aefd59eb7d1c49ff");
        form.setUpdateAt(TIME);
        form.setCreateAt(TIME);
        form.setCreateBy("yaojiang wx1299574");
        form.setUpdateBy("yaojiang wx1299574");
        form.setType("component");
        form.setAppearance("[{\"key\": \"ke1\", \"name\": \"name1\", \"type\": \"TEXT\"}]");
        form.setFormProperties(mockFormProperties());
        form.getFormProperties().forEach(fp -> fp.setForm(form));
        return form;
    }

    private List<AppBuilderFormProperty> mockFormProperties() {
        Object[] values = new Object[] {
                0.3, "你是一个前端专家，擅长用react实现需求。\\n请输出代码。", new ArrayList<String>(),
                new ArrayList<String>(),
                JSON.parseArray("[{\"name\":\"财经黑话\",\"description\":\"null\",\"id\":94}]"),
                JSONObject.parseObject("{\"type\":\"Customizing\",\"fitableId\":\"MemoryAfterResume\"}"),
                this.generateInspirations(), "Qwen1.5-32B-Chat"
        };
        String[] names = new String[] {
                "temperature", "systemPrompt", "tool", "workflows", "knowledge", "memory", "inspiration", "model"
        };
        String[] dataTypes = new String[] {
                "Number", "String", "List<String>", "List<String>", "List", "object", "object", "String"
        };
        List<AppBuilderFormProperty> formProperties = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            AppBuilderFormProperty formProperty = new AppBuilderFormProperty();
            formProperty.setFormId("cc65e235b3d24209aefd59eb7d1a5499");
            formProperty.setName(names[i]);
            formProperty.setId(i + "c65e235b3d24209aefd59eb7d1a549" + i);
            formProperty.setDataType(dataTypes[i]);
            formProperty.setDefaultValue(values[i]);
            formProperty.setFormRepository(this.formRepository);
            formProperties.add(formProperty);
        }
        return formProperties;
    }

    private List<AppBuilderConfigProperty> mockConfigProperties() {
        String[] nodeIds = new String[] {"4agtId", "4agtId", "4agtId", "4agtId", "sciinj", null, null, "4agtId"};

        List<AppBuilderConfigProperty> configProperties = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            AppBuilderConfigProperty configProperty = new AppBuilderConfigProperty(this.configRepository,
                    this.formRepository,
                    this.formPropertyRepository);
            configProperty.setConfigId(i + "275e235b3d24209aefd59eb8541a549" + i);
            configProperty.setFormPropertyId(i + "c65e235b3d24209aefd59eb7d1a549" + i);
            configProperty.setNodeId(nodeIds[i]);
            configProperty.setConfigId("24581235b3d24209aefd59eb7d1c3322");
            configProperties.add(configProperty);
        }
        return configProperties;
    }

    private AppBuilderFlowGraph mockGraph() {
        String appearance =
                "{\"id\": \"69e9dec999384b1791e24a3032010e77\", \"type\": \"jadeFlowGraph\", \"pages\": [{\"x\": 0, "
                        + "\"y\": 0, \"id\": \"elsa-page:t1qrig\", \"bold\": false, \"mode\": \"configuration\", "
                        + "\"text\": \"newFlowPage\", \"type\": \"jadeFlowPage\", \"dirty\": false, \"index\": 0, "
                        + "\"width\": 2000, \"hAlign\": \"left\", \"height\": 1600, \"isPage\": true, \"italic\": "
                        + "false, \"shapes\": [{\"x\": 194, \"y\": 134, \"id\": \"a6lgso\", \"pad\": 6, \"bold\": "
                        + "false, \"name\": \"被监听者\", \"text\": \"\", \"type\": \"jadeInputNode\", \"dirty\": true, "
                        + "\"index\": 100, \"jober\": {\"name\": \"\", \"type\": \"general_jober\", \"fitables\": [],"
                        + " \"converter\": {\"type\": \"mapping_converter\", \"entity\": []}}, \"width\": 500, "
                        + "\"height\": 180, \"italic\": false, \"shadow\": \"0 2px 4px 0 rgba(0,0,0,.1)\", "
                        + "\"hideText\": true, \"backColor\": \"white\", \"container\": \"elsa-page:t1qrig\", "
                        + "\"dashWidth\": 0, \"namespace\": \"flowable\", \"autoHeight\": true, \"rotateAble\": "
                        + "false, \"borderColor\": \"rgba(28,31,35,.08)\", \"borderWidth\": 1, \"focusShadow\": \"0 0"
                        + " 1px rgba(0,0,0,.3),0 4px 14px rgba(0,0,0,.1)\", \"runningTask\": 0, \"triggerMode\": "
                        + "\"auto\", \"warningTask\": 0, \"completedTask\": 0, \"componentName\": "
                        + "\"jadeInputComponent\", \"focusBackColor\": \"white\", \"enableAnimation\": true, "
                        + "\"focusBorderColor\": \"#4d53e8\", \"focusBorderWidth\": 2, \"mouseInBorderColor\": "
                        + "\"#4d53e8\"}, {\"x\": 120, \"y\": 80, \"id\": \"8ka4p9\", \"pad\": 6, \"bold\": false, "
                        + "\"name\": \"大模型\", \"text\": \"\", \"type\": \"llmNode\", \"dirty\": true, \"index\": 101,"
                        + " \"jober\": {\"name\": \"\", \"type\": \"general_jober\", \"fitables\": [], \"converter\":"
                        + " {\"type\": \"mapping_converter\", \"entity\": [{\"id\": "
                        + "\"c4aa80b5-70ef-4fb8-91f6-c9a832d5d6ec\", \"from\": \"value\", \"name\": \"model\", "
                        + "\"type\": \"String\", \"value\": \"Qwen\"}, {\"id\": "
                        + "\"96282fba-d75e-464c-b021-f390beccc942\", \"from\": \"value\", \"name\": \"temperature\", "
                        + "\"type\": \"Number\", \"value\": \"0.3\"}, {\"id\": "
                        + "\"b6440db6-f8c7-4775-870e-ba32623b8945\", \"from\": \"value\", \"name\": \"prompt\", "
                        + "\"type\": \"Object\", \"value\": [{\"id\": \"70f63499-8a4a-48c4-a5e0-e616d9ac2c08\", "
                        + "\"from\": \"value\", \"name\": \"template\", \"type\": \"String\", \"value\": \"\"}, "
                        + "{\"id\": \"15402f32-0ca9-46db-868c-69be7df334c0\", \"from\": \"value\", \"name\": "
                        + "\"variables\", \"type\": \"Object\", \"value\": [{\"id\": "
                        + "\"17832beb-c237-459e-8432-f0457a858413\", \"from\": \"reference\", \"name\": \"\", "
                        + "\"type\": \"String\", \"value\": \"\", \"referenceId\": \"\", \"referenceKey\": \"\", "
                        + "\"referenceNode\": \"\"}]}]}, {\"id\": \"5ec6d065-c6b9-4685-ad1b-ea5459d03069\", \"from\":"
                        + " \"value\", \"name\": \"tools\", \"type\": \"array\", \"value\": []}, {\"id\": "
                        + "\"bf1721b7-a88a-43e8-9ee1-8e6938c589b6\", \"from\": \"value\", \"name\": \"workflows\", "
                        + "\"type\": \"array\", \"value\": []}, {\"id\": \"b0215ced-f97f-4f87-883d-a76adb31abd7\", "
                        + "\"from\": \"value\", \"name\": \"output\", \"type\": \"Object\", \"value\": [{\"id\": "
                        + "\"24e24707-19ff-424c-a007-6c2d4a5abf05\", \"from\": \"value\", \"name\": \"llmOutput\", "
                        + "\"type\": \"string\", \"value\": \"\"}]}]}}, \"width\": 500, \"height\": 836, \"italic\": "
                        + "false, \"shadow\": \"0 2px 4px 0 rgba(0,0,0,.1)\", \"hideText\": true, \"backColor\": "
                        + "\"white\", \"container\": \"elsa-page:t1qrig\", \"dashWidth\": 0, \"namespace\": "
                        + "\"flowable\", \"autoHeight\": true, \"rotateAble\": false, \"borderColor\": \"rgba(28,31,"
                        + "35,.08)\", \"borderWidth\": 1, \"focusShadow\": \"0 0 1px rgba(0,0,0,.3),0 4px 14px rgba"
                        + "(0,0,0,.1)\", \"runningTask\": 0, \"triggerMode\": \"auto\", \"warningTask\": 0, "
                        + "\"completedTask\": 0, \"componentName\": \"llmComponent\", \"focusBackColor\": \"white\", "
                        + "\"enableAnimation\": true, \"focusBorderColor\": \"#4d53e8\", \"focusBorderWidth\": 2, "
                        + "\"mouseInBorderColor\": \"#4d53e8\"}], \"vAlign\": \"top\", \"itemPad\": [0, 0, 0, 0], "
                        + "\"division\": -1, \"dockMode\": \"none\", \"fontFace\": \"arial\", \"fontSize\": 18, "
                        + "\"hideText\": true, \"moveable\": true, \"shapesAs\": {}, \"backColor\": \"#f2f3f5\", "
                        + "\"container\": \"elsa-page:t1qrig\", \"dockAlign\": \"top\", \"fontColor\": \"#ECD0A7\", "
                        + "\"fontStyle\": \"normal\", \"itemSpace\": 5, \"namespace\": \"jadeFlow\", \"fontWeight\": "
                        + "\"bold\", \"itemScroll\": {\"x\": 0, \"y\": 0}, \"borderColor\": \"white\", "
                        + "\"focusBackColor\": \"#f2f3f5\"}], \"title\": \"jadeFlow\", \"source\": \"elsa\", "
                        + "\"tenant\": \"default\", \"setting\": {\"pad\": 10, \"tag\": {}, \"code\": \"\", "
                        + "\"pDock\": \"none\", \"hAlign\": \"center\", \"margin\": 25, \"shadow\": \"\", \"shared\":"
                        + " false, \"vAlign\": \"top\", \"itemPad\": [5, 5, 5, 5], \"visible\": true, \"autoText\": "
                        + "false, \"dockMode\": \"none\", \"dragable\": true, \"editable\": true, \"fontFace\": "
                        + "\"arial\", \"fontSize\": 12, \"infoType\": {\"name\": \"none\", \"next\": "
                        + "\"INFORMATION\"}, \"moveable\": true, \"priority\": 0, \"allowLink\": true, \"autoWidth\":"
                        + " false, \"backAlpha\": 0.15, \"backColor\": \"whitesmoke\", \"dashWidth\": 0, "
                        + "\"deletable\": true, \"fontColor\": \"steelblue\", \"fontStyle\": \"normal\", "
                        + "\"headColor\": \"steelblue\", \"lineWidth\": 2, \"underline\": false, \"autoHeight\": "
                        + "false, \"emphasized\": false, \"fontWeight\": \"lighter\", \"itemScroll\": {\"x\": 0, "
                        + "\"y\": 0}, \"lineHeight\": 1.5, \"resizeable\": true, \"rotateAble\": true, "
                        + "\"scrollLock\": {\"x\": false, \"y\": false}, \"selectable\": true, \"shadowData\": \"2px "
                        + "2px 4px\", \"borderColor\": \"steelblue\", \"borderWidth\": 1, \"bulletSpeed\": 1, "
                        + "\"focusMargin\": 0, \"focusShadow\": \"\", \"globalAlpha\": 1, \"outstanding\": false, "
                        + "\"bulletedList\": false, \"cornerRadius\": 4, \"enableSocial\": true, \"mouseInColor\": "
                        + "\"orange\", \"numberedList\": false, \"rotateDegree\": 0, \"captionhAlign\": \"center\", "
                        + "\"strikethrough\": false, \"focusBackColor\": \"whitesmoke\", \"focusFontColor\": "
                        + "\"darkorange\", \"progressStatus\": {\"name\": \"NONE\", \"next\": \"UNKNOWN\", \"color\":"
                        + " \"gray\"}, \"showedProgress\": false, \"captionfontFace\": \"arial black\", "
                        + "\"captionfontSize\": 14, \"enableAnimation\": false, \"progressPercent\": 0.65, "
                        + "\"captionfontColor\": \"whitesmoke\", \"captionfontStyle\": \"normal\", "
                        + "\"focusBorderColor\": \"darkorange\", \"focusBorderWidth\": 1, \"mouseInBackColor\": "
                        + "\"whitesmoke\", \"mouseInFontColor\": \"orange\", \"captionfontWeight\": \"lighter\", "
                        + "\"captionlineHeight\": 1, \"mouseInBorderColor\": \"orange\"}}";
        AppBuilderFlowGraph graph = new AppBuilderFlowGraph("69e9dec999384b1791e24a3032010e77", "graph", appearance);
        graph.setUpdateAt(TIME);
        graph.setCreateAt(TIME);
        graph.setCreateBy("yaojiang wx1299574");
        graph.setUpdateBy("yaojiang wx1299574");
        return graph;
    }

    /**
     * 为 {@link AppBuilderAppServiceImpl#create(String, AppBuilderAppCreateDto, OperationContext)} 提供测试
     */
    @Nested
    @DisplayName("创建测试")
    class TestCreate {
        @Test
        @DisplayName("测试创建成功")
        public void test01() {


        }
    }

    private Object generateInspirations() {
        return JSONObject.parseObject("{\"category\":[{\"title\":\"root\",\"id\":\"root\",\"disabled\":true,"
                + "\"children\":[{\"title\":\"root\",\"id\":\"root\",\"disabled\":true,"
                + "\"children\":[{\"title\":\"产品线\",\"id\":\"11\",\"parent\":\"root:11\",\"disabled\":true,"
                + "\"children\":[{\"title\":\"数存\",\"id\":\"111\",\"parent\":\"11:111\",\"disabled\":true,"
                + "\"children\":[{\"title\":\"数字化工具\",\"id\":\"1111\",\"parent\":\"111:1111\","
                + "\"children\":[]}]}]},{\"title\":\"BG\",\"id\":\"12\",\"parent\":\"root:12\","
                + "\"disabled\":true,\"children\":[{\"title\":\"运营商\",\"id\":\"121\",\"parent\":\"12:121\","
                + "\"disabled\":true,\"children\":[{\"title\":\"数字化工具\",\"id\":\"1211\","
                + "\"parent\":\"121:1211\",\"children\":[]}]}]},{\"title\":\"区域\",\"id\":\"13\","
                + "\"parent\":\"root:13\",\"disabled\":true,\"children\":[{\"title\":\"中国区\",\"id\":\"131\","
                + "\"parent\":\"13:131\",\"disabled\":true,\"children\":[{\"title\":\"数字化工具\","
                + "\"id\":\"1311\",\"parent\":\"131:1311\",\"children\":[]}]}]}]}]}],\"inspirations\":[]}");
    }
}
