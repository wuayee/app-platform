import { isPointInRect, pixelRateAdapter, sleep, uuid } from '../common/util.js';
import { INFO_TYPE, PAGE_MODE, PROGRESS_STATUS } from '../common/const.js';
import Popup from "../common/component/popup.js";
import {layoutCommand} from "./commands.js";

/**
 * shape上面的热区，这些热区可以自己的绘制，有自己的点击事件
 * 辉子 2020-02-25
 */
let hitRegion = (shape, getx, gety, getWidth = (shape, region) => region.width, getHeight = (shape, region) => region.height, index) => {
    let self = {};
    self.dragable = true;
    self.selectable = true;
    self.getSelectable = () => self.selectable;
    self.context = null;
    self.width = self.height = 24;

    self.getx = getx;
    self.gety = gety;
    self.getWidth = getWidth;
    self.getHeight = getHeight;

    self.isType = (type) => {
        self.type === type
    };

    // if (getWidth === undefined) self.getWidth = () => self.width;//为矢量缩放
    // if (getHeight === undefined) self.getHeight = () => self.height;
    self.getBound = () => {
        return {
            x: self.getx(shape, self) + shape.x, y: self.gety(shape, self) + shape.y, width: self.getWidth(shape, self),
            height: self.getHeight(shape, self)
        };
    }
    self.isMouseIn = (x, y) => isPointInRect({ x, y }, self.getBound()) && self.visible;

    self.getId = () => "region-" + self.type + ":" + self.id;

    self.image = null;
    self.drawStatic = (context, x, y, width, height) => {
        if (self.image) {
            context.drawImage(self.image, x, y);
            return;
        }

        if (self.src && self.src !== '') {
            const img = new Image();
            img.src = self.src;
            img.onload = () => {
                self.image = img;
                context.drawImage(self.image, x, y);
            }
        }
    };

    /**
     * 设置src，同时清除image对象.
     *
     * @param src 源.
     */
    self.setSrc = (src) => {
        self.src = src;
        self.image = null;
    };

    self.drawDynamic = (context, x, y, width, height) => {
        context.clearRect(0, 0, width / self.pixelRate.ratioX, height / self.pixelRate.ratioY);
        self.drawStatic(context, x, y, width, height);
    };

    self.draw = (x = 0, y = 0) => {
        self.x = self.getx(shape, self);
        self.y = self.gety(shape, self);

        const width = self.getWidth(shape, self);
        const height = self.getHeight(shape, self);
        self.context.canvas.style.left = (self.x - shape.borderWidth) + "px";
        self.context.canvas.style.top = (self.y - shape.borderWidth) + "px";
        self.context.canvas.width = width;//self.width;
        self.context.canvas.height = height;//self.height;
        self.context.canvas.style.visibility = self.getVisibility() ? "visible" : "hidden";
        self.context.clearRect(0, 0, self.context.canvas.width, self.context.canvas.height);
        self.pixelRate = pixelRateAdapter(self.context, shape.page.scaleX, shape.page.scaleY, shape.page.graph.ignoreHighQuality);

        self.context.restore();
        self.context.save();
        self.context.scale(width / self.width, height / self.height);//region scale;
        self.drawStatic(self.context, x, y, self.width, self.height);
        self.drawDynamic && self.drawDynamic(self.context, x, y, self.width, self.height);
        self.beginX = x;//绘画的起始位置
        self.beginY = y;
    };

    self.shape = shape;
    self.click = () => shape.selectedRegion = self;
    self.dbClick = () => {
    };
    self.cursor = "hand";
    self.id = uuid();
    self.type = "hitRegion";
    self.tag = "";//标识region，用于其他使用
    self.editable = false;
    self.visible = true;
    self.getVisibility = () => self.visible && shape.getVisibility();
    self.getEditRect = () => ({ x: self.x, y: self.y, width: self.width, height: self.height });
    self.text = "";
    self.endEdit = text => {
        self.text = text;
        self.draw(self.beginX, self.beginY);
    };
    self.onMouseDrag = position => {
    };
    self.backColor = "";
    self.fontColor = "";
    self.getRateX = () => self.getWidth(shape, self) / self.width;
    self.getRateY = () => self.getHeight(shape, self) / self.height;
    shape.addRegion(self, index);
    //window.requestAnimationFrame(self.drawAnimation);
    return self;
};

