/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.service.impl.EvalDatasetServiceImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 数据集测试代码。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EvalDatasetServiceTest {
    @InjectMocks
    private EvalDatasetServiceImpl evalDatasetServiceImpl;

    @Mock
    private EvalDatasetMapper evalDatasetMapper;

    @Mock
    private EvalDataMapper evalDataMapper;

    private EvalTestUtil util = new EvalTestUtil();

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void prepareDB() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        util.mockEvalDatasetMapper(evalDatasetMapper);
        util.mockEvalDataMapper(evalDataMapper);
    }

    @AfterEach
    public void resetDB() throws Exception {
        autoCloseable.close();
        util.resetMockDB();
    }

    @Test
    @DisplayName("测试创建数据集")
    public void shouldOkWhenCreateDataset() {
        EvalDatasetPo po = util.genTestDatasetPo();
        evalDatasetServiceImpl.createEvalDataset(po);
        EvalDatasetPo evalDatasetById = evalDatasetServiceImpl.getEvalDatasetById(po.getId());
        Assertions.assertTrue(util.equal(evalDatasetById, po));
    }

    @Test
    @DisplayName("测试编辑数据集")
    public void shouldOkWhenUpdateDataset() {
        EvalDatasetPo po = util.genTestDatasetPo();
        evalDatasetServiceImpl.createEvalDataset(po);

        po.setDescription("修改后的描述");
        po.setDatasetName("修改后的名字");
        po.setModifyTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        evalDatasetServiceImpl.updateEvalDatasetById(po);

        EvalDatasetPo evalDatasetById = evalDatasetServiceImpl.getEvalDatasetById(po.getId());

        Assertions.assertTrue(util.equal(evalDatasetById, po));
    }

    @Test
    @DisplayName("测试删除数据集")
    public void shouldOkWhenDeleteDataset() {
        EvalDatasetPo po = util.genTestDatasetPo();
        evalDatasetServiceImpl.createEvalDataset(po);

        evalDatasetServiceImpl.deleteEvalDatasetById(po.getId());

        EvalDatasetPo evalDatasetById = evalDatasetServiceImpl.getEvalDatasetById(po.getId());

        Assertions.assertTrue(evalDatasetById == null);
    }

    @Test
    @DisplayName("测试添加数据")
    public void shouldOkWhenCreateData() {
        EvalDataPo po = util.genTestDataPo(1);
        evalDatasetServiceImpl.insertEvalData(po);
        EvalDataPo evalData = evalDatasetServiceImpl.getEvalDataById(po.getId());

        Assertions.assertTrue(util.equal(po, evalData));

        int num = 10;
        long datasetId = 2L;
        List<EvalDataPo> poList = util.genTestDataPoList(datasetId, num);
        evalDatasetServiceImpl.insertEvalData(poList);
        List<EvalDataPo> evalDataList = evalDatasetServiceImpl.getEvalDataByDatasetId(datasetId, null);

        Assertions.assertEquals(evalDataList.size(), poList.size());
        for (int i = 0; i < evalDataList.size(); i++) {
            Assertions.assertTrue(util.equal(evalDataList.get(i), poList.get(i)));
        }
    }

    @Test
    @DisplayName("测试编辑数据")
    public void shouldOkWhenUpdateData() {
        EvalDataPo po = util.genTestDataPo(1);
        evalDatasetServiceImpl.insertEvalData(po);

        po.setInput("编辑后的输入");
        po.setOutput("编辑后的输出");
        po.setModifyTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        evalDatasetServiceImpl.updateEvalDataById(po);
        EvalDataPo evalData = evalDatasetServiceImpl.getEvalDataById(po.getId());

        Assertions.assertTrue(util.equal(po, evalData));
    }

    @Test
    @DisplayName("测试删除数据")
    public void shouldOkWhenDeleteData() {
        EvalDataPo po = util.genTestDataPo(1);
        evalDatasetServiceImpl.insertEvalData(po);

        evalDatasetServiceImpl.deleteEvalDataById(po.getId());

        EvalDataPo evalDataById = evalDatasetServiceImpl.getEvalDataById(po.getId());

        Assertions.assertTrue(evalDataById == null);
    }
}
