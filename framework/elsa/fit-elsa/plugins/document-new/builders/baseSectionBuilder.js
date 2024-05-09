import {Z_INDEX_OFFSET} from "../../../common/const.js";

/**
 * 基础构造类.
 *
 * @return {{}}
 */
export const baseSectionBuilder = () => {
    const self = {};

    /**
     * 根据编辑器元素构建 {@link #docSection} 对象.
     *
     * @param element 编辑器元素.
     * @param topSection 顶层section对象.
     * @param parent 父节点.
     * @return {*}
     */
    self.create = (element, topSection, parent) => {
        const position = topSection.calcPosition(element);
        return self.createSection(topSection, position.x, position.y, element.attributes.id, parent);
    }

    /**
     * 创建 {@link #docSection} 对象.创建时，需要将 {@link #docSection} 置于其他类型的图形之下.
     *
     * @param topSection 顶层文档.
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param id 唯一标识.
     * @param parent 父节点.
     */
    self.createSection = (topSection, x, y, id = undefined, parent) => {
        const page = topSection.page;
        const shape = page.createNew("docSection", x, y, id, null, parent);
        const index = page.shapes.indexOf(shape) - 1;
        let targetIndex = index;
        try {
            while (!page.shapes[targetIndex].isTypeof("docSection")) {
                targetIndex--;
            }
        } catch (e) {
            console.log(e);
        }
        if (targetIndex !== index) {
            page.moveIndexAfter(shape, targetIndex + Z_INDEX_OFFSET);
        }
        return shape;
    }

    /**
     * 对 {@link #docSection} 进行属性设置.
     *
     * @param docSection 待设置的docSection的对象.
     * @param element 编辑器元素.
     * @param context 上下文对象.
     */
    self.set = (docSection, element, context) => {
        const position = context.topSection.calcPosition(element);
        docSection.x = position.x;
        docSection.y = position.y;
        docSection.docIndex = context.index;
        docSection.height = element.height;
        docSection.width = element.width;
        docSection.docType = element.name;
        docSection.text = element;
    }

    return self;
}