import { casecadeMap, conditionMap, compareMap } from '../common/condition';
import { uniqBy } from 'lodash';
import { getOptionNodes } from '@shared/http/aipp';

let jsonArray = [];
let hierarchy = [];
import('./data.json').then((res) => {
  jsonArray = Object.values(res).flat();
  hierarchy = buildValueHierarchy(jsonArray);
});

export function getOptionsByCasecade(field, form) {
  let cascade = casecadeMap[field.belongs]
  if (Array.isArray(field.belongs)) {
    const flag = form.oversea_flag || form.condition?.oversea_flag || form.condition1?.oversea_flag || form.condition2?.oversea_flag
    if (flag === '国内') {
      [,field.belongs] = field.belongs
      cascade = casecadeMap[field.belongs]
    } else if (flag === '海外') {
      [field.belongs]= field.belongs
      cascade = casecadeMap[field.belongs]
    } else {
      cascade = []
      field.belongs.forEach(belong => {
        cascade.push(casecadeMap[belong])
      });
      cascade = Array.from(new Set(cascade))
    }
  }
  if (getDepth(cascade) > 1) {
    return cascade.map((item, index) => {
      const tempField = JSON.parse(JSON.stringify(field))
      tempField.belongs = field.belongs.at(index)
      return getOptions(field, item, form)
    })
  }
  return getOptions(field, cascade, form)
}
function getOptions(field, cascade, form) {
  const curIndex = cascade.findIndex((cas) => cas.prop === field.prop);
  const upperLevel = cascade.slice(0, curIndex);
  let values = [];
  let fields = [];
  upperLevel.forEach((cas) => {
    let value =
      form[cas.prop] ||
      (form.conditions && form.conditions[cas.prop]) ||
      (form.condition1 && form.condition1[cas.prop]) ||
      (form.condition2 && form.condition2[cas.prop]);
    fields.push(cas.label);
    if (value) {
      values.push(value);
    } else {
      values.push('');
    }
  });
  // 查询包含关系
  // const fields = ['重量级团队LV0中文名称', '重量级团队LV1中文名称'];
  // const values = ['IRB', '数据存储'];
  if (values[0] === '') {
    if (Array.isArray(field.belongs)) {
      field.belongs.forEach(belong => {
        values[0] = conditionMap[belong][0].options.map((a) => a.value);
      });
    } else {
      values[0] = conditionMap[field.belongs][0].options.map((a) => a.value);
    }
  }
  const field3Values = findHierarchy(fields, values, hierarchy);
  return field3Values
}
function findHierarchy(fields, values, hierarchy) {
  let currentLevel = hierarchy;
  // 迭代每个字段和对应的值
  for (let i = 0; i < fields.length; i += 1) {
    const field = fields[i];
    const value = values[i];
    if (value.length === 0) {
      let obj = {};
      Object.values(currentLevel).forEach((item) => {
        obj = merge(obj, item);
      });
      currentLevel = obj;
    } else if (Array.isArray(value)) {
      // 多选
      let obj = {};
      // eslint-disable-next-line no-loop-func
      value.forEach((val) => {
        obj = merge(obj, currentLevel[val]);
      });
      currentLevel = obj;
    } else {
      // 如果当前层级不存在该值，则返回空数组
      if (!currentLevel.hasOwnProperty.call[value]) {
        return [];
      }
      // 进入下一层级
      currentLevel = currentLevel[value];
    }
  }
  return Object.keys(currentLevel);
}
function buildValueHierarchy(jsonArray) {
  const result = {};
  // 遍历 JSON 数组
  for (let i = 0; i < jsonArray.length; i += 1) {
    const jsonObject = jsonArray[i];
    let currentLevel = result;
    // 遍历每个字段
    Object.keys(jsonObject).forEach((field) => {
      const value = jsonObject[field];
      // 如果当前层级不存在该值，则添加
      if (!currentLevel[value]) {
        currentLevel[value] = {};
      }

      // 进入下一层级
      currentLevel = currentLevel[value];
    });
  }
  return result;
}
function max(arr) {
  return arr.reduce( (accu, curr) => {
    if (curr > accu) return curr
    return accu
  })
}
function getDepth(arr) {
  const eleDepths = []
  arr.forEach( ele => {
    let depth = 0
    if (Array.isArray(ele)) {
      depth = getDepth(ele)
    }
    eleDepths.push(depth)
  })
  return 1 + max(eleDepths)
}
function merge(a, b) {
  const result = {};
  Object.keys(a).forEach((key) => {
    if (b[key]) {
      if (Array.isArray(a[key])) {
        result[key] = [...new Set([...a[key], ...b[key]])];
      } else if (typeof a[key] === 'object') {
        result[key] = merge(a[key], b[key]);
      } else {
        result[key] = b[key];
      }
    } else {
      result[key] = a[key];
    }
  });
  Object.keys(b).forEach((key) => {
    if (!a[key]) {
      result[key] = b[key];
    }
  });

  return result;
};
// 数字转换为日期字符串
export const numToDate = (num) => {
  if (num) {
    try {
      let dateArr = num.toString().split('');
      dateArr.splice(4, 0, '-')
      return dateArr.join('');
    } catch {
      return ''
    }
  }
  return ''
}
// 获取选项label
export const formatterLabel = (data) => {
  if (data.filterType === 'time') {
    return data.value.join('-');
  }
  if (Array.isArray(data.value)) {
    return data.value.join('，');
  }
  if (data.filterType === 'compare') {
    return `${compareMap[Object.keys(data.value)[0]]}${
      Object.values(data.value)[0]
    }`;
  }
  const option = data.options.find((item) => data.value === item.value);
  return option.label;
};
// 根据级联获取字段已选择的选项值
const getSelectedCascade = (casecadeMap: any, formData: string, currentItem: any) => {
  const curIndex = casecadeMap[currentItem.belongs]?.findIndex((cas: any) => {
    return cas.prop === currentItem.prop;
  });
  const parentsField = casecadeMap[currentItem.belongs]?.slice(0, curIndex);
  const arrOptions: any = [];
  const formDataVal = JSON.parse(formData);
  parentsField?.forEach((item: any) => {
    const val = formDataVal[currentItem.category][item.prop];
    if (val) {
      arrOptions.push({
        label: item.label,
        isIn: Object.keys(val)[0] === 'in',
        names: Object.values(val).flat(1),
      });
    }
  });
  return arrOptions;
};
// 获取下拉
export const getOptionsLabel = async (
  val: string,
  allFields: any[],
  casecadeMap: any,
  category: any,
  formData: string,
  belongsMap: any,
  value: never[]
) => {
  const aimItem = allFields.find((item) => item.label === val);
  const aimItemProp = casecadeMap[aimItem?.belongs]?.find((val: any) => val.prop === aimItem.prop);
  const aimCas = aimItem?.belongs && JSON.parse(JSON.stringify(aimItemProp));
  let currentItem = JSON.parse(JSON.stringify(aimItem));
  currentItem.category = category;
  currentItem.operator = 'in';
  currentItem.value = value;
  currentItem.belongs = aimItem?.belongs;
  currentItem.belongsTo = aimItem?.belongs;
  currentItem.options = aimItem?.options;
  currentItem.fullLabel = aimCas?.label;
  currentItem.prop = aimItem?.prop;
  if (currentItem?.belongs) {
    let type = currentItem?.belongsTo === '财务指标' ? belongsMap['DSPL'] : currentItem.belongsTo;
    const data = {
      queryLabel: currentItem.fullLabel,
      type: type,
      conditions: getSelectedCascade(casecadeMap, formData, currentItem),
    };
    const res = await getOptionNodes(data);
    if (res.code == 0) {
      currentItem.options = res?.data?.map((val: any) => {
        return {
          label: val?.name,
          value: val?.name,
        };
      });
      currentItem.options = uniqBy(currentItem.options, 'label');
    }
  }
  return currentItem;
};