let exampleRegion = (shape, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(shape, getx, gety, getWidth, getHeight, index);
    self.drawStatic = (context, x, y) => {
        const r = 12;
        let x2 = x + r, y2 = y + r;
        context.lineWidth = 1;
        context.beginPath();
        context.arc(x2, y2, 10, 0, 2 * Math.PI);
        context.strokeStyle = shape.getBorderColor();
        context.fillStyle = "white";
        context.fill();
        context.stroke();
    };
    let drawArc = (angle, context) => {
        context.beginPath();
        context.strokeStyle = shape.getBorderColor();
        context.lineWidth = 3;
        context.arc(0, 0, 7, angle, angle + 0.15 * Math.PI);
        context.stroke();
    };

    let degree = 0, step = Math.PI / 200;
    let drawDynamic = self.drawDynamic;
    self.drawDynamic = (context, x, y) => {
        drawDynamic.call(self, context, x, y);
        const r = 12;
        let x1 = x + r, y1 = y + r;
        context.save();
        context.translate(x1, y1);
        context.rotate(degree);
        drawArc(0, context);
        drawArc((1 / 3) * Math.PI, context);
        drawArc((2 / 3) * Math.PI, context);
        drawArc(1 * Math.PI, context);
        drawArc((4 / 3) * Math.PI, context);
        drawArc((5 / 3) * Math.PI, context);
        context.restore();
        degree += step;
        if (degree >= Math.PI * 2) {
            degree = 0;
        }
    };
    return self;
};

const countRegion = page => {
    let self = hitRegion(page, () => 2, () => 2);
    self.type = "sessionCount";
    self.width = 150;
    self.height = 20;
    self.getVisibility = () => page.graph.collaboration.getStatus() === "running";
    self.drawStatic = (context, x, y, width, height) => {
        // todo 暂时注释掉，不显示协同编辑人数
        // let title = "协同编辑人数：";
        // (page.graph.mode === PAGE_MODE.PRESENTATION) && (title = "在线观摩人数：");
        // context.fillStyle = page.graph.setting.fontColor;
        // context.font = "normal bold 12px arial";
        // context.fillText(title + page.sessionCount, 10, 18);
    };
    return self;
}
const lockRegion = (shape, userId, name, index) => {
    const o_x = 4;
    const o_y = -5;
    const width = 10;
    const getx = (shape, region) => o_x + self.index * width;
    const gety = (shape, region) => o_y;
    let self = hitRegion(shape, getx, gety);
    self.type = "lock";
    self.width = width;
    self.height = width;
    self.index = index;
    self.userId = userId;
    // self.getVisibility = () => shape.isLocked();
    //self.selectable = false;
    let img;
    self.drawStatic = (context, x, y, width, height) => {
        let x2 = 0, y2 = gety();
        context.strokeStyle = "white";
        context.fillStyle = "darkorange";

        context.beginPath();
        context.arc(x+5, y+10, 4, 0, 2 * Math.PI);
        context.fill();

        context.beginPath();
        context.arc(x+5, y+3, 3, 0, 2 * Math.PI);
        context.fill();
        context.stroke();
        
        self.text = name+"("+userId+")编辑...";
    };
    return self;
};

const priorityRegion = (shape, getx, gety, index) => {
    const getWidth = (shape, region) => {
        region.width = 12;//12 / shape.page.scaleX;
        return region.width;
    };
    const getHeight = (shape, region) => {
        region.height = 12;//12 / shape.page.scaleY;
        return region.height;
    };
    let self = hitRegion(shape, getx, gety, getWidth, getHeight, index);
    // self.x = 0;
    // self.y = -7;
    self.type = "priority";
    self.text = "优先级";
    self.drawStatic = (context, x, y, width, height) => {
        let red = 255 - (shape.priority - 1) * 25;
        let green = (shape.priority - 1) * 25;
        context.dynamicRect(x, y, width, height, 1, "transparent", "RGB(" + red + "," + green + ",0)", 4, 1);
        context.fillStyle = "white";
        context.font = "normal bold " + (height - 2) + "px arial";
        const w = context.measureText(shape.priority).width;
        context.fillText(shape.priority, x + height / 2 - w / 2, y + height - 2);
    };
    self.drawDynamic = undefined;
    return self;
};

