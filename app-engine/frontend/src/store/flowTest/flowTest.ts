import { createSlice } from '@reduxjs/toolkit';

// 为 slice state 定义一个类型
interface FlowTestI {
  testStatus: string;
  testTime: number;
}

// 使用该类型定义初始 state
const initialState: { testStatus: null; testTime: null } = {
  testStatus: null,
  testTime: null
};

export const flowTestStore = createSlice({
  name: 'app',
  initialState,
  reducers: {
    // 设置调试状态
    setTestStatus: (state, action: any) => {
      state.testStatus = action.payload;
    },
    // 设置调试时间
    setTestTime: (state, action: any) => {
      state.testTime = action.payload;
    }
  }
});

export const { setTestStatus, setTestTime } = flowTestStore.actions;

export default flowTestStore.reducer;
