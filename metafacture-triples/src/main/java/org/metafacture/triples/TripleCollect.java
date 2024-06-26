/*
 * Copyright 2013, 2014, 2016 Deutsche Nationalbibliothek
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

package org.metafacture.triples;

import org.metafacture.formeta.parser.FormetaParser;
import org.metafacture.formeta.parser.PartialRecordEmitter;
import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.StreamReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultObjectPipe;
import org.metafacture.framework.objects.Triple;
import org.metafacture.framework.objects.Triple.ObjectType;

/**
 * Collects named values to form records. The name becomes the id, the value is
 * split by 'separator' into name and value
 *
 * @author markus geipel
 *
 */
@Description("Collects named values to form records. The name becomes the id, the value is split by 'separator' into name and value")
@In(Triple.class)
@Out(StreamReceiver.class)
@FluxCommand("collect-triples")
public final class TripleCollect extends DefaultObjectPipe<Triple, StreamReceiver> {

    private final FormetaParser parser = new FormetaParser();
    private final PartialRecordEmitter emitter = new PartialRecordEmitter();

    private String currentSubject;

    /**
     * Creates an instance of {@link TripleCollect}.
     */
    public TripleCollect() {
        parser.setEmitter(emitter);
    }

    @Override
    public void process(final Triple triple) {
        if (currentSubject == null) {
            currentSubject = triple.getSubject();
            getReceiver().startRecord(currentSubject);
        }

        if (currentSubject.equals(triple.getSubject())) {
            decodeTriple(triple);
        }
        else {
            getReceiver().endRecord();
            currentSubject = triple.getSubject();
            getReceiver().startRecord(currentSubject);
            decodeTriple(triple);
        }
    }

    /**
     * Decodes a Triple. Passes the predicate and the object to the receiver.
     *
     * @param triple the Triple
     */
    public void decodeTriple(final Triple triple) {
        if (triple.getObjectType() == ObjectType.STRING) {
            getReceiver().literal(triple.getPredicate(), triple.getObject());
        }
        else if (triple.getObjectType() == ObjectType.ENTITY) {
            emitter.setDefaultName(triple.getPredicate());
            parser.parse(triple.getObject());
        }
        else {
            throw new UnsupportedOperationException(triple.getObjectType() + " can not yet be decoded");
        }
    }

    @Override
    protected void onResetStream() {
        if (currentSubject != null) {
            currentSubject = null;
            getReceiver().endRecord();
        }
    }

    @Override
    protected void onCloseStream() {
        if (currentSubject != null) {
            currentSubject = null;
            getReceiver().endRecord();
        }
    }

    @Override
    protected void onSetReceiver() {
        emitter.setReceiver(getReceiver());
    }

}
