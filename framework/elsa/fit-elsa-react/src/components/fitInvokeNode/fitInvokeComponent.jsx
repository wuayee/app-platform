import InvokeInput from "@/components/common/InvokeInput.jsx";
import FitInvokeService from "@/components/fitInvokeNode/FitInvokeService.jsx";
import InvokeOutput from "@/components/common/InvokeOutput.jsx";
import {v4 as uuidv4} from "uuid";
import {convertParameter, convertReturnFormat} from "@/components/util/MethodMetaDataParser.js";
import {toolInvokeComponent} from "@/components/toolInvokeNode/toolInvokeComponent.jsx";

/**
 * FIT调用节点组件
 *
 * @param jadeConfig
 */
export const fitInvokeComponent = (jadeConfig) => {
    const self = toolInvokeComponent(jadeConfig);

    /**
     * 必填
     *
     * @return 组件信息
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [],
            genericable: {
                id: "genericable_" + uuidv4(),
                name: "genericable",
                type: "Object",
                from: "Expand",
                // 保存当前选中的Genericable信息
                value: [{id: uuidv4(), name: 'id', type: 'String', from: 'Input', value: ''}]
            },
            fitable: {
                id: "fitable_" + uuidv4(),
                name: "fitable",
                type: "Object",
                from: "Expand",
                // 保存当前选中的fitable信息
                value: [{id: uuidv4(), name: 'id', type: 'String', from: 'Input', value: ''}]
            },
            outputParams: []
        };
    };

    /**
     * 获取当前节点的所有组件
     *
     * @return {JSX.Element}
     */
    self.getReactComponents = () => {
        return (<>
            <InvokeInput/>
            <FitInvokeService/>
            <InvokeOutput/>
        </>);
    };

    /**
     * @override
     */
    const reducers = self.reducers;
    self.reducers = (config, action) => {
        const _selectGenericable = () => {
            newConfig.genericable.value.find(item => item.name === "id").value = action.value;
            // 切换服务选择后，把选则的fitable置空
            newConfig.fitable.value.find(item => item.name === "id").value = '';
        };

        const _selectFitable = () => {
            newConfig.fitable.value.find(item => item.name === "id").value = action.value.schema.parameters.fitableId;
        };

        const _generateOutput = () => {
            const inputJson = action.value;
            const newOutputParams = convertReturnFormat(inputJson.schema.return);
            // 这里可能有问题 生成的不是数组，需要改为数组
            newConfig.outputParams.push(newOutputParams);
        }

        const _generateInput = () => {
            const inputJson = action.value;
            const convertedParameters = Object.keys(inputJson.schema.parameters.properties).map(key => {
                return convertParameter({
                    propertyName: key,
                    property: inputJson.schema.parameters.properties[key]
                });
            });
            delete newConfig.inputParams;
            newConfig.inputParams = convertedParameters;
        };

        let newConfig = {...config};
        switch (action.type) {
            // 通过json字符串生成jadeConfig的input对象
            case 'generateInput':
                _generateInput();
                return newConfig;
            case 'selectGenericable':
                _selectGenericable();
                return newConfig;
            case 'selectFitable':
                _selectFitable();
                _generateInput();
                _generateOutput();
                return newConfig;
        }
        return reducers.apply(self, [config, action]);
    };

    return self;
}