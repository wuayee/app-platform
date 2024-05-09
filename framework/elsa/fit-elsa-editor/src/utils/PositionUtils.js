import {env} from "@ckeditor/ckeditor5-utils";

/**
 * 坐标相关工具类.
 *
 * @author z00559346 张越.
 */
export default class PositionUtils {
    /**
     * 两坐标转换为viewRange.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param editor 编辑器对象.
     * @return {null|module:engine/view/range~Range|null|*}
     */
    static toViewRange(x, y, editor) {
        let domRange;
        if (document.caretRangeFromPoint && document.caretRangeFromPoint(x, y)) {
            domRange = document.caretRangeFromPoint(x, y);
        } else {
            const position = document.caretPositionFromPoint(x, y)
            if (position) {
                domRange = document.createRange()
                domRange.setStart(position.offsetNode, position.offset)
                domRange.setEnd(position.offsetNode, position.offset)
            }
        }

        if (domRange) {
            return editor.editing.view.domConverter.domRangeToView(domRange);
        }

        return null;
    }

    /**
     * 将坐标转换为modelRange.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param editor 编辑器对象.
     * @return {null|*|module:engine/model/range~Range}
     */
    static toModelRange(x, y, editor) {
        const viewTargetRange = this.toViewRange(x, y, editor);
        if (!viewTargetRange) {
            return null;
        }

        const model = editor.model;
        const mapper = editor.editing.mapper;
        const targetViewPosition = viewTargetRange.start;
        const targetModelPosition = targetViewPosition ? mapper.toModelPosition(targetViewPosition) : null;
        const range = model.schema.getNearestSelectionRange(targetModelPosition, env.isGecko ? 'forward' : 'backward');
        return range ? range : this._toModelRangeOnAncestorObject(editor, targetModelPosition.parent);
    }

    static _toModelRangeOnAncestorObject(editor, element) {
        const model = editor.model;
        let parent = element;
        while (parent) {
            if (model.schema.isObject(parent)) {
                return model.createRangeOn(parent);
            }
            parent = parent.parent;
        }
        return null;
    }
}