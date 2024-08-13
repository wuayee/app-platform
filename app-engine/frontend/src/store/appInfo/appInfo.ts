import { createSlice } from '@reduxjs/toolkit';
import { TENANT_ID } from '../../pages/chatPreview/components/send-editor/common/config'
import type { RootState } from '../index';

// 为 slice state 定义一个类型
interface AppI {
  appId: string;
  tenantId: string;
  appInfo: object;
  atAppId: string;
  atAppInfo: object;
}

// 使用该类型定义初始 state
const initialState: AppI = {
  appId: '',
  tenantId: TENANT_ID,
  appInfo: {},
  atAppId: null,
  atAppInfo: null,
};

export const appStore = createSlice({
  name: 'app',
  // `createSlice` 将从 `initialState` 参数推断 state 类型
  initialState,
  reducers: {
    // 设置当前聊天界面Id
    setAppId: (state, action: any) => {
      state.appId = action.payload;
    },
    // 设置当前聊天界面AppInfo
    setAppInfo: (state, action: any) => {
      state.appInfo = action.payload;
    },
    // 设置用户ID
    setTenantId: (state, action: any) => {
      state.tenantId = action.payload;
    },
    // 设置聊天界面@应用Id
    setAtAppId: (state, action: any) => {
      state.atAppId = action.payload;
    },
    // 设置聊天界面@应用AppInfo
    setAtAppInfo: (state, action: any) => {
      state.atAppInfo = action.payload;
    },
  },
});

export const { setAppId, setAppInfo, setTenantId, setAtAppId, setAtAppInfo } = appStore.actions;
// 选择器等其他代码可以使用导入的 `RootState` 类型

export default appStore.reducer;
