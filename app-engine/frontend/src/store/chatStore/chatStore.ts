import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { RootState } from '../index';

// 为 slice state 定义一个类型
interface ChatCommonI {
  chatRunning: boolean;
  chatList: Array;
  inspirationOpen: boolean;
  openStar: boolean;
  chatType: string;
  chatId: string;
}

// 使用该类型定义初始 state
const initialState: ChatCommonI = {
  chatRunning: false,
  chatList: [],
  inspirationOpen: false,
  openStar: false,
  chatType: null,
  chatId: null,
} as ChatCommonI;

export const chatCommonStore = createSlice({
  name: 'app',
  // `createSlice` 将从 `initialState` 参数推断 state 类型
  initialState,
  reducers: {
    setChatId: (state, action: any) => {
      state.chatId = action.payload;
    },

    setChatList: (state, action: any) => {
      state.chatList = action.payload;
    },

    setOpenStar: (state, action: any) => {
      state.openStar = action.payload;
    },

    setInspirationOpen: (state, action: any) => {
      state.inspirationOpen = action.payload;
    },

    setChatRunning: (state, action: any) => {
      state.chatRunning = action.payload;
    },

    setChatType: (state, action: any) => {
      state.chatType = action.payload;
    },
  },
});

export const {
  setChatId,
  setChatList,
  setOpenStar,
  setInspirationOpen,
  setChatRunning,
  setChatType,
} = chatCommonStore.actions;
// 选择器等其他代码可以使用导入的 `RootState` 类型

export default chatCommonStore.reducer;
