import {DocumentFragment} from "@ckeditor/ckeditor5-engine";

/**
 * json数据处理器.
 */
export default class JSONDataProcessor {
    constructor(dataController) {
        this.dataController = dataController;
        this.document = dataController.viewDocument;
    }

    /**
     * 将视图对象转换成格式化的json数据.
     *
     * @param viewFragment 视图对象 。
     * @return {string}
     */
    toData(viewFragment) {
        const json = [];
        for (const child of viewFragment) {
            json.push(this._viewToJson(child));
        }
        return JSON.stringify(json);
    }

    _viewToJson(viewElement) {
        const json = {};
        if (viewElement.is('text')) {
            json.data = viewElement.data;
        } else {
            json.name = viewElement.name;

            // 设置属性.
            json.attributes = {};
            for (const [key, value] of viewElement.getAttributes()) {
                json.attributes[key] = value;
            }

            // 设置children.
            json.children = [];
            viewElement.getChildren().map(c => this._viewToJson(c)).forEach(cv => json.children.push(cv));
        }

        return json;
    }

    /**
     * 将数据(json或html)转换为对应的viewFragment.
     *
     * @param data 格式化的json数据.
     * @return {DocumentFragment} view视图对象.
     */
    toView(data) {
        // 如果数据是字符串，那么说明是html格式的数据，这里直接调用dataController的htmlProcessor进行处理.
        if (typeof data === "string") {
            return this.dataController.htmlProcessor.toView(data);
        }

        if (!Array.isArray(data)) {
            throw new Error("JSONDataProcessor#toView:json data must be an array.");
        }
        const modelDocumentFragment = DocumentFragment.fromJSON(data);
        return this.dataController.toView(modelDocumentFragment);
    }
}