const infoTypeRegion = (shape, getx, gety, index) => {
    let self = priorityRegion(shape, getx, gety, index);
    self.type = "information";
    self.text = "信息";
    self.drawStatic = (context, x, y, width, height) => {
        switch (shape.infoType.name) {
            case INFO_TYPE.INFORMATION.name:
                context.fillStyle = "steelblue";
                context.strokeStyle = "white";
                context.font = "normal bold " + (height - 1) + "px Arial";
                context.beginPath();
                context.arc(x + width / 2, y + height / 2, width / 2, 0, 2 * Math.PI);
                context.fill();
                //context.stroke();
                context.fillStyle = "white";
                const w = context.measureText("i").width;
                context.fillText("i", x + width / 2 - w / 2, y + height * (height - 1) / height);
                break;
            case INFO_TYPE.WARNING.name:
                context.beginPath();
                context.moveTo(x + width / 2, y);
                context.lineTo(x + width, y + height - 1);
                context.lineTo(x, y + height - 1);
                context.closePath();
                context.fillStyle = "yellow";
                context.strokeStyle = "orange";
                context.fill();
                context.lineWidth = 1;
                context.stroke();
                context.beginPath();
                context.lineWidth = 2;
                context.moveTo(width / 2, height * 0.3);
                context.lineTo(width / 2, height * 0.8);
                context.stroke();
                break;
            case INFO_TYPE.ERROR.name:
                context.fillStyle = "red";
                context.strokeStyle = "white";
                context.beginPath();
                context.arc(x + width / 2, y + height / 2, width / 2, 0, 2 * Math.PI);
                context.fill();
                context.drawLine(x + width / 2, y + height * 0.2, x + width / 2, y + height * 0.6, 3, "white");
                context.arc(x + width / 2, y + height * 0.8, 2, 0, 2 * Math.PI);
                context.fillStyle = "white";
                context.fill();
                break;
        }
    };
    return self;
};
const progressRegion = (shape, getx, gety, index) => {
    let self = priorityRegion(shape, getx, gety, index);
    self.type = "progress";
    self.text = "进度";
    self.drawStatic = (context, x, y, width, height) => {
        let x2 = x + width / 2, y2 = y + height / 2;
        context.beginPath();
        context.arc(x2, y2, width / 2, 0, 2 * Math.PI);
        context.fillStyle = "white";
        context.fill();

        context.strokeStyle = context.fillStyle = "darkred";
        context.lineWidth = 1;
        context.beginPath();
        context.moveTo(x2, y2);
        context.lineTo(x2 + width / 2, y2);
        context.arc(x2, y2, width / 2 - 1, 1, 1 + (2 * Math.PI * (shape.progressPercent === 0 ? 1 : shape.progressPercent)));
        context.closePath();
        context.fill();
        // context.stroke();
        context.beginPath();
        context.arc(x2, y2, width / 2 - 1, 0, 2 * Math.PI);
        context.stroke();
    };
    return self;
};

const assignToRegion = (shape, getx, gety, index) => {
    const self = priorityRegion(shape, getx, gety, index);
    self.type = "assignTo";
    self.text = "--";
    self.drawStatic = (context, x, y, width, height) => {
        let x2 = x + width / 2, y2 = y + height / 2;
        context.strokeStyle = "white";
        context.fillStyle = "darkgreen";

        context.beginPath();
        context.arc(x2, y + height, height / 2, 0, 2 * Math.PI);
        context.fill();

        context.beginPath();
        context.arc(x2, y2 - 2, 4, 0, 2 * Math.PI);
        context.fill();
        context.stroke();
    };
    return self;
};

