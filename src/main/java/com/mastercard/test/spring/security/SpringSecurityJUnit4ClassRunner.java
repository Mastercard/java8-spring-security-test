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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.context.SpringSecurityTestBootstrapUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The SpringSecurityJUnit4ClassRunner implements the JUnit 4 standard test case class model, as defined by the
 * annotations in the org.junit package. It provides support for executing tests with authenticated
 * users established with Spring Security Test support using @WithSecurityContext.
 * Each mock/test user is executed as a separate test with a separate result.
 *
 * <p>SpringSecurityJUnit4ClassRunner extends the SpringJUnit4ClassRunner without altering its underlying
 * behavior.
 * </p>
 */
public class SpringSecurityJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    /**
     * Construct a new {@code SpringSecurityJUnit4ClassRunner} to execute
     * standard JUnit tests with multiple mock/test users.
     * @param clazz the test class to be run
     */
     public SpringSecurityJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    /**
     * Returns a list of child tests that include a single test for each test/mock
     * user combination.
     * @return A list of FrameworkMethods that represent the child tests to
     * be executed for the test class.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> retVal = new ArrayList<>();

        List<Annotation> testClassAnnotations = new ArrayList<>();
        for (Annotation annotation : getTestClass().getAnnotations()) {
            testClassAnnotations.addAll(findUserAnnotations(annotation));
        }

        List<Annotation> userAnnotations;
        List<FrameworkMethod> methods = super.getChildren();
        for (FrameworkMethod method : methods) {
            userAnnotations = new ArrayList<>();

            //add test class annotations to each method
            userAnnotations.addAll(testClassAnnotations);

            //for all method annotations check to see if they have WithSecurityContext and include each one in the list of children
            for (Annotation annotation : method.getAnnotations()) {
                userAnnotations.addAll(findUserAnnotations(annotation));
            }

            if (userAnnotations.size() > 0) {
                AnnotationFrameworkMethod annotationFrameworkMethod;
                for (Annotation userAnnotation : userAnnotations) {
                    annotationFrameworkMethod = new AnnotationFrameworkMethod(method, userAnnotation);
                    retVal.add(annotationFrameworkMethod);
                }
            } else {
                retVal.add(method);
            }
        }

        return retVal;
    }

    /**
     * Construct a description that properly nests the child tests with mock/test users.
     * The implementation relies on the inherited describeChild() method where possible.
     * @return A Description defining the tests to be run by the receiver.
     */
    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(getName(), getRunnerAnnotations());

        Map<String, Description> parentDescriptions = new HashMap<>();

        for (FrameworkMethod child : getChildren()) {
            if (containsUserAnnotation(child)) {
                Description parent = parentDescriptions.get(child.getName());
                if (parent == null) {
                    parent = super.describeChild(child);
                    parentDescriptions.put(child.getName(), parent);
                    description.addChild(parent);
                }
                parent.addChild(describeChild(child));
            } else {
                description.addChild(super.describeChild(child));
            }
        }
        return description;
    }

    /**
     * Check to see if the method contains annotations that represent mock/test users.
     * @param method The method to check for user annotations.
     * @return True if the method's annotations contain user annotations, otherwise false.
     */
    private boolean containsUserAnnotation(FrameworkMethod method) {
        return findUserAnnotations(method.getAnnotations()).size() > 0;
    }

    /**
     * Construct a Description that represents the method provided. The implementation relies
     * on the inherited describeChild() method where possible.
     * @param method The method to describe.
     * @return The Description representing the provided method.
     */
    @Override
    protected Description describeChild(FrameworkMethod method) {
        Description retVal;
        if (method instanceof AnnotationFrameworkMethod) {
            AnnotationFrameworkMethod withUserTestFrameworkMethod = (AnnotationFrameworkMethod) method;
            retVal = Description.createTestDescription(method.getDeclaringClass().getName(), method.getMethod().getName(), new TestIdentifier(withUserTestFrameworkMethod.toString()));
        } else {
            retVal = super.describeChild(method);
        }

        return retVal;
    }

    /**
     * Run the test corresponding to the child, which can be assumed to be an element
     * of the list returned by getChildren(). Ensures that relevant test events are
     * reported through the notifier.
     *
     * The implementation wraps the inherited runChild() method to insert the mock/test
     * user into the SecurityContext prior to execution and removes it after execution.
     *
     * @param frameworkMethod The method representing the child test.
     * @param notifier The notifier for the test execution.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
        if (frameworkMethod instanceof AnnotationFrameworkMethod) {
            AnnotationFrameworkMethod annotationFrameworkMethod = (AnnotationFrameworkMethod) frameworkMethod;

            Annotation userAnnotation = annotationFrameworkMethod.getAnnotation();
            WithSecurityContext withSecurityContext = userAnnotation.annotationType().getAnnotation(WithSecurityContext.class);

            Class<? extends WithSecurityContextFactory<? extends Annotation>> clazz = withSecurityContext.factory();

            WithSecurityContextFactory withSecurityContextFactory = buildWithSecurityContextFactory(clazz);

            SecurityContext securityContext = null;
            if (withSecurityContextFactory != null) {
                securityContext = withSecurityContextFactory.createSecurityContext(userAnnotation);
            }

            if (securityContext == null) {
                securityContext = SecurityContextHolder.createEmptyContext();
            }
            SecurityContextHolder.setContext(securityContext);
        }
        super.runChild(frameworkMethod, notifier);

        SecurityContextHolder.clearContext();
    }

    /**
     * Construct a new WithSecurityContextFactory for the provided class name.
     * @param clazz The name of the class implementing the WithSecurityContextFactory interface.
     * @return The instance if it could be constructed, otherwise null.
     */
    private WithSecurityContextFactory buildWithSecurityContextFactory(Class<? extends WithSecurityContextFactory<? extends Annotation>> clazz) {
        WithSecurityContextFactory retVal;

        ApplicationContext context = getApplicationContext(getTestClass().getJavaClass());

        try {
            retVal = context.getAutowireCapableBeanFactory().createBean(clazz);
        } catch (IllegalStateException e) {
            return BeanUtils.instantiateClass(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct an instance of " + clazz.getName(), e);
        }

        return retVal;
    }

    /**
     * Locate any annotations representing mock/test users located within the provided annotations.
     * The search for annotations is executed recursively.
     * @param annotations The annotations to investigate.
     * @return A list of annotations that represent mock/test users.
     */
    private List<Annotation> findUserAnnotations(Annotation[] annotations) {
        List<Annotation> retVal = new ArrayList<>();

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
     * Retrieve the ApplicationContext for the provided test Class.
     * @param clazz The class under test.
     * @return The ApplicationContext.
     */
    public ApplicationContext getApplicationContext(Class<?> clazz) {
        return SpringSecurityTestBootstrapUtils.resolveTestContextBootstrapper(SpringSecurityTestBootstrapUtils.createBootstrapContext(clazz)).buildTestContext().getApplicationContext();
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
            if ("value".equals(method.getName())) {
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
     * Simple Serializable class to use as the unique identifier for child tests
     * that roll up under a single method name.
     */
    public static class TestIdentifier implements Serializable {
        static final long serialVersionUID = 1L;

        private final String name;

        /**
         * Construct a new TestIdentifier with the provided name.
         * @param name The name for the identifier.
         * @throws IllegalArgumentException if the name provided is null.
         */
        public TestIdentifier(String name) {
            if (name == null) {
                throw new IllegalArgumentException("The name provided is null");
            }
            this.name = name;
        }

        /**
         * Retrieve the name of the test.
         * @return The name.
         */
        public String getName() {
            return name;
        }
    }

}
