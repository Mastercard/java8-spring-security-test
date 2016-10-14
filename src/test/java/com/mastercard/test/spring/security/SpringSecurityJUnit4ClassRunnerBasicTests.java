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
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.Assert.assertTrue;

/**
 * Contains tests cases for validating base/standard JUnit Runner behavior
 * independent of custom logic in SpringSecurityJUnit4ClassRunner.
 */
@RunWith(SpringSecurityJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringTestApplication.class})
@TestExecutionListeners({WatchWithUserTestExecutionListener.class})
public class SpringSecurityJUnit4ClassRunnerBasicTests {

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule();

    @Test(expected = NullPointerException.class)
    public void getUserThrowsNullPointerExceptionWhenUserIsNotSetup() {
        getUser();
    }

    @Test
    public void normalTestProcessingWorksAsExpectedOnSuccess() {
        assertTrue(true);
    }

    public User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
