import { compareAndSet } from '../../common/util.js';
import { drawer } from './htmlDrawer.js';

/**
 * 使用canvas绘制技术绘制shape
 * 辉子 2021
 */
let canvasDrawer = (shape, div, x = 0, y = 0) => {
    // if (x === undefined) x = 0;
    // if (y === undefined) y = 0;
    let self = drawer(shape, div);
    self.type = "canvas drawer";

    //for basic drawing
    self.pixelRate = { ratioX: 1, ratioY: 1 };
    self.canvas = self.createElement("canvas","static:" + shape.id);
    self.canvas.style.position = "absolute";
    // self.canvas.id = "static:" + shape.id;
    self.parent.insertBefore(self.canvas, self.text);
    self.context = self.canvas.getContext("2d", { willReadFrequently: true });


    const containsBack = self.containsBack;
    self.containsBack = (x, y) => {
        if (self.displayBackground) {
            return containsBack.call(self, x, y);
        }
        //未能命中文字，再进行图形判断
        // const context = self.canvas.getContext("2d", { willReadFrequently: true });
        let left = 0, top = 0;

        let x1 = x + shape.margin - shape.x;
        let y1 = y + shape.margin - shape.y;
        if (shape.width < 0) {
            left += shape.width;
        }
        if (shape.height < 0) {
            top += shape.height;
        }

        //rotate coordinate
        try {
            let imageData = self.context.getImageData(((x1 - left) * self.pixelRate.ratioX), ((y1 - top) * self.pixelRate.ratioY), 1, 1);
            return imageData.data[3] > 0;
        } catch (e) {
            let error = x1 + "|" + y1;
            console.error("determine x,y image data error on: " + x1 + "," + y1 + "。" + e);
        }
    };

    let resize = self.resize;
    self.resize = () => {//react to shape.width,shape.height
        let size = resize.apply(self);
        self.resizeCanvas(size);
        self.displayBackground ? compareAndSet(self.parent.style, 'background', shape.getBackColor()) : compareAndSet(self.parent.style, 'background', "transparent");
        return size;
    };

    self.resizeCanvas = size =>{
        compareAndSet(self.canvas, 'id', "static:" + shape.id);
        const canvasSize = self.updateCanvas(size.width, size.height, 'canvas');
        // const dx = self.canvas.clientWidth - self.parent.clientWidth;
        // const dy = self.canvas.clientHeight - self.parent.clientHeight;
        const dx = canvasSize.width - size.width;
        const dy = canvasSize.height - size.height;
        self.updateIfChange(self.canvas.style, 'left', (-dx / 2) + "px", 'canvas_left');
        self.updateIfChange(self.canvas.style, 'top', (-dy / 2) + "px", 'canvas_top');
        self.updateIfChange(self.canvas.style, 'opacity', shape.globalAlpha, 'canvas_opacity');
    }

    self.drawStatic = (context, x, y) => {
    };
    //self.drawRegions = (context,x, y) => { };

    self.setVisibility = () => {
        if (!shape.inScreen()) {
            self.parent.style.visibility = "hidden";
            return;
        } else {
            self.parent.style.visibility = "visible";
        }
        self.resize();
    }
    self.getSnapshot = ()=>{
        const node =  self.parent.cloneNode();
        node.style.border = "solid "+shape.borderWidth+" "+shape.borderColor;
        return node;
    };

    self.draw = function () {
        let initialized = false;
        return () => {
            if (!initialized) {
                self.initialize();
                initialized = true;
            }
            if (!shape.getVisibility()) {
                return;
            }
            self.drawBorder();
            //重新绘制
            let offsetX = shape.width < 0 ? -shape.width : 0;
            let offsetY = shape.height < 0 ? -shape.height : 0;

            let context = self.context;
            context.save();
            self.resize();
            self.clearCanvas(context);
            // context.clearRect(0, 0, context.canvas.width / shape.page.scaleX, context.canvas.height / shape.page.scaleY);
            context.strokeStyle = shape.getBorderColor();
            context.fillStyle = shape.getBackColor();
            context.lineWidth = shape.borderWidth;
            context.globalAlpha = shape.globalAlpha;
            self.drawStatic(context, x + shape.margin + offsetX, y + shape.margin + offsetY);
            self.drawRegions(context);
            context.restore();
        };
    }();

    self.clearCanvas = context=>{
        // context.clearRect(0, 0, context.canvas.width / shape.page.scaleX, context.canvas.height / shape.page.scaleY);
        context.clearRect(0, 0, context.canvas.clientWidth, context.canvas.clientHeight);
    }
    return self;
};

let simpleCanvasDrawer = (shape, div, x = 0, y = 0) => {
    let self = canvasDrawer(shape, div);
    self.type = "simple canvas drawer";

    self.draw = () => {
        if (self.canvas !== self.parent) {
            self.canvas.id = self.parent.id;
            self.canvas.style = self.parent.style;
            self.parent.remove();
            self.parent = self.canvas;

        }
    }
    self.animationResize = () => self.animationCanvas === undefined;
    self.drawAnimation = () => {
        if (!shape.visible) {
            return;
        }
        if (!shape.enableAnimation) {
            return;
        }
        self.drawDynamic();
    };

    return self;
};

export { canvasDrawer, simpleCanvasDrawer };
