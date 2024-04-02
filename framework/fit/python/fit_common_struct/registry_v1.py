# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.

from typing import List
from numpy import int32


class AddressForRegistryV1(object):

    def __init__(self, host: str, port: int32, id: str, protocol: int32, formats: List[int32], environment: str):
        self.host = host
        self.port = port

        # 地址所在进程唯一标识，正常ip网络下可用host:port拼接或uuid作为唯一标识，心跳时进程上报给心跳服务时应使用该标识，用于进程下线时更新对应服务的状态
        self.id = id

        """ 
        rsocket------0
        socket-------1
        http---------2
        grpc---------3
        uc-----------10
        shareMemory--11
        """
        self.protocol = protocol

        """
        protobuf-----0
        jackson------1
        """
        self.formats = formats
        self.environment = environment

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class FitableForRegistryV1(object):

    def __init__(self, genericId: str, genericVersion: str, fitId: str, fitVersion: str):
        self.genericId = genericId
        self.genericVersion = genericVersion
        self.fitId = fitId
        self.fitVersion = fitVersion

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))


class ServiceMetaForRegistryV1(object):

    def __init__(self, fitable: FitableForRegistryV1, aliases: List[str], serviceName: str, pluginName: str):
        self.fitable = fitable
        self.aliases = aliases
        self.serviceName = serviceName
        self.pluginName = pluginName

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        return self.__dict__ == other.__dict__

    def __hash__(self):
        return hash(tuple(self.__dict__.values()))

    def __repr__(self):
        return str(tuple(self.__dict__.values()))
