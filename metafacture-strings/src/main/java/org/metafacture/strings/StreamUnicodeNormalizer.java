/*
 * Copyright 2016 Christoph Böhme
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

package org.metafacture.strings;

import org.metafacture.framework.FluxCommand;
import org.metafacture.framework.StreamReceiver;
import org.metafacture.framework.annotations.Description;
import org.metafacture.framework.annotations.In;
import org.metafacture.framework.annotations.Out;
import org.metafacture.framework.helpers.DefaultStreamPipe;

import java.text.Normalizer;

/**
 * Normalises Unicode characters in record identifiers, entity and literal
 * names and literal values. Unicode normalisation converts between precomposed
 * characters and composed characters. There are four different forms of
 * conversion which can be selected using
 * {@link #setNormalizationForm(Normalizer.Form)}.
 * <p>
 * In the default configuration only literal values are
 * converted. The {@link #setNormalizeIds(boolean)},
 * {@link #setNormalizeKeys(boolean)} and {@link #setNormalizeValues(boolean)}
 * parameters can be used to change this behaviour.
 *
 * @author Christoph Böhme
 */
@Description("Normalises composed and decomposed Unicode characters.")
@In(StreamReceiver.class)
@Out(StreamReceiver.class)
@FluxCommand("normalize-unicode-stream")
public final class StreamUnicodeNormalizer extends DefaultStreamPipe<StreamReceiver> {

    /**
     * The default value for {@link #setNormalizationForm(Normalizer.Form)}.
     */
    public static final Normalizer.Form DEFAULT_NORMALIZATION_FORM = Normalizer.Form.NFC;

    private boolean normalizeIds;
    private boolean normalizeKeys;
    private boolean normalizeValues = true;

    private Normalizer.Form normalizationForm = DEFAULT_NORMALIZATION_FORM;

    /**
     * Creates an instance of {@link StreamUnicodeNormalizer}.
     */
    public StreamUnicodeNormalizer() {
    }

    /**
     * Controls whether to normalize record identifiers. By default record
     * identifiers are not normalized.
     * <p>
     * This parameter may be changed at any time. It becomes immediately
     * effective and affects all subsequently received <i>start-record</i>
     * events.
     *
     * @param normalizeIds if true identifiers are normalized, otherwise not.
     */
    public void setNormalizeIds(final boolean normalizeIds) {
        this.normalizeIds = normalizeIds;
    }

    /**
     * Checks whether IDs should be normalized.
     *
     * @return true if IDs should be normalized
     */
    public boolean getNormalizeIds() {
        return normalizeIds;
    }

    /**
     * Controls whether to normalize literal and entity names. By default these
     * are not normalized.
     * <p>
     * This parameter may be changed at any time. It becomes immediately
     * effective and affects all subsequently received <i>start-entity</i> and
     * <i>literal</i> events.
     *
     * @param normalizeKeys if true literal and entity names are normalized,
     * otherwise not.
     */
    public void setNormalizeKeys(final boolean normalizeKeys) {
        this.normalizeKeys = normalizeKeys;
    }

    /**
     * Checks whether keys should be normalized.
     *
     * @return true if the keys should be normalized
     */
    public boolean getNormalizeKeys() {
        return normalizeKeys;
    }

    /**
     * Controls whether to normalise literal values. By default these are
     * normalized.
     * <p>
     * This parameter may be changed at any time. It becomes immediately
     * effective and affects all subsequently received <i>literal</i> events.
     *
     * @param normalizeValues if true literal values are normalized, otherwise
     * not.
     */
    public void setNormalizeValues(final boolean normalizeValues) {
        this.normalizeValues = normalizeValues;
    }

    /**
     * Checks whether values should be normalized.
     *
     * @return true if values should be normalized
     */
    public boolean getNormalizeValues() {
        return normalizeValues;
    }

    /**
     * Sets the normalisation form used for normalising identifiers, names and
     * values.
     * <p>
     * The default value is NFC.
     * <p>
     * This parameter may be set at any time during processing. It becomes
     * immediately effective and affects all subsequently received events.
     *
     * @param normalizationForm the normalisation form to use.
     *
     */
    public void setNormalizationForm(
            final Normalizer.Form normalizationForm) {
        this.normalizationForm = normalizationForm;
    }

    /**
     * Gets the normalization form.
     *
     * @return the {@link java.text.Normalizer.Form}
     */
    public Normalizer.Form getNormalizationForm() {
        return normalizationForm;
    }

    @Override
    public void startRecord(final String identifier) {
        final String normalizedIdentifier =
                normalizeIds ? normalize(identifier) : identifier;

        getReceiver().startRecord(normalizedIdentifier);
    }

    @Override
    public void endRecord() {
        getReceiver().endRecord();
    }

    @Override
    public void startEntity(final String name) {
        final String normalizedName =
                normalizeKeys ? normalize(name) : name;

        getReceiver().startEntity(normalizedName);
    }

    @Override
    public void endEntity() {
        getReceiver().endEntity();
    }

    @Override
    public void literal(final String name, final String value) {
        final String normalizedName = normalizeKeys ? normalize(name) : name;
        final String normalizedValue = normalizeValues ? normalize(value) : value;

        getReceiver().literal(normalizedName, normalizedValue);
    }

    private String normalize(final String string) {
        return string == null ? null : Normalizer.normalize(string, normalizationForm);
    }

}
