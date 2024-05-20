/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.knowledge.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.PutMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.jade.app.engine.knowledge.dto.KGenerateConfigDto;
import com.huawei.jade.app.engine.knowledge.dto.KRepoDto;
import com.huawei.jade.app.engine.knowledge.dto.KStorageDto;
import com.huawei.jade.app.engine.knowledge.dto.KTableDto;
import com.huawei.jade.app.engine.knowledge.service.KGenerateService;
import com.huawei.jade.app.engine.knowledge.service.KRepoService;
import com.huawei.jade.app.engine.knowledge.service.KStorageService;
import com.huawei.jade.app.engine.knowledge.service.KTableService;

import java.util.List;

/**
 * 处理知识库相关的HTTP请求。
 *
 * @since 2024-05-18
 */
@Component
@RequestMapping("/knowledge")
public class KnowledgeBaseController {
    @Fit
    private KTableService kTableService;

    @Fit
    private KRepoService kRepoService;

    @Fit
    private KStorageService kStorageService;

    @Fit
    private KGenerateService kGenerateService;

    /**
     * 获取所有的知识库。
     *
     * @return 返回知识库列表
     */
    @GetMapping("/repos")
    public List<KRepoDto> getAllRepos() {
        return kRepoService.getAllRepos();
    }

    /**
     * 根据ID获取知识库。
     *
     * @param id 表示id的 {@link Long}。
     * @return 返回对应的知识库记录
     */
    @GetMapping("/repos/{id}")
    public KRepoDto getRepoById(@PathVariable("id") Long id) {
        return kRepoService.getById(id);
    }

    /**
     * 创建知识库。
     *
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @PostMapping("/repos")
    public void createRepo(@RequestBody KRepoDto kRepoDto) {
        kRepoService.create(kRepoDto);
    }

    /**
     * 更新知识库
     *
     * @param id 表示知识库ID的 {@link Long}。
     * @param kRepoDto 表示知识库记录的 {@link KRepoDto}。
     */
    @PutMapping("/repos/{id}")
    public void updateRepo(@PathVariable("id") Long id, @RequestBody KRepoDto kRepoDto) {
        kRepoService.update(id, kRepoDto);
    }

    /**
     * 删除知识库。
     *
     * @param id 表示知识库ID的 {@link Long}。
     */
    @DeleteMapping("/repos/{id}")
    public void deleteRepo(@PathVariable("id") Long id) {
        kRepoService.delete(id);
    }

    // tables

    /**
     * 获取某个知识库下的所有知识表。
     *
     * @param repoId 表示知识库ID的 {@link Long}。
     * @return 返回知识表列表。
     */
    @GetMapping("/repos/{repo_id}/tables")
    public List<KTableDto> getKnowledgeTables(@PathVariable("repo_id") Long repoId) {
        return kTableService.getByRepoId(repoId);
    }

    /**
     * 获取知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     * @return 返回知识表。
     */
    @GetMapping("/tables/{id}")
    public KTableDto getTable(@PathVariable("id") Long id) {
        return kTableService.getById(id);
    }

    /**
     * 创建知识表。
     *
     * @param kTableDto 表示知识表的 {@link KTableDto}
     */
    @PostMapping("/repos/{repo_id}/tables")
    public void createTable(@RequestBody KTableDto kTableDto) {
        kTableService.create(kTableDto);
    }

    /**
     * 更新知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     * @param kTableDto 表示知识表的 {@link KTableDto}。
     */
    @PutMapping("/tables/{id}")
    public void updateTable(@PathVariable("id") Long id, @RequestBody KTableDto kTableDto) {
        kTableService.update(id, kTableDto);
    }

    /**
     * 删除知识表。
     *
     * @param id 表示知识表ID的 {@link Long}。
     */
    @DeleteMapping("/tables/{id}")
    public void deleteTable(@PathVariable("id") Long id) {
        kTableService.delete(id);
    }

    // storages

    /**
     * 获取所有存储服务。
     *
     * @return 返回存储服务列表。
     */
    @GetMapping("/storages")
    public List<KStorageDto> getAllStorages() {
        return kStorageService.getAllStorages();
    }

    /**
     * 获取存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     * @return 返回存储服务。
     */
    @GetMapping("/storages/{id}")
    public KStorageDto getStorage(@PathVariable("id") Long id) {
        return kStorageService.getById(id);
    }

    /**
     * 创建存储服务。
     *
     * @param kStorageDto 表示存储服务的 {@link KStorageDto}。
     */
    @PostMapping("/storages")
    public void createStorage(@RequestBody KStorageDto kStorageDto) {
        kStorageService.create(kStorageDto);
    }

    /**
     * 更新存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     * @param kStorageDto 表示存储服务的 {@link KStorageDto}。
     */
    @PutMapping("/storages/{id}")
    public void updateKStorage(@PathVariable("id") Long id, @RequestBody KStorageDto kStorageDto) {
        kStorageService.update(id, kStorageDto);
    }

    /**
     * 删除存储服务。
     *
     * @param id 表示存储服务的 {@link Long}。
     */
    @DeleteMapping("/storages/{id}")
    public void deleteKStorage(@PathVariable("id") Long id) {
        kStorageService.delete(id);
    }

    /**
     * 导入文本类型知识接口
     *
     * @param fileConfigDto 文件导入配置信息
     */
    @PostMapping(path = "/import-knowledge/text")
    public void importKnowledge(@RequestBody KGenerateConfigDto fileConfigDto) {
        kGenerateService.importKnowledge(fileConfigDto);
    }
}
