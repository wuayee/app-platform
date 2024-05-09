/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

import {omml2mathXsl} from "./omml2mathmlXsl.js";
import {text} from "../../core/rectangle.js";

const xslString = omml2mathXsl;

/**
 * 公式图形，待支持编辑操作
 * @param id
 * @param x
 * @param y
 * @param width
 * @param height
 * @param parent
 * @param drawer
 * @returns {Atom}
 */
const formula = (id, x, y, width, height, parent, drawer) => {
    const self = text(id, x, y, width, height, parent);
    self.type = "formula";
    self.hideText = true;
    const formulaId = `${self.page.div.id}_${self.id}_formula`;

    function createFormula() {
        const dom = document.createElement('div');
        dom.style.position = "absolute";
        dom.style.width = "100%";
        dom.style.height = "100%";
        dom.style.textAlign = "center";
        dom.id = formulaId;
        dom.innerHTML = convertOmmlToMathml(self.tag.data, xslString);
        self.drawer.parent.appendChild(dom);
    }

    self.drawer.drawStatic = (context, x, y) => {
        if(self.tag && self.tag.data && !document.getElementById(formulaId)) {
            createFormula();
        }
    }

    self.addDetection(["tag"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        createFormula();
    });

    return self;
};

const convertOmmlToMathml = (ommlString, xslString) => {
    const parser = new DOMParser();
    const ommlDoc = parser.parseFromString(ommlString, 'application/xml');
    const xslDoc = parser.parseFromString(xslString, 'application/xml');

    const xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(xslDoc);

    const resultDoc = xsltProcessor.transformToDocument(ommlDoc);
    const serializer = new XMLSerializer();
    return serializer.serializeToString(resultDoc);
};


export {formula};