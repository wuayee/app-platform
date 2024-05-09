import {baseSectionBuilder} from "./baseSectionBuilder.js";

/**
 * 默认 {@link #docSection} 构造器.
 *
 * @return {{}}
 */
export const defaultSectionBuilder = () => {
    const self = baseSectionBuilder();

    /**
     * 默认 {@link #docSection} 对象需要设置容器.
     *
     * @param section docSection 对象.
     * @param element 编辑器元素.
     * @param context 上下文对象.
     */
    const set = self.set;
    self.set = (section, element, context) => {
        set.apply(self, [section, element, context]);
        const containers = context.containers;
        const container = containers.length > 0 ? containers[containers.length - 1] : context.topSection;
        container.addSection(section);
    }

    return self;
}