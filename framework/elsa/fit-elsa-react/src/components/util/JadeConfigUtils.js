/**
 * 更新jadeConfig。使用了InvokeInput的组件，在修改输入时需要使用此方法更新jadeConfig
 *
 * @param data jadeConfig
 * @param id 组件对应的id
 * @param changes 变化的数据
 * @return {*} 变化后的新对象
 * @private
 */
export const updateInput = (data, id, changes) => data.map(d => {
    const newD = {...d};
    if (d.id === id) {
        changes.forEach(change => {
            newD[change.key] = change.value;
            // 当对象变为引用或输入时，需要把对象的value置空
            if (change.value === "Reference" || change.value === "Input") {
                newD.value = [];
            }
        });
        return newD;
    }
    // 当处理的数据是对象，并且对象的from是Expand，则递归处理当前数据的属性
    if (newD.from === "Expand") {
        newD.value = updateInput(newD.value, id, changes);
    }
    return newD;
});