const progressStatusRegion = (shape, getx, gety, index) => {
    let self = priorityRegion(shape, getx, gety, index);
    self.type = "progressStatus";
    self.text = "状态";
    let degree = 0, step = Math.PI / 100, arc = 0.2;
    let drawArc = (angle, context, radius) => {
        context.beginPath();
        context.strokeStyle = shape.progressStatus.color;
        context.lineWidth = 3;
        context.arc(0, 0, radius * 0.8, angle, angle + 0.15 * Math.PI);
        context.stroke();
    };
    self.drawDynamic = (context, x, y, width, height) => {
        width === undefined && (width = self.getWidth(shape, self));
        height === undefined && (height = self.getHeight(shape, self));
        context.clearRect(0, 0, context.canvas.width, context.canvas.height);
        self.drawStatic(context, x, y, width, height);
        let x1 = x + width / 2, y1 = y + height / 2;
        switch (shape.progressStatus.name) {
            case PROGRESS_STATUS.RUNNING.name:
            case PROGRESS_STATUS.DOING.name:
                context.save();
                context.translate(x1, y1);
                context.rotate(degree);
                const r = width / 3;
                drawArc(0, context, r);
                drawArc((1 / 3) * Math.PI, context, r);
                drawArc((2 / 3) * Math.PI, context, r);
                drawArc(1 * Math.PI, context, r);
                drawArc((4 / 3) * Math.PI, context, r);
                drawArc((5 / 3) * Math.PI, context, r);
                context.restore();
                degree += step * (shape.progressStatus.name === PROGRESS_STATUS.RUNNING.name ? 1 : 0.2);
                if (degree >= Math.PI * 2) {
                    degree = 0;
                }
                break;
        }
    };

    self.drawStatic = (context, x, y, width, height) => {
        let x1 = x + 3, y1 = y + 10;
        let x2 = x + width / 2, y2 = y + height / 2;
        context.lineWidth = 1;
        context.beginPath();
        context.arc(x2, y2, width / 2 - context.lineWidth, 0, 2 * Math.PI);
        context.strokeStyle = shape.progressStatus.color;
        context.fillStyle = "white";
        context.fill();
        context.stroke();

        switch (shape.progressStatus.name) {
            case PROGRESS_STATUS.NOTSTARTED.name:
                context.beginPath();
                context.fillStyle = shape.progressStatus.color;
                context.rect(x + width / 3, y + height * 2 / 5, width / 3, height / 5);
                context.fill();
                context.stroke();
                break;
            case PROGRESS_STATUS.PAUSE.name:
                context.fillStyle = shape.progressStatus.color;
                context.fillRect(width * 1 / 3 - width / 12, height / 4, width / 6, height / 2);
                context.fillRect(width * 2 / 3 - width / 12, height / 4, width / 6, height / 2);
                break;
            case PROGRESS_STATUS.COMPLETE.name:
                let sx1 = width / 4, sy1 = height / 2;
                context.beginPath();
                context.moveTo(sx1, sy1);
                context.lineTo(sx1 + width / 6, sy1 + height / 4);
                context.lineTo(sx1 + width / 2, sy1 - height / 6);
                context.lineWidth = 2;
                context.strokeStyle = shape.progressStatus.color;
                context.stroke();
                break;
            case PROGRESS_STATUS.ERROR.name:
                let sx2 = width / 4, sy2 = height / 4;
                context.lineWidth = 2;
                context.strokeStyle = shape.progressStatus.color;
                context.beginPath();
                context.moveTo(sx2, sy2);
                context.lineTo(sx2 + width / 2, sy2 + height / 2);
                context.stroke();
                context.beginPath();
                context.moveTo(sx2 + width / 2, sy2);
                context.lineTo(sx2, sy2 + height / 2);
                context.stroke();
                break;
            case PROGRESS_STATUS.UNKNOWN.name:
                context.lineWidth = 2;
                context.strokeStyle = shape.progressStatus.color;
                context.beginPath();
                context.arc(x + width / 2, y + height / 2, width / 4, 0, 2 * Math.PI);
                context.fillStyle = shape.progressStatus.color;
                context.fill();
                break;
        }
    };
    return self;
};

const shapeCommentRegion = (shape, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(shape, getx, gety, getWidth, getHeight, index);
    self.type = "shape-comment";
    self.text = "评论";
    self.getVisibility = () => shape.comments !== undefined && shape.comments.length > 0 && self.visible && shape.getVisibility();

    self.click = async () => {
        if (shape.comments === undefined) {
            return;
        }
        shape.page.shapes.filter(s => s.isType('shapeComment')).forEach(s => s.stop("pop"));
        shape.comments.forEach(async c => {
            shape.page.showComment(shape, c, "pop");
            await sleep(100);
        });
    };

    self.drawStatic = (context, x, y) => {
        context.fillStyle = "white";
        context.strokeStyle = "green";
        //context.fillRect(x, y, self.width, self.height);
        context.lineWidth = 1;

        context.beginPath();
        context.rect(6, 2, 17, 12);
        context.fill();
        context.stroke();

        context.beginPath();
        context.rect(1, 7, 17, 12);
        context.fill();
        context.stroke();

        context.fillStyle = "green";
        context.font = "normal bold 12px Arial";
        context.fillText("...", 5, 14);

        context.beginPath();
        let x1 = 5, y1 = 19;
        context.moveTo(x1, y1);
        context.lineTo(x1 - 3, y1 + 3);
        context.lineTo(x1 + 6, y1);
        context.closePath();
        context.fill();
    };
    return self;
};

