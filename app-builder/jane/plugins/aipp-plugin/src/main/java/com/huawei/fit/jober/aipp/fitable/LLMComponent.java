/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jober.FlowCallbackService;
import com.huawei.fit.jober.FlowInstanceService;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.fel.AippLlmMeta;
import com.huawei.fit.jober.aipp.fel.AippMemory;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippLogStreamService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.vo.AippLogVO;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.parameterization.StringFormatException;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.serialization.SerializationException;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.UuidUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.ToolMessage;
import com.huawei.jade.fel.core.template.StringTemplate;
import com.huawei.jade.fel.core.template.support.DefaultStringTemplate;
import com.huawei.jade.fel.core.util.Tip;
import com.huawei.jade.fel.engine.flows.AiFlows;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.patterns.AbstractAgent;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.tool.ToolProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * LLM 组件实现
 *
 * @author h00804153
 * @since 2024/4/15
 */
@Component
public class LLMComponent implements FlowableService, FlowCallbackService {
    private static final Logger log = Logger.get(LLMComponent.class);
    private static final String SYSTEM_PROMPT = "# 人设与回复逻辑\n\n{{0}}";
    private static final String PROMPT_TEMPLATE = "{{1}}";
    private static final String CALLBACK_ID = "com.huawei.fit.jober.aipp.fitable.LLMComponentCallback";
    private static final String AGENT_NODE_ID = "agent";

    // todo: 暂时使用ConcurrentHashMap存储父节点的元数据
    private final ConcurrentHashMap<String, AippLlmMeta> llmCache = new ConcurrentHashMap<>();

    private final FlowInstanceService flowInstanceService;
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;
    private final ToolProvider toolProvider;
    private final AiProcessFlow<Tip, Prompt> agentFlow;
    private final AippLogService aippLogService;
    private final AippLogStreamService aippLogStreamService;
    private final BrokerClient client;
    private final ObjectSerializer serializer;

    /**
     * 大模型节点构造器，内部通过提供的agent和tool构建智能体工作流。
     *
     * @param flowInstanceService 表示流程实例服务的 {@link FlowInstanceService}。
     * @param metaInstanceService 表示元数据实例服务的 {@link MetaInstanceService}。
     * @param metaService 表示提供给AIPP元数据服务的 {@link MetaService}。
     * @param toolProvider 表示具提供者功能的 {@link ToolProvider}。
     * @param agent 表示提供智能体功能的 {@link AbstractAgent}{@code <}{@link ChatMessages}{@code ,
     * }{@link ChatMessages}{@code >}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public LLMComponent(FlowInstanceService flowInstanceService,
            MetaInstanceService metaInstanceService,
            MetaService metaService,
            ToolProvider toolProvider,
            @Fit(alias = AippConst.WATER_FLOW_AGENT_BEAN) AbstractAgent<Prompt, Prompt> agent,
            AippLogService aippLogService, AippLogStreamService aippLogStreamService, BrokerClient client,
            ObjectSerializer serializer) {
        this.flowInstanceService = flowInstanceService;
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.toolProvider = toolProvider;
        this.aippLogService = aippLogService;
        this.aippLogStreamService = aippLogStreamService;
        this.client = client;
        this.serializer = Validation.notNull(serializer, "The serializer cannot be nul.");

        // handleTask从入口开始处理，callback从agent node开始处理
        this.agentFlow = AiFlows.<Tip>create()
                .prompt(Prompts.sys(SYSTEM_PROMPT), Prompts.history(), Prompts.human(PROMPT_TEMPLATE))
                .id(AGENT_NODE_ID)
                .delegate(agent)
                .close();
    }

    /**
     * 工作流回调大模型节点的接口实现。
     *
     * @param childFlowData 工作流上下文信息，需要包含子流程的输出结果和主流程的instId。
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMComponentCallback")
    @Override
    public void callback(List<Map<String, Object>> childFlowData) {
        Map<String, Object> childBusinessData = DataUtils.getBusiness(childFlowData);
        log.debug("LLMComponentCallback business data {}", childBusinessData);
        String toolOutput = ObjectUtils.cast(childBusinessData.get(AippConst.BS_AIPP_FINAL_OUTPUT));
        String parentInstanceId = ObjectUtils.cast(childBusinessData.get(AippConst.PARENT_INSTANCE_ID));
        AippLlmMeta llmMeta = llmCache.get(parentInstanceId);
        if (!ObjectUtils.<Boolean>cast(childBusinessData.get(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM))) {
            Map<String, Object> businessData = llmMeta.getBusinessData();
            Map<String, Object> output = new HashMap<>();
            // todo: 当前如果子流程不需要模型加工，子流程和主流程会重复打印 toolOutput。
            //  为了避免这种情况，临时设置一个 key 来表明结果是否来自子流程。
            //  如果结果来自子流程，主流程的结束节点不打印；否则主流程的结束节点打印。
            output.put("llmOutput", toolOutput);
            businessData.put("output", output);
            businessData.put(AippConst.OUTPUT_IS_FROM_CHILD, true);
            doOnAgentComplete(llmMeta);
            return;
        }
        // todo: 暂时原地修改，之后再看是否需要创建新的
        ChatMessages chatMessages = ChatMessages.from(llmMeta.getTrace().messages());
        ChatMessage lastMessage = chatMessages.messages().remove(chatMessages.messages().size() - 1);
        chatMessages.add(new ToolMessage(lastMessage.id().orElse(null), toolOutput));
        llmMeta.setTrace(chatMessages);
        String msgId = UuidUtils.randomUuidString();
        String path = this.aippLogService.getParentPath(parentInstanceId);
        agentFlow.converse()
                .bind((acc, chunk) -> this.sendLog(chunk, path, msgId, parentInstanceId))
                .doOnSuccess(msg -> llmOutputConsumer(llmMeta, ObjectUtils.cast(msg)))
                .doOnError(throwable -> doOnAgentError(llmMeta, throwable.getMessage()))
                .offer(AGENT_NODE_ID, Collections.singletonList(chatMessages));
    }

    /**
     * 调用模型接口的实现
     *
     * @param flowData 流程执行上下文数据，包含模型参数、用户问题及技能列表
     * @return 流程执行上下文数据，包含模型执行结果
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.LLMComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        String instId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentInstId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        log.debug("LLMComponent business data {}", businessData);

        AippLlmMeta llmMeta = AippLlmMeta.parse(flowData, metaService);
        llmCache.put(llmMeta.getInstId(), llmMeta);

        String systemPrompt = ObjectUtils.cast(businessData.get("systemPrompt"));
        String msgId = UuidUtils.randomUuidString();

        if (businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            this.processFile(llmMeta, businessData);
            return flowData;
        }

        String path = this.aippLogService.buildPath(instId, parentInstId);

        // todo: 待add多模态，期望使用image的url，当前传入的历史记录里面没有image
        Map<String, Object> toolContext = MapBuilder.<String, Object>get()
                .put(AippConst.TRACE_ID, llmMeta.getInstId())
                .put(AippConst.CALLBACK_ID, CALLBACK_ID)
                .put(AippConst.CONTEXT_USER_ID, ObjectUtils.cast(businessData.get(AippConst.CONTEXT_USER_ID)))
                .build();
        agentFlow.converse()
                .bind((acc, chunk) -> this.sendLog(chunk, path, msgId, instId))
                .bind(new AippMemory(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_MEMORIES_KEY))))
                .bind(AippConst.TOOL_CONTEXT_KEY, toolContext)
                .doOnSuccess(msg -> llmOutputConsumer(llmMeta, msg))
                .doOnError(throwable -> doOnAgentError(llmMeta, throwable.getMessage()))
                .bind(buildChatOptions(businessData))
                .offer(Tip.fromArray(systemPrompt, buildInputText(businessData)));
        return flowData;
    }

    private void processFile(AippLlmMeta llmMeta, Map<String, Object> businessData) {
        try {
            Map<String, String> fileDescription = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_FILE_DESC_KEY));
            String filePath = fileDescription.get("file_path");
            String fileContent = this.client.getRouter("com.huawei.fit.jober.aipp.tool.extract.file")
                    .route(new FitableIdFilter("extract.multi.type"))
                    .invoke(filePath);
            this.addAnswer(llmMeta, fileContent);
        } catch (FitException e) {
            // 打印错误日志
            String errorMsg = "无法解析文件";
            this.doOnAgentError(llmMeta, errorMsg);
        }
    }

    private void sendLog(ChatMessage chunk, String path, String msgId, String instId) {
        String msg = chunk.text();
        if (StringUtils.isBlank(msg)) {
            return;
        }
        AippLogData logData = AippLogData.builder().msg(chunk.text()).build();
        AippLogVO logVO = AippLogVO.builder()
                .logData(JsonUtils.toJsonString(logData))
                .logType(AippInstLogType.MSG.name())
                .path(path)
                .msgId(msgId)
                .instanceId(instId)
                .build();
        this.aippLogStreamService.send(logVO);
    }

    /**
     * 处理agent响应的回调函数。
     * <ul>
     * <li>当模型返回最终结果时，保存数据，同时唤醒流程</li>
     * <li>当模型返回工作流实例id时，通知前端处理工作流，并保存大模型处理元数据</li>
     * </ul>
     */
    private void llmOutputConsumer(AippLlmMeta llmMeta, Prompt trace) {
        ChatMessage answer = trace.messages().get(trace.messages().size() - 1);
        if (answer.type() == MessageType.AI) {
            addAnswer(llmMeta, answer.text());
            return;
        }
        // todo: 还没保存trace数据，子流程就跑完了怎么办？（目前走到这里一定有表单阻塞，所以暂时不会有这个问题）
        llmMeta.setTrace(trace);
        try {
            String childInstanceId = JsonUtils.parseObject(answer.text(), String.class);
            this.setChildInstanceId(llmMeta, childInstanceId);
        } catch (AippJsonDecodeException e) {
            this.doOnAgentError(llmMeta, e.getMessage());
        }
    }

