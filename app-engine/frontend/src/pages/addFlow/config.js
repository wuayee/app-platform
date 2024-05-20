export const configMap = {
  'development': {
      CONFIGS: [
          {
              node: "startNodeStart",
              urls: {
                  customHistoryUrl: "https://jane-beta.huawei.com/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0"
              }
          },
          {
              node: "llmNodeState",
              urls: {
                  llmModelEndpoint: "https://tzaip-beta.paas.huawei.com",
                  toolListEndpoint: "https://jane-beta.huawei.com",
                  workflowListEndpoint: "https://jane-beta.huawei.com"
              },
              params: {
                tenantId: '',
                appId: '',
              }
          },
          {
              node: "manualCheckNodeState",
              urls: {
                  runtimeFormUrl: "https://jane-beta.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/form/type/runtime"
              }
          },
          {
            node: "knowledgeState",
            urls: {knowledgeUrl: "https://jane-beta.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/knowledge"}
          },
          {
            node: "fitInvokeState",
            urls: {
                serviceListEndpoint: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables",
                fitableMetaInfoUrl: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables/"
            }
          }
      ]
  },
  'production': {
      CONFIGS: [
          {
              node: "startNodeStart",
              urls: {
                  customHistoryUrl: "https://jane.huawei.com/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0"
              }
          },
          {
              node: "llmNodeState",
              urls: {
                  llmModelEndpoint: "https://tzaip.paas.huawei.com",
                  toolListEndpoint: "http://10.91.144.110:8080",
                  workflowListEndpoint: "http://10.91.144.110:8080"
              },
              params: {
                tenantId: '',
                appId: '',
              }
          },
          {
              node: "manualCheckNodeState",
              urls: {
                  runtimeFormUrl: "https://jane.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/form/type/runtime"
              }
          },
          {
              node: "knowledgeState",
              urls: {
                  knowledgeUrl: "10.91.144.92:8028/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/knowledge?pageNum=1&pageSize=10"
              }
          },
          {
              node: "fitInvokeState",
              urls: {
                  serviceListEndpoint: "https://tzaip.paas.huawei.com",
                  fitableMetaInfoUrl: "http://10.91.144.110:8080"
              }
          }
      ]
  },
  'gamma': {
      CONFIGS: []
  },
  'beta': {
      CONFIGS: [
          {
              node: "startNodeStart",
              urls: {
                  customHistoryUrl: "https://jane-beta.huawei.com/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0"
              }
          },
          {
              node: "llmNodeState",
              urls: {
                  llmModelEndpoint: "https://tzaip-beta.paas.huawei.com",
                  toolListEndpoint: "http://10.91.144.110:8080",
                  workflowListEndpoint: "http://10.91.144.110:8080"
              },
              params: {
                tenantId: '',
                appId: '',
              }
          },
          {
              node: "manualCheckNodeState",
              urls: {
                  runtimeFormUrl: "https://jane-beta.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/form/type/runtime"
              }
          },
          {
            node: "knowledgeState",
            urls: {knowledgeUrl: "https://jane-beta.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/knowledge"}
          },
          {
            node: "fitInvokeState",
            urls: {
                serviceListEndpoint: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables",
                fitableMetaInfoUrl: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables/"
            }
          }
      ]
  },
  'alpha': {
      CONFIGS: [
          {
              node: "startNodeStart",
              urls: {
                  customHistoryUrl: "https://jane-alpha.huawei.com/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0"
              }
          },
          {
              node: "llmNodeState",
              urls: {
                  llmModelEndpoint: "https://tzaip-beta.paas.huawei.com",
                  toolListEndpoint: "http://10.91.144.110:8080",
                  workflowListEndpoint: "http://10.91.144.110:8080"
              },
              params: {
                tenantId: '',
                appId: '',
              }
          },
          {
              node: "manualCheckNodeState",
              urls: {
                  runtimeFormUrl: "https://jane-alpha.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/form/type/runtime"
              }
          },
          {
              node: "knowledgeState",
              urls: {
                  knowledgeUrl: "10.91.144.92:8028/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/knowledge?pageNum=1&pageSize=10"
              }
          },
          {
              node: "fitInvokeState",
              urls: {
                  serviceListEndpoint: "https://tzaip-beta.paas.huawei.com",
                  fitableMetaInfoUrl: "http://10.91.144.110:8080"
              }
          }
      ]
  },
}
