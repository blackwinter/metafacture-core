/*
 * Copyright 2020 Fabian Steeg, hbz
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
package org.metafacture.html;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.ObjectReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultObjectPipe;

/**
 * Extracts the first script from an HTML document
 *
 * @author Fabian Steeg
 */
@Description("Extracts the first script from an HTML document")
@In(Reader.class)
@Out(String.class)
@FluxCommand("extract-script")
public class ScriptExtractor extends DefaultObjectPipe<Reader, ObjectReceiver<String>> {
    @Override
    public void process(final Reader reader) {
        try {
            Document document = Jsoup.parse(IOUtils.toString(reader));
            Element firstScript = document.select("script").first();
            getReceiver().process(firstScript.data());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