    private void addAnswer(AippLlmMeta llmMeta, String answer) {
        Map<String, Object> businessData = llmMeta.getBusinessData();
        Map<String, Object> output = new HashMap<>();
        output.put("llmOutput", answer);
        businessData.put("output", output);
        InstanceDeclarationInfo info = InstanceDeclarationInfo.custom().putInfo("llmOutput", answer).build();
        this.metaInstanceService.patchMetaInstance(llmMeta.getVersionId(),
                llmMeta.getInstId(),
                info,
                llmMeta.getContext());
        doOnAgentComplete(llmMeta);
    }

    private void setChildInstanceId(AippLlmMeta llmMeta, String childInstanceId) {
        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_CHILD_INSTANCE_ID, childInstanceId).build();
        this.metaInstanceService.patchMetaInstance(llmMeta.getVersionId(),
                llmMeta.getInstId(),
                info,
                llmMeta.getContext());
    }

    /**
     * llm节点处理结束回调函数。
     * <ul>
     * <li>当模型返回最终结果时触发</li>
     * <li>当工作流触发回调，但没有返回数据时触发</li>
     * </ul>
     */
    private void doOnAgentComplete(AippLlmMeta llmMeta) {
        // 删除cache
        llmCache.remove(llmMeta.getInstId());
        // resumeFlow
        flowInstanceService.resumeAsyncJob(llmMeta.getFlowDefinitionId(),
                llmMeta.getFlowTraceId(),
                llmMeta.getBusinessData(),
                llmMeta.getContext());
    }

    private void doOnAgentError(AippLlmMeta llmMeta, String errorMessage) {
        // todo: 临时逻辑，如果出错则停止前端轮询并主动终止流程；待流程支持异步调用抛异常后再修改
        log.error("versionId {} errorMessage {}", llmMeta.getVersionId(), errorMessage);
        String msg = "很抱歉，模型节点遇到了问题，请稍后重试。";
        this.aippLogService.insertErrorLog(msg, llmMeta.getFlowData());
        InstanceDeclarationInfo declarationInfo = InstanceDeclarationInfo.custom()
                .putInfo(AippConst.INST_FINISH_TIME_KEY, LocalDateTime.now())
                .putInfo(AippConst.INST_STATUS_KEY, MetaInstStatusEnum.ERROR.name())
                .build();
        metaInstanceService.patchMetaInstance(llmMeta.getVersionId(),
                llmMeta.getInstId(),
                declarationInfo,
                llmMeta.getContext());
        flowInstanceService.terminateFlows(llmMeta.getFlowDefinitionId(),
                llmMeta.getFlowTraceId(),
                Collections.emptyMap(),
                llmMeta.getContext());
    }

    /**
     * 使用{@link StringTemplate}渲染用户模板。
     */
    private String buildInputText(Map<String, Object> businessData) {
        Map<String, Object> input = ObjectUtils.cast(businessData.get("prompt"));
        // todo: 如果有文件，将内容拼到template里；为临时方案，历史记录的多模态会有问题
        StringTemplate template = new DefaultStringTemplate(ObjectUtils.cast(input.get("template"))
                + this.getFilePath(businessData));
        Map<String, Object> variables = ObjectUtils.cast(input.get("variables"));
        Validation.notNull(variables, "The prompt variables cannot be null.");
        try {
            Map<String, String> standardInput = variables.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> this.getStandardValue(entry.getValue())));
            return template.render(standardInput);
        } catch (StringFormatException e) {
            throw new AippException(
                    DataUtils.getOpContext(businessData), AippErrCode.LLM_COMPONENT_TEMPLATE_RENDER_FAILED);
        }
    }

    private String getStandardValue(Object value) {
        Validation.notNull(value, "The value cannot be null.");
        if (value instanceof String) {
            return ObjectUtils.cast(value);
        }
        if (value instanceof List) {
            return ((List<?>) value).stream().map(Object::toString)
                    .collect(Collectors.joining(AippConst.CONTENT_DELIMITER));
        }
        try {
            return this.serializer.serialize(value);
        } catch (SerializationException exception) {
            log.error("Serialize failed, value: {}.", value.toString());
            return StringUtils.EMPTY;
        }
    }

    /**
     * 获取文件路径。
     */
    private String getFilePath(Map<String, Object> businessData) {
        if (!businessData.containsKey(AippConst.BS_AIPP_FILE_DESC_KEY)) {
            return StringUtils.EMPTY;
        }
        Object data = businessData.get(AippConst.BS_AIPP_FILE_DESC_KEY);
        if (!(data instanceof Map)) {
            throw new AippException(AippErrCode.DATA_TYPE_IS_NOT_SUPPORTED, data.getClass().getName());
        }
        Map<String, String> fileDesc = ObjectUtils.cast(data);
        if (MapUtils.isEmpty(fileDesc)) {
            return StringUtils.EMPTY;
        }
        return ("filePath: " + fileDesc.get("file_path") + "\n");
    }

    /**
     * 解析表示自定义参数的{@link ChatOptions}, 当前支持模型、温度、工具。
     */
    private ChatOptions buildChatOptions(Map<String, Object> businessData) {
        List<String> skillNameList = new ArrayList<>(ObjectUtils.cast(businessData.get("tools")));
        skillNameList.addAll(ObjectUtils.cast(businessData.get("workflows")));
        return ChatOptions.builder()
                .model(ObjectUtils.cast(businessData.get("model")))
                .temperature(ObjectUtils.cast(businessData.get("temperature")))
                .tools(this.toolProvider.getTool(skillNameList))
                .build();
    }
}