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

import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;

/**
 * Contains test cases that validate usage of @WithMockUser.
 */
@RunWith(SpringSecurityJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringTestApplication.class})
public class WithMockUserTests {

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule();

    @Test
    @WithMockUser
    public void testWithDefaultWithMockUser() {
        assertEquals("user", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals("password", SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        assertEquals(1, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
        assertEquals("ROLE_USER", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @WithMockUser(username = "testuser", password = "testpassword")
    public void testWithOverridesOnWithMockUser() {
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals("testpassword", SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        assertEquals(1, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
        assertEquals("ROLE_USER", SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @WithMockUser(roles = {"test1", "test2"})
    public void testWithRolesOnWithMockUser() {
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());

        Iterator<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator();
        assertEquals("ROLE_test1", authorities.next().getAuthority());
        assertEquals("ROLE_test2", authorities.next().getAuthority());
    }

    @Test
    @WithMockUser(authorities = {"test1", "test2"})
    public void testWithAuthoritiesOnWithMockUser() {
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());

        Iterator<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator();
        assertEquals("test1", authorities.next().getAuthority());
        assertEquals("test2", authorities.next().getAuthority());
    }
}
