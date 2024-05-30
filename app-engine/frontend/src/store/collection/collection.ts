import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../index';

// 为 slice state 定义一个类型
interface CounterState {
  value: {};
  rawData: any;

  defaultAppId: string;
}

// 使用该类型定义初始 state
const initialState: CounterState = {
  value: {},
  rawData: null,
  defaultAppId: '',
} as CounterState

export const collectionStore = createSlice({
  name: 'counter',
  // `createSlice` 将从 `initialState` 参数推断 state 类型
  initialState,
  reducers: {
    // 设置已收藏应用列表
    setCollectionValue: (state, action: any)=> {
      state.value = {...action.payload};
    },
    setRawData: (state, action: any)=> {
      state.rawData = action.payload;
    },

    // 移除应用
    removeCollectionApp: (state, action: any) => {
      const data: any = state.value;
      data[action.payload] = false;

      state.value = {...data};
    },

    // 添加应用
    addCollectionApp: (state, action: any) => {
      const data: any = state.value;
      data[action.payload] = true;

      state.value = {...data};
    },

    // 设置默认应用
    setDefaultApp:(state, action: any) => {
      state.defaultAppId = action.payload;
    }
  }
})

export const { setCollectionValue, removeCollectionApp, addCollectionApp, setDefaultApp } = collectionStore.actions
// 选择器等其他代码可以使用导入的 `RootState` 类型

export default collectionStore.reducer