/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.huawei.fit.service.entity.Address;
import com.huawei.fit.service.entity.Application;
import com.huawei.fit.service.entity.FitableAddressInstance;
import com.huawei.fit.service.entity.FitableInfo;
import com.huawei.fit.service.entity.FitableMeta;
import com.huawei.fit.service.entity.Worker;
import com.huawei.fit.service.server.RegistryServer;
import com.huawei.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RegistryServer} 的单测类。
 *
 * @author 邬涨财 w00575064
 * @author 季聿阶 j00559309
 * @since 2022-04-14
 */
@DisplayName("测试 RegistryServer")
public class RegistryServerTest {
    @Test
    @DisplayName("当将服务元数据列表进行注册时，注册成功")
    void testRegisterFitables() {
        // given
        List<FitableMeta> fitableMetas = new ArrayList<>();
        FitableInfo fitable1 = this.buildFitable("fid1", "gid1");
        FitableMeta fitableMeta1 = this.buildFitableMeta(fitable1, Collections.singletonList(1));
        FitableInfo fitable2 = this.buildFitable("fid2", "gid1");
        FitableMeta fitableMeta2 = this.buildFitableMeta(fitable2, Collections.singletonList(2));
        FitableInfo fitable3 = this.buildFitable("fid3", "gid2");
        FitableMeta fitableMeta3 = this.buildFitableMeta(fitable3, Collections.singletonList(1));
        Worker worker = this.buildWorker();
        Application application = this.buildApplication();
        fitableMetas.add(fitableMeta1);
        fitableMetas.add(fitableMeta2);
        fitableMetas.add(fitableMeta3);
        WorkerCache cache = Mockito.mock(WorkerCache.class);
        RegistryServer server = new RegistryServer(90, cache);

        // when and then
        assertThatNoException().isThrownBy(() -> server.registerFitables(fitableMetas, worker, application));
    }

    private Worker buildWorker() {
        Worker worker = new Worker();
        worker.setEnvironment("debug");
        worker.setId("wid1");
        worker.setAddresses(Collections.singletonList(new Address()));
        worker.setExtensions(MapBuilder.<String, String>get().put("expire", "1000").build());
        return worker;
    }

    @Test
    @DisplayName("当查询的服务包含已注册服务时，查询成功")
    void testQueryFitables() {
        // given
        List<FitableMeta> fitableMetas = new ArrayList<>();
        FitableInfo fitable1 = this.buildFitable("fid1", "gid1");
        FitableMeta fitableMeta1 = this.buildFitableMeta(fitable1, Collections.singletonList(1));
        FitableInfo fitable2 = this.buildFitable("fid2", "gid1");
        Worker worker = this.buildWorker();
        Application application = this.buildApplication();
        fitableMetas.add(fitableMeta1);
        WorkerCache cache = Mockito.mock(WorkerCache.class);
        RegistryServer server = new RegistryServer(90, cache);
        server.registerFitables(fitableMetas, worker, application);

        // when
        List<FitableAddressInstance> foundInstances =
                server.queryFitables(new ArrayList<>(Arrays.asList(fitable1, fitable2)), "wid1");

        // then
        assertThat(foundInstances.size()).isEqualTo(1);
        assertThat(foundInstances.get(0).getFitable().getFitableId()).isEqualTo("fid1");
    }

    private Application buildApplication() {
        Application application = new Application();
        application.setName("appName1");
        application.setNameVersion("1.0.0");
        application.setExtensions(new HashMap<>());
        return application;
    }

    private FitableInfo buildFitable(String fitableId, String genericableId) {
        FitableInfo fitable = new FitableInfo();
        fitable.setGenericableId(genericableId);
        fitable.setGenericableVersion("1.0.0");
        fitable.setFitableId(fitableId);
        fitable.setFitableVersion("1.0.0");
        return fitable;
    }

    private FitableMeta buildFitableMeta(FitableInfo fitable, List<Integer> formats) {
        FitableMeta meta = new FitableMeta();
        meta.setFitable(fitable);
        meta.setFormats(formats);
        return meta;
    }
}
