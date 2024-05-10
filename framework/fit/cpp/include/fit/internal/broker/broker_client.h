
/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/05/18 17:04
 * Notes        :
 */
#ifndef IBROKER_CLIENT_H
#define IBROKER_CLIENT_H

#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/any.hpp>
#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/framework/entity.hpp>
#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <broker/client/domain/fitable_invoker_factory.hpp>
#include <fit/internal/runtime/runtime_element.hpp>

namespace Fit {
class IBrokerClient : public RuntimeElementBase {
public:
    explicit IBrokerClient(Runtime* runtime) : RuntimeElementBase(runtime, "brokerClient") {};
    ~IBrokerClient() override = default;

    virtual int32_t GenericableInvoke(ContextObj context,
        const Fit::string& genericableId,
        Fit::vector<Fit::any>& in, Fit::vector<Fit::any>& out) = 0;

    virtual int32_t ServiceInvoker(
        const Framework::ServiceAddress &service,
        ContextObj context,
        Fit::vector<Fit::any>& in,
        Fit::vector<Fit::any>& out) = 0;

    virtual int32_t LocalInvoke(ContextObj context,
        const fit::registry::Fitable  &fitable,
        Fit::vector<Fit::any>& in, Fit::vector<Fit::any>& out,
        Fit::Framework::Annotation::FitableType fitableType) = 0;

    virtual const ::Fit::Framework::Formatter::FormatterServicePtr& GetFormatterService() const = 0;
    virtual const BrokerFitableDiscoveryPtr& GetFitableDiscovery() const = 0;
};
using BrokerClientPtr = std::shared_ptr<Fit::IBrokerClient>;
}
#endif