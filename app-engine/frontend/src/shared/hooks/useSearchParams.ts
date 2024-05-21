/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
import { useMemo } from 'react';
import { useLocation } from 'react-router-dom';

interface AnyObject {
  [key: string]: any;
}

/**
 * 获取当前页面的查询参数
 */
function useSearchParams(): AnyObject {
  const { search } = useLocation();
  return useMemo(() => {
    const urlSearchParams = new URLSearchParams(search);
    const params = {};
    urlSearchParams.forEach((value, key) => {
      params[key] = value;
    });
    return params;
  }, [search]);
}

export default useSearchParams;
