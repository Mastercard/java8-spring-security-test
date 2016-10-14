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

import junit.framework.AssertionFailedError;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A Spring TestExecutionListener that watches tests that are annotated with mock/test users
 * and validates that all users were executed.
 */
public class WatchWithUserTestExecutionListener extends AbstractTestExecutionListener {

    private final Map<String, TestMethodContext> methodContextMap = new HashMap<>();

    private Class testClass;

    /**
     * Build a list of mock/test users for the provided TestContext if not present.  If
     * present, validate that the user set in the SecurityContext matches the expected User based
     * on the current iteration of the TestContext.  If the actual user does not match expected,
     * then a test failure is raised.
     * @param testContext The TestContext being executed
     * @throws Exception if an Exception occurs
     */
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        testClass = testContext.getTestClass();
        String methodMapKey = testClass.getName() + ":" + testContext.getTestMethod().getName();
        TestMethodContext context = methodContextMap.get(methodMapKey);
        if (context == null) {
            context = new TestMethodContext(testContext.getTestMethod().getName(), findUserAnnotations(testContext),testContext.getApplicationContext());
            methodContextMap.put(methodMapKey, context);
        }

        boolean pop = false;

        User expectedUser = null;
        User user = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        if (context.getExecutionStack().size() > 0) {
            expectedUser = context.getExecutionStack().pop();
        }

