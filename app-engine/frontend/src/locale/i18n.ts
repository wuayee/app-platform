/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
import i18n from 'i18next';
import { initReactI18next } from "react-i18next";
import en from './en_US.json';
import zh from './zh_CN.json';
import { getCookie } from "@/shared/utils/common";

const resources = {
  en: {
    translation: en
  },
  zh: {
    translation: zh
  }
};

i18n.use(initReactI18next).init({
  resources,
  fallbackLng: getCookie('locale') || 'zh-cn',
  interpolation: {
    escapeValue: false
  },
  returnNull: false
});

export default i18n;
