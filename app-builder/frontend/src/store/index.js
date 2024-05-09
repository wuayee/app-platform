import React, {createContext, useReducer, useContext} from 'react';
import Reducers from "./reducers";
import {Space} from "./modules/common";

const initialState = {
  SPACE: Space
};

const StateContext = createContext();
const DispatchContext = createContext();

function useStateStore() {
    return useContext(StateContext);
}

function useDispatchStore() {
    return useContext(DispatchContext);
}

function StoreProvider({children}) {
    const [state, dispatch] = useReducer(Reducers, initialState);

    return (<>
        <StateContext.Provider value={state}>
            <DispatchContext.Provider value={dispatch}>
                {children}
            </DispatchContext.Provider>
        </StateContext.Provider>
    </>);
}

export {useStateStore, useDispatchStore, StoreProvider};
