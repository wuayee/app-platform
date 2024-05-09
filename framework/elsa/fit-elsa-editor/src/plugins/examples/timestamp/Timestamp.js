import {Plugin} from "@ckeditor/ckeditor5-core";
import {ButtonView} from '@ckeditor/ckeditor5-ui';

export default class Timestamp extends Plugin {
    init() {
        console.log("Timestamp was initialized.");
        const editor = this.editor;

        editor.ui.componentFactory.add("timestamp", () => {
            const button = new ButtonView();
            button.set({
                label: "Timestamp", withText: true
            });

            button.on("execute", () => {
                const now = new Date();
                editor.model.change(writer => {
                    editor.model.insertContent(writer.createText(now.toString()));
                });
            });

            return button;
        });
    }
}