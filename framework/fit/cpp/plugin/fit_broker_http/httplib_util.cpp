/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implement for fit http lib util
 * Author       : songyongtan
 * Create       : 2023-08-03
 * Notes:       :
 */

#include "httplib_util.hpp"

#include <fit/stl/memory.hpp>
#include <fit/external/util/string_utils.hpp>
#include <fit/external/util/base64.h>
#include <fit/internal/util/protocol/fit_meta_package_builder.h>
#include <fit/internal/util/protocol/fit_response_meta_data.h>
#include <fit/internal/util/protocol/fit_meta_package_parser.h>
#include <fit/fit_log.h>
#include "http_util.hpp"

using namespace fit_meta_defines;

namespace Fit {
HttpResult HttplibUtil::GetRequest(const httplib::Request& req, Network::Request& innerReq)
{
    // match[0] is the whole path, match[1] should like this "genericableId/fitableId"
    constexpr uint32_t EXPECT_MATCH_SIZE = 2;
    if (req.matches.size() != EXPECT_MATCH_SIZE) {
        return {HTTP_STATUS_BAT_REQUEST, "Illegal fit path"};
    }
    constexpr uint32_t EXPECT_PATH_LEVEL = 2;
    const auto paths = StringUtils::Split(to_fit_string(req.matches[1]), '/');
    if (paths.size() != EXPECT_PATH_LEVEL) {
        return {HTTP_STATUS_BAT_REQUEST, "Illegal fit path"};
    }
    auto format = atoi(req.get_header_value(HEADER_FIT_DATA_FORMAT).c_str());
    if (format == 0) {
        return {HTTP_STATUS_BAT_REQUEST, "No specified FIT-Data-Format"};
    }
    uint32_t version = atoi(req.get_header_value(HEADER_FIT_VERSION).c_str());
    if (version == 0) {
        version = META_VERSION_HAS_RESPONSE_META;
    }
    auto gerericableVersion = req.get_header_value(HEADER_FIT_GENERICABLE_VERSION);
    const string& genericableId = paths[0];
    const string& fitableId = paths[1];
    auto meta = fit_meta_data(
        version, format, fit_version::parse_from(gerericableVersion, {1, 0, 0}), genericableId, fitableId);
    auto tlvBase64Bytes = req.get_header_value(HEADER_FIT_TLV);
    constexpr int32_t BASE64_ENCODE_SIZE_UNIT = 4;
    if (!tlvBase64Bytes.empty() && (tlvBase64Bytes.size() / BASE64_ENCODE_SIZE_UNIT == 0)) {
        meta.Deserialize(Base64Decode(to_fit_string(tlvBase64Bytes)));
    }
    auto metaBytes = fit_meta_package_builder::build(meta);
    if (metaBytes.empty()) {
        return {HTTP_STATUS_BAT_REQUEST, "Invalid request"};
    }

    innerReq.metadata = move(metaBytes);
    innerReq.payload = string(req.body.data(), req.body.size());

    return {HTTP_STATUS_OK, "ok"};
}

string HttplibUtil::BuildExceptionResponse(int32_t status, const string& msg, const httplib::Request& req)
{
    return StringUtils::Format(
        "{\"error\": \"%s\",\"path\":\"%s\",\"status\":%d,\"suppressed\":null}", msg.c_str(), req.path.c_str(), status);
}

httplib::Headers HttplibUtil::BuildRequestHeaders(const fit_meta_data& meta)
{
    httplib::Headers headers;
    headers.insert(std::make_pair<std::string, std::string>(HEADER_FIT_VERSION, std::to_string(meta.get_version())));
    headers.insert(
        std::make_pair<std::string, std::string>(HEADER_FIT_DATA_FORMAT, std::to_string(meta.get_payload_format())));
    headers.insert(std::make_pair<std::string, std::string>(
        HEADER_FIT_GENERICABLE_VERSION, to_std_string(meta.get_generic_version().to_string())));
    string tlvBytes;
    meta.Serialize(tlvBytes);
    headers.insert(std::make_pair<std::string, std::string>(HEADER_FIT_TLV, Base64Encode(tlvBytes)));
    return headers;
}

bytes HttplibUtil::GetResponseMetaBytes(const httplib::Response& res)
{
    return Base64Decode(to_fit_string(res.get_header_value(HEADER_FIT_META)));
}

string HttplibUtil::GetHttpAddress(string host, int32_t port)
{
    return "http://" + host + ":" + to_string(port);
}

string HttplibUtil::GetHttpsAddress(string host, int32_t port)
{
    return "https://" + host + ":" + to_string(port);
}

Fit::string HttplibUtil::GetFileFirstLine(const Fit::string& fullFilePath)
{
    std::string value;
    std::ifstream ifs(fullFilePath.c_str());
    if (!ifs.is_open()) {
        FIT_LOG_ERROR("Open file failed %s.", fullFilePath.c_str());
        return value;
    }
    std::getline(ifs, value);
    ifs.close();
    return value;
}
}