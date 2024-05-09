import {hitRegion} from '../../../core/hitRegion.js';
import {EVENT_TYPE, FLOWABLE_STATE_STATUS} from '../../../common/const.js';

/**
 * flowable节点的状态图标
 * 辉子 2021
 */
// let statusRegion = (state, getx, gety, getWidth, getHeight, index) => {
//     let self = hitRegion(state, getx, gety, getWidth, getHeight, index);
//     self.drawStatic = (context, x, y) => {
//         drawFrame(context, x, y, state);

//         let x1 = x + 3, y1 = y + 3;
//         context.fillStyle = context.strokeStyle = state.status.color;
//         switch (status.status) {
//             case FLOWABLE_STATE_STATUS.NOTSTARTED:
//                 context.beginPath();
//                 context.rect(x + self.x + 4, y + self.y + 5, 4.5, 2.5);
//                 context.fill();
//                 context.stroke();
//                 break;
//             case FLOWABLE_STATE_STATUS.PENDING:
//                 context.fillRect(x1, y1, 2, 6);
//                 context.fillRect(x1 + 4, y1, 2, 6);
//                 break;
//             case FLOWABLE_STATE_STATUS.DONE:
//                 let x3 = x1 + 0.5, y3 = y1 + 2;
//                 context.beginPath();
//                 context.moveTo(x3, y3);
//                 context.lineTo(x3 + 2, y3 + 3);
//                 context.lineTo(x3 + 5, y3 - 2);
//                 context.lineWidth = 2;
//                 context.stroke();
//                 break;
//         }
//     };
//     let degree = 0, step = Math.PI / 200;
//     let drawArc = (angle, context) => {
//         context.beginPath();
//         context.strokeStyle = state.getBorderColor();
//         context.lineWidth = 3;
//         context.arc(0, 0, 6, angle, angle + 0.15 * Math.PI);
//         context.stroke();
//     };
//     let drawDynamic = self.drawDynamic;
//     self.drawDynamic = (context, x, y) => {
//         drawDynamic.call(self, context, x, y);
//         const r = 12;
//         let x1 = x + r, y1 = y + r;
//         switch (state.status) {
//             case FLOWABLE_STATE_STATUS.RUNNING:
//                 context.save();
//                 context.translate(x1, y1);
//                 context.rotate(degree);
//                 drawArc(0, context);
//                 drawArc((1 / 3) * Math.PI, context);
//                 drawArc((2 / 3) * Math.PI, context);
//                 drawArc(1 * Math.PI, context);
//                 drawArc((4 / 3) * Math.PI, context);
//                 drawArc((5 / 3) * Math.PI, context);
//                 context.restore();
//                 degree += step;
//                 if (degree >= Math.PI * 2) {
//                     degree = 0;
//                 }
//                 break;
//             default:
//                 break;
//         }

//     };
//     self.click = () => {
//         console.log("flowable state status click");
//     }
//     return self;
// };

/**
 * flowable节点运行模式：自动，手动，定时
 * 辉子 2021
 */
let modeRegion = (state, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(state, getx, gety, getWidth, getHeight, index);
    self.drawStatic = (context, x, y) => staticMethods[state.triggerMode + "StaticDraw"](state, context, x, y);
    // self.drawStatic = (context, x, y) => eval(state.triggerMode + "StaticDraw(state,context, x, y)");
    let degree = 0, step = Math.PI / 200;
    let drawDynamic = self.drawDynamic;
    self.drawDynamic = (context, x, y) => {
        drawDynamic.call(self, context, x, y);
        const R = 12;
        let x1 = x + R, y1 = y + R;
        context.strokeStyle = state.getBorderColor();
        context.save();
        context.translate(x1, y1);
        context.rotate(degree);
        dynamicMethods[state.triggerMode + "DynamicDraw"](state, context, x, y);
        // eval(state.triggerMode + "DynamicDraw(state,context, x, y)");
        context.restore();
        degree += step;
        if (degree >= Math.PI * 2) {
            degree = 0;
        }
    };

    return self;
};

let taskStatRegion = (state, type, getx, gety, getWidth, getHeight, index) => {
    const statType = ['warningTask', 'runningTask', 'completedTask'];
    const statColor = ['red', 'blue', 'green'];
    const ind = statType.findIndex(i => i === type);
    if (ind === -1) {
        return;
    }
    let self = hitRegion(state, getx, gety, getWidth, getHeight, index);
    self.type = type;
    self.drawStatic = (context, x, y, height, width) => {
        drawCountRegion(context, x, y, height, width, statColor[ind], state[type])
    }
    self.drawDynamic = undefined;
    return self;
}

