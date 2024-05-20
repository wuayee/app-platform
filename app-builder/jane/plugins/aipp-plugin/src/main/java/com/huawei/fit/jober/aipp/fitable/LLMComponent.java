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
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippJsonDecodeException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.fel.AippLlmMeta;
import com.huawei.fit.jober.aipp.fel.AippMemory;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
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
import com.huawei.jade.fel.engine.operators.patterns.Agent;
import com.huawei.jade.fel.engine.operators.prompts.Prompts;
import com.huawei.jade.fel.tool.ToolProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM 组件实现
 *
 * @author h00804153
 * @since 2024/4/15
 */
@Component
public class LLMComponent implements FlowableService, FlowCallbackService {
    private static final Logger log = Logger.get(LLMComponent.class);

    private static final String SYSTEM_PROMPT = "# 工具参数\n\n"
            + "在调用工具时尽可能使用以下参数：\n"
            + "- 参数 " + AippConst.TRACE_ID + "，值 {{0}}\n"
            + "- 参数 " + AippConst.CALLBACK_ID + "，值 com.huawei.fit.jober.aipp.fitable.LLMComponentCallback\n\n"
            + "# 人设与回复逻辑\n\n{{2}}";

    private static final String PROMPT_TEMPLATE = "{{1}}";

    // todo: 暂时使用ConcurrentHashMap存储父节点的元数据
    private final ConcurrentHashMap<String, AippLlmMeta> llmCache = new ConcurrentHashMap<>();

    private final FlowInstanceService flowInstanceService;
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;
    private final ToolProvider toolProvider;
    private final AiProcessFlow<Tip, Prompt> agentFlow;
    private final AippLogService aippLogService;

