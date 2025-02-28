/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
const { origin } = window.location;

export const configMap = {
  'development': {
    CONFIGS: [
      {
        node: "startNodeStart",
        urls: {
          customHistoryUrl: ""
        }
      },
      {
        node: "llmNodeState",
        urls: {
          llmModelEndpoint: `/llmApi/v1/api`,
          toolListEndpoint: "/api/jober/store/plugins/tools",
          workflowListEndpoint: ""
        },
        params: {
          tenantId: '',
          appId: '',
        }
      },
      {
        node: "manualCheckNodeState",
        urls: {
          runtimeFormUrl: `/elsaApi/v1/api/${TENANT_ID}/form/type/runtime`
        }
      },
      {
        node: "knowledgeState",
        urls: { knowledgeUrl: `` }
      },
      {
        node: "fitInvokeState",
        urls: {
          serviceListEndpoint: "",
          fitableMetaInfoUrl: ""
        }
      },
      {
        node: "toolInvokeNodeState",
        urls: {
          versionInfo: "/elsaApi/v1/api/{tenant}/app/published/unique_name/{uniqueName}"
        }
      },
      {
        node: "codeNodeState",
        urls: {
          testCodeUrl: `${origin}/api/jober/v1/api/code/run`
        }
      },
      {
        node: 'evaluationAlgorithmsNodeState', urls: {
          evaluationAlgorithmsUrl: '',
        },
      },
      {
        node: 'evaluationTestSetNodeState', urls: {
          datasetUrlPrefix: '',
        },
      },{
        node: 'queryOptimizationNodeState',
        urls: {
          llmModelEndpoint: ``,
        },
      }, {
        node: 'textExtractionNodeState',
        urls: {
          llmModelEndpoint: `/v1/api/fetch/model-list`,
        },
      },{
        node: "questionClassificationNodeCondition",
        urls: {
          llmModelEndpoint: `/llmApi/v1/api/fetch/model-list`,
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
          llmModelEndpoint: `${origin}/api/jober/v1/api`,
          toolListEndpoint: `${origin}/api/jober/store/plugins/tools`,
          workflowListEndpoint: `${origin}/api/jober`,
          aippUrl: `${origin}/api/jober/v1/api`
        },
        params: {
          tenantId: '',
          appId: '',
        }
      },
      {
        node: "manualCheckNodeState",
        urls: {
          runtimeFormUrl: `${origin}/api/jober/v1/api/${TENANT_ID}/form/type/runtime`
        }
      },
      {
        node: "knowledgeState",
        urls: {
          knowledgeUrl: `${origin}/api/jober/v1/api/${TENANT_ID}/knowledge?pageNum=1&pageSize=10`
        }
      },
      {
        node: "toolInvokeNodeState",
        urls: {
          versionInfo: `${origin}/api/jober/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
        }
      },
      {
        node: "codeNodeState",
        urls: {
          testCodeUrl: `${origin}/api/jober/v1/api/code/run`
        }
      },
      {
        node: 'evaluationAlgorithmsNodeState', urls: {
          evaluationAlgorithmsUrl: `${origin}/api/jober/store/plugins/tools/search`,
        },
      },
      {
        node: 'evaluationTestSetNodeState', urls: {
          datasetUrlPrefix: `${origin}/api/jober/eval`,
        },
      },{
        node: 'queryOptimizationNodeState',
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        },
      },{
        node: 'textExtractionNodeState',
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        },
      },
      {
        node: "questionClassificationNodeCondition",
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
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
          customHistoryUrl: ""
        }
      },
      {
        node: "llmNodeState",
        urls: {
          llmModelEndpoint: "",
          toolListEndpoint: `${origin}/api/jober/store/plugins/tools`,
          workflowListEndpoint: ""
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
        urls: { knowledgeUrl: `` }
      },
      {
        node: "fitInvokeState",
        urls: {
          serviceListEndpoint: "",
          fitableMetaInfoUrl: ""
        }
      },
      {
        node: "toolInvokeNodeState",
        urls: {
          versionInfo: `${origin}/api/jober/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
        }
      },
      {
        node: "codeNodeState",
        urls: {
          testCodeUrl: `${origin}/api/jober/v1/api/code/run`
        }
      },
      {
        node: 'evaluationAlgorithmsNodeState', urls: {
          evaluationAlgorithmsUrl: `${origin}/api/jober/store/plugins/tools/search`,
        },
      },
      {
        node: 'evaluationTestSetNodeState', urls: {
          datasetUrlPrefix: `${origin}/api/jober/eval`,
        },
      },{
        node: 'queryOptimizationNodeState',
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        },
      },{
        node: 'textExtractionNodeState',
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        },
      },
      {
        node: "questionClassificationNodeCondition",
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        }
      }
    ]
  },
  'alpha': {
    CONFIGS: [
      {
        node: "startNodeStart",
        urls: {
          customHistoryUrl: ""
        }
      },
      {
        node: "llmNodeState",
        urls: {
          llmModelEndpoint: "",
          toolListEndpoint: "",
          workflowListEndpoint: ""
        },
        params: {
          tenantId: '',
          appId: '',
        }
      },
      {
        node: "manualCheckNodeState",
        urls: {
          runtimeFormUrl: ``
        }
      },
      {
        node: "knowledgeState",
        urls: {
          knowledgeUrl: ``
        }
      },
      {
        node: "fitInvokeState",
        urls: {
          serviceListEndpoint: "",
          fitableMetaInfoUrl: ""
        }
      },
      {
        node: "toolInvokeNodeState",
        urls: {
          versionInfo: `${origin}/api/jober/v1/api/{tenant}/app/published/unique_name/{uniqueName}`
        }
      },
      {
        node: "codeNodeState",
        urls: {
          testCodeUrl: `${origin}/api/jober/v1/api/code/run`
        }
      },
      {
        node: 'evaluationAlgorithmsNodeState', urls: {
          evaluationAlgorithmsUrl: `${origin}/api/jober/store/plugins/tools/search`,
        },
      },
      {
        node: 'evaluationTestSetNodeState', urls: {
          datasetUrlPrefix: `${origin}/api/jober/eval`,
        },
      },{
        node: 'queryOptimizationNodeState',
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        },
      },{
        node: 'textExtractionNodeState',
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        },
      },
      {
        node: "questionClassificationNodeCondition",
        urls: {
          llmModelEndpoint: `${origin}/api/jober/v1/api/fetch/model-list`,
        }
      }
    ]
  },
}
