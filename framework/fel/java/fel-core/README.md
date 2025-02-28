# Prompt
Prompt最初是 NLP 研究者为下游任务设计出来的一种任务专属的输入形式或模板，
在 ChatGPT 引发大语言模型新时代之后，Prompt 即成为与大模型交互输入的代称。
即我们一般将给大模型的输入称为 Prompt，将大模型返回的输出称为 Completion。

## 创建单条消息模板
FEL基于Mustache语法，允许开发者将运行时变量绑定到提示模板中。

+ 键值对是常用的构造模板输入的方式：

```java
import chat.modelengine.fel.ChatMessage;

import support.template.modelengine.fel.core.HumanMessageTemplate;
import template.modelengine.fel.core.MessageTemplate;
import util.modelengine.fel.core.Tip;

public class DemoApplication {
    public static void main(String[] args) {
        MessageTemplate prompt = new HumanMessageTemplate("Tell me a {{adjective}} joke about {{content}}.");
        ChatMessage message = prompt.render(Tip.from("adjective", "funny").add("content", "chickens"));
        System.out.println(message.text()); // Tell me a funny joke about chickens.
    }
}
```

+ 使用Tip.fromArray可以省略key，相应的需要在模板中用数组下标代替健值。

```java
import chat.modelengine.fel.ChatMessage;

import support.template.modelengine.fel.core.HumanMessageTemplate;
import template.modelengine.fel.core.MessageTemplate;
import util.modelengine.fel.core.Tip;

public class DemoApplication {
    public static void main(String[] args) {
        MessageTemplate prompt = new HumanMessageTemplate("Tell me a {{0}} joke about {{1}}.");
        // Tip.fromArray允许使用下标作为占位符
        ChatMessage message = prompt.render(Tip.fromArray("funny", "chickens").freeze());
        System.out.println(message.text()); // Tell me a funny joke about chickens.
    }
}
```

**Note**
请注意，输入键值对必须包含所有在模板中使用的占位符，以下代码将无法工作:

```java
import chat.modelengine.fel.ChatMessage;

import support.template.modelengine.fel.core.HumanMessageTemplate;
import template.modelengine.fel.core.MessageTemplate;
import util.modelengine.fel.core.Tip;

public class DemoApplication {
    public static void main(String[] args) {
        MessageTemplate prompt = new HumanMessageTemplate("Tell me a {{0}} joke about {{1}}.");
        // Tip.fromArray允许使用下标作为占位符
        ChatMessage message = prompt.render(Tip.fromArray("funny").freeze()); // throw IllegalArgumentException
    }
}
```