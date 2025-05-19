package modelengine.fit.jober.taskcenter.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.domain.PropertyCategory;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jober.common.enums.JaneCategory;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskTemplate;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import modelengine.fit.jober.taskcenter.eventhandler.converter.impl.MetaConverterImpl;
import modelengine.fit.jober.taskcenter.eventhandler.converter.impl.TaskConverterImpl;
import modelengine.fit.jober.taskcenter.service.TaskService;

import modelengine.fit.jane.Undefinable;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.ConflictException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@link MetaMultiVersionFitable} 对应测试类
 *
 * @author 姚江
 * @since 2024-02-29
 */
@ExtendWith(MockitoExtension.class)
public class MetaMultiVersionFitableTest {
    @Mock
    private TaskService taskService;

    @Mock
    private modelengine.fit.jane.task.domain.TaskProperty.Repo taskPropertyRepo;

    private MetaConverter metaConverter;

    @Mock
    private MetaPropertyConverter metaPropertyConverter;

    @Mock
    private TaskType.Repo taskTypeRepo;

    @Mock
    private TaskTemplate.Repo taskTemplateRepo;

    @Mock
    private TaskConverterImpl taskConverter;

    private MetaMultiVersionFitable fitable;

    @BeforeEach
    void init() {
        metaConverter = new MetaConverterImpl(taskConverter, metaPropertyConverter);
        fitable = new MetaMultiVersionFitable(taskService, taskPropertyRepo, metaConverter, metaPropertyConverter,
                taskTypeRepo, taskTemplateRepo);
    }

    @Nested
    @DisplayName("测试list方法")
    class ListTest {
        @Test
        @DisplayName("成功获取列表")
        public void success() {
            OperationContext context = generateContext();
            MetaFilter filter = createFilter();
            when(taskService.listMeta(eq(filter), eq(false), eq(0L), eq(10), any())).thenReturn(mockListResult());
            when(taskConverter.convert(any())).thenCallRealMethod();
            RangedResultSet<Meta> list = fitable.list(filter, false, 0L, 10, context);
            Assertions.assertEquals(1, list.getRange().getTotal());
            Meta meta = list.getResults().get(0);
            Assertions.assertEquals("1.0.0", meta.getVersion());
            Assertions.assertEquals("fruit", meta.getName());
        }

        private MetaFilter createFilter() {
            MetaFilter filter = new MetaFilter();
            filter.setCategories(new ArrayList<String>() {{
                add("META");
            }});
            filter.setVersions(new ArrayList<String>() {{
                add("1.0.0");
            }});
            filter.setCreators(new ArrayList<String>() {{
                add("姚江 WX1299574");
            }});
            filter.setNames(new ArrayList<String>() {{
                add("fruit");
            }});
            filter.setAttributes(new HashMap<String, List<String>>() {{
                put("publish", new ArrayList<String>() {{
                    add("true");
                }});
            }});

            filter.setMetaIds(new ArrayList<String>() {{
                add("4c9989421364404eb80ddf71271e9cd7");
            }});
            filter.setOrderBys(new ArrayList<String>() {{
                add("desc(created_at)");
            }});
            filter.setVersionIds(new ArrayList<String>() {{
                add("c40459cb27b246dda92e3c858bf10ab4");
            }});
            return filter;
        }
    }

    private modelengine.fitframework.model.RangedResultSet<TaskEntity> mockListResult() {
        TaskEntity entity = new TaskEntity();
        entity.setId("18d31c043ca046f099059b01bfe77d1f");
        entity.setName("fruit|1.0.0");
        entity.setTemplateId("25263c043ca04450990597758fe77d1f");
        entity.setCreationTime(LocalDateTime.of(2024, 2, 20, 15, 11, 13));
        entity.setAttributes(new HashMap<String, Object>() {{
            put("publishStatus", true);
        }});

        entity.setCategory(JaneCategory.META);

        PropertyCategory category = new PropertyCategory("1", "JANE");
        TaskProperty property = TaskProperty.custom().name("name").categories(new ArrayList<PropertyCategory>() {{
            add(category);
        }}).build();
        entity.setProperties(new ArrayList<TaskProperty>() {{
            add(property);
        }});
        return modelengine.fitframework.model.RangedResultSet.create(new ArrayList<TaskEntity>() {{
            add(entity);
        }}, 0, 10, 1);
    }

    private static OperationContext generateContext() {
        return new OperationContext("4c9989421364404eb80ddf71271e9cd7", "String operator",
                "4c9989421364404eb80ddf71271e9cd7", "String w3Account", "String employeeNumber", "String name",
                "127.0.0.1", "String sourcePlatform", "zn");
    }

    @Nested
    @DisplayName("测试create方法")
    class CreateTest {
        @Test
        @DisplayName("创建成功-查询到之前版本")
        @Disabled
        public void test01() {
            when(taskService.listMeta(any(), eq(true), eq(0L), eq(10), any())).thenReturn(mockListResult());
            when(taskConverter.convert(any())).thenCallRealMethod();
            when(taskService.create(any(), any())).thenReturn(createEntity());
            Meta meta = fitable.create(generateDeclaration(), generateContext());
            Assertions.assertEquals("1.0.1", meta.getVersion());
            Assertions.assertEquals("AIPP", meta.getCategory());
            Assertions.assertEquals("fruit", meta.getName());
        }

