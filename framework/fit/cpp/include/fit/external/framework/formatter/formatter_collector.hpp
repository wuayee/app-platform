/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/12
 * Notes:       :
 */

#ifndef FORMATTER_COLLECTOR_HPP
#define FORMATTER_COLLECTOR_HPP

#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/external/framework/function_proxy.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <fit/external/framework/annotation/fitable_detail.hpp>
#include <fit/external/util/context/context_api.hpp>

namespace Fit {
namespace Framework {
namespace Formatter {
using SerializeFunc = std::function<FitCode(ContextObj ctx, const Argument &, Fit::string &)>;
using DeserializeFunc = std::function<FitCode(ContextObj ctx, const Fit::string &, Argument &)>;

enum ProtocolType : int32_t {
    PROTOCOL_TYPE_PROTOBUF = 0,
    PROTOCOL_TYPE_JSON = 1,
};

struct ArgConverter {
    SerializeFunc Serialize;
    DeserializeFunc Deserialize;
};
using ArgConverterList = Fit::vector<ArgConverter>;

class __attribute__ ((visibility ("default"))) FormatterMeta {
public:
    FormatterMeta();
    ~FormatterMeta();
    FormatterMeta(const FormatterMeta &) = delete;
    FormatterMeta &operator=(const FormatterMeta &) = delete;
    FormatterMeta(FormatterMeta &&) = delete;
    FormatterMeta &operator=(FormatterMeta &&) = delete;

    void SetGenericId(Fit::string val);
    const Fit::string &GetGenericId() const;
    void SetFormat(int32_t val);
    int32_t GetFormat() const;
    void SetArgsInConverter(ArgConverterList val);
    const ArgConverterList &GetArgsInConverter() const;
    void SetArgsOutConverter(ArgConverterList val);
    const ArgConverterList &GetArgsOutConverter() const;
    Arguments CreateArgOut(ContextObj ctx) const;
    void SetCreateArgsOut(std::function<Arguments(ContextObj ctx)> func);
    void SetFitableType(Fit::Framework::Annotation::FitableType fitableType);
    Fit::Framework::Annotation::FitableType GetFitableType();
private:
    class Impl;

    Impl *impl_;
};

using FormatterMetaPtr = std::shared_ptr<FormatterMeta>;
using FormatterMetaPtrList = Fit::vector<FormatterMetaPtr>;

class __attribute__ ((visibility ("default"))) FormatterCollector {
public:
    static void Register(const FormatterMetaPtrList &val);

    static void UnRegister(const FormatterMetaPtrList &val);
};

class __attribute__ ((visibility ("hidden"))) FormatterPluginCollector {
public:
    ~FormatterPluginCollector()
    {
        FormatterCollector::UnRegister(formatters_);
    }

    static void Register(const FormatterMetaPtrList &formatters)
    {
        Instance().AddItems(formatters);
        FormatterCollector::Register(formatters);
    }

    static FormatterPluginCollector &Instance()
    {
        static FormatterPluginCollector instance;
        return instance;
    }

    void AddItems(const FormatterMetaPtrList &formatters)
    {
        formatters_.insert(formatters_.end(), formatters.begin(), formatters.end());
    }

private:
    FormatterMetaPtrList formatters_ {};
};

template<typename T>
inline std::function<Arguments(ContextObj ctx)> BuildCreateArgOut()
{
    static_assert(!std::is_pointer<T>::value, "need a raw type");
    return [](ContextObj ctx) -> Arguments {
        return Arguments {Fit::Context::NewObj<T *>(ctx)};
    };
}

template<>
inline std::function<Arguments(ContextObj ctx)> BuildCreateArgOut<void>()
{
    return [](ContextObj ctx) -> Arguments {
        return Arguments {};
    };
}

template<size_t FormatType>
class ArgConverterDispatcher {
public:
    template<typename T>
    static ArgConverter Raw();
    template<typename T>
    static ArgConverter Repeated();
    template<typename T>
    static ArgConverter Mapped();
};

template<typename T, size_t FormatType>
class ConverterBuilder {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Raw<T>();
    }
};

template<size_t FormatType>
class ConverterBuilder<void, FormatType> {
public:
    static ArgConverter Build()
    {
        return {
            [](ContextObj ctx, const Argument &arg, Fit::string &result) { return FIT_OK; },
            [](ContextObj ctx, const Fit::string &str, Argument &result) { return FIT_OK; }
        };
    }
};

template<typename T, size_t FormatType>
class ConverterBuilder<const Fit::vector<T> *, FormatType> {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Repeated<const Fit::vector<T> *>();
    }
};

template<typename T, size_t FormatType>
class ConverterBuilder<Fit::vector<T> *, FormatType> {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Repeated<Fit::vector<T> *>();
    }
};

template<typename T, size_t FormatType>
class ConverterBuilder<Fit::vector<T> **, FormatType> {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Repeated<Fit::vector<T> **>();
    }
};

// map
template<typename KEY, typename VALUE, size_t FormatType>
class ConverterBuilder<const Fit::map<KEY, VALUE> *, FormatType> {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Mapped<const Fit::map<KEY, VALUE> *>();
    }
};

template<typename KEY, typename VALUE, size_t FormatType>
class ConverterBuilder<Fit::map<KEY, VALUE> *, FormatType> {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Mapped<Fit::map<KEY, VALUE> *>();
    }
};

template<typename KEY, typename VALUE, size_t FormatType>
class ConverterBuilder<Fit::map<KEY, VALUE> **, FormatType> {
public:
    static ArgConverter Build()
    {
        return ArgConverterDispatcher<FormatType>::template Mapped<Fit::map<KEY, VALUE> **>();
    }
};

template<typename... T>
class CreateArgOutBuilder {
public:
    static std::function<Arguments(ContextObj ctx)> Build()
    {
        return [](ContextObj ctx) -> Arguments {
            return Arguments {};
        };
    }
};

template<typename T>
class CreateArgOutBuilder<T> {
public:
    static_assert(!std::is_pointer<T>::value, "need a raw type");

    static std::function<Arguments(ContextObj ctx)> Build()
    {
        return BuildCreateArgOut<T>();
    }
};

template<typename T>
class CreateArgOutBuilder<T **> {
public:
    static_assert(!std::is_pointer<T>::value, "need a raw type");

    static std::function<Arguments(ContextObj ctx)> Build()
    {
        return BuildCreateArgOut<T>();
    }
};

template<>
class CreateArgOutBuilder<void> {
public:
    static std::function<Arguments(ContextObj ctx)> Build()
    {
        return [](ContextObj ctx) -> Arguments {
            return Arguments {};
        };
    }
};

template<size_t FormatType>
class BuilderDispatcher {
public:
    template<typename T>
    using Converter = ConverterBuilder<T, FormatType>;

    template<typename... T>
    using ArgOutCreator = CreateArgOutBuilder<T...>;
};

template<size_t FormatType, size_t N>
class ConverterFiller;

template<size_t FormatType>
class ConverterFiller<FormatType, 0> {
public:
    template<typename... T>
    static void Fill(ArgConverterList &argsInConverter) {}
};

template<size_t FormatType>
class ConverterFiller<FormatType, 1> {
public:
    template<typename T>
    static typename std::enable_if<!std::is_void<T>::value, void>::type Fill(ArgConverterList &argsInConverter)
    {
        argsInConverter.push_back(BuilderDispatcher<FormatType>::template Converter<T>::Build());
    }
    template<typename T>
    static typename std::enable_if<std::is_void<T>::value, void>::type Fill(ArgConverterList &argsInConverter) {}
};

template<size_t FormatType, size_t N>
class ConverterFiller {
public:
    template<typename T, typename ...Args>
    static void Fill(ArgConverterList &argsInConverter)
    {
        argsInConverter.push_back(BuilderDispatcher<FormatType>::template Converter<T>::Build());
        ConverterFiller<FormatType, sizeof...(Args)>::template Fill<Args...>(argsInConverter);
    }
};

template<size_t FormatType, typename ...Args>
class FormatterMetaBuilder;

template<size_t FormatType, typename ...ArgsIn, typename ...ArgsOut>
class FormatterMetaBuilder<FormatType, ArgumentsIn<ArgsIn...>, ArgumentsOut<ArgsOut...>> {
public:
    static FormatterMetaPtr Build(const char *genericId, Fit::Framework::Annotation::FitableType fitableType)
    {
        auto meta = std::make_shared<FormatterMeta>();
        meta->SetGenericId(genericId);
        meta->SetFormat(FormatType);
        meta->SetFitableType(fitableType);
        ArgConverterList argsInConverter;
        ConverterFiller<FormatType, sizeof...(ArgsIn)>::template Fill<ArgsIn...>(argsInConverter);
        meta->SetArgsInConverter(argsInConverter);
        ArgConverterList argsOutConverter;
        ConverterFiller<FormatType, sizeof...(ArgsOut)>::template Fill<ArgsOut...>(argsOutConverter);
        meta->SetArgsOutConverter(argsOutConverter);
        meta->SetCreateArgsOut(BuilderDispatcher<FormatType>::template ArgOutCreator<ArgsOut...>::Build());

        return meta;
    }
};

template<typename Meta, typename = void>
class ArgumentsInTypeHelper {
public:
    using Type = ArgumentsIn<void>;
};

template<typename Meta>
class ArgumentsInTypeHelper<Meta, typename std::enable_if<!std::is_void<typename Meta::InType>::value, void>::type> {
public:
    using Type = typename Meta::InType;
};

template<typename Meta, typename = void>
class ArgumentsOutTypeHelper {
public:
    using Type = ArgumentsOut<void>;
};

template<typename Meta>
class ArgumentsOutTypeHelper<Meta, typename std::enable_if<!std::is_void<typename Meta::OutType>::value, void>::type> {
public:
    using Type = typename Meta::OutType;
};

template<typename GMeta, typename GType, size_t FormatterType>
FormatterMetaPtr FormatterMetaBuilderHelper(Annotation::FitableType type)
{
    return FormatterMetaBuilder<FormatterType,
        typename ArgumentsInTypeHelper<GMeta>::Type,
        typename ArgumentsOutTypeHelper<GMeta>::Type>::Build(GType::GENERIC_ID, type);
}

template<typename GMeta, typename GType, size_t FormatterType>
void FormatterMetaRegisterHelper(Annotation::FitableType type)
{
    auto meta = FormatterMetaBuilderHelper<GMeta, GType, FormatterType>(type);
    FormatterPluginCollector::Register({meta});
}
}
}
}
#endif // FORMATTER_COLLECTOR_HPP
