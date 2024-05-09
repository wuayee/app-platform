import {formComponent} from "../form.js";
import {eChart, eChartDrawer} from "../../chart/echart.js";

/**
 *
 *
 * @override
 */
const htmlReportCharts = (id, x, y, width, height, parent) => {
    const self = formComponent(eChart, id, x, y, width, height, parent, eChartDrawer);
    self.type = "htmlReportCharts";
    self.serializedFields.batchAdd("option", "chartType");
    self.chartMap = new Map();
    self.chartMap.set("LINE", ["line"]);
    self.chartMap.set("BAR", ["bar"]);
    self.chartMap.set("MIX_LINE_BAR", ["bar", "line"]);
    self.chartMap.set("PIE", ["pie"]);
    self.dashWidth = 0;
    const defaultHeight = 324;
    const maxBarWidth = 30;
    const isMobile = window.isMobile;

    /**
     * 获取echarts绘制图形的模板数据
     */
    self.getTemplateOption = () => {
        return {
            color: ['#5E7CE0', '#6CBFFF', '#50D4AB', '#A6DD82', '#FAC20A'],
            title: {
                text: 'default title',
                textAlign: 'justify',
                textStyle: {
                    overflow: 'truncate',
                    width: isMobile ? 150 : 700,
                    fontWeight: 400,
                    fontFamily: 'PingFangSC-Regular, sans-serif',
                    fontSize: 14,
                    lineHeight: 20
                }
            },
            grid: {
                top: isMobile ? '20%' : '10%',
                right: '15%',
                left: '5%'
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {type: 'shadow'}
            },
            dataZoom: {
                // 内置的缩放、移动功能，鼠标滑轮缩放、平移，以及鼠标拖拽平移
                type: "slider",
                start: 0,
                end: 30,
                // 用于限制窗口大小的最小值（百分比值），取值范围是 0 ~ 100
                // minSpan: 20,
                // 在类目轴上可以设置为 5 表示 5 个类目。
                // minValueSpan : 4,
            },
            toolbox: {
                show: true,
                itemSize: 15,
                feature: {
                    magicType: {
                        show: true,
                        type: ["bar", "line"],
                        title: {
                            line: '折线图',
                            bar: '柱状图'
                        },
                    },
                    restore: {title: '还原'},
                    saveAsImage: {
                        title: '下载图片'
                    }
                }
            },
            legend: {
                show: true,
                type: 'scroll',
                orient: isMobile ? 'horizontal' : 'vertical',
                right: isMobile ? 0 : 'right',
                top: isMobile ? '13%' : '15%'
            },
            xAxis: {
                type: 'category',
                axisTick: {
                    alignWithLabel: true
                },
                axisLabel: {
                    // x轴文字的配置
                    show: true,
                    // 使x轴文字显示全 注释之后，缩放时，会间隔显示X轴的标签
                    // interval: 0,
                }
            },
            yAxis: [
                {
                    show: false,
                    type: "value"
                }
            ]
        };
    }

    self.addDetection(["option"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.setOption(self.option);
    });

    /**
     * 获取数据.
     *
     * @returns {{}} 数据.
     */
    self.getData = () => {
        const data = {chartType: self.chartType, chartTitle: self.option.title.text, chartData: {}};
        data.chartData.labels = self.option.xAxis.data;
        self.chartMap.get(self.chartType).forEach(type => {
            data.chartData[type] = [];
            let chartDataItem = self.option.series.filter(series => series.type === type).map(series => {
                return {data: series.data, title: series.name}
            });
            data.chartData[type].push(...chartDataItem);
        })
        return data;
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        if (data) {
            self.height = defaultHeight;
            self.chartType = data.chartType;
            self.option = buildOption(data.chartData, data.chartType, data.chartTitle);
        }
    };

    // 组装数据
    const buildOption = (chartData, chartType, chartTitle) => {
        // 需要保证templateOption不会被修改
        let option = self.getTemplateOption();
        option.title.text = chartTitle;
        option.xAxis.data = chartData.labels;
        const series = [];

        chartData.bar && chartData.bar.forEach(barData => {
            const barSeries = {type: 'bar'};
            barSeries.data = barData.data.map(data => parseFloat(data));
            barSeries.name = barData.title;
            // 设置bar最大宽度，解决数据量很少时，bar自适应宽度过大的情况
            barSeries.barMaxWidth = maxBarWidth;
            series.push(barSeries);
        });

        chartData.line && chartData.line.forEach(lineData => {
            const lineSeries = {type: 'line', smooth: true};
            // 指定line根据第二个Y坐标轴绘制
            if (chartType === 'MIX_LINE_BAR') {
                lineSeries.yAxisIndex = 1;
            }
            // 忽略字符最后的单位
            lineSeries.data = lineData.data.map(data => parseFloat(data));
            lineSeries.name = lineData.title;
            series.push(lineSeries);
        });
        if (chartType === 'MIX_LINE_BAR') {
            // 折柱混合场景，不用默认值
            option.yAxis = [{
                type: 'value', position: 'left', show: false
            }, {
                type: 'value', position: 'right', show: false, axisLabel: {
                    formatter: '{value}%'
                }
            }];
        }
        series && (option.series = series);
        return option;
    };

    return self;
};

export {htmlReportCharts};