const sharedRegion = (shape, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(shape, getx, gety, getWidth, getHeight, index);
    self.type = "shape-shared";
    self.text = "共享";
    self.getVisibility = () => shape.shared && shape.getVisibility();

    self.drawStatic = (context, x, y) => {
        const R = 4;
        context.strokeStyle = "Teal";
        // context.rect(0,0,23,23);
        // context.stroke();
        context.fillStyle = "Teal";
        context.beginPath();
        context.arc(24 - R - 2, R + 2, R, 0, 2 * Math.PI);
        context.fill();
        context.beginPath();
        context.arc(R + 1, 12, R, 0, 2 * Math.PI);
        context.fill();
        context.beginPath();
        context.arc(24 - R - 2, 24 - R - 2, R, 0, 2 * Math.PI);
        context.fill();

        context.lineWidth = 2;
        context.beginPath();
        context.moveTo(24 - R - 2, R + 2);
        context.lineTo(R + 1, 12);
        context.lineTo(24 - R - 2, 24 - R - 2);
        context.stroke();

        // for (let i = 0; i < 360; i += 10) {
        //     drawArc(i / 180 * Math.PI, context, 12, 12, 9, 6);
        // }
    }
};

const copyCot = (content) => {
    const pEle = document.createElement('p');
    pEle.innerHTML = content || '';
    document.body.appendChild(pEle);

    const range = document.createRange();
    window.getSelection().removeAllRanges();
    range.selectNode(pEle);
    window.getSelection().addRange(range);

    document.execCommand("Copy");
    document.body.removeChild(pEle);
    window.getSelection().removeAllRanges();
}

const sharedButtonRegion = (shape, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(shape, getx, gety, getWidth, getHeight, index);
    self.type = "shape-shared-button";
    self.text = "点击共享";
    self.src = "http://s3-hc-dgg.hics.huawei.com/fit.elsa-g.bucket/a74c33a3815644a4ac82d7f5851db104.png";
    // const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14"><path d="M16395 19384v-1h11v-5h1v6Zm-1 0v-10h1v10Zm3.531-3.785a.384.384 0 0 1-.291-.369c-.01-5.152 3.119-7.113 4.953-7.375v-1.625a.378.378 0 0 1 .232-.352.365.365 0 0 1 .408.074l4.049 3.814a.376.376 0 0 1 0 .553l-4.049 3.814a.365.365 0 0 1-.408.074.378.378 0 0 1-.232-.352v-1.576a5.828 5.828 0 0 0-4.232 3.123.379.379 0 0 1-.342.205.672.672 0 0 1-.09-.006Zm5.426-7.395a.378.378 0 0 1-.375.379 4.359 4.359 0 0 0-2.744 1.363 6.612 6.612 0 0 0-1.752 3.881 6.182 6.182 0 0 1 4.49-2.33.381.381 0 0 1 .381.379v1.1l3.107-2.934-3.107-2.928Z" transform="translate(-16393.5 -19370.232)" style="fill: rgb(255, 255, 255);"></path></svg>`;
    // self.src = "data:image/svg+xml;charset=utf-8," + svg;

    self.getVisibility = () => {
        return shape.shareAble && shape.getVisibility();
    }

    self.click = () => {
        const container = shape.getContainer();
        container.borderWidth = 1;
        container.invalidateAlone();

        const btn = "share-and-copy";
        const content = `<div style="display: flex;">
                            <div>复制并粘贴到其他文档中，即可完成当前内容的共享操作</div>
                            <button id="${btn}" style="width: 87px;height: 28px;background: #fff;border: 1px solid #ccc;border-radius: 3px;color: #0078d4;cursor: pointer;">共享复制</button>
                        </div>`;
        const pop = new Popup(shape.page.div, "共享", content);
        pop.show();

        shape.page.div.addEventListener("click", async (event) => {
            if(event.target.id === btn){
                event.preventDefault();

                const container = shape.getContainer();
                container.shared = true;
                // const copyResult = container.serialize();
                // copyResult.text = container.getData();
                // copyResult.text[0].name = "paragraph";
                const copyResult = shape.page.onCopy([shape.getContainer()]);
                // copyResult.data.filter(d => d.text.name === "heading1").forEach(d => d.text.name = "paragraph");
                if (copyResult) {
                    const data = {
                        type: `elsa`,
                        shapes: copyResult.data
                    }
                    await navigator.clipboard.writeText(JSON.stringify(data)); // navigator仅支持https站点
                }
                pop.cancel();
                // container.shared = true;
                container.borderWidth = 0;
                layoutCommand(shape.page, [{shape: container, shared: true, sharedBy: shape.page.graph.session.id}]).execute();
                console.log("=================================", container)
                // container.invalidateAlone();
            }
        });
    };
};



export {
    hitRegion,
    priorityRegion,
    infoTypeRegion,
    progressRegion,
    progressStatusRegion,
    shapeCommentRegion,
    lockRegion,
    assignToRegion,
    countRegion,
    sharedRegion,
    sharedButtonRegion
};

