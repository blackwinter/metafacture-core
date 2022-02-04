/*
 * Copyright 2022 hbz NRW
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

package org.metafacture.metafix.util;

import org.metafacture.metafix.FixPath;
import org.metafacture.metafix.Metafix;
import org.metafacture.metafix.Record;
import org.metafacture.metafix.api.FixContext;
import org.metafacture.metafix.fix.Expression;

import java.util.List;
import java.util.Map;

public class TestContext implements FixContext {

    public TestContext() {
    }

    @Override
    public void execute(final Metafix metafix, final Record record, final List<String> params, final Map<String, String> options, final List<Expression> expressions) {
        new FixPath("BEFORE").appendIn(record, params.get(0));
        metafix.getRecordTransformer().process(expressions);
        new FixPath("AFTER").appendIn(record, options.get("data"));
    }

}
