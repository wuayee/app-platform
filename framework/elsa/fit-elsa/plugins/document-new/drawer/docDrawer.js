import {containerDrawer} from "../../../core/drawers/containerDrawer.js";

/**
 * 结构化文档绘制器.
 *
 * @param docSection 文档对象.
 * @param div dom元素.基于该元素进行绘制.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @return {Window | (WorkerGlobalScope & Window)}
 */
export const docDrawer = (docSection, div, x, y) => {
    const self = containerDrawer(docSection, div, x, y);
    self.type = "doc drawer";

    /**
     * 渲染文本.
     * 只有顶部section才需要渲染文本.
     *
     * @param autoFocus 是否自动聚焦.
     */
    self.renderText = (autoFocus) => {
        if (!docSection.isTopSection()) {
            docSection.getTopSection().drawer.renderText(autoFocus);
            return;
        }

        const data = docSection.getData();
        data.forEach(t => {
            delete t.x;
            delete t.y;
            delete t.width;
            delete t.height;
        });

        const editor = self.getEditor();
        editor.unmount();
        editor.render(data);
        autoFocus && editor.focus();
    };

    /**
     * 渲染toolbar.
     *
     * @param dom toolbar对应的dom元素.
     */
    self.renderToolbar = (dom) => {
        self.getEditor().renderToolbar(dom);
    };

    /**
     * 重写drawFocusFrame，docSection不进行其绘制.
     *
     * @param context 上下文对象.
     */
    self.drawFocusFrame = context => {
    };

    /**
     * 执行操作的时候，使pointer-events生效.
     *
     * @param action 操作.
     * @return {*}
     */
    self.withPointerEvents = (action) => {
        const isDisabled = self.isTextPointerEventsDisabled();
        try {
            isDisabled && self.enableTextPointerEvents();
            return action();
        } finally {
            isDisabled && self.disableTextPointerEvents();
        }
    };

    /**
     * 获取编辑器对象.
     *
     * @return {null|(function(*=): {})|*} 编辑器对象.
     * @override
     */
    self.getEditor = () => {
        if (!docSection.isTopSection()) {
            return null;
        }

        if (self.brush === null || self.brush === undefined) {
            self.brush = docSection.graph.createEditor(docSection);

            // 覆盖dataListener.
            const editableName = docSection.page.id + "_" + docSection.id + "_" + docSection.page.mode;
            const editor = self.brush.editor;
            editor.addDataListener(editableName, (prevData, data) => {
                docSection.page.ignoreReact(() => docSection.parseData(data));
                docSection.reset();
            });

            // 初始化所有事件.
            initEvents(editor, editableName);
        }
        return self.brush;
    };

    const initEvents = (editor, editableName) => {
        // 监听选区变化，实现图文选中效果.
        editor.on(editableName + ":selection:change", (eventInfo, data) => {
            docSection.onDocSelectionChange(data.selectedBlocks);
        });

        // 监听批量删除事件，实现图文一起删除的效果.
        editor.on(editableName + ":deleteBatch", () => {
            docSection.onDocDeleteBatch();
        });

        // 监听拷贝事件，实现图文一起拷贝效果.
        editor.on(editableName + ":copy", (eventInfo, data) => {
            docSection.onDocCopy(data.event, data.anchorElement, docSection);
        });

        // 监听剪切事件，实现图文一起剪切效果.
        editor.on(editableName + ":cut", (eventInfo, data) => {
            docSection.onDocCut(data.event, data.anchorElement);
        });

        // 监听剪切事件，实现图文一起粘贴效果.
        editor.on(editableName + ":paste", (eventInfo, data) => {
            docSection.onDocPaste(data.event, data.anchorElement, docSection);
        });

        // 监听嵌入图形新增事件.
        editor.on(editableName + ":shape:add", (eventInfo, data) => {
            docSection.onEmbedShapeAdd(data);
        });

        // 监听嵌入图形删除事件.
        editor.on(editableName + ":shape:remove", (eventInfo, data) => {
            docSection.onEmbedShapeRemove(data);
        });

        // 监听嵌入图形拷贝事件.
        editor.on(editableName + ":shape:copy", (eventInfo, data) => {
            docSection.onEmbedShapeCopy(data);
        });

        // 监听嵌入图形修改事件.
        editor.on(editableName + ":shape:modify", (eventInfo, data) => {
            docSection.onEmbedShapeModify(data);
        });
    };

    /**
     * @override
     */
    const containerResize = self.containerResize;
    self.containerResize = (width, height) => {
        containerResize.apply(self, [width, height]);
        self.updateIfChange(self.text.style, 'position', "absolute", 'text_position');
        self.updateIfChange(self.text.style, 'top', "0", 'text_top');
    };

    return self;
}