        if (user != null || expectedUser != null) {
            if (user != null && !user.equals(expectedUser)) {
                throw new AssertionFailedError("Invalid SecurityUser, actual=" + user + ", expected=" + expectedUser);
            }
        }
    }

    /**
     * Validate that a test has been executed for every mock/test user expected.  If any remain,
     * raise a failure.
     * @param testContext The TestContext being executed
     * @throws Exception if an Exception occurs
     */
    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        int errorCount = 0;
        for (String key : methodContextMap.keySet()) {
            TestMethodContext context = methodContextMap.get(key);
            if (context.getExecutionStack().size() > 0) {
                System.out.println(context.getExecutionStack().size() + " Unused SecurityUsers on " + key);
                errorCount++;
            }
        }

        if (errorCount > 0) {
            throw new AssertionFailedError("Unused SecurityUsers remaining on " + errorCount + " methods");
        }
    }

    /**
     * Find all of the annotations that represent mock/test users for the TestContext.
     * @param testContext The TestContext for the test.
     * @return The List of annotations representing mock/test users.
     */
    private List<Annotation> findUserAnnotations(TestContext testContext) {
        List<Annotation> retVal = new ArrayList<>();

        //class annotations
        for (Annotation annotation : testContext.getTestClass().getAnnotations()) {
            retVal.addAll(findUserAnnotations(annotation));
        }

        //method annotations
        Annotation[] annotations = testContext.getTestMethod().getAnnotations();
        for (Annotation annotation : annotations) {
            retVal.addAll(findUserAnnotations(annotation));
        }

        return retVal;
    }

    /**
     * Locate all annotations representing mock/test users located within the provided annotation.
     * The search for annotations is executed recursively.
     * @param annotation The annotation to investigate.
     * @return A list of annotations that represent mock/test users.
     */
    private List<Annotation> findUserAnnotations(Annotation annotation) {
        return findUserAnnotationsRecursively(annotation, new ArrayList<>());
    }

    /**
     * Locate all annotations representing mock/test users located within the provided annotation by searching
     * recursively.  In order to prevent issues with self-referencing annotations (i.e. @Documented), a list
     * of previously checked annotations must be provided so that they can be ignored.
     * @param annotation The annotation to investigate.
     * @param ignoreList A list of Annotations that should be ignored when searching recursively.
     * @return A list of annotations that represent mock/test users.
     */
    private List<Annotation> findUserAnnotationsRecursively(Annotation annotation, List<Annotation> ignoreList) {
        List<Annotation> retVal = new ArrayList<>();

        if (annotation.annotationType().getAnnotation(WithSecurityContext.class) != null) {
            retVal.add(annotation);
        } else {
            List<Annotation> repeatedAnnoations = findRepeatableAnnotations(annotation);
            if (repeatedAnnoations.size() > 0) {
                for (Annotation child : repeatedAnnoations) {
                    retVal.addAll(findUserAnnotationsRecursively(child, ignoreList));
                }
            } else {
                for (Annotation child : annotation.annotationType().getDeclaredAnnotations()) {
                    if (!ignoreList.contains(child)) {
                        ignoreList.add(child);
                        retVal.addAll(findUserAnnotationsRecursively(child, ignoreList));
                    }
                }
            }
        }

        return retVal;
    }

    /**
     * Locate repeated annoations within a Java 8 annotation container.  If the annotation
     * provided is a container for annotations with the @Repeatable annotation, then the contained
     * annotations are returned.
     * @param annotation The annotation to investigate for repeated annotations.
     * @return A list containing repeated annotations if present.
     */
    private List<Annotation> findRepeatableAnnotations(Annotation annotation) {
        List<Annotation> retVal = new ArrayList<>();

        for (Method method : annotation.annotationType().getMethods()) {
            if (method.getName().equals("value")) {
                if (method.getReturnType().isArray()) {
                    try {
                        Annotation[] types = (Annotation[]) method.invoke(annotation);
                        retVal.addAll(Arrays.asList(types));
                    } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                        //ignore, must not be a container for annotations
                    }
                }
                break;
            }
        }

        return retVal;
    }

    /**
     * Internal class used to store the users that should be simulated while executing tests on a
     * specific test method.
     */
    private static class TestMethodContext {
        private final String methodName;
        private final Deque<User> executionStack = new ArrayDeque<>();

        /**
         * Construct a new TestMethodContext with the provided details.
         * @param methodName The name of the method being tested.
         * @param withTestUsers A list of annotations representing mock/test users.
         * @param context The ApplicationContext for the test.
         */
        public TestMethodContext(String methodName, List<Annotation> withTestUsers, ApplicationContext context) {
            this.methodName = methodName;

            WithSecurityContextFactory factory;
            for (Annotation withTestUser : withTestUsers) {
                factory = buildWithSecurityContextFactory(withTestUser, context);
                executionStack.add((User)factory.createSecurityContext(withTestUser).getAuthentication().getPrincipal());
            }
        }

        /**
         * Construct a new WithSecurityContextFactory for the provided class name.
         * @param withUser The annotation representing the mock/test user.
         * @param context The ApplicationContext under which the test is executing.
         * @return The instance if it could be constructed, otherwise null.
         */
        private WithSecurityContextFactory buildWithSecurityContextFactory(Annotation withUser, ApplicationContext context) {
            WithSecurityContext withSecurityContext = withUser.annotationType().getAnnotation(WithSecurityContext.class);

            Class<? extends WithSecurityContextFactory<? extends Annotation>> clazz = withSecurityContext.factory();

            return buildWithSecurityContextFactory(clazz, context);
        }

        /**
         * Construct a new WithSecurityContextFactory for the provided class name.
         * @param clazz The name of the class implementing the WithSecurityContextFactory interface.
         * @return The instance if it could be constructed, otherwise null.
         */
        private WithSecurityContextFactory buildWithSecurityContextFactory(Class<? extends WithSecurityContextFactory<? extends Annotation>> clazz, ApplicationContext context) {
            WithSecurityContextFactory retVal;

            try {
                retVal = context.getAutowireCapableBeanFactory().createBean(clazz);
            } catch (IllegalStateException e) {
                return BeanUtils.instantiateClass(clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return retVal;
        }

        /**
         * Retreive the name of the method.
         * @return The name of the method.
         */
        public String getMethodName() {
            return methodName;
        }

        /**
         * Retrieve the stack of users remained to be simulated during execution of the method.
         * @return The stack of users.
         */
        public Deque<User> getExecutionStack() {
            return executionStack;
        }
    }
}
