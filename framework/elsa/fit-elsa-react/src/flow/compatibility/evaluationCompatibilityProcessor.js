/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {pageCompatibilityProcessor} from '@/flow/compatibility/compatibilityProcessors.js';
import {DEFAULT_MAX_MEMORY_ROUNDS} from '@/common/Consts.js';

/**
 * @override
 */
export const evaluationPageCompatibilityProcessor = (pageData, graph) => {
  const self = pageCompatibilityProcessor(pageData, graph);

  /**
   * @override
   */
  self.createShapeProcessor = (shapeData) => {
    switch (shapeData.type) {
      case 'jadeEvent':
        return evaluationEventCompatibilityProcessor(shapeData, graph, self);
      case 'conditionNodeCondition':
        return evaluationConditionCompatibilityProcessor(shapeData, graph, self);
      case 'llmNodeState':
        return evaluationLlmCompatibilityProcessor(shapeData, graph, self);
      default:
        return evaluationShapeCompatibilityProcessor(shapeData, graph, self);
    }
  };

  return self;
};

/**
 * @override
 */
export const evaluationShapeCompatibilityProcessor = (shapeData, graph, pageProcessor) => {
  const self = {};
  self.shapeData = shapeData;
  self.graph = graph;
  self.pageProcessor = pageProcessor;

  /**
   * 兼容性处理.
   */
  self.process = () => {
    self.shapeData.deletable = false;
    self.shapeData.runnable = false;
    self.shapeData.enableMask = true;
  };

  return self;
};

/**
 * @override
 */
export const evaluationConditionCompatibilityProcessor = (shapeData, graph, pageProcessor) => {
  const self = evaluationShapeCompatibilityProcessor(shapeData, graph, pageProcessor);

  /**
   * @override
   */
  const process = self.process;
  self.process = () => {
    process.apply(self);
    self.shapeData.flowMeta.conditionParams.branches.forEach(b => {
      b.runnable = false;
      b.disabled = true;
    });
  };

  return self;
};

/**
 * @override
 */
export const evaluationEventCompatibilityProcessor = (shapeData, graph, pageProcessor) => {
  const self = evaluationShapeCompatibilityProcessor(shapeData, graph, pageProcessor);

  /**
   * @override
   */
  const process = self.process;
  self.process = () => {
    process.apply(self);
    self.shapeData.selectable = false;
    delete self.shapeData.enableMask;
  };

  return self;
};

/**
 * 大模型节点兼容性处理器.
 *
 * @override
 */
export const evaluationLlmCompatibilityProcessor = (shapeData, graph, pageProcessor) => {
  const self = evaluationShapeCompatibilityProcessor(shapeData, graph, pageProcessor);

  /**
   * @override
   */
  const process = self.process;
  self.process = () => {
    process.apply(self);
    const maxMemoryRounds = self.shapeData.flowMeta
      .jober
      .converter
      .entity
      .inputParams
      .find(i => i.name === 'maxMemoryRounds');
    if (!maxMemoryRounds) {
      self.shapeData.
        flowMeta.
        jober.
        converter.
        entity.
        inputParams.push(DEFAULT_MAX_MEMORY_ROUNDS);
    }
  };

  return self;
};
