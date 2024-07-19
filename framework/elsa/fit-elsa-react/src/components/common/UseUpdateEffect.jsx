import {useEffect, useRef} from "react";

/**
 * 第一次变化时不执行逻辑的effect.
 * 
 * @param effect 回调.
 * @param deps 依赖.
 * @constructor
 */
export const useUpdateEffect = (effect, deps) => {
    const firstUpdate = useFirstMountState();

    useEffect(() => {
        if (!firstUpdate) {
            return effect();
        }
    }, deps);
};

const useFirstMountState = () => {
    const isFirst = useRef(true);

    if (isFirst.current) {
        isFirst.current = false;
        return true;
    }

    return isFirst.current;
};