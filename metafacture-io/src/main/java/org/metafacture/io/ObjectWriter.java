/*
 * Copyright 2013, 2014 Deutsche Nationalbibliothek
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

package org.metafacture.io;

import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.annotations.ReturnsAvailableArguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Writes objects to stdout or a file
 * @param <T> object type
 *
 * @author Christoph Böhme
 *
 */
@Description("Writes objects to stdout or a file")
@In(Object.class)
@Out(Void.class)
@FluxCommand("write")
public final class ObjectWriter<T> implements ConfigurableObjectWriter<T> {

    public static final String STDOUT = "stdout";

    private static final List<String> ARGUMENTS = Collections.unmodifiableList(Arrays.asList(STDOUT, "PATH"));

    private final ConfigurableObjectWriter<T> objectWriter;

    /**
     * Sets the destination to write objects to. If the destination is set to
     * {@value #STDOUT} the object is written to the standard output. Otherwise
     * it's written to a file of that name.
     *
     * @param destination the path to be written to or standard out if it's
     *                    {@value #STDOUT}
     */
    public ObjectWriter(final String destination) {
        if (STDOUT.equals(destination)) {
            objectWriter = new ObjectStdoutWriter<T>();
        }
        else {
            objectWriter = new ObjectFileWriter<T>(destination);
        }
    }

    /**
     * Returns the available arguments.
     *
     * @return arguments
     */
    @ReturnsAvailableArguments
    public static Collection<String> getArguments() {
        return ARGUMENTS;
    }

    @Override
    public String getEncoding() {
        return objectWriter.getEncoding();
    }

    @Override
    public void setEncoding(final String encoding) {
        objectWriter.setEncoding(encoding);
    }

    @Override
    public FileCompression getCompression() {
        return objectWriter.getCompression();
    }

    @Override
    public void setCompression(final FileCompression compression) {
        objectWriter.setCompression(compression);
    }

    @Override
    public void setCompression(final String compression) {
        objectWriter.setCompression(compression);
    }

    @Override
    public String getHeader() {
        return objectWriter.getHeader();
    }

    @Override
    public void setHeader(final String header) {
        objectWriter.setHeader(header);
    }

    @Override
    public String getFooter() {
        return objectWriter.getFooter();
    }

    @Override
    public void setFooter(final String footer) {
        objectWriter.setFooter(footer);
    }

    @Override
    public String getSeparator() {
        return objectWriter.getSeparator();
    }

    @Override
    public void setSeparator(final String separator) {
        objectWriter.setSeparator(separator);
    }

    /**
     * Controls whether to open files in append mode if they exist.
     *
     * @see ObjectFileWriter#setAppendIfFileExists
     *
     * @param appendIfFileExists true if new data should be appended,
     *                           false to overwrite the existing file
     */
    public void setAppendIfFileExists(final boolean appendIfFileExists) {
        if (objectWriter instanceof ObjectFileWriter) {
            ((ObjectFileWriter) objectWriter).setAppendIfFileExists(appendIfFileExists);
        }
    }

    @Override
    public void process(final T obj) {
        objectWriter.process(obj);
    }

    @Override
    public void resetStream() {
        objectWriter.resetStream();
    }

    @Override
    public void closeStream() {
        objectWriter.closeStream();
    }

}
