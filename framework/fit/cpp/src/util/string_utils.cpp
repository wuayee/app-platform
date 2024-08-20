/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 *
 * Description  : Provides utility methods for strings.
 * Author       : songyongtan 00558940
 * Date         : 2022/02/24
 */

#include <fit/external/util/string_utils.hpp>

#include <cstdarg>
#include <sstream>
#include <string>
#include <iomanip>
#include <algorithm>
#include <securec.h>

#include <fit/stl/memory.hpp>
#include <fit/stl/vector.hpp>

using namespace Fit;
namespace {
const long long STREAM_SIZE = 16;
constexpr const uint32_t STRING_FORMAT_TEST_SIZE = 1;
constexpr uint32_t STRING_FORMAT_MAX_SIZE = 4096;
constexpr uint32_t STRING_FORMAT_START_SIZE = 256;
}

bool StringUtils::NotEmpty(const string& s)
{
    return !s.empty();
}

int32_t StringUtils::ToInt32(const string& value)
{
    return atoi(value.c_str());
}

string StringUtils::ToHexString(uint64_t value)
{
    std::stringstream ss;
    ss.fill('0');
    ss << std::setw(STREAM_SIZE) << std::hex << value;
    return Fit::to_fit_string(ss.str());
}

string StringUtils::ToHexString(const string& value)
{
    std::ostringstream result;
    std::for_each(value.begin(), value.end(), [&result](const char &item) { result << std::hex << item; });
    return result.str().c_str();
}

size_t StringUtils::ComputeHash(const string& value)
{
    return std::hash<std::string>()(Fit::to_std_string(value));
}

vector<string> StringUtils::Split(const string& s, char separator)
{
    return Split(s, separator, [](const string& part) { return true; });
}

vector<string> StringUtils::Split(const string& s, char separator, const std::function<bool(const string&)>& filter)
{
    std::istringstream iss(Fit::to_std_string(s));
    auto res = Fit::vector<Fit::string> {};
    auto token = Fit::string {};

    while (getline(iss, token, separator)) {
        if (filter(token)) {
            res.push_back(token);
        }
    }

    return res;
}

string StringUtils::Format(const char* fmt, ...)
{
    string result;
    va_list args;
    va_start(args, fmt);
    result = Format(fmt, args);
    va_end(args);
    return result;
}

string StringUtils::Format(const char* fmt, va_list args)
{
    vector<char> buffer;
    uint32_t bufferSize = STRING_FORMAT_START_SIZE;
    do {
        buffer.resize(bufferSize);
        va_list copy;
        va_copy(copy, args);
        auto length = vsnprintf_s(&buffer[0], buffer.size(), buffer.size() - 1, fmt, copy);
        va_end(copy);
        if (length != -1) {
            buffer[length] = '\0';
            return string(&buffer[0], length);
        }
        bufferSize += bufferSize;
    } while (bufferSize <= STRING_FORMAT_MAX_SIZE);

    return "";
}

string StringUtils::Join(char separator, const vector<string>& parts)
{
    std::stringstream ss;
    auto iter = parts.begin();
    if (iter != parts.end()) {
        ss << *iter;
        while (++iter != parts.end()) {
            ss << separator << *iter;
        }
    }
    return Fit::to_fit_string(ss.str());
}

string StringUtils::Join(const string& separator, const vector<string>& parts)
{
    std::ostringstream ss;
    if (!parts.empty()) {
        ss << parts[0];
        for (size_t i = 1; i < parts.size(); i++) {
            ss << separator << parts[i];
        }
    }
    return Fit::to_fit_string(ss.str());
}

string StringUtils::Replace(const string &s, const string &target, const string &replaceStr)
{
    std::ostringstream result;
    size_t startPos = 0;
    size_t targetPos = s.find(target, startPos);
    while (targetPos != Fit::string::npos) {
        result << s.substr(startPos, targetPos - startPos) << replaceStr;
        startPos = targetPos + target.size();
        targetPos = s.find(target, startPos);
    }
    result << s.substr(startPos);

    return result.str().c_str();
}

bool StringUtils::StartsWith(const char *s, const char *prefix)
{
    int32_t i = 0;
    for (; prefix[i] != 0 && s[i] != 0 && s[i] == prefix[i]; ++i) {}

    return prefix[i] == 0;
}

bool StringUtils::StartsWith(const Fit::string &s, const char *prefix)
{
    return StartsWith(s.c_str(), prefix);
}

bool StringUtils::IsBlank(char ch)
{
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
}

bool StringUtils::IsEqualIgnoreCase(const Fit::string& str1, const Fit::string& str2)
{
    if (str1.length() != str2.length()) {
        return false;
    }

    for (size_t i = 0; i < str1.length(); ++i) {
        if (std::tolower(str1[i]) != std::tolower(str2[i])) {
            return false;
        }
    }
    return true;
}