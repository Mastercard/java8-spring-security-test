/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.mastercard.test.spring.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Duplication of <a href="https://github.com/spring-projects/spring-security">Spring Security</a> implementation of
 * <a href="https://github.com/spring-projects/spring-security/blob/master/test/src/main/java/org/springframework/security/test/context/support/WithUserDetails.java">org.springframework.security.test.context.support.WithUserDetails</a>
 * with the "Repeatable" annotation available within Java 8.
 *
 * When used with {@link SpringSecurityJUnit4ClassRunner} this annotation can be
 * added to a test method or class to emulate running with a UserDetails returned from the
 * UserDetailsService. In order to work with MockMvc The
 * SecurityContext that is used will have the following properties:
 *
 * <ul>
 * <li>The SecurityContext created with be that of
 * SecurityContextHolder.createEmptyContext()}</li>
 * <li>It will be populated with an UsernamePasswordAuthenticationToken that uses
 * the username of {@link #value()}.
 * </ul>
 *
 * @see WithMockUser
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Repeatable(WithUserDetailsContainer.class)
@WithSecurityContext(factory = WithUserDetailsSecurityContextFactory.class)
public @interface WithUserDetails {

    /**
     * The username to look up in the UserDetailsService
     *
     * @return
     */
    String value() default "user";

    /**
     * The bean name for the UserDetailsService to use. If this is not
     * provided, then the lookup is done by type and expects only a single
     * UserDetailsService bean to be exposed.
     *
     * @return the bean name for the UserDetailsService to use.
     */
    String userDetailsServiceBeanName() default "";
}
