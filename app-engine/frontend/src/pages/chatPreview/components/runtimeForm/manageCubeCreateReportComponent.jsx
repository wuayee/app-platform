import ManageCubeCreateReport from "./ManageCubeCreateReport.jsx";
import {v4 as uuidv4} from "uuid";
import React from 'react';

export const manageCubeCreateReportComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
      return jadeConfig ? jadeConfig : {
        "inputParams": [{
            id: uuidv4(), name: "reportResult", type: "String", from: "Reference", value: ["output"]
        }], "outputParams": [{
            id: uuidv4(), name: "reportResult", type: "String", from: "Input", value: ""
        }]
      }
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><ManageCubeCreateReportComponent/></>);
    };

    /**
     * 必须.
     */
    self.reducers = (data, action) => {

    };

    return self;
};

const ManageCubeCreateReportComponent = () => {
    return (<>
        <ManageCubeCreateReport/>
    </>)
};
