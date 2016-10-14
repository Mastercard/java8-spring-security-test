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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Contains tests cases for validating SpringSecurityJUnit4ClassRunner
 * functionality when mock/test user annotations are located at the
 * method level.
 */
@RunWith(SpringSecurityJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringTestApplication.class})
@TestExecutionListeners({WatchWithUserTestExecutionListener.class})
public class SpringSecurityJUnit4ClassRunnerMethodAnnotationTests {

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule();

    @Test
    @WithMockUser
    public void runningWithOneBasicAnnotatedUserWhichExecutesOneTime() {
        assertNotNull(getUser());
    }

    @Test
    @WithMockUser
    @WithMockUser
    public void runningWithTwoBasicAnnotatedUsersWhichExecutesTwoTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdmin
    public void runningWithOneNamedAnnotatedUserWhichExecutesOneTime() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdmin
    @WithMockUser
    public void runningWithOneNamedAnnotatedUserAndOneBasicAnnoatedUserWhichExecutesTwoTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdmin
    @WithMockUser
    @WithMockUser
    public void runningWithOneNamedAnnotatedUserAndTwoBasicAnnotatedUsersWhichExecutesThreeTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithIssuers
    public void runningWithOneNamedCompositeOfAnnotationUsersWhichExecutesTwoTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdminPlus1
    public void runningWithOneNamedAnnotationThatIncludesANamedAnnotationUserAndOneBasicAnnotatedUserWhichExecutesTwoTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdminPlus2
    public void runningWithOneNamedAnnotationThatIncludesANamedAnnotationUserAndTwoBasicAnnotatedUsersWhichExecutesThreeTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdminAndCardholder
    public void runningWithOneNamedAnnotationThatIncludesTwoNamedAnnotationUsersWhichExecutesTwoTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdminAndCardholderPlus1
    public void runningWithOneNamedAnnotationThatIncludesTwoNamedAnnotationUsersAndOneBasicAnnotatedUserWhichExecutesThreeTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSystemAdminAndCardholderPlus2
    public void runningWithOneNamedAnnotationThatIncludesTwoNamedAnnotationUsersAndTwoBasicAnnotatedUsersWhichExecutesFourTimes() {
        assertNotNull(getUser());
    }

    @Test
    @WithSingleUserDetails
    public void runningWithSingleNamedAnnotationThatRequiresAutowiringForOneUserWhichExecutesOneTime() {
        assertNotNull(getUser());
    }

    @Test
    @WithDualUserDetails
    public void runningWithSingleNamedAnnotationThatRequiresAutowiringForTwoUsersWhichExecutesTwoTimes() {
        assertNotNull(getUser());
    }

    @Test(expected = SecurityException.class)
    @WithMockUser
    public void runningWithOneBasicUserSupportsExpectedExceptionsAndExecutesOneTime() {
        throw new SecurityException("Test");
    }

    @Test(expected = SecurityException.class)
    @WithMockUser
    @WithMockUser
    public void runningWithTwoBasicUsersSupportsExpectedExceptionsAndExecutesTwoTimes() {
        throw new SecurityException("Test");
    }

    @Test(expected = SecurityException.class)
    @WithSystemAdmin
    public void runningWithOneNamedUserSupportsExpectedExceptionsAndExecutesOneTime() {
        throw new SecurityException("Test");
    }

    @Test(expected = SecurityException.class)
    @WithUserDetails
    public void runningWithOneBasicUserThatRequiresAutowiringSupportsExpectedExceptionsAndExecutesOneTime() {
        throw new SecurityException("Test");
    }


    //============= TEST ANNOTATIONS BELOW ========================
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(roles = {"A"})
    public @interface WithSystemAdmin {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(roles = {"B"})
    @WithMockUser(roles = {"C"})
    public @interface WithIssuers {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithSystemAdmin
    @WithMockUser(roles = {"D"})
    public @interface WithSystemAdminPlus1 {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithSystemAdmin
    @WithMockUser(roles = {"E"})
    @WithMockUser(roles = {"F"})
    public @interface WithSystemAdminPlus2 {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(roles = {"G"})
    public @interface WithCardholder {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithSystemAdmin
    @WithCardholder
    public @interface WithSystemAdminAndCardholder {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithSystemAdmin
    @WithCardholder
    @WithMockUser(roles = {"H"})
    public @interface WithSystemAdminAndCardholderPlus1 {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithSystemAdmin
    @WithCardholder
    @WithMockUser(roles = {"I"})
    @WithMockUser(roles = {"J"})
    public @interface WithSystemAdminAndCardholderPlus2 {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithUserDetails
    public @interface WithSingleUserDetails {
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @WithUserDetails
    @WithUserDetails
    public @interface WithDualUserDetails {
    }

    public User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
