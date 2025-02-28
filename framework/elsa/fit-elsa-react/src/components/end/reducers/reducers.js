/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {getDefaultReference} from '@/components/util/ReferenceUtil.js';
import {v4 as uuidv4} from 'uuid';
import {updateInput} from '@/components/util/JadeConfigUtils.js';
import {FLOW_TYPE} from '@/common/Consts.js';
import {VALID_FORM_KEY} from '@/components/end/EndConst.js';

const notifyReferenceChange = (shape, inputParams) => {
  try {
    if (shape.onFinalOutputChange) {
      shape.onFinalOutputChange(inputParams[0]);
    }
  } catch (e) {
    // 不影响，出错也继续执行.
  }
};

/**
 * addInput 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const AddInputReducer = () => {
  const self = {};
  self.type = 'addInput';

  /**
   * 处理方法.
   *
   * @param config 配置数据.
   * @return {*} 处理之后的数据.
   */
  self.reduce = (config) => {
    const newConfig = {...config};
    const newInput = {...newConfig.inputParams[0]};
    const newRefInput = getDefaultReference(uuidv4());
    newRefInput.isRequired = true;
    newInput.value.push(newRefInput);
    newConfig.inputParams = [newInput];
    return newConfig;
  };

  return self;
};

/**
 * deleteInput 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const DeleteInputReducer = (shape) => {
  const self = {};
  self.type = 'deleteInput';

  /**
   * 处理方法.
   *
   * @param config 配置数据.
   * @param action 行为参数.
   * @return {*} 处理之后的数据.
   */
  self.reduce = (config, action) => {
    const newConfig = {...config};
    const newInput = {...newConfig.inputParams[0]};
    newInput.value = newInput.value.filter(v => v.id !== action.id);
    newConfig.inputParams = [newInput];
    notifyReferenceChange(shape, newConfig.inputParams);
    return newConfig;
  };

  return self;
};

/**
 * update 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const UpdateInputReducer = (shape) => {
  const self = {};
  self.type = 'update';

  /**
   * 处理方法.
   *
   * @param config 配置数据.
   * @param action 行为参数.
   * @return {*} 处理之后的数据.
   */
  self.reduce = (config, action) => {
    const newConfig = {...config};
    newConfig.inputParams = updateInput(config.inputParams, action.id, action.changes);

    // 当reference字段发生变化时才触发，修改name等操作不触发.
    if (action.changes.some(c => c.key.startsWith('reference'))) {
      notifyReferenceChange(shape, newConfig.inputParams);
    }
    return newConfig;
  };

  return self;
};

/**
 * changeMode 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const ChangeModeReducer = (shape, component) => {
  const self = {};
  self.type = 'changeMode';
  let prevInputParams = null;

  /**
   * 处理方法.
   *
   * @param config 配置数据.
   * @param action 行为参数.
   * @return {*} 处理之后的数据.
   */
  self.reduce = (config, action) => {
    const newConfig = {...config};
    if (action.value === 'manualCheck') {
      const manualConfig = [{
        id: uuidv4(),
        name: 'endFormId',
        type: 'String',
        from: 'Input',
        value: '',
        isHidden: true,
      }, {
        id: uuidv4(),
        name: 'endFormName',
        type: 'String',
        from: 'Input',
        value: '',
        isHidden: true,
      }, {
        id: uuidv4(),
        name: 'endFormImgUrl',
        type: 'String',
        from: 'Input',
        value: '',
        isHidden: true,
      }];
      prevInputParams = config.inputParams;
      newConfig.inputParams = manualConfig;
    } else {
      if (!prevInputParams) {
        const id = uuidv4();
        prevInputParams = shape.graph.flowType === FLOW_TYPE.WORK_FLOW ?
          component.getDefaultWorkflowInputParams() : component.getDefaultAppInputParams(id);
      }
      newConfig.inputParams = prevInputParams;
    }

    return newConfig;
  };

  return self;
};

/**
 * changeFormByMetaData 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const ChangeFormByMetaDataReducer = () => {
    const self = {};
    self.type = 'changeFormByMetaData';

    /**
     * 处理方法.
     *
     * @param config 配置数据.
     * @param action 行为参数.
     * @return {*} 处理之后的数据.
     */
    self.reduce = (config, action) => {
        const newConfig = {...config};
        newConfig.inputParams = newConfig.inputParams
          .filter(item => VALID_FORM_KEY.has(item.name))
          .map(item => {
            if (item.name === 'endFormId') {
              item.value = action.formId;
            }
            if (item.name === 'endFormName') {
              item.value = action.formName;
            }
            if (item.name === 'endFormImgUrl') {
              item.value = action.formImgUrl;
            }
            return item;
          });
        if (action.entity.inputParams) {
            newConfig.inputParams.push(...action.entity.inputParams);
        }
        return newConfig;
    };

    return self;
};

/**
 * deleteForm 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const DeleteFormReducer = () => {
    const self = {};
    self.type = 'deleteForm';

    /**
     * 处理方法.
     *
     * @param config 配置数据.
     * @return {*} 处理之后的数据.
     */
    self.reduce = (config) => {
      const newConfig = {...config};
      newConfig.inputParams = newConfig.inputParams
        .filter(item => VALID_FORM_KEY.has(item.name))
        .map(item => ({...item, value: ''}));
      return newConfig;
    };

  return self;
};

/**
 * editOutputVariable 事件处理器.
 *
 * @return {{}} 处理器对象.
 * @constructor
 */
export const EditOutputVariableReducer = () => {
  const self = {};
  self.type = 'editOutputVariable';

  /**
   * 处理方法.
   *
   * @param config 配置数据.
   * @param action 行为参数.
   * @return {*} 处理之后的数据.
   */
  self.reduce = (config, action) => {
    const newConfig = {...config};
    const finalOutput = newConfig.inputParams.find(item => item.name === 'finalOutput');
    if (!finalOutput) {
      return newConfig;
    }
    const newFinalOutput = {...finalOutput};
    newConfig.inputParams = [
      ...newConfig.inputParams.filter(item => item.name !== 'finalOutput'),
      newFinalOutput,
    ];
    action.changes.forEach(change => {
      newFinalOutput[change.key] = change.value;
    });
    return newConfig;
  };

  return self;
};