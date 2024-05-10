/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides endpoint information for load balance.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#ifndef FIT_LOAD_BALANCE_FITABLE_ENDPOINT_HPP
#define FIT_LOAD_BALANCE_FITABLE_ENDPOINT_HPP

#include <fit/external/util/context/context_api.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>

namespace Fit {
namespace LoadBalance {
using Address = ::fit::hakuna::kernel::registry::shared::Address;
using Application = ::fit::hakuna::kernel::registry::shared::Application;
using ApplicationInstance = ::fit::hakuna::kernel::registry::shared::ApplicationInstance;
using Endpoint = ::fit::hakuna::kernel::registry::shared::Endpoint;
using Fitable = ::fit::hakuna::kernel::shared::Fitable;
using Worker = ::fit::hakuna::kernel::registry::shared::Worker;

class FitableEndpoint {
public:
    explicit FitableEndpoint(const Endpoint* endpoint, const Address* address, const Worker* worker,
        const ApplicationInstance* application);
    ~FitableEndpoint() = default;

    /**
     * 使用当前的服务实现网络端点信息在指定的上下文中生成应用程序实例。
     *
     * @param context 表示用以生成结果的上下文。
     * @return 表示通过当前信息生成的应用程序实例信息。
     */
    ApplicationInstance* CreateApplicationInstance(ContextObj context);

    /**
     * 将当前的终结点信息与另一个进行比较。
     *
     * @param another 表示待与当前网络终结点进行比较的另一个网络终结点信息的引用。
     * @return 若当前网络终结点大于另一个，则是一个正数；若小于另一个，则是一个负数；否则为 0。
     */
    int32_t Compare(const FitableEndpoint& another) const;

    /**
     * 获取网络终结点信息。
     *
     * @return 表示指向网络终结点信息的指针。
     */
    const Endpoint* GetEndpoint() const;

    /**
     * 获取地址信息。
     *
     * @return 表示指向地址信息的指针。
     */
    const Address* GetAddress() const;

    /**
     * 获取工作进程信息。
     *
     * @return 表示指向工作进程信息的指针。
     */
    const Worker* GetWorker() const;

    /**
     * 获取应用程序实例信息。
     *
     * @return 表示指向应用程序实例信息的指针。
     */
    const ApplicationInstance* GetApplicationInstance() const;

    /**
     * 获取应用程序信息。
     *
     * @return 表示指向应用程序信息的指针。
     */
    const Application* GetApplication() const;

    /**
     * 将指定的应用程序实例所包含的信息展开成泛化服务的网络终结点信息的集合。
     *
     * @param application 表示待展开的应用程序实例信息。
     * @return 表示展开后得到的网络终结点信息的集合。
     */
    static vector<FitableEndpoint> Flat(const ApplicationInstance& application);

    /**
     * 将指定的应用程序实例所包含的信息展开成泛化服务的网络终结点信息的集合。
     *
     * @param applications 表示待展开的应用程序实例信息的集合。
     * @return 表示展开后得到的网络终结点信息的集合。
     */
    static vector<FitableEndpoint> Flat(const vector<ApplicationInstance>& applications);

    /**
     * 聚合指定的服务终结点。
     *
     * @param context
     * @param endpoints
     * @return
     */
    static vector<ApplicationInstance>* Aggregate(ContextObj context, const vector<FitableEndpoint>& endpoints);
private:
    /**
     * 展开指定的应用程序实例信息，并将得到的结果填充到服务实现网络终结点的集合中。
     *
     * @param endpoints 表示待接收展开得到的终结点信息的集合。
     * @param application 表示待展开的应用程序实例信息。
     */
    static void Fill(vector<FitableEndpoint>& endpoints, const ApplicationInstance& application);
    static ApplicationInstance* GetOrCreate(vector<ApplicationInstance>& instances, ContextObj context,
        const ApplicationInstance* current);
    static Address* GetOrCreate(vector<Address>& addresses, ContextObj context, const Address* current);
    static Endpoint* GetOrCreate(vector<Endpoint>& endpoints, ContextObj context, const Endpoint* current);
    static Worker* GetOrCreate(vector<Worker>& workers, ContextObj context, const Worker* current);
    const Endpoint* endpoint_;
    const Address* address_;
    const Worker* worker_;
    const ApplicationInstance* application_;
};
}
}

#endif // FIT_LOAD_BALANCE_FITABLE_ENDPOINT_HPP
