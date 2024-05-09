import {ALIGN, CURSORS, ELSA_NAME_SPACE, LINEMODE} from '../../common/const.js';
import {shape} from '../../core/shape.js';
import {customizedDrawer} from "../../core/drawers/htmlDrawer.js";

export const namespace = ELSA_NAME_SPACE;
/**
 * @maliya 2023.6.6 临时方案，为鸿蒙演示
 * 文档中批注功能，要求：批注的笔记可以跟随文字自适应变化，先只实现一根直线，不考虑圆
 */
const docPen = (id, x, y, width, height, parent) => {
    let self = shape(id, x, y, width, height, parent, drawer);
    self.type = "docPen";
    self.points = [];
    self.needLevitation = false;
    self.backColor = "transparent";
    self.borderWidth = 0;

    self.initialize = () => {
        self.draw = false;
        self.currentLine = document.createElementNS("http://www.w3.org/2000/svg", 'path');
        self.currentLine.setAttribute("points", `0, 0`);
        self.currentLine.setAttribute("d", `M ${0} ${0}`);
        self.currentLine.setAttribute("stroke", "green");
        self.currentLine.setAttribute("stroke-width", "8");
        self.currentLine.setAttribute("fill", "none");
        self.currentLine.style.shapeRendering = 'geometricPrecision';
        self.currentLine.setAttribute("stroke-linecap", "round"); // 使线条末端圆润
        self.currentLine.setAttribute("stroke-linejoin", "round"); // 使线条连接点圆润

        self.drawer.element.appendChild(self.currentLine);
    }

    self.onMouseDrag = position => {
        let d = self.currentLine.getAttribute("d");
        d += ` Q ${position.x-self.x} ${position.y-self.y} ${position.x-self.x} ${position.y-self.y}`;
        self.currentLine.setAttribute("d", d);
        self.points.push([position.x-self.x, position.y-self.y]);
    };

    self.onMouseUp = async position => {
        const box = self.currentLine.getBBox();
        self.width = box.width;
        self.height = box.height;
        self.invalidateAlone();

        self.createReferWithText();
    };

    self.addDetection(["width"], (property, value, preValue) => {
        if (value === preValue) return;
        // 计算缩放比例
        if(preValue === 1) preValue = value;
        const scale = value / preValue;

        const newPoints = self.points.map(([x, y]) => [x * scale, y]);
        // 生成新的路径数据
        const newPathData = newPoints.map(([x, y]) => `Q ${x} ${y} ${x} ${y}`).join(' ');
        self.points = newPoints;

        self.currentLine.setAttribute('d', `M ${0} ${0} ${newPathData}`);
        self.invalidateAlone();
    });

    return self;
};

const drawer = (shape, div, x, y) => {
    let self = customizedDrawer("svg")(shape, div, x, y);
    self.offset = 0;
    self.element.style.overflow = "visible";
    self.element.style.opacity = "0.3";

    return self;
};

export {docPen};