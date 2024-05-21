/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
import { useCallback, useEffect } from 'react';

/**
 *
 * @param effect debounce回调函数
 * @param deps effect触发的依赖
 * @param delay debounce时间间隔，默认300ms
 */
const useDebouncedEffect = (effect, deps, delay = 300): void => {
  const callback = useCallback(effect, deps);

  useEffect(() => {
    const handler = setTimeout(() => {
      callback();
    }, delay);
    return (): void => {
      clearTimeout(handler);
    };
  }, [callback, delay]);
};

export default useDebouncedEffect;
