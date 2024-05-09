import FitInvokeInput from "@/components/fitInvokeNode/FitInvokeInput.jsx";
import FitInvokeService from "@/components/fitInvokeNode/FitInvokeService.jsx";
import FitInvokeOutput from "@/components/fitInvokeNode/FitInvokeOutput.jsx";
import {v4 as uuidv4} from "uuid";

/**
 * FIT调用节点组件
 *
 * @param jadeConfig
 */
export const fitInvokeComponent = (jadeConfig) => {
    const self = {};

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
            <FitInvokeInput/>
            <FitInvokeService/>
            <FitInvokeOutput/>
        </>);
    };

    const convertReturnFormat = input => {
        const output = {
            id: "output_" + uuidv4(),
            name: "output",
            type: "",
            value: []
        };

        if (input.return.type === "object") {
            output.type = "Object";
            const properties = input.return.properties;
            for (const prop in properties) {
                const property = properties[prop];
                if (property.type === "object") {
                    output.value.push(...processObjectProperty(prop, property));
                } else {
                    output.value.push({
                        id: uuidv4(),
                        name: prop,
                        type: property.type.capitalize(),
                        value: property.type.capitalize()
                    });
                }
            }
        } else {
            output.type = input.return.type.capitalize();
        }

        return output;
    };

    function processObjectProperty(name, obj) {
        const result = [];
        if (obj.type === "object") {
            for (const prop in obj.properties) {
                const property = obj.properties[prop];
                if (property.type === "object") {
                    result.push(...processObjectProperty(prop, property));
                } else {
                    result.push({
                        id: uuidv4(),
                        name: prop,
                        type: property.type.capitalize(),
                        value: property.type.capitalize()
                    });
                }
            }
        }
        return [{
            id: 'output_' + uuidv4(),
            name: name,
            type: "Object",
            value: result
        }];
    }

    const convertParameter = param => {
        const result = {
            id: param.name + "_" + uuidv4(),
            name: param.name,
            type: param.parameter.type === 'object' ? 'Object' : param.parameter.type.capitalize(),
            // 对象默认展开
            from: param.parameter.type === 'object' ? 'Expand' : 'Reference',
            referenceNode: "",
            referenceId: "",
            referenceKey: "",
            value: []
        };

        // todo 数组不展开数组直接是引用，所以可以按照普通数据类型处理？
        if (param.parameter.type === 'object') {
            const properties = param.parameter.properties;
            result.value = Object.keys(properties).map(key => {
                return convertParameter({
                    name: key,
                    parameter: properties[key]
                });
            });
            result.props = [...result.value];
        }
        return result;
    };

    /**
     * @override
     */
    self.reducers = (config, action) => {
        const _updateInput = (data, id, changes) => data.map(d => {
            const newD = {...d};
            if (d.id === id) {
                changes.forEach(change => {
                    newD[change.key] = change.value;
                });
                return newD;
            }
            if (newD.type === "Object" && Array.isArray(newD.value) && newD.from !== "Reference") {
                newD.value = _updateInput(newD.value, id, changes);
            }

            return newD;
        });

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
            const newOutputParams = convertReturnFormat(inputJson.schema);
            delete newConfig.outputParams;
            newConfig.outputParams = newOutputParams;
        }

        const _generateInput = () => {
            const inputJson = action.value;
            const convertedParameters = Object.keys(inputJson.schema.parameters.properties).map(key => {
                return convertParameter({
                    name: key,
                    parameter: inputJson.schema.parameters.properties[key]
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
            case "update":
                newConfig.inputParams = _updateInput(config.inputParams, action.id, action.changes);
                return newConfig;
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}