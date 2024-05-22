const data = [
  {
    name: 'word抽取算子',
    description: '抽取Word中的文本',
    uniqueName: 'abe840fb-251f-4bc2-a05e-90e943807f66',
    schema: {
      name: '小魔方测试-2',
      description: '',
    },
    runnables: {
      FIT: {
        genericableId: '07b51bd246594c159d403164369ce1db',
      },
    },
    source: null,
    tags: ['APP'],
  },
  {
    name: 'Nas扫盘算子',
    description:
      'Nas扫盘算子',
    uniqueName: '1ec86f6d-c748-4221-854b-265c1ebfa1ff',
    schema: {
      fitableId: 'water.flow.invoke',
      name: '创建应用 v0.1',
      description:
        '创建应用 v0.1，根据用户需求智能生成应用配置，并自动创建应用，支持创建各类助手、数字人、智能体等',
      manualIntervention: true,
      parameters: {
        type: 'object',
        properties: {
          aippId: {
            description: 'the aipp id of the waterFlow tool',
            default: '012429d7f5f640018b63aa4c1b60ad74',
            type: 'string',
          },
          tenantId: {
            description: 'the tenant id of the waterFlow tool',
            default: '31f20efc7e0848deab6a6bc10fc3021e',
            type: 'string',
          },
          inputParams: {
            type: 'object',
            properties: {
              traceId: {
                type: 'string',
              },
              callbackId: {
                type: 'string',
              },
              query: {
                type: 'String',
                description: '这是用户输入的问题',
              },
            },
          },
          version: {
            description: 'the aipp version of the waterFlow tool',
            default: '1.0.0',
            type: 'string',
          },
        },
        required: ['tenantId', 'aippId', 'version', 'inputParams'],
        order: ['tenantId', 'aippId', 'version', 'inputParams'],
      },
      return: {
        type: 'string',
      },
    },
    runnables: {
      FIT: {
        genericableId: '07b51bd246594c159d403164369ce1db',
      },
    },
    source: null,
    tags: ['WATERFLOW'],
  },
  {
    name: 'obs连通性检测算子',
    description: 'obs连通性检测算子',
    uniqueName: 'ff70d6c1-c5c4-4e88-ab4b-179d686584a3',
    schema: {
      fitableId: 'water.flow.invoke',
      name: '创建应用',
      description: '创建应用工具流，根据用户问题智能生成应用配置信息，并自动创建应用',
      manualIntervention: true,
      parameters: {
        type: 'object',
        properties: {
          aippId: {
            description: 'the aipp id of the waterFlow tool',
            default: '981b7259230542f386f60eaf09af5668',
            type: 'string',
          },
          tenantId: {
            description: 'the tenant id of the waterFlow tool',
            default: '31f20efc7e0848deab6a6bc10fc3021e',
            type: 'string',
          },
          inputParams: {
            type: 'object',
            properties: {
              traceId: {
                type: 'string',
              },
              callbackId: {
                type: 'string',
              },
              query: {
                type: 'String',
                description: '这是用户输入的问题',
              },
            },
          },
          version: {
            description: 'the aipp version of the waterFlow tool',
            default: '1.0.0',
            type: 'string',
          },
        },
        required: ['tenantId', 'aippId', 'version', 'inputParams'],
        order: ['tenantId', 'aippId', 'version', 'inputParams'],
      },
      return: {
        type: 'string',
      },
    },
    runnables: {
      FIT: {
        genericableId: '07b51bd246594c159d403164369ce1db',
      },
    },
    source: null,
    tags: ['WATERFLOW'],
  },
  {
    name: '文本质量评估算子',
    description: '文本质量评估算子',
    uniqueName: '1afdab63-62e3-4965-ac91-a7a66099f068',
    schema: {
      name: '应用创建工具',
      description: '这是一个用于应用创建的工具。',
      parameters: {
        type: 'object',
        properties: {
          appInfo: {
            type: 'string',
            description: '应用创建的信息。',
          },
        },
        required: ['appInfo'],
        order: ['appInfo'],
      },
      return: {
        type: 'string',
      },
    },
    runnables: {
      FIT: {
        fitableId: 'default',
        genericableId: 'com.huawei.fit.jober.aipp.tool.create.app',
      },
    },
    source: null,
    tags: ['FIT'],
  },
  {
    name: 'pdf提取算子',
    description: 'pdf提取算子',
    uniqueName: '24474301-937a-4335-a155-1e86d1a48de0',
    schema: {
      name: '财经问题结果生成',
      description: '这是一个用于生成财经类问题结果的工具。',
      parameters: {
        type: 'object',
        properties: {
          condition: {
            type: 'string',
            description: '生成结果的规则。',
          },
          query: {
            type: 'string',
            description: '用户问题。',
          },
        },
        required: ['condition', 'query'],
        order: ['condition', 'query'],
      },
      return: {
        type: 'string',
      },
    },
    runnables: {
      FIT: {
        fitableId: 'default',
        genericableId: 'com.huawei.fit.finance.autoGraph',
      },
    },
    source: null,
    tags: ['FIT'],
  },
  {
    name: 'img调整大小算子',
    description: 'img调整大小算子',
    uniqueName: 'c6c2d272-90e5-4c06-b8fa-5d94930b7532',
    schema: {
      name: '财经问题路由',
      description: '这是一个用于路由财经类问题的工具。',
      parameters: {
        type: 'object',
        properties: {
          query: {
            type: 'string',
            description: '用户问题。',
          },
        },
        required: ['query'],
        order: ['query'],
      },
      return: {
        type: 'object',
        properties: {
          result: {
            type: 'string',
            description: '问题路由结果。',
          },
          matched: {
            type: 'boolean',
            description: '表明问题是否命中规则。',
          },
          completeQuery: {
            type: 'string',
            description: '完整问题。',
          },
        },
      },
    },
    runnables: {
      FIT: {
        fitableId: 'default',
        genericableId: 'com.huawei.fit.finance.router',
      },
    },
    source: null,
    tags: ['FIT'],
  },
];

export default data;