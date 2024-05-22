import {useLocation} from "react-router-dom";
import {useCallback, useMemo, useState} from "react";
import DOMPurify from 'dompurify';
import { marked } from 'marked';
import { Message } from '__shared/utils/message';

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
  if (text?.trim().length) {
    return DOMPurify.sanitize(marked.parse(text.replaceAll('<br>', '')));
  }
  return '';
}

// 日期方法
export const formatDateTime = (dateString) => {
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
    // @ts-ignore
    const context = this
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
        timer = null
        fn.apply(context,args)
    }, wait)
  }
}
