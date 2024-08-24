import ManageCubeCreateReport from './ManageCubeCreateReport';
import { v4 as uuidv4 } from 'uuid';
import React from 'react';

export const manageCubeCreateReportComponent = (jadeConfig) => {
  const self = {};
  self.getJadeConfig = () => {
    return jadeConfig ? jadeConfig : {
      'inputParams': [{
        id: uuidv4(), name: 'reportResult', type: 'String', from: 'Reference',
        value: ['output'], fallbackOnNodeDataMiss: true
      }], 'outputParams': [{
        id: uuidv4(), name: 'reportResult', type: 'String', from: 'Input', value: ''
      }]
    }
  };
  self.getReactComponents = () => {
    return (<><ManageCubeCreateReportComponent /></>);
  };
  return self;
};
const ManageCubeCreateReportComponent = () => {
  return (<>
    <ManageCubeCreateReport />
  </>);
};
