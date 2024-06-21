export const NODE_STATUS = {
    RUNNING: "RUNNING",
    ERROR: "ERROR",
    SUCCESS: "ARCHIVED",
    DEFAULT: "DEFAULT",
    UN_RUNNING: "UN_RUNNING"
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