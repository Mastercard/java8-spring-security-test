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

import org.junit.After;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Contains test cases for LogPrincipalRule.
 */
public class LogPrincipalRuleTests {

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void ruleDoesNotBreakWhenUserIsNotProvided() throws Throwable {
        DefaultStatement statement = new DefaultStatement();
        Description description = Description.createTestDescription(MockWithMockUserTest.class.getName(), "test");

        LogPrincipalRule rule = new LogPrincipalRule();

        Statement actual = rule.apply(statement, description);
        actual.evaluate();

        assertNotSame(statement, actual);
        assertTrue(statement.isEvaluated());
    }

    @Test
    public void ruleDoesNotBreakWhenUserIsProvided() throws Throwable {
        DefaultStatement statement = new DefaultStatement();
        Description description = Description.createTestDescription(MockWithMockUserTest.class.getName(), "test");

        LogPrincipalRule rule = new LogPrincipalRule();

        Statement actual = rule.apply(statement, description);

        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "password"));

        actual.evaluate();

        assertNotSame(statement, actual);
        assertTrue(statement.isEvaluated());
    }

    @Test
    public void ruleDoesNotBreakWhenAuthenticationIsNotProvided() throws Throwable {
        DefaultStatement statement = new DefaultStatement();
        Description description = Description.createTestDescription(MockWithMockUserTest.class.getName(), "test");

        LogPrincipalRule rule = new LogPrincipalRule();

        Statement actual = rule.apply(statement, description);

        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());

        actual.evaluate();

        assertNotSame(statement, actual);
        assertTrue(statement.isEvaluated());
    }

    class DefaultStatement extends Statement {

        private boolean isEvaluated = false;

        @Override
        public void evaluate() throws Throwable {
            isEvaluated = true;
        }

        public boolean isEvaluated() {
            return isEvaluated;
        }
    }
}
