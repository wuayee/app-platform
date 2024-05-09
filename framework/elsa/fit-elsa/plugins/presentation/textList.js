import {ALIGN, DOCK_MODE, PAGE_MODE} from '../../common/const.js';
import {container} from '../../core/container.js';
import {text} from '../../core/rectangle.js';
import {canvasRectangleDrawer} from '../../core/drawers/rectangleDrawer.js';

/**
 * text lines in presentation
 * the difference betwwen framelist and textlist is that framelist has caption, and textlist is more like text in powerpoint
 * 辉子 2021-04-07
 */
let frameList = (id, x, y, width, height, parent) => {
    let self = container(id, x, y, width, height, parent);
    self.type = "frameList";
    self.namespace = "presentation";
    self.text = "Fit Frame";
    self.fontSize = 28;
    self.width = 800;
    self.height = 500;
    self.itemSpace = 0;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.hAlign = ALIGN.LEFT;
    self.lineHeight = 2;
    self.backColor = self.focusBackColor = "transparent";
    //self.style = "sdfgasdfsdafasdf(shape)";

    //-------------method--------------
    let styleAction = undefined;
    self.applyStyle = () => {
        if (self.style === "" || self.style === undefined) {
            styleAction = textList(self);
        } else {
            styleAction = eval(self.style + "(self);");
        }
    };

    self.getStyle = () => {
        if (styleAction === undefined) {
            self.applyStyle();
        }
        return styleAction;
    };

    self.childAllowed = child => child.isType('frameLine');
    self.addLine = text => {
        let line = self.page.createNew("frameLine", self.x, self.y);
        line.text = text;
        line.container = self.id;
        return line;
    };
    let invalidate = self.invalidate;
    self.invalidate = () => {
        invalidate.call(self);
        let shapes = self.getShapes();
        if (shapes.length === 0) {
            return;
        }
        let height = self.getShapes().max(s => s.y + s.height) - self.y + 5;
        if (height !== self.height) {
            self.height = height;
            self.invalidate();
        }
    };

    let styles = ["textList", "section", "agendaColor", "agendaGray"];
    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if (keyPressed.call(self, e) === false) {
            return false;
        }

        if ((e.ctrlKey || e.metaKey || e.shiftKey) && (e.code == "KeyM")) {
            if (self.style === undefined) {
                self.style = styles[1];
            } else {
                let index = styles.indexOf(self.style) + 1;
                if (index === styles.length) {
                    index = 0;
                }
                self.style = styles[index];
            }
        }
    };

    let get = self.get;
    self.get = field => {
        let value = get.call(self, field);
        if (field === "borderWidth" && self.page.mode === PAGE_MODE.CONFIGURATION && value === 0 && self.container === self.page.getFrame().id) {
            value = 1;
        }
        return value;
    };

    //-----------initializing------------------
    self.initialize = () => {
        self.addLine("text1");
        self.addLine("text2");
        self.addLine("text3");
    };

    //--------------------------serialization & detection------------------------------
    self.serializedFields.batchAdd("style");

    self.addDetection(["fontSize", "lineHeight", "backColor", "fontColor",
        "headColor"], (property, value, preValue) => {
        self.getShapes().forEach(s => {
            if (s[property] === preValue) {
                s[property] = value;
            }
        })

    });

    self.addDetection(["style"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.applyStyle();
        self.invalidate();
    });
    //--------------------------------------------------------------------------------------

    //--------------------------------------styles------------------------------------------
    let emptyStyle = () => {
        return {
            drawStatic: (context, x, y, shape) => {
                let height = Math.round(shape.fontSize * 1.1);
                let number = shape.getBulletIndex();
                context.beginPath();
                context.fillStyle = shape.get("headColor");
                context.font = "normal bold " + height + "px comforta";//comforta
                let width = context.measureText(number).width;
                context.fillText(shape.getBulletIndex(), x + shape.lineHeight * shape.fontSize / 2 - width / 2, y + (shape.lineHeight + 0.8) * shape.fontSize / 2);
                //context.fillText(number, x+shape.height/2-width/2, y+height);
                context.strokeStyle = "green";

            }, drawDynamic: (context, x, y, shape) => {
            }
        }
    };
    /**
     * textlist is from framelist without border and caption
     * 辉子 2021-04-07
     */
    let textList = list => {
        let self = list
        self.text = "";
        self.itemPad = [6, 6, 6, 6];
        self.hideText = true;
        //self.dashWidth = 5;
        self.borderColor = "silver";
        self.borderWidth = 0;
        self.bullet = "fitStatic";

        let style = emptyStyle();
        let drawStatic = style.drawStatic;
        style.drawStatic = (context, x, y, shape) => {
            shape.padLeft = shape.fontSize * 2.5;
            drawStatic.call(style, context, x, y, shape)
        };

        return style;
    };

    let section = list => {
        let self = list;
        self.bullet = "fitDynamic";
        self.itemPad = [0, 0, 26, 0];
        self.hideText = false;
        return emptyStyle();
    };

    let colors = ["steelblue", "darkorange", "OLIVE", "teal", "darkred", "green", "#EAC117", "red", "gray", "lightblue",
        "#CD7F32", "steelblue"];
    let drawColor = (context, x, y, shape) => {
        shape.headColor = "whitesmoke";
        const color = colors[shape.getBulletIndex() % colors.length];
        const height = shape.fontSize * 1.7;
        shape.padLeft = shape.fontSize * 2.5;

        context.fillStyle = shape.getBorderColor();
        let g = context.createLinearGradient(x + shape.fontSize / 2, 0, x + shape.width, 0);
        g.addColorStop(0, "rgba(222,222,222,1)");
        g.addColorStop(1, "rgba(222,222,222,0)");
        context.fillStyle = g;
        let x1 = x + shape.lineHeight * shape.fontSize / 2,
            y1 = y + shape.lineHeight * shape.fontSize / 2,
            r = shape.lineHeight * shape.fontSize * 0.8 / 2;
        // context.rect(x + shape.height / 2, y + shape.height / 2 - height / 2, shape.width - shape.height, height);
        context.rect(x1, y1 - shape.fontSize * 0.8, shape.width, shape.fontSize * 1.5);
        context.fill();
        context.beginPath();
        // context.arc(x + shape.height / 2, y + shape.height / 2, 1.4 * shape.fontSize / 2 + 4, 0, 2 * Math.PI);
        context.arc(x1, y1, r, 0, 2 * Math.PI);
        context.strokeStyle = "whitesmoke";
        context.lineWidth = 5;
        context.stroke();
        context.fillStyle = color;
        context.fill();
        //context.fillRect(x+2, y, shape.height-5, shape.height-5);
    };
    let drawGray = (context, x, y, shape) => {
        shape.headColor = "dimgray";
        const height = shape.fontSize * 1.7;
        shape.padLeft = shape.fontSize * 2.5;

        context.fillStyle = shape.getBorderColor();
        let g = context.createLinearGradient(x + shape.fontSize / 2, 0, x + shape.width, 0);
        g.addColorStop(0, "rgba(222,222,222,0)");
        g.addColorStop(0.3, "rgba(222,222,222,1)");
        g.addColorStop(1, "rgba(222,222,222,0)");
        context.fillStyle = g;
        let x1 = x + shape.lineHeight * shape.fontSize / 2,
            y1 = y + shape.lineHeight * shape.fontSize / 2,
            r = shape.lineHeight * shape.fontSize * 0.8 / 2;
        // context.rect(x + shape.height / 2, y + shape.height / 2 - height / 2, shape.width - shape.height, height);
        context.rect(x1, y1 - shape.fontSize * 0.8, shape.width, shape.fontSize * 1.5);
        context.fill();
        context.beginPath();
        // let x1 = x + shape.height / 2, y1 = y + shape.height / 2, r = 1.4 * shape.fontSize / 2 + 4;
        context.arc(x1, y1, r, 0, 2 * Math.PI);
        context.strokeStyle = "whitesmoke";
        context.lineWidth = 5;
        context.stroke();
        let g2 = context.createRadialGradient(x1, y1, 1, x1, y1, r);
        g2.addColorStop(0, "rgba(255,255,255,0)");
        g2.addColorStop(1, "silver");
        context.fillStyle = g2;
        context.fill();
        //context.fillRect(x+2, y, shape.height-5, shape.height-5);
    };

    let agendaColor = list => {
        let self = list;
        self.bullet = "fitAgenda";
        self.borderWidth = 0;
        self.itemPad = [6, 6, 6, 6];
        self.hideText = true;
        self.fontSize = 40;
        self.lineHeight = 2.5;
        //self.headColor = "whitesmoke";

        let style = emptyStyle();
        let drawStatic = style.drawStatic;
        style.drawStatic = (context, x, y, shape) => {
            drawColor(context, x, y, shape);
            drawStatic.call(style, context, x, y, shape)
        };
        style.drawDynamic = (context, x, y, shape) => {
        };
        return style;
    };
    let agendaGray = list => {
        let self = list;
        self.bullet = "fitAgenda";
        self.borderWidth = 0;
        self.itemPad = [6, 6, 6, 6];
        self.hideText = true;
        self.fontSize = 40;
        self.fontColor = "gray";
        self.lineHeight = 2.5;
        //self.headColor = "dimgray";

        let style = emptyStyle();
        let drawStatic = style.drawStatic;
        style.drawStatic = (context, x, y, shape) => {
            if (shape.emphasized) {
                drawColor(context, x, y, shape);
            } else {
                drawGray(context, x, y, shape);
            }
            drawStatic.call(style, context, x, y, shape)
        };
        return style;
    };
    return self;
};

