import {EditorUI} from "@ckeditor/ckeditor5-core";
import {InlineEditableUIView} from "@ckeditor/ckeditor5-ui";
import {enablePlaceholder} from "@ckeditor/ckeditor5-engine";

/**
 * 多根节点的编辑器的 UI 组件.
 *
 * @author z00559346 张越.
 */
export class MultiRootEditorUI extends EditorUI {
    constructor(editor, view) {
        super(editor);
        this.view = view;
    }

    /**
     * 初始化.
     */
    init() {
        const view = this.view;
        view.render();

        this.focusTracker.on('change:focusedElement', (evt, name, focusedElement) => {
            for (const editable of this.view.editables) {
                if (editable.element === focusedElement) {
                    this.focusedEditableElement = editable.element;
                }
            }
        });

        this.focusTracker.on('change:isFocused', (evt, name, isFocused) => {
            if (!isFocused) {
                this.focusedEditableElement = null;
                this.editor.model.enqueueChange({isUndoable: false}, writer => {
                    writer.setSelection(null);
                });

                // 当选中range时，将selection设置为null，选中效果不会消失，因此这里调用原生api进行清理.
                const selection = window.getSelection();
                if (selection) {
                    selection.removeAllRanges();
                }
            }
        });

        for (const editable of this.view.editables) {
            this._attach(editable);
        }

        this._initToolbar();
        this.fire('ready');
    }

    /**
     * 在UI中注册可编辑区.
     *
     * @param editableName 编辑区名称.
     * @param editableElement 编辑区元素(dom).
     * @param placeholder 占位提示符.
     */
    registerRoot(editableName, editableElement, placeholder = undefined) {
        const editable = new InlineEditableUIView(this.editor.locale, this.editor.editing.view, editableElement);
        editable.name = editableName;
        this.view.editables.push(editable);
        this.view.registerChild(editable);

        // 必须加这一行，不然在页面中无法展示.
        this._attach(editable, placeholder);
    }

    /**
     * 在UI中注销可编辑区.
     * 参考 {@link #destroy}
     *
     * @param editableName 编辑区名称.
     */
    deregisterRoot(editableName) {
        // 从自定义数组中删除.
        const index = this.view.editables.findIndex(e => e.name === editableName);
        const toBeDeleteEditable = this.view.editables[index];
        toBeDeleteEditable.destroy();
        this.view.editables.splice(index, 1);

        // 从ui view中注销.
        this.view.deregisterChild(toBeDeleteEditable);

        // 在editing view中注销.
        this.editor.editing.view.detachDomRoot(editableName);

        // 从focusTracker中删除元素.
        this.focusTracker.remove(toBeDeleteEditable.element);
    }

    _attach(editable, placeholder = undefined) {
        const editableElement = editable.element;
        this.setEditableElement(editable.name, editableElement);
        editable.bind('isFocused').to(this.focusTracker, 'isFocused', this.focusTracker, 'focusedElement', (isFocused, focusedElement) => {
            if (!isFocused) {
                return false;
            }
            if (focusedElement === editableElement) {
                return true;
            }
            return this.focusedEditableElement === editableElement;
        });
        this.editor.editing.view.attachDomRoot(editableElement, editable.name);
        this._initPlaceholder(editable, placeholder);
    }

    _initPlaceholder(editable, placeholder) {
        const editingView = this.editor.editing.view;
        const editingRoot = editingView.document.getRoot(editable.name);

        let placeholderText = placeholder;
        if (!placeholderText) {
            const configPlaceholder = this.editor.config.get('placeholder');
            if (configPlaceholder) {
                placeholderText = typeof configPlaceholder === 'string' ? configPlaceholder : configPlaceholder[editable.name];
            }
        }

        if (placeholderText) {
            enablePlaceholder({
                view: editingView, element: editingRoot, text: placeholderText, isDirectHost: false, keepOnFocus: true
            });
        }
    }

    /**
     * @inheritDoc
     * @override
     */
    setEditableElement(rootName, domElement) {
        this._editableElementsMap.set(rootName, domElement);
        if (!domElement.ckeditorInstance) {
            domElement.ckeditorInstance = this.editor;
        }
        this.focusTracker.add(domElement);

        /**
         * 只有当未ready时注册的editable才触发keystorke监听，否则，会导致触发多次撤销重做等快捷键.
         */
        if (!this.isReady) {
            this.once('ready', () => {
                if (this.editor.editing.view.getDomRoot(rootName)) {
                    return;
                }
                this.editor.keystrokes.listenTo(domElement);
            });
        }
    }

    /**
     * @inheritDoc
     */
    destroy() {
        const view = this.view;
        const editingView = this.editor.editing.view;
        for (const editable of this.view.editables) {
            editingView.detachDomRoot(editable.name);
        }
        view.destroy();
        super.destroy();
    }

    /**
     * Initializes the editor main toolbar and its panel.
     *
     * @private
     */
    _initToolbar() {
        const editor = this.editor;
        const view = this.view;
        const toolbar = view.toolbar;
        toolbar.fillFromConfig(editor.config.get('toolbar'), this.componentFactory);
        this.addToolbar(view.toolbar);
    }
}