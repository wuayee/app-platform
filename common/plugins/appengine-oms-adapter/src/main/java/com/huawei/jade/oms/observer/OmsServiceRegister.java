/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.oms.observer;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.jade.oms.util.ResourceUtils.resolve;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.resource.ResourceResolver;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.runtime.FitRuntimeStartedObserver;
import com.huawei.jade.oms.certificate.management.service.CertMgmtService;
import com.huawei.jade.oms.certificate.management.service.impl.CertMgmtServiceImpl;
import com.huawei.jade.oms.license.manager.LicenseClient;
import com.huawei.jade.oms.service.access.meta.menu.MenuRegisterInfo;
import com.huawei.jade.oms.service.access.meta.permission.AuthorityInfo;
import com.huawei.jade.oms.service.access.meta.role.RoleI18nInfo;
import com.huawei.jade.oms.service.access.meta.role.RoleRegisterInfo;
import com.huawei.jade.oms.service.access.meta.role.RoleRegisterVo;
import com.huawei.jade.oms.service.access.processor.RegisterProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 解析资源注册 OMS 服务类。
 *
 * @author 鲁为
 * @since 2024-11-18
 */
@Component
public class OmsServiceRegister implements FitRuntimeStartedObserver {
    private static final Logger LOG = Logger.get(OmsServiceRegister.class);
    private static final String MENU_RESOURCE = "**/preset_menu*.json";
    private static final String ROLE_RESOURCE = "**/preset_role.json";
    private static final String ROLE_I18N_RESOURCE = "**/preset_role_i18n.json";
    private static final List<String> AUTHORITY_RESOURCES = Arrays.asList("**/common.json",
            "**/rest-service-aipp.json",
            "**/rest-service-appBuilder.json",
            "**/rest-service-store.json");

    private final Plugin plugin;
    private final RegisterProcessor registerProcessor;
    private final LicenseClient licenseClient;
    private final CertMgmtService certMgmtService;
    private final Boolean initCert;


    /**
     * 用插件实例和注册处理器实例构造 {@link OmsServiceRegister} 实例。
     *
     * @param plugin 表示插件类的 {@link Plugin}。
     * @param registerProcessor 表示注册处理器的 {@link RegisterProcessor}。
     * @param licenseClient 表示许可证客户端的 {@link LicenseClient}。
     * @param certMgmtService
     */
    public OmsServiceRegister(Plugin plugin, RegisterProcessor registerProcessor, LicenseClient licenseClient,
            CertMgmtService certMgmtService, @Value("${certMgmt.enable}") Boolean initCert) {
        this.plugin = notNull(plugin, "The plugin cannot be null.");
        this.registerProcessor = notNull(registerProcessor, "The register processor cannot be null.");
        this.licenseClient = notNull(licenseClient, "The license client cannot be null.");
        this.certMgmtService = certMgmtService;
        this.initCert = (initCert != null) && initCert;
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        this.registerLicense();
        this.registerRbac();
        if (this.initCert) {
            LOG.info("Start to initialize the cert.");
            this.certMgmtService.registerCertToOms();
        }
    }

    private void registerLicense() {
        this.licenseClient.registerProductInfo();
    }

    private void registerRbac() {
        ResourceResolver resourceResolver = this.plugin.resolverOfResources();
        this.registerProcessor.registerMenu(this.parseMenuInfo(resourceResolver, MENU_RESOURCE));
        RoleRegisterVo roleRegisterVo = new RoleRegisterVo();
        roleRegisterVo.setRoleRegisterInfos(this.parseRoleInfo(resourceResolver, ROLE_RESOURCE));
        roleRegisterVo.setRoleI18nInfos(this.parseRoleI18nInfo(resourceResolver, ROLE_I18N_RESOURCE));
        this.registerProcessor.registerRole(Arrays.asList(roleRegisterVo));
        for (String metaName : AUTHORITY_RESOURCES) {
            this.registerProcessor.registerPermission(this.parseAuthorityInfo(resourceResolver, metaName));
        }
    }

    private List<MenuRegisterInfo> parseMenuInfo(ResourceResolver resourceResolver, String metaName) {
        return cast(this.registerProcessor.parse(resolve(resourceResolver, metaName), MenuRegisterInfo.class));
    }

    private List<RoleRegisterInfo> parseRoleInfo(ResourceResolver resourceResolver, String metaName) {
        return cast(this.registerProcessor.parse(resolve(resourceResolver, metaName), RoleRegisterInfo.class));
    }

    private List<RoleI18nInfo> parseRoleI18nInfo(ResourceResolver resourceResolver, String metaName) {
        return cast(this.registerProcessor.parse(resolve(resourceResolver, metaName), RoleI18nInfo.class));
    }

    private List<AuthorityInfo> parseAuthorityInfo(ResourceResolver resourceResolver, String metaName) {
        return cast(this.registerProcessor.parse(resolve(resourceResolver, metaName), AuthorityInfo.class));
    }
}
