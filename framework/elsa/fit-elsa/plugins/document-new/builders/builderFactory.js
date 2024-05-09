import {headingSectionBuilder} from "./headingSectionBuilder.js";
import {defaultSectionBuilder} from "./defaultSectionBuilder.js";

/**
 * 构造器工厂.
 */
export default class BuilderFactory {
    static BUILDERS = {
        default: defaultSectionBuilder(), heading: headingSectionBuilder()
    }

    /**
     * 获取构造器.
     *
     * @param key 键值.
     * @return {{}} 构造器对象.
     */
    static getBuilder(key) {
        if (key.startsWith("heading")) {
            return this.BUILDERS.heading;
        }
        return this.BUILDERS.default;
    }
}