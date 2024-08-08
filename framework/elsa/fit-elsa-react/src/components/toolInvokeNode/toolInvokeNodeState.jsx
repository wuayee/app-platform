import {jadeNode} from "@/components/jadeNode.jsx";
import {convertParameter, convertReturnFormat} from "@/components/util/MethodMetaDataParser.js";
import httpUtil from "@/components/util/httpUtil.jsx";
import {formatString} from "@/components/util/StringUtil.js";
import {toolInvokeNodeDrawer} from "@/components/toolInvokeNode/toolInvokeNodeDrawer.jsx";
import {SOURCE_PLATFORM} from "@/common/Consts.js";

/**
 * 工具调用节点shape
 *
 * @override
 */
export const toolInvokeNodeState = (id, x, y, width, height, parent, drawer) => {
    const self = jadeNode(id, x, y, width, height, parent, drawer ? drawer : toolInvokeNodeDrawer);
    self.type = "toolInvokeNodeState";
    self.width = 360;
    self.componentName = "toolInvokeComponent";
    self.flowMeta.jober.type = 'STORE_JOBER';
    const toolEntity = {
        uniqueName: "",
        params: [],
        return: {
            type: ""
        }
    };
    const template = {
        inputParams: [],
        outputParams: []
    };

    /**
     * 拉取versionInfo数据.
     *
     * @param callback 回调.
     */
    self.fetchVersionInfo = (callback) => {
        const url = self.graph.getConfig(self)?.urls?.versionInfo;
        if (!url) {
            return;
        }
        const uniqueName = self.flowMeta.jober.entity.uniqueName;
        const replacedUrl = formatString(url, {tenant: self.graph.tenant, uniqueName});
        httpUtil.get(replacedUrl, new Map(), (result) => {
            callback(result.data);
        });
    };

    /**
     * @override
     */
    const serializerJadeConfig = self.serializerJadeConfig;
    self.serializerJadeConfig = (jadeConfig) => {
        serializerJadeConfig.apply(self, [jadeConfig]);
        self.flowMeta.jober.entity.params = self.flowMeta.jober.converter.entity.inputParams.map(property => {
            return {name: property.name}
        });
    };

    /**
     * @override
     */
    self.processMetaData = (metaData) => {
        const _generateOutput = () => {
            newConfig.outputParams.push(convertReturnFormat(metaData.schema.return));
        }

        const _generateInput = () => {
            // 这里需要确认，返回的到底是什么数据类型，data是个数组还是对象
            delete newConfig.inputParams;
            const orderProperties = metaData.schema.parameters.order ? metaData.schema.parameters.order : Object.keys(metaData.schema.parameters.properties);
            newConfig.inputParams = orderProperties.map(key => {
                return convertParameter({
                    propertyName: key,
                    property: metaData.schema.parameters.properties[key],
                    isRequired: metaData.schema.parameters.required.some(item => item === key)
                });
            });
        };

        const newConfig = {...template};
        _generateInput();
        _generateOutput();
        self.flowMeta.jober.converter.entity = newConfig;
        self.flowMeta.jober.entity = toolEntity;
        self.sourcePlatform = metaData.source?.toLowerCase() ?? SOURCE_PLATFORM.OFFICIAL;
        self.flowMeta.jober.entity.uniqueName = metaData.uniqueName;
        self.flowMeta.jober.entity.return.type = metaData.schema.return.type;
        self.text = self.page.generateNodeName(metaData.name, self.type);
        self.drawer.unmountReact();
        self.invalidateAlone();
    };

    return self;
};