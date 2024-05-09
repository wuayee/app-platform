/**
 * 标题序号属性处理器
 *
 * @type {{fontBackgroundColor: SERIAL_NO_ATTRIBUTES_HANDLERS.fontBackgroundColor, fontFamily: SERIAL_NO_ATTRIBUTES_HANDLERS.fontFamily, underline: SERIAL_NO_ATTRIBUTES_HANDLERS.underline, fontSize: SERIAL_NO_ATTRIBUTES_HANDLERS.fontSize, bold: SERIAL_NO_ATTRIBUTES_HANDLERS.bold, strikethrough: SERIAL_NO_ATTRIBUTES_HANDLERS.strikethrough, italic: SERIAL_NO_ATTRIBUTES_HANDLERS.italic, fontColor: SERIAL_NO_ATTRIBUTES_HANDLERS.fontColor}}
 */
export const SERIAL_NO_ATTRIBUTES_HANDLERS = {
    bold: (dom) => {
        const strong = document.createElement("strong");
        strong.append(dom.firstChild);
        dom.append(strong);
    }, italic: (dom) => {
        const italic = document.createElement("i");
        italic.append(dom.firstChild);
        dom.append(italic);
    }, underline: (dom) => {
        const underline = document.createElement("u");
        underline.append(dom.firstChild);
        dom.append(underline);
    }, strikethrough: (dom) => {
        const strikethrough = document.createElement("s");
        strikethrough.append(dom.firstChild);
        dom.append(strikethrough);
    }, fontBackgroundColor: (dom, value) => {
        dom.style.backgroundColor = value;
    }, fontColor: (dom, value) => {
        dom.style.color = value;
    }, fontSize: (dom, value) => {
        dom.style.fontSize = value;
    }, fontFamily: (dom, value) => {
        dom.style.fontFamily = value;
    }
};