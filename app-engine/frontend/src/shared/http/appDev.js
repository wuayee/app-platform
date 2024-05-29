import {del, get, post, put, patch} from "./http";
import { httpUrlMap } from './httpConfig';


const collectUrl = window.localStorage.getItem('COLLECT_URL') || '/api/jober/aipp';

const { JANE_URL, AIPP_URL,APP_URL, COLLECT_URL = collectUrl } = httpUrlMap[process.env.NODE_ENV];

// 获取应用开发列表
export function queryAppDevApi(tenantId,params) {
  return get(`${AIPP_URL}/${tenantId}/app`, params);
}

// 删除应用
export function deleteAppApi(tenantId,appId) {
  return del(`${AIPP_URL}/${tenantId}/app/${appId}`);
}

/**
 * @description 收藏应用
 * @param {Data} data - 收藏应用数据.
 * @property {string} aippId - 应用id.
 * @property {string} usrInfo - 用户信息.
 * @property {boolean} isDefault - 是否默认.
 * */ 
export function collectionApp(data) {
  const url = `${COLLECT_URL}/usr/collection`
  return post(url, data);
}

/**
 * @description 更新收藏
 * @param {any} isDefault - 是否默认.
 * @property {string} id - 收藏的id.
 * 
 * */ 
export function updateCollectionApp(id, isDefault) {
  const url = `${COLLECT_URL}/usr/collection/${id}`
  return patch(url, isDefault);
}

/**
 * @description 查询应用收藏用户数量
 * @property {string} id - 应用id.
 * */ 
export function getCollectionCountApp(id) {
  const url = `${COLLECT_URL}/usr/collection/count/${id}`
  return get(url);
}

/**
 * @description 查询用户收藏的应用
 * @property {string} id - 用户id.
 * */ 
export function getUserCollection(id) {
  const url = `${COLLECT_URL}/usr/collection/app/${id}`
  return get(url);
}

/**
 * @description 取消收藏
 * @property {string} usrInfo - 用户id.
 * @property {string} aippId - 用户appId.
 * */ 
export function cancleUserCollection(data) {
  const url = `${COLLECT_URL}/usr/collection`
  return del(url, data);
}