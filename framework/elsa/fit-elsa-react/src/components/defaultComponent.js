/**
 * 默认component.
 *
 * @param jadeConfig 配置.
 */
export const defaultComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
    };

    /**
     * 获取组件.
     */
    self.getReactComponents = (disabled, data) => {
    };

    /**
     * 处理器
     */
    self.reducers = (config, action) => {
        const newConfig = {...config};
        if (action.type === "system_update") {
            action.changes.forEach(c => {
                newConfig[c.key] = c.value;
            });
            return newConfig;
        }
        throw Error('Unknown action: ' + action.type);
    };

    return self;
}
