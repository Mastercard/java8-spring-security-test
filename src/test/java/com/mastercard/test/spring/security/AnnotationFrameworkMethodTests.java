/*
 * Copyright (c) 2016 Mastercard Worldwide
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mastercard.test.spring.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Contains tests for AnnotationFrameworkMethod
 */
public class AnnotationFrameworkMethodTests {

    @Test(expected = NullPointerException.class)
    public void constructorThrowsIllegalArgumentExceptionIfMethodIsNull() throws NoSuchMethodException {
        WithMockUser mockUser = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);
        new AnnotationFrameworkMethod<Annotation>(null, mockUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalArgumentExceptionIfAnnotationIsNull() throws NoSuchMethodException {
        FrameworkMethod method = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        new AnnotationFrameworkMethod<Annotation>(method, null);
    }

    @Test
    public void getAnnotationReturnsTheAnnotation() throws NoSuchMethodException {
        FrameworkMethod method = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test = new AnnotationFrameworkMethod<>(method, mockUser);
        assertSame(mockUser, test.getAnnotation());
    }

    @Test
    public void toStringProvidesAStringWithAnnotationDetails() throws NoSuchMethodException {
        FrameworkMethod method = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test = new AnnotationFrameworkMethod<>(method, mockUser);
    }

    @Test
    public void equalsReturnsTrueWhenTheSameMethodAndSameAnnotation() throws NoSuchMethodException {
        FrameworkMethod method1 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser1 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test1 = new AnnotationFrameworkMethod<>(method1, mockUser1);

        FrameworkMethod method2 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser2 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test2 = new AnnotationFrameworkMethod<>(method2, mockUser2);

        assertTrue(test1.equals(test2));
    }

    @Test
    public void equalsReturnsFalseWhenTheSameMethodAndDifferentAnnotation() throws NoSuchMethodException {
        FrameworkMethod method1 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser1 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test1 = new AnnotationFrameworkMethod<>(method1, mockUser1);

        FrameworkMethod method2 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithUserDetails mockUser2 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithUserDetails.class);

        AnnotationFrameworkMethod<Annotation> test2 = new AnnotationFrameworkMethod<>(method2, mockUser2);

        assertFalse(test1.equals(test2));
    }

    @Test
    public void equalsReturnsFalseWhenDifferentMethodAndSameAnnotation() throws NoSuchMethodException {
        FrameworkMethod method1 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser1 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test1 = new AnnotationFrameworkMethod<>(method1, mockUser1);

        FrameworkMethod method2 = new FrameworkMethod(MockTest.class.getMethod("testing", new Class<?>[0]));
        WithMockUser mockUser2 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test2 = new AnnotationFrameworkMethod<>(method2, mockUser2);

        assertFalse(test1.equals(test2));
    }

    @Test
    public void equalsReturnsFalseWhenNotAnnotationFrameworkMethod() throws NoSuchMethodException {
        FrameworkMethod method1 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));
        WithMockUser mockUser1 = MockTest.class.getMethod("test", new Class<?>[0]).getAnnotation(WithMockUser.class);

        AnnotationFrameworkMethod<Annotation> test1 = new AnnotationFrameworkMethod<>(method1, mockUser1);

        FrameworkMethod method2 = new FrameworkMethod(MockTest.class.getMethod("test", new Class<?>[0]));

        assertFalse(test1.equals(method2));
    }

    class MockTest {
        @WithMockUser(username = "test")
        @WithUserDetails("test0")
        public void test() {

        }

        public void testing() {

        }
    }
}