/**
 * single text line in frame list
 * 辉子 2021-04-07
 */
let frameLine = (id, x, y, width, height, parent) => {
    let self = text(id, x, y, width, height, parent, canvasRectangleDrawer);
    self.type = "frameLine";
    self.namespace = "presentation";
    self.autoHeight = true;
    self.autoText = true;
    self.hAlign = ALIGN.LEFT;
    //self.borderWidth =1;
    self.pad = 0;
    self.padLeft = 36;
    self.getBulletIndex = () => self.getContainer().getShapes().indexOf(self) + 1;
    self.initConnectors = () => self.connectors = [];

    //----------------------method-----------------------
    self.dragTo = (position) => {
        self.getContainer().dragTo(position);
    };

    self.drawer.drawStatic = (context, x, y) => {
        let container = self.getContainer()
        container.getStyle && container.getStyle().drawStatic(context, x, y, self);
    };

    const OFFSET = 50, RATE = 1.5;
    let pos = -OFFSET * RATE;
    self.drawer.drawDynamic = (context, x, y) => {
        if (!self.emphasized) {
            return;
        }
        let g = context.createLinearGradient(pos - self.width / 2, self.height / 2, pos - self.width / 2 + OFFSET, self.height / 2)
        g.addColorStop(0, "rgba(255,255,255,0)");
        g.addColorStop(0.4, "rgba(255,255,255,0.7)");
        g.addColorStop(0.5, "rgba(255,255,255,0.8)");
        //g.addColorStop(0.5, "red");
        g.addColorStop(0.6, "rgba(255,255,255,0.7)");
        g.addColorStop(1, "rgba(255,255,255,0)");
        context.fillStyle = g;
        context.beginPath();
        context.rect(-self.width / 2, -self.height / 2, self.width, self.height);
        context.fill();
        self.getContainer().getStyle().drawDynamic(context, x, y, self);
        pos += 1;
        while (pos > (self.width + OFFSET * RATE)) pos = -OFFSET * RATE;
    };

    // let drawDynamic = self.drawer.drawDynamic;
    // self.drawer.drawDynamic = (context, x, y) => {
    //     drawDynamic.call(self.drawer, context, x, y);
    //     self.getContainer().getStyle().drawDynamic(context, x, y, self);
    // };

    let invalidateAlone = self.invalidateAlone;
    self.invalidateAlone = () => {
        self.focusBackColor = self.backColor;
        let container = self.getContainer();
        if (container.style !== "" && container.style !== undefined) {
            self.lineHeight = container.lineHeight;
            self.fontSize = container.fontSize;
            self.lineHeight = container.lineHeight;
            self.backColor = container.backColor;
            self.fontColor = container.fontColor;
            self.headColor = container.headColor;
        }
        invalidateAlone.call(self);
    };

    self.textSizeChanged = () => self.getContainer().invalidate();

    self.drawer.drawFocusFrame = context => {
    };

    self.serializedFields.batchDelete("lineHeight", "fontSize", "lineHeight", "backColor", "fontColor", "headColor", "focusBackColor");//这些属性跟随父亲，不序列化

    //--------------------events-----------------
    self.dbClick = () => {
        if (!self.getSelectable()) {
            return;
        }
        self.beginEdit();
    };

    let beginEdit = self.beginEdit;
    self.beginEdit = (x, y) => {
        beginEdit.call(self, x, y);
        let edit = document.getElementById(EDITOR_NAME);
        if (edit === undefined || edit === null) {
            return;
        }
        let editRect = self.getEditRect();
        edit.style.paddingLeft = "2px";
        edit.style.left = editRect.x + self.page.x + self.getPadLeft() - 2 + "px";
        edit.style.height = "";
    };

    // let setCursorPosition = pos => {
    //     let editor = document.getElementById(EDITOR_NAME);
    //     let newRange = document.createRange();
    //     let nowRanges = window.getSelection();

    //     let node = editor.childNodes[0];
    //     if (node === undefined || node === null) return;
    //     newRange.setStart(node, pos);
    //     newRange.collapse(true);
    //     nowRanges.removeAllRanges();
    //     nowRanges.addRange(newRange);
    // }
    self.editing = (editor, e) => {
        //backspace, to join the previous line if back the first charactor
        if (e.keyCode === 8 && editor.keydownText === editor.innerText) {// && editor.innerText.trim() === ""
            let idx = self.getBulletIndex() - 1;
            if (idx > 0) {
                self.remove();
                self.endEdit();
                let line = self.getContainer().getShapes()[idx - 1];
                line.text += self.text;
                line.beginEdit();
                line.getContainer().invalidate();
            }
            return;
        }
        //delete, to join next line if delete the end charactor
        if (e.keyCode === 46 && editor.keydownText === editor.innerText) {
            let idx = self.getBulletIndex() - 1;
            if (idx < self.getContainer().getShapes().length - 1) {
                let line = self.getContainer().getShapes()[idx + 1];
                line.remove();
                self.endEdit();
                let pos = self.text.length;
                self.text += line.text;
                self.beginEdit();
                editor.setCaretPosition(pos);
                self.getContainer().invalidate();
            }
            return;
        }
        //left,up
        if ((e.keyCode === 37 || e.keyCode === 38) && editor.keyDownCursorPosition === editor.keyUpCursorPosition) {
            let idx = self.getBulletIndex() - 1;
            if (idx > 0) {
                let line = self.getContainer().getShapes()[idx - 1];
                self.endEdit();
                line.beginEdit();
            }
        }
        //right,down
        if ((e.keyCode === 39 || e.keyCode === 40) && editor.keyDownCursorPosition === editor.keyUpCursorPosition) {
            let idx = self.getBulletIndex() - 1;
            if (idx < self.getContainer().getShapes().length - 1) {
                let line = self.getContainer().getShapes()[idx + 1];
                self.endEdit();
                line.beginEdit();
                editor.setCaretPosition(0);
            }
        }

        //enter to add a new line
        if (e.keyCode === 13) {
            let texts = editor.innerText.split("\n");
            self.endEdit();
            self.text = texts[0];
            //self.invalidate();
            let line = self.getContainer().addLine(texts[1]);
            //if(line.text === "") line.text = " ";
            self.page.moveIndexAfter(line, self.getIndex());
            self.getContainer().invalidate();
            line.beginEdit();
            //if(line.text.trim()==="")setCursorPosition(0);
            return;
        }
        self.text = editor.innerText;
        self.render();
        self.getContainer().invalidate();
    };

    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        keyPressed.call(self, e);
        if (self.emphasized) {
            self.getContainer().getShapes().filter(s => s.id !== self.id).forEach(s => {
                s.emphasized = false;
                s.invalidate();
            });
        }
    };
    return self;
};

/**
 * presentation agenda
 * presenation中第一个agenda默认为目录数据源，为彩色目录
 * 第n个目录默认为第一个目录的具体某项，为灰度目录，第n条会亮显
 * 辉子 2021
 */
let agenda = (id, x, y, width, height, parent) => {
    let self = frameList(id, x, y, width, height, parent);
    self.type = "agenda";
    self.isNewAgenda = true;
    self.getAgendaStyle = () => self.agendaStyle === undefined ? "agendaColor" : self.agendaStyle;//可扩展目录绘制
    self.getAendaItemStyle = () => self.agendaItemStyle === undefined ? "agendaGray" : self.agendaItemStyle;
    self.style = self.getAgendaStyle();

    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if ((e.ctrlKey || e.metaKey || e.shiftKey) && (e.code == "KeyM")) {
            self.isNewAgenda = !self.isNewAgenda;
            return false;
        }
        if ((e.ctrlKey || e.metaKey || e.shiftKey) && (e.code == "KeyR")) {
            let agenda = self.initialize();
            self.moveTo(agenda.x, agenda.y);//手工刷新，连坐标一起跟随
            self.resize(agenda.width, agenda.height);
            self.invalidate();
            return false;
        }
        if (keyPressed.call(self, e) === false) {
            return false;
        }
    };

    let reset = self.reset;
    self.reset = () => {
        if (!self.isNewAgenda) {
            self.initialize();
        }
        reset.call();
    };

    //----------------------serialize & properties change detection----------------------
    self.serializedFields.batchAdd("isNewAgenda");
    self.addDetection(["isNewAgenda"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }

        self.style = self.isNewAgenda ? self.getAgendaStyle() : self.getAendaItemStyle();
        self.applyStyle();
        //self.initialize();
        self.invalidate();
    });

    let initialize = self.initialize;
    self.initialize = () => {
        self.graph.collaboration.mute = true;
        let agenda = self.page.graph.getLatestAgenda(self.page);
        if (agenda === undefined) {//本presentation中放下的第一个agenda
            if (self.getShapes().length === 0) {
                initialize.call(self);
            }
            self.isNewAgenda = true;
        } else {//有agenda，继承agenda内容
            let lines = agenda.items;
            if (lines.length === 0) {
                return;
            }
            let value = self.page.disableReact;
            self.page.disableReact = false;
            self.isNewAgenda = false;
            //fill lines
            self.getShapes().forEach(s => s.remove());
            lines.forEach(l => {
                let newline = self.addLine(l.text);
                newline.id = l.id;
            });
            //set emphasized item
            if (agenda.agenda.isNewAgenda) {
                self.getShapes()[0].emphasized = true;
            } else {
                for (let i = 0; i < lines.length - 1; i++) {
                    if (lines[i].emphasized) {
                        self.getShapes()[i + 1].emphasized = true;
                        break;
                    }
                }
            }
            self.page.disableReact = value;
            self.graph.collaboration.mute = false;
            return agenda.agenda;

        }
        self.graph.collaboration.mute = false;
    };
    return self;
};

export {frameList, frameLine, agenda};


