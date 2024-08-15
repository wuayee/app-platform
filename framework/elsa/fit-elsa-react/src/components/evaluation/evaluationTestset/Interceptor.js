import {v4 as uuidv4} from "uuid";

/**
 * 处理数组的拦截器
 *
 * @param chain 责任链对象
 * @return {{}} 处理数组的拦截器
 * @constructor
 */
export const ArrayFilter = (chain) => {
    const self = {};
    self.chain = chain;

    /**
     * 是否匹配
     *
     * @param item 数据
     * @return {arg is any[]} 是否是数组类型
     */
    self.match = (item) => {
        return Array.isArray(item);
    };

    /**
     * 处理数据
     *
     * @param property 属性名
     * @param value 属性值
     * @return {{name, from: string, id: (*|string), type: string, value}} jadeConfig对象
     */
    self.process = (property, value) => {
        return {
            id: uuidv4(),
            name: property,
            type: 'Array',
            from: 'Expand',
            value: self.chain.doFilter(value)
        }
    };

    return self;
};

/**
 * 处理对象的拦截器
 *
 * @param chain 责任链对象
 * @return {{}} 处理对象的拦截器
 * @constructor
 */
export const ObjectFilter = (chain) => {
    const self = {};
    self.chain = chain;

    /**
     * 是否是对象
     *
     * @param item 数据
     * @return {boolean} 是否是对象
     */
    self.match = (item) => {
        return typeof item === 'object';
    };

    /**
     * 处理数据
     *
     * @param property 属性名
     * @param value 属性值
     * @return {{name, from: string, id: (*|string), type: string, value}} jadeConfig对象
     */
    self.process = (property, value) => {
        return {
            id: uuidv4(),
            name: property,
            type: 'Object',
            from: 'Expand',
            value: self.chain.doFilter(value)
        }
    };
    return self;
};

/**
 * 处理字符串的拦截器
 *
 * @param chain 责任链对象
 * @return {{}} 处理字符串的拦截器
 * @constructor
 */
export const StringFilter = (chain) => {
    const self = {};
    self.chain = chain;

    /**
     * 是否是字符串
     *
     * @param item 数据
     * @return {boolean} 是否是字符串
     */
    self.match = (item) => {
        return typeof item === 'string';
    };

    /**
     * 处理数据
     *
     * @param property 属性名
     * @param value 属性值
     * @return {{name, from: string, id: (*|string), type: string, value}} jadeConfig对象
     */
    self.process = (property, value) => {
        return {
            id: uuidv4(),
            name: property,
            type: 'String',
            from: 'Input',
            value: value
        };
    };
    return self;
};

/**
 * 处理数字的拦截器
 *
 * @param chain 责任链对象
 * @return {{}} 处理数字的拦截器
 * @constructor
 */
export const NumberFilter = (chain) => {
    const self = {};
    self.chain = chain;

    /**
     * 是否是数字
     *
     * @param item 数据
     * @return {boolean} 是否是数字
     */
    self.match = (item) => {
        return typeof item === 'number';
    };

    /**
     * 处理数据
     *
     * @param property 属性名
     * @param value 属性值
     * @return {{name, from: string, id: (*|string), type: string, value}} jadeConfig对象
     */
    self.process = (property, value) => {
        return {
            id: uuidv4(),
            name: property,
            type: 'Integer',
            from: 'Input',
            value: value
        };
    };
    return self;
};

/**
 * 责任链
 *
 * @return {{}}
 */
export const filterChain = () => {
    const self = {};
    self.filters = [];

    /**
     * 创建拦截器对象
     *
     * @param type 数据类型
     */
    self.createFilter = (type) => {
        switch (type) {
            case "array": {
                self.filters.push(ArrayFilter(self));
                break;
            }
            case "object": {
                self.filters.push(ObjectFilter(self));
                break;
            }
            case "string": {
                self.filters.push(StringFilter(self));
                break;
            }
            case "number": {
                self.filters.push(NumberFilter(self));
                break;
            }
        }
    };

    /**
     * 拦截器方法
     *
     * @param data 数据
     * @return {unknown[]}
     */
    self.doFilter = (data) => {
        return Object.keys(data).map(property => {
            for (let filter of self.filters) {
                if (filter.match(data[property])) {
                    return filter.process(property, data[property]);
                }
            }
        })
    };

    return self;
};