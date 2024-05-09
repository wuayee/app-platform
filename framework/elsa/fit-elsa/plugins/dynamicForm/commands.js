import {addCommand, deleteCommand} from "../../core/commands.js";

/**
 * 表单特有的删除命令.
 *
 * @override
 */
const formDeleteCommand = (page, shapes) => {
    shapes = shapes.filter(s => s.shape.get("deletable"));//筛选可以删除的
    const self = deleteCommand(page, shapes);
    self.type = "formDeleteCommand";

    /**
     * @override
     */
    const redo = self.redo;
    self.redo = (host) => {
        redo.apply(self, [host]);
        page.getForm().invalidate();
    };

    /**
     *@override
     */
    const undo = self.undo;
    self.undo = (host) => {
        undo.apply(self, [host]);
        page.getForm().invalidate();
    };

    /**
     * @override
     */
    const execute = self.execute;
    self.execute = host => {
        execute.apply(self, [host]);
        page.getForm().invalidate();
    };

    return self;
};

/**
 * 表单特有的添加命令.
 *
 * @override
 */
const formAddCommand = (page, shapes) => {
    const self = addCommand(page, shapes);
    self.type = "formAddCommand";

    /**
     * undo完成后需要刷新表单.
     *
     * @override
     */
    const undo = self.undo;
    self.undo = (host) => {
        undo.apply(self, [host]);
        page.getForm().invalidate();
    };

    /**
     * redo完成后需要刷新表单.
     *
     * @override
     */
    self.redo = (host) => {
        self.saved.forEach(s => {
            // 这里不需要createNew.
            const shape = host.createShape(s.type, s.x, s.y, s.id);

            /*
             * 这里需要通过undoRemove来找到图形之前的container，并进行设置.
             * 否则，图形找到的container可能和撤销时的container不一致.
             */
            shape.undoRemove(host);
        });
        page.getForm().invalidate();
    };

    return self;
};

export {formDeleteCommand, formAddCommand};