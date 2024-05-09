import {DataApiMixin, Editor} from "@ckeditor/ckeditor5-core";
import {MultiRootEditorUI} from "./MultiRootEditorUI";
import {MultiRootEditorUIView} from "./MultiRootEditorUIView";
import {CKEditorError, mix, setDataInElement} from "@ckeditor/ckeditor5-utils";
import Batch from "@ckeditor/ckeditor5-engine/src/model/batch";
import {enablePlaceholder} from "@ckeditor/ckeditor5-engine";

/**
 * 多根节点编辑器.
 *
 * @author z00559346 张越.
 */
export class MultiRootEditor extends Editor {
    /**
     * 记录各个根节点上注册的监听器.
     *
     * @type {{}}
     * @private
     */
    _dataChangeCallbacks = {};

    /**
     * 记录各个根节点上一次的快照数据.
     *
     * @type {Map<string, {}>}
     * @private
     */
    static _ROOTS_SNAPSHOT_MAP = new Map();

    constructor(sourceElements, config) {
        super(config);

        // 处理初始化数据.
        if (this.config.get('initialData') === undefined) {
            const initialData = {};
            for (const rootName of Object.keys(sourceElements)) {
                const data = sourceElements[rootName].data;
                if (!data) {
                    throw new Error("MultiRootEditor#constructor: editable data must be an array.");
                }
                initialData[rootName] = data;
            }
            this.config.set('initialData', initialData);
        }

        // 为每一个可编辑区创建根节点.
        for (const rootName of Object.keys(sourceElements)) {
            this.model.document.createRoot('$root', rootName);
        }

        // 为每一个可编辑区创建 ui view 元素.
        this.ui = new MultiRootEditorUI(this, new MultiRootEditorUIView(this.locale, this.editing.view, sourceElements));

        // 重写dataController的set方法.
        this._overrideDataControllerSet();
    }

    /**
     * 重写 {@link DataController#set} 方法，只有当set的数据中包含被focused的root时，才清除选区.
     *
     * @private
     */
    _overrideDataControllerSet() {
        const editor = this;
        const dataController = this.data;
        dataController.set = (data, options = {}) => {
            const newData = editor._normalizeData(data);
            if (!dataController._checkIfRootsExists(Object.keys(newData))) {
                throw new CKEditorError('datacontroller-set-non-existent-root', editor);
            }

            // 先改为true，到时候协同有问题的话，再想如何处理.
            // todo@zhangyue 这里设置为false的原因是，在beginEdit中，使用setData的方式设置数据时，不需要进入历史记录.
            const batch = new Batch({isUndoable: false});

            // 用于区分当前操作是否是手动对整个root进行set操作，而不是编辑器触发的操作.
            batch.isSetManually = true;
            editor.model.enqueueChange(batch, writer => {
                // 判断设置时，是否包含了被focused的root，只有包含时，才清除选区.
                if (editor._containsFocusedRoot(Object.keys(newData))) {
                    writer.setSelection(null);
                    writer.removeSelectionAttribute(editor.model.document.selection.getAttributeKeys());
                }

                // 重新设置数据.
                for (const rootName of Object.keys(newData)) {
                    const modelRoot = editor.model.document.getRoot(rootName);
                    writer.remove(writer.createRangeIn(modelRoot));
                    writer.insert(dataController.parse(newData[rootName], modelRoot), modelRoot, 0);
                }
            });
        }
    }

    _containsFocusedRoot(rootNames) {
        return this.ui.focusedEditableElement && rootNames.indexOf(this.ui.focusedEditableElement.name) >= 0;
    }

    _normalizeData(data) {
        if (typeof data !== "string") {
            return data;
        }
        const result = {};
        result.main = data;
        return result;
    }

