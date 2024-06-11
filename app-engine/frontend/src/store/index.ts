import { configureStore } from '@reduxjs/toolkit';
import collectionStore from './collection/collection';
import appStore from './appInfo/appInfo';
import chatCommonStore from './chatStore/chatStore';
// ...

export const store = configureStore({
  reducer: {
    collectionStore,
    appStore,
    chatCommonStore,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