    /**
     * 大模型节点构造器，内部通过提供的agent和tool构建智能体工作流。
     *
     * @param flowInstanceService 表示流程实例服务的 {@link FlowInstanceService}。
     * @param metaInstanceService 表示元数据实例服务的 {@link MetaInstanceService}。
     * @param metaService 表示提供给AIPP元数据服务的 {@link MetaService}。
     * @param toolProvider 表示具提供者功能的 {@link ToolProvider}。
     * @param agent 表示提供智能体功能的 {@link Agent}{@code <}{@link ChatMessages}{@code ,} {@link ChatMessages}{@code >}。
     */
    public LLMComponent(FlowInstanceService flowInstanceService, MetaInstanceService metaInstanceService,
            MetaService metaService, ToolProvider toolProvider, Agent<Prompt, Prompt> agent,
            AippLogService aippLogService) {
        this.flowInstanceService = flowInstanceService;
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.toolProvider = toolProvider;
        this.aippLogService = aippLogService;

        // handleTask从入口开始处理，callback从agent node开始处理
        this.agentFlow = AiFlows.<Tip>create()
                .prompt(Prompts.sys(SYSTEM_PROMPT), Prompts.history(), Prompts.human(PROMPT_TEMPLATE))
                .id("agent")
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
        Map<String, Object> childBusinessData = Utils.getBusiness(childFlowData);
        log.debug("LLMComponentCallback business data {}", childBusinessData);
        String toolOutput = ObjectUtils.cast(childBusinessData.get(AippConst.BS_AIPP_FINAL_OUTPUT));
        String parentInstanceId = ObjectUtils.cast(childBusinessData.get(AippConst.PARENT_INSTANCE_ID));
        AippLlmMeta llmMeta = llmCache.get(parentInstanceId);
        if (!ObjectUtils.<Boolean>cast(childBusinessData.get(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM))) {
            Map<String, Object> businessData = llmMeta.getBusinessData();
            businessData.putIfAbsent("output", new HashMap<String, Object>());
            Map<String, Object> output = ObjectUtils.cast(businessData.get("output"));
            // todo: 当前如果子流程不需要模型加工，子流程和主流程会重复打印 toolOutput。
            //  为了避免这种情况，临时设置一个 key 来表明结果是否来自子流程。
            //  如果结果来自子流程，主流程的结束节点不打印；否则主流程的结束节点打印。
            output.put("llmOutput", toolOutput);
            businessData.put(AippConst.OUTPUT_IS_FROM_CHILD, true);
            doOnAgentComplete(llmMeta);
            return;
        }
        // todo: 暂时原地修改，之后再看是否需要创建新的
        ChatMessages chatMessages = ChatMessages.from(llmMeta.getTrace().messages());
        ChatMessage lastMessage = chatMessages.messages().remove(chatMessages.messages().size() - 1);
        chatMessages.add(new ToolMessage(lastMessage.id().orElse(null), toolOutput));
        llmMeta.setTrace(chatMessages);
        agentFlow.converse()
                .doOnSuccess(msg -> llmOutputConsumer(llmMeta, ObjectUtils.cast(msg)))
                .doOnError(throwable -> doOnAgentError(llmMeta, throwable.getMessage()))
                .offer("agent", Collections.singletonList(chatMessages));
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
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("LLMComponent business data {}", businessData);

        AippLlmMeta llmMeta = AippLlmMeta.parse(flowData, metaService, metaInstanceService);
        llmCache.put(llmMeta.getInstId(), llmMeta);

        String systemPrompt = ObjectUtils.cast(businessData.get("systemPrompt"));
        // todo: 待add多模态，期望使用image的url，当前传入的历史记录里面没有image
        agentFlow.converse()
                .bind(new AippMemory(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_MEMORY_KEY))))
                .doOnSuccess(msg -> {
                    llmOutputConsumer(llmMeta, msg);
                })
                .doOnError(throwable -> {
                    doOnAgentError(llmMeta, throwable.getMessage());
                })
                .bind(buildChatOptions(businessData))
                .offer(Tip.fromArray(llmMeta.getInstId(), buildInputText(businessData), systemPrompt));
        return flowData;
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
            // todo: resumeAsyncJober保存businessData
            Map<String, Object> businessData = llmMeta.getBusinessData();
            businessData.putIfAbsent("output", new HashMap<String, Object>());
            Map<String, Object> output = ObjectUtils.cast(businessData.get("output"));
            output.put("llmOutput", answer.text());
            InstanceDeclarationInfo info = InstanceDeclarationInfo.custom().putInfo("llmOutput", answer.text()).build();
            metaInstanceService.patchMetaInstance(llmMeta.getVersionId(),
                    llmMeta.getInstId(),
                    info,
                    llmMeta.getContext());
            doOnAgentComplete(llmMeta);
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

    private void setChildInstanceId(AippLlmMeta llmMeta, String childInstanceId) {
        InstanceDeclarationInfo info =
                InstanceDeclarationInfo.custom().putInfo(AippConst.INST_CHILD_INSTANCE_ID, childInstanceId).build();
        this.metaInstanceService.patchMetaInstance(llmMeta.getVersionId(), llmMeta.getInstId(), info, llmMeta.getContext());
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

        // todo@zhangyue 待确认，最后状态是变成ERROR还是TERMINAL.
        Utils.persistAippErrorLog(this.aippLogService, msg, llmMeta.getFlowData());
    }

    /**
     * 使用{@link StringTemplate}渲染用户模板。
     */
    private static String buildInputText(Map<String, Object> businessData) {
        Map<String, Object> input = ObjectUtils.cast(businessData.get("prompt"));
        StringTemplate template = new DefaultStringTemplate(ObjectUtils.cast(input.get("template")));
        Map<String, String> variables = ObjectUtils.cast(input.get("variables"));
        try {
            return template.render(variables);
        } catch (NullPointerException e) {
            throw new AippException(Utils.getOpContext(businessData), AippErrCode.LLM_COMPONENT_TEMPLATE_RENDER_FAILED);
        }
    }

    /**
     * 解析表示自定义参数的{@link ChatOptions}, 当前支持模型、温度、工具。
     */
    private ChatOptions buildChatOptions(Map<String, Object> businessData) {
        List<String> skillNameList = new ArrayList<>(ObjectUtils.cast(businessData.get("tools")));
        skillNameList.addAll(ObjectUtils.cast(businessData.get("workflows")));
        return ChatOptions.builder()
                .model(ObjectUtils.cast(businessData.get("model")))
                .maxTokens(18000)
                .temperature(ObjectUtils.cast(businessData.get("temperature")))
                .tools(this.toolProvider.getTool(skillNameList))
                .build();
    }
}