/*
 * Copyright 2021 Fabian Steeg, hbz
 *
 * Licensed under the Apache License, Version 2.0 the "License";
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

package org.metafacture.metafix;

import org.metafacture.metafix.api.FixPredicate;

import java.util.List;
import java.util.Map;

public enum FixConditional implements FixPredicate {

    all_contain {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ALL, CONTAINS);
        }
    },
    any_contain {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ANY, CONTAINS);
        }
    },
    none_contain {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return !any_contain.test(metafix, record, params, options);
        }
    },
    str_contain {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(params, CONTAINS);
        }
    },

    all_equal {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ALL, EQUALS);
        }
    },
    any_equal {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ANY, EQUALS);
        }
    },
    none_equal {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return !any_equal.test(metafix, record, params, options);
        }
    },
    str_equal {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(params, EQUALS);
        }
    },

    exists {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return record.containsPath(params.get(0));
        }
    },

    in {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            final Value value1 = record.get(params.get(0));
            final Value value2 = record.get(params.get(1));

            return value1 != null && value2 != null && value1.<Boolean>extractType((m, c) -> m
                .ifArray(a1 -> value2.matchType()
                    .ifArray(a2 -> c.accept(a1.equals(a2)))
                    .orElse(v -> c.accept(false))
                )
                .ifHash(h1 -> value2.matchType()
                    .ifHash(h2 -> c.accept(h1.equals(h2)))
                    .orElse(v -> c.accept(false))
                )
                .ifString(s1 -> value2.matchType()
                    .ifArray(a2 -> c.accept(a2.stream().anyMatch(value1::equals)))
                    .ifHash(h2 -> c.accept(h2.containsField(s1)))
                    .ifString(s2 -> c.accept(s1.equals(s2)))
                )
            );
        }
    },
    is_contained_in {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return in.test(metafix, record, params, options);
        }
    },

    is_array {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, Value::isArray);
        }
    },
    is_empty {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, IS_EMPTY);
        }
    },
    is_false {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testStringConditional(record, params, IS_FALSE); // TODO: strict=false
        }
    },
    is_hash {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return is_object.test(metafix, record, params, options);
        }
    },
    is_number {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testStringConditional(record, params, IS_NUMBER);
        }
    },
    is_object {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, Value::isHash);
        }
    },
    is_string {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, Value::isString) && !is_number.test(metafix, record, params, options);
        }
    },
    is_true {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testStringConditional(record, params, IS_TRUE); // TODO: strict=false
        }
    },

    all_match {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ALL, MATCHES);
        }
    },
    any_match {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ANY, MATCHES);
        }
    },
    none_match {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return !any_match.test(metafix, record, params, options);
        }
    },
    str_match {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(params, MATCHES);
        }
    },

    greater_than {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ALL, GREATER_THAN);
        }
    },
    less_than {
        @Override
        public boolean test(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options) {
            return testConditional(record, params, ALL, LESS_THAN);
        }
    }

}
