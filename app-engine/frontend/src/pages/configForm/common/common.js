const workFlowTypes = [
    {
        group:"group1",
        name:"flow1",
        uniqueName:"id1",
        description:"XXX"
    },
    {
        group:"group1",
        name:"flow2",
        uniqueName:"id2",
        description:"XXX"
    }
];

const toolTypes = [
    {
        group:"group1",
        name:"tool1",
        uniqueName:"id1",
        description:"XXX"
    },
    {
        group:"group1",
        name:"tool2",
        uniqueName:"id2",
        description:"XXX"
    }
];

const sourceTypes = [
    {
        value: 'fitable',
        label: '选择服务',
    },
    {
        value: 'input',
        label: '自定义选项',
    },
];

const multiModal = [
    {
        value: 'file',
        label: '文件',
    },
    {
        value: 'image',
        label: '图片',
    },
    {
        value: 'radio',
        label: '音频',
    },
    {
        value: 'video',
        label: '视频',
    },
];

export {toolTypes, multiModal, sourceTypes, workFlowTypes};
