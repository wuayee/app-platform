import { levitation } from "./levitation.js";

export const LevitationUtils = {
    show: (shape) => {
        /**
         * @maliya 2023.6.9 临时方案，为鸿蒙演示
         * 文档中批注功能，要求：批注的笔记可以跟随文字自适应变化，先只实现一根直线，不考虑圆
         */
        if(shape.needLevitation){
            levitation(shape);
        }
    }, remove: (shape) => {
        if (!shape || shape.name === 'imageTool' || shape.name === 'toolItem') {
            return;
        }
        const data = shape.page.shapes.filter(s => s.name === 'tool' || s.name === 'toolItems');
        data.forEach(s => s.remove());
    }
};
