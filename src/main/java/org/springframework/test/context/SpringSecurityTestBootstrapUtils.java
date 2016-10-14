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
package org.springframework.test.context;

/**
 * SpringSecurityTestBootstrapUtils is a wrapper to allow non-package access to key utility methods.
 * This wrapper approach was chosen to reduce the amount of Spring code that would need to be duplicated.
 */
public final class SpringSecurityTestBootstrapUtils {

    /**
     * Private Constructor to prevent construction of utility class.
     */
    private SpringSecurityTestBootstrapUtils() {

    }

    /**
     * Construct a TestContextBootstrapper for a given BootstrapContext.
     * The implementation wraps the corresponding method in the Spring Security Test Support
     * framework's BootstrapUtils within the same package.
     * @param bootstrapContext The BootstrapContext for the TestContext.
     * @return The constructed TestContextBootstrapper.
     */
    public static TestContextBootstrapper resolveTestContextBootstrapper(BootstrapContext bootstrapContext) {
        return BootstrapUtils.resolveTestContextBootstrapper(bootstrapContext);
    }

    /**
     * Create a BootstrapContext for a provided test class.
     * The implementation wraps the corresponding method in the Spring Security Test Support
     * framework's BootstrapUtils within the same package.
     * @param clazz The test class.
     * @return The constructed BootstrapContext.
     */
    public static BootstrapContext createBootstrapContext(Class<?> clazz) {
        return BootstrapUtils.createBootstrapContext(clazz);
    }
}