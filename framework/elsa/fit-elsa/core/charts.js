const chart = (id, x, y, width, height, parent,drawer) => {
    const self = rectangle(id, x, y, width, height, parent, drawer);
    self.grid = undefined;//grid id
    self.range = undefined;//{1,2,3,4}第一行，第二列，3行，4列
    self.cells = [];

    const invalidate = self.invalidate;
    self.invalidate = ()=>{

    };

    // self.serializedFields.batchAdd("cells","grid","range");
    return self;
};

const columnChart = (id, x, y, width, height, parent) => {
    const self = chart(id, x, y, width, height, parent, columnChartDrawer);
    return self;
};

export{columnChart};