/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PatchMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.entity.FileEntity;
import com.huawei.fit.http.entity.PartitionedEntity;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.validation.Validated;
import com.huawei.jade.app.engine.eval.dto.EvalDataDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataUpdateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetDto;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集相关操作接口。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@Component
@RequestMapping(path = "/evalDataset", group = "评估数据集相关接口")
public class EvalDatasetController {
    @Fit
    private EvalDatasetService evalDatasetService;

    /**
     * 创建评估数据集接口。
     *
     * @param evalDatasetDto 表示创建数据集的参数的 {@link EvalDatasetDto}
     */
    @PostMapping(description = "创建评估数据集接口")
    public void createEvalDataset(@RequestBody EvalDatasetDto evalDatasetDto) {
        EvalDatasetPo evalDatasetPO = EvalDatasetStructMapper.INSTANCE.dtoToPO(evalDatasetDto);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        evalDatasetPO.setCreateTime(now);
        evalDatasetPO.setModifyTime(now);
        evalDatasetService.createEvalDataset(evalDatasetPO);
        long datasetId = evalDatasetPO.getId();

        List<EvalDataPo> dataList =
                evalDatasetDto.getData().stream()
                        .map(
                                d -> {
                                    EvalDataPo po = EvalDataStructMapper.INSTANCE.dtoToPO(d);
                                    po.setCreateTime(now);
                                    po.setModifyTime(now);
                                    po.setDatasetId(datasetId);
                                    return po;
                                })
                        .collect(Collectors.toList());
        evalDatasetService.insertEvalData(dataList);
    }

    /**
     * 获取评估数据集列表接口
     *
     * @param evalDatasetListQuery 表示查询条件的 {@link EvalDatasetListQuery}
     * @return 表示数据集列表的 {@link Page}{@code <}{@link EvalDatasetVo}{@code >}
     */
    @PostMapping(path = "/list", description = "获取评估数据集列表接口")
    public Page<EvalDatasetVo> getEvalDatasetList(@RequestBody EvalDatasetListQuery evalDatasetListQuery) {
        return evalDatasetService.getEvalDatasetList(evalDatasetListQuery);
    }

    /**
     * 获取评估数据集内容接口。
     *
     * @param query 表示查询条件的 {@link EvalDatasetQuery}
     * @return 表示数据集的 {@link EvalDatasetVo}
     */
    @GetMapping(description = "获取评估数据集内容接口")
    public EvalDatasetVo getEvalDataset(@RequestBean @Validated EvalDatasetQuery query) {
        return evalDatasetService.getEvalDataset(query);
    }

    /**
     * 删除数据集。
     *
     * @param datasetId 表示数据id的 {@link Long}
     */
    @DeleteMapping(path = "/{datasetId}", description = "删除评估数据集接口")
    public void deleteEvalDataset(@PathVariable("datasetId") long datasetId) {
        evalDatasetService.deleteEvalDatasetById(datasetId);
    }

    /**
     * 更新数据集接口。
     *
     * @param evalDatasetUpdateDTO 表示数据更新数据的 {@link EvalDatasetUpdateDto}
     */
    @PatchMapping(description = "编辑评估数据集接口")
    public void updateEvalDataset(@RequestBody EvalDatasetUpdateDto evalDatasetUpdateDTO) {
        EvalDatasetPo evalDatasetPO = EvalDatasetStructMapper.INSTANCE.updateDTOToPO(evalDatasetUpdateDTO);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        evalDatasetPO.setModifyTime(now);
        evalDatasetService.updateEvalDatasetById(evalDatasetPO);
    }

    /**
     * 创建评估数据接口。
     *
     * @param evalDataDTO 表示评估数据的 {@link EvalDataDto}
     */
    @PostMapping(path = "/evalData", description = "创建评估数据接口")
    public void createEvalData(@RequestBody EvalDataDto evalDataDTO) {
        EvalDataPo evalDataPO = EvalDataStructMapper.INSTANCE.dtoToPO(evalDataDTO);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        evalDataPO.setCreateTime(now);
        evalDataPO.setModifyTime(now);
        evalDatasetService.insertEvalData(evalDataPO);
    }

    /**
     * 删除评估数据。
     *
     * @param dataId 表示评估数据的id的 {@link Long}
     */
    @DeleteMapping(path = "/evalData/{dataId}", description = "删除评估数据接口")
    public void deleteEvalData(@PathVariable("dataId") long dataId) {
        evalDatasetService.deleteEvalDataById(dataId);
    }

    /**
     * 更新评估数据。
     *
     * @param evalDataUpdateDTO 表示更新评估数据的 {@link EvalDataUpdateDto}
     */
    @PatchMapping(path = "/evalData", description = "编辑评估数据接口")
    public void updateEvalData(@RequestBody EvalDataUpdateDto evalDataUpdateDTO) {
        EvalDataPo evalDataPO = EvalDataStructMapper.INSTANCE.updateDTOToPO(evalDataUpdateDTO);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        evalDataPO.setModifyTime(now);
        evalDatasetService.updateEvalDataById(evalDataPO);
    }

    /**
     * 上传文件创建数据集。
     *
     * @param partitionedEntity 表示上传的excel文件的 {@link PartitionedEntity}
     * @return 表示解析的评估数据 {@link List}{@code <}{@link EvalDataVo}{@code >}
     * @throws IOException 表示文件解析可能出的异常
     */
    @PostMapping(path = "/upload", description = "上传文件接口")
    public List<EvalDataVo> upload(PartitionedEntity partitionedEntity) throws IOException {
        try (FileEntity fileEntity = partitionedEntity.entities().get(0).asFile();
                FileInputStream inputStream =
                        (fileEntity.getInputStream() instanceof FileInputStream)
                                ? (FileInputStream) fileEntity.getInputStream()
                                : null) {
            return evalDatasetService.excelToEvalDataList(inputStream);
        }
    }
}
