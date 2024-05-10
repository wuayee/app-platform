/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides concrete fitable in specific application.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#ifndef FIT_REGISTRY_LISTENER_CONCRETE_FITABLE_HPP
#define FIT_REGISTRY_LISTENER_CONCRETE_FITABLE_HPP

#include <cstdint>
#include <fit/stl/vector.hpp>
#include <fit/memory/fit_base.hpp>
#include "../registry_listener_types.hpp"
#include "../registry_listener_element.hpp"

namespace Fit {
namespace Registry {
namespace Listener {
/**
 * 为服务实现提供在某个应用中的具体呈现。
 */
class ApplicationFitable : public std::enable_shared_from_this<ApplicationFitable>,
    public virtual RegistryListenerElement, public FitBase {
public:
    explicit ApplicationFitable(ApplicationPtr application, FitablePtr fitable);
    ~ApplicationFitable() override = default;
    RegistryListenerPtr GetRegistryListener() const override;

    /**
     * 获取提供服务实现的应用。
     *
     * @return 表示指向提供服务实现的应用的指针。
     */
    ApplicationPtr GetApplication() const;

    /**
     * 获取服务实现的原型。
     *
     * @return 表示指向服务实现的原型的指针。
     */
    FitablePtr GetFitable() const;

    /**
     * 获取在当前应用程序中支持的消息格式的列表。
     *
     * @return 表示消息格式的列表。
     */
    Fit::vector<int32_t> GetFormats() const;

    /**
     * 设置当前应用程序中支持的消息格式的列表。
     *
     * @param formats 表示消息格式的列表。
     */
    void SetFormats(Fit::vector<int32_t> formats);

    /**
     * 比较当前关联关系与另一个关联关系。
     *
     * @param another 表示指向与当前关联关系比较的另一个关联关系的指针。
     * @return 若当前关联关系大于另一个关联关系，则是一个正数；若小于另一个关联关系，则是一个负数；否则为 0。
     */
    int32_t Compare(const ApplicationFitablePtr& another) const;

    /**
     * 比较当前关联关系与另一个关联关系。
     *
     * @param application 表示指向与当前关联关系比较的另一个关联关系的应用程序的指针。
     * @param fitable 表示指向与当前关联关系比较的另一个关联关系的服务实现的指针。
     * @return 若当前关联关系大于另一个关联关系，则是一个正数；若小于另一个关联关系，则是一个负数；否则为 0。
     */
    int32_t Compare(const ApplicationPtr& application, const FitablePtr& fitable) const;
private:
    ApplicationPtr application_;
    FitablePtr fitable_;
    Fit::vector<int32_t> formats_ {};
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_CONCRETE_FITABLE_HPP
