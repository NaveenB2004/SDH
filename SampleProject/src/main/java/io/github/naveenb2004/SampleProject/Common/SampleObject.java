/*
 * Copyright 2024 Naveen Balasooriya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.naveenb2004.SampleProject.Common;

import java.io.Serializable;

public class SampleObject implements Serializable {

    private static final long serialVersionUID = 123456L;

    private final long id;
    private final String name;
    private final int age;
    private final boolean isMale;

    private SampleObject(long id, String name, int age, boolean isMale) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isMale = isMale;
    }

    public String toString() {
        return "id = " + id + ", name = " + name + ", age = " + age + ", isMale = " + isMale;
    }

    public static SampleObjectBuilder builder() {
        return new SampleObjectBuilder();
    }

    public static class SampleObjectBuilder {
        private long id;
        private String name;
        private int age;
        private boolean isMale;

        public SampleObjectBuilder id(long id) {
            this.id = id;
            return this;
        }

        public SampleObjectBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SampleObjectBuilder age(int age) {
            this.age = age;
            return this;
        }

        public SampleObjectBuilder isMale(boolean isMale) {
            this.isMale = isMale;
            return this;
        }

        public SampleObject build() {
            return new SampleObject(id, name, age, isMale);
        }
    }
}
