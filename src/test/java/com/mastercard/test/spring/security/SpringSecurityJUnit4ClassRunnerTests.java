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
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

/**
 * Contains tests for SpringSecurityJUnit4ClassRunner methods that have
 * been extended from its superclass.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringTestApplication.class})
public class SpringSecurityJUnit4ClassRunnerTests {

    //NOTE: Testing currently relies on SpringSecurityJUnit4ClassRunner*IntegrationTests to validate runChild(...)

    @Rule
    public LogPrincipalRule logPrincipalRule = new LogPrincipalRule();

    @Test
    public void getChildrenCreatesAChildForEachSecurityUser() throws Exception {
        //establish expected methods that use @WithTestUser
        List<String> expectedAnnotatedMethodNames = buildListOfMethodsUsingWithTestUser();

        //establish expected methods that do not use @WithTestUser
        List<String> expectedMethodNames = buildListOfMethodsNotUsingWithTestUser();

        //Initialize Runner to be tested and resulting Children
        SpringSecurityJUnit4ClassRunner runner = new SpringSecurityJUnit4ClassRunner(MockWithMockUserTest.class);
        List<FrameworkMethod> actualChildren = runner.getChildren();

        //Initialize Runner and Description to which results will be compared
        BlockJUnit4ClassRunner expectedRunner = new BlockJUnit4ClassRunner(MockWithMockUserTest.class);
        Method method1 = BlockJUnit4ClassRunner.class.getDeclaredMethod("getChildren");
        method1.setAccessible(true);
        List<FrameworkMethod> expectedChildren = (List<FrameworkMethod>) method1.invoke(expectedRunner);

        assertEquals(expectedChildren.size()+1, actualChildren.size());

        for (int i=0; i < actualChildren.size(); i++) {
            FrameworkMethod actualChild = actualChildren.get(i);
            FrameworkMethod expectedChild = findFrameworkMethodByName(actualChild.getName(), expectedChildren);

            assertEquals(expectedChild.getDeclaringClass(), actualChild.getDeclaringClass());
            assertEquals(expectedChild.getName(), actualChild.getName());
            assertEquals(expectedChild.getMethod(), actualChild.getMethod());
            assertEquals(expectedChild.getReturnType(), actualChild.getReturnType());
            assertEquals(expectedChild.getType(), actualChild.getType());
            assertEquals(expectedChild.isPublic(), actualChild.isPublic());
            assertEquals(expectedChild.isStatic(), actualChild.isStatic());

            assertAnnotationsAreEqual(expectedChild.getAnnotations(), actualChild.getAnnotations());

            if (actualChild instanceof AnnotationFrameworkMethod) {
                AnnotationFrameworkMethod withUserTestFrameworkMethod = (AnnotationFrameworkMethod) actualChild;
                assertNotNull(withUserTestFrameworkMethod.getAnnotation());

                //Remove Description from the expected list of methods using @WithTestUser
                assertTrue(expectedAnnotatedMethodNames.remove(actualChild.getName()));
            } else {
                //Remove Description from the expected list of methods not using @WithTestUser
                assertTrue(expectedMethodNames.remove(actualChild.getName()));
            }
        }

        //assert that all expected 'Test' Descriptions have been identified (and removed from expected lists)
        assertEquals(0, expectedMethodNames.size());
        assertEquals(0, expectedAnnotatedMethodNames.size());
    }

    @Test
    public void getDescriptionCreatesChildrenForEachSecurityUser() throws Exception {
        //establish expected methods that use @WithTestUser
        List<String> expectedAnnotatedMethodNames = buildListOfMethodsUsingWithTestUser();

        //establish expected methods that do not use @WithTestUser
        List<String> expectedMethodNames = buildListOfMethodsNotUsingWithTestUser();

        //Initialize Runner to be tested and resulting Description
        SpringSecurityJUnit4ClassRunner runner = new SpringSecurityJUnit4ClassRunner(MockWithMockUserTest.class);
        Description actualDescription = runner.getDescription();

        //Initialize Runner and Description to which results will be compared
        BlockJUnit4ClassRunner expectedRunner = new BlockJUnit4ClassRunner(MockWithMockUserTest.class);
        Description expectedDescription = expectedRunner.getDescription();

        //Compare Tested Description to baseline Description (root Description)
        assertDescriptionDetailsEqual(expectedDescription, actualDescription);
        assertEquals(expectedDescription.getChildren().size(), actualDescription.getChildren().size());

        //Compare Children Descriptions
        for (int i = 0; i < actualDescription.getChildren().size(); i++) {
            Description expectedChild = expectedDescription.getChildren().get(i);
            Description actualChild = actualDescription.getChildren().get(i);

            assertEquals(expectedChild.getClassName(), actualChild.getClassName());
            assertEquals(expectedChild.getMethodName(), actualChild.getMethodName());
            assertEquals(expectedChild.getDisplayName(), actualChild.getDisplayName());
            assertEquals(expectedChild.isEmpty(), actualChild.isEmpty());

            if (!actualChild.getMethodName().endsWith("WithoutWithMockUser")) {
                //Has WithTestUser
                assertTrue(actualChild.isSuite());
                assertFalse(actualChild.isTest());

                //Compare grandchildren Descriptions
                for (Description grandchild : actualChild.getChildren()) {
                    assertEquals(actualChild.getClassName(), grandchild.getClassName());
                    assertEquals(actualChild.getMethodName(), grandchild.getMethodName());
                    assertEquals(actualChild.getDisplayName(), grandchild.getDisplayName());
                    assertEquals(actualChild.isEmpty(), grandchild.isEmpty());
                    assertFalse(grandchild.isSuite());
                    assertTrue(grandchild.isTest());

                    //Remove Description from the expected list of methods using @WithTestUser
                    assertTrue(expectedAnnotatedMethodNames.remove(grandchild.getMethodName()));
                }
            } else {
                //Remove Description from the expected list of methods NOT using @WithTestUser
                assertTrue(expectedMethodNames.remove(actualChild.getMethodName()));
            }
        }

        //assert that all expected 'Test' Descriptions have been identified (and removed from expected lists)
        assertEquals(0, expectedMethodNames.size());
        assertEquals(0, expectedAnnotatedMethodNames.size());
    }

    @Test
    public void describeChildMatchesBlockJUnit4ClassRunnerForNonWithTestUserTest() throws Exception {
        SpringSecurityJUnit4ClassRunner runner = new SpringSecurityJUnit4ClassRunner(MockWithMockUserTest.class);

        FrameworkMethod method = new FrameworkMethod(MockWithMockUserTest.class.getMethod("testWithoutWithMockUser"));
        Description actualDescription = runner.describeChild(method);

        BlockJUnit4ClassRunner expectedRunner = new BlockJUnit4ClassRunner(MockWithMockUserTest.class);
        Method method1 = BlockJUnit4ClassRunner.class.getDeclaredMethod("describeChild", FrameworkMethod.class);
        method1.setAccessible(true);
        Description expectedDescription = (Description) method1.invoke(expectedRunner, method);

        assertDescriptionDetailsEqual(expectedDescription, actualDescription);
        assertEquals(expectedDescription.getChildren().size(), actualDescription.getChildren().size());
    }

    @Test
    public void describeChildCreatesDescriptionForWithTestUserTest() throws Exception {
        SpringSecurityJUnit4ClassRunner runner = new SpringSecurityJUnit4ClassRunner(MockWithMockUserTest.class);

        Method method2Test = MockWithMockUserTest.class.getMethod("testWithWithMockUser");
        FrameworkMethod method = new AnnotationFrameworkMethod(new FrameworkMethod(method2Test), method2Test.getDeclaredAnnotation(WithMockUser.class));
        Description actualDescription = runner.describeChild(method);

        BlockJUnit4ClassRunner expectedRunner = new BlockJUnit4ClassRunner(MockWithMockUserTest.class);
        Method method1 = BlockJUnit4ClassRunner.class.getDeclaredMethod("describeChild", FrameworkMethod.class);
        method1.setAccessible(true);
        Description expectedDescription = (Description) method1.invoke(expectedRunner, method);

        assertDescriptionDetailsEqual(expectedDescription, actualDescription);
        assertEquals(expectedDescription.getChildren().size(), actualDescription.getChildren().size());
    }

    private void assertDescriptionDetailsEqual(Description expectedDescription, Description actualDescription) {
        assertEquals(expectedDescription.getClassName(), actualDescription.getClassName());
        assertEquals(expectedDescription.getMethodName(), actualDescription.getMethodName());
        assertEquals(expectedDescription.getDisplayName(), actualDescription.getDisplayName());
        assertEquals(expectedDescription.isEmpty(), actualDescription.isEmpty());
        assertEquals(expectedDescription.isSuite(), actualDescription.isSuite());
        assertEquals(expectedDescription.isTest(), actualDescription.isTest());
    }

    private List<String> buildListOfMethodsUsingWithTestUser() {
        List<String> retVal = new ArrayList<>();
        retVal.add("testWithWithMockUser");
        retVal.add("testWithTwoWithMockUser");
        retVal.add("testWithTwoWithMockUser");
        return retVal;
    }

    private List<String> buildListOfMethodsNotUsingWithTestUser() {
        List<String> retVal = new ArrayList<>();
        retVal.add("testWithoutWithMockUser");
        retVal.add("getUserThrowsNullPointerExceptionWhenWithoutWithMockUser");
        return retVal;
    }

    private FrameworkMethod findFrameworkMethodByName(String name, List<FrameworkMethod> methods) {
        FrameworkMethod retVal = null;

        for (FrameworkMethod method : methods){
            if (name.equals(method.getName())) {
                retVal = method;
                break;
            }
        }

        return retVal;
    }

    private void assertAnnotationsAreEqual(Annotation[] expected, Annotation[] actual) {
        assertEquals(expected.length, actual.length);

        Annotation expectedAnno = null;
        Annotation actualAnno = null;
        for (int i =0; i < expected.length; i++) {
            expectedAnno = expected[i];
            actualAnno = actual[i];

            assertEquals(expectedAnno.getClass(), actualAnno.getClass());
        }
    }
}
