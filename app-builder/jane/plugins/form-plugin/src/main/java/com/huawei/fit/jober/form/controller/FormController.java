/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.controller;

import com.huawei.fit.dynamicform.common.PageResponse;
import com.huawei.fit.dynamicform.condition.FormQueryCondition;
import com.huawei.fit.dynamicform.condition.PaginationCondition;
import com.huawei.fit.dynamicform.dto.DynamicFormDto;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.form.dto.FormDetailDto;
import com.huawei.fit.jober.form.dto.FormDto;
import com.huawei.fit.jober.form.exception.FormErrCode;
import com.huawei.fit.jober.form.service.impl.DynamicFormServiceImpl;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

import java.util.stream.Collectors;

/**
 * 表单管理接口
 *
 * @author 熊以可
 * @since 2023/12/13
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "表单接口")
public class FormController extends AbstractController {
    private final DynamicFormServiceImpl formServiceImpl;

    /**
     * 构造函数
     *
     * @param authenticator 认证器
     * @param formServiceImpl 表单服务
     */
    public FormController(@Fit Authenticator authenticator, @Fit DynamicFormServiceImpl formServiceImpl) {
        super(authenticator);
        this.formServiceImpl = formServiceImpl;
    }

    /**
     * 查询表单
     *
     * @param tenantId 租户id
     * @param cond 条件
     * @param page 页
     * @return 表单数据
     */
    @GetMapping(value = "/form", description = "批量查询表单")
    public Rsp<PageResponse<FormDto>> queryForm(@PathVariable("tenant_id") String tenantId,
            @RequestBean FormQueryCondition cond, @RequestBean PaginationCondition page) {
        PageResponse<DynamicFormEntity> result = formServiceImpl.queryFormWithCondition(tenantId, cond, page);
        return Rsp.ok(new PageResponse<>(result.getTotal(),
                result.getItems().stream().map(FormDto::new).collect(Collectors.toList())));
    }

    /**
     * 查询表单
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param formId 表单id
     * @param version 版本
     * @return 查询数据
     */
    @GetMapping(value = "/form/{form_id}", description = "查询单个表单详细数据")
    public Rsp<FormDetailDto> queryForm(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("form_id") String formId,
            @RequestParam("version") String version) {
        DynamicFormDetailEntity entity =
                formServiceImpl.queryFormDetailByPrimaryKey(formId, version, this.contextOf(httpRequest, tenantId));
        if (entity != null) {
            return Rsp.ok(new FormDetailDto(entity));
        }
        return Rsp.err(FormErrCode.NOT_FOUND);
    }

    /**
     * 保存表单
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param formId 表单id
     * @param formDetailDto 表单数据
     * @return 保存结果
     */

    @PostMapping(path = "/form/{form_id}", description = "创建或保存单个表单，返回操作状态：成功/失败")
    public Rsp<Object> saveForm(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("form_id") String formId, @RequestBody FormDetailDto formDetailDto) {
        formDetailDto.getMeta().setId(formId);
        if (formServiceImpl.saveForm(formDetailDto.toEntity(), this.contextOf(httpRequest, tenantId))) {
            return Rsp.ok();
        }
        return Rsp.err(FormErrCode.UNKNOWN);
    }

    /**
     * 删除表单
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param formId 表单id
     * @param version 版本
     * @return 删除结果
     */
    @DeleteMapping(path = "/form/{form_id}", description = "删除单个表单, 返回是否成功")
    public Rsp<Object> deleteForm(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("form_id") String formId, @RequestParam("version") String version) {
        if (formServiceImpl.deleteForm(DynamicFormDto.builder().id(formId).version(version).build(),
                this.contextOf(httpRequest, tenantId))) {
            return Rsp.ok();
        }
        return Rsp.err(FormErrCode.NOT_FOUND);
    }
}
