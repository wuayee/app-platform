
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import { AippContext } from '../aippIndex/context';
import ChatPreview from './index';

// 公共参数，公共聊天界面
const CommonChat = ({ contextProvider, previewBack }) => {
  return (
    <AippContext.Provider value={{ ...contextProvider }}>
      <ChatPreview previewBack={previewBack} />
    </AippContext.Provider>
  )
};

export default CommonChat;
