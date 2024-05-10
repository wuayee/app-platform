// /*
//  *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
//  */
//
// package com.huawei.fit.jober.taskcenter.fitable;
//
// import static com.huawei.fitframework.util.ObjectUtils.cast;
//
// import com.huawei.fit.http.annotation.DeleteMapping;
// import com.huawei.fit.http.annotation.GetMapping;
// import com.huawei.fit.http.annotation.PathVariable;
// import com.huawei.fit.http.annotation.PostMapping;
// import com.huawei.fit.http.annotation.RequestBody;
// import com.huawei.fit.http.annotation.RequestMapping;
// import com.huawei.fit.http.annotation.ResponseStatus;
// import com.huawei.fit.http.protocol.HttpResponseStatus;
// import com.huawei.fit.jane.Undefinable;
// import com.huawei.fit.jane.common.entity.OperationContext;
// import com.huawei.fit.jane.meta.definition.Meta;
// import com.huawei.fit.jane.meta.definition.MetaDeclarationInfo;
// import com.huawei.fit.jane.meta.definition.MetaFilter;
// import com.huawei.fit.jane.meta.instance.Instance;
// import com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo;
// import com.huawei.fit.jane.meta.instance.MetaInstanceFilter;
// import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
// import com.huawei.fit.jane.task.gateway.Authenticator;
// import com.huawei.fit.jane.task.util.Dates;
//
// import com.huawei.fit.jober.common.RangeResult;
// import com.huawei.fit.jober.common.RangedResultSet;
// import com.huawei.fit.jober.entity.task.TaskProperty;
// import com.huawei.fit.jober.entity.task.TaskPropertyCategory;
// import com.huawei.fit.jober.taskcenter.controller.AbstractController;
// import com.huawei.fitframework.annotation.Component;
//
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.function.Function;
// import java.util.stream.Collectors;
//
// /**
//  * Todo
//  *
//  * @author 孙怡菲 s00664640
//  * @since 2023-12-13
//  */
// @Component
// @RequestMapping(value = AbstractController.URI_PREFIX + "/fitable")
// public class LocalController extends AbstractController {
//     private final MetaFitable metaFitable;
//
//     private final MetaInstanceFitable metaInstanceFitable;
//
//     public LocalController(Authenticator authenticator, MetaFitable metaFitable, MetaInstanceFitable metaInstanceFitable) {
//         super(authenticator);
//         this.metaFitable = metaFitable;
//         this.metaInstanceFitable = metaInstanceFitable;
//     }
//
//     @PostMapping(value="/meta")
//     @ResponseStatus(HttpResponseStatus.CREATED)
//     public Map<String, Object> create(@RequestBody("data") Map<String, Object> data) {
//         MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         Meta entity = this.metaFitable.create(declaration, context);
//         return viewOf(entity);
//     }
//
//     @PostMapping(value="/meta/{metaId}")
//     @ResponseStatus(HttpResponseStatus.CREATED)
//     public void patch(@RequestBody("data") Map<String, Object> data, @PathVariable("metaId") String metaId) {
//         MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         this.metaFitable.patch(metaId, declaration, context);
//         // return viewOf(entity);
//     }
//
//     @DeleteMapping(value="/meta")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public void delete(@RequestBody("data") String data) {
//         // MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         this.metaFitable.delete(data, context);
//     }
//
//     @GetMapping(value="/meta/{metaId}")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public Map<String, Object> retrieve(@PathVariable("metaId") String metaId) {
//         // MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         Meta entity = this.metaFitable.retrieve(metaId, context);
//         return viewOf(entity);
//     }
//
//     @GetMapping(value="/meta/list")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public Map<String, Object> list(@RequestBody("data") Map<String, Object> data) {
//         // MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         Undefinable<List<String>> ids = new Undefinable(true, data.get("list"));
//         Undefinable<List<String>> names = new Undefinable(false, null);
//         MetaFilter metaFilter = new MetaFilter();
//         metaFilter.setIds(ids);
//         metaFilter.setNames(names);
//         RangedResultSet<Meta> res = this.metaFitable.list(metaFilter, 0, 10, context);
//         return viewOf(res, "meta", LocalController::viewOf);
//     }
//
//
//
//     @PostMapping(value="/metaInstance/{metaId}")
//     @ResponseStatus(HttpResponseStatus.CREATED)
//     public Map<String, Object> createInstance(@RequestBody("data") Map<String, Object> data, @PathVariable("metaId") String metaId) {
//         InstanceDeclarationInfo info = declareInstance(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         Instance entity = this.metaInstanceFitable.createMetaInstance(metaId, info, context);
//         return viewOf(entity);
//     }
//
//     @PostMapping(value="/metaInstance/{instanceId}/{metaId}")
//     @ResponseStatus(HttpResponseStatus.CREATED)
//     public void patchInstance(@RequestBody("data") Map<String, Object> data, @PathVariable("instanceId") String instanceId, @PathVariable("metaId") String metaId) {
//         InstanceDeclarationInfo info = declareInstance(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         this.metaInstanceFitable.patchMetaInstance(metaId, instanceId, info, context);
//         // return viewOf(entity);
//     }
//
//     @DeleteMapping(value="/metaInstance/{instanceId}/{metaId}")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public void deleteInstance(@PathVariable("instanceId") String instanceId, @PathVariable("metaId") String metaId) {
//         // MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         this.metaInstanceFitable.deleteMetaInstance(metaId, instanceId, context);
//     }
//
//
//     @GetMapping(value="/metaInstance/list/{metaId}")
//     @ResponseStatus(HttpResponseStatus.OK)
//     public Map<String, Object> listInstance(@PathVariable("metaId") String metaId) {
//         // MetaDeclarationInfo declaration = declareTask(data);
//         OperationContext context = new OperationContext("3e59c6bc800a4c7682a5f9e27eccf276", "syf_test", "", "", "", "", "", "", "");
//         MetaInstanceFilter metaFilter = new MetaInstanceFilter();
//         Undefinable<List<String>> list = new Undefinable(false, null);
//         Undefinable<Map<String, List<String>>> map = new Undefinable(false, null);
//         metaFilter.setIds(list);
//         metaFilter.setTags(list);
//         metaFilter.setInfos(map);
//         metaFilter.setOrderBy(list);
//         RangedResultSet<Instance> res = this.metaInstanceFitable.list(metaId, metaFilter, 0, 10, context);
//         return viewOf(res, "metaInstance", LocalController::viewOf);
//     }
//
//
//
//     private MetaDeclarationInfo declareTask(Map<String, Object> view) {
//         if (view == null) {
//             return null;
//         }
//         MetaDeclarationInfo declaration = new MetaDeclarationInfo();
//         declaration.setName(valueOf(view, "name"));
//         declaration.setAttributes(valueOf(view, "attributes"));
//         declaration.setProperties(listOf(view, "properties"));
//         return declaration;
//     }
//
//     private static <T> Undefinable<T> valueOf(Map<String, Object> view, String key) {
//         Undefinable<T> test = new Undefinable<>();
//         if (!view.containsKey(key)) {
//             test.setDefined(false);
//             return test;
//         }
//         T value = cast(view.get(key));
//         test.setDefined(true);
//         test.setValue(value);
//         return test;
//     }
//
//     private static Undefinable<List<MetaPropertyDeclarationInfo>> listOf(Map<String, Object> view, String key) {
//         Undefinable<List<MetaPropertyDeclarationInfo>> res = new Undefinable<>();
//         if (!view.containsKey(key)) {
//             res.setDefined(false);
//             return res;
//         }
//         @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) view.get(key);
//         List<MetaPropertyDeclarationInfo> results = new ArrayList<>(list.size());
//         for (Map<String, Object> item : list) {
//             MetaPropertyDeclarationInfo result = new MetaPropertyDeclarationInfo();
//             result.setName(valueOf(item, "name"));
//             result.setDataType(valueOf(item, "dataType"));
//             result.setDescription(valueOf(item, "dataType"));
//             result.setRequired(valueOf(item, "required"));
//             result.setIdentifiable(valueOf(item, "identifiable"));
//             result.setScope(valueOf(item, "scope"));
//             result.setAppearance(valueOf(item, "appearance"));
//             results.add(result);
//         }
//         res.setDefined(true);
//         res.setValue(results);
//         return res;
//     }
//
//     public static Map<String, Object> viewOf(Meta task) {
//         if (task == null) {
//             return new LinkedHashMap<>();
//         }
//         Map<String, Object> view = new LinkedHashMap<>();
//         view.put("id", task.getId());
//         view.put("name", task.getName());
//         view.put("properties", task.getProperties().stream().map(LocalController::viewOf).collect(Collectors.toList()));
//         view.put("attributes", task.getAttributes());
//         view.put("creator", task.getCreator());
//         view.put("creationTime", Dates.toString(task.getCreationTime()));
//         view.put("lastModifier", task.getLastModifier());
//         view.put("lastModificationTime", Dates.toString(task.getLastModificationTime()));
//         return view;
//     }
//
//     public static Map<String, Object> viewOf(TaskProperty property) {
//         if (property == null) {
//             return Collections.emptyMap();
//         }
//         Map<String, Object> view = new LinkedHashMap<>();
//         view.put("id", property.getId());
//         view.put("name", property.getName());
//         view.put("dataType", property.getDataType());
//         view.put("sequence", property.getSequence());
//         view.put("description", property.getDescription());
//         view.put("identifiable", property.isIdentifiable());
//         view.put("required", property.isRequired());
//         view.put("scope", property.getScope());
//         view.put("appearance", property.getAppearance());
//         view.put("categories", property.getCategories().stream().map(LocalController::viewOf).collect(Collectors.toList()));
//         return view;
//     }
//
//     public static Map<String, Object> viewOf(TaskPropertyCategory propertyCategory) {
//         if (propertyCategory == null) {
//             return new LinkedHashMap<>();
//         }
//         Map<String, Object> view = new LinkedHashMap<>(2);
//         view.put("value", propertyCategory.getValue());
//         view.put("category", propertyCategory.getCategory());
//         return view;
//     }
//
//     public static <T> Map<String, Object> viewOf(RangedResultSet<T> results, String key,
//         Function<T, Map<String, Object>> mapper) {
//         if (results == null) {
//             return new LinkedHashMap<>();
//         }
//         Map<String, Object> view = new LinkedHashMap<>(2);
//         view.put(key, results.getResults().stream().map(mapper).collect(Collectors.toList()));
//         view.put("pagination", viewOf(results.getRange()));
//         return view;
//     }
//
//     private static Map<String, Object> viewOf(RangeResult result) {
//         if (result == null) {
//             return new LinkedHashMap<>();
//         }
//         Map<String, Object> view = new LinkedHashMap<>(3);
//         view.put("offset", result.getOffset());
//         view.put("limit", result.getLimit());
//         view.put("total", result.getTotal());
//         return view;
//     }
//
//     private InstanceDeclarationInfo declareInstance(Map<String, Object> data) {
//         if (data == null) {
//             return null;
//         }
//         InstanceDeclarationInfo declaration = new InstanceDeclarationInfo();
//         declaration.setInfo(valueOf(data, "info"));
//         declaration.setTags(valueOf(data, "tags"));
//         return declaration;
//     }
//
//     public static Map<String, Object> viewOf(Instance instance) {
//         if (instance == null) {
//             return new LinkedHashMap<>();
//         }
//         Map<String, Object> view = new LinkedHashMap<>(5);
//         view.put("id", instance.getId());
//         view.put("info", instance.getInfo());
//         view.put("tags", instance.getTags());
//         return view;
//     }
//
// }
