import InvokeInput from "@/components/common/InvokeInput.jsx";
import InvokeOutput from "@/components/common/InvokeOutput.jsx";
import {v4 as uuidv4} from "uuid";

/**
 * huggingFace调用节点组件
 *
 * @param jadeConfig
 */
export const huggingFaceComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必填
     *
     * @return 组件信息
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            inputParams: [],
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
            <InvokeOutput/>
        </>);
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
                    // 当对象由展开变为引用时，需要把对象的value置空
                    if (change.value === "Reference") {
                        newD.value = [];
                    }
                });
                return newD;
            }
            // 当处理的数据是对象，并且对象的from是Expand，则递归处理当前数据的属性
            if (newD.from === "Expand") {
                newD.value = _updateInput(newD.value, id, changes);
            }
            return newD;
        });

        let newConfig = {...config};

        function _insertOrUpdateModelParam() {
            const inputParams = config.inputParams.slice(); // 创建一个新数组以避免直接修改原数组
            const modelParam = {
                id: "model_" + uuidv4(),
                name: "model",
                type: "String",
                from: "Input",
                value: action.value
            };
            if (inputParams.length === 0) {
                // 如果数组为空，直接添加 modelParam 对象
                inputParams.push(modelParam);
            } else if (inputParams.length === 1) {
                // 如果数组长度为1，直接在第二个位置插入新的 modelParam 对象
                inputParams.splice(1, 0, modelParam);
            } else {
                const secondElement = inputParams[1];
                if (secondElement.id.startsWith("model_")) {
                    // 修改第二个对象的 value 属性
                    inputParams[1] = {...secondElement, value: action.value};
                } else {
                    // 在第二个位置插入新的 modelParam 对象
                    inputParams.splice(1, 0, modelParam);
                }
            }
            return inputParams;
        }

        switch (action.type) {
            case "update": {
                newConfig.inputParams = _updateInput(config.inputParams, action.id, action.changes);
                return newConfig;
            }
            case "insertOrUpdateModel": {
                newConfig.inputParams = _insertOrUpdateModelParam();
                return newConfig;
            }
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
}