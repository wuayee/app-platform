/**
 * 格式化存在占位符('{}')的字符串.
 *
 * @param str 字符串.
 * @param data 需要替换的数据.
 * @return {*} 格式化之后的字符串.
 */
export const formatString = (str, data) => {
    let formatStr = str;
    for(let key in data) {
        formatStr = formatStr.replace(new RegExp("\\{" + key + "\\}", "g"), data[key]);
    }
    return formatStr;
};