        @Test
        @DisplayName("创建成功-创建模板")
        @Disabled
        public void test02() {
            when(taskService.listMeta(any(), eq(true), eq(0L), eq(10), any())).thenReturn(
                    modelengine.fitframework.model.RangedResultSet.create(Collections.emptyList(), 0, 10, 0));
            when(taskTemplateRepo.create(any(), any())).thenReturn(
                    TaskTemplate.custom().id("25263c043ca04450990597758fe77d1f").build());
            when(taskService.create(any(), any())).thenReturn(createEntity());
            Meta meta = fitable.create(generateDeclaration(), generateContext());
            Assertions.assertEquals("1.0.1", meta.getVersion());
            Assertions.assertEquals("AIPP", meta.getCategory());
            Assertions.assertEquals("fruit", meta.getName());
            Assertions.assertEquals("25263c043ca04450990597758fe77d1f", meta.getId());
        }

        private MetaDeclarationInfo generateDeclaration() {
            MetaDeclarationInfo declarationInfo = new MetaDeclarationInfo();
            declarationInfo.setVersion(Undefinable.defined("1.0.1"));
            declarationInfo.setBasicMetaTemplateId(Undefinable.defined("25263c043ca0664521597758fe77d1f"));
            declarationInfo.setCategory(Undefinable.defined("AIPP"));
            declarationInfo.setName(Undefinable.defined("fruit"));
            declarationInfo.setProperties(Undefinable.undefined());
            declarationInfo.setAttributes(Undefinable.undefined());
            return declarationInfo;
        }

        private TaskEntity createEntity() {
            TaskEntity entity = new TaskEntity();
            entity.setTemplateId("25263c043ca04450990597758fe77d1f");
            entity.setCreationTime(LocalDateTime.of(2024, 2, 28, 13, 15, 56));
            entity.setCreator("UT");
            entity.setName("fruit|1.0.1");
            entity.setCategory(JaneCategory.AIPP);
            return entity;
        }
    }

    @Nested
    @DisplayName("测试patch方法")
    class PatchTest {
        @Test
        @DisplayName("测试成功")
        public void test01() {
            Assertions.assertDoesNotThrow(
                    () -> fitable.patch("1231231231231231", new MetaDeclarationInfo(), generateContext()));
        }

        @Test
        @DisplayName("测试失败-空declaration")
        public void test02() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> fitable.patch("1231231231231231", null, generateContext()));

            Assertions.assertEquals(10000000, exception.getCode());
        }

        @Test
        @DisplayName("测试成功-修改版本信息")
        public void test03() {
            MetaDeclarationInfo declarationInfo = new MetaDeclarationInfo();
            declarationInfo.setVersion(Undefinable.defined("1.0.1"));
            declarationInfo.setName(Undefinable.defined("fruit"));
            Assertions.assertDoesNotThrow(
                    () -> fitable.patch("1231231231231231", declarationInfo, generateContext()));
        }

        @Test
        @DisplayName("测试失败-仅修改了名称")
        public void test04() {
            MetaDeclarationInfo declarationInfo = new MetaDeclarationInfo();
            declarationInfo.setName(Undefinable.defined("fruit"));
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> fitable.patch("1231231231231231", declarationInfo, generateContext()));

            Assertions.assertEquals(10000000, exception.getCode());
        }

        @Test
        @DisplayName("测试失败-仅修改了版本")
        public void test05() {
            MetaDeclarationInfo declarationInfo = new MetaDeclarationInfo();
            declarationInfo.setVersion(Undefinable.defined("1.0.1"));

            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> fitable.patch("1231231231231231", declarationInfo, generateContext()));

            Assertions.assertEquals(10000000, exception.getCode());
        }
    }

    @Nested
    @DisplayName("测试publish方法")
    class PublishTest {
    }

    @Nested
    @DisplayName("测试delete方法")
    class DeleteTest {
        @Test
        @DisplayName("没有任意异常")
        public void allSuccess() {
            OperationContext operationContext = generateContext();
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setTemplateId("templateId");
            when(taskService.retrieve(eq("versionId"), any())).thenReturn(taskEntity);
            Assertions.assertDoesNotThrow(() -> fitable.delete("versionId", operationContext));
        }

        @Test
        @DisplayName("删除模板失败")
        public void onException() {
            OperationContext operationContext = generateContext();
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setTemplateId("templateId");
            when(taskService.retrieve(eq("versionId"), any())).thenReturn(taskEntity);
            doThrow(new ConflictException(ErrorCodes.TASK_TEMPLATE_USED)).when(taskTemplateRepo)
                    .delete(eq("templateId"), any());
            Assertions.assertDoesNotThrow(() -> fitable.delete("versionId", operationContext));
        }
    }

    @Nested
    @DisplayName("测试retrieve方法")
    class RetrieveTest {
    }
}
