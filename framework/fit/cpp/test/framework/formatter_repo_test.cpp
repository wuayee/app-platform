/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/2/21
 * Notes:       :
 */

#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <mock/formatter_repo_mock.hpp>
#include <src/framework/default_formatter_repo.hpp>

using namespace ::Fit;
using namespace ::Fit::Framework;
using namespace ::Fit::Framework::Formatter;
using namespace ::testing;

class FormatterRepoTest : public testing::Test {
public:
    void SetUp() override
    {
        validTarget_.genericId = "add";
        validTarget_.fitableType = Fit::Framework::Annotation::FitableType::MAIN;
        validTarget_.formats = {PROTOCOL_TYPE_PROTOBUF, PROTOCOL_TYPE_JSON};

        validJsonConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
        validJsonConverterMeta_->SetGenericId(validTarget_.genericId);
        validJsonConverterMeta_->SetFormat(PROTOCOL_TYPE_JSON);
        validProtobufConverterMeta_ = std::make_shared<Fit::Framework::Formatter::FormatterMeta>();
        validProtobufConverterMeta_->SetGenericId(validTarget_.genericId);
        validProtobufConverterMeta_->SetFormat(PROTOCOL_TYPE_PROTOBUF);

        repo_ = make_unique<DefaultFormatterRepo>();
        repo_->Add({validJsonConverterMeta_, validProtobufConverterMeta_});
    }

    void TearDown() override {}

protected:
    BaseSerialization validTarget_;
    std::unique_ptr<FormatterRepo> repo_;
    FormatterMetaPtr validJsonConverterMeta_;
    FormatterMetaPtr validProtobufConverterMeta_;
};

TEST_F(FormatterRepoTest, should_return_added_formatter_when_Get_given_exist_genericable_id_and_type)
{
    auto result = repo_->Get(validTarget_);
    ASSERT_THAT(result, NotNull());
    EXPECT_THAT(result->GetGenericId(), ::testing::Eq(validTarget_.genericId));
}

TEST_F(FormatterRepoTest, should_return_null_formatter_when_Get_given_after_remove_it)
{
    repo_->Remove({validJsonConverterMeta_, validProtobufConverterMeta_});
    auto result = repo_->Get(validTarget_);
    ASSERT_THAT(result, IsNull());
}

TEST_F(FormatterRepoTest, should_return_null_formatter_when_Get_given_not_matched_type)
{
    BaseSerialization notMatched;
    notMatched.genericId = "add";
    notMatched.fitableType = Fit::Framework::Annotation::FitableType::MAIN;
    notMatched.formats = {100};

    auto result = repo_->Get(notMatched);
    ASSERT_THAT(result, IsNull());
}

TEST_F(FormatterRepoTest, should_return_null_formatter_when_Get_given_after_clear)
{
    repo_->Clear();
    auto result = repo_->Get(validTarget_);
    ASSERT_THAT(result, IsNull());
}

TEST_F(FormatterRepoTest, should_return_support_types_when_GetFormats_given_exist_genericable_id)
{
    auto result = repo_->GetFormats(validTarget_.genericId);
    ASSERT_THAT(result.size(), Eq(2));
    EXPECT_THAT(result, Contains(validJsonConverterMeta_->GetFormat()));
    EXPECT_THAT(result, Contains(validProtobufConverterMeta_->GetFormat()));
}
