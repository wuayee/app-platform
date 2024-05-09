import {TEXT_ATTRIBUTES} from "../../common/const.js";

class Normalizer {
    constructor(attribute) {
        this._attribute = attribute;
    }

    /**
     * 对属性值进行规范.
     *
     * @param value 待规范的值.
     * @param shape 图形对象.
     */
    normalize(value, shape) {
    };
}

class DefaultNormalizer extends Normalizer {
    constructor(attribute) {
        super(attribute);
    }

    /**
     * @inheritDoc
     * @override
     */
    normalize(value, shape) {
        return value === null || value === undefined ? shape.get(this._attribute) : value;
    }
}

class FontSizeNormalizer extends Normalizer {
    constructor() {
        super(TEXT_ATTRIBUTES.FONT_SIZE);
    }

    /**
     * @inheritDoc
     * @override
     */
    normalize(value, shape) {
        if (value === null || value === undefined) {
            return shape.get(this._attribute);
        }

        if (typeof value === "string") {
            if (value.endsWith("px")) {
                value = value.replace("px", "");
            }
            return parseInt(value);
        }
        return value;
    }
}

export class NormalizerFactory {
    static get(attribute) {
        switch (attribute) {
            case TEXT_ATTRIBUTES.BOLD:
            case TEXT_ATTRIBUTES.ITALIC:
            case TEXT_ATTRIBUTES.UNDERLINE:
            case TEXT_ATTRIBUTES.STRIKETHROUGH:
            case TEXT_ATTRIBUTES.FONT_FACE:
            case TEXT_ATTRIBUTES.FONT_COLOR:
            case TEXT_ATTRIBUTES.BACK_COLOR:
            case TEXT_ATTRIBUTES.BULLETED_LIST:
            case TEXT_ATTRIBUTES.NUMBERED_LIST:
                return new DefaultNormalizer(attribute);
            case TEXT_ATTRIBUTES.FONT_SIZE:
                return new FontSizeNormalizer();
            default:
                return new DefaultNormalizer(attribute);
        }
    }
}