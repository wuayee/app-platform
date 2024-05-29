/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.mapstruct.mapper.EvalDataStructMapper;
import com.huawei.jade.app.engine.eval.mapstruct.mapper.EvalDatasetStructMapper;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.query.EvalDatasetListQuery;
import com.huawei.jade.app.engine.eval.query.EvalDatasetQuery;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;
import com.huawei.jade.app.engine.eval.vo.EvalDataVo;
import com.huawei.jade.app.engine.eval.vo.EvalDatasetVo;
import com.huawei.jade.app.engine.eval.vo.Page;

import org.apache.ibatis.session.RowBounds;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评估数据集服务实现。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
public class EvalDatasetServiceImpl implements EvalDatasetService {
    @Fit
    private EvalDatasetMapper evalDatasetMapper;

    @Fit
    private EvalDataMapper evalDataMapper;

    /**
     * 创建评估数据集。
     *
     * @param evalDatasetPo 表示评估数据集PO的 {@link EvalDatasetPo}
     */
    @Override
    public void createEvalDataset(EvalDatasetPo evalDatasetPo) {
        evalDatasetMapper.insert(evalDatasetPo);
    }

    /**
     * 向评估数据集中插入一条评估数据。
     *
     * @param evalDataPo 表示评估数据的 {@link EvalDataPo}
     */
    @Override
    public void insertEvalData(EvalDataPo evalDataPo) {
        evalDataMapper.insert(evalDataPo);
    }

    /**
     * 向评估数据集中插入一批评估数据。
     *
     * @param evalDataPoList 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}
     */
    @Override
    public void insertEvalData(List<EvalDataPo> evalDataPoList) {
        evalDataMapper.insertAll(evalDataPoList);
    }

    /**
     * 获取评估数据列表。
     *
     * @param evalDatasetListQuery 表示查询评估数据集列表条件参数的 {@link EvalDatasetListQuery}
     * @return 表示评估数据列表的 {@link Page}{@code <}{@link EvalDatasetVo}{@code >}
     */
    @Override
    public Page<EvalDatasetVo> getEvalDatasetList(EvalDatasetListQuery evalDatasetListQuery) {
        Page<EvalDatasetVo> page = new Page<>();
        page.setPageIndex(evalDatasetListQuery.getPageIndex());
        page.setPageSize(evalDatasetListQuery.getPageSize());

        page.setTotal(evalDatasetMapper.getCountByConditions(evalDatasetListQuery));

        RowBounds rowBounds =
                new RowBounds(
                        (evalDatasetListQuery.getPageIndex() - 1) * evalDatasetListQuery.getPageSize(),
                        evalDatasetListQuery.getPageSize());
        page.setData(
                evalDatasetMapper.getByConditions(evalDatasetListQuery, rowBounds).stream()
                        .map(EvalDatasetStructMapper.INSTANCE::poToVO)
                        .collect(Collectors.toList()));

        return page;
    }

    /**
     * 通过id获取评估数据集。
     *
     * @param id 表示评估数据集id的 {@link Long}
     * @return 表示评估数据集的 {@link EvalDatasetPo}
     */
    @Override
    public EvalDatasetPo getEvalDatasetById(long id) {
        return evalDatasetMapper.getById(id);
    }

    /**
     * 通过查询条件查找评估数据集。
     *
     * @param query 表示查询条件的 {@link EvalDatasetQuery}
     * @return 表示数据集的 {@link EvalDatasetVo}
     */
    @Override
    public EvalDatasetVo getEvalDataset(EvalDatasetQuery query) {
        EvalDatasetPo evalDatasetPO = evalDatasetMapper.getById(query.getDatasetId());
        RowBounds rowBounds = new RowBounds((query.getPageIndex() - 1) * query.getPageSize(), query.getPageSize());
        EvalDatasetVo evalDatasetVO = EvalDatasetStructMapper.INSTANCE.poToVO(evalDatasetPO);

        Page<EvalDataVo> evalDataPage = new Page<>();
        evalDataPage.setPageIndex(query.getPageIndex());
        evalDataPage.setPageSize(query.getPageSize());
        evalDataPage.setTotal(evalDataMapper.getCountByDatasetId(query.getDatasetId()));
        evalDataPage.setData(
                evalDataMapper.getByDatasetId(query.getDatasetId(), rowBounds).stream()
                        .map(EvalDataStructMapper.INSTANCE::poToVO)
                        .collect(Collectors.toList()));
        evalDatasetVO.setData(evalDataPage);

        return evalDatasetVO;
    }

    /**
     * 通过id删除一个评估数据集。
     *
     * @param id 表示评估数据集id {@link Long}
     */
    @Override
    public void deleteEvalDatasetById(long id) {
        evalDatasetMapper.deleteById(id);
    }

    /**
     * 通过id删除一条评估数据。
     *
     * @param id 表示评估数据id的 {@link Long}
     */
    @Override
    public void deleteEvalDataById(long id) {
        evalDataMapper.deleteById(id);
    }

    /**
     * 通过评估数据集id获取评估数据。
     *
     * @param datasetId 表示数据集id的 {@link Long}
     * @param rowBounds 表示分页信息的 {@link RowBounds}
     * @return 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}
     */
    @Override
    public List<EvalDataPo> getEvalDataByDatasetId(long datasetId, RowBounds rowBounds) {
        return evalDataMapper.getByDatasetId(datasetId, rowBounds);
    }

    /**
     * 通过id更新一个数据集。
     *
     * @param evalDatasetPo 表示更新后评估数据集的 {@link EvalDatasetPo}
     */
    @Override
    public void updateEvalDatasetById(EvalDatasetPo evalDatasetPo) {
        evalDatasetMapper.updateById(evalDatasetPo);
    }

    /**
     * 通过id更新一条评估数据。
     *
     * @param evalDataPo 表示更新后评估数据的 {@link EvalDataPo}
     */
    @Override
    public void updateEvalDataById(EvalDataPo evalDataPo) {
        evalDataMapper.updateById(evalDataPo);
    }

    /**
     * 获取单元格内容。
     *
     * @param cell 表示需要取值的单元格的 {@link Cell}
     * @return 表示单元格的值的 {@link Object}
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case ERROR:
                return cell.getErrorCellValue();
            default:
                return "";
        }
    }

    /**
     * excel文件转换评估数据列表。
     *
     * @param inputStream 表示excel文件输入流的 {@link FileInputStream}
     * @return 表示转换后评估数据列表的 {@link List}{@code <}{@link EvalDataVo}{@code >}
     */
    @Override
    public List<EvalDataVo> excelToEvalDataList(FileInputStream inputStream) {
        Sheet sheet;
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            return Collections.emptyList();
        }

        List<EvalDataVo> evalDataVoList = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row dataRow = sheet.getRow(i);
            if (dataRow == null) {
                continue;
            }

            evalDataVoList.add(
                    new EvalDataVo() {
                        {
                            setInput(getCellValue(dataRow.getCell(0)).toString());
                            setOutput(getCellValue(dataRow.getCell(1)).toString());
                        }
                    });
        }

        return evalDataVoList;
    }

    /**
     * 根据id获取评估数据。
     *
     * @param id 表示评估数据id的 {@link Long}
     * @return 表示评估数据的 {@link EvalDataPo}
     */
    @Override
    public EvalDataPo getEvalDataById(long id) {
        return evalDataMapper.getById(id);
    }
}
