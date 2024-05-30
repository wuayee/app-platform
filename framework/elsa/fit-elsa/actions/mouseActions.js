import {offsetPosition, sleep, uuid} from '../common/util.js';
import {addCommand, eraserComamnd, positionCommand, resizeCommand, updateFreeLineCommand} from '../core/commands.js';
import {EVENT_TYPE} from "../common/const.js";

// todo@zhangyue 调试鼠标事件时可用.
// function debounce(func, wait, immediate) {
//     let timeout;
//     return function() {
//         let context = this, args = arguments;
//         let later = function() {
//             timeout = null;
//             if (!immediate) func.apply(context, args);
//         };
//         let callNow = immediate && !timeout;
//         clearTimeout(timeout);
//         timeout = setTimeout(later, wait);
//         if (callNow) func.apply(context, args);
//     };
// }

/*
透出鼠标事件
辉子 2020-02-21
*/
let mouseEvents = () => {
    let element = null;
    let nextX = 0;
    let nextY = 0;
    let page = null;
    const mouseActionsRecords = {};

    const calculatePosition = (e) => {
        if (isNaN(page.x) || isNaN(page.y)) {
            page.moveTo(0, 0);
        }

        // 鼠标事件会互相影响，因此这里通过类型区分鼠标的prevX和prevY.
        !mouseActionsRecords[e.type] && (mouseActionsRecords[e.type] = {prevX: 0, prevY: 0});
        let prevX = 0;
        let prevY = 0;
        if (e.type) {
            prevX = mouseActionsRecords[e.type].prevX;
            prevY = mouseActionsRecords[e.type].prevY;
        }

        const zoom = page.interactDrawer.zoom;

        // rect中的left和top会被zoom缩放，但clientX和clientY不会被缩放.
        // 因此这里和clientX、clientY进行计算时，需要先将left和top还原.
        const rect = page.div.getBoundingClientRect();
        const mouseX = e.clientX - rect.left * zoom;
        const mouseY = e.clientY - rect.top * zoom;

        const scrollPosition = page.getScrollPosition();
        const x = calculateX(mouseX, zoom, scrollPosition);
        const y = calculateY(mouseY, zoom, scrollPosition);
        const nx = calculateX(prevX, zoom, scrollPosition);
        const ny = calculateY(prevY, zoom, scrollPosition);
        if (e.type) {
            mouseActionsRecords[e.type].prevX = mouseX;
            mouseActionsRecords[e.type].prevY = mouseY;
        }

        return {
            x, y,
            mousex: x,//e.clientX - os.x,
            mousey: y,//e.clientY - os.y,
            currentTarget: e.currentTarget,
            e: e,
            deltaX: x - nx,
            deltaY: y - ny
        }
    };

    const calculateX = (mouseX, zoom, scrollPosition) => {
        const rect = page.div.getBoundingClientRect();
        const scaleX = rect.width / page.div.offsetWidth;
        return (mouseX / zoom /scaleX + scrollPosition.x) / page.scaleX - page.x;
    };

    const calculateY = (mouseY, zoom, scrollPosition) => {
        const rect = page.div.getBoundingClientRect();
        const scaleY = rect.height / page.div.offsetHeight;
        return (mouseY / zoom /scaleY + scrollPosition.y) / page.scaleY - page.y;
    };

    let touchPosition = (touchEvent) => {//todo: change nextx with movementX
        let positions = [];
        for (let i = 0; i < touchEvent.changedTouches.length; ++i) {
            let e = touchEvent.changedTouches[i];
            let os = offsetPosition(page);
            let deltaX = (e.clientX - nextX) / page.scaleX;
            let deltaY = (e.clientY - nextY) / page.scaleY;
            if (isNaN(page.x) || isNaN(page.y)) {
                page.moveTo(0, 0);
            }
            nextX = e.clientX;
            nextY = e.clientY;

            // todo@zhangyue 暂时注释掉，会导致文件上传功能在手机端不可用.
            // if (touchEvent.preventDefault) {
            //     try {
            //         touchEvent.preventDefault();
            //     } catch (e) {
            //     }
            // }
            let x = parseInt((e.clientX - os.x) / page.scaleX) - page.x;
            let y = parseInt((e.clientY - os.y) / page.scaleY) - page.y;
            let position = {
                x: x,
                y: y,
                mousex: x,
                mousey: y,
                deltaX: deltaX,
                deltaY: deltaY,
                currentTarget: touchEvent.currentTarget,
                e: e,
                type: touchEvent.type
            }
            positions.push(position);
        }
        return positions;

        // todo:临时解决方案，解决ICT年会游戏H5页面强制横屏后，鼠标事件坐标计算转换
        // if(document.documentElement.clientWidth < document.documentElement.clientHeight) {
        //
        //     position =  {
        //         x: parseInt((e.clientY - os.y) / page.scaleY) - page.x,
        //         y: parseInt((document.documentElement.clientWidth - e.clientX + os.x) / page.scaleX) - page.y,
        //         mousex: e.clientY - os.y,
        //         mousey: document.documentElement.clientWidth - e.clientX + os.x,
        //         deltaX: deltaY,
        //         deltaY: -deltaX,
        //         e: e
        //     }
        // }
    };
    /*let mouseWheelEvent = (mousewheel) => {
        element.onmousewheel = (e) => {
            return mousewheel(window.event || e);
        };
    };*/
    let addMouseMoveListener = (func) => {
        return (e) => {
            const position = calculatePosition(e);
            return func(position);
        };
    }
    let addMouseListener = (func) => (e) => {
        let position = calculatePosition(e);
        return func(position);
    };

    let mouseMoveListener = () => {
    };
    let mouseClickListener = () => {
    };
    let mouseRightClickListener = () => {
    };
    let mouseDownListener = () => {
    };
    let mouseUpListener = () => {
    };
    let mouseOverListener = () => {
    };
    let mouseOutListener = () => {
    };
    let mouseInListener = () => {
    };
    let dbclickListener = () => {
    };
    let mouseWheelListener = () => {
    };
    let touchMoveListener = () => {
    };
    let touchStartListener = () => {
    };
    let touchEndListener = () => {
    };
    let touchCancelListener = () => {
    };
    let mouseLeaveListener = () => {
    };

    let directTouch = (func) => (position) => {
        func(position);
    }

    let onTouch = (func) => (position) => {
        func(position);
    }

    let events = {
        init: (p) => {
            page = p;
            element = page.interactDrawer.getInteract();
            const touchmove = (e) => {
                let positions = touchPosition(e);
                positions.forEach(position => touchMoveListener(position));
            };
            const touchstart = (e) => {
                let positions = touchPosition(e);
                positions.forEach(position => touchStartListener(position));
            };
            const touchend = (e) => {
                let positions = touchPosition(e);
                positions.forEach(position => touchEndListener(position));
            };
            const touchcancel = (e) => {
                let positions = touchPosition(e);
                positions.forEach(position => touchCancelListener(position));
            };
            element.addEventListener('touchmove', onTouch(touchmove));
            element.addEventListener('touchstart', onTouch(touchstart));
            element.addEventListener('touchend', onTouch(touchend));
            element.directtouchmove = directTouch(touchmove);
            element.directtouchstart = directTouch(touchstart);
            element.directtouchend = directTouch(touchend);
            element.directtouchcancel = directTouch(touchcancel);
            element.onmousemove = addMouseMoveListener(mouseMoveListener);
            element.onclick = addMouseListener(mouseClickListener);
            element.oncontextmenu = addMouseListener(mouseRightClickListener);
            element.onmouseup = addMouseListener(mouseUpListener);
            element.onmouseover = addMouseListener(mouseOverListener);
            element.onmouseout = addMouseListener(mouseOutListener);
            element.onmouseenter = addMouseListener(mouseInListener);
            element.ondblclick = addMouseListener(dbclickListener);
            element.onmousedown = e => {
                element.focus();
                addMouseListener(mouseDownListener)(e);
            };
            element.onmousewheel = addMouseListener(mouseWheelListener);
            element.onmouseleave = addMouseListener(mouseLeaveListener);
            events.element = element;
            // p.managePosition = managePosition;
            p.calculatePosition = calculatePosition;
        },
        onMouseLeave: mouseLeave => mouseLeaveListener = mouseLeave,
        onMouseWheel: mousewheel => mouseWheelListener = mousewheel,
        onMouseClick: click => mouseClickListener = click,
        onMouseRightClick: click => mouseRightClickListener = click,
        onMouseDown: mousedown => mouseDownListener = mousedown,
        onMouseUp: mouseup => mouseUpListener = mouseup,
        onMouseMove: mousemove => mouseMoveListener = mousemove,
        onMouseOver: mouseover => mouseOverListener = mouseover,
        onMouseOut: mouseout => mouseOutListener = mouseout,
        onMouseIn: mousein => mouseInListener = mousein,
        onDbClick: dbclick => dbclickListener = dbclick,
        ontouchmove: touchmove => touchMoveListener = touchmove,
        ontouchstart: touchstart => touchStartListener = touchstart,
        ontouchend: touchend => touchEndListener = touchend,
        ontouchcancel: touchcancel => touchCancelListener = touchcancel
    };

    return events;
};

