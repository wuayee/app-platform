import {ALIGN, PARENT_DOCK_MODE} from '../../common/const.js';
import {text} from '../../core/rectangle.js';
import {canvasRectangleDrawer} from '../../core/drawers/rectangleDrawer.js';

/**
 * presentation页面上的标题
 * 可以自定义标题名，就像ppt
 * 可以设置agendaindex>0，来取得该presentation的目录某项文本
 * 可以 agendaindex=0,来取得最近高亮目录项的文本
 * 辉子 2021
 */
let caption = (id, x, y, width, height, parent) => {
    let self = text(id, x, y, width, height, parent, canvasRectangleDrawer);
    self.type = "caption";
    self.namespace = "presentation";
    self.text = "[标题]";
    self.enableAnimation = true;
    self.pDock = PARENT_DOCK_MODE.TOP;
    self.height = 100;
    self.fontSize = 48;
    self.hAlign = ALIGN.LEFT;
    self.pad = 15;
    self.agendaIndex = 0;//-1:显示text；n：显示第一个agenda的n项

    /**
     * 如果agendaIndex===0，从该页开始往前一页一页找，找到最近的一个目录，取得该目录高亮项
     * 如果找不到目录，则显示自己的text
     * 如果找到了目录，但没有高亮项，则显示目录第一项
     * huiz 2021
     */
    let getLatestAgendaItem = () => {
        let agenda = self.page.graph.getLatestAgenda(self.page);
        if (agenda === undefined) {
            return self.text;
        } else {
            let lines = agenda.getShapes();
            if (lines.length === 0) {
                return self.text;
            }

            let line = lines.find(l => l.emphasized);
            if (line === undefined) {
                return lines[0].text;
            } else {
                return line.text;
            }
        }
    };

    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if ((e.ctrlKey || e.metaKey || e.shiftKey) && (e.code == "KeyR")) {
            self.initialize();
            self.invalidate();
            return false;
        }
        if (keyPressed.call(self, e) === false) {
            return false;
        }
    };

    self.drawer.drawStatic = (context, x, y) => {
        context.rect(7, self.height - 7, self.width - 4, 7);
        context.fillStyle = self.get("borderColor");
        context.fill();

    };

    let reset = self.reset;
    self.reset = () => {
        self.initialize();
        reset.call();
    };

    let load = self.load;
    self.load = ignoerFilter => {
        load.call(self, p => {
            return self.agendaIndex !== -1 && p === "text";
        });
    };

    /**
     * 国际化情况下要变成相应国家的文字
     * 辉子 2021
     */
    self.getAgendaDisplay = () => "目录";
    self.initialize = () => {
        if (self.agendaIndex === -1) {
            return;
        }//完全自由文本
        let agenda = self.page.shapes.find(s => s.isType('agenda'));
        if (agenda !== undefined) {
            self.text = self.getAgendaDisplay();
            if (!agenda.isNewAgenda) {
                let line = agenda.getShapes().find(s => s.emphasized);
                if (line !== undefined) {
                    self.text = line.text;
                }
            }
            return;
        }

        agenda = self.page.graph.getLatestAgenda(self.page);
        if (agenda === undefined) {
            return;
        }

        let lines = agenda.items;
        if (lines.length === 0) {
            return;
        }

        if (agenda.agenda.isNewAgenda) {
            self.text = lines[self.agendaIndex].text;
        } else {
            let line = lines.find(l => l.emphasized);
            line && (self.text = line.text);
            // for (let i = 0; i < lines.length - 1; i++) {
            //     if (lines[i].emphasized) {
            //         self.text = lines[i].text;
            //         break;
            //     }
            // }
        }
    };

    //----------------------serialize & properties change detection----------------------
    // self.serializedFields.batchAdd("agendaIndex");
    return self;
};

export {caption};