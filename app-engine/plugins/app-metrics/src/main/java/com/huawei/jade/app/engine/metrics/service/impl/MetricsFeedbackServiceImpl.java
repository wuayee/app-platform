/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.metrics.service.impl;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import com.huawei.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import com.huawei.jade.app.engine.metrics.service.MetricsFeedbackService;
import com.huawei.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import com.huawei.jade.app.engine.metrics.vo.Page;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * MetricsFeedackServiceImpl类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024-05-21
 */
@Component
public class MetricsFeedbackServiceImpl implements MetricsFeedbackService {
    @Fit
    private ConversationRecordMapper conversationRecordMapper;

    /**
     * 获取以页为单位的feedback数据
     *
     * @param metricsFeedbackDTO 筛选条件
     * @return 以页为单位的feedback数据
     */
    @Override
    public Page<MetricsFeedbackVo> getMetricsFeedback(MetricsFeedbackDto metricsFeedbackDTO) {
        List<MetricsFeedbackVo> conversations = conversationRecordMapper.getByCondition(metricsFeedbackDTO);

        Page<MetricsFeedbackVo> page = new Page<>();
        page.setPageIndex(metricsFeedbackDTO.getPageIndex());
        page.setPageSize(metricsFeedbackDTO.getPageSize());
        page.setTotal(conversationRecordMapper.getCountByCondition(metricsFeedbackDTO));
        page.setData(conversations);
        return page;
    }

    /**
     * 导出所有符合条件的数据
     *
     * @param metricsFeedbackDTO 筛选条件
     * @return ByteArrayInputStream
     * @throws IOException 异常捕获
     */
    @Override
    public ByteArrayInputStream export(MetricsFeedbackDto metricsFeedbackDTO) throws IOException {
        String[] headStr = {"用户提问", "应用回答", "时间", "响应速度", "用户", "反馈", "反馈详情"};

        try (Workbook wb = new HSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet();
            int rowIndex = 0;

            // 创建并填充表头
            Row headRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < headStr.length; i++) {
                Cell cell = headRow.createCell(i, CellType.STRING);
                cell.setCellValue(headStr[i]);
            }

            long count = conversationRecordMapper.getCountByCondition(metricsFeedbackDTO);
            // 每次取500条数据处理
            int batch = 500;
            metricsFeedbackDTO.setPageSize(batch);
            for (int i = 0; i <= count / batch; i++) {
                metricsFeedbackDTO.setPageIndex(i + 1);
                Page<MetricsFeedbackVo> feedbackPage = getMetricsFeedback(metricsFeedbackDTO);
                for (MetricsFeedbackVo vo : feedbackPage.getData()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0, CellType.STRING).setCellValue(vo.getQuestion());
                    row.createCell(1, CellType.STRING).setCellValue(vo.getAnswer());
                    row.createCell(2, CellType.STRING).setCellValue(vo.getCreateTime());
                    row.createCell(3, CellType.STRING).setCellValue(vo.getResponseTime());
                    row.createCell(4, CellType.STRING).setCellValue(vo.getCreateUser());
                    row.createCell(5, CellType.STRING)
                            .setCellValue(vo.getUserFeedback() == null ? -1 : vo.getUserFeedback());
                    row.createCell(6, CellType.STRING)
                            .setCellValue(vo.getUserFeedbackText() == null ? "" : vo.getUserFeedbackText());
                }
            }

            wb.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