/*
鼠标事件
double click: begin edit in config mode
mouseDown,mouseMove,mouseUp,mouseOver,mouseOut
辉子 2020-02-21
*/
let bindMouseActions = page => {
    let events = mouseEvents();
    events.onMouseClick(position => {
        // 如果是小程序，既会触发touchStart事件，又会触发mousedown->click事件，
        // 在文件上传的场景下，两者都会触发打开文件选择框，因此这里若不是pc端，则阻止默认事件.
        if (page.graph.environment === "wecode") {
            position.e.preventDefault();
            return;
        }
        page.mouseClick(position)
    });//click允许在runtime下执行
    events.onMouseRightClick(position => page.mouseRightClick(position));//允许在runtime下执行
    events.onDbClick(position => {
        if (page.readOnly()) {
            return;
        }
        page.dbClick(position);
    });

    events.mouseContext = {};
    events.onMouseDown(async position => {
        page.graph.getHistory().clearBatchNo();
        if (document.activeElement !== document.body) {
            document.activeElement.blur();
        }
        page.mouseButton = position.e.button;
        page.mousedownx = position.x;
        page.mousedowny = position.y;

        //set context for command
        events.mouseContext = {};
        events.mouseContext.id = uuid();
        events.mouseContext.command = "";
        events.mouseContext.shapes = [];
        position.context = events.mouseContext;

        console.log("============== welink test: mouseActions#onMouseDown");
        page.mouseDown(position);
        while (page.mousedownShape) {
            page.mouseHold({x: page.mousex, y: page.mousey});
            await sleep(40);
        }
    });

    async function touchCancel(position) {
        if (page.readOnly()) {
            return;
        }
        clearTimeout(events.mouseContext.longTouchTimer);
        position.context = events.mouseContext;
        page.mousedownShape = null;
        page.mouseCancel && (await page.mouseCancel(position));
        page.touching = false;
        page.invalidateInteraction();
    }

    events.ontouchstart(async position => {
        page.triggerEvent({type: EVENT_TYPE.TOUCH_START});

        page.touching = true;
        page.mouseButton = position.e.button;
        page.mousedownx = position.x;
        page.mousedowny = position.y;

        //set context for command
        events.mouseContext = {};
        events.mouseContext.id = uuid();
        events.mouseContext.command = "";
        events.mouseContext.shapes = [];
        events.mouseContext.start = new Date().getTime();
        position.context = events.mouseContext;

        console.log("============== welink test: mouseActions#touchStart");
        page.mouseDown(position);

        events.mouseContext.longTouchTimer = setTimeout(function () {
            page.mouseCancel && (page.mouseCancel(position));
            page.mousedownShape.onLongClick(position);
            if (page.mousedownShape === page) {
                page.triggerEvent({type: EVENT_TYPE.PAGE_LONG_CLICK, value: page});
            } else {
                page.triggerEvent({type: EVENT_TYPE.SHAPE_LONG_CLICK, value: page.mousedownShape});
            }
        }, 750);
    });

    function generateCommand(position) {
        const commandName = position.context.command;
        let command = {
            execute: () => {
            }
        };
        switch (commandName) {
            case "position":
                // 存在图形x和y发生了变化的情况，才需要生成positionCommands.
                position.context.shapes = position.context.shapes
                    .filter(s => s.x.preValue !== s.x.value || s.y.preValue !== s.y.value);
                if (position.context.shapes.length > 0) {
                    command = positionCommand(page, position.context.shapes);
                    page.triggerEvent({
                        type: EVENT_TYPE.SHAPE_MOVED, value: position.context.shapes.map(dirty => dirty.shape)
                    });
                }
                page.triggerEvent({
                    type: EVENT_TYPE.CONTEXT_CREATE,
                    value: [page.mousedownShape]
                });
                break;
            case "resize":
                command = resizeCommand(page, position.context.shapes);
                page.triggerEvent({
                    type: EVENT_TYPE.SHAPE_RESIZED, value: position.context.shapes.map(dirty => dirty.shape)
                });
                page.triggerEvent({
                    type: EVENT_TYPE.CONTEXT_CREATE,
                    value: [page.mousedownShape]
                });
                command.execute();
                break;
            case "addShape":
                command = addCommand(page, position.context.shapes);
                command.execute();
                break;
            case "eraser":
                command = eraserComamnd(page, position.context.shapes);
                command.execute();
                break;
            case "updateFreeLine":
                command = updateFreeLineCommand(page, position.context.shapes);
                command.execute();
                break;
            default:
                break;
        }
        position.conext = {command: undefined, shapes: []};
        events.mouseContext = {command: undefined, shapes: []};
        page.triggerEvent({type: EVENT_TYPE.TOUCH_END});
    }

    const mouseup = async position => {
        if (page.readOnly() || !events.mouseContext) {
            return;
        }
        clearTimeout(events.mouseContext.longTouchTimer);
        position.context = events.mouseContext;
        await page.mouseUp(position);
        if (new Date().getTime() - events.mouseContext.start < 100) {
            page.mouseClick(position);
        }
        page.mousedownShape = null;
        page.touching = false;
        page.invalidateInteraction();

        if (!position.context.command) {
            return;
        }

        generateCommand(position);
    };

    events.onMouseUp(mouseup);

    events.ontouchcancel(async position => {
        await touchCancel(position);
        page.triggerEvent({type: EVENT_TYPE.TOUCH_END});
    })

    events.ontouchend(mouseup);
    // events.onMouseUp = events.ontouchend;

    events.onMouseMove(position => {
        // 如果存在activeElement，则将事件处理交由activeElement直接处理，elsa的事件机制不进行处理.
        // @todo@zhangyue 这个是不是最佳判断存疑，后续有新的思路再进行修改.
        if (page.isMouseDown() && document.activeElement !== document.body) {
            return;
        }

        // 这里阻止默认事件，是因为避免在拖动图形的时候，触发浏览器的默认行为选中所有文本(mousedownShape是page的情况例外)
        // *重要* 必须放在第一行，放在mouseDrag中，也会触发浏览器默认行为
        if (page.isMouseDown() && page.mousedownShape !== page) {
            position.e.preventDefault();
        }
        if (page.readOnly()) {
            return;
        }
        if (position.deltaX === 0 && position.deltaY === 0) {
            return;
        }
        page.mousex = position.x;
        page.mousey = position.y;
        position.context = events.mouseContext;
        //page.invalidateInteraction();//这句可以不执行，但出现过不可预知的mousex响应失败
        if (page.isMouseDown()) {
            page.mouseDrag(position);
        } else {
            page.mouseMove(position);
        }
    });

    events.ontouchmove(position => {
        if (page.readOnly()) {
            return;
        }
        if (position.deltaX === 0 && position.deltaY === 0) {
            return;
        }
        // 如果发生了超出一定范围的移动，则长按不成立
        if (Math.abs(position.x - page.mousedownx) > 10 || Math.abs(position.y - page.mousedowny) > 10) {
            clearTimeout(events.mouseContext.longTouchTimer);
        }
        page.mousex = position.x;
        page.mousey = position.y;
        position.context = events.mouseContext;

        if (page.isMouseDown()) {
            page.mouseDrag(position);
        } else {
            page.mouseMove(position);
        }
    })

    events.onMouseOver(position => {
        if (page.readOnly()) {
            return;
        }
        page.mouseOver(position);
    });
    events.onMouseOut(position => {
        //这个事件不靠谱，乱触发，请高人处理 辉子 2022
        return;
        if (page.readOnly()) {
            return;
        }
        page.mouseOut();
    });
    events.onMouseIn(position => {
        if (page.readOnly()) {
            return;
        }
        page.mouseIn(position);
    });
    events.onMouseWheel((position, direction) => {
        page.mousex = position.x;
        page.mousey = position.y;
        page.switchMouseInShape(position.x, position.y);

        if (page.ctrlKeyPressed) {
            const SCALE = 0.1
            let rate = position.e.wheelDelta > 0 ? SCALE : -SCALE;// 1 + event.wheelDelta / 1000;
            page.zoom(rate, position.mousex, position.mousey);
            position.e.preventDefault();
            return false;
        } else {
            // xiafei 暂不放开缩放以外的功能
            // let direction = "y";
            // let moveStep = 0;
            // if (typeof (event.e.deltaY) != "undefined") {
            //     direction = Math.abs(event.e.deltaY) > Math.abs(event.e.deltaX) ? "y" : "x";
            //     moveStep = Math.abs(event.e.deltaY) > Math.abs(event.e.deltaX) ? event.e.deltaY : event.e.deltaX;
            //     moveStep = 10 * moveStep / Math.abs(moveStep);
            // }
            // //if(Math.abs(wheelData)>1) page.scroll(moveStep,direction);
            // if (Math.abs(moveStep) > 0) {
            //     page.scroll(-moveStep, direction);
            // }
        }
        return page.mouseWheelAble;
    });

    /**
     * 添加mouseLeave事件.
     * 用于规避拖动图形超出画布时，elsa无法响应鼠标mouseUp事件，之后无法选中该图形的问题.
     *
     * * 注意 *
     * 以后实现无限画布之后，应该可以去掉该方法.
     *
     * @author z00559346 2023.3.30
     */
    events.onMouseLeave(async (position) => {
        if (page.readOnly()) {
            return;
        }
        position.context = events.mouseContext;
        await page.mouseLeave();
        generateCommand(position);
    });

    events.init(page);
    return events;
};

export {bindMouseActions};
