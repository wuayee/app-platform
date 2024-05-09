import {baseSectionBuilder} from "./baseSectionBuilder.js";

/**
 * 标题的构造器.
 * 1、标题构造器不同的地方在于创建标题的时候需要同时创建结构化文档.
 *
 * @return {{}}
 */
export const headingSectionBuilder = () => {
    const self = baseSectionBuilder();

    /**
     * 重写build方法.当构造heading时，需要同时构造结构化文档.
     *
     * @override
     */
    const create = self.create;
    self.create = (element, topSection, parent) => {
        // 先创建结构化文档，此时结构化文档的container是frame.
        // 创建结构化文档, 将heading的container设置为新创建的结构化文档.
        const structuredDoc = self.createSection(topSection, 0, 0, null, parent);
        structuredDoc.docType = "structuredDoc";

        const section = create.apply(self, [element, topSection, structuredDoc]);
        structuredDoc.addSection(section);
        return section;
    }

    /**
     * 标题设置时，需要同时设置其所属的结构化文档.
     *
     * @param section docSection 对象.
     * @param element 编辑器元素.
     * @param context 上下文对象.
     */
    const set = self.set;
    self.set = (section, element, context) => {
        set.apply(self, [section, element, context]);
        section.level = element.level;

        const getStructuredDocParent = () => {
            const containers = context.containers;
            for (let i = containers.length - 1; i >= 0; i--) {
                if (containers[i].level < element.level) {
                    return containers[i];
                }
            }
            return context.topSection;
        }

        // 设置结构化文档.
        // 此时结构化文档的container还不是topSection.
        const structuredDoc = section.getContainer();

        // @马莉亚 2023.1.19 结构化文档再ppt中和文档中共享协作时，避免反复重置外层docsection的宽高坐标属性
        if(!structuredDoc.isTopSection()) {
            // 重新设置结构化文档的属性.
            structuredDoc.level = element.level;
            structuredDoc.docIndex = context.index;
            structuredDoc.x = section.x;
            structuredDoc.y = section.y;
            structuredDoc.height = element.height;
            structuredDoc.width = element.width;
        }

        const structuredParentDoc = getStructuredDocParent();
        if (structuredParentDoc.id !== structuredDoc.id) {
            getStructuredDocParent().addSection(structuredDoc);
        }
    }

    return self;
}