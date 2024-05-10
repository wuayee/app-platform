/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fitframework.runtime.FitStarter;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JoberApplication
 *
 * @author c00572808
 * @since 2023/10/30
 */
public class JoberApplication {
    private static final String ADS_VMIP_KEY = "VMIP";

    private static final String ADS_VMPORT_KEY = "VMPORT";

    private static final String DEFAULT_VMIP = "localhost";

    private static final String DEFAULT_VMPORT = "8028";

    private static final String WORKER_ID_PREFIX = "jober-worker";

    private static String registryIp;

    private static String registryPort;

    public static void main(String[] args) {
        System.out.println("Start application.");
        String[] modifiedArgs = args;
        if (StringUtils.isNotBlank(System.getProperty(ADS_VMIP_KEY))) {
            modifiedArgs = insertVMIP(modifiedArgs);
            modifiedArgs = insertVMPort(modifiedArgs);
            modifiedArgs = insertWorkerId(modifiedArgs);
        }
        FitStarter.start(JoberApplication.class, modifiedArgs);
    }

    private static String[] insertWorkerId(String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        String workerId = WORKER_ID_PREFIX + "-" + registryIp + "-" + registryPort;
        argList.add("worker.id=" + workerId);
        System.out.println("worker.id=" + workerId);
        return argList.toArray(new String[0]);
    }

    private static String[] insertVMIP(String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        String vmIp = System.getProperty(ADS_VMIP_KEY, DEFAULT_VMIP);
        argList.add("worker.host=" + vmIp);
        System.out.println("worker.host=" + vmIp);
        registryIp = vmIp;
        return argList.toArray(new String[0]);
    }

    private static String[] insertVMPort(String[] args) {
        String vmPort = DEFAULT_VMPORT;
        Optional<String> optionalVMPorts = Optional.ofNullable(System.getProperty(ADS_VMPORT_KEY));
        if (optionalVMPorts.isPresent()) {
            String vmPorts = optionalVMPorts.get();
            vmPort = JSON.parseArray(vmPorts, PortStruct.class)
                    .stream()
                    .filter(vmInfo -> StringUtils.equals("public", vmInfo.getPortname()))
                    .map(PortStruct::getPorts)
                    .flatMap(Collection::stream)
                    .map(HostStruct::getHostPort)
                    .collect(Collectors.toList())
                    .get(0);
        }
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        argList.add("server.http.to-register-port=" + vmPort);
        System.out.println("server.http.to-register-port=" + vmPort);
        registryPort = vmPort;
        return argList.toArray(new String[0]);
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PortStruct {
        private String originport;

        private String portname;

        private List<HostStruct> ports;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class HostStruct {
        @JSONField(name = "HostIP")
        private String hostIp;

        @JSONField(name = "HostPort")
        private String hostPort;
    }
}
