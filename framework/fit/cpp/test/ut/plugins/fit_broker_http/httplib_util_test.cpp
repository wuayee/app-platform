/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : test for fit httplib util
 * Author       : songyongtan
 * Create       : 2023-08-03
 * Notes:       :
 */

#include <gtest/gtest.h>
#include <regex>
#include "fit_broker_http/httplib_util.hpp"
#include "fit_broker_http/http_util.hpp"

using namespace Fit;

class FitBrokerHttplibUtilTest : public ::testing::Test {
public:
    void SetUp() override
    {
        accessToken_ = "925a55c673f3487b9e7117243d1ec223";
    }

    void TearDown() override
    {
    }
public:
    string accessToken_ {};
};

TEST_F(FitBrokerHttplibUtilTest, should_return_ok_when_GetRequest_given_correct_request)
{
    auto meta = fit_meta_data(3, 1, fit_version(1, 0, 1), "9289a2a4322d47d38f33fc32c47f04d2", "fitableId",
        accessToken_);
    string expectPayloadBytes = "body";

    httplib::Request httpReq;
    std::regex reg(R"(/fit/(.*))");
    std::string path = "/fit/9289a2a4322d47d38f33fc32c47f04d2/fitableId";
    std::regex_match(path, httpReq.matches, reg);
    httpReq.set_header(HEADER_FIT_DATA_FORMAT, std::to_string(meta.get_payload_format()));
    httpReq.set_header(HEADER_FIT_VERSION, std::to_string(meta.get_version()));
    httpReq.set_header(HEADER_FIT_GENERICABLE_VERSION, to_std_string(meta.get_generic_version().to_string()));
    httpReq.set_header(HEADER_FIT_ACCESS_TOKEN, to_std_string(meta.get_access_token()));
    httpReq.body = "body";

    Network::Request req {};
    auto ret = HttplibUtil::GetRequest(httpReq, req);

    ASSERT_EQ(ret.status, HTTP_STATUS_OK);
    EXPECT_EQ(req.payload, expectPayloadBytes);
}

TEST_F(FitBrokerHttplibUtilTest, should_return_illegal_fit_path_when_GetRequest_given_large_path_layer)
{
    httplib::Request httpReq;
    std::regex reg(R"(/fit/(.*))");
    std::string path = "/fit/9289a2a4322d47d38f33fc32c47f04d2/fitableId/xxxx";
    std::regex_match(path, httpReq.matches, reg);

    Network::Request req {};
    auto ret = HttplibUtil::GetRequest(httpReq, req);

    ASSERT_EQ(ret.status, HTTP_STATUS_BAT_REQUEST);
    ASSERT_EQ(ret.msg, "Illegal fit path");
}

TEST_F(FitBrokerHttplibUtilTest, should_return_illegal_fit_path_when_GetRequest_given_empty_match_size)
{
    httplib::Request httpReq;
    Network::Request req {};
    auto ret = HttplibUtil::GetRequest(httpReq, req);

    ASSERT_EQ(ret.status, HTTP_STATUS_BAT_REQUEST);
    ASSERT_EQ(ret.msg, "Illegal fit path");
}

TEST_F(FitBrokerHttplibUtilTest, should_return_no_format_header_when_GetRequest_given_no_format_header)
{
    httplib::Request httpReq;
    Network::Request req {};
    std::regex reg(R"(/fit/(.*))");
    std::string path = "/fit/9289a2a4322d47d38f33fc32c47f04d2/fitableId";
    std::regex_match(path, httpReq.matches, reg);

    auto ret = HttplibUtil::GetRequest(httpReq, req);

    ASSERT_EQ(ret.status, HTTP_STATUS_BAT_REQUEST);
    ASSERT_EQ(ret.msg, "No specified FIT-Data-Format");
}

TEST_F(FitBrokerHttplibUtilTest, should_return_ok_when_GetRequest_given_no_version_header)
{
    httplib::Request httpReq;
    Network::Request req {};
    std::regex reg(R"(/fit/(.*))");
    std::string path = "/fit/9289a2a4322d47d38f33fc32c47f04d2/fitableId";
    std::regex_match(path, httpReq.matches, reg);
    httpReq.set_header(HEADER_FIT_DATA_FORMAT, "1");
    httpReq.set_header(HEADER_FIT_GENERICABLE_VERSION, "1.0.0");
    httpReq.set_header(HEADER_FIT_ACCESS_TOKEN, to_std_string(accessToken_));

    auto ret = HttplibUtil::GetRequest(httpReq, req);

    ASSERT_EQ(ret.status, HTTP_STATUS_OK);
}

TEST_F(FitBrokerHttplibUtilTest, should_return_ok_when_GetRequest_given_no_genericable_version_header)
{
    httplib::Request httpReq;
    Network::Request req {};
    std::regex reg(R"(/fit/(.*))");
    std::string path = "/fit/9289a2a4322d47d38f33fc32c47f04d2/fitableId";
    std::regex_match(path, httpReq.matches, reg);
    httpReq.set_header(HEADER_FIT_DATA_FORMAT, "1");
    httpReq.set_header(HEADER_FIT_VERSION, "3");
    httpReq.set_header(HEADER_FIT_ACCESS_TOKEN, to_std_string(accessToken_));

    auto ret = HttplibUtil::GetRequest(httpReq, req);

    ASSERT_EQ(ret.status, HTTP_STATUS_OK);
}

TEST_F(FitBrokerHttplibUtilTest, should_return_msg_when_BuildExceptionResponse_given_params)
{
    httplib::Request httpReq;
    httpReq.path = "/fit/xx/xx";
    string expectMsg = R"({"error": "error","path":"/fit/xx/xx","status":200,"suppressed":null})";

    auto ret = HttplibUtil::BuildExceptionResponse(HTTP_STATUS_OK, "error", httpReq);

    ASSERT_EQ(ret, expectMsg);
}

TEST_F(FitBrokerHttplibUtilTest, should_return_correct_headers_when_BuildRequestHeaders_given_req_meta)
{
    auto meta = fit_meta_data(3, 1, fit_version(1, 0, 1), "9289a2a4322d47d38f33fc32c47f04d2", "fitableId",
        "925a55c673f3487b9e7117243d1ec223");
    httplib::Headers headers;
    headers.insert(std::make_pair<std::string, std::string>(HEADER_FIT_VERSION, std::to_string(meta.get_version())));
    headers.insert(
        std::make_pair<std::string, std::string>(HEADER_FIT_DATA_FORMAT, std::to_string(meta.get_payload_format())));
    headers.insert(std::make_pair<std::string, std::string>(
        HEADER_FIT_GENERICABLE_VERSION, to_std_string(meta.get_generic_version().to_string())));
    headers.insert(std::make_pair<std::string, std::string>(
        HEADER_FIT_ACCESS_TOKEN, to_std_string(meta.get_access_token())));
    string tlvBytes;
    meta.Serialize(tlvBytes);
    headers.insert(std::make_pair<std::string, std::string>(HEADER_FIT_TLV, ""));

    auto ret = HttplibUtil::BuildRequestHeaders(meta);

    ASSERT_EQ(ret, headers);
}