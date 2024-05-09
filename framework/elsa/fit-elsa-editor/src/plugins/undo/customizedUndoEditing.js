import {Plugin} from "@ckeditor/ckeditor5-core";

/**
 * 用于提醒外部系统，产生了历史记录.
 *
 * @author z00559346 张越.
 */
export default class CustomizedUndoEditing extends Plugin {
    static get pluginName() {
        return 'ElsaUndoEditing';
    }

    constructor(editor) {
        super(editor);
        this._batchRegistry = new WeakSet();
    }

    /**
     * @inheritDoc
     */
    init() {
        this.listenTo(this.editor.model, "applyOperation", (evt, args) => {
            const operation = args[0];
            if (!operation.isDocumentOperation) {
                return;
            }

            const batch = operation.batch;
            if (!batch.isUndoable || batch.isUndo) {
                return;
            }

            const wasProcessed = this._batchRegistry.has(batch);
            if (wasProcessed) {
                return;
            }

            this._batchRegistry.add(batch);

            this.editor.fire("history:undo:event", operation);
        }, {priority: "highest"});
    }
}