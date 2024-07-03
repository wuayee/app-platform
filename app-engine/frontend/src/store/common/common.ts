import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { RootState } from '../index';

// 为 slice state 定义一个类型
interface CommonI {
  historySwitch: boolean;
  dimension: string;
  useMemory: boolean;
}

// 使用该类型定义初始 state
const initialState: CommonI = {
  historySwitch: false,
  dimension: null,
  useMemory: true,
} as CommonI;

export const commonStore = createSlice({
  name: 'app',
  // `createSlice` 将从 `initialState` 参数推断 state 类型
  initialState,
  reducers: {
    // 设置是否使用历史会话
    setHistorySwitch: (state, action: any) => {
      state.historySwitch = action.payload;
    },
    // 设置小魔方产品线维度
    setDimension: (state, action: any) => {
      state.dimension = action.payload;
    },
    // 设置当轮对话是否使用历史会话
    setUseMemory: (state, action: any) => {
      state.useMemory = action.payload;
    },
  },
});

export const { setHistorySwitch, setDimension, setUseMemory } = commonStore.actions;
// 选择器等其他代码可以使用导入的 `RootState` 类型

export default commonStore.reducer;
