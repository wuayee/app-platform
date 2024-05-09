import {FONT_WEIGHT, MIN_WIDTH} from '../common/const.js';

import {canvasRectangleDrawer} from './drawers/rectangleDrawer.js';
import {rectangle} from './rectangle.js';

/**
 * 矢量图
 * canvas上用一个基础数据画的图，比如100*100，在放大缩小后会自动缩放
 * 辉子 2021
 */
let vector = (id, x, y, width, height, parent, drawer = canvasRectangleDrawer) => {
    if (width === MIN_WIDTH) {
        width = 100;
    }
    if (height === MIN_WIDTH) {
        height = 100;
    }
    let self = rectangle(id, x, y, width, height, parent, drawer);
    self.type = "vector";
    self.backColor = self.focusBackColor = "transparent";
    self.text = "";
    self.originWidth = width;
    self.originHeight = height;
    self.enableAnimation = true;

    let addRegion = self.addRegion;
    self.addRegion = (region, index) => {
        addRegion.apply(self, [region, index]);
        let getx = region.getx;
        let gety = region.gety;
        let getWidth = region.getWidth;
        let getHeight = region.getHeight;
        region.getx = () => getx.apply(region) * self.width / self.originWidth;
        region.gety = () => gety.apply(region) * self.height / self.originHeight;
        region.getWidth = () => getWidth.apply(region) * self.width / self.originWidth;
        region.getHeight = () => getHeight.apply(region) * self.height / self.originHeight;
    };

    self.drawer.draw = () => {
        //自己不可见则不绘制
        self.drawer.setVisibility();
        self.drawer.drawBorder();
        //重新绘制
        let context = self.drawer.canvas.getContext("2d");
        context.clearRect(0, 0, context.canvas.width, context.canvas.height);
        context.save();
        let rateX = self.width / self.originWidth, rateY = self.height / self.originHeight
        context.scale(rateX, rateY);
        context.strokeStyle = self.getBorderColor();
        context.fillStyle = self.getBackColor();
        self.drawStatic(context, self.margin / rateX, self.margin / rateY, self.originWidth, self.originHeight);
        self.drawer.drawRegions();
        context.restore();
    };

    let drawRegions = self.drawer.drawRegions;
    self.drawer.drawRegions = () => {
        if (self.isType('vector') && self.regions.length == 0) {
            exampleRegion(self, () => width - 10, () => 3, () => 16, () => 16);
        }
        drawRegions.apply(self.drawer);
    };

    let drawAnimation = self.drawer.drawAnimation;
    let drawDynamic = self.drawer.drawDynamic;
    self.drawer.drawAnimation = (context) => {
        self.drawer.drawDynamic = (context, x, y) => {
            drawDynamic.apply(self.drawer, [context, x, y]);
            context.save();
            //context.translate(x, y);
            context.scale(self.width / self.originWidth, self.height / self.originHeight);
            self.drawDynamic(context, self.originWidth, self.originHeight);
            context.restore();
        };
        drawAnimation.apply(self.drawer, [context]);
    };

    let drawStaticFunction = undefined;
    self.drawStatic = (context, x, y, width, height) => {
        if (self.drawStaticCode && drawStaticFunction === undefined) {
            drawStaticFunction = eval("(" + self.drawStaticCode + ")");
        }
        drawStaticFunction && drawStaticFunction(context, x, y, width, height);
    };

    const global = {};
    let drawDynamicFunction = undefined;
    self.drawDynamicCode = (function (context, width, height, global, shape) {
        let drawArc = (angle, context, self, width) => {
            context.beginPath();
            context.fillStyle = "red";// self.getBorderColor();
            context.lineWidth = 3;
            context.moveTo(0, 0);
            context.arc(0, 0, width / 4, angle, angle + 0.15 * Math.PI);
            context.closePath();
            context.fill();
        };
        const r = 12, step = Math.PI / 200;
        (!global.degree) && (global.degree = 0);
        context.save();
        context.rotate(global.degree);

        drawArc(0, context, self, width);
        drawArc((1 / 3) * Math.PI, context, self, width);
        drawArc((2 / 3) * Math.PI, context, self, width);
        drawArc(1 * Math.PI, context, self, width);
        drawArc((4 / 3) * Math.PI, context, self, width);
        drawArc((5 / 3) * Math.PI, context, self, width);
        context.restore();
        global.degree -= step;
        if (global.degree <= -Math.PI * 2) {
            global.degree = 0;
        }

    }).toString();
    self.drawDynamic = (context, width, height) => {
        if (self.drawDynamicCode && drawDynamicFunction === undefined) {
            drawDynamicFunction = eval("(" + self.drawDynamicCode + ")");
        }
        drawDynamicFunction && drawDynamicFunction(context, width, height, global, self);
    };

    //--------------------------serialization & detection------------------------------
    // self.serializedFields.batchAdd("originWidth", "originHeight", "drawDynamicCode");

    return self;
};

