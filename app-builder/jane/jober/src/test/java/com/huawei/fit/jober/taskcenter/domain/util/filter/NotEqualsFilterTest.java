package modelengine.fit.jober.taskcenter.domain.util.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.MockitoAnnotations.openMocks;

import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jober.taskcenter.domain.util.Filter;
import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * 判断不等于指定值的过滤器测试
 *
 * @author 罗书强
 * @since 2024-04-25
 */
public class NotEqualsFilterTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Object value;

    private NotEqualsFilter notEqualsFilter;

    private NotEqualsFilter.Parser parser;

    private AutoCloseable mockitoCloseable;

    @BeforeEach
    public void setUp() throws Exception {
        mockitoCloseable = openMocks(this);
        notEqualsFilter = new NotEqualsFilter(value);
        parser = new NotEqualsFilter.Parser();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockitoCloseable.close();
    }

    @Test
    public void test_indexable_should_return_true_when_test_data_combination() throws Exception {
        // run the test
        boolean result = notEqualsFilter.indexable();
        // verify the results
        assertTrue(result);
    }

    @Test
    public void test_to_condition_should_return_not_null_when_test_data_combination() throws Exception {
        try (MockedStatic<Condition> mockedStaticCondition = mockStatic(Condition.class, RETURNS_DEEP_STUBS)) {
            // setup
            Condition condition = Mockito.mock(Condition.class);
            mockedStaticCondition.when(() -> Condition.expectNotEqual(any(ColumnRef.class), any(Object.class)))
                    .thenReturn(condition);
            ColumnRef column = Mockito.mock(ColumnRef.class);
            // run the test
            Condition result = notEqualsFilter.toCondition(column);
            // verify the results
            assertNotNull(result);
        }
    }

    @Test
    public void test_equals_should_return_false_when_test_data_combination() throws Exception {
        // setup
        Object obj = new Object();
        // run the test
        boolean result = notEqualsFilter.equals(obj);
        // verify the results
        assertFalse(result);
    }

    @Test
    public void test_equals_should_return_true_when_objects_is_notEq() throws Exception {
        NotEqualsFilter notEqualsFilter1 = new NotEqualsFilter(value);
        // run the test
        boolean result = notEqualsFilter.equals(notEqualsFilter1);
        // verify the results
        assertTrue(result);
    }

    @Test
    public void test_to_string_should_equal_result_when_test_data_combination() {
        // run the test
        String result = notEqualsFilter.toString();
        // verify the results
        assertEquals("notEq(value)", result);
    }

    @Test
    public void test_hash_code_should_equal_result_when_test_data_combination() {
        int result = notEqualsFilter.hashCode();
        assertNotNull(result);
    }

    @Test
    public void test_parse_should_return_not_null_when_test_data_combination() {
        try (MockedStatic<Filter> mockedStaticFilter = mockStatic(Filter.class, RETURNS_DEEP_STUBS)) {
            // setup
            Filter filter = Mockito.mock(Filter.class);
            mockedStaticFilter.when(Filter::alwaysFalse).thenReturn(filter);
            // run the test
            Filter result = parser.parse(PropertyDataType.TEXT, "string");
            // verify the results
            assertNotNull(result);
        }
    }
}