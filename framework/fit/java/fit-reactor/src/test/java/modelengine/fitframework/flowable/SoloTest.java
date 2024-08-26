/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.flowable;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.flowable.subscriber.RecordSubscriber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link Solo} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-02-10
 */
@DisplayName("测试 Solo")
public class SoloTest {
    @Nested
    @DisplayName("测试创建 Solo")
    class TestCreate {
        @Test
        @DisplayName("使用 empty 方法创建的响应式流，结果符合预期")
        void shouldReturnCorrectSoloWhenEmpty() {
            Optional<Integer> actual = Solo.<Integer>empty().block();
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("使用 just 方法创建的响应式流，结果符合预期")
        void shouldReturnCorrectSoloWhenJust() {
            Optional<Integer> actual = Solo.just(1).block();
            assertThat(actual).isPresent().hasValue(1);
        }
    }

    @Nested
    @DisplayName("测试转换 Solo")
    class TestTransform {
        @Test
        @DisplayName("使用 map 方法生成的响应式流，结果符合预期")
        void shouldReturnCorrectSoloWhenMap() {
            Optional<String> actual = Solo.just(1).map(String::valueOf).block();
            assertThat(actual).isPresent().hasValue("1");
        }

        @Test
        @DisplayName("使用 toChoir 方法生成的响应式流，结果符合预期")
        void shouldReturnCorrectChoirWhenToChoir() {
            List<Integer> actual = Solo.just(1).toChoir().blockAll();
            assertThat(actual).hasSize(1).contains(1);
        }
    }

    @Nested
    @DisplayName("测试过滤 Solo")
    class TestFilter {
        @Test
        @DisplayName("使用 filter 过滤响应式流并请求每一个元素，结果符合预期")
        void shouldReturnOneNumWithCompleteWhenFilterAndRequestEach() {
            RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1, 1);
            Solo.just(1).filter(value -> value % 2 == 1).subscribe(subscriber);
            assertThat(subscriber.getElements()).hasSize(1).containsSequence(1);
            assertThat(subscriber.receivedCompleted()).isTrue();
            assertThat(subscriber.receivedFailed()).isFalse();
        }

        @Test
        @DisplayName("使用 filter 过滤响应式流并请求一个元素，结果符合预期")
        void shouldReturnOneNumWithoutCompleteWhenFilterAndRequestOne() {
            RecordSubscriber<Integer> subscriber = new RecordSubscriber<>(1);
            Solo.just(1).filter(value -> value % 2 == 1).subscribe(subscriber);
            assertThat(subscriber.getElements()).hasSize(1).containsSequence(1);
            assertThat(subscriber.receivedCompleted()).isTrue();
            assertThat(subscriber.receivedFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("测试通过 flatMap 对于元素进行展平")
    class TestFlatMap {
        @Test
        @DisplayName("使用 flatMap 展平 Solo 响应式流，请求足量元素，结果符合预期")
        void shouldReturnNumAsIntWithCompleteWhenRequestEnough() {
            List<Integer> actual =
                    Solo.just(0).flatMap((Integer value) -> Choir.just(value * 2, value * 2 + 1)).blockAll();
            assertThat(actual).hasSize(2).containsSequence(0, 1);
        }

        @Test
        @DisplayName("使用 flatMap 展平 Solo 响应式流并且元素类型发生变化，请求足量元素，结果符合预期")
        void shouldReturnNumAsStringWithCompleteWhenRequestEnough() {
            List<String> actual = Solo.just(0)
                    .flatMap((Integer value) -> Choir.just(Integer.toString(value * 2),
                            Integer.toString(value * 2 + 1)))
                    .blockAll();
            assertThat(actual).hasSize(2).containsSequence("0", "1");
        }
    }
}
