import {hitRegion} from '../../core/hitRegion.js';

/**
 * 类/接口的展开图标
 * 展开后可以看到所有的方法和字段，收缩后只能看到类名/接口名
 * 辉子 2021
 */
let expanded = (clazz, getx, gety, index) => {
    let self = hitRegion(clazz, getx, gety, () => 10, () => 10, index);
    self.drawStatic = (context, x, y) => {
        context.fillStyle = clazz.getBorderColor();
        context.strokeStyle = "white";
        context.lineWidth = 1;
        context.beginPath();
        context.moveTo(x, y);
        if (!clazz.isExpanded) {
            context.lineTo(x, y + self.height);
            context.lineTo(x + self.width, y + self.height / 2);
            context.rotate(Math.PI / 2);
        } else {
            context.lineTo(x + self.width, y);
            context.lineTo(x + self.width / 2, y + self.height);
        }
        context.closePath();
        context.fill();
        context.stroke();

        // context.lineWidth = 2;
        // context.beginPath();
        // context.moveTo(x+1,y+self.height/2-1);
        // context.lineTo(x+self.width-1,y+self.height/2-1);
        // context.stroke();
        // if(clazz.isExpanded){
        //     context.beginPath();
        //     context.moveTo(x+self.width/2,y+1);
        //     context.lineTo(x+self.width/2,y+self.height-2);
        //     context.stroke();
        // }
    };

    self.drawDynamic = (context, x, y) => {

    };

    self.click = function () {
        clazz.isExpanded = !clazz.isExpanded;
        clazz.invalidate();
    };

    return self;
};

/**
 * uml里package的小图标
 * 辉子 2021
 */
let packageRegion = (pack, getx, gety, index) => {
    let self = hitRegion(pack, getx, gety, () => 24, () => 24, index);
    self.drawStatic = (context, x, y) => {
        context.strokeStyle = pack.getBorderColor();
        context.fillStyle = pack.backColor;
        context.beginPath();
        context.rect(x + 8, y, 14, 20);
        context.stroke();
        context.beginPath();
        context.rect(x + 5, y + 5, 6, 4);
        context.fill();
        context.stroke();
        context.beginPath();
        context.rect(x + 5, y + 12, 6, 4);
        context.fill();
        context.stroke();
    }

    return self;
};

export {expanded, packageRegion};