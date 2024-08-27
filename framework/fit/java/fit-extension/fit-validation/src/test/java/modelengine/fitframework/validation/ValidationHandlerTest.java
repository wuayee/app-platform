/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.annotation.AnnotationMetadataResolver;
import modelengine.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.validation.data.Person;
import modelengine.fitframework.validation.data.PersonValidate;
import modelengine.fitframework.validation.data.Product;
import modelengine.fitframework.validation.data.ProductValidate;
import modelengine.fitframework.validation.data.StudentValidate;
import modelengine.fitframework.validation.exception.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link ValidationHandler} 的单元测试。
 *
 * @author 邬涨财
 * @since 2023-05-23
 */
public class ValidationHandlerTest {
    private final BeanContainer beanContainer = mock(BeanContainer.class);
    private final FitRuntime fitRuntime = mock(FitRuntime.class);
    private final AnnotationMetadataResolver annotationMetadataResolver = new DefaultAnnotationMetadataResolver();
    private final Validated validated = Mockito.mock(Validated.class);
    private final ValidationHandler handler = new ValidationHandler(beanContainer);

    @Nested
    @DisplayName("使用默认的分组进行校验")
    class UseDefaultGroupTest {
        @BeforeEach
        void setUp() {
            when(validated.value()).thenReturn(new Class[0]);
            when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
            when(beanContainer.runtime()).thenReturn(fitRuntime);
        }

        /**
         * 调用 {@link PersonValidate#validate1(Person)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，该数据类的 Constraint 字段会被校验，其余字段不会被校验")
        public void givenFieldsWithConstraintAnnotationThenValidateHappened() {
            // when
            Method validateMethod = ReflectionUtils.getDeclaredMethod(PersonValidate.class, "validate1", Person.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Person person = new Person(10, 10, "", "", -1, -1);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {person});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo(
                    "validate1.mouth: 嘴巴数量范围只能在0和1！, validate1.eyes: 眼睛数量范围只能在0和2！, "
                            + "validate1.name: 姓名不能为空！, validate1.sex: 性别不能为空！");
        }

        /**
         * 调用 {@link ProductValidate#validate1(Product)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，该数据类的 Constraint 字段会被校验，其余字段不会被校验")
        void givenFieldsWithConstraintAnnotationThenValidateHappened2() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(ProductValidate.class, "validate1", Product.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Product product = new Product("computer", -1.0, 100, " ");
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {product});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo(
                    "validate1.price: 产品价格必须为正, validate1.category: 产品类别不能为空");
        }

        /**
         * 调用 {@link ProductValidate#validate1(Product)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，该数据类的 Constraint 字段会被校验，其余字段不会被校验")
        void givenFieldsWithConstraintAnnotationThenValidateHappened3() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(ProductValidate.class, "validate1", Product.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Product product = new Product(null, 12999.0, null, "electronic devices");
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {product});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("validate1.name: 产品名不能为空, validate1.quantity: "
                    + "产品数量必须为正");
        }

        /**
         * 调用 {@link PersonValidate#validate2(int, int)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验方法参数，该方法的 Constraint 参数会被校验，其余参数不会被校验")
        public void givenParametersWithConstraintAnnotationThenValidateHappened() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(PersonValidate.class, "validate2", int.class, int.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {-1, -1});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("validate2.ears: 耳朵数量范围只能在0和1！");
        }
    }

    @Nested
    @DisplayName("使用自定义的分组进行校验")
    class UseCustomGroupTest {
        @BeforeEach
        void setUp() {
            when(validated.value()).thenReturn(new Class[0]);
            when(fitRuntime.resolverOfAnnotations()).thenReturn(annotationMetadataResolver);
            when(beanContainer.runtime()).thenReturn(fitRuntime);
        }

        /**
         * 调用 {@link PersonValidate#validate3(Person)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验数据类，只会校验与数据类的分组一致的字段分组。")
        public void givenFieldsThenSameGroupValidateHappened() {
            // when
            Method validateMethod = ReflectionUtils.getDeclaredMethod(PersonValidate.class, "validate3", Person.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            Person person = new Person(10, 10, "", "", -1, -1);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {person});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("validate3.personAge: 人类年龄要在0~150之内");
        }

        /**
         * 调用 {@link StudentValidate#validateStudent(int)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验方法参数，只会校验与数据类的分组一致的方法参数。")
        public void givenParametersThenSameGroupValidateHappened() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(StudentValidate.class, "validateStudent", int.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {-1});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            ConstraintViolationException expectedException =
                    ObjectUtils.cast(invocationTargetException.getTargetException());
            assertThat(expectedException.getMessage()).isEqualTo("validateStudent.age: 范围要在7~20之内");
        }

        /**
         * 调用 {@link StudentValidate#validateTeacher(int)} 作为需要校验的方法。
         */
        @Test
        @DisplayName("校验方法参数，与数据类的分组不一致的方法参数不会校验。")
        public void givenParametersThenDifferentGroupValidateNotHappened() {
            // when
            Method validateMethod =
                    ReflectionUtils.getDeclaredMethod(StudentValidate.class, "validateTeacher", int.class);
            Method handleValidatedMethod = ReflectionUtils.getDeclaredMethod(ValidationHandler.class,
                    "handle",
                    JoinPoint.class,
                    Validated.class);
            handleValidatedMethod.setAccessible(true);
            JoinPoint joinPoint = mock(JoinPoint.class);
            when(joinPoint.getMethod()).thenReturn(validateMethod);
            when(joinPoint.getArgs()).thenReturn(new Object[] {-1});
            InvocationTargetException invocationTargetException =
                    catchThrowableOfType(() -> handleValidatedMethod.invoke(handler, joinPoint, validated),
                            InvocationTargetException.class);

            // then
            assertThat(invocationTargetException == null).isTrue();
        }
    }
}
