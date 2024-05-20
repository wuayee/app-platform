import {del, get, post, put} from "./http" ;
import { httpUrlMap } from './httpConfig';


const { KNOWLEDGE_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 获取知识库 传参name offset size
export function queryKnowledgeBase(data: {offset: number, size: number, name?: string}) {
  const url = `${KNOWLEDGE_URL}/repos/list`;
  return post(url, data);
}

// 修改知识库
export function modifyKnowledgeBase(data: {name: string, description: string, id: string}) {
  const url = `${KNOWLEDGE_URL}/repos/update`;
  return post(url, data);
}

// 删除知识库
export function deleteKnowledgeBase(id: string) {
  const url = `${KNOWLEDGE_URL}/repos/${id}`;
  return del(url);
}

// 创建知识库
export function createKnowledgeBase(data: {name: string, description: string, owner: string}) {
  const url = `${KNOWLEDGE_URL}/repos`;
  return post(url, data);
}

// 获取知识库详情
export function getKnowledgeBaseById(id: string) {
  const url = `${KNOWLEDGE_URL}/repos/${id}`;
  return get(url);
}

// 获取知识库下知识表
export function getKnowledgeDetailById(id: string, data: {pageNum: number, pageSize: number}) {
  const url = `${KNOWLEDGE_URL}/repos/${id}/tables/query`;
  return post(url, data);
}
