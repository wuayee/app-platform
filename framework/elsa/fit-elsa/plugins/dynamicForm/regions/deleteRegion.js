import {hitRegion} from "../../../core/hitRegion.js";
import {formDeleteCommand} from "../commands.js";

/**
 * 删除region，用于删除图形.
 *
 */
export const deleteRegion = (shape, getX = undefined, getY = undefined) => {
    /* 获取x的值. */
    const defaultGetX = () => {
        return shape.width - 40;
    };

    /* 获取y的值. */
    const defaultGetY = (s, r) => {
        return s.height / 2 - r.getHeight() / 2;
    };

    const self = hitRegion(shape, getX ? getX : defaultGetX, getY ? getY : defaultGetY, () => 25, () => 25);
    self.type = "deleteRegion";
    self.disableCanvas = true;
    self.div = null;

    /**
     * @override
     */
    self.draw = () => {
        self.x = self.getx(shape, self);
        self.y = self.gety(shape, self);

        const width = self.getWidth(shape, self);
        const height = self.getHeight(shape, self);

        if (!self.div) {
            self.div = document.createElement("div");
            self.div.id = "delete-region:" + shape.id;
            self.div.style.zIndex = shape.index + 1 + (self.index ? self.index : 1);
            self.div.style.width = width + "px";
            self.div.style.height = height + "px";
            self.div.style.position = "absolute";
            shape.drawer.parent.appendChild(self.div);
        }
        self.div.style.left = self.x + "px";
        self.div.style.top = self.y + "px";
        self.div.innerHTML = "<svg width=\"25.000000\" height=\"25.000000\" viewBox=\"0 0 28 28\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" + "\t<desc>\n" + "\t\t\tCreated with Pixso.\n" + "\t</desc>\n" + "\t<defs/>\n" + "\t<circle id=\"椭圆 9\" cx=\"14.000000\" cy=\"14.000000\" r=\"14.000000\" fill=\"#FFEEED\"/>\n" + "\t<path id=\"path\" d=\"M18.5 11C18.7761 11 19 11.2239 19 11.5L19 19C19 20.0543 18.1841 20.9182 17.1493 20.9945L17 21L11 21C9.94568 21 9.08179 20.1841 9.00549 19.1493L9 19L9 11.5C9 11.2239 9.22388 11 9.5 11C9.77612 11 10 11.2239 10 11.5L10 19C10 19.5128 10.386 19.9355 10.8834 19.9933L11 20L17 20C17.5128 20 17.9355 19.614 17.9933 19.1166L18 19L18 11.5C18 11.2239 18.2239 11 18.5 11ZM12.5333 12C12.7952 12 13.0129 12.1769 13.0581 12.4102L13.0667 17.5C13.0667 17.7761 12.8279 18 12.5333 18C12.2715 18 12.0537 17.8231 12.0085 17.5898L12 12.5C12 12.2239 12.2388 12 12.5333 12ZM15.5333 12C15.7952 12 16.0129 12.1769 16.0581 12.4102L16.0667 17.5C16.0667 17.7761 15.8279 18 15.5333 18C15.2715 18 15.0537 17.8231 15.0085 17.5898L15 12.5C15 12.2239 15.2388 12 15.5333 12ZM16 7C16.5128 7 16.9355 7.38599 16.9933 7.88342L17 8L17 9L20.5 9C20.7761 9 21 9.22388 21 9.5C21 9.77612 20.7761 10 20.5 10L7.5 10C7.22388 10 7 9.77612 7 9.5C7 9.22388 7.22388 9 7.5 9L11 9L11 8C11 7.48718 11.386 7.06445 11.8834 7.00671L12 7L16 7ZM16 8L12 8L12 9L16 9L16 8Z\" fill-rule=\"evenodd\" fill=\"#F66F6A\"/>\n" + "</svg>";
    };

    /**
     * 重写click方法.点击删除图形.
     *
     * @override
     */
    self.click = () => {
        const page = shape.page;
        const cmd = formDeleteCommand(page, [{shape: shape}]);
        cmd.execute(page);
    };

    /**
     * 除了判断region本身能否展示外，还需要判断图形是否允许展示.
     *
     * @override
     */
    const getVisibility = self.getVisibility;
    self.getVisibility = () => {
        return getVisibility.apply(self) && shape.enableRegion();
    };

    return self;
};