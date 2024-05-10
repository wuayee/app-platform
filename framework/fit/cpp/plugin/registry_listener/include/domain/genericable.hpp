/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides definition for genericable.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/08
 */

#ifndef FIT_REGISTRY_LISTENER_GENERICABLE_HPP
#define FIT_REGISTRY_LISTENER_GENERICABLE_HPP

#include <cstdint>
#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"
#include "../repo/fitable_repo.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为泛化服务提供定义。
 * <p>监听程序围绕服务实现展开，泛化服务的定义本质上为服务实现提供分组，并将公共信息提取出来以降低内存开销。<br />
 * 其在第一个泛化服务产生时被创建，在最后一个服务实现被释放时自动销毁。</p>
 */
class Genericable : public std::enable_shared_from_this<Genericable>,
    public virtual RegistryListenerElement, public FitBase {
public:
    explicit Genericable(const GenericableRepoPtr& repo, Fit::string id, Fit::string version);
    ~Genericable() override = default;
    RegistryListenerPtr GetRegistryListener() const final;

    /**
     * 获取泛化服务的唯一标识。
     *
     * @return 表示泛化服务的唯一标识的字符串。
     */
    const Fit::string& GetId() const;

    /**
     * 获取泛化服务的版本。
     *
     * @return 表示泛化服务的版本的字符串。
     */
    const Fit::string& GetVersion() const;

    /**
     * 获取包含的服务实现的仓库。
     *
     * @return 表示指向服务实现仓库的指针。
     */
    FitableRepoPtr GetFitables();

    /**
     * 将当前泛化服务与指定泛化服务进行比较。
     *
     * @param another 表示指向待与当前泛化服务比较的另一个泛化服务的指针。
     * @return 若当前泛化服务比另一个大，则为一个正数；若比另一个小，则为一个负数；否则为 0。
     */
    int32_t Compare(const GenericablePtr& another) const;

    /**
     * 将当前泛化服务与指定的唯一标识和版本进行比较。
     *
     * @param id 表示待与当前泛化服务比较的唯一标识的字符串。
     * @param version 表示待与当前泛化服务比较的版本的字符串。
     * @return 若当前泛化服务比另一个大，则为一个正数；若比另一个小，则为一个负数；否则为 0。
     */
    int32_t Compare(const Fit::string& id, const Fit::string& version) const;
private:
    std::weak_ptr<GenericableRepo> repo_;
    Fit::string id_;
    Fit::string version_;
    FitableRepoPtr fitables_ {};
    Fit::mutex mutex_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_GENERICABLE_HPP
