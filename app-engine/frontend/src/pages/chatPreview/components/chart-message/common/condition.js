export const conditionMap = {
  财务指标: [
    {
      label: '报表项1级',
      prop: 'report_item_l1_cn_name',
      filterType: 'checkbox',
      options: [
        {
          label: '净销售收入',
          value: '净销售收入',
        },
      ],
      readonly: false,
    },
    {
      label: '报表项2级',
      prop: 'report_item_l2_cn_name',
      filterType: 'checkbox',
      belongs: '财务指标',
      options: [],
      readonly: false,
    },
    {
      label: '报表项3级',
      prop: 'report_item_l3_cn_name',
      filterType: 'checkbox',
      belongs: '财务指标',
      options: [],
      readonly: false,
    },
    {
      label: '报表项4级',
      prop: 'report_item_l4_cn_name',
      filterType: 'checkbox',
      belongs: '财务指标',
      options: [],
      readonly: false,
    },
    {
      label: '报表项5级',
      prop: 'report_item_l5_cn_name',
      filterType: 'checkbox',
      belongs: '财务指标',
      options: [],
      readonly: false,
    },
  ],
  common: [
    {
      label: '会计期',
      prop: 'period_id',
      filterType: 'time',
      options: [],
      readonly: false,
    },
    {
      label: '币种',
      prop: 'currency',
      filterType: 'radio',
      value: '',
      options: [
        {
          label: 'RMB',
          value: 'rmb',
        },
        {
          label: 'USD',
          value: 'usd',
        },
      ],
    },
    {
      label: '口径',
      prop: 'loa',
      value: '',
      filterType: 'radio',
      options: [
        {
          label: '经营',
          value: 'operating',
        },
        {
          label: '经营双算',
          value: 'double',
        },
      ],
      readonly: false,
    },
    {
      label: '条件',
      prop: 'filter',
      filterType: 'compare',
      options: [],
      readonly: false,
    },
  ],
  主维度: [
    // {
    //   label: '重量级团队LV0',
    //   prop: 'lv0_prod_rd_team_cn_name',
    //   filterType: 'checkbox',
    //   options: [
    //     {
    //       label:'IRB',
    //       value:'IRB',
    //     }
    //   ],
    // },
    {
      label: '重量级团队LV1',
      prop: 'lv1_prod_rd_team_cn_name',
      filterType: 'checkbox',
      hide: true,
      options: [
        {
          label: '计算',
          value: '计算',
        },
        {
          label: '数据存储',
          value: '数据存储',
        },
      ],
    },
    {
      label: '重量级团队LV2',
      prop: 'lv2_prod_rd_team_cn_name',
      filterType: 'checkbox',
      belongs: '主维度',
      options: [],
    },
    {
      label: '重量级团队LV3',
      prop: 'lv3_prod_rd_team_cn_name',
      filterType: 'checkbox',
      belongs: '主维度',
      options: [],
    },
    {
      label: '重量级团队LV4',
      prop: 'lv4_prod_rd_team_cn_name',
      filterType: 'checkbox',
      belongs: '主维度',
      options: [],
    },
    // {
    //   label: '产品自定义LV2',
    //   prop: 'lv2_prod_list_cn_name_p',
    //   filterType: 'checkbox',
    //   belongs: '主维度',
    //   options: [],
    // },
    {
      label: '产品自定义LV3',
      prop: 'lv3_prod_list_cn_name_p',
      filterType: 'checkbox',
      belongs: '主维度',
      options: [],
    },
    {
      label: '产品自定义LV4',
      prop: 'lv4_prod_list_cn_name_p',
      filterType: 'checkbox',
      belongs: '主维度',
      options: [],
    },
    // {
    //   label: '产品名称',
    //   prop: 'prod_cn_name',
    //   filterType: 'checkbox',
    //   belongs: '主维度',
    //   options: [],
    // },
  ],
  辅维度: [
    // {
    //   label: '辅产品BG',
    //   prop: 'minor_lv0_prod_list_cn_name',
    //   filterType: 'checkbox',
    //   options:[
    //     {
    //       label:'运营商网络',
    //       value:'运营商网络',
    //     },
    //     {
    //       label:'政企',
    //       value:'政企',
    //     }
    //   ]
    // },
    {
      label: '辅产品LV1',
      prop: 'minor_lv1_prod_list_cn_name',
      filterType: 'checkbox',
      belongs: '辅维度',
      options: [
        {
          label: '企业服务与软件',
          value: '企业服务与软件',
        },
        {
          label: '运营商服务与软件',
          value: '运营商服务与软件',
        },
      ],
    },
    {
      label: '辅产品LV2',
      prop: 'minor_lv2_prod_list_cn_name',
      filterType: 'checkbox',
      belongs: '辅维度',
      options: [],
    },
    // {
    //   label: '辅产品LV3',
    //   prop: 'minor_lv3_prod_list_code',
    //   filterType: 'checkbox',
    //   belongs: '辅维度',
    //   options:[]
    // },
    // {
    //   label: '辅产品LV4',
    //   prop: 'minor_lv4_prod_list_code',
    //   filterType: 'checkbox',
    //   belongs: '辅维度',
    //   options:[]
    // },
    // {
    //   label: '辅产品OFFERING',
    //   prop: 'minor_offering_cn_name',
    //   filterType: 'checkbox',
    //   belongs: '辅维度',
    //   options:[]
    // },
  ],
  区域: [
    {
      label: '国内海外标识',
      prop: 'oversea_flag',
      filterType: 'checkbox',
      options: [
        {
          label: '国内',
          value: '国内',
        },
        {
          label: '海外',
          value: '海外',
        },
      ],
      readonly: false,
    },
    {
      label: '地区部',
      prop: 'region_cn_name',
      filterType: 'checkbox',
      belongs: '区域',
      options: [],
    },
    {
      label: '代表处',
      prop: 'repoffice_cn_name',
      filterType: 'checkbox',
      belongs: '区域',
      options: [],
    },
    {
      label: '办事处',
      prop: 'office_cn_name',
      filterType: 'checkbox',
      belongs: '区域',
      options: [],
    },
    {
      label: '国家',
      prop: 'country_cn_name',
      filterType: 'checkbox',
      belongs: '区域',
      options: [],
    },
  ],
  BG: [
    {
      label: 'BG名称',
      prop: 'sales_lv0_prod_list_cn_name',
      filterType: 'checkbox',
      options: [
        {
          label: '产业间关联交易',
          value: '产业间关联交易',
        },
        {
          label: '运营商网络',
          value: '运营商网络',
        },
        {
          label: '政企',
          value: '政企',
        },
      ],
      readonly: false,
    },
  ],
  // 公司中文名称: [
  //   {
  //     label: '公司名称',
  //     prop: 'company_cn_name',
  //     filterType: 'checkbox',
  //     options: [
  //       {
  //         label: 'PT Sparkoo Technologies Indonesia',
  //         value: 'PT Sparkoo Technologies Indonesia',
  //       },
  //       {
  //         label: 'PT华为技术投资有限公司',
  //         value: 'PT华为技术投资有限公司',
  //       },
  //       {
  //         label: 'Sparkoo Technologies (Malaysia) Sdn. Bhd.',
  //         value: 'Sparkoo Technologies (Malaysia) Sdn. Bhd.',
  //       },
  //       {
  //         label: 'Sparkoo Technologies (Thailand) Co.. Ltd.',
  //         value: 'Sparkoo Technologies (Thailand) Co.. Ltd.',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Arabia Co.. Ltd.',
  //         value: 'Sparkoo Technologies Arabia Co.. Ltd.',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Chile SpA',
  //         value: 'Sparkoo Technologies Chile SpA',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Deutschland GmbH',
  //         value: 'Sparkoo Technologies Deutschland GmbH',
  //       },
  //       {
  //         label: 'Sparkoo Technologies France S.A.S.U.',
  //         value: 'Sparkoo Technologies France S.A.S.U.',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Hong Kong Co.. Limited',
  //         value: 'Sparkoo Technologies Hong Kong Co.. Limited',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Ireland Co.. Limited',
  //         value: 'Sparkoo Technologies Ireland Co.. Limited',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Singapore Pte. Ltd.',
  //         value: 'Sparkoo Technologies Singapore Pte. Ltd.',
  //       },
  //       {
  //         label: 'Sparkoo Technologies Switzerland Ltd.',
  //         value: 'Sparkoo Technologies Switzerland Ltd.',
  //       },
  //       {
  //         label: 'SPARKOO TECHNOLOGIES PERU S.A.C.',
  //         value: 'SPARKOO TECHNOLOGIES PERU S.A.C.',
  //       },
  //       {
  //         label: 'SPARKOO TECHNOLOGIES SOUTH AFRICA (PTY) LTD',
  //         value: 'SPARKOO TECHNOLOGIES SOUTH AFRICA (PTY) LTD',
  //       },
  //       {
  //         label: 'SPARKOO TECNOLOGIAS DO BRASIL LTDA',
  //         value: 'SPARKOO TECNOLOGIAS DO BRASIL LTDA',
  //       },
  //       {
  //         label: '阿尔及利亚华为电讯有限公司',
  //         value: '阿尔及利亚华为电讯有限公司',
  //       },
  //       {
  //         label: '阿联酋华为技术有限公司',
  //         value: '阿联酋华为技术有限公司',
  //       },
  //       {
  //         label: '阿联酋华为技术有限公司迪拜分公司',
  //         value: '阿联酋华为技术有限公司迪拜分公司',
  //       },
  //       {
  //         label: '阿曼华为技术投资有限公司',
  //         value: '阿曼华为技术投资有限公司',
  //       },
  //       {
  //         label: '阿斯比格有限公司（合并）',
  //         value: '阿斯比格有限公司（合并）',
  //       },
  //       {
  //         label: '埃及华为技术有限公司',
  //         value: '埃及华为技术有限公司',
  //       },
  //       {
  //         label: '爱尔兰华为技术有限公司',
  //         value: '爱尔兰华为技术有限公司',
  //       },
  //       {
  //         label: '澳大利亚华为技术有限公司',
  //         value: '澳大利亚华为技术有限公司',
  //       },
  //       {
  //         label: '巴拿马华为技术股份有限公司',
  //         value: '巴拿马华为技术股份有限公司',
  //       },
  //       {
  //         label: '巴西华为电信管理服务有限公司',
  //         value: '巴西华为电信管理服务有限公司',
  //       },
  //       {
  //         label: '巴西华为电讯合并',
  //         value: '巴西华为电讯合并',
  //       },
  //       {
  //         label: '巴西华为电讯有限公司',
  //         value: '巴西华为电讯有限公司',
  //       },
  //       {
  //         label: '巴西华为服务有限公司',
  //         value: '巴西华为服务有限公司',
  //       },
  //       {
  //         label: '白俄华为技术有限公司',
  //         value: '白俄华为技术有限公司',
  //       },
  //       {
  //         label: '保加利亚华为技术有限责任公司',
  //         value: '保加利亚华为技术有限责任公司',
  //       },
  //       {
  //         label: '北京华为数字技术有限公司',
  //         value: '北京华为数字技术有限公司',
  //       },
  //       {
  //         label: '波兰华为有限公司',
  //         value: '波兰华为有限公司',
  //       },
  //       {
  //         label: '博茨瓦纳华为技术有限公司',
  //         value: '博茨瓦纳华为技术有限公司',
  //       },
  //       {
  //         label: '成都华为技术有限公司',
  //         value: '成都华为技术有限公司',
  //       },
  //       {
  //         label: '丹麦华为技术有限公司',
  //         value: '丹麦华为技术有限公司',
  //       },
  //       {
  //         label: '德国华为技术有限公司',
  //         value: '德国华为技术有限公司',
  //       },
  //       {
  //         label: '第比利斯华为技术有限责任公司',
  //         value: '第比利斯华为技术有限责任公司',
  //       },
  //       {
  //         label: '杜塞尔多夫华为技术有限公司',
  //         value: '杜塞尔多夫华为技术有限公司',
  //       },
  //       {
  //         label: '俄罗斯华为技术有限公司',
  //         value: '俄罗斯华为技术有限公司',
  //       },
  //       {
  //         label: '法国华为技术有限公司',
  //         value: '法国华为技术有限公司',
  //       },
  //       {
  //         label: '菲律宾华为技术有限公司',
  //         value: '菲律宾华为技术有限公司',
  //       },
  //       {
  //         label: '刚果布华为技术有限公司',
  //         value: '刚果布华为技术有限公司',
  //       },
  //       {
  //         label: '哥伦比亚华为技术有限公司',
  //         value: '哥伦比亚华为技术有限公司',
  //       },
  //       {
  //         label: '海思光电子有限公司',
  //         value: '海思光电子有限公司',
  //       },
  //       {
  //         label: '海思技术有限公司',
  //         value: '海思技术有限公司',
  //       },
  //       {
  //         label: '韩国华为技术有限公司',
  //         value: '韩国华为技术有限公司',
  //       },
  //       {
  //         label: '杭州华为企业通信技术有限公司',
  //         value: '杭州华为企业通信技术有限公司',
  //       },
  //       {
  //         label: '荷兰(合并)',
  //         value: '荷兰(合并)',
  //       },
  //       {
  //         label: '荷兰华为技术有限公司',
  //         value: '荷兰华为技术有限公司',
  //       },
  //       {
  //         label: '荷兰华为技术有限公司(合并)',
  //         value: '荷兰华为技术有限公司(合并)',
  //       },
  //       {
  //         label: '荷兰数字能源技术有限公司',
  //         value: '荷兰数字能源技术有限公司',
  //       },
  //       {
  //         label: '洪都拉斯华为技术有限公司',
  //         value: '洪都拉斯华为技术有限公司',
  //       },
  //       {
  //         label: '华技(合并)',
  //         value: '华技(合并)',
  //       },
  //       {
  //         label: '华为埃塞俄比亚有限公司',
  //         value: '华为埃塞俄比亚有限公司',
  //       },
  //       {
  //         label: '华为安驰智行技术有限公司',
  //         value: '华为安驰智行技术有限公司',
  //       },
  //       {
  //         label: '华为电讯对外贸易有限公司',
  //         value: '华为电讯对外贸易有限公司',
  //       },
  //       {
  //         label: '华为服务(香港)有限公司',
  //         value: '华为服务(香港)有限公司',
  //       },
  //       {
  //         label: '华为服务（香港）有限公司-澳门分公司',
  //         value: '华为服务（香港）有限公司-澳门分公司',
  //       },
  //       {
  //         label: '华为国际有限公司',
  //         value: '华为国际有限公司',
  //       },
  //       {
  //         label: '华为机器有限公司',
  //         value: '华为机器有限公司',
  //       },
  //       {
  //         label: '华为技术（贝尔格莱德）有限公司',
  //         value: '华为技术（贝尔格莱德）有限公司',
  //       },
  //       {
  //         label: '华为技术（布兰太尔）有限公司',
  //         value: '华为技术（布兰太尔）有限公司',
  //       },
  //       {
  //         label: '华为技术（斐济）私人有限公司',
  //         value: '华为技术（斐济）私人有限公司',
  //       },
  //       {
  //         label: '华为技术（冈比亚）有限责任公司',
  //         value: '华为技术（冈比亚）有限责任公司',
  //       },
  //       {
  //         label: '华为技术（老挝）单一股东有限公司',
  //         value: '华为技术（老挝）单一股东有限公司',
  //       },
  //       {
  //         label: '华为技术（马来西亚）有限公司',
  //         value: '华为技术（马来西亚）有限公司',
  //       },
  //       {
  //         label: '华为技术（马里）有限公司',
  //         value: '华为技术（马里）有限公司',
  //       },
  //       {
  //         label: '华为技术(蒙古)有限责任公司',
  //         value: '华为技术(蒙古)有限责任公司',
  //       },
  //       {
  //         label: '华为技术（摩洛哥）有限责任公司',
  //         value: '华为技术（摩洛哥）有限责任公司',
  //       },
  //       {
  //         label: '华为技术（尼日尔）有限公司',
  //         value: '华为技术（尼日尔）有限公司',
  //       },
  //       {
  //         label: '华为技术（塞拉利昂）有限公司',
  //         value: '华为技术（塞拉利昂）有限公司',
  //       },
  //       {
  //         label: '华为技术阿尔巴尼亚有限公司',
  //         value: '华为技术阿尔巴尼亚有限公司',
  //       },
  //       {
  //         label: '华为技术阿富汗有限公司',
  //         value: '华为技术阿富汗有限公司',
  //       },
  //       {
  //         label: '华为技术阿联酋有限公司',
  //         value: '华为技术阿联酋有限公司',
  //       },
  //       {
  //         label: '华为技术阿联酋有限公司阿布扎比分公司1',
  //         value: '华为技术阿联酋有限公司阿布扎比分公司1',
  //       },
  //       {
  //         label: '华为技术阿塞拜疆有限责任公司',
  //         value: '华为技术阿塞拜疆有限责任公司',
  //       },
  //       {
  //         label: '华为技术爱沙尼亚有限责任公司',
  //         value: '华为技术爱沙尼亚有限责任公司',
  //       },
  //       {
  //         label: '华为技术安哥拉有限公司',
  //         value: '华为技术安哥拉有限公司',
  //       },
  //       {
  //         label: '华为技术奥地利有限责任公司',
  //         value: '华为技术奥地利有限责任公司',
  //       },
  //       {
  //         label: '华为技术巴基斯坦（私有）有限公司',
  //         value: '华为技术巴基斯坦（私有）有限公司',
  //       },
  //       {
  //         label: '华为技术巴拉圭股份公司',
  //         value: '华为技术巴拉圭股份公司',
  //       },
  //       {
  //         label: '华为技术巴林有限公司',
  //         value: '华为技术巴林有限公司',
  //       },
  //       {
  //         label: '华为技术巴新有限公司',
  //         value: '华为技术巴新有限公司',
  //       },
  //       {
  //         label: '华为技术贝宁有限公司',
  //         value: '华为技术贝宁有限公司',
  //       },
  //       {
  //         label: '华为技术比利时有限公司',
  //         value: '华为技术比利时有限公司',
  //       },
  //       {
  //         label: '华为技术比什凯克有限责任公司',
  //         value: '华为技术比什凯克有限责任公司',
  //       },
  //       {
  //         label: '华为技术波黑有限责任公司',
  //         value: '华为技术波黑有限责任公司',
  //       },
  //       {
  //         label: '华为技术玻利维亚有限公司',
  //         value: '华为技术玻利维亚有限公司',
  //       },
  //       {
  //         label: '华为技术布基纳法索公司',
  //         value: '华为技术布基纳法索公司',
  //       },
  //       {
  //         label: '华为技术多哥有限公司',
  //         value: '华为技术多哥有限公司',
  //       },
  //       {
  //         label: '华为技术多米尼加有限公司',
  //         value: '华为技术多米尼加有限公司',
  //       },
  //       {
  //         label: '华为技术厄瓜多尔有限责任公司',
  //         value: '华为技术厄瓜多尔有限责任公司',
  //       },
  //       {
  //         label: '华为技术芬兰有限责任公司',
  //         value: '华为技术芬兰有限责任公司',
  //       },
  //       {
  //         label: '华为技术服务（广西）有限公司',
  //         value: '华为技术服务（广西）有限公司',
  //       },
  //       {
  //         label: '华为技术服务（湖南）有限公司',
  //         value: '华为技术服务（湖南）有限公司',
  //       },
  //       {
  //         label: '华为技术服务（吉林）有限公司',
  //         value: '华为技术服务（吉林）有限公司',
  //       },
  //       {
  //         label: '华为技术服务（利比亚）有限公司',
  //         value: '华为技术服务（利比亚）有限公司',
  //       },
  //       {
  //         label: '华为技术服务（辽宁）有限公司',
  //         value: '华为技术服务（辽宁）有限公司',
  //       },
  //       {
  //         label: '华为技术服务（云南）有限公司',
  //         value: '华为技术服务（云南）有限公司',
  //       },
  //       {
  //         label: '华为技术服务有限公司',
  //         value: '华为技术服务有限公司',
  //       },
  //       {
  //         label: '华为技术刚果金有限责任公司',
  //         value: '华为技术刚果金有限责任公司',
  //       },
  //       {
  //         label: '华为技术哥斯达黎加股份有限公司',
  //         value: '华为技术哥斯达黎加股份有限公司',
  //       },
  //       {
  //         label: '华为技术管理服务哥伦比亚有限公司',
  //         value: '华为技术管理服务哥伦比亚有限公司',
  //       },
  //       {
  //         label: '华为技术管理服务塞内加尔有限公司',
  //         value: '华为技术管理服务塞内加尔有限公司',
  //       },
  //       {
  //         label: '华为技术管理服务塞内加尔有限公司科特迪瓦分公司',
  //         value: '华为技术管理服务塞内加尔有限公司科特迪瓦分公司',
  //       },
  //       {
  //         label: '华为技术哈萨克斯坦有限公司',
  //         value: '华为技术哈萨克斯坦有限公司',
  //       },
  //       {
  //         label: '华为技术海湾有限公司南苏丹分公司',
  //         value: '华为技术海湾有限公司南苏丹分公司',
  //       },
  //       {
  //         label: '华为技术几内亚有限责任公司',
  //         value: '华为技术几内亚有限责任公司',
  //       },
  //       {
  //         label: '华为技术加拿大有限公司',
  //         value: '华为技术加拿大有限公司',
  //       },
  //       {
  //         label: '华为技术加蓬有限公司',
  //         value: '华为技术加蓬有限公司',
  //       },
  //       {
  //         label: '华为技术柬埔寨有限公司',
  //         value: '华为技术柬埔寨有限公司',
  //       },
  //       {
  //         label: '华为技术津巴布韦有限责任公司',
  //         value: '华为技术津巴布韦有限责任公司',
  //       },
  //       {
  //         label: '华为技术卡萨布兰卡有限责任公司',
  //         value: '华为技术卡萨布兰卡有限责任公司',
  //       },
  //       {
  //         label: '华为技术科特迪瓦有限公司',
  //         value: '华为技术科特迪瓦有限公司',
  //       },
  //       {
  //         label: '华为技术科威特有限责任公司',
  //         value: '华为技术科威特有限责任公司',
  //       },
  //       {
  //         label: '华为技术克罗地亚有限责任公司',
  //         value: '华为技术克罗地亚有限责任公司',
  //       },
  //       {
  //         label: '华为技术黎巴嫩有限责任公司',
  //         value: '华为技术黎巴嫩有限责任公司',
  //       },
  //       {
  //         label: '华为技术立陶宛有限公司',
  //         value: '华为技术立陶宛有限公司',
  //       },
  //       {
  //         label: '华为技术卢布尔雅那有限责任公司',
  //         value: '华为技术卢布尔雅那有限责任公司',
  //       },
  //       {
  //         label: '华为技术卢森堡有限责任公司',
  //         value: '华为技术卢森堡有限责任公司',
  //       },
  //       {
  //         label: '华为技术卢旺达有限公司',
  //         value: '华为技术卢旺达有限公司',
  //       },
  //       {
  //         label: '华为技术罗马尼亚有限责任公司',
  //         value: '华为技术罗马尼亚有限责任公司',
  //       },
  //       {
  //         label: '华为技术马其顿有限公司',
  //         value: '华为技术马其顿有限公司',
  //       },
  //       {
  //         label: '华为技术毛里塔尼亚有限公司',
  //         value: '华为技术毛里塔尼亚有限公司',
  //       },
  //       {
  //         label: '华为技术摩尔多瓦有限责任公司',
  //         value: '华为技术摩尔多瓦有限责任公司',
  //       },
  //       {
  //         label: '华为技术莫桑比克有限公司',
  //         value: '华为技术莫桑比克有限公司',
  //       },
  //       {
  //         label: '华为技术尼泊尔私营有限公司',
  //         value: '华为技术尼泊尔私营有限公司',
  //       },
  //       {
  //         label: '华为技术葡萄牙有限责任公司',
  //         value: '华为技术葡萄牙有限责任公司',
  //       },
  //       {
  //         label: '华为技术日本株式会社',
  //         value: '华为技术日本株式会社',
  //       },
  //       {
  //         label: '华为技术瑞典公司冰岛分公司',
  //         value: '华为技术瑞典公司冰岛分公司',
  //       },
  //       {
  //         label: '华为技术瑞士股份有限公司',
  //         value: '华为技术瑞士股份有限公司',
  //       },
  //       {
  //         label: '华为技术塞内加尔有限公司',
  //         value: '华为技术塞内加尔有限公司',
  //       },
  //       {
  //         label: '华为技术塞浦路斯有限公司',
  //         value: '华为技术塞浦路斯有限公司',
  //       },
  //       {
  //         label: '华为技术斯里兰卡有限责任公司',
  //         value: '华为技术斯里兰卡有限责任公司',
  //       },
  //       {
  //         label: '华为技术斯洛伐克有限责任公司',
  //         value: '华为技术斯洛伐克有限责任公司',
  //       },
  //       {
  //         label: '华为技术塔吉克有限责任公司',
  //         value: '华为技术塔吉克有限责任公司',
  //       },
  //       {
  //         label: '华为技术坦桑尼亚有限公司',
  //         value: '华为技术坦桑尼亚有限公司',
  //       },
  //       {
  //         label: '华为技术特多有限公司',
  //         value: '华为技术特多有限公司',
  //       },
  //       {
  //         label: '华为技术投资公司也门分公司',
  //         value: '华为技术投资公司也门分公司',
  //       },
  //       {
  //         label: '华为技术投资塔什干有限责任公司',
  //         value: '华为技术投资塔什干有限责任公司',
  //       },
  //       {
  //         label: '华为技术投资有限公司',
  //         value: '华为技术投资有限公司',
  //       },
  //       {
  //         label: '华为技术投资有限公司阿根廷代表处',
  //         value: '华为技术投资有限公司阿根廷代表处',
  //       },
  //       {
  //         label: '华为技术投资有限公司巴格达分公司',
  //         value: '华为技术投资有限公司巴格达分公司',
  //       },
  //       {
  //         label: '华为技术投资有限公司突尼斯分公司',
  //         value: '华为技术投资有限公司突尼斯分公司',
  //       },
  //       {
  //         label: '华为技术乌干达有限公司',
  //         value: '华为技术乌干达有限公司',
  //       },
  //       {
  //         label: '华为技术新西兰有限责任公司',
  //         value: '华为技术新西兰有限责任公司',
  //       },
  //       {
  //         label: '华为技术亚美尼亚有限公司',
  //         value: '华为技术亚美尼亚有限公司',
  //       },
  //       {
  //         label: '华为技术仰光有限公司',
  //         value: '华为技术仰光有限公司',
  //       },
  //       {
  //         label: '华为技术伊拉克有限公司',
  //         value: '华为技术伊拉克有限公司',
  //       },
  //       {
  //         label: '华为技术伊斯瓦蒂尼有限公司',
  //         value: '华为技术伊斯瓦蒂尼有限公司',
  //       },
  //       {
  //         label: '华为技术有限公司',
  //         value: '华为技术有限公司',
  //       },
  //       {
  //         label: '华为技术有限公司(分公司合并)',
  //         value: '华为技术有限公司(分公司合并)',
  //       },
  //       {
  //         label: '华为技术有限公司厄瓜多尔分公司',
  //         value: '华为技术有限公司厄瓜多尔分公司',
  //       },
  //       {
  //         label: '华为技术有限公司哥伦比亚分公司',
  //         value: '华为技术有限公司哥伦比亚分公司',
  //       },
  //       {
  //         label: '华为技术有限责任公司',
  //         value: '华为技术有限责任公司',
  //       },
  //       {
  //         label: '华为技术约旦有限公司',
  //         value: '华为技术约旦有限公司',
  //       },
  //       {
  //         label: '华为技术越南有限公司',
  //         value: '华为技术越南有限公司',
  //       },
  //       {
  //         label: '华为技术赞比亚有限公司',
  //         value: '华为技术赞比亚有限公司',
  //       },
  //       {
  //         label: '华为技术乍得有限公司',
  //         value: '华为技术乍得有限公司',
  //       },
  //       {
  //         label: '华为技术中非有限公司',
  //         value: '华为技术中非有限公司',
  //       },
  //       {
  //         label: '华为加纳技术有限公司',
  //         value: '华为加纳技术有限公司',
  //       },
  //       {
  //         label: '华为卡塔尔有限责任公司',
  //         value: '华为卡塔尔有限责任公司',
  //       },
  //       {
  //         label: '华为软件技术有限公司',
  //         value: '华为软件技术有限公司',
  //       },
  //       {
  //         label: '华为数字技术（成都）有限公司',
  //         value: '华为数字技术（成都）有限公司',
  //       },
  //       {
  //         label: '华为数字技术（苏州）有限公司',
  //         value: '华为数字技术（苏州）有限公司',
  //       },
  //       {
  //         label: '华为数字能源技术有限公司',
  //         value: '华为数字能源技术有限公司',
  //       },
  //       {
  //         label: '华为通信萨尔瓦多有限责任公司',
  //         value: '华为通信萨尔瓦多有限责任公司',
  //       },
  //       {
  //         label: '华为云计算合并',
  //         value: '华为云计算合并',
  //       },
  //       {
  //         label: '华为云计算技术有限公司',
  //         value: '华为云计算技术有限公司',
  //       },
  //       {
  //         label: '华为终端(香港)有限公司',
  //         value: '华为终端(香港)有限公司',
  //       },
  //       {
  //         label: '华为终端有限公司',
  //         value: '华为终端有限公司',
  //       },
  //       {
  //         label: '捷克华为技术有限责任公司',
  //         value: '捷克华为技术有限责任公司',
  //       },
  //       {
  //         label: '喀麦隆华为技术有限公司',
  //         value: '喀麦隆华为技术有限公司',
  //       },
  //       {
  //         label: '喀麦隆华为技术有限公司赤道几内亚分公司',
  //         value: '喀麦隆华为技术有限公司赤道几内亚分公司',
  //       },
  //       {
  //         label: '肯尼亚华为技术有限公司',
  //         value: '肯尼亚华为技术有限公司',
  //       },
  //       {
  //         label: '拉脱维亚华为技术有限公司',
  //         value: '拉脱维亚华为技术有限公司',
  //       },
  //       {
  //         label: '廊坊华为云计算技术有限公司',
  //         value: '廊坊华为云计算技术有限公司',
  //       },
  //       {
  //         label: '马达加斯加华为技术有限公司',
  //         value: '马达加斯加华为技术有限公司',
  //       },
  //       {
  //         label: '毛里求斯华为技术有限公司',
  //         value: '毛里求斯华为技术有限公司',
  //       },
  //       {
  //         label: '孟加拉华为技术有限公司',
  //         value: '孟加拉华为技术有限公司',
  //       },
  //       {
  //         label: '秘鲁华为有限公司',
  //         value: '秘鲁华为有限公司',
  //       },
  //       {
  //         label: '墨西哥华为技术有限公司',
  //         value: '墨西哥华为技术有限公司',
  //       },
  //       {
  //         label: '纳米比亚华为技术有限公司',
  //         value: '纳米比亚华为技术有限公司',
  //       },
  //       {
  //         label: '南非BEE合资有限责任公司',
  //         value: '南非BEE合资有限责任公司',
  //       },
  //       {
  //         label: '尼日利亚华为技术有限公司',
  //         value: '尼日利亚华为技术有限公司',
  //       },
  //       {
  //         label: '挪威华为技术有限公司',
  //         value: '挪威华为技术有限公司',
  //       },
  //       {
  //         label: '瑞典研究所(瑞典华为技术有限公司)',
  //         value: '瑞典研究所(瑞典华为技术有限公司)',
  //       },
  //       {
  //         label: '沙特华为技术有限公司',
  //         value: '沙特华为技术有限公司',
  //       },
  //       {
  //         label: '上海华为技术有限公司',
  //         value: '上海华为技术有限公司',
  //       },
  //       {
  //         label: '深圳市海思半导体有限公司',
  //         value: '深圳市海思半导体有限公司',
  //       },
  //       {
  //         label: '苏州华为技术研发有限公司',
  //         value: '苏州华为技术研发有限公司',
  //       },
  //       {
  //         label: '苏州市海思半导体有限公司',
  //         value: '苏州市海思半导体有限公司',
  //       },
  //       {
  //         label: '泰国华为技术有限公司',
  //         value: '泰国华为技术有限公司',
  //       },
  //       {
  //         label: '突尼斯华为技术有限公司',
  //         value: '突尼斯华为技术有限公司',
  //       },
  //       {
  //         label: '危地马拉华为电讯股份公司',
  //         value: '危地马拉华为电讯股份公司',
  //       },
  //       {
  //         label: '委内瑞拉华为技术有限公司',
  //         value: '委内瑞拉华为技术有限公司',
  //       },
  //       {
  //         label: '未知公司',
  //         value: '未知公司',
  //       },
  //       {
  //         label: '文莱华为技术有限公司',
  //         value: '文莱华为技术有限公司',
  //       },
  //       {
  //         label: '乌克兰华为技术有限公司',
  //         value: '乌克兰华为技术有限公司',
  //       },
  //       {
  //         label: '乌拉圭华为技术有限公司',
  //         value: '乌拉圭华为技术有限公司',
  //       },
  //       {
  //         label: '乌兰察布华为云计算技术有限公司',
  //         value: '乌兰察布华为云计算技术有限公司',
  //       },
  //       {
  //         label: '西安华为技术有限公司',
  //         value: '西安华为技术有限公司',
  //       },
  //       {
  //         label: '西班牙华为技术有限公司',
  //         value: '西班牙华为技术有限公司',
  //       },
  //       {
  //         label: '希腊华为有限公司',
  //         value: '希腊华为有限公司',
  //       },
  //       {
  //         label: '香港华为国际有限公司',
  //         value: '香港华为国际有限公司',
  //       },
  //       {
  //         label: '星扬技术有限公司',
  //         value: '星扬技术有限公司',
  //       },
  //       {
  //         label: '匈牙利华为有限公司',
  //         value: '匈牙利华为有限公司',
  //       },
  //       {
  //         label: '牙买加华为技术有限公司',
  //         value: '牙买加华为技术有限公司',
  //       },
  //       {
  //         label: '意大利华为技术有限公司',
  //         value: '意大利华为技术有限公司',
  //       },
  //       {
  //         label: '印度华为电讯分公司合并',
  //         value: '印度华为电讯分公司合并',
  //       },
  //       {
  //         label: '印度华为电讯有限公司',
  //         value: '印度华为电讯有限公司',
  //       },
  //       {
  //         label: '印度华为电讯有限公司香港分公司',
  //         value: '印度华为电讯有限公司香港分公司',
  //       },
  //       {
  //         label: '英国华为技术有限公司',
  //         value: '英国华为技术有限公司',
  //       },
  //       {
  //         label: '源为空',
  //         value: '源为空',
  //       },
  //       {
  //         label: '浙江华为通信技术有限公司',
  //         value: '浙江华为通信技术有限公司',
  //       },
  //       {
  //         label: '智利华为公司',
  //         value: '智利华为公司',
  //       },
  //     ],
  //     readonly: false,
  //   },
  // ],
  CNBG: [
    {
      label: '大T系统部',
      prop: 'top_cust_category_cn_name',
      filterType: 'checkbox',
      options: [
        {
          label: 'America Movil系统部',
          value: 'America Movil系统部',
        },
        {
          label: 'BHARTI系统部',
          value: 'BHARTI系统部',
        },
        {
          label: 'DT系统部',
          value: 'DT系统部',
        },
        {
          label: 'e&系统部',
          value: 'e&系统部',
        },
        {
          label: 'Millicom系统部',
          value: 'Millicom系统部',
        },
        {
          label: 'MTN系统部',
          value: 'MTN系统部',
        },
        {
          label: 'NJJ & Iliad系统部',
          value: 'NJJ & Iliad系统部',
        },
        {
          label: 'Ooredoo系统部',
          value: 'Ooredoo系统部',
        },
        {
          label: 'Orange系统部',
          value: 'Orange系统部',
        },
        {
          label: 'PPF系统部',
          value: 'PPF系统部',
        },
        {
          label: 'STC系统部',
          value: 'STC系统部',
        },
        {
          label: 'Telefonica系统部',
          value: 'Telefonica系统部',
        },
        {
          label: 'Telenor系统部',
          value: 'Telenor系统部',
        },
        {
          label: 'Turkcell系统部',
          value: 'Turkcell系统部',
        },
        {
          label: 'United Group系统部',
          value: 'United Group系统部',
        },
        {
          label: 'Veon系统部',
          value: 'Veon系统部',
        },
        {
          label: 'Viettel系统部',
          value: 'Viettel系统部',
        },
        {
          label: 'Vodafone系统部',
          value: 'Vodafone系统部',
        },
        {
          label: 'ZAIN系统部',
          value: 'ZAIN系统部',
        },
        {
          label: '电信系统部',
          value: '电信系统部',
        },
        {
          label: '广电系统部',
          value: '广电系统部',
        },
        {
          label: '和记系统部',
          value: '和记系统部',
        },
        {
          label: '联通系统部',
          value: '联通系统部',
        },
        {
          label: '铁塔系统部',
          value: '铁塔系统部',
        },
        {
          label: '虚拟系统部',
          value: '虚拟系统部',
        },
        {
          label: '亚太Axiata系统部',
          value: '亚太Axiata系统部',
        },
        {
          label: '亚太Singtel系统部',
          value: '亚太Singtel系统部',
        },
        {
          label: '移动系统部',
          value: '移动系统部',
        },
      ],
    },
    {
      label: 'ACCOUNT客户子网',
      prop: 'acctcust_subsidiary_cn_name',
      filterType: 'checkbox',
      isInterface: true,
      options: [],
    },
    {
      label: '区域系统部',
      prop: 'region_custcatg_cn_name',
      filterType: 'checkbox',
      belongs: 'CNBG',
      options: [],
    },
  ],
  EBG国内: [
    {
      label: '行业（国内）',
      prop: 'domtc_entps_indu_class_cn_name',
      filterType: 'checkbox',
      options: [
        {
          label: '大企业系统部',
          value: '大企业系统部',
        },
        {
          label: '电力系统部',
          value: '电力系统部',
        },
        {
          label: '电网拓展部',
          value: '电网拓展部',
        },
        {
          label: '分销',
          value: '分销',
        },
        {
          label: '国铁大客户部',
          value: '国铁大客户部',
        },
        {
          label: '国网大客户部',
          value: '国网大客户部',
        },
        {
          label: '互联网传媒系统部',
          value: '互联网传媒系统部',
        },
        {
          label: '交通系统部',
          value: '交通系统部',
        },
        {
          label: '教育医疗系统部',
          value: '教育医疗系统部',
        },
        {
          label: '金融系统部',
          value: '金融系统部',
        },
        {
          label: '商业销售部',
          value: '商业销售部',
        },
        {
          label: '数据中心能源拓展部',
          value: '数据中心能源拓展部',
        },
        {
          label: '数字政府系统部',
          value: '数字政府系统部',
        },
        {
          label: '销售业务部',
          value: '销售业务部',
        },
        {
          label: '油气矿山系统部',
          value: '油气矿山系统部',
        },
        {
          label: '源为空',
          value: '源为空',
        },
        {
          label: '智能电动拓展部',
          value: '智能电动拓展部',
        },
        {
          label: '智能光伏拓展部',
          value: '智能光伏拓展部',
        },
        {
          label: '智能制造系统部',
          value: '智能制造系统部',
        },
        {
          label: '综合系统部',
          value: '综合系统部',
        },
      ],
    },
    {
      label: '子行业（国内）',
      prop: 'domtc_sub_indu_class_cn_name',
      filterType: 'checkbox',
      belongs: 'EBG国内',
      options: [],
    },
  ],
  EBG海外: [
    {
      label: '行业（海外）',
      prop: 'industry_class_cn_name',
      filterType: 'checkbox',
      options: [
        {
          label: 'ISP与互联网系统部(L1)',
          value: 'ISP与互联网系统部(L1)',
        },
        {
          label: '电力数字化军团',
          value: '电力数字化军团',
        },
        {
          label: '公共事业系统部(L1)',
          value: '公共事业系统部(L1)',
        },
        {
          label: '交通智慧化军团(L1)',
          value: '交通智慧化军团(L1)',
        },
        {
          label: '矿山军团(L1)',
          value: '矿山军团(L1)',
        },
        {
          label: '其他(L1)',
          value: '其他(L1)',
        },
        {
          label: '数字金融军团(L1)',
          value: '数字金融军团(L1)',
        },
        {
          label: '政务一网通军团',
          value: '政务一网通军团',
        },
        
        {
          label: '制造与大企业系统部(L1)',
          value: '制造与大企业系统部(L1)',
        },
        {
          label: '智能光伏(L1)',
          value: '智能光伏(L1)',
        },
      ]
    },
    {
      label: '子行业（海外）',
      prop: 'sub_industry_class_cn_name',
      filterType: 'checkbox',
      belongs: 'EBG海外',
      options: [],
    },
  ],
  国内海外公共字段: [
    {
      label: '最终客户',
      prop: 'end_cust_en_name',
      datasetLabel: '最终客户英文名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '最终客户企业网客户群',
      prop: 'end_ent_cust_class_cn_name',
      filterType: 'checkbox',
      belongs: ['EBG海外', 'EBG国内'],
      options: [],
    },
    {
      label: '合同维最终客户',
      prop: 'end_cust_name',
      datasetLabel: '合同维最终客户名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: 'EBG父客户',
      prop: 'ebg_parent_cust_en_name',
      datasetLabel: 'EBG父客户英文名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '合同维签约客户',
      prop: 'sign_cust_name',
      datasetLabel: '合同维签约客户名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '项目名称',
      prop: 'proj_cn_name',
      datasetLabel: '项目名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '企业业务客户群中文名称',
      prop: 'enterprise_cust_class_cn_name',
      datasetLabel: '企业业务客户群中文名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '签约客户',
      prop: 'sign_cust_en_name',
      datasetLabel: '签约客户英文名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: 'OFFERING名称',
      prop: 'offering_cn_name',
      datasetLabel: 'OFFERING中文名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '公司名称',
      prop: 'company_cn_name',
      datasetLabel: '公司中文名称',
      filterType: 'backEnd',
      options: [],
    },
    {
      label: '科目',
      prop: 'grp_account_code_cn_name',
      datasetLabel: '科目名称',
      filterType: 'backEnd',
      options: [],
    },
  ],
};

export const casecadeMap = {
  财务指标: [
    {
      label: '报表项1级中文名',
      prop: 'report_item_l1_cn_name',
    },
    {
      label: '报表项2级中文名',
      prop: 'report_item_l2_cn_name',
    },
    {
      label: '报表项3级中文名',
      prop: 'report_item_l3_cn_name',
    },
    {
      label: '报表项4级中文名',
      prop: 'report_item_l4_cn_name',
    },
    {
      label: '报表项5级中文名',
      prop: 'report_item_l5_cn_name',
    },
  ],
  主维度: [
    // {
    //   label: '重量级团队LV0中文名称',
    //   prop: 'lv0_prod_rd_team_cn_name',
    // },
    {
      label: '重量级团队LV1中文名称',
      prop: 'lv1_prod_rd_team_cn_name',
    },
    {
      label: '重量级团队LV2中文名称',
      prop: 'lv2_prod_rd_team_cn_name',
    },
    {
      label: '重量级团队LV3中文名称',
      prop: 'lv3_prod_rd_team_cn_name',
    },
    {
      label: '重量级团队LV4中文名称',
      prop: 'lv4_prod_rd_team_cn_name',
    },
    // {
    //   label: '产品LV2自定义名称',
    //   prop: 'lv2_prod_list_cn_name_p',
    // },
    {
      label: '产品LV3自定义名称',
      prop: 'lv3_prod_list_cn_name_p',
    },
    {
      label: '产品LV4自定义名称',
      prop: 'lv4_prod_list_cn_name_p',
    },
    // {
    //   label: '产品名称',
    //   prop: 'prod_cn_name',
    // }
  ],
  辅维度: [
    // {
    //   label: '辅产品BG中文名称',
    //   prop: 'minor_lv0_prod_list_cn_name',
    // },
    {
      label: '辅产品LV1中文名称',
      prop: 'minor_lv1_prod_list_cn_name',
    },
    {
      label: '辅产品LV2中文名称',
      prop: 'minor_lv2_prod_list_cn_name',
    },
    // {
    //   label: '辅产品LV3中文名称',
    //   prop: 'minor_lv3_prod_list_code',
    // },
    // {
    //   label: '辅产品LV4中文名称',
    //   prop: 'minor_lv4_prod_list_code',
    // },
    // {
    //   label: '辅产品OFFERING中文名称',
    //   prop: 'minor_offering_cn_name',
    // },
  ],
  区域: [
    {
      label: '国内海外标识',
      prop: 'oversea_flag',
    },
    {
      label: '地区部中文名称',
      prop: 'region_cn_name',
    },
    {
      label: '代表处中文名称',
      prop: 'repoffice_cn_name',
    },
    {
      label: '办事处中文名称',
      prop: 'office_cn_name',
    },
    {
      label: '国家中文名称',
      prop: 'country_cn_name',
    },
  ],
  CNBG: [
    {
      label: '大T系统部中文名称',
      prop: 'top_cust_category_cn_name',
    },
    {
      label: '区域系统部中文名称',
      prop: 'region_custcatg_cn_name',
    },
  ],
  EBG国内: [
    {
      label: '行业系统部中文名称-中国',
      prop: 'domtc_entps_indu_class_cn_name',
    },
    {
      label: 'EBG国内子行业中文名称',
      prop: 'domtc_sub_indu_class_cn_name',
    },
    {
      label: '最终客户企业网客户群中文名称',
      prop: 'end_ent_cust_class_cn_name',
    },
  ],
  EBG海外: [
    {
      label: '行业系统部中文名称-全球',
      prop: 'industry_class_cn_name',
    },
    {
      label: '子行业中文名称',
      prop: 'sub_industry_class_cn_name',
    },
    {
      label: '最终客户企业网客户群中文名称',
      prop: 'end_ent_cust_class_cn_name',
    },
  ],
};

export const compareMap = {
  lt: '小于',
  lte: '小于等于',
  gt: '大于',
  gte: '大于等于',
};
export const operatorMap = {
  in: '包含',
  nin: '不包含',
}
export function conditionList() {
  return Object.values(conditionMap).flat(1);
}
