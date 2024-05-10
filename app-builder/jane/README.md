<div style="text-align: center;"><span style="font-size:36px">Jane（玉简）</span></div>

[TOC]

### 本地启动

由于接入了KMS加解密密钥的能力，而KMS的密钥配置文件按照安全的要求，必须分段式存储，本地启动时需要加入如下4个环境变量

```shell
CONFIG_PART1
CONFIG_PART2
work_key_cipher
cipher_token
```

同时KMS还有一份密钥文件（密钥文件请到玉简群空间下载：[opt.rar](https://onebox.huawei.com/p/5c995aadd8bbc2f85d99e69a7ac396fe)），是根据如下3个参数生成的，因此需要将密钥文件按对应参数值内容放在本地D盘，参数具体的值以及文件目录规则参考<code>
SecurityUtil.java</code>

```shell
appName
subAppName
environment
```

## 组件结构图

```plantuml
component "Task Center" as TC
component "Data Engine" as DE
component "Flows Engine" as FE
```

## 类图

```plantuml
hide empty member

class TaskDefinitionSource {
- id : String
- definitionId : String
- sourceApp : String
- obtainingMethod : MetaDataSourceType
- properties : Map<String, String>
- taskDefinitionSourceRepo : <u><color:blue>TaskDefinitionSourceRepo</color></u>
}

enum MetaDataSourceType {
+ SCHEDULE
+ REFRESH
+ PROCESS_DRIVEN
+ THIRD_PARTY_PUSH
}
TaskDefinitionSource -down- MetaDataSourceType

class TaskDefinitionProperty {
- id : String
- taskDefinitionId : String
- name : String
- nameEn : String
- description : String
- options : List<<u><color:blue>TextStringValue</color></u>>
- icon : String
- propertyType : <u><color:blue>PropertyType</color></u>
- displayType : <u><color:blue>PropertyValueType</color></u>
- <color:red>isRequired</color> : Boolean <i><color:gray>// required</color></i>
- order : Integer
- <color:red>isDisplayed</color> : Boolean <i><color:gray>// visible</color></i>
- displayOrder : Integer
- taskPropertyHandlers : List<<u><color:blue>TaskPropertyHandler</color></u>>
- taskDefinitionPropertyRepo : <u><color:blue>TaskDefinitionPropertyRepo</color></u>
- taskPropertyHandlerRepo : <u><color:blue>TaskPropertyHandlerRepo</color></u>
}

enum PropertyType {
+ SYSTEM
+ PUBLIC
+ PRIVATE
}
TaskDefinitionProperty -down- PropertyType

enum PropertyValueType {
+ RADIO
+ CHECK_BOX
+ NUMBER
+ DATE
+ TEXT
+ FILE
+ LEVEL
+ SELECTION
+ PERSON
}
TaskDefinitionProperty -down- PropertyValueType

class TaskPropertyHandler {
- id : String
- taskPropertyDefinitionId : String
- sourceDefinitionId : String
- sourceApp : String
- triggerBehavior : TriggerBehavior
- taskPropertyHandlerRepo : <u><color:blue>TaskPropertyHandlerRepo</color></u>
}
TaskDefinitionProperty *-down-> TaskPropertyHandler

enum TriggerBehavior {
+ REMOTE_CALL
}
TaskPropertyHandler -down- TriggerBehavior

interface Repo<T> {
+ save(entity: T) : T
+ find(id: String) : T
+ delete(entity: T) : void
}

interface TaskDefinitionPropertyRepo {
+ findByDefinitionId(definitionId: String) : List<<u><color:blue>TaskDefinitionProperty</color></u>>
}
TaskDefinitionPropertyRepo -up-|> Repo : <i><TaskDefinitionProperty></i>
TaskDefinitionProperty *-down-> TaskDefinitionPropertyRepo

interface TaskPropertyHandlerRepo {
+ findByPropertyDefinitionId(propertyDefinitionId: String) : List<<u><color:blue>TaskPropertyHandler</color></u>>
}
TaskPropertyHandlerRepo -up-|> Repo : <i><TaskPropertyHandler</i>
TaskDefinitionProperty *-down-> TaskPropertyHandlerRepo

class BaseDomain {
- isDeleted : boolean
- createdBy : String
- updatedBy : String
- deletedBy : String
- createdAt : LocalDateTime
- updatedAt : LocalDateTime
- deletedAt : LocalDateTime
# lazyGet(target: X, supplier: Supplier<X>, consumer: Consumer<X>) : X <X>
}

class TaskDefinition {
- id : String
- type : String
- referenceId : String
- referenceSource : String
- detail : String
- propertyDefinitions : List<<u><color:blue>TaskDefinitionProperty</color</u>>
- sourceDefinitions : List<<u><color:blue>TaskDefinitionSource</color></u>>
- tenant : String
- taskDefinitionRepo : <u><color:blue>TaskDefinitionRepo</color></u>
- taskDefinitionPropertyRepo : <u><color:blue>TaskDefinitionPropertyRepo</color></u>
- taskDefinitionSourceRepo : <u><color:blue>TaskDefinitionSourceRepo</color></u>
- systemFieldConfig : <u><color:blue>SystemFieldConfig</color></u>
+ save() : <u><color:blue>TaskDefinition</color></u>
+ update() : <u><color:blue>TaskDefinition</color></u>
+ delete() : void
+ getPropertyDefinitionByNameEn(nameEn: String) : Optional<<u><color:blue>TaskDefinitionProperty</color></u>>
+ getPropertyDefinitions() : List<<u><color:blue>TaskDefinitionProperty</color></u>>
+ getSourceDefinitions() : List<<u><color:blue>TaskDefinitionSource</color></u>>
+ setTaskDefinitionPropertyRepo(repo: <u><color:blue>TaskDefinitionPropertyRepo</color></u>) : void
+ setTaskPropertyDefinitionHandlerRepo(repo: <u><color:blue>TaskPropertyHandlerRepo</color></u>) : void
+ setTaskDefinitionSourceRepo(repo: <u><color:blue>TaskDefinitionSourceRepo</color></u>) : void
- updateDefinitionPropertyList(updatedDefinition: <u><color:blue>TaskDefinition</color></u>) : void
- updateDefinitionSourceList(updatedDefinition: <u><color:blue>TaskDefinition</color></u>) : void
- deleteSourceNotInUpdatedList(updatedDefinitionSourceList: List<<u><color:blue>TaskDefinitionSource</color></u>>) : void
- deletePropertiesNotInUpdatedList(updatedDefinitionPropertyList: List<<u><color:blue>TaskDefinitionProperty</color></u>>) : void
- setOrderOfDefinitionPropertyList(propertyList: List<<u><color:blue>TaskDefinitionProperty</color></u>>) : void
- getTaskSourceDefinitionsByDefinitionId() : List<<u><color:blue>TaskDefinitionSource</color></u>>
- getTaskPropertyDefinitionsByDefinitionId() : List<<u><color:blue>TaskDefinitionProperty>>
- addSourceAppOfHandlers(definitionProperties: List<<u><color:blue>TaskDefinitionProperty</color></u>>) : void
}
TaskDefinition -up-|> BaseDomain
TaskDefinition *-down-> TaskDefinitionProperty
TaskDefinition *-down-> TaskDefinitionSource
TaskDefinition *-down-> TaskDefinitionRepo
TaskDefinition *-down-> TaskDefinitionPropertyRepo
TaskDefinition *-down-> TaskDefinitionSourceRepo

interface SystemFieldConfig {
+ getOrder(fieldKey: String) : Optional<Integer>
+ listAll() : Map<String, Integer>
}
TaskDefinition *-down-> SystemFieldConfig

interface TaskDefinitionRepo {
+ findByReferenceId(referenceId: String) : Optional<TaskDefinition>
}
TaskDefinitionRepo -down-|> Repo : <i><TaskDefinition></i>

interface TaskDefinitionSourceRepo {
+ findByDefinitionId(definitionId: String) : List<<u><color:blue>TaskDefinitionSource</color></u>>
}
TaskDefinitionSourceRepo -down-|> Repo : <i><TaskDefinitionSource></i>
```