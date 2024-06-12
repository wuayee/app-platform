import {del, get, post, put} from "./http";
import { httpUrlMap } from './httpConfig';

const { JANE_URL, MODEL_URL, AIPP_URL} = httpUrlMap[process.env.NODE_ENV];

// mock获取生成经营报告数据
const getMockChart = () => {
  return new Promise((resolve, reject) => {
    get(`/src/pages/chatPreview/components/runtimeForm/mock-Chart.json`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
};

// 保存
const saveContent = (tenant_id, instance_id, value) => {
  return new Promise((resolve, reject) => {
    put(`${AIPP_URL}/${tenant_id}/app/instances/${instance_id}`, value).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    })
  })
};

// 获取模型列表接口
const getModels = () => {
  return new Promise((resolve, reject) => {
    get(`${MODEL_URL}/gateway/v1/chat/models`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

// 获取工具 工具流列表
const getTools = (params) => {
  let url = `${JANE_URL}/store/platform/jade/categories/TOOL?pageNum=${params.pageNum}&pageSize=${params.pageSize}`;
  if (params.includeTags) {
    url += `&includeTags=${params.includeTags}`;
  }
  if (params.excludeTags) {
    url += `&excludeTags=${params.excludeTags}`;
  }
  return new Promise((resolve, reject) => {
    get(url).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

const getWaterFlows = (params) => {
  let url = `${AIPP_URL}/${params.tenantId}/store/waterflow?pageNum=${params.pageNum}&pageSize=${params.pageSize}`;
  return new Promise((resolve, reject) => {
    get(url).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}
// 获取知识库
const getKnowledges = (params) => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${params.tenantId}/knowledge/repos?pageNum=${params.pageNum}&pageSize=${params.pageSize}&name=${params.name}`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}
// 获取知识库
const getKnowledgesList = (params) => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${params.tenantId}/knowledge/repos/${params.repoId}/tables?pageNum=${params.pageNum}&pageSize=${params.pageSize}`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}
// 获取灵感大全fitable列表
const getFitables = () => {
  return new Promise((resolve, reject) => {
    get(`${JANE_URL}/jober/v1/api/public/genericables/d01041a73e00ac46bedde08d02c6818e`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
};

// 获取流程配置面板配置
const getAddFlowConfig = (tenant_Id) => {
  return new Promise((resolve, reject) => {
    get(`${AIPP_URL}/${tenant_Id}/store/nodes`).then((res) => {
      resolve(res);
    }, (error) => {
      reject(error);
    });
  });
}

export {
  getMockChart, 
  saveContent, 
  getModels, 
  getTools, 
  getKnowledges, 
  getFitables, 
  getAddFlowConfig, 
  getWaterFlows, 
  getKnowledgesList
};
