import { configureStore } from '@reduxjs/toolkit'
import collectionStore from './collection/collection'
// ...

export const store = configureStore({
  reducer: {
    collectionStore
  }
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch