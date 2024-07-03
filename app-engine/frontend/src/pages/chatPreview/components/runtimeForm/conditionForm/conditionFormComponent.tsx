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
            id: uuidv4(), name: 'instanceId', type: 'String', from: 'Reference', value: ['instanceId']
          },
          {
            id: uuidv4(), name: 'correctSql', type: 'String', from: 'Reference', value: ['output']
          },
          {
            id: uuidv4(), name: 'dsl', type: 'String', from: 'Reference', value: ['dsl']
          },
          {
            id: uuidv4(), name: 'dimension', type: 'String', from: 'Reference', value: ['dimension']
          },
          {
            id: uuidv4(), name: 'useMemory', type: 'Boolean', from: 'Reference', value: [false]
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
