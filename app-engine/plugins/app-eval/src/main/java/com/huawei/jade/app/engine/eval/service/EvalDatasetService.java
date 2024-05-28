/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.query.EvalDatasetListQuery;
import com.huawei.jade.app.engine.eval.query.EvalDatasetQuery;
import com.huawei.jade.app.engine.eval.vo.EvalDataVo;
import com.huawei.jade.app.engine.eval.vo.EvalDatasetVo;
import com.huawei.jade.app.engine.eval.vo.Page;

import org.apache.ibatis.session.RowBounds;

import java.io.FileInputStream;
import java.util.List;

/**
 * 评估数据集相关服务。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
public interface EvalDatasetService {
    /**
     * 创建评估数据集。
     *
     * @param evalDatasetPo 表示评估数据集PO的 {@link EvalDatasetPo}
     */
    void createEvalDataset(EvalDatasetPo evalDatasetPo);

    /**
     * 向评估数据集中插入一条评估数据。
     *
     * @param evalDataPo 表示评估数据的 {@link EvalDataPo}
     */
    void insertEvalData(EvalDataPo evalDataPo);

    /**
     * 向评估数据集中插入一批评估数据。
     *
     * @param evalDataPoList 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}
     */
    void insertEvalData(List<EvalDataPo> evalDataPoList);

    /**
     * 获取评估数据列表。
     *
     * @param evalDatasetListQuery 表示查询评估数据集列表条件参数的 {@link EvalDatasetListQuery}
     * @return 表示评估数据列表的 {@link Page}{@code <}{@link EvalDatasetVo}{@code >}
     */
    Page<EvalDatasetVo> getEvalDatasetList(EvalDatasetListQuery evalDatasetListQuery);

    /**
     * 通过id获取评估数据集。
     *
     * @param id 表示评估数据集id的 {@link Long}
     * @return 表示评估数据集的 {@link EvalDatasetPo}
     */
    EvalDatasetPo getEvalDatasetById(long id);

    /**
     * 通过查询条件查找评估数据集。
     *
     * @param query 表示查询条件的 {@link EvalDatasetQuery}
     * @return 表示数据集的 {@link EvalDatasetVo}
     */
    EvalDatasetVo getEvalDataset(EvalDatasetQuery query);

    /**
     * 通过id删除一个评估数据集。
     *
     * @param id 表示评估数据集id {@link Long}
     */
    void deleteEvalDatasetById(long id);

    /**
     * 通过id删除一条评估数据。
     *
     * @param id 表示评估数据id的 {@link Long}
     */
    void deleteEvalDataById(long id);

    /**
     * 通过评估数据集id获取评估数据。
     *
     * @param datasetId 表示数据集id的 {@link Long}
     * @param rowBounds 表示分页信息的 {@link RowBounds}
     * @return 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}
     */
    List<EvalDataPo> getEvalDataByDatasetId(long datasetId, RowBounds rowBounds);

    /**
     * 通过id更新一个数据集。
     *
     * @param evalDatasetPo 表示更新后评估数据集的 {@link EvalDatasetPo}
     */
    void updateEvalDatasetById(EvalDatasetPo evalDatasetPo);

    /**
     * 通过id更新一条评估数据。
     *
     * @param evalDataPo 表示更新后评估数据的 {@link EvalDataPo}
     */
    void updateEvalDataById(EvalDataPo evalDataPo);

    /**
     * excel文件转换评估数据列表。
     *
     * @param inputStream 表示excel文件输入流的 {@link FileInputStream}
     * @return 表示转换后评估数据列表的 {@link List}{@code <}{@link EvalDataVo}{@code >}
     */
    List<EvalDataVo> excelToEvalDataList(FileInputStream inputStream);

    /**
     * 根据id获取评估数据。
     *
     * @param id 表示评估数据id的 {@link Long}
     * @return 表示评估数据的 {@link EvalDataPo}
     */
    EvalDataPo getEvalDataById(long id);
}
