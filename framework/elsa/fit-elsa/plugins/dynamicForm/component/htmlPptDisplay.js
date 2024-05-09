import {containerDrawer} from "../../../core/drawers/containerDrawer.js";
import {labelContainer} from "./labelContainer.js";
import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {pptIcon} from "../icons/icons.js";

/**
 * ppt展示组件.
 *
 * @override
 */
const htmlPptDisplay = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent, pptDisplayDrawer);
    self.type = "htmlPptDisplay";
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.serializedFields.add("pptData");
    self.autoFit = false;
    self.height = 42;

    self.componentId = "ppt_" + self.id;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'ppt_' + self.id
    }];

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.text = "已为您制作好PPT";
    };

    /**
     * @override
     */
    self.childAllowed = (s) => {
        return s.isTypeof("htmlLabel");
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        let pptData = data[self.meta[0].key];
        if (!pptData) {
            return;
        }
        self.pptData = typeof pptData === "string" ? JSON.parse(pptData) : pptData;
    };

    return self;
};

/**
 * ppt展示组件绘制器.
 */
const pptDisplayDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "pptDisplayDrawer";

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        self.pptDisplayContainer = document.createElement("div");
        self.pptDisplayContainer.className = "aipp-ppt-display-container";
        self.parent.appendChild(self.pptDisplayContainer);
    };

    const parentResize = self.parentResize;
    self.parentResize = (width, height) => {
        parentResize.apply(self, [width, height]);
        self.parent.style.display = "block";
    };

    /**
     * @override
     */
    self.drawStatic = () => {
        if (!shape.pptData) {
            return;
        }

        const pptItemContainer = document.createElement("div");
        pptItemContainer.className = "aipp-ppt-item-container";

        const pptDisplayIcon = document.createElement("div");
        pptDisplayIcon.className = "aipp-ppt-display-icon";
        pptDisplayIcon.innerHTML = `${pptIcon}`;

        const pptDisplayTitle = document.createElement("div");
        pptDisplayTitle.className = "aipp-ppt-display-title";
        pptDisplayTitle.innerHTML = `${shape.pptData.title}`;

        pptItemContainer.appendChild(pptDisplayIcon);
        pptItemContainer.appendChild(pptDisplayTitle);
        self.pptDisplayContainer.appendChild(pptItemContainer);

        const pptRedirectContainer = document.createElement("div");
        pptRedirectContainer.className = "aipp-ppt-display-redirect-container";

        const pptRedirect = document.createElement("a");
        pptRedirect.className = "ppt-redirect-a";
        pptRedirect.setAttribute('href', `${shape.pptData.url}`);
        pptRedirect.setAttribute('target', '_blank');
        pptRedirect.textContent = "前往修改";

        pptRedirectContainer.appendChild(pptRedirect);
        self.pptDisplayContainer.appendChild(pptRedirectContainer);

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-ppt-display-container {
                padding-top: 8px;
                position: absolute;
                left: 0px;
            }
            .aipp-ppt-item-container {
                height: 48px;
                display: flex;
                justify-content: start;
                align-items: center;
                border-radius: 4px;
                gap: 8px;
                border: 1px solid rgb(215, 216, 218);
                pointer-events: auto;
                width: fit-content;
            }
            
            .aipp-ppt-display-icon {
                display: flex;
                padding-left: 8px;
            }
            
            .aipp-ppt-display-title {
                font-weight: 400;
                font-size: 16px;
                padding-right: 16px;
            }
            
            .aipp-ppt-display-redirect-container {
                height: 22px;
                margin-top: 10px;
                pointer-events: auto;
                font-size: 16px;
                font-weight: 400;
                color: rgb(4, 123, 252);
            }
            
            .ppt-redirect-a:link,
            .ppt-redirect-a:visited {
                color: inherit;
                text-decoration: none;
            }
        `;
        self.parent.appendChild(style);
        shape.height = self.getContentHeight();
        self.pptDisplayContainer.style.top = shape.getLabel().height + 'px';
    };

    self.getContentHeight = () => {
        return self.pptDisplayContainer.offsetHeight + shape.getLabel().height;
    }

    return self;
};

export {htmlPptDisplay};