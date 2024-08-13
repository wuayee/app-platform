/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : 提供对的 http 客户端的默认实现
 * Author       : w00561424
 * Date:        : 2024/05/09
 */
#include <include/curl_http_client.h>
#include <curl/include/curl/easy.h>
#include <fit/fit_log.h>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/external/util/string_utils.hpp>
#include <fit/external/util/base64.h>
#include <http_util.hpp>
#include <httplib_util.hpp>
namespace Fit {
constexpr uint32_t MS_PER_SECOND = 1000;
constexpr uint32_t US_PER_MS = 1000;

inline void ConvertTimeout(int64_t ms, time_t& second, time_t& us)
{
    second = ms / MS_PER_SECOND;
    us = (ms % MS_PER_SECOND) * US_PER_MS;
}
typedef struct MemoryStruct {
    char *memory;
    size_t size;

    static void Init(MemoryStruct& data)
    {
        data.memory = (char*)malloc(1);
        data.size = 0;
    }
} MemoryStruct;

static size_t MemoryCallback(void *contents, size_t size, size_t nmemb, void *user) {
    size_t realSize = size * nmemb;
    struct MemoryStruct *mem = (struct MemoryStruct *)user;

    mem->memory = (char*)realloc(mem->memory, mem->size + realSize + 1);
    if(mem->memory == NULL) {
        FIT_LOG_ERROR("Not enough memory.");
        return 0;
    }

    memcpy(&(mem->memory[mem->size]), contents, realSize);
    mem->size += realSize;
    mem->memory[mem->size] = 0;
    return realSize;
}

std::string removeSpecialChars(const std::string& str) {
    std::string result;
    for (char c : str) {
        if (std::isspace(c) || c == '\r') {
            continue;
        }
        result += c;
    }
    return result;
}

string GetHeaderData(const string& header, const Fit::string key)
{
    vector<string> elements = StringUtils::Split(header, '\n');
    for (string element : elements) {
        size_t pos = element.find_first_of(':');
        if (pos == std::string::npos) {
            continue;
        }

        string keyTemp = element.substr(0, pos);
        string value = element.substr(pos + 1, element.length());
        if (StringUtils::IsEqualIgnoreCase(keyTemp, key)) {
            return removeSpecialChars(value);
        }
    }
    return "";
}

string GetMetaData(const string& header)
{
    return GetHeaderData(header, HEADER_FIT_META);
}

string GetCode(const string& header)
{
    return GetHeaderData(header, HEADER_FIT_CODE);
}

string GetMessage(const string& header)
{
    return GetHeaderData(header, HEADER_FIT_MESSAGE);
}

struct curl_slist* BuildHeaders(const fit_meta_data& meta)
{
    auto funcAppendStr = [](const string& key, const string& value) {
        return key + ": " + value;
    };

    curl_slist* headers = nullptr;
    headers = curl_slist_append(headers, funcAppendStr(HEADER_FIT_DATA_FORMAT,
        Fit::to_string(meta.get_payload_format())).c_str());
    headers = curl_slist_append(headers, funcAppendStr(HEADER_FIT_GENERICABLE_VERSION,
        meta.get_generic_version().to_string()).c_str());
    headers = curl_slist_append(headers, funcAppendStr(HEADER_FIT_ACCESS_TOKEN,
        meta.get_access_token()).c_str());
    headers = curl_slist_append(headers, funcAppendStr(HEADER_FIT_VERSION,
        Fit::to_string(meta.get_version())).c_str());
    string tlvBytes;
    meta.Serialize(tlvBytes);
    headers = curl_slist_append(headers, funcAppendStr(HEADER_FIT_TLV, Base64Encode(tlvBytes)).c_str());
    headers = curl_slist_append(headers, funcAppendStr(HTTP_CONTENT_TYPE_KEY, HTTP_CONTENT_TYPE_JSON).c_str());
    return headers;
}

CurlHttpClient::CurlHttpClient(string contextPath, const HttpConfig* config, string hostAndPort)
    : contextPath_(move(contextPath)), config_(config),  hostAndPort_(std::move(hostAndPort))
{
}

void CurlHttpClient::GlobalInit()
{
    curl_global_init(CURL_GLOBAL_ALL);
}

void CurlHttpClient::GlobalUninit()
{
    curl_global_cleanup();
}

CURL* CurlHttpClient::PreProcess(int64_t timeoutMs)
{
    time_t second {};
    time_t us {};
    ConvertTimeout(timeoutMs, second, us);
    CURL* curl = curl_easy_init();
    if (curl != nullptr) {
        curl_easy_setopt(curl, CURLOPT_POST, 1L);
        curl_easy_setopt(curl, CURLOPT_CONNECTTIMEOUT, 1L);
        curl_easy_setopt(curl, CURLOPT_TIMEOUT, second);
    }
    return curl;
}

FitCode CurlHttpClient::Call(CURL* curl, Response& result)
{
    FitCode ret = FIT_OK;
    MemoryStruct data;
    MemoryStruct::Init(data);
    MemoryStruct headerData;
    MemoryStruct::Init(headerData);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, MemoryCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void*)&data);
    curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, MemoryCallback);
    curl_easy_setopt(curl, CURLOPT_HEADERDATA, (void*)&headerData);
    CURLcode res = curl_easy_perform(curl);
    if(res != CURLE_OK) {
        FIT_LOG_ERROR("Perform error : %s.", curl_easy_strerror(res));
        ret = FIT_ERR_FAIL;
    } else {
        string dataStr = string(headerData.memory, headerData.size);
        string headerStr = string(headerData.memory, headerData.size);
        *result.metadata = Base64Decode(GetMetaData(headerStr));
        // 兼容新接口
        if (result.metadata->empty()) {
            // 获取状态码
            string codeStr = GetCode(headerStr);
            FIT_LOG_CORE("CodeStr is :%s.", codeStr.c_str());
            if (!codeStr.empty()) {
                result.code = atoi(codeStr.c_str());
            } else {
                curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &result.code);
            }
            result.message = GetMessage(headerStr);
        }
        *result.data = bytes(data.memory, data.size);
    }
    free(data.memory);
    free(headerData.memory);
    return ret;
}

FitCode CurlHttpClient::RequestResponse(const fit::hakuna::kernel::broker::client::RequestParam& req, Response& result)
{
    CURL* curl = PreProcess(req.timeout);
    if (curl == nullptr) {
        FIT_LOG_ERROR("Curl http client pre process failed.");
        return FIT_ERR_FAIL;
    }

    fit_meta_data meta(req.metaData.version, req.metaData.payloadFormat,
        fit_version::parse_from(req.metaData.genericableVersion, fit_version(atoi("1"), atoi("0"), atoi("0"))),
        req.metaData.genericableId, req.metaData.fitableId, req.metaData.accessToken);
    string reqData = std::string(req.data.data(), req.data.size());
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, reqData.c_str());

    auto path = config_->GetClientPath(contextPath_, meta.get_generic_id(), meta.get_fit_id());
    path = hostAndPort_ + path;
    FIT_LOG_INFO("Path is %s.", path.c_str());

    curl_easy_setopt(curl, CURLOPT_URL, path.c_str());
    curl_slist *headers = BuildHeaders(meta);
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    FitCode ret = Call(curl, result);
    curl_slist_free_all(headers);
    AfterProcess(curl);
    return ret;
}

FitCode CurlHttpClient::AfterProcess(CURL* curl)
{
    curl_easy_cleanup(curl);
    return FIT_OK;
}
}