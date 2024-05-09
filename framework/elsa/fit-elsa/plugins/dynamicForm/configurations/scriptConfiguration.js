import {formBaseConfiguration} from "./formBaseConfiguration.js";

/**
 * script配置.
 *
 * @param target 图形对象.
 * @param field 字段.
 * @returns {{}} 配置对象.
 */
export const scriptConfiguration = (target, field) => {
    const self = formBaseConfiguration(target, field);
    self.type = "string";
    self.title = "script";
    return self;
};