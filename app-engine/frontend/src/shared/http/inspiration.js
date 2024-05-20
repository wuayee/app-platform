import { del, get, post, put } from './http';
import { ENV_CONFIG } from '../../config/envConfig';
class InspirationService {
  constructor() {}
  // 查询灵感大全
  queryInspiration(params) {
    return new Promise((resolve, reject) => {
      get(`${ENV_CONFIG.aiUrl}/hisp/api/v1/platform/app/tips/list`, params).then(
        (res) => {
          resolve(res);
        },
        (error) => {
          reject(error);
        }
      );
    });
  }
  // 查询灵感大全
  deleteInspirationByUser(id) {
    return new Promise((resolve, reject) => {
      del(
        `${ENV_CONFIG.aiUrl}/hisp/api/v1/platform/app/tips?idList=${id}`
      ).then(
        (res) => {
          resolve(res);
        },
        (error) => {
          reject(error);
        }
      );
    });
  }
}

export default InspirationService;
