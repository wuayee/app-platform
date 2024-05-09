import {excelIcon, pdfIcon, pptIcon, wordIcon} from "./icons.js";

/**
 * 文件图标工厂类.
 *
 * @returns {{}}
 */
export const fileIconFactory = (function() {
    const self = {};

    const icons = {};
    icons["pdf"] = pdfIcon;
    icons["docx"] = wordIcon;
    icons["doc"] = wordIcon;
    icons["ppt"] = pptIcon;
    icons["pptx"] = pptIcon;
    icons["xlsx"] = excelIcon;

    /**
     * 通过文件名获取图标.
     *
     * @param fileName
     * @returns {*}
     */
    self.getIconByFileName = (fileName) => {
        const fileNames = fileName.split(".");
        const suffix = fileNames[fileNames.length - 1];
        return icons[suffix];
    };

    return self;
})();