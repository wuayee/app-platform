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
const categoryItems = [
  { key: 'FIT', label: '推荐' },
  { key: 'NEWS', label: '新闻阅读' },
  { key: 'UTILITY', label: '实用工具' },
  { key: 'SCIENCE', label: '科教' },
  { key: 'SOCIAL', label: '社交' },
  { key: 'LIFE', label: '便民生活' },
  { key: 'WEBSITE', label: '网站搜索' },
  { key: 'GAMES', label: '游戏娱乐' },
  { key: 'FINANCE', label: '财经商务' },
  { key: 'MEDIA', label: '摄影摄像' },
  { key: 'MEETING', label: '会议记录' },
];
const pluginItems = [
  { key: 'FIT', label: '工具' },
  { key: 'WATERFLOW', label: '工具流' }
];
export {toolTypes, multiModal, sourceTypes, workFlowTypes, categoryItems, pluginItems};
