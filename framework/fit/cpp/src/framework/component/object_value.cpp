/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/8
 * Notes:       :
 */

#include <fit/value.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/memory.hpp>
#include <memory>

namespace Fit {
class ConstMemberIterator::Impl {
public:
    using Iterator = Fit::map<Fit::string, Value>::const_iterator;
    Impl() = default;
    ~Impl() = default;

    explicit Impl(const Iterator &current) : current_(current) {}

    Iterator current_ {};
    ConstMember value_ {};
};

ConstMemberIterator::ConstMemberIterator() : impl_(make_unique<Impl>()) {}

ConstMemberIterator::ConstMemberIterator(std::unique_ptr<Impl> impl) : impl_(std::move(impl)) {}

ConstMemberIterator::~ConstMemberIterator() {}

const ConstMember &ConstMemberIterator::operator*() const
{
    impl_->value_ = {impl_->current_->first.c_str(), &impl_->current_->second};
    return impl_->value_;
}

const ConstMember *ConstMemberIterator::operator->() const
{
    impl_->value_ = {impl_->current_->first.c_str(), &impl_->current_->second};
    return &impl_->value_;
}

ConstMemberIterator &ConstMemberIterator::operator++()
{
    ++impl_->current_;
    return *this;
}

ConstMemberIterator &ConstMemberIterator::operator--()
{
    --(impl_->current_);
    return *this;
}

ConstMemberIterator ConstMemberIterator::operator++(int)
{
    auto old = impl_->current_;
    ++(impl_->current_);
    return ConstMemberIterator(make_unique<ConstMemberIterator::Impl>(old));
}

ConstMemberIterator ConstMemberIterator::operator--(int)
{
    auto old = impl_->current_;
    --(impl_->current_);
    return ConstMemberIterator(make_unique<ConstMemberIterator::Impl>(old));
}

bool ConstMemberIterator::operator==(const ConstMemberIterator &other) const noexcept
{
    return impl_->current_ == other.impl_->current_;
}

bool ConstMemberIterator::operator!=(const ConstMemberIterator &other) const noexcept
{
    return impl_->current_ != other.impl_->current_;
}

ConstMemberIterator::ConstMemberIterator(const ConstMemberIterator &other)
    : impl_(make_unique<ConstMemberIterator::Impl>(other.impl_->current_)) {}

ConstMemberIterator &ConstMemberIterator::operator=(const Fit::ConstMemberIterator &other)
{
    impl_->current_ = other.impl_->current_;
    return *this;
}

class MemberIterator::Impl {
public:
    using Iterator = Fit::map<Fit::string, Value>::iterator;
    Impl() = default;
    ~Impl() = default;

    explicit Impl(const Iterator &current) : current_(current) {}

    Iterator current_ {};
    Member value_ {};
};

MemberIterator::MemberIterator() : impl_(make_unique<Impl>()) {}

MemberIterator::MemberIterator(std::unique_ptr<Impl> impl) : impl_(std::move(impl)) {}

MemberIterator::~MemberIterator() {}

Member &MemberIterator::operator*()
{
    impl_->value_ = {impl_->current_->first.c_str(), &impl_->current_->second};
    return impl_->value_;
}

Member *MemberIterator::operator->()
{
    impl_->value_ = {impl_->current_->first.c_str(), &impl_->current_->second};
    return &impl_->value_;
}

MemberIterator &MemberIterator::operator++()
{
    ++impl_->current_;
    return *this;
}

MemberIterator &MemberIterator::operator--()
{
    --(impl_->current_);
    return *this;
}

MemberIterator MemberIterator::operator++(int)
{
    auto old = impl_->current_;
    ++(impl_->current_);
    return MemberIterator(make_unique<MemberIterator::Impl>(old));
}

MemberIterator MemberIterator::operator--(int)
{
    auto old = impl_->current_;
    --(impl_->current_);
    return MemberIterator(make_unique<MemberIterator::Impl>(old));
}

bool MemberIterator::operator==(const MemberIterator &other) const noexcept
{
    return impl_->current_ == other.impl_->current_;
}

bool MemberIterator::operator!=(const MemberIterator &other) const noexcept
{
    return impl_->current_ != other.impl_->current_;
}

MemberIterator::MemberIterator(const MemberIterator &other)
    : impl_(make_unique<MemberIterator::Impl>(other.impl_->current_)) {}

MemberIterator &MemberIterator::operator=(const Fit::MemberIterator &other)
{
    impl_->current_ = other.impl_->current_;
    return *this;
}

class ObjectValue::Impl {
public:
    Impl() = default;
    ~Impl() = default;

    /**
     * get member Value with name
     * if name is not found in object, then is is added to the object with null type value
     * @param name of member
     * @return if not exist, return nullptr
     */
    Value &operator[](const char *name)
    {
        auto iter = values_.find(name); // LCOV_EXCL_LINE
        if (iter == values_.end()) {
            iter = values_.insert(std::make_pair(name, Value())).first; // LCOV_EXCL_LINE
        }

        return iter->second;
    }

    /**
     * @return member count
     */
    uint32_t Size() const
    {
        return values_.size();
    }

    /**
     * @return Size() == 0
     */
    bool Empty() const
    {
        return Size() == 0;
    }

    /**
     * release all member
     */
    void Clear()
    {
        values_.clear();
    }

    /**
     * add a member with name, return null type value
     * if the name is exist, will return the exist value and set with null type
     * @param name name of member
     * @return the member value for name
     */
    Value &Add(const char *name)
    {
        return (*this)[name].SetNull();
    }

    /**
     * remove name of member
     * @param name name of member
     * @return
     */
    void Remove(const char *name)
    {
        values_.erase(name); // LCOV_EXCL_LINE
    }

    void Remove(Iterator iter)
    {
        values_.erase(iter.impl_->current_);
    }

    ConstIterator Find(const char *name) const
    {
        return ConstIterator(
            make_unique<ConstIterator::Impl>(values_.find(name))); // LCOV_EXCL_LINE
    }

    Iterator Find(const char *name)
    {
        return Iterator(
            make_unique<Iterator::Impl>(values_.find(name))); // LCOV_EXCL_LINE
    }

    /**
     * get all member names, if empty, return an empty list
     * @return all member names
     */
    Fit::vector<Fit::string> GetNames() const
    {
        Fit::vector<Fit::string> result;
        result.reserve(values_.size()); // LCOV_EXCL_LINE
        for (const auto &node : values_) {
            result.emplace_back(node.first); // LCOV_EXCL_LINE
        }

        return result;
    }

    ConstIterator Begin() const
    {
        return ConstIterator(
            make_unique<ConstIterator::Impl>(values_.begin())); // LCOV_EXCL_LINE
    }

    ConstIterator End() const
    {
        return ConstIterator(
            make_unique<ConstIterator::Impl>(values_.end())); // LCOV_EXCL_LINE
    }

    Iterator Begin()
    {
        return Iterator(
            make_unique<Iterator::Impl>(values_.begin())); // LCOV_EXCL_LINE
    }

    Iterator End()
    {
        return Iterator(
            make_unique<Iterator::Impl>(values_.end())); // LCOV_EXCL_LINE
    }

private:
    Fit::map<Fit::string, Value> values_;
};

ObjectValue::ObjectValue() : impl_(make_unique<Impl>()) {}

ObjectValue::ObjectValue(const ObjectValue &other)
{
    if (other.impl_) {
        impl_ = make_unique<Impl>(*other.impl_);
    }
}

ObjectValue::ObjectValue(ObjectValue &&other) noexcept
{
    impl_ = std::move(other.impl_);
}

ObjectValue &ObjectValue::operator=(const Fit::ObjectValue &other)
{
    if (this == &other) {
        return *this;
    }
    if (!other.impl_) {
        impl_.reset();
        return *this;
    }

    *impl_ = *other.impl_;

    return *this;
}

ObjectValue &ObjectValue::operator=(Fit::ObjectValue &&other) noexcept
{
    if (this == &other) {
        return *this;
    }

    impl_ = std::move(other.impl_);

    return *this;
}

ObjectValue::ObjectValue(std::initializer_list<Value> init)
    : impl_(make_unique<Impl>())
{
    if (init.size() == 2 && init.begin()->IsString()) { // 2 is size
        Add(std::begin(init)->AsString()) = *(std::next(init.begin()));
        return;
    }
    for (const auto &element : init) {
        if (!element.IsArray() || element.AsArray().Size() != 2 || !element.AsArray()[0].IsString()) {
            throw std::logic_error("cann't as a object");
        }
    }

    for (auto &element : init) {
        Add(element.AsArray()[0].AsString()) = element.AsArray()[1];
    }
}

ObjectValue::~ObjectValue()
{
    if (impl_) {
        impl_->Clear();
    }
}

Value &ObjectValue::operator[](const char *name)
{
    return (*impl_)[name];
}

const Value &ObjectValue::operator[](const char *name) const
{
    return (*impl_)[name];
}

uint32_t ObjectValue::Size() const
{
    return impl_->Size();
}

bool ObjectValue::Empty() const
{
    return impl_->Empty();
}

void ObjectValue::Clear()
{
    impl_->Clear();
}

Value &ObjectValue::Add(const char *name)
{
    return impl_->Add(name);
}

ObjectValue &ObjectValue::Add(const char *name, bool value)
{
    impl_->Add(name).SetBool(value);

    return *this;
}

ObjectValue &ObjectValue::Add(const char *name, int32_t value)
{
    impl_->Add(name).SetInt32(value);

    return *this;
}

ObjectValue &ObjectValue::Add(const char *name, uint32_t value)
{
    impl_->Add(name).SetUInt32(value);

    return *this;
}

ObjectValue &ObjectValue::Add(const char *name, double value)
{
    impl_->Add(name).SetDouble(value);

    return *this;
}

ObjectValue &ObjectValue::Add(const char *name, const char *value)
{
    impl_->Add(name).SetString(value);

    return *this;
}

ObjectValue &ObjectValue::Remove(const char *name)
{
    impl_->Remove(name);

    return *this;
}

ObjectValue &ObjectValue::Remove(Iterator iter)
{
    impl_->Remove(iter);

    return *this;
}

bool ObjectValue::Exist(const char *name) const
{
    return Find(name) != End();
}

Fit::vector<Fit::string> ObjectValue::GetNames() const
{
    return impl_->GetNames();
}

ObjectValue::ConstIterator ObjectValue::Find(const char *name) const
{
    return ((const Impl &)*impl_).Find(name);
}

ObjectValue::Iterator ObjectValue::Find(const char *name)
{
    return impl_->Find(name);
}

ObjectValue::ConstIterator ObjectValue::Begin() const
{
    return ((const Impl &)*impl_).Begin();
}

ObjectValue::ConstIterator ObjectValue::End() const
{
    return ((const Impl &)*impl_).End();
}

ObjectValue::Iterator ObjectValue::Begin()
{
    return impl_->Begin();
}

ObjectValue::Iterator ObjectValue::End()
{
    return impl_->End();
}
}