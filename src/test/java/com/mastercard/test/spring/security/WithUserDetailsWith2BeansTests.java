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


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains test cases that validate usage of @WithUserDetails when UserDetailsService
 * beans are present in the Spring application configuration.
 */
@RunWith(SpringSecurityJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringTestApplicationWith2UserDetailsServiceBeans.class})
public class WithUserDetailsWith2BeansTests {

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule();

    @Test
    @WithUserDetails(userDetailsServiceBeanName = "getUserDetailsService1")
    public void testWithDefaultWithUserDetails() {
        assertEquals("user", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @WithUserDetails(value="testuser", userDetailsServiceBeanName = "getUserDetailsService1")
    @Test
    public void testWithNameOverridenWithUserDetails() {
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
