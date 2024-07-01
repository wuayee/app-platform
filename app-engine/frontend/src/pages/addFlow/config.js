const { origin } = window.location;

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
                  llmModelEndpoint: "https://tzaip-beta.paas.huawei.com/api",
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
                runtimeFormUrl: "/elsaApi/v1/api/31f20efc7e0848deab6a6bc10fc3021e/form/type/runtime"
              }
          },
          {
            node: "knowledgeState",
            urls: {knowledgeUrl: "https://jane-beta.huawei.com/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/knowledge"}
          },
          {
            node: "fitInvokeState",
            urls: {
                serviceListEndpoint: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables",
                fitableMetaInfoUrl: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables/"
            }
          },
          {
              node: "toolInvokeNodeState",
              urls: {
                  versionInfo: "/elsaApi/v1/api/{tenant}/app/published/unique_name/{uniqueName}"
              }
          }
      ]
  },
  'production': {
      CONFIGS: [
          {
              node: "startNodeStart",
              urls: {
                  customHistoryUrl: `${origin}/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0`
              }
          },
          {
              node: "llmNodeState",
              urls: {
                  llmModelEndpoint: `${origin}/api`,
                  toolListEndpoint: origin,
                  workflowListEndpoint: origin,
                  aippUrl:`${origin}/api/jober/v1/api`
              },
              params: {
                tenantId: '',
                appId: '',
              }
          },
          {
              node: "manualCheckNodeState",
              urls: {
                  runtimeFormUrl: `${origin}/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/form/type/runtime`
              }
          },
          {
              node: "knowledgeState",
              urls: {
                  knowledgeUrl: `${origin}/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/knowledge?pageNum=1&pageSize=10`
              }
          },
          {
              node: "toolInvokeNodeState",
              urls: {
                  versionInfo: `${origin}/api/jober/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
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
                runtimeFormUrl: "/elsaApi"
              }
          },
          {
            node: "knowledgeState",
            urls: {knowledgeUrl: "https://jane-beta.huawei.com/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/knowledge"}
          },
          {
            node: "fitInvokeState",
            urls: {
                serviceListEndpoint: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables",
                fitableMetaInfoUrl: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables/"
            }
          },
          {
              node: "toolInvokeNodeState",
              urls: {
                  versionInfo: `${origin}/api/jober/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
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
                  runtimeFormUrl: "https://jane-alpha.huawei.com/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/form/type/runtime"
              }
          },
          {
              node: "knowledgeState",
              urls: {
                  knowledgeUrl: "10.91.144.92:8028/api/jober/v1/api/31f20efc7e0848deab6a6bc10fc3021e/knowledge?pageNum=1&pageSize=10"
              }
          },
          {
              node: "fitInvokeState",
              urls: {
                  serviceListEndpoint: "https://tzaip-beta.paas.huawei.com",
                  fitableMetaInfoUrl: "http://10.91.144.110:8080"
              }
          },
          {
              node: "toolInvokeNodeState",
              urls: {
                  versionInfo: `${origin}/api/jober/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
              }
          }
      ]
  },
}
