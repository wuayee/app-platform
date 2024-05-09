import {domtoimage} from "../common/dom2image.js";
import {shape} from "./shape.js";
import {canvasRectangleDrawer} from "./drawers/rectangleDrawer.js";

/*
缩略图形状：显示画布中所有的图形缩略图，可以点击前往某个区域
辉子 2020-02-25
*/
let thumb = (id, x, y, width, height, parent) => {
    return shape(id, x, y, width, height, parent, "thumb", canvasRectangleDrawer);
};

export let imageSaver = (shape, element) => {
    let self = {};

    let toPng = handle => {
        domtoimage.toPng(element)
            .then(image => {
                // shape.page.drawer.parent.appendChild(image);
                if (handle === undefined) {
                    let save = document.createElement("a");
                    save.download = shape.text + '.png';
                    save.id = "saveImage";
                    save.href = image;
                    save.click();
                } else {
                    // let img = new Image();
                    // img.src = dataUrl;
                    handle(image);
                    //img.onload = ()=>handle(img);
                }
            })
            .catch(error => {
                console.error('save png fail', error);
            });
    };

    self.download = () => toPng();
    self.toImage = handle => toPng(handle);
    self.toPng = handle => toPng(handle);
    self.toCanvas = handle => domtoimage.toCanvas(element).then(canvas=>handle(canvas));
    return self;
};