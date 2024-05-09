import {Plugin} from "@ckeditor/ckeditor5-core";

/**
 * Elsa的标题删除类.
 *
 * @author z00559346 张越.
 */
export default class ElsaHeadingDelete extends Plugin {
    static get pluginName() {
        return 'elsaHeadingDelete';
    }

    constructor(editor) {
        super(editor);
    }

    /**
     * 监听前删命令，当发现光标处于heading的最前方时，将 heading 编程paragraph.
     *
     * @override
     */
    init() {
        const model = this.editor.model;
        const deleteCommand = this.editor.commands.get("delete");
        deleteCommand.on("execute", (eventInfo) => {
            const selection = model.document.selection;
            if (selection.isCollapsed && this._isAtStartOfHeading(selection)) {
                this.editor.commands.get("paragraph").execute();

                // 停止事件继续传播.
                eventInfo.stop();
            }
        }, {priority: "highest"});
    }

    _isAtStartOfHeading(selection) {
        const selectedBlocks = Array.from(selection.getSelectedBlocks());
        const position = selection.getFirstPosition();
        return selectedBlocks.length === 1 && selectedBlocks[0].name.startsWith("heading") && position.isAtStart;
    }
}