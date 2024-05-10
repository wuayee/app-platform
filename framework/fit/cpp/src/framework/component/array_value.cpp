/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/8
 * Notes:       :
 */

#include <fit/value.hpp>
#include <fit/stl/memory.hpp>
#include <memory>

namespace Fit {
class ConstValueIterator::Impl {
public:
    using Iterator = Fit::vector<Value>::const_iterator;
    Impl() = default;
    ~Impl() = default;

    explicit Impl(const Iterator &current) : current_(current) {}

    Iterator current_;
};

ConstValueIterator::ConstValueIterator() : impl_(make_unique<Impl>()) {}

ConstValueIterator::ConstValueIterator(std::unique_ptr<Impl> impl) : impl_(std::move(impl)) {}

ConstValueIterator::~ConstValueIterator() = default;

const Value &ConstValueIterator::operator*() const
{
    return (*impl_->current_);
}

const Value *ConstValueIterator::operator->() const
{
    return &(*impl_->current_);
}

ConstValueIterator &ConstValueIterator::operator++()
{
    ++impl_->current_;
    return *this;
}

ConstValueIterator &ConstValueIterator::operator--()
{
    --(impl_->current_);
    return *this;
}

ConstValueIterator ConstValueIterator::operator++(int)
{
    auto old = impl_->current_;
    ++(impl_->current_);
    return ConstValueIterator(make_unique<ConstValueIterator::Impl>(old));
}

ConstValueIterator ConstValueIterator::operator--(int)
{
    auto old = impl_->current_;
    --(impl_->current_);
    return ConstValueIterator(make_unique<ConstValueIterator::Impl>(old));
}

bool ConstValueIterator::operator==(const ConstValueIterator &other) const noexcept
{
    return impl_->current_ == other.impl_->current_;
}

bool ConstValueIterator::operator!=(const ConstValueIterator &other) const noexcept
{
    return impl_->current_ != other.impl_->current_;
}

ConstValueIterator::ConstValueIterator(const ConstValueIterator &other)
    : impl_(make_unique<ConstValueIterator::Impl>(other.impl_->current_)) {}

ConstValueIterator &ConstValueIterator::operator=(const Fit::ConstValueIterator &other)
{
    impl_->current_ = other.impl_->current_;
    return *this;
}

class ValueIterator::Impl {
public:
    using Iterator = Fit::vector<Value>::iterator;
    Impl() = default;
    ~Impl() = default;

    explicit Impl(const Iterator &current) : current_(current) {}

    Iterator current_;
};

ValueIterator::ValueIterator() : impl_(make_unique<Impl>()) {}

ValueIterator::ValueIterator(std::unique_ptr<Impl> impl) : impl_(std::move(impl)) {}

ValueIterator::~ValueIterator() = default;

Value &ValueIterator::operator*() const
{
    return (*impl_->current_);
}

Value *ValueIterator::operator->() const
{
    return &(*impl_->current_);
}

ValueIterator &ValueIterator::operator++()
{
    ++impl_->current_;
    return *this;
}

ValueIterator &ValueIterator::operator--()
{
    --(impl_->current_);
    return *this;
}

ValueIterator ValueIterator::operator++(int)
{
    auto old = impl_->current_;
    ++(impl_->current_);
    return ValueIterator(make_unique<ValueIterator::Impl>(old));
}

ValueIterator ValueIterator::operator--(int)
{
    auto old = impl_->current_;
    --(impl_->current_);
    return ValueIterator(make_unique<ValueIterator::Impl>(old));
}

bool ValueIterator::operator==(const ValueIterator &other) const noexcept
{
    return impl_->current_ == other.impl_->current_;
}

bool ValueIterator::operator!=(const ValueIterator &other) const noexcept
{
    return impl_->current_ != other.impl_->current_;
}

ValueIterator::ValueIterator(const ValueIterator &other)
    : impl_(make_unique<ValueIterator::Impl>(other.impl_->current_)) {}

ValueIterator &ValueIterator::operator=(const Fit::ValueIterator &other)
{
    impl_->current_ = other.impl_->current_;
    return *this;
}

class ArrayValue::Impl {
public:
    Impl() = default;
    ~Impl() = default;

    /**
     * get Value with index
     * if index is over range in object, throw std::out_of_range
     * @param index value index
     * @return value
     */
    Value &operator[](uint32_t index)
    {
        if (index >= Size()) {
            throw std::out_of_range("over array size"); // LCOV_EXCL_LINE
        }

        return values_[index];
    }

    /**
     * @return value count
     */
    uint32_t Size() const
    {
        return values_.size();
    }

    /**
     * release all values
     */
    void Clear()
    {
        values_.clear();
    }

