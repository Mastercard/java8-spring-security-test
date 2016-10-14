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


import java.lang.reflect.Method;
import org.junit.Test;


import static junit.framework.Assert.assertEquals;

/**
 * Contains tests for WithUserDetailsContainer
 */
public class WithUserDetailsContainerTests {

    @Test
    public void containerReturnsMultipleAnnotations() throws NoSuchMethodException {
        Method method = MockTest.class.getMethod("test", new Class<?>[0]);

        WithUserDetailsContainer container = method.getAnnotation(WithUserDetailsContainer.class);
        WithUserDetails[] users = container.value();

        assertEquals(2, users.length);
        assertEquals("test0", users[0].value());
        assertEquals("test1", users[1].value());
    }


    class MockTest {

        @WithUserDetails("test0")
        @WithUserDetails("test1")
        public void test() {

        }
    }
}
