import { sleep, uuid } from '../../common/util.js';
import { ANIMIATION_ACTION, PAGE_MODE } from '../../common/const.js';

/**
 * shape in animation
 * 辉子 2021
 */
let presentationActions = { methods: {}, currentThread: "", currentPage: "" };
presentationActions.animate = async function (s, methodName, animationCode, finalizeCode) {
    let self = this;
    self.currentThread = uuid();
    let methodFunc = presentationActions.methods[methodName];
    if (methodFunc === undefined) {
        return;
    }
    let method = methodFunc(s, self.currentThread, self.currentPage.id);
    // if (s !== undefined) {
    //     s.visible = true;
    //     s.invalidate();
    // }
    await method.init();
    while (method.condition() && self.currentPage.graph.getMode() !== PAGE_MODE.CONFIGURATION) {
        let result = method.animate(animationCode);
        if (result === -1) {
            break;
        }
        await sleep(10);
    }
    method.finalize(finalizeCode);
};
presentationActions.newMethod = (shape, action) => {
    let self = {};
    self.shape = shape;
    self.init = () => {
    };
    self.condition = () => {
    };
    self.animate = () => {
    };
    self.finalize = () => {
        shape.drawer.parent.style.opacity = self.action === ANIMIATION_ACTION.IN ? shape.globalAlpha : "0";
        shape.animationHide = self.action !== ANIMIATION_ACTION.IN;
        // shape.visible = self.action === ANIMIATION_ACTION.IN;
        // shape.render();
        //  shape.reset();
    }
    self.action = action;
    return self;
}

//------------------------------in/out actions---------------------------------
/**
 * fade in:淡入效果
 * 辉子
 */
presentationActions.methods.fadeIn = s => {
    let self = presentationActions.newMethod(s, ANIMIATION_ACTION.IN);
    self.init = () => {
        s.drawer.parent.style.opacity = "0";
    };
    let opacity = 0;
    self.condition = () => opacity < 1;
    self.animate = () => {
        opacity += 0.02;
        s.drawer.parent.style.opacity = opacity;
        return opacity;
    };
    return self;
};

/**
 * fade out:淡出效果
 * 辉子
 */
presentationActions.methods.fadeOut = s => {
    let self = presentationActions.newMethod(s, ANIMIATION_ACTION.OUT);
    let opacity = 1;
    self.condition = () => opacity > 0;
    self.animate = () => {
        opacity -= 0.02;
        s.drawer.parent.style.opacity = opacity;
        return opacity;
    };
    return self;
};

//========================end of fade in and fade out======================================

presentationActions.methods.flyIn = s => {
    let self = presentationActions.methods.fadeIn(s);
    let y;
    let init = self.init;
    self.init = async () => {
        init.call(self);
        const pos = await s.drawer.move();
        y = pos.y;
    };

    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        s.drawer.parent.style.top = (y + (1 - opacity) * 40) + "px";
        return opacity;
    };
    return self;
}

presentationActions.methods.flyOut = s => {
    let self = presentationActions.methods.fadeOut(s);
    let y;
    self.init = () => y = s.drawer.move().y;

    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        s.drawer.parent.style.top = (y + (1 - opacity) * 40) + "px";
        return opacity;
    };
    return self;
};
//========================end of fly in and fly out======================================

presentationActions.methods.rotateIn = s => {
    let self = presentationActions.methods.fadeIn(s);
    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        let degree = opacity * 360 * 1.5;
        s.drawer.parent.style.transform = 'rotate(' + degree + 'deg)';
        return opacity;  
    };
    return self;
}

presentationActions.methods.rotateOut = s => {
    let self = presentationActions.methods.fadeOut(s);
    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        let degree = opacity * 360 * 1.5;
        s.drawer.parent.style.transform = 'rotate(' + degree + 'deg)';
        return opacity;
    };

    return self;
};
//========================end of rotate in and rotate out======================================

presentationActions.methods.shrink = s => {
    let self = presentationActions.methods.fadeIn(s);
    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        let scale = 1 / Math.sqrt(opacity);
        s.drawer.parent.style.transform = "scale(" + scale + "," + scale + ")";
        return opacity;
    };
    return self;
}

presentationActions.methods.expand = s => {
    let self = presentationActions.methods.fadeOut(s);
    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        let scale = 1 / Math.sqrt(opacity);
        s.drawer.parent.style.transform = "scale(" + scale + "," + scale + ")";
        return opacity;
    };

    return self;
};
//========================end of rotate in and rotate out======================================

presentationActions.methods.moveIn = s => {
    let self = presentationActions.methods.fadeIn(s);
    let x, offset;
    self.init = async () => {
        const sPos = await s.drawer.move();
        x = sPos.x;
        let frame = s.page.getFrame();
        const framePos = await frame.drawer.move();
        offset = framePos.x + frame.width;
        s.drawer.parent.style.left = offset + "px";
    };
    self.condition = () => offset > x;
    let animate = self.animate;
    self.animate = () => {
        let opacity = animate.call(self);
        s.drawer.parent.style.left = offset + "px";
        offset -= 20;
        return opacity;
    };
    let finalize = self.finalize;
    self.finalize = () => {
        s.drawer.move();
        finalize.call(self);
    }
    return self;
};

