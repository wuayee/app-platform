// /*
//  *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
//  */
//
// package com.huawei.fit.jober.bff.controller.a3000;
//
// import com.huawei.fit.http.annotation.DeleteMapping;
// import com.huawei.fit.http.annotation.GetMapping;
// import com.huawei.fit.http.annotation.PathVariable;
// import com.huawei.fit.http.annotation.PostMapping;
// import com.huawei.fit.http.annotation.RequestBody;
// import com.huawei.fit.http.annotation.RequestMapping;
// import com.huawei.fit.http.annotation.ResponseStatus;
// import com.huawei.fit.http.protocol.HttpResponseStatus;
// import com.huawei.fit.jane.task.gateway.Authenticator;
// import com.huawei.fit.jober.bff.service.a3000.OrchestratorFitService;
// import com.huawei.fit.jober.taskcenter.controller.AbstractController;
// import com.huawei.fitframework.annotation.Component;
// import com.huawei.hisp.clean.client.FlowConfiguration;
// import com.huawei.hisp.clean.client.QueryCriteria;
//
// import com.alibaba.fastjson2.JSONObject;
//
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
// /**
//  * Todo
//  *
//  * @author 孙怡菲 s00664640
//  * @since 2023-12-28
//  */
// // @AllArgsConstructor
// @Component
// @RequestMapping(value = AbstractController.URI_PREFIX + "/test", group = "A3000 clean")
// public class Locala3000Controller extends AbstractController {
//     private final OrchestratorFitService orchestratorController;
//
//     public Locala3000Controller(Authenticator authenticator, OrchestratorFitService orchestratorController) {
//         super(authenticator);
//         this.orchestratorController = orchestratorController;
//     }
//
//     @DeleteMapping(value = "/{flowId}/{version}")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public void deleteFlow(@PathVariable("flowId") String flowId, @PathVariable("version") String version) {
//         orchestratorController.deleteFlow(flowId, version, null);
//     }
//
//     @GetMapping(value = "/ok")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public String testOk() {
//         return "ok";
//     }
//
//     @PostMapping(value = "/{flowId}/{version}")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public Optional<Map<String, Object>> findFlow(@PathVariable("flowId") String flowId, @PathVariable("version") String version) {
//         return orchestratorController.getFlowConfigById(flowId, version, null);
//     }
//
//     @PostMapping(value = "/create/{flowId}/{version}")
//     @ResponseStatus(HttpResponseStatus.CREATED)
//     public void createFlow(@PathVariable("flowId") String flowId, @PathVariable("version") String version, @RequestBody Map<String, String> data) {
//         FlowConfiguration flowConfiguration = new FlowConfiguration();
//         Optional.ofNullable(data.get("tag"))
//             .ifPresent(tag -> flowConfiguration.setTags(Arrays.asList(tag.split(","))));
//         Optional.ofNullable(data.get("previous"))
//             .ifPresent(flowConfiguration::setPrevious);
//         flowConfiguration.setDefinitionData(data.get("graphData"));
//         orchestratorController.createFlow(flowId, version, flowConfiguration, null);
//     }
//
//     @PostMapping(value = "/list")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public Optional<Map<String, Object>> getFlowList(@RequestBody QueryCriteria queryCriteria) {
//         return orchestratorController.getAllFlows(queryCriteria, null);
//     }
//
//     @PostMapping(value =  "/generate-insert")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public String getInsertSql(@RequestBody Map<String, String> data) {
//         List<String> tags = new ArrayList<>();
//         Optional.ofNullable(data.get("tag"))
//             .ifPresent(tag -> tags.addAll(Arrays.asList(tag.split(","))));
//         String version = data.get("version");
//         String json = data.get("definitionData");
//         JSONObject parsedData = JSONObject.parseObject(json);
//         String definitionName = (String) parsedData.get("title");
//         String dataId = (String) parsedData.get("id");
//         String publishStatus = "active";
//         LocalDateTime currentTime = LocalDateTime.now();
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//         String time = currentTime.format(formatter);
//         StringBuilder stringBuilder = new StringBuilder();
//         stringBuilder.append(
//             "INSERT INTO \"public\".\"flow_graph\" (\"id\", \"version\", \"tenant\", \"status\", \"name\", \"data\", \"created_by\", \"created_at\", \"updated_by\", \"updated_at\", \"previous\", \"is_deleted\") VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}', '{9}', NULL, '{10}') ON CONFLICT (id, version) DO NOTHING;");
//         List<String> args = new ArrayList<>();
//         args.add(dataId);
//         args.add(version);
//         args.add("A3000");
//         args.add(publishStatus);
//         args.add(definitionName);
//         args.add(json);
//         args.add("A3000");
//         args.add(time);
//         args.add("A3000");
//         args.add(time);
//         args.add("f");
//         for (int i = 0; i < args.size(); i++) {
//             String placeholder = "{" + i + "}";
//             String replacement = args.get(i);
//             replacePlaceholder(stringBuilder, placeholder, replacement);
//         }
//
//         // 打印结果
//         System.out.println(stringBuilder.toString());
//         return null;
//     }
//
//     private static void replacePlaceholder(StringBuilder stringBuilder, String placeholder, String replacement) {
//         int index = stringBuilder.indexOf(placeholder);
//         while (index != -1) {
//             stringBuilder.replace(index, index + placeholder.length(), replacement);
//             index = stringBuilder.indexOf(placeholder, index + replacement.length());
//         }
//     }
// }
