/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for fitable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/08
 */

#ifndef FIT_REGISTRY_LISTENER_FITABLE_HPP
#define FIT_REGISTRY_LISTENER_FITABLE_HPP

#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../repo/fitable_application_repo.hpp"
#include "../repo/fitable_unavailable_endpoint_repo.hpp"
namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务实现提供定义。
 * <p>服务实现的生命周期受且仅受监听程序的外部调用影响。当使用方订阅服务实现变更时产生，当适用方注销服务实现变更时销毁。</p>
 */
class Fitable : public std::enable_shared_from_this<Fitable>, public virtual RegistryListenerElement, public FitBase {
public:
    explicit Fitable(const FitableRepoPtr& repo, Fit::string id, Fit::string version);
    ~Fitable() override = default;
    RegistryListenerPtr GetRegistryListener() const final;

    /**
     * 获取服务实现所属的仓库。
     *
     * @return 表示指向所属仓库的指针。
     */
    FitableRepoPtr GetRepo() const;

    /**
     * 获取所实现的泛化服务的信息。
     *
     * @return 表示指向所实现的泛化服务的信息的共享指针。
     */
    GenericablePtr GetGenericable() const;

    /**
     * 获取服务实现的唯一标识。
     *
     * @return 表示泛化服务唯一标识的字符串。
     */
    const Fit::string& GetId() const;

    /**
     * 获取服务实现的版本。
     *
     * @return 表示服务实现的版本的字符串。
     */
    const Fit::string& GetVersion() const;

    /**
     * 比较当前服务实现的实例与给定的另一个服务实现的实例。
     *
     * @param another 表示指向待与当前服务实现比较的另一个服务实现的指针。
     * @return 若当前服务实现大于另一个服务实现，则为一个正数；若小于另一个服务实现，则是一个负数；否则为 0。
     */
    int32_t Compare(const FitablePtr& another) const;

    /**
     * 比较当前服务实现与目标的服务实现的唯一标识和版本。
     *
     * @param id 表示待与当前服务实现比较的另一个服务实现的唯一标识的字符串。
     * @param version 表示待与当前服务实现比较的另一个服务实现的版本的字符串。
     * @return 若当前服务实现大于另一个服务实现，则为一个正数；若小于另一个服务实现，则是一个负数；否则为 0。
     */
    int32_t Compare(const Fit::string& id, const Fit::string& version) const;

    /**
     * 获取与当前服务实现关联的应用程序的仓库。
     *
     * @return 表示指向应用程序仓库的指针。
     */
    FitableApplicationRepoPtr GetApplications();

    /**
     * 获取当前服务实现中不可用的地址的仓库。
     *
     * @return 表示指向不可用地址仓库的指针。
     */
    FitableUnavailableEndpointRepoPtr GetUnavailableEndpoints();

    /**
     * 返回一个字符串，用以描述当前的服务实现信息。
     *
     * @return 表示用以描述服务实现信息的字符串。
     */
    ::Fit::string ToString() const;

    /**
     * 移除当前服务实现。
     */
    void Remove();
private:
    std::weak_ptr<FitableRepo> repo_;
    Fit::string id_;
    Fit::string version_;
    FitableApplicationRepoPtr applications_ {};
    FitableUnavailableEndpointRepoPtr unavailableEndpoints_ {};
    Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_FITABLE_HPP
