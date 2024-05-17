import axios from "axios";
import { Message } from '../utils/message';

let baseAxios = axios.create();
baseAxios.defaults.timeout = 100000;
baseAxios.defaults.withCredentials = true;
baseAxios.defaults.headers["Content-Type"] = "application/json";

baseAxios.interceptors.request.use(
  (req) => {
    return req;
  },
  (error) => {
    return Promise.reject(error);
  }
);

baseAxios.interceptors.response.use(
  (response) => {
    if (response.data.code === undefined || response.data.code === 0) {
      return Promise.resolve(response.data);
    }
    Message({ type: 'error', content: response.data.msg || '操作失败' });
  },
  (error) => {
    if (error.response.status === 401) {
      if (error.response.headers["fit-redirect-to-prefix"]) {
        window.location.href = error.response.headers["fit-redirect-to-prefix"] + window.location.href;
      } else {
        Message({ type: 'error', content: '登录失效且headers里面没有跳转登录的url' });
      }
    } else {
      return Promise.reject(error);
    }
  }
);

/**
 * 封装get方法
 * @param url  请求url
 * @param params  请求参数
 * @returns {Promise}
 */
export const get = (url, params = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios
      .get(url, {params, ..._object})
      .then((response) => {
        resolve(response);
      })
      .catch((error) => {
        msag(error);
        reject(error);
      });
  });
};

/**
 * 封装post请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export const post = (url, data, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.post(url, data, _object).then(
      (response) => {
        //关闭进度条
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装patch请求
 * @param url
 * @param data
 * @returns {Promise}
 */
export const patch = (url, data = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.patch(url, data, _object).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装put请求
 * @param url
 * @param data
 * @returns {Promise}
 */

export const put = (url, data = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.put(url, data, _object).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};

/**
 * 封装delete请求
 * @param url
 * @param params
 * @returns {Promise}
 */

export const del = (url, params = {}, _object = {}) => {
  return new Promise((resolve, reject) => {
    baseAxios.delete(url, { params, ..._object}).then(
      (response) => {
        resolve(response);
      },
      (err) => {
        msag(err);
        reject(err);
      }
    );
  });
};


const msag = (err) => {
  if (err && err.response) {
    switch (err.response.status) {
      case 400:
        Message({ type: 'error', content: err.response });
        break;
      case 401:
        Message({ type: 'error', content: "未授权，请登录" });
        break;
      case 403:
        Message({ type: 'error', content: "拒绝访问" });
        break;
      case 404:
        Message({ type: 'error', content: "请求地址出错" });
        break;
      case 408:
        Message({ type: 'error', content: "请求超时" });
        break;
      case 500:
        Message({ type: 'error', content: "服务器内部错误" });
        break;
      case 501:
        Message({ type: 'error', content: "服务未实现" });
        break;
      case 502:
        Message({ type: 'error', content: "网关错误" });
        break;
      case 503:
        Message({ type: 'error', content: "服务不可用" });
        break;
      case 504:
        Message({ type: 'error', content: "网关超时" });
        break;
      case 505:
        Message({ type: 'error', content: "HTTP版本不受支持" });
        break;
      default:
    }
  }
};
