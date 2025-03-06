/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.license.manager.support;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.jade.oms.util.Constants.OMS_FRAMEWORK_NAME;
import static com.huawei.jade.oms.util.ResourceUtils.resolve;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.resource.ResourceResolver;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.oms.OmsClient;
import com.huawei.jade.oms.license.manager.LicenseClient;
import com.huawei.jade.oms.license.meta.LicenseInfo;
import com.huawei.jade.oms.license.meta.LicenseProductRegisterRequest;
import com.huawei.jade.oms.response.ResultVo;
import com.huawei.jade.oms.util.parser.JsonParser;

/**
 * 许可证处理客户端的默认实现。
 *
 * @author 李金绪
 * @since 2024-12-04
 */
@Component
public class DefaultLicenseClient implements LicenseClient {
    private static final String DEFAULT_LICENSE_RESOURCE = "**/product_register_info.json";
    private static final String URL_REGISTER_LICENSE_INFO = "/framework/v1/license/action/register/product/internal";
    private static final String URL_GET_LICENSE_INFO = "/framework/v1/license/info";
    private static final String LICENSE_VALID_CODE = "2";

    private final Plugin plugin;
    private final JsonParser parser;
    private final OmsClient omsClient;

    /**
     * 获取许可证信息。
     *
     * @param plugin 表示插件的 {@link Plugin}。
     * @param omsClient 表示 OMS 客户端的 {@link OmsClient}。
     * @param parser 表示 JSON 解析器的 {@link JsonParser}。
     */
    public DefaultLicenseClient(Plugin plugin, OmsClient omsClient, JsonParser parser) {
        this.plugin = notNull(plugin, "The plugin cannot be null.");
        this.omsClient = notNull(omsClient, "The oms client cannot be null.");
        this.parser = notNull(parser, "The parser cannot be null.");
    }

    @Override
    public ResultVo<LicenseInfo> getLicenseInfo() {
        return this.omsClient.executeJson(OMS_FRAMEWORK_NAME,
                HttpRequestMethod.GET,
                URL_GET_LICENSE_INFO,
                null,
                LicenseInfo.class);
    }

    @Override
    public boolean isLicenseValid() {
        ResultVo<LicenseInfo> result = this.omsClient.executeJson(OMS_FRAMEWORK_NAME,
                HttpRequestMethod.GET,
                URL_GET_LICENSE_INFO,
                null,
                LicenseInfo.class);
        return result.getData() != null && result.getData().getStatus() != null && StringUtils.equals(result.getData()
                .getStatus(), LICENSE_VALID_CODE);
    }

    @Override
    public void registerProductInfo() {
        LicenseProductRegisterRequest licenseProductRegisterRequest =
                this.parseLicenseProductRegisterRequest(this.plugin.resolverOfResources(), DEFAULT_LICENSE_RESOURCE);
        this.registerProductInfo(licenseProductRegisterRequest);
    }

    @Override
    public void registerProductInfo(LicenseProductRegisterRequest request) {
        this.omsClient.executeJson(OMS_FRAMEWORK_NAME,
                HttpRequestMethod.POST,
                URL_REGISTER_LICENSE_INFO,
                request,
                Void.class);
    }

    private LicenseProductRegisterRequest parseLicenseProductRegisterRequest(ResourceResolver resourceResolver,
            String metaName) {
        return cast(this.parser.parse(resolve(resourceResolver, metaName), LicenseProductRegisterRequest.class));
    }
}
