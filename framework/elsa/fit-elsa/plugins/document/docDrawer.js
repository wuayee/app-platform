import {containerDrawer} from '../../core/drawers/containerDrawer.js';

/**
 * document 绘制
 * 辉子 2021-061-0
 */
let docDrawer = (shape, div, x, y) => {
    let self = containerDrawer(shape, div, x, y);
    self.container.remove();
    self.container = self.parent;
    self.containerResize = (width, height) => {
    };//退化container
    return self;
};

export {docDrawer};