    /**
     * 设置占位符.
     *
     * @param rootName 根节点名称.
     * @param placeholder 占位符字符串.
     */
    setPlaceholder(rootName, placeholder) {
        if (!rootName || !placeholder) {
            return;
        }
        const index = this.ui.view.editables.findIndex(e => e.name === rootName);
        const editable = this.ui.view.editables[index];
        const editingView = this.editing.view;
        const editingRoot = editingView.document.getRoot(editable.name);
        enablePlaceholder({
            view: editingView, element: editingRoot, text: placeholder, isDirectHost: false, keepOnFocus: true
        });
    }

    /**
     * 添加数据监听器.
     *
     * @param rootName 根节点名称.
     * @param callback 回调.
     */
    addDataListener(rootName, callback) {
        const editor = this;

        // 如果已存在，则先停止之前的监听器.
        if (editor._dataChangeCallbacks[rootName]) {
            editor.removeDataListener(rootName);
        }

        // 定义回调函数.
        editor._dataChangeCallbacks[rootName] = (eventInfo, batch) => {
            const prevData = MultiRootEditor._ROOTS_SNAPSHOT_MAP.get(rootName);

            // 这里必须要变成异步，否则执行时view还没刷新，获取到的dom还是老的dom，计算出来的坐标及宽高都不对.
            Promise.resolve().then(() => {
                // * 注意 *
                // 将图形撤销至无内容时，如果不加上trim: "none"，那么trim的默认值是"empty"，此时会返回空字符串
                // 那么shape.text就会变成空字符串，如果此时删除图形，再撤销使图形恢复时，因为shape.text是空字符串，
                // 就会导致生成一个新的paragraph.此时，编辑器的撤销操作就对该root无效了.
                const data = editor.getData({rootName, trim: "none"});
                callback(prevData, data, batch.isSetManually, batch.isUndo);
                MultiRootEditor._ROOTS_SNAPSHOT_MAP.set(rootName, data);
            });
        }

        // 注册监听.
        editor.on("root:change:" + rootName, editor._dataChangeCallbacks[rootName]);
    }

    /**
     * 取消监听.
     *
     * @param rootName 根节点名称.
     */
    removeDataListener(rootName) {
        // 若存在，才执行取消操作.
        if (this._dataChangeCallbacks[rootName]) {
            this.off("root:change:" + rootName, this._dataChangeCallbacks[rootName]);
        }
    }

    /**
     * 动态注册可编辑区.
     *
     * @param rootName 编辑区的名称.
     * @param editableDom 编辑区对应的dom元素.
     */
    registerRoot(rootName, editableDom) {
        // 如果已存在，则先销毁，再重新注册.
        if (this.model.document.getRoot(rootName)) {
            this.removeDataListener(rootName);
            this.deregisterRoot(rootName);
        }

        // 创建对应的根节点.
        this.model.document.createRoot("$root", rootName);

        // 注册 ui view 元素.
        this.ui.registerRoot(rootName, editableDom);
    }

    /**
     * 动态注销可编辑区.
     *
     * @param rootName 可编辑区名称.
     */
    deregisterRoot(rootName) {
        if (!this.model.document.getRoot(rootName)) {
            return;
        }

        // 在model中删除根节点.
        this.model.document.roots.remove(rootName);

        // 在UI中删除对应的编辑区view.
        this.ui.deregisterRoot(rootName);
    }

    /**
     * 聚焦编辑器.
     */
    focus() {
        const isFocused = this.ui.focusTracker.isFocused;
        if (isFocused) {
            this.ui.focusTracker.set("isFocused", false);
        }
        this.editing.view.focus();
    }

    /**
     * @inheritDoc
     */
    destroy() {
        const data = {};
        const editables = {};
        const editablesNames = Array.from(this.ui.getEditableElementsNames());

        for (const rootName of editablesNames) {
            data[rootName] = this.getData({rootName});
            editables[rootName] = this.ui.getEditableElement(rootName);
        }

        this.ui.destroy();
        return super.destroy()
            .then(() => {
                for (const rootName of editablesNames) {
                    setDataInElement(editables[rootName], data[rootName]);
                }
            });
    }
}

mix(MultiRootEditor, DataApiMixin);

