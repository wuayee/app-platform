/**
 * 表单配置基类.
 *
 * @param shape 图形对象.
 * @param field 字段.
 * @returns {{}} 配置对象.
 */
export const formBaseConfiguration = (shape, field) => {
    const self = {};

    /**
     * 触发行为.这里只需要简单设置componentId即可.
     *
     * @param value 设置的值.
     */
    self.action = (value) => {
        shape[field] = value;
    };

    /**
     * 获取值.
     *
     * @returns {*} 属性值.
     */
    self.getValue = () => {
        return shape.get(field);
    };

    return self;
}