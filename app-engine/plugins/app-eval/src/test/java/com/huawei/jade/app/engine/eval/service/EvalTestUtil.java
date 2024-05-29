/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalReportMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalTaskDatasetMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalTaskMapper;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;
import com.huawei.jade.app.engine.eval.po.EvalReportPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskDatasetPo;
import com.huawei.jade.app.engine.eval.po.EvalTaskPo;
import com.huawei.jade.app.engine.eval.query.EvalTaskListQuery;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评估测试代码工具类。
 *
 * @author 董春寅
 * @since 2024-05-28
 */
@ExtendWith(MockitoExtension.class)
public class EvalTestUtil {
    private List<EvalDatasetPo> mockEvalDatasetDB = new ArrayList<>();
    private long datasetId = 1L;

    private List<EvalDataPo> mockEvalDataDB = new ArrayList<>();
    private long dataId = 1L;

    private List<EvalTaskPo> mockEvalTaskDB = new ArrayList<>();
    private long taskId = 1L;

    private List<EvalReportPo> mockEvalReportDB = new ArrayList<>();
    private long reportId = 1L;

    private List<EvalTaskDatasetPo> mockEvalTaskDatasetDB = new ArrayList<>();

    /**
     * 模拟评估数据集的数据库接口。
     *
     * @param evalDatasetMapper 表示打桩的评估数据集的Mapper的 {@link EvalDatasetMapper}。
     */
    public void mockEvalDatasetMapper(@Mock EvalDatasetMapper evalDatasetMapper) {
        doAnswer(
                        invocation -> {
                            EvalDatasetPo datasetPo = invocation.getArgument(0);
                            datasetPo.setId(datasetId++);
                            mockEvalDatasetDB.add(datasetPo);
                            return 1L;
                        })
                .when(evalDatasetMapper)
                .insert(any(EvalDatasetPo.class));

        when(evalDatasetMapper.getById(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            for (EvalDatasetPo evalDatasetPo : mockEvalDatasetDB) {
                                if (evalDatasetPo.getId() == id) {
                                    return evalDatasetPo;
                                }
                            }
                            return null;
                        });

        doAnswer(
                        invocation -> {
                            EvalDatasetPo datasetPo = invocation.getArgument(0);
                            for (int i = 0; i < mockEvalDatasetDB.size(); i++) {
                                if (mockEvalDatasetDB.get(i).getId() == datasetPo.getId()) {
                                    mockEvalDatasetDB.set(i, datasetPo);
                                    break;
                                }
                            }
                            return null;
                        })
                .when(evalDatasetMapper)
                .updateById(any(EvalDatasetPo.class));

        doAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            mockEvalDatasetDB.removeIf(item -> id == item.getId());
                            mockEvalDataDB.removeIf(item -> id == item.getDatasetId());
                            return null;
                        })
                .when(evalDatasetMapper)
                .deleteById(anyLong());
    }

    /**
     * 模拟评估数据的数据库接口。
     *
     * @param evalDataMapper 表示打桩的评估数据的Mapper的 {@link EvalDataMapper}。
     */
    public void mockEvalDataMapper(@Mock EvalDataMapper evalDataMapper) {
        doAnswer(
                        invocation -> {
                            EvalDataPo evalDataPo = invocation.getArgument(0);
                            evalDataPo.setId(dataId++);
                            mockEvalDataDB.add(evalDataPo);
                            return null;
                        })
                .when(evalDataMapper)
                .insert(any(EvalDataPo.class));

        doAnswer(
                        invocation -> {
                            List<EvalDataPo> evalDataPoList = invocation.getArgument(0);
                            for (EvalDataPo po : evalDataPoList) {
                                po.setId(dataId++);
                                mockEvalDataDB.add(po);
                            }
                            return null;
                        })
                .when(evalDataMapper)
                .insertAll(any(List.class));

        when(evalDataMapper.getById(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            for (EvalDataPo evalDataPo : mockEvalDataDB) {
                                if (evalDataPo.getId() == id) {
                                    return evalDataPo;
                                }
                            }
                            return null;
                        });

        when(evalDataMapper.getByDatasetId(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            List<EvalDataPo> res = new ArrayList<>();
                            for (EvalDataPo evalDataPo : mockEvalDataDB) {
                                if (evalDataPo.getDatasetId() == id) {
                                    res.add(evalDataPo);
                                }
                            }
                            return res;
                        });

        when(evalDataMapper.getByDatasetId(anyLong(), any()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            List<EvalDataPo> res = new ArrayList<>();
                            for (EvalDataPo evalDataPo : mockEvalDataDB) {
                                if (evalDataPo.getDatasetId() == id) {
                                    res.add(evalDataPo);
                                }
                            }
                            return res;
                        });

        doAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            mockEvalDataDB.removeIf(item -> item.getId() == id);
                            return null;
                        })
                .when(evalDataMapper)
                .deleteById(anyLong());
    }

    /**
     * 模拟评估任务的数据库接口。
     *
     * @param evalTaskMapper 表示打桩的评估任务的Mapper的 {@link EvalTaskMapper}。
     */
    public void mockEvalTaskMapper(@Mock EvalTaskMapper evalTaskMapper) {
        doAnswer(
                        invocation -> {
                            EvalTaskPo po = invocation.getArgument(0);
                            po.setId(taskId++);
                            mockEvalTaskDB.add(po);
                            return null;
                        })
                .when(evalTaskMapper)
                .insert(any(EvalTaskPo.class));

        when(evalTaskMapper.getById(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            for (EvalTaskPo po : mockEvalTaskDB) {
                                if (po.getId() == id) {
                                    return po;
                                }
                            }
                            return null;
                        });

        when(evalTaskMapper.getByConditions(any(EvalTaskListQuery.class), any()))
                .thenAnswer(
                        invocation -> {
                            EvalTaskListQuery query = invocation.getArgument(0);
                            return mockEvalTaskDB.stream()
                                    .filter(
                                            t -> {
                                                boolean isOk = query.getAppId().equals(t.getAppId());
                                                if (query.getAuthor() != null) {
                                                    isOk = isOk && (query.getAuthor().equals(t.getAuthor()));
                                                }
                                                if (query.getVersion() != null) {
                                                    isOk = isOk && (query.getVersion().equals(t.getVersion()));
                                                }
                                                if (query.getCreateTimeTo() != null) {
                                                    isOk = isOk && (query.getCreateTimeTo().isAfter(t.getCreateTime()));
                                                }
                                                if (query.getCreateTimeFrom() != null) {
                                                    isOk =
                                                            isOk
                                                                    && (query.getCreateTimeFrom()
                                                                            .isBefore(t.getCreateTime()));
                                                }

                                                if (query.getStatusList() != null) {
                                                    isOk = isOk && (query.getStatusList().contains(t.getStatus()));
                                                }
                                                return isOk;
                                            })
                                    .collect(Collectors.toList());
                        });
    }

    /**
     * 模拟评估任务报告的数据库接口。
     *
     * @param evalReportMapper 表示打桩的评估任务报告的Mapper的 {@link EvalReportMapper}。
     */
    public void mockEvalReportMapper(@Mock EvalReportMapper evalReportMapper) {
        doAnswer(
                        invocation -> {
                            EvalReportPo po = invocation.getArgument(0);
                            po.setId(reportId++);
                            mockEvalReportDB.add(po);
                            return null;
                        })
                .when(evalReportMapper)
                .insert(any(EvalReportPo.class));

        when(evalReportMapper.getById(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            for (EvalReportPo po : mockEvalReportDB) {
                                if (po.getId() == id) {
                                    return po;
                                }
                            }
                            return null;
                        });

        when(evalReportMapper.getByEvalTaskId(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            return mockEvalReportDB.stream()
                                    .filter(r -> r.getEvalTaskId() == id)
                                    .collect(Collectors.toList());
                        });
        doAnswer(
                        invocation -> {
                            EvalReportPo po = invocation.getArgument(0);
                            for (int i = 0; i < mockEvalReportDB.size(); i++) {
                                if (po.getId() == mockEvalReportDB.get(i).getId()) {
                                    mockEvalReportDB.set(i, po);
                                }
                            }
                            return null;
                        })
                .when(evalReportMapper)
                .updateById(any(EvalReportPo.class));
    }

    /**
     * 模拟任务数据集关联表的数据库接口。
     *
     * @param evalTaskDatasetMapper 表示打桩的任务数据集关联表的Mapper的 {@link EvalTaskDatasetMapper}。
     */
    public void mockEvalTaskDatasetMapper(@Mock EvalTaskDatasetMapper evalTaskDatasetMapper) {
        doAnswer(
                        invocation -> {
                            EvalTaskDatasetPo po = invocation.getArgument(0);
                            mockEvalTaskDatasetDB.add(po);
                            return null;
                        })
                .when(evalTaskDatasetMapper)
                .insert(any(EvalTaskDatasetPo.class));

        doAnswer(
                        invocation -> {
                            List<EvalTaskDatasetPo> list = invocation.getArgument(0);
                            mockEvalTaskDatasetDB.addAll(list);
                            return (long) list.size();
                        })
                .when(evalTaskDatasetMapper)
                .insertAll(any(List.class));

        when(evalTaskDatasetMapper.getByEvalTaskId(anyLong()))
                .thenAnswer(
                        invocation -> {
                            long id = invocation.getArgument(0);
                            return mockEvalTaskDatasetDB.stream()
                                    .filter(e -> e.getEvalTaskId() == id)
                                    .collect(Collectors.toList());
                        });
    }

    /**
     * 重置数据库。
     */
    public void resetMockDB() {
        mockEvalDatasetDB = Collections.emptyList();
        mockEvalDataDB = Collections.emptyList();
        mockEvalTaskDB = Collections.emptyList();
        mockEvalReportDB = Collections.emptyList();
        mockEvalTaskDatasetDB = Collections.emptyList();
        dataId = 1;
        datasetId = 1;
        taskId = 1;
        reportId = 1;
    }

    /**
     * 判断两个对象是否相同。
     *
     * @param po1 表示对象1的 {@link Object}。
     * @param po2 表示对象2的 {@link Object}。
     * @return 表示是否相同的 {@link Boolean}。
     */
    public boolean equal(Object po1, Object po2) {
        return ObjectUtils.toString(po1).equals(ObjectUtils.toString(po2));
    }

    /**
     * 生成测试用的评估数据集。
     *
     * @return 表示评估数据集的 {@link EvalDatasetPo}。
     */
    public EvalDatasetPo genTestDatasetPo() {
        EvalDatasetPo po = new EvalDatasetPo();
        po.setDescription("test");
        po.setAuthor("d000000");
        po.setDatasetName("test_dataset_1");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        po.setCreateTime(now);
        po.setModifyTime(now);
        po.setAppId("app1");
        return po;
    }

    /**
     * 生成一条评估数据。
     *
     * @param datasetId 表示评估数据集id的 {@link Long}。
     * @return 表示评估数据的实体类 {@link EvalDataPo}
     */
    public EvalDataPo genTestDataPo(long datasetId) {
        EvalDataPo po = new EvalDataPo();
        po.setDatasetId(datasetId);
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        po.setCreateTime(now);
        po.setModifyTime(now);
        po.setInput("测试输入");
        po.setOutput("测试输出");
        return po;
    }

    /**
     * 生成测试用的评估数据列表。
     *
     * @param datasetId 表示评估数据集id的 {@link Long}
     * @param num 表示生成评估数据的数量 {@link Integer}
     * @return 表示评估数据列表的 {@link List}{@code <}{@link EvalDataPo}{@code >}
     */
    public List<EvalDataPo> genTestDataPoList(long datasetId, int num) {
        List<EvalDataPo> poList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            EvalDataPo po = new EvalDataPo();
            po.setDatasetId(datasetId);
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            po.setCreateTime(now);
            po.setModifyTime(now);
            po.setInput("测试输入" + i);
            po.setOutput("测试输出" + i);
            poList.add(po);
        }
        return poList;
    }
}
