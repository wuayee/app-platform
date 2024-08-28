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

export const BINARY_OPERATOR = {
    LONGER_THAN: "longer than",
    LONGER_THAN_OR_EQUAL: "longer than or equal",
    SHORTER_THAN: "shorter than",
    SHORTER_THAN_OR_EQUAL: "shorter than or equal",
    STARTS_WITH: "starts with",
    ENDS_WITH: "ends with",
    EQUAL: "equal",
    NOT_EQUAL: "not equal",
    CONTAINS: "contains",
    DOES_NOT_CONTAIN: "does not contain",
    GREATER_THAN: "greater than",
    GREATER_THAN_OR_EQUAL: "greater than or equal",
    LESS_THAN: "less than",
    LESS_THAN_OR_EQUAL: "less than or equal",
};

export const VIRTUAL_CONTEXT_NODE = {
    id: "_systemEnv",
    name: "系统上下文"
};

export const CONNECTOR = {
    RADIUS: 6
};

export const SOURCE_PLATFORM = {
    OFFICIAL: "official",
    HUGGING_FACE: "huggingface",
    LLAMA_INDEX: "llamaindex",
    LANG_CHAIN: "langchain",
};

export const DATA_TYPES = {
    STRING: 'String',
    INTEGER: 'Integer',
    BOOLEAN: 'Boolean',
    NUMBER: 'Number',
    OBJECT: 'Object',
    ARRAY: 'Array'
};

export const OBSERVER_STATUS = {
    ENABLE: "enable",
    DISABLE: "disable"
};

export const EVALUATION_ALGORITHM_NODE_CONST = {
    ALGORITHM: "algorithm",
    UNIQUE_NAME: "uniqueName",
    PASS_SCORE: "passScore",
    IS_PASS: "isPass",
    SCORE: "score"
};