import {
    View,
    LabeledFieldView,
    createLabeledInputText,
    ButtonView,
    submitHandler, FocusCycler
} from "@ckeditor/ckeditor5-ui";
import {icons} from "@ckeditor/ckeditor5-core";
import {FocusTracker, KeystrokeHandler} from "@ckeditor/ckeditor5-utils";

export default class FormView extends View {
    constructor(locale) {
        super(locale);

        // 创建两个输入框.
        this.abbrInputView = this._createInput('Add abbreviation');
        this.titleInputView = this._createInput('Add title');

        // 创建确认按钮.
        this.saveButtonView = this._createButton('Save', icons.check, 'ck-button-save');
        this.saveButtonView.type = 'submit';

        // 创建取消按钮.
        this.cancelButtonView = this._createButton('Cancel', icons.cancel, 'ck-button-cancel');
        this.cancelButtonView.delegate('execute').to(this, 'cancel');

        // 创建子view集合.
        this.childViews = this.createCollection([
            this.abbrInputView,
            this.titleInputView,
            this.saveButtonView,
            this.cancelButtonView
        ]);

        this.focusTracker = new FocusTracker();
        this.keystrokes = new KeystrokeHandler();

        this._focusCycler = new FocusCycler({
            focusables: this.childViews,
            focusTracker: this.focusTracker,
            keystrokeHandler: this.keystrokes,
            actions: {
                focusPrevious: 'shift + tab',
                focusNext: 'tab'
            }
        });

        this.setTemplate({
            tag: 'form',
            attributes: {
                class: ['ck', 'ck-abbr-form'],
                tabindex: '-1'
            },
            children: this.childViews
        });
    }

    render() {
        super.render();
        submitHandler({view: this});

        this.childViews._items.forEach(view => {
            this.focusTracker.add(view);
        });

        this.keystrokes.listenTo(this.element);
    }

    destroy() {
        super.destroy();
        this.focusTracker.destroy();
        this.keystrokes.destroy();
    }

    focus() {
        if (this.abbrInputView.isEnabled) {
            this.abbrInputView.focus();
        } else {
            this.titleInputView.focus();
        }
    }

    _createInput(label) {
        const labeledInput = new LabeledFieldView(this.locale,  createLabeledInputText);
        labeledInput.label = label;
        return labeledInput;
    }

    _createButton(label, icon, className) {
        const button = new ButtonView();
        button.set({
            label,
            icon,
            tooltip: true,
            class: className
        });

        return button;
    }
}