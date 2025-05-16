/*************************************************请勿修改或删除该文件**************************************************/
import { createContext } from 'react';

interface ContextProps {
  data: object,
  terminateClick: () => {},
  resumingClick: () => {},
  restartClick: () => {},
}

export const DataContext = createContext({
  data: null,
  terminateClick: null,
  resumingClick: null,
  restartClick: null,
});