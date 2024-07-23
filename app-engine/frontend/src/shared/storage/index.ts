/**
 * 本地缓存的操作对象
 * @type {{set(*=, *=): void, get(*=): (any|undefined), remove(*=): void}}
 */
export const storage = {
  set(key, value) {
    if (value instanceof Object) {
      localStorage.setItem(key, JSON.stringify(value));
    } else {
      localStorage.setItem(key, value);
    }
  },
  get(key) {
    const data = localStorage.getItem(key);
    try {
      return JSON.parse(data);
    } catch (e) {
      // 打印错误日志
      return data;
    }
  },
  remove(key) {
    localStorage.removeItem(key);
  },
};
