import {useLocation} from "react-router-dom";
import {useCallback, useMemo, useState} from "react";
import DOMPurify from 'dompurify';
import { Message } from '@shared/utils/message';

/**
 * 获取url中status的类型
 *
 * @param name status类型
 * @returns {string} status的值
 */
export const getQueryStatus = (name) => {
    return (() => {
        const {search} = useLocation();
        return useMemo(() => new URLSearchParams(search), [search]);
    })().get(name);
};

/**
 * 获取url指定位置的path变量
 *
 * @param index 数组下表
 * @returns {*}
 */
export const getQueryVariableByIndex = (index) => {
    const query = window.location.pathname.split("/");
    return query[index];
}

/**
 * 根据变量名获取url的path变量
 *
 * @param props
 * @param variable path变量
 * @returns {*}
 */
export const getUrlVariableByName = (props, variable) => {
    return props.match.params[variable];
}

/**
* 使用合并状态的自定义 Hook
* @param {Object} initialState - 初始状态
* @return {Array} 返回一个数组，包含当前状态和一个用于更新状态的函数
*/
export const useMergeState = (initialState) => {
  const [state, setState] = useState(initialState);
  const setMergeState = useCallback((newState) => setState((prevState) => ({...prevState, ...newState})), []);
  return [state, setMergeState];
};


/**
* 将传入的文本进行转换处理，去除空格后，如果长度不为0，则进行marked解析和DOMPurify清洁处理，否则返回空字符串
*
* @param {string} text - 需要进行转换的文本
* @return {string} 返回处理后的文本，如果文本为空或者为空字符串，则返回空字符串
*/
export const trans = (text) => {
  text = urlify(text);
  if (text?.trim().length) {
    return DOMPurify.sanitize(text.replaceAll('<br>', ''));
  }
  return '';
}

/**
* 格式化日期时间字符串
* @param {string} dateString - 日期时间字符串
* @return {string} 返回格式化后的日期时间字符串，如果输入为空，则返回'-'
*/
export const formatDateTime = (dateString) => {
  if (!dateString) {
    return '-'
  }
  let date = new Date(dateString);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minute = date.getMinutes();
  const second = date.getSeconds();
  return `${year}-${pad(month)}-${pad(day)} ${pad(hour)}:${pad(minute)}:${pad(
    second
  )}`;
}
function pad(num) {
  return num.toString().padStart(2, '0');
}
/**
* 获取一个随机生成的唯一ID
*
* @export
* @return {string} 返回一个由随机数生成的唯一ID
*/
export function getUiD() {
  const random = window.crypto || window.msCrypto;
  let arrayList = new Uint32Array(3);
  random.getRandomValues(arrayList);
  return random.getRandomValues(arrayList).join('');
}
/**
* 将指定值复制到剪贴板
*
* @param {string} val - 需要复制的值
* @throws {Error} 如果无法访问剪贴板，将抛出错误
*/
export const toClipboard = (val) => {
  if (navigator.clipboard && navigator.permissions && window.self === window.top) {
    navigator.clipboard.writeText(val);
    Message({ type: 'success', content: '复制成功' });
  } else {
    let textArea = document.createElement('textArea');
    textArea.value = val;
    textArea.style.width = 0;
    textArea.style.position = 'fixed';
    textArea.style.left = '-999px';
    textArea.style.top = '10px';
    textArea.setAttribute('readonly', 'readonly');
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
    Message({ type: 'success', content: '复制成功' })
  }
}
/**
* 防抖函数，用于限制高频触发事件的执行频率
*
* @export
* @param {Function} fn 需要防抖的函数
* @param {number} wait 防抖的时间间隔
* @return {Function} 返回一个新的函数，该函数在指定的时间间隔内只执行一次
*/
export const debounce = (fn, wait) => {
  let timer
  return (...args) => {
    const context = this
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
        timer = null
        fn.apply(context,args)
    }, wait)
  }
}
/**
* 判断一个字符串是否为合法的JSON字符串
*
* @param {string} str - 需要判断的字符串
* @return {boolean} 如果是合法的JSON字符串，返回true，否则返回false
*/
export const isJsonString = (str) => {
  try {
    if (typeof JSON.parse(str) === "object") {
      return true;
    }
  } catch (e) {
    return false;
  }
  return false;
}

/**
* 将文本中的URL转换为链接的HTML格式
*
* @param {string} text - 需要转换的文本
* @return {string} 返回转换后的HTML格式文本
*/
export const urlify = (text) => { 
  const urlRegex = /(\b(https?):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig; 
  return text.replace(urlRegex, (url) => { 
    return `<a href="${url}" target="_blank">${url}</a>`; 
  }) 
} 

/**
* 版本号比较函数
* @param {string} preVersion - 需要比较的版本号，默认为空字符串
* @param {string} lastVersion - 参照的版本号，默认为空字符串
* @return {number} 返回比较结果，-1表示preVersion小于lastVersion，1表示preVersion大于lastVersion，0表示两者相等
*/
export const versionStringCompare = (preVersion='', lastVersion='') => {
  let sources = preVersion.split('.');
  let dests = lastVersion.split('.');
  let maxL = Math.max(sources.length, dests.length);
  let result = 0;
  for (let i = 0; i < maxL; i++) {  
    let preValue = sources.length>i ? sources[i]:0;
    let preNum = isNaN(Number(preValue)) ? preValue.charCodeAt() : Number(preValue);
    let lastValue = dests.length>i ? dests[i]:0;
    let lastNum =  isNaN(Number(lastValue)) ? lastValue.charCodeAt() : Number(lastValue);
    if (preNum < lastNum) {
      result = -1;
      break;
    } else if (preNum > lastNum) { 
      result = 1;
      break;
    }
  }
  return result;
}