let insertNodeRegion = (event, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(event, getx, gety, getWidth, getHeight, index);
    self.type = "insert-node-button";
    self.visible = false;

    // 此热点的UI为蓝色背景的圆，中间是白色的+
    self.drawStatic = (context, x, y, height, width) => {
        // 让图像和点击热区重合
        if (event.from().x > event.to().x) {
            self.context.canvas.style.left = (x - getWidth() / 2 + event.from().x - event.to().x) + "px";
        }
        if (event.from().y > event.to().y) {
            self.context.canvas.style.top = (y - getHeight() / 2 + event.from().y - event.to().y) + "px";
        }
        drawRect(context, x, y, "#0478fc");
        context.beginPath();
        context.lineWidth = 5;
        const w = context.measureText("+").width;
        context.strokeStyle = "white"
        context.fillStyle = "white"
        context.font = (height + 2) + "px Ariel"
        context.fillText("+", x + width / 2 - w -2, y + height - 5);
        context.fill();
        context.stroke();
    }
    self.drawDynamic = undefined;

    return self;
};

let agentRegion = (state, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(state, getx, gety, getWidth, getHeight, index);
    self.type = "agentRegion";

    // 此热点的UI为黄色背景的矩形和白色字
    self.drawStatic = (context, x, y, height, width) => {
        drawRect(context, x, y, "#FFB142");
        context.fillStyle = "white";
        context.font = "12px Ariel";
        context.fillText("Agent", 4, 15, 16);
    }
    self.drawDynamic = undefined;

    return self;
};

const drawCountRegion = (context, x, y, height, width, color, num) =>{
    drawCircle(context, x, y, color);
    context.beginPath();
    context.lineWidth = 3;
    const w = context.measureText(num).width;
    context.strokeStyle = "white"
    context.fillStyle = "white"
    context.font = (height - 8) + "px Ariel"
    context.fillText(num, x + width / 2 - w + 1 , y + height - 7);
    context.fill();
    context.stroke();
}

let drawCircle = (context, x, y, color) => {
    const r = 12
    let x2 = x + r, y2 = y + r;
    context.beginPath();
    context.arc(x2, y2, r, 0, 2 * Math.PI);
    context.strokeStyle = color;
    context.fillStyle = color;
    context.fill();
    context.stroke();
};

let drawRect = (context, x, y, color) => {
    context.strokeStyle = color;
    context.fillStyle = color;
    context.beginPath();
    context.fillRect(x, y, 24, 24);
    context.fill();
    context.stroke();
};

let drawFrame = (context, x, y, state) => {
    const r = 12;
    let x2 = x + r, y2 = y + r;
    context.lineWidth = 1;
    context.beginPath();
    context.arc(x2, y2, 10, 0, 2 * Math.PI);
    context.strokeStyle = "white";
    context.stroke();

    context.beginPath();
    context.arc(x2, y2, 9, 0, 2 * Math.PI);
    context.strokeStyle = state.getBorderColor();
    context.fillStyle = "white";
    context.fill();
    context.stroke();
};

let autoStaticDraw = (state, context, x, y) => drawFrame(context, x, y, state);
let autoDynamicDraw = (state, context, x, y) => {
    context.lineWidth = 2;
    context.beginPath();
    context.arc(0, 0, 5, 0, 0.7 * Math.PI);
    context.stroke();
    context.beginPath();
    context.arc(0, 0, 5, Math.PI, 1.7 * Math.PI);
    context.stroke();
};

let auto_asynStaticDraw = autoStaticDraw;
let auto_asynDynamicDraw = autoDynamicDraw;

let scheduleStaticDraw = (state, context, x, y) => {
    drawFrame(context, x, y, state);
    let x2 = x + 12, y2 = y + 12;
    context.lineWidth = 1;
    context.beginPath();
    context.moveTo(x2, y2);
    context.lineTo(x2, y2 - 9);
    context.stroke();
};

let scheduleDynamicDraw = (state, context, x, y) => {
    let R1 = 6;
    context.beginPath();
    context.lineWidth = 1.5
    context.moveTo(0, 0);
    context.lineTo(R1, 0);
    context.stroke();
};

let manualStaticDraw = (state, context, x, y) => {
    drawFrame(context, x, y, state);
    const x1 = x + 7, y1 = y + 9;
    context.beginPath();
    context.lineWidth = 3;
    context.strokeStyle = state.getBorderColor();
    context.moveTo(x1, y1);
    context.lineTo(x1 + 10, y1);
    context.moveTo(x1, y1 + 6);
    context.lineTo(x1 + 10, y1 + 6);
    context.stroke();
}
let manualDynamicDraw = (state, context, x, y) => {
};

let staticMethods = {};
staticMethods.autoStaticDraw = autoStaticDraw;
staticMethods.auto_asynStaticDraw = auto_asynStaticDraw;
staticMethods.scheduleStaticDraw = scheduleStaticDraw;
staticMethods.manualStaticDraw = manualStaticDraw;

let dynamicMethods = {};
dynamicMethods.autoDynamicDraw = autoDynamicDraw;
dynamicMethods.auto_asynDynamicDraw = auto_asynDynamicDraw;
dynamicMethods.scheduleDynamicDraw = scheduleDynamicDraw;
dynamicMethods.manualDynamicDraw = manualDynamicDraw;

export { modeRegion, taskStatRegion, manualDynamicDraw, manualStaticDraw, scheduleDynamicDraw, scheduleStaticDraw, autoDynamicDraw, autoStaticDraw, auto_asynStaticDraw, auto_asynDynamicDraw, insertNodeRegion, agentRegion};