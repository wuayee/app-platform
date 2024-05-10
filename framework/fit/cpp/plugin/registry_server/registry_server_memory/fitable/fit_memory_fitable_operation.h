/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/12/03
 * Notes:       :
 */
#ifndef FIT_MEMORY_FITABLE_OPERATION_H
#define FIT_MEMORY_FITABLE_OPERATION_H
#include <cstdint>
#include <fit/internal/registry/fit_registry_entities.h>
#include <fit/stl/mutex.hpp>
#include <fit/stl/memory.hpp>
namespace Fit {
namespace Registry {
class FitMemoryFitableOperation {
public:
    using FitableMetaPtr = RegistryInfo::FitableMetaPtr;
    using FitableMetaPtrSet = Fit::vector<std::shared_ptr<Fit::RegistryInfo::FitableMeta>>;
    using FitableMetaIndexByFitable = Fit::unordered_map<Fit::RegistryInfo::Fitable,
        std::weak_ptr<Fit::RegistryInfo::FitableMeta>,
        Fit::RegistryInfo::FitableHash, Fit::RegistryInfo::FitableEqual>;
    using FitableMetaSetIndexByApplication = Fit::unordered_map<Fit::RegistryInfo::Application,
        FitableMetaIndexByFitable,
        Fit::RegistryInfo::ApplicationHash, Fit::RegistryInfo::ApplicationEqual>;

    using FitableMetaIndexByApplication = Fit::unordered_map<Fit::RegistryInfo::Application,
        std::shared_ptr<Fit::RegistryInfo::FitableMeta>,
        Fit::RegistryInfo::ApplicationHash, Fit::RegistryInfo::ApplicationEqual>;

    using FitableMetaSetIndexByFitable = Fit::unordered_map<Fit::RegistryInfo::Fitable,
        FitableMetaIndexByApplication,
        Fit::RegistryInfo::FitableHash, Fit::RegistryInfo::FitableEqual>;

    using FitableMetaSetIndexByGenericable = unordered_map<string,
        unordered_set<RegistryInfo::FitableMetaPtr,
        RegistryInfo::FitableMetaSharedPtrHash, RegistryInfo::FitableMetaSharedPtrEq>>;

public:
    int32_t Save(std::shared_ptr<Fit::RegistryInfo::FitableMeta> fitableMetaPtr);
    FitableMetaPtrSet Query(const Fit::RegistryInfo::Fitable& fitable);
    std::shared_ptr<Fit::RegistryInfo::FitableMeta> Query(
        const Fit::RegistryInfo::FitableMeta& fitableMeta);
    FitableMetaPtrSet Query(const string& genericId) const;
    FitableMetaPtrSet Query(const Fit::RegistryInfo::Application& application);
    int32_t Remove(const Fit::RegistryInfo::Application& application);
    bool IsApplicationExist(const Fit::RegistryInfo::Application& application);
    static Fit::shared_ptr<FitMemoryFitableOperation> Create();
protected:
    /**
     * @brief 内部更新genericable索引使用，无锁
     *
     * @param original 前一个版本的数据
     * @param current 当前要更新的数据
     */
    void UpdateGenericableIndex(const FitableMetaPtr& original, const FitableMetaPtr& current);
    /**
     * @brief 清理genericable索引中的FitableMeta关联，无锁
     *
     * @param target 目标对象
     */
    void RemoveGenericableIndex(const FitableMetaPtr& target);
    /**
     * @brief 清理fitable索引中和FitableMeta的关联，无锁
     *
     * @param target 目标对象
     */
    void RemoveFitableIndex(const FitableMetaPtr& target);

private:
    mutable Fit::mutex fitableMetaSetIndexByApplicationMutex_;
    FitableMetaSetIndexByApplication fitableMetaSetIndexByApplication_;
    mutable Fit::mutex fitableMetaSetIndexByFitableMutex_;
    FitableMetaSetIndexByFitable fitableMetaSetIndexByFitable_;
    mutable Fit::mutex fitableMetaSetIndexByGenericableMutex_;
    FitableMetaSetIndexByGenericable fitableMetaSetIndexByGenericable_;
};
}
}

#endif