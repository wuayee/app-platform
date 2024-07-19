/**
 * 数组工具类.
 */
export default class ArrayUtil {
    /**
     * 比较两个数组是否相等.
     * 数组中的元素比较地址.
     *
     * @param arr1 数组1对象.
     * @param arr2 数组2对象.
     * @return {boolean} true/false.
     */
    static isEqual(arr1, arr2) {
        if (arr1.length !== arr2.length) {
            return false;
        }
        for (let i = 0; i < arr1.length; i++) {
            if (arr1[i] !== arr2[i]) {
                return false;
            }
        }
        return true;
    }
}