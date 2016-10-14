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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * Spring Application configuration class used in the tests.
 */
@Configuration
public class SpringTestApplicationWith2UserDetailsServiceBeans {

    /**
     * Provide an instance of UserDetailsService to help test @WithUserDetails.
     * @return The instance of UserDetailsService.
     */
    @Bean
    public UserDetailsService getUserDetailsService1() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return new User(username,"password", new ArrayList<>());
            }
        };
    }

    /**
     * Provide an instance of UserDetailsService to help test @WithUserDetails.
     * @return The instance of UserDetailsService.
     */
    @Bean
    public UserDetailsService getUserDetailsService2() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return new User(username,"password", new ArrayList<>());
            }
        };
    }
}