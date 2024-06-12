import ToolInvokeFormWrapper from "@/components/toolInvokeNode/ToolInvokeFormWrapper.jsx";

/**
 * 工具调用节点组件
 *
 * @param jadeConfig
 */
export const toolInvokeComponent = (jadeConfig) => {
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
            <ToolInvokeFormWrapper/>
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
        switch (action.type) {
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