presentationActions.methods.moveOut = s => {
    let self = presentationActions.newMethod(s, ANIMIATION_ACTION.OUT);
    let x, offset;
    self.init = () => {
        x = s.drawer.move().x;
        let frame = s.page.getFrame();
        offset = frame.drawer.move().x + frame.width;
    };
    self.condition = () => x < offset;
    self.animate = () => {
        s.drawer.parent.style.left = x + "px";
        x += 20;
    };
    return self;
};

//========================end of move in and move out======================================
presentationActions.methods.code = (s, thread, pageId) => {
    //--------------用户自定义代码感知变量&方法-------------------
    let page = presentationActions.currentPage;
    //用于用户脚本放一下上下文变量
    let context = {};
    //公布用户可操作的最顶级形状
    let frame = page.getFrame();
    //公布鼠标位置和移动量给脚本
    let mousex, mousey, dx = 0, dy = 0;
    //公布新建shape方法
    let createShape = (type, x = 100, y = 100) => {
        // x = x === undefined ? 100 : x;
        // y = y === undefined ? 100 : y;
        return presentationActions.currentPage.createNew(type, x, y);
    };
    //公布查询shape方法
    let getShape = id => page.shapes.find(s => s.id === id);
    /**
     * 闪烁，标准为visible，可以用户自定义
     */
    let flash = function () {
        let count = 0, flashcounter = 0;
        let shapes = [], visibilities = [];
        return (findShapes, times, action, final, step) => {
            const TOTAL = (times !== undefined ? (2 * times + 1) : 1), STEP = step ? step : 10;
            let ids = findShapes();
            if (shapes.length === 0) {
                ids.forEach(id => {
                    let s = (id.id !== undefined) ? id : getShape(id);//如果传入的就是形状也可以运行
                    shapes.push(s);
                    visibilities.push(s.visible);
                });
            }
            count++;
            if (flashcounter <= TOTAL) {
                if ((count > (flashcounter * STEP)) && (count < ((flashcounter + 1) * STEP))) {
                    shapes.forEach(s => {
                        let value = (flashcounter % 2 === 0);
                        if (action !== undefined) {
                            action(s, value);
                        } else {
                            s.visible = value;
                        }
                        s.render();
                    });
                } else {
                    flashcounter++;
                    if (times === undefined && flashcounter > 1) {//一直循环
                        count = flashcounter = 0;
                    }
                }
            } else {
                shapes.forEach((s, i) => {
                    if (final !== undefined) {
                        final(s);
                    } else {
                        s.visible = visibilities[i];
                    }
                    s.render();
                });
                return -1;
            }
        };
    }();
    /**
     * 移动，参数为相对位置
     */
    let move = function () {
        let gonex = 0, goney = 0, shapes = [];
        return (findShapes, offsetx, offsety) => {
            const UNIT = 10;
            const STEP_X = UNIT * Math.abs(offsetx) / offsetx;
            const STEP_Y = UNIT * Math.abs(offsetx) / offsety;

            let ids = findShapes();
            if (shapes.length === 0) {
                ids.forEach(id => {
                    let s = (id.id !== undefined) ? id : getShape(id);//如果传入的就是形状也可以运行
                    shapes.push(s);
                });
            }
            if (gonex !== offsetx || goney !== offsety) {
                let dx = STEP_X, dy = STEP_Y;
                if (Math.abs(gonex + dx) < Math.abs(offsetx)) {
                    gonex += dx;
                } else {
                    dx = offsetx - gonex;
                    gonex = offsetx;
                }
                if (Math.abs(goney + dy) < Math.abs(offsety)) {
                    goney += dy;
                } else {
                    dy = offsety - goney;
                    goney = offsety;
                }
                shapes.forEach(s => {
                    s.moveTo(s.x + dx, s.y + dy);
                    // s.x += dx;
                    // s.y += dy;
                })

            } else {
                return -1;
            }
        };
    }();
    let runCode = (code) => {
        try {
            return eval("(function dynamicCode(){" + code + "})();");
        } catch (e) {
            console.warn("animation executing error:\n" + e);
            return -1;
        }
    }
    //-----------------------------------------------------------

    let self = presentationActions.newMethod(s, ANIMIATION_ACTION.CODE)
    self.init = () => {
    };
    self.condition = () => thread === presentationActions.currentThread;
    self.animate = code => {
        if (mousex !== undefined) {
            dx = page.mousex - mousex;
        }//允许用户自定义代码感知鼠标移动
        if (mousey !== undefined) {
            dy = page.mousey - mousey;
        }
        let result = runCode(code);
        mousex = page.mousex;
        mousey = page.mousey;
        return result;
    };
    self.finalize = code => {
        runCode(code);
        context = undefined;
    };
    return self;
};
presentationActions.methods.pageCode = (s, thread, pageId) => {
    let self = presentationActions.methods.code(s, thread);
    self.condition = () => pageId === presentationActions.currentPage.id;
    return self;
};

export { presentationActions };