    /**
     * prepare capacity to store values
     * @param size the capacity that the array at least need to have
     * @return self
     */
    void Reserve(uint32_t size)
    {
        values_.reserve(size);
    }

    /**
     * add a null type value to array
     * @return added value
     */
    Value &PushBack()
    {
        values_.emplace_back(Value()); // LCOV_EXCL_LINE

        return values_.back();
    }

    /**
     * remove the back value
     * @return self
     */
    void PopBack()
    {
        if (values_.empty()) {
            return;
        }
        values_.pop_back();
    }

    /**
     * remove the value in the index, if index is not exist, do nothing
     * @param index value index
     * @return self
     */
    void Remove(uint32_t index)
    {
        values_.erase(std::next(values_.begin(), index)); // LCOV_EXCL_LINE
    }

    void Remove(Iterator iter)
    {
        values_.erase(iter.impl_->current_); // LCOV_EXCL_LINE
    }

    ConstValueIterator Begin() const
    {
        return ConstValueIterator(
            make_unique<ConstValueIterator::Impl>(values_.begin())); // LCOV_EXCL_LINE
    }

    ConstValueIterator End() const
    {
        return ConstValueIterator(
            make_unique<ConstValueIterator::Impl>(values_.end())); // LCOV_EXCL_LINE
    }

    ValueIterator Begin()
    {
        return ValueIterator(
            make_unique<ValueIterator::Impl>(values_.begin())); // LCOV_EXCL_LINE
    }

    ValueIterator End()
    {
        return ValueIterator(
            make_unique<ValueIterator::Impl>(values_.end())); // LCOV_EXCL_LINE
    }

private:
    Fit::vector<Value> values_;
};

ArrayValue::ArrayValue() : impl_(make_unique<Impl>()) {}

ArrayValue::ArrayValue(const ArrayValue &other)
{
    if (other.impl_) {
        impl_ = make_unique<Impl>(*(other.impl_));
    }
}

ArrayValue::ArrayValue(ArrayValue &&other) noexcept
{
    impl_ = std::move(other.impl_);
}

ArrayValue &ArrayValue::operator=(const Fit::ArrayValue &other)
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

ArrayValue &ArrayValue::operator=(Fit::ArrayValue &&other) noexcept
{
    if (this == &other) {
        return *this;
    }

    impl_ = std::move(other.impl_);

    return *this;
}

ArrayValue::ArrayValue(std::initializer_list<Value> init)
    : impl_(make_unique<Impl>())
{
    for (auto &element : init) {
        PushBack() = element;
    }
}

ArrayValue::~ArrayValue()
{
    if (impl_) {
        impl_->Clear();
    }
}

Value &ArrayValue::operator[](uint32_t index)
{
    return (*impl_)[index];
}

const Value &ArrayValue::operator[](uint32_t index) const
{
    return (*impl_)[index];
}

uint32_t ArrayValue::Size() const
{
    return impl_->Size();
}

bool ArrayValue::Empty() const
{
    return Size() == 0;
}

void ArrayValue::Clear()
{
    impl_->Clear();
}

ArrayValue &ArrayValue::Reserve(uint32_t size)
{
    impl_->Reserve(size);
    return *this;
}

Value &ArrayValue::PushBack()
{
    return impl_->PushBack();
}

ArrayValue &ArrayValue::PushBack(bool value)
{
    impl_->PushBack().SetBool(value);
    return *this;
}

ArrayValue &ArrayValue::PushBack(int32_t value)
{
    impl_->PushBack().SetInt32(value);
    return *this;
}

ArrayValue &ArrayValue::PushBack(uint32_t value)
{
    impl_->PushBack().SetUInt32(value);
    return *this;
}

ArrayValue &ArrayValue::PushBack(double value)
{
    impl_->PushBack().SetDouble(value);
    return *this;
}

ArrayValue &ArrayValue::PushBack(const char *value)
{
    impl_->PushBack().SetString(value);
    return *this;
}

ArrayValue &ArrayValue::PopBack()
{
    impl_->PopBack();
    return *this;
}

ArrayValue &ArrayValue::Remove(uint32_t index)
{
    impl_->Remove(index);
    return *this;
}

ArrayValue &ArrayValue::Remove(ArrayValue::Iterator iter)
{
    impl_->Remove(iter);
    return *this;
}

ConstValueIterator ArrayValue::Begin() const
{
    return ((const Impl &)*impl_).Begin();
}

ConstValueIterator ArrayValue::End() const
{
    return ((const Impl &)*impl_).End();
}

ValueIterator ArrayValue::Begin()
{
    return impl_->Begin();
}

ValueIterator ArrayValue::End()
{
    return impl_->End();
}
}