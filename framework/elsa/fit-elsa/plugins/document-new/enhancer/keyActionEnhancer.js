/**
 * 增强键盘事件处理.
 *
 * @param shape 图形对象.
 * @param docSection 文档.
 */
export const enhanceKeyAction = (shape, docSection) => {
    if (!docSection) {
        return;
    }

    /**
     * 当图形处于嵌入状态，并且触发"移动"相关的按键时，不触发操作.
     *
     * @override
     */
    const keyPressed = shape.keyPressed;
    shape.keyPressed = (e) => {
        if (shape.isEmbedded() && isMoveEvent(e)) {
            return false;
        }
        return keyPressed.apply(shape, [e]);
    }

    const isMoveEvent = (e) => {
        return e.key.indexOf("Left") >= 0 || e.key.indexOf("Right") >= 0 || e.key.indexOf("Up") >= 0 || e.key.indexOf("Down") >= 0;
    }

    /**
     * 当粘贴完成时，判断是否是嵌入模式。若是嵌入模式，则需要在文档中插入对应的shape元素。
     *
     * @override
     */
    shape.pasted = () => {
        if (!shape.isEmbedded()) {
            return;
        }
        const editor = docSection.drawer.getEditor();
        const param = {};
        param.externalId = shape.id;
        param.size = {width: shape.width + "px", height: shape.height + "px"};

        // 如果存在光标，则插入光标位置；否则，插入到elementId对应元素的后方.
        !editor.isFocused() && (param.elementId = shape.externalId);
        shape.externalId = editor.format("shape", param, false);
    }
}