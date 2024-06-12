export const NODE_STATUS = {
    RUNNING: "running",
    ERROR: "failed",
    SUCCESS: "success",
    DEFAULT: "default",
    UN_RUNNING: "unRunning"
};

export const SECTION_TYPE = {
    CONDITION: "condition",
    DEFAULT: "default"
}

export const UNARY_OPERATOR = {
    IS_EMPTY: "is empty",
    IS_NOT_EMPTY: "is not empty",
    IS_EMPTY_STRING: "is empty string",
    IS_NOT_EMPTY_STRING: "is not empty string",
    IS_TRUE: "is true",
    IS_FALSE: "is false",
    IS_NULL: "is null",
    IS_NOT_NULL: "is not null",
};

export const VIRTUAL_CONTEXT_NODE = {
    id: "_systemEnv",
    name: "系统上下文"
}

export const JADE_MODEL_PREFIX = "jadeModel_";
export const JADE_TASK_ID_PREFIX = "jadeTaskId_";