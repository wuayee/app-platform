LLM 产生结构化输出的能力对于依赖可靠解析输出值的下游应用程序非常重要。开发人员希望将 AI 模型的结果快速转换为数据类型，例如 Java 类，这些数据类型可以传递给其他应用程序的函数和方法。

FEL 输出解释器可以将 LLM 输出转换为结构化格式。接口如下：

```java
public interface OutputParser<O> extends Formatter, Parser<String, O> {}
```

解析器调用的接口帮助实现了引导模型输出和将输出转换成对象的流程。
`Formatter`：格式化器用于提供语言模型的输出应该如何格式化的提示词。
`Parser`：解析器提供把输入解析成指定对象的方法。

实现接口如下：

```java
public interface Formatter {
    String instruction();
}
```

```java
public interface Parser<I, R> {
    R parse(I input);
}
```

## 解析成 JavaBean

通过在目标 Java 类中添加 `@Property` 注解，其中 `description`、`defaultValue`、`required` 等参数来扩充指引。

```java
static class Joke{
        @Property(description = "question to set up a joke", required = True, defaultValue: "等病好了我就去看医生。")
        private String setup;

        @Property(description = "answer to resolve the joke")
        private String punchline;

        // 省略获取和设置值的方法
}
```

1. 完整解析

```java
ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);
OutputParser<Test> outputParser = JsonOutputParser.create(serializer, Test.class);
String json = "{\"setup\": \"有一只鲨鱼吃下了一颗绿豆，结果它变成了什么？\",\"punchline\": \"绿豆沙\"}";
Joke joke = outputParser.parse(json);
System.out.println(outputParser.instruction())
System.out.println(joke.getSetup())
System.out.println(joke.getPunchline())
```

````plaintext
The output should be formatted as a JSON instance that conforms to the JSON schema below.
Here is the output schema:
```
{"type":"object","required":["setup"],"properties":{"punchline":{"type":"string","description":"answer to resolve the joke"},"setup":{"type":"string","description":"question to set up a joke","default":"等病好了我就去看医生。"}}}
```
有一只鲨鱼吃下了一颗绿豆，结果它变成了什么？
绿豆沙
````

2. 部分解析

当被解析的 json 不完整时，可以使用 partial 解析器进行修复，这在流式输出场景非常有用：

```java
OutputParser<Test> outputParser = JsonOutputParser.createPartial(serializer, Test.class);
String json = "{\"setup\": \"有一只鲨鱼吃下了一颗绿豆，结果它变成了什么？\", \"punchline\":}";
Joke joke = outputParser.parse(json);
System.out.println(joke.getSetup())
System.out.println(joke.getPunchline())
```

```plaintext
有一只鲨鱼吃下了一颗绿豆，结果它变成了什么？
null
```
## 示例

接下来展示如何使用 `outputParser` 来实现内容提取功能。

1. 在项目 pom.xml 加入以下依赖：

```xml

<dependencies>
    <dependency>
        <groupId>modelengine.fit.starter</groupId>
        <artifactId>fit-starter</artifactId>
        <version>${fit.version}</version>
    </dependency>
    <dependency>
        <groupId>modelengine.fit.starter</groupId>
        <artifactId>fit-plugins-starter-web</artifactId>
        <version>${fit.version}</version>
    </dependency>
    <dependency>
        <groupId>modelengine.fit.plugin</groupId>
        <artifactId>fit-http-client-okhttp</artifactId>
        <version>${fit.version}</version>
    </dependency>
    <dependency>
        <groupId>modelengine.fit.jade.fel</groupId>
        <artifactId>fel-core</artifactId>
        <version>${fel.version}</version>
    </dependency>
    <dependency>
        <groupId>modelengine.fit.jade.community</groupId>
        <artifactId>fel-model-openai-plugin</artifactId>
        <version>${fel.version}</version>
    </dependency>
</dependencies>
```

2. 在 application.yml 配置文件中加入以下配置：


```yaml
fel:
    openai:
    api-base:'${api-base}'
    api-key:'${your-api-key}'
example:
    model:'${model-name}'
```

3. 添加如下代码：

```java
@Component
@RequestMapping("/ai/example")
public class OutputParserExampleController {
    private final ChatModel chatModel;
    private final OutputParser<Demo> outputParser;
    private final MessageTemplate template;
    
    @Value("${example.model}")
    private String modelName;

    public OutputParserExampleController(ChatModel chatModel, ObjectSerializer serializer) {
        this.chatModel = chatModel;
        this.outputParser = JsonOutputParser.createPartial(serializer, Demo.class);
        this.template = new HumanMessageTemplate(new DefaultStringTemplate(
                "从用户输入中提取时间，当前时间 {{ctime}}\n\n{{format}}\n\nInput: {{query}}\nOutput:\n").partial("ctime",
                LocalDate.now().toString()).partial("format", this.outputParser.instruction()));
    }

    @GetMapping("/chat")
    public Demo chat(@RequestParam("query") String query) {
        ChatOption option = ChatOption.custom().model(this.modelName).stream(false).build();
        ChatMessage aiMessage =
                this.chatModel.generate(ChatMessages.from(this.template.render(Tip.from("query", query).freeze())),
                        option).first().block().get();
        return outputParser.parse(aiMessage.text());
    }

    @GetMapping("/chat-stream")
    public Choir<Demo> chatStream(@RequestParam("query") String query) {
        ChatOption option = ChatOption.custom().model(this.modelName).stream(true).build();
        StringBuffer sb = new StringBuffer();
        return this.chatModel.generate(ChatMessages.from(this.template.render(Tip.from("query", query).freeze())),
                option).map(ChatMessage::text).map(t -> {
            sb.append(t);
            return sb.toString();
        }).map(outputParser::parse);
    }

    public static class Demo {
        @Property(description = "时间，格式为 yyyy-MM-dd")
        private String date;
    }
}
```
## 验证

- 在浏览器栏输入：`http://localhost:8080/ai/example/chat?query=昨天真是个高兴的日子`

返回如下响应：

```json
{
  "date": "2024-08-28"
}
```
- 在浏览器栏输入：`http://localhost:8080/ai/example/chat-stream?query=昨天真是个高兴的日子`

返回如下响应：


```plaintext
data:{"date":null}

data:{"date":null}

data:{"date":null}

data:{"date":null}

data:{"date":null}

data:{"date":null}

data:{"date":null}

data:{"date":""}

data:{"date":"2"}

data:{"date":"20"}

data:{"date":"202"}

data:{"date":"2024"}

data:{"date":"2024-"}

data:{"date":"2024-0"}

data:{"date":"2024-08"}

data:{"date":"2024-08-"}

data:{"date":"2024-08-2"}

data:{"date":"2024-08-28"}

data:{"date":"2024-08-28"}

data:{"date":"2024-08-28"}

data:{"date":"2024-08-28"}
```