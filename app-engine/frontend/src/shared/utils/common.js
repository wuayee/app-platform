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
 * 合并多个state和setState的方法
 */
export const useMergeState = (initialState) => {
    const [state, setState] = useState(initialState);
    const setMergeState = useCallback((newState) => setState((prevState) => ({...prevState, ...newState})), []);
    return [state, setMergeState];
};


// 内容格式转换
export const trans = (text) => {
  text = urlify(text);
  try {
    let textItem = JSON.parse(text);
    let textHtml = setFileType(textItem);
    if (textHtml.length) {
      return textHtml;
    }
    return DOMPurify.sanitize(text.replaceAll('<br>', ''));
  } catch {
    if (text?.trim().length) {
      return DOMPurify.sanitize(text.replaceAll('<br>', ''));
    }
  }
  return '';
}

// 消息类型判断
const setFileType = (textItem) => {
  let isObject = Object.prototype.toString.call(textItem) === '[object Object]';
  let isArray = Object.prototype.toString.call(textItem) === '[object Array]';
  let htmlStr = ''
  if (isObject) {
    if (textItem.mime.indexOf('image') !== -1) {
      htmlStr = `<img src=data:${textItem.mime};base64,${textItem.data} />`
    } else if (textItem.mime.indexOf('audio') !== -1) {
      htmlStr = `<audio src=data:${textItem.mime};base64,${textItem.data} controls></audio>`
    }
  } else if (isArray){
    textItem.forEach(item => {
      if (item.mime.indexOf('image') !== -1) {
        htmlStr += `<img src=data:${item.mime};base64,${item.data} /><br />`
      } else if (item.mime.indexOf('audio') !== -1) {
        htmlStr += `<audio src=data:${item.mime};base64,${item.data} controls></audio><br />`
      }
    })
  }
  return htmlStr;
}

// 日期方法
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

export function getUiD() {
  const random = window.crypto || window.msCrypto;
  let arrayList = new Uint32Array(3);
  random.getRandomValues(arrayList);
  return random.getRandomValues(arrayList).join('');
}

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
// 判断是否为json
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

export const urlify = (text) => { 
  const urlRegex = /(\b(https?):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
  return text.replace(urlRegex, (url) => { 
    return `<a href="${url}" target="_blank">${url}</a>`;
  }) 
} 
