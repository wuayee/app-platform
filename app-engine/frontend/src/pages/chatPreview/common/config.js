export const initChat = {
  content: '',
  type: 'send',
  checked: false
}
export const chatMock = {}
export const chatMock3 = {}
export const codeMock = {}
export const formMock = {
  formAppearance: [{
		name: 'conditionForm'
	}],
  formData: {
    instanceId: "bdbd632606394db6810868d7f3b5dc8a",
    correctSql: JSON.stringify({
      "chartType": ["TABLE"],
      "chartData": ["{\"columns\":[\"2022年1月\",\"2023年1月\",\"同比增长(%)\"],\"rows\":[[\"13.7\",\"36.6\",\"166.3%\"]]}"],
      "chartTitle": ["口径：经营双算，单位：M￥，指标：净销售收入"],
      "chartAnswer": ["根据您的查询条件，结果如下："],
      "answer": "根据您的查询条件，结果如下："
    }),
    dsl: JSON.stringify({
      'currency': 'rmb',
      'loa': 'double',
      'conditions': {
        'period_id': {
          'in': [202301, 202312]
        },
        'oversea_flag': {
          'in': ['国内']
        },
        'domtc_entps_indu_class_cn_name': {
          'in': ['数字政府系统部']
        }
      }
    }),
    dimension: "DSPL",
    useMemory: true
  }
}
export const chatMock2 = {}
export const pduMap = {
  'ICT P&S': 'ICTPS',
  '无线': 'WNPL',
  '云核心网': 'CCNPL',
  '光': 'OBPL',
  '数据存储': 'DSPL',
  '数据通信': 'DCPL',
  '计算': 'CPL',
  '运营商': 'CNBG',
  '政企': 'EBG',
  '终端': 'CBG',
  '中国区': 'CR',
  '欧洲': 'ER',
  '拉美': 'LAR',
  '中东中亚': 'MECAR',
  '亚太': 'APR',
  '北部非洲': 'NAR',
  '南部非洲': 'SAR'
}
