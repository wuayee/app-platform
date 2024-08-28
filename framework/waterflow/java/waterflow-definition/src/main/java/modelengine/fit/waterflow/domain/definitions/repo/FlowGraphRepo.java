/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.repo;

import modelengine.fit.waterflow.domain.definitions.FlowGraphDefinition;

import java.util.List;
import java.util.Optional;

/**
 * FlowGraphRepo
 * 流程graph数据Repo
 *
 * @author 孙怡菲
 * @since 1.0
 */
public interface FlowGraphRepo {
    /**
     * 保存definition
     *
     * @param flowGraphDefinition 待保存的definition
     */
    void save(FlowGraphDefinition flowGraphDefinition);

    /**
     * 根据tenantId、flowId、version查找definition
     *
     * @param tenantId 租户id
     * @param flowId 流程id
     * @param version 流程版本
     * @return 查找到的definition
     */
    Optional<FlowGraphDefinition> find(String tenantId, String flowId, String version);

    /**
     * 根据flow的id和verison查找definition
     *
     * @param flowId 流程id
     * @param version 流程版本
     * @return 查找到的definition
     */
    FlowGraphDefinition findFlowByFlowIdAndVersion(String flowId, String version);

    /**
     * 根据flow的id查询definition列表
     *
     * @param flowId 流程id
     * @return 该流程id对应的definition列表
     */
    List<FlowGraphDefinition> findFlowsByFlowId(String flowId);

    /**
     * 删除definition
     *
     * @param flowId 流程id
     * @param version 流程版本
     * @return 删除的数量，没有definition被删除时，返回0
     */
    int delete(String flowId, String version);

    /**
     * 根据创建人、tag批量查询definition列表
     *
     * @param createdBy 创建人
     * @param tag tag
     * @param offset 偏移量，通常在分页时使用
     * @param limit 查询个数，通常在分页时使用
     * @return 查询到的列表
     */
    List<FlowGraphDefinition> findByUserOrTag(String createdBy, String tag, int offset, int limit);

    /**
     * 根据创建人、tag批量查询definition列表
     *
     * @param flowIds 给定flowIds
     * @param createdBy 创建人
     * @param offset 偏移量，通常在分页时使用
     * @param limit 查询个数，通常在分页时使用
     * @return 查询到的列表
     */
    List<FlowGraphDefinition> findByFlowIdsOrUser(List<String> flowIds, String createdBy, int offset, int limit);

    /**
     * 统计definition数量
     *
     * @param flowIds 给定flowIds
     * @param createdBy 创建人
     * @return 满足条件的数量
     */
    int getCount(List<String> flowIds, String createdBy);
}
