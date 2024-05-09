import {Plugin} from "@ckeditor/ckeditor5-core";

/**
 * 用于触发heading相关的改动事件.
 *
 * @author z00559346 张越.
 */
export default class ElsaHeadingChangeEmitter extends Plugin {
    static get pluginName() {
        return 'ElsaHeadingChangeEmitter';
    }

    init() {
        const editor = this.editor;
        const model = editor.model;
        model.document.on("change:data", () => {
            const changes = model.document.differ.getChanges();

            // name存在，代表是对element的修改.
            const headingChanges = changes.filter(c => c.name && c.name.startsWith("heading") && (c.type === "remove" || c.type === "insert"));

            // 如果不存在heading相关的改动，则直接返回.
            if (!headingChanges || headingChanges.length === 0) {
                return;
            }

            this.fire("heading:change", headingChanges);
        }, {priority: "high"}); // 需要比用户的change事件先执行.
    }
}