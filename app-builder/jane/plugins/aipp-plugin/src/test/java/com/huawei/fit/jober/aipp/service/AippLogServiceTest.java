/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.dynamicform.DynamicFormService;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.dummy.OperationContextDummy;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.entity.AippLogData;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.AippTypeEnum;
import com.huawei.fit.jober.aipp.enums.MetaInstStatusEnum;
import com.huawei.fit.jober.aipp.mapper.AippLogMapper;
import com.huawei.fit.jober.aipp.service.impl.AippLogServiceImpl;
import com.huawei.fit.jober.common.RangeResult;
import com.huawei.fit.jober.common.RangedResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class AippLogServiceTest {
    private static final String DUMMY_ID = "someRandomId";
    private static final String DUMMY_AIPP_TYPE = "normal";
    private static final String DUMMY_VERSION = "1.0.0";
    private static final String DUMMY_LOG_MSG = "some random log message";
    private static final String DUMMY_W3ACCOUNT = "z00000001";
    private static final String DUMMY_META_VERSION_NEW = "1.0.1";

    @InjectMocks
    private AippLogServiceImpl aippLogService;
    @Mock
    private AippLogMapper aippLogMapperMock;
    @Mock
    private DynamicFormService dynamicFormServiceMock;
    @Mock
    private MetaInstanceService metaInstanceServiceMock;
    @Mock
    private UploadedFileManageService uploadedFileManageServiceMock;
    @Mock
    private MetaService metaServiceMock;

    private AtomicLong logId;
    private Function<AippInstLogType, AippInstLog> generateAippInstLogFunc;

    static Stream<AippLogCreateDto> invalidAippLogCreateDtoCreatorForTest() {
        return Stream.of(AippLogCreateDto.builder()
                        .aippId(null)
                        .instanceId(DUMMY_ID)
                        .logData(DUMMY_LOG_MSG)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(DUMMY_W3ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(null)
                        .logData(DUMMY_LOG_MSG)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(DUMMY_W3ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(DUMMY_ID)
                        .logData(null)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(DUMMY_W3ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(DUMMY_ID)
                        .logData(DUMMY_LOG_MSG)
                        .logType(null)
                        .createUserAccount(DUMMY_W3ACCOUNT)
                        .build(),
                AippLogCreateDto.builder()
                        .aippId(DUMMY_ID)
                        .instanceId(DUMMY_ID)
                        .logData(DUMMY_LOG_MSG)
                        .logType(AippInstLogType.MSG.name())
                        .createUserAccount(null)
                        .build());
    }

    @BeforeEach
    void setUp() {
        logId = new AtomicLong(1L);
        generateAippInstLogFunc = (AippInstLogType logType) -> {
            AippInstLog log = new AippInstLog();
            log.setLogId(logId.get());
            logId.incrementAndGet();
            log.setLogType(logType.name());
            return log;
        };
    }

    private void mockMta() {
        Meta expectMeta = GenerateInactiveMeta();
        when(this.metaServiceMock.list(any(MetaFilter.class),
                eq(false),
                eq(0L),
                eq(10),
                any(OperationContext.class),
                any(MetaFilter.class))).thenReturn(RangedResultSet.create(Collections.singletonList(expectMeta),
                0L,
                10,
                1L));
    }

    @Test
    void shouldInsertIntoDbWhenCallInsertLog() {
        AippLogCreateDto dummyCreateDto = AippLogCreateDto.builder()
                .aippId(DUMMY_ID)
                .version(DUMMY_VERSION)
                .aippType(AippTypeEnum.NORMAL.name())
                .instanceId(DUMMY_ID)
                .logData(DUMMY_LOG_MSG)
                .logType(AippInstLogType.MSG.name())
                .createUserAccount(DUMMY_W3ACCOUNT)
                .build();
        aippLogService.insertLog(dummyCreateDto);
        verify(aippLogMapperMock, times(1)).insertOne(eq(dummyCreateDto));
    }

    @Test
    void shouldDeleteWhenCallDeleteLogWithLastNotRunning() {
        final String dummyAippInstanceStatus = MetaInstStatusEnum.ARCHIVED.name();
        final String dummyInstId = DUMMY_LOG_MSG;
        this.mockMta();
        RangedResultSet<Instance> metaInstanceResult = new RangedResultSet<>();
        metaInstanceResult.setRange(new RangeResult(0, 1, 1));
        metaInstanceResult.setResults(Collections.singletonList(new Instance(dummyInstId,
                Collections.singletonMap(AippConst.INST_STATUS_KEY, dummyAippInstanceStatus),
                null)));
        when(this.aippLogMapperMock.selectNormalInstanceIdOrderByTimeDesc(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type()),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT))).thenReturn(Collections.singletonList(dummyInstId));
        when(this.metaInstanceServiceMock.getMetaVersionId(any())).thenReturn(DUMMY_ID);
        when(this.metaInstanceServiceMock.list(eq(DUMMY_ID),
                argThat(filter -> filter.getIds().size() == 1 && filter.getIds().get(0).equals(dummyInstId)),
                eq(0L),
                eq(1),
                argThat(OperationContextDummy::operationContextDummyMatcher))).thenReturn(metaInstanceResult);
        aippLogService.deleteAippInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        verify(aippLogMapperMock, times(1)).selectNormalInstanceIdOrderByTimeDesc(any(), any(), any());
        verify(metaInstanceServiceMock, times(1)).list(any(), any(), anyLong(), anyInt(), any());
        verify(uploadedFileManageServiceMock, times(1)).cleanAippFiles(any(), any());
        verify(aippLogMapperMock, times(1)).delete(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(AippTypeEnum.NORMAL.name()).type()),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT),
                isNull());
    }

    Meta GenerateInactiveMeta() {
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime modifyTime = LocalDateTime.now();
        Meta expectMeta = new Meta();
        expectMeta.setName("testName");
        expectMeta.setId(DUMMY_ID);
        expectMeta.setVersion(DUMMY_META_VERSION_NEW);
        expectMeta.setCreator("testUser");
        expectMeta.setCreationTime(createTime);
        expectMeta.setLastModificationTime(modifyTime);
        Map<String, Object> attribute = new HashMap<>();
        attribute.put(AippConst.ATTR_AIPP_TYPE_KEY, AippTypeEnum.getType(DUMMY_AIPP_TYPE).type());
        attribute.put(AippConst.ATTR_APP_ID_KEY, DUMMY_ID);
        expectMeta.setAttributes(attribute);
        return expectMeta;
    }

    @Test
    void shouldNotDeleteWhenCallDeleteLogWithLastRunning() {
        final String dummyAippInstanceStatus = MetaInstStatusEnum.RUNNING.name();
        final String dummyInstId = DUMMY_LOG_MSG;
        this.mockMta();
        RangedResultSet<Instance> metaInstanceResult = new RangedResultSet<>();
        metaInstanceResult.setRange(new RangeResult(0, 1, 1));
        metaInstanceResult.setResults(Collections.singletonList(new Instance(dummyInstId,
                Collections.singletonMap(AippConst.INST_STATUS_KEY, dummyAippInstanceStatus),
                null)));
        when(this.aippLogMapperMock.selectNormalInstanceIdOrderByTimeDesc(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type()),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT))).thenReturn(Collections.singletonList(dummyInstId));
        when(this.metaInstanceServiceMock.getMetaVersionId(any())).thenReturn(DUMMY_ID);
        when(this.metaInstanceServiceMock.list(eq(DUMMY_ID),
                argThat(filter -> filter.getIds().size() == 1 && filter.getIds().get(0).equals(dummyInstId)),
                eq(0L),
                eq(1),
                argThat(OperationContextDummy::operationContextDummyMatcher))).thenReturn(metaInstanceResult);
        aippLogService.deleteAippInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        verify(aippLogMapperMock, times(1)).selectNormalInstanceIdOrderByTimeDesc(any(), any(), any());
        verify(metaInstanceServiceMock, times(1)).list(any(), any(), anyLong(), anyInt(), any());
        verify(aippLogMapperMock, times(1)).delete(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.NORMAL.name()),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT),
                eq(dummyInstId));
        verify(uploadedFileManageServiceMock, never()).cleanAippFiles(any(), any());
    }

    @Test
    void shouldDoNothingWhenCallDeleteLogWithNoInstId() {
        this.mockMta();
        when(aippLogMapperMock.selectNormalInstanceIdOrderByTimeDesc(eq(Collections.singletonList(DUMMY_ID)),
                eq(AippTypeEnum.getType(DUMMY_AIPP_TYPE).type()),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT))).thenReturn(Collections.emptyList());
        aippLogService.deleteAippInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        verify(aippLogMapperMock, times(1)).selectNormalInstanceIdOrderByTimeDesc(any(), any(), any());
        verify(metaInstanceServiceMock, never()).list(any(), any(), anyLong(), anyInt(), any());
        verify(aippLogMapperMock, never()).delete(any(), any(), any(), any());
    }

    @ParameterizedTest
    @MethodSource("invalidAippLogCreateDtoCreatorForTest")
    void shouldThrowWhenCallInsertLogWithInvalidDto(AippLogCreateDto invalidDto) {
        Assertions.assertThrows(AippParamException.class, () -> aippLogService.insertLog(invalidDto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid datetime string", "2000/01/01 00:00:00"})
    void shouldReturnAllWhenCallQueryInstanceLogSinceWithInvalidTimeString(String datetimeString) {
        when(aippLogMapperMock.selectWithCondition(argThat(cond -> cond.getInstanceId().equals(DUMMY_ID)
                && Objects.isNull(cond.getAfterAt())))).thenReturn(Stream.of(AippInstLogType.MSG,
                AippInstLogType.FORM,
                AippInstLogType.ERROR,
                AippInstLogType.MSG,
                AippInstLogType.QUESTION,
                AippInstLogType.HIDDEN_QUESTION,
                AippInstLogType.HIDDEN_MSG,
                AippInstLogType.FILE).map(generateAippInstLogFunc).collect(Collectors.toList()));
        String timeString = datetimeString.isEmpty() ? null : datetimeString;
        List<AippInstLog> result = aippLogService.queryInstanceLogSince(DUMMY_ID, timeString);
        Assertions.assertEquals(5, result.size());
        List<Long> logIdSequence = result.stream().map(AippInstLog::getLogId).collect(Collectors.toList());
        Assertions.assertIterableEquals(logIdSequence.stream().sorted().collect(Collectors.toList()),
                logIdSequence,
                "logId乱序");
        Assertions.assertEquals(AippInstLogType.MSG.name(), result.get(0).getLogType());
        Assertions.assertEquals(AippInstLogType.ERROR.name(), result.get(1).getLogType());
        Assertions.assertEquals(AippInstLogType.MSG.name(), result.get(2).getLogType());
        Assertions.assertEquals(AippInstLogType.QUESTION.name(), result.get(3).getLogType());
        Assertions.assertEquals(AippInstLogType.FILE.name(), result.get(4).getLogType());
    }

    @Test
    void shouldQueryDbWithTimeWhenCallQueryInstanceLogSinceWithTimeString() {
        String afterTimeString = "2020-01-01 00:00:01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        LocalDateTime afterTimestamp = LocalDateTime.parse(afterTimeString, formatter);
        // 截止时间后的记录
        List<AippInstLog> dummyAfterAippInstLog =
                Stream.of(AippInstLogType.MSG, AippInstLogType.FORM, AippInstLogType.ERROR)
                        .map(generateAippInstLogFunc)
                        .peek(logData -> logData.setCreateAt(afterTimestamp))
                        .collect(Collectors.toList());
        when(aippLogMapperMock.selectWithCondition(argThat(cond -> cond.getInstanceId().equals(DUMMY_ID)
                && cond.getAfterAt().equals(afterTimestamp)))).thenReturn(dummyAfterAippInstLog);

        List<AippInstLog> result = aippLogService.queryInstanceLogSince(DUMMY_ID, afterTimeString);
        Assertions.assertEquals(2, result.size());
        List<Long> logIdSequence = result.stream().map(AippInstLog::getLogId).collect(Collectors.toList());
        Assertions.assertIterableEquals(logIdSequence.stream().sorted().collect(Collectors.toList()),
                logIdSequence,
                "logId乱序");
        Assertions.assertEquals(AippInstLogType.MSG.name(), result.get(0).getLogType());
        Assertions.assertEquals(AippInstLogType.ERROR.name(), result.get(1).getLogType());
    }

    @Test
    void shouldSuccessWhenQueryAippRecentInstLog() {
        final String dummyFormId = "form id";
        final String dummyFormVersion = "form version";
        final String dummyFormArgs = "{\"key\": \"form args\"}";
        final String dummyFormData = "{\"key\": \"form data\"}";
        final String dummyAippInstanceStatus = MetaInstStatusEnum.ARCHIVED.name();
        Map<String, Object> dummyFormObject = new HashMap<String, Object>() {{
            put("form_args", dummyFormArgs);
            put("form_data", dummyFormData);
        }};
        AippLogData dummyLogData = new AippLogData(dummyFormId, dummyFormVersion, dummyFormArgs, "");
        String dummyLogDataJson = JsonUtils.toJsonString(dummyLogData);
        RangedResultSet<Instance> metaInstanceResult = new RangedResultSet<>();
        metaInstanceResult.setRange(new RangeResult(0, 1, 1));
        metaInstanceResult.setResults(Collections.singletonList(new Instance(DUMMY_ID,
                Collections.singletonMap(AippConst.INST_STATUS_KEY, dummyAippInstanceStatus),
                null)));

        List<AippInstLog> dummyAippInstLog = Stream.of(AippInstLogType.MSG, AippInstLogType.FORM, AippInstLogType.ERROR)
                .map(generateAippInstLogFunc)
                .peek(logData -> logData.setAippId(DUMMY_ID))
                .peek(logData -> logData.setInstanceId(DUMMY_ID))
                .peek(logData -> logData.setLogData(dummyLogDataJson))
                .collect(Collectors.toList());
        when(aippLogMapperMock.selectRecentByAippId(eq(DUMMY_ID),
                eq(DUMMY_AIPP_TYPE),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT))).thenReturn(dummyAippInstLog);
        when(dynamicFormServiceMock.queryFormDetailByPrimaryKey(eq(dummyFormId),
                eq(dummyFormVersion),
                argThat(OperationContextDummy::operationContextDummyMatcher))).thenReturn(new DynamicFormDetailEntity(
                null,
                dummyFormData));
        when(metaInstanceServiceMock.list(eq(DUMMY_ID),
                argThat(filter -> filter.getIds().size() == 1 && filter.getIds().get(0).equals(DUMMY_ID)),
                eq(0L),
                eq(1),
                argThat(OperationContextDummy::operationContextDummyMatcher))).thenReturn(metaInstanceResult);

        List<AippInstLogDataDto> result =
                aippLogService.queryAippRecentInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        Assertions.assertEquals(1, result.size());
        AippInstLogDataDto logDto = result.get(0);
        Assertions.assertEquals(DUMMY_ID, logDto.getAippId());
        Assertions.assertEquals(DUMMY_ID, logDto.getInstanceId());
        Assertions.assertEquals(dummyAippInstanceStatus, logDto.getStatus());
        Assertions.assertEquals(dummyAippInstLog.size(), logDto.getInstanceLogBodies().size());
        Assertions.assertTrue(logDto.getInstanceLogBodies()
                .stream()
                .filter(l -> l.getLogData().equals(AippInstLogType.FORM.name()))
                .allMatch(l -> l.getLogData().equals(JsonUtils.toJsonString(dummyFormObject))));
        // 检查log是否成功排序
        List<Long> logIdSequence = logDto.getInstanceLogBodies()
                .stream()
                .map(AippInstLogDataDto.AippInstanceLogBody::getLogId)
                .collect(Collectors.toList());
        Assertions.assertIterableEquals(logIdSequence.stream().sorted().collect(Collectors.toList()), logIdSequence);
        verify(aippLogMapperMock, times(1)).selectRecentByAippId(eq(DUMMY_ID),
                eq(DUMMY_AIPP_TYPE),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT));
        verify(dynamicFormServiceMock,  // 查表单接口的次数应该和log记录里FORM数量一致
                times((int) dummyAippInstLog.stream()
                        .filter(logData -> logData.getLogType().equals(AippInstLogType.FORM.name()))
                        .count())).queryFormDetailByPrimaryKey(any(), any(), any());
        verify(metaInstanceServiceMock, times(1)).list(any(), any(), anyLong(), anyInt(), any());
    }

    @Test
    void shouldReturnEmptyListWhenQueryAippRecentInstLogWithDbReturnNull() {
        this.mockMta();
        Assertions.assertTrue(aippLogService.queryAippRecentInstLog(DUMMY_ID,
                DUMMY_AIPP_TYPE,
                OperationContextDummy.getDummy()).isEmpty());
        verify(dynamicFormServiceMock, never()).queryFormDetailByPrimaryKey(any(), any(), any());
        verify(metaInstanceServiceMock, never()).list(any(), any(), anyLong(), anyInt(), any());
    }

    @Test
    void shouldSortedWhenQueryAippRecentInstLog() {
        final long instanceCount = 2L;
        final long logCountPerInstance = 3L;
        final AippInstLogType msgType = AippInstLogType.MSG;

        final String dummyAippInstanceStatus = MetaInstStatusEnum.ARCHIVED.name();
        final LocalDateTime createTimestamp = LocalDateTime.of(2000, 1, 1, 0, 0);
        final List<String> dummyInstIdList =
                LongStream.rangeClosed(1, instanceCount).mapToObj(String::valueOf).collect(Collectors.toList());
        List<AippInstLog> dummyAippInstLog = Stream.generate(() -> msgType)
                .limit(instanceCount * logCountPerInstance)
                .map(generateAippInstLogFunc)
                .peek(l -> l.setAippId(DUMMY_ID))
                .peek(l -> l.setCreateAt(createTimestamp))
                .peek(l -> l.setCreateUserAccount(DUMMY_W3ACCOUNT))
                .peek(l -> l.setInstanceId(dummyInstIdList.get((int) ((l.getLogId() - 1) / logCountPerInstance))))
                .collect(Collectors.toList());
        Collections.shuffle(dummyAippInstLog);  // 打乱
        when(aippLogMapperMock.selectRecentByAippId(eq(DUMMY_ID),
                eq(DUMMY_AIPP_TYPE),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT))).thenReturn(dummyAippInstLog);
        RangedResultSet<Instance> metaInstanceResult =
                new RangedResultSet<>(Collections.singletonList(new Instance(null,
                        Collections.singletonMap(AippConst.INST_STATUS_KEY, dummyAippInstanceStatus),
                        null)), new RangeResult(0, 1, 1));
        when(metaInstanceServiceMock.list(eq(DUMMY_ID),
                argThat(filter -> filter.getIds().size() == 1 && dummyInstIdList.contains(filter.getIds().get(0))),
                eq(0L),
                eq(1),
                argThat(OperationContextDummy::operationContextDummyMatcher))).thenReturn(metaInstanceResult);
        this.mockMta();
        when(this.aippLogMapperMock.selectRecentInstanceIdByAippIds(any(), any(), anyInt(), any())).thenReturn(
                dummyInstIdList);
        List<AippInstLogDataDto> result =
                aippLogService.queryAippRecentInstLog(DUMMY_ID, DUMMY_AIPP_TYPE, OperationContextDummy.getDummy());
        Assertions.assertEquals(instanceCount, result.size());
        Assertions.assertIterableEquals(dummyInstIdList,
                result.stream().map(AippInstLogDataDto::getInstanceId).collect(Collectors.toList()));
        for (AippInstLogDataDto logDto : result) {
            Assertions.assertEquals(DUMMY_ID, logDto.getAippId());
            Assertions.assertEquals(dummyAippInstanceStatus, logDto.getStatus());
            Assertions.assertEquals(logCountPerInstance, logDto.getInstanceLogBodies().size());
            Assertions.assertTrue(logDto.getInstanceLogBodies()
                    .stream()
                    .allMatch(l -> l.getLogType().equals(msgType.name()) && l.getCreateAt().equals(createTimestamp)
                            && l.getCreateUserAccount().equals(DUMMY_W3ACCOUNT)));
            // 检查log内部是否成功排序
            List<Long> logIdSequence = logDto.getInstanceLogBodies()
                    .stream()
                    .map(AippInstLogDataDto.AippInstanceLogBody::getLogId)
                    .collect(Collectors.toList());
            Assertions.assertIterableEquals(logIdSequence.stream().sorted().collect(Collectors.toList()),
                    logIdSequence);
        }
        verify(aippLogMapperMock, times(1)).selectRecentByAippId(eq(DUMMY_ID),
                eq(DUMMY_AIPP_TYPE),
                eq(OperationContextDummy.DUMMY_W3_ACCOUNT));
        verify(dynamicFormServiceMock, never()).queryFormDetailByPrimaryKey(any(), any(), any());
        verify(metaInstanceServiceMock, times(1)).list(any(), any(), anyLong(), anyInt(), any());
    }
}
