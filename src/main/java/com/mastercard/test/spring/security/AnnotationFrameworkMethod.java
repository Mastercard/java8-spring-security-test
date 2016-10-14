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

import org.junit.runners.model.FrameworkMethod;

import java.lang.annotation.Annotation;

/**
 * Appends an Annotation to the standard JUnit FrameworkMethod implementation used in the
 * JUnit 4 Runner.
 * @param <T> Any annotation to append to the FrameworkMethod.
 */
public class AnnotationFrameworkMethod<T extends Annotation> extends FrameworkMethod {
    private static final int HASH_PRIME = 31;

    private final T annotation;

    /**
     * Construct a new instance to wrap the provided FrameworkMethod.
     * @param method The FrameworkMethod to wrap.
     * @param annotation The annotation to append to the method.
     * @throws IllegalArgumentException if the annotation provided is null.
     */
    public AnnotationFrameworkMethod(FrameworkMethod method, T annotation) {
        super(method.getMethod());
        if (annotation == null) {
            throw new IllegalArgumentException("The annotation provided is null");
        }
        this.annotation = annotation;
    }

    /**
     * Retrieve the annotation.
     * @return The annotation.
     */
    public T getAnnotation() {
        return annotation;
    }

    /**
     * Provide a text representation of the instance.
     * @return A text representation of the instance.
     */
    public String toString() {
        return super.toString() + annotation.toString();
    }

    /**
     * Determine if the provided object is equal to the current instance.
     * @param obj The object to compare.
     * @return True if the object is equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;

        if (obj != null) {
            retVal = super.equals(obj);

            if (retVal && this.getClass() == obj.getClass()) {
                AnnotationFrameworkMethod test = (AnnotationFrameworkMethod) obj;
                retVal = getAnnotation().equals(test.getAnnotation());
            } else {
                retVal = false;
            }
        }

        return retVal;
    }

    /**
     * Calculate a hash value.
     * @return The hash value.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = HASH_PRIME * result + (annotation != null ? annotation.hashCode() : 0);
        return result;
    }
}
