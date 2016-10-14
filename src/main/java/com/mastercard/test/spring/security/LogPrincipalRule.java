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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * {@code LogPrincipalRule} is a custom JUnit {@link TestRule} that supports
 * <em>class-level</em> and <em>method-level</em> features of the <em>Spring Security
 * Test Framework</em> in standard JUnit tests by means of the
 * WithSecurityContext and
 * associated support classes and annotations.
 */
public class LogPrincipalRule implements TestRule {

     /**
     * Apply <em>class-level</em> and <em>method-level</em> features of the <em>Spring Security
     * Test Framework</em> to the supplied {@code base} statement.
     * <p>Specifically, this method retrieves the {@link java.security.Principal}
     * from the {@link org.springframework.security.core.context.SecurityContext}
     * and logs the information prior to test execution so that the identity under
     * which each test is executed is clear.  The information is logged to System.out.
     * @param base the base {@code Statement} that this rule should be applied to
     * @param description a {@code Description} of the current test execution
     * @return a statement that wraps the supplied {@code base} with class-level, or
     * method-level, features of the Spring Security Test Framework
     */
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Object principal = null;
                if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                    principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                }
                System.out.println("Principal=" + principal);
                base.evaluate();
            }
        };
    }
}
