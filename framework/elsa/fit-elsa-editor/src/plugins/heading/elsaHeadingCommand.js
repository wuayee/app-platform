import {Command} from "@ckeditor/ckeditor5-core";
import {first} from "@ckeditor/ckeditor5-utils";

/**
 * Elsa自定义标题 {@link #Command}.
 *
 * @author z00559346 张越.
 */
export default class ElsaHeadingCommand extends Command {
    constructor(editor, modelElements) {
        super(editor);
        this.modelElements = modelElements;
    }

    refresh() {
        const block = first(this.editor.model.document.selection.getSelectedBlocks());
        this.value = !!block && this.modelElements.includes(block.name) && block.name;
        this.isEnabled = !!block && this.modelElements.some(heading => this._checkCanBecomeHeading(block, heading, this.editor.model.schema));
    }

    execute(options) {
        const model = this.editor.model;
        const document = model.document;
        const targetModelElement = options.value;
        model.change(writer => {
            Array.from(document.selection.getSelectedBlocks())
                .filter(block => this._checkCanBecomeHeading(block, targetModelElement, model.schema))
                .filter(block => !block.is("element", targetModelElement))
                .forEach(block => writer.rename(block, targetModelElement));
        });
    }

    /**
     * 基于elsa的机制判断，heading之间的是否能相互转换，以及paragraph和heading之间是否能相互转换.
     *
     * @param block 当前block元素对象.
     * @param heading 目标heading.
     * @param schema 看预定义的模式是否匹配.
     * @return {boolean} true/false.
     * @private
     */
    _checkCanBecomeHeading(block, heading, schema) {
        if (!schema.checkChild(block.parent, heading) || schema.isObject(block)) {
            return false;
        }

        // 如果目标heading是以及标题，则直接返回true.
        if (heading === "heading1") {
            return true;
        }

        // 获取目标标题的level.
        const targetLevel = parseInt(heading.replace("heading", ""));

        // 往前遍历，遇到title遍历结束.
        // 1、如果是heading，判断heading的level是否和目标level一样或只比目标level小1，如果是，则返回true，说明可以转换
        // 2、如果条件1不满足，并且level小于目标level，则跳出循环
        // 3、返回false.
        for (let prev = block.previousSibling; prev.name !== "titleBox"; prev = prev.previousSibling) {
            if (prev.name.startsWith("heading")) {
                const prevLevel = this._getLevel(prev);
                if (prevLevel === targetLevel || prevLevel === targetLevel - 1) {
                    return true;
                }
                if (prevLevel < targetLevel) {
                    break;
                }
            }
        }

        return false;
    }

    _getLevel(block) {
        return parseInt(block.name.replace("heading", ""));
    }
}