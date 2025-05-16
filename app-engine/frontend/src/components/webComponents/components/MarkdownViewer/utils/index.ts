/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

/**
* 将指定值复制到剪贴板
*
* @param {string} val - 需要复制的值
* @throws {Error} 如果无法访问剪贴板，将抛出错误
*/
export const toClipboard = (val) => {
  console.log(val);
  
  if (navigator.clipboard && navigator.permissions && window.self === window.top) {
    navigator.clipboard.writeText(val);
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
  }
}