/**
 * 继承自vector，ESLA的logo，可无极缩放
 * 辉子 2021
 */
let elsaLogo = (id, x, y, width, height, parent) => {
    let self = vector(id, x, y, width, height, parent);
    self.originWidth = self.width = 350;
    self.originHeight = self.height = 100;
    self.type = "elsaLogo";
    self.hideText = true;
    self.text = "E. L. S. A";
    self.fontFace = "arial black";
    self.fontWeight = FONT_WEIGHT.BOLD;
    self.borderColor = "darkred";
    self.fontColor = "darkred";
    self.borderWidth = 0;
    self.fontSize = 35;
    self.enableAnimation = true;

    self.drawStatic = (context, x, y, width, height) => {
        context.font = self.fontStyle + " " + self.fontWeight + " " + self.fontSize + "px " + self.fontFace;
        context.fillStyle = "silver";
        context.lineWidth = 3;
        x = 70, y = 40;
        let offset = 20;
        //context.fillText(self.text, x + 5, y + 15);

        x -= 30;
        let addGradientStops = g => {
            g.addColorStop(0, "rgba(255,255,255,0)");
            g.addColorStop(0.5, self.getBorderColor());
            g.addColorStop(1, self.getBorderColor());
        };
        context.lineWidth = 2;
        //上横线
        let g1 = context.createLinearGradient(x - 2 * offset, y, x + offset, y);
        addGradientStops(g1);
        context.beginPath();
        context.moveTo(x - 2 * offset, y);
        context.lineTo(x + offset, y);
        context.strokeStyle = g1;
        context.stroke();
        //左竖线
        let g2 = context.createLinearGradient(x, y + 3 * offset, x, y);
        addGradientStops(g2);
        context.beginPath();
        context.moveTo(x, y);
        context.lineTo(x, y + 3 * offset);
        context.strokeStyle = g2;
        context.stroke();
        //右竖线
        let g3 = context.createLinearGradient(x + offset, y - 2 * offset, x + offset, y + offset);
        addGradientStops(g3);
        context.beginPath();
        context.moveTo(x + offset, y - 2 * offset);
        context.lineTo(x + offset, y + offset);
        context.strokeStyle = g3;
        context.stroke();
        //下横线
        let g4 = context.createLinearGradient(x + offset + width, y + offset, x, y + offset);
        addGradientStops(g4);
        context.beginPath();
        context.moveTo(x, y + offset);
        context.lineTo(x + offset + width, y + offset);
        context.strokeStyle = g4;
        context.stroke();
        //三个小方块
        let r = 4, r2 = 6;
        context.lineWidth = 1;
        context.globalAlpha = 0.5;
        context.beginPath();
        context.strokeStyle = self.page.fontColor;// "rgba(236,208,167,0.5)"
        context.rect(x + offset - r, y - r, 2 * r, 2 * r);
        context.rect(x - r, y + offset - r, 2 * r, 2 * r);
        context.rect(x + offset / 2 - r2, y + offset / 2 - r2, 2 * r2, 2 * r2);
        //再来几条线
        context.moveTo(x + 5, y + offset + 3);
        context.lineTo(x + Math.ceil(width / 2), y + offset + 3);
        context.moveTo(x + 5, y + offset + 6);
        context.lineTo(x + Math.ceil(width / 5), y + offset + 6);
        context.moveTo(x + 5, y + offset + 9);
        context.lineTo(x + Math.ceil(width / 15), y + offset + 9);
        context.stroke();
        //完全是意识流瞎设计的logo
    };
    const OFFSET = 150, RATE = 0.65;
    let pos = -OFFSET * RATE;
    self.xoffset = 0;
    self.yoffset = 0;
    self.drawDynamic = (context, width, height) => {
        context.font = self.fontStyle + " " + self.fontWeight + " " + self.fontSize + "px " + self.fontFace;
        let w = context.measureText(self.text).width;
        let x = -self.originWidth / 2 + 69 + self.xoffset, y = 4 + self.yoffset;
        let g = context.createLinearGradient(pos + x, y + self.fontSize / 2, pos + x + OFFSET, y - self.fontSize / 2);
        g.addColorStop(0, self.fontColor);
        g.addColorStop(0.4, self.fontColor);
        g.addColorStop(0.5, "rgba(255,255,255,0.8)");
        g.addColorStop(0.6, self.fontColor);
        g.addColorStop(1, self.fontColor);
        context.fillStyle = g;
        context.fillText(self.text, x, y);
        pos += 1;
        while (pos > (w + OFFSET * RATE)) pos = -OFFSET * RATE;
    };

    // self.serializedFields.batchAdd("xoffset", "yoffset");

    return self;

}

export {vector, elsaLogo};