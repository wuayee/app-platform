/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import i18n from "@/locale/i18n";

const sourceTypes = [
  {
    value: 'input',
    label: i18n.t('selectionCustom'),
  },
];
const multiModal = [
  {
    value: 'file',
    label: '文件',
  },
  {
    value: 'image',
    label: '图片',
  },
  {
    value: 'radio',
    label: '音频',
  },
  {
    value: 'video',
    label: '视频',
  },
];
const pluginItems = [
  { key: 'FIT', label: '工具' },
  { key: 'WATERFLOW', label: '工具流' }
];
export { multiModal, sourceTypes, pluginItems };
