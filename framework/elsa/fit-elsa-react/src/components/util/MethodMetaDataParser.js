import {v4 as uuidv4} from "uuid";

/**
 * 将接口出参元数据信息转换成如下的格式
 *
 * {
 *                 id: "output_" + uuidv4(),
 *                 name: "output",
 *                 type: "Object",
 *                 value: [
 *                     {
 *                         id: uuidv4(),
 *                         name: "age",
 *                         type: "Integer",
 *                         value: "Integer"
 *                     },
 *                     {
 *                         id: uuidv4(),
 *                         name: "height",
 *                         type: "Integer",
 *                         value: "Integer"
 *
 *                     }]
 * }
 *
 * @param input 入参数据，格式如下：{
 *         "type": "object",
 *         "properties": {
 *             "testStr": {
 *                 "type": "object",
 *                 "properties": {
 *                     "P3": {
 *                         "type": "object",
 *                         "properties": {
 *                             "P4": {
 *                                 "type": "string"
 *                             }
 *                         }
 *                     }
 *                 }
 *             }
 *         }
 * }
 *
 * @return {{name: string, id: string, type: string, value: *[]}}
 */
export const convertReturnFormat = input => {
    const processObjectProperty = (name, obj) => {
        const result = [];
        if (obj.type === "object") {
            for (const prop in obj.properties) {
                const property = obj.properties[prop];
                if (property.type === "object") {
                    result.push(...processObjectProperty(prop, property));
                } else {
                    result.push({
                        id: uuidv4(),
                        name: prop,
                        type: property.type.capitalize(),
                        value: property.type.capitalize()
                    });
                }
            }
        }
        return [{
            id: 'output_' + uuidv4(),
            name: name,
            type: "Object",
            value: result
        }];
    };

    const output = {
        id: "output_" + uuidv4(),
        name: "output",
        type: "",
        value: []
    };

    if (input.type === "object") {
        output.type = "Object";
        const isMap = input.hasOwnProperty("additionalProperties") && input.additionalProperties.hasOwnProperty("type");
        if (isMap) {
            return output;
        }
        const properties = input.properties;
        for (const prop in properties) {
            const property = properties[prop];
            if (property.type === "object") {
                output.value.push(...processObjectProperty(prop, property));
            } else {
                output.value.push({
                    id: uuidv4(),
                    name: prop,
                    type: property.type.capitalize(),
                    value: property.type.capitalize()
                });
            }
        }
    } else {
        output.type = input.type.capitalize();
    }

    return output;
};


/**
 * 将接口入参元数据信息转换成如下的格式:
 * {
 *                     id: "p1_" + uuidv4(),
 *                     name: "p1",
 *                     type: "String",
 *                     from: "Reference",
 *                     referenceNode: "",
 *                     referenceId: "",
 *                     referenceKey: "",
 *                     value: []
 *                 }
 *
 * @param param 对象 {propertyName : "p1", property: {
 *                     "type": "string",
 *                     "default": "default_value"
 *                 }}
 * @return {{referenceNode: string, name, from: (string), id: string, type: (string|*), value: *[], referenceId: string, referenceKey: string}}
 */
export const convertParameter = param => {
    const isMap = param.property.hasOwnProperty("additionalProperties") && param.property.additionalProperties.hasOwnProperty("type");
    const result = {
        id: param.propertyName + "_" + uuidv4(),
        name: param.propertyName,
        type: param.property.type === 'object' ? 'Object' : param.property.type.capitalize(),
        description: param.property.description,
        // 对象默认展开，map直接为引用
        from: param.property.type === 'object' ? (isMap ? 'Reference' : 'Expand') : 'Reference',
        isRequired: param.isRequired,
        referenceNode: "",
        referenceId: "",
        referenceKey: "",
        value: []
    };
    if (isMap) {
        result.generic = "Map";
    }

    // 如果入参为map，则不进行属性展开
    if (param.property.type === 'object' && !isMap) {
        const properties = param.property.properties;
        result.value = Object.keys(properties).map(key => {
            return convertParameter({
                propertyName: key,
                property: properties[key]
            });
        });
        result.props = [...result.value];
    }
    return result;
};