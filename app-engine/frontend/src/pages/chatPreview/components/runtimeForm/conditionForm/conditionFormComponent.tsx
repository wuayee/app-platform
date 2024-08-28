import ConditionForm from './conditionForm';
import {v4 as uuidv4} from 'uuid';
import React from 'react';

export const conditionFormComponent = (jadeConfig) => {
    const self = {};
    

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
      return jadeConfig ? jadeConfig : {
        'inputParams': [
          {
            id: uuidv4(), name: 'instanceId', type: 'String', from: 'Reference',
            value: ['instanceId'], fallbackOnNodeDataMiss: true
          },
          {
            id: uuidv4(), name: 'chartsData', type: 'String', from: 'Reference',
            value: ['output', 'chartsData'], fallbackOnNodeDataMiss: true
          },
          {
            id: uuidv4(), name: 'dsl', type: 'String', from: 'Reference', value: ['output', 'dsl'],
            fallbackOnNodeDataMiss: true
          },
          {
            id: uuidv4(), name: 'dimension', type: 'String', from: 'Reference',
            value: ['dimension'], fallbackOnNodeDataMiss: true
          },
          {
            id: uuidv4(), name: 'rewriteQuery', type: 'String', from: 'Reference',
            value: ['query'], fallbackOnNodeDataMiss: true
          }
        ], 
        'outputParams': [
          {
            id: uuidv4(), name: 'conditionForm', type: 'String', from: 'Input', value: ''
          }
        ]
      }
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><ConditionFormComponent/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {

    };

    return self;
};

const ConditionFormComponent = () => {
    return (<>
        <ConditionForm/>
    </>)
};
