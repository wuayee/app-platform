# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
from typing import List
from numpy import int32, uint32, uint64


class FitableIds(object):

    def __init__(self, genericableId: str, fitableIds: List[str]):
        self.genericableId = genericableId
        self.fitableIds = fitableIds

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class BaseFitTrace(object):

    def __init__(self, traceTimestamp: uint64, traceId: str, span: str, traceType: uint32, host: str, port: uint32,
                 workerId: str, resultCode: str, timeCost: uint32, flowType: uint32, stage: uint32):
        self.traceTimestamp = traceTimestamp
        self.traceId = traceId
        self.span = span

        # 调用类型
        self.traceType = traceType

        # 主机
        self.host = host

        # 端口
        self.port = port
        self.workerId = workerId

        # 调用结果，ok/Timeout/ConnectError
        self.resultCode = resultCode

        # 时间花费
        self.timeCost = timeCost

        # 流量类型
        self.flowType = flowType

        # 阶段
        self.stage = stage

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class FitableTrace(object):

    def __init__(self, baseFitTrace: BaseFitTrace, genericId: str, genericVersion: str, fitId: str, serviceName: str,
                 targetHost: str, targetPort: uint32, callType: uint32, fromFitId: str, trustStage: uint32):
        self.baseFitTrace = baseFitTrace
        self.genericId = genericId
        self.genericVersion = genericVersion
        self.fitId = fitId
        self.serviceName = serviceName
        self.targetHost = targetHost
        self.targetPort = targetPort

        # 调用类型  1 本地调用本地  2 本地调用远程  3 远程调用本地
        self.callType = callType
        self.fromFitId = fromFitId

        # 可信步骤 0: validation, 1: before, 2: process, 3: degredation, 4: after, 5: error
        self.trustStage = trustStage

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class Address(object):

    def __init__(self, workerId: str, host: str, port: int32, context_path: str):
        """
        地址所在进程唯一标识，正常ip网络下可用host:ip拼接或uuid作为唯一标识.
        心跳时，进程上报给心跳服务时应使用该标识，用于进程下线时更新对应服务的状态.
        """
        self.workerId = workerId
        self.host = host
        self.port = port
        self.context_path = context_path

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class FitableInfo(object):

    def __init__(self, genericableId: str, fitableId: str):
        self.genericableId = genericableId
        self.fitableId = fitableId

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class Endpoint(object):

    def __init__(self, address: Address, protocol: int32, serializeFormats: List[int32], environment: str,
                 weight: int32):
        self.address = address

        """
        rsocket - 0
        socket - 1
        http - 2
        grpc - 3
        uc - 10
        shareMemory - 11
        """
        self.protocol = protocol

        """
        protobuf - 0
        json - 1
        """
        self.serializeFormats = serializeFormats
        self.environment = environment
        self.weight = weight

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))
