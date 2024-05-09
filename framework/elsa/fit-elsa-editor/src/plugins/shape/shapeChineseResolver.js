import {Command} from "@ckeditor/ckeditor5-core";

/**
 * 处理中文对图形的影响.
 *
 * @author z00559346 张越.
 */
export default class ShapeChineseResolver extends Command {
    /**
     * 当输入中文并且还未输入完成时，需要重新计算shape的坐标.
     *
     * @inheritDoc
     * @override
     */
    init() {
        const editor = this.editor;
        editor.editing.view.document.on("beforeinput", (evt, data) => {
            if (data.isComposing && data.inputType === "insertCompositionText") {
                data.targetRanges && data.targetRanges.filter(r => r).forEach(range => {
                    editor.fire("root:change:" + range.start.root.rootName, {isUndo: false, isSetManually: false});
                });
            }
        });
    }
}