import {Plugin} from "@ckeditor/ckeditor5-core";
import {throttle} from "lodash-es";
import PositionUtils from "../../utils/PositionUtils";

/**
 * 自定义dragDrop插件.
 *
 * @author z00559346 张越.
 */
export default class CustomizedDragDrop extends Plugin {
    /**
     * @inheritDoc
     */
    static get pluginName() {
        return "ElsaDragDrop";
    }

    /**
     * @inheritDoc
     * @override
     */
    init() {
        this._updateDropMarkerThrottled = throttle((x, y) => this._updateMarker(x, y), 40);
        this._removeDropMarkerDelayed = this._delay(() => this._removeMarker(), 40);
        this._setupDropMarker();
    }

    _delay(func, waitTime) {
        let timer;
        const delayed = (...args) => {
            delayed.cancel();
            timer = setTimeout(() => func(...args), waitTime);
        }
        delayed.cancel = () => {
            clearTimeout(timer);
        };
        return delayed;
    }

    /**
     * 修改elsa相关的drop标记.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     */
    updateMarker(x, y) {
        this._updateDropMarkerThrottled(x, y);
    }

    removeMarker() {
        this._removeDropMarkerDelayed();
    }

    /**
     * @private
     */
    _setupDropMarker() {
        const editor = this.editor;
        editor.conversion.for("editingDowncast").markerToHighlight({
            model: "elsa-drop-target", view: {
                classes: ["ck-clipboard-drop-target-range"]
            }
        });

        editor.conversion.for("editingDowncast").markerToElement({
            model: "elsa-drop-target", view: (data, {writer}) => {
                const inText = editor.model.schema.checkChild(data.markerRange.start, "$text");
                if (!inText) {
                    return;
                }

                const clazz = "ck ck-clipboard-drop-target-position";
                return writer.createUIElement("span", {class: clazz}, function (domDocument) {
                    const domElement = this.toDomElement(domDocument);
                    domElement.append('\u2060', domDocument.createElement("span"), '\u2060');
                    return domElement;
                });
            }
        });
    }

    /**
     * @private
     */
    _removeMarker() {
        this._removeDropMarkerDelayed.cancel();
        this._updateDropMarkerThrottled.cancel();

        const model = this.editor.model;
        if (model.markers.has("elsa-drop-target")) {
            model.change(writer => {
                writer.removeMarker("elsa-drop-target");
            });
        }
    }

    /**
     * @private
     */
    _updateMarker(x, y) {
        const editor = this.editor;
        const markers = editor.model.markers;

        editor.model.change(writer => {
            const modelTargetRange = PositionUtils.toModelRange(x, y, this.editor);
            if (!modelTargetRange) {
                return;
            }

            if (markers.has('elsa-drop-target')) {
                if (!markers.get('elsa-drop-target').getRange().isEqual(modelTargetRange)) {
                    writer.updateMarker('elsa-drop-target', {range: modelTargetRange});
                }
            } else {
                writer.addMarker('elsa-drop-target', {
                    range: modelTargetRange, usingOperation: false, affectsData: false
                });
            }
        });
    }
}