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

package org.metafacture.commons.tries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A simple Trie, which accepts a trailing wildcard
 *
 * @param <P> type of value stored
 * @author Markus Michael Geipel
 * @author Pascal Christoph
 */
public final class WildcardTrie<P> {

    public static final char STAR_WILDCARD = '*';
    public static final char Q_WILDCARD = '?';
    public static final String OR_STRING = "|";

    private static final Pattern OR_PATTERN = Pattern.compile(OR_STRING, Pattern.LITERAL);
    private final Node<P> root = new Node<P>();

    private Set<Node<P>> nodes = new HashSet<Node<P>>();
    private Set<Node<P>> nextNodes = new HashSet<Node<P>>();

    /**
     * Creates an instance of {@link WildcardTrie}.
     */
    public WildcardTrie() {
    }

    /**
     * Inserts keys into the trie. Use '|' to concatenate. Use '*' (0,inf) and '?'
     * (1,1) to express wildcards.
     *
     * @param keys  pattern of keys to register
     * @param value value to associate with the key pattern
     */
    public void put(final String keys, final P value) {
        if (keys.contains(OR_STRING)) {
            final String[] keysSplit = OR_PATTERN.split(keys);
            for (final String string : keysSplit) {
                simplyPut(string, value);
            }
        }
        else {
            simplyPut(keys, value);
        }
    }

    private void simplyPut(final String key, final P value) {
        final int length = key.length();

        Node<P> node = root;
        Node<P> next = null;
        for (int i = 0; i < length; ++i) {
            next = node.getNext(key.charAt(i));
            if (next == null) {
                next = node.addNext(key.charAt(i));
            }
            node = next;
        }
        node.addValue(value);
    }

    /**
     * Gets the List of values identified by a key.
     *
     * @param key the key
     * @return the List of
     */
    public List<P> get(final String key) {
        nodes.add(root);
        final int length = key.length();
        for (int i = 0; i < length; ++i) {
            for (final Node<P> node : nodes) {
                Node<P> temp = node.getNext(key.charAt(i));
                if (temp != null) {
                    nextNodes.add(temp);
                    temp = temp.getNext(STAR_WILDCARD);
                    if (temp != null) {
                        nextNodes.add(temp);
                    }
                }
                temp = node.getNext(Q_WILDCARD);
                if (temp != null) {
                    nextNodes.add(temp);
                }

                temp = node.getNext(STAR_WILDCARD);
                if (temp != null) {
                    nextNodes.add(temp);
                    if (temp != node) {
                        temp = temp.getNext(key.charAt(i));
                        if (temp != null) {
                            nextNodes.add(temp);
                        }
                    }
                }
            }
            nodes.clear();
            final Set<Node<P>> temp = nodes;
            nodes = nextNodes;
            nextNodes = temp;
        }

        return matches();
    }

    private List<P> matches() {
        List<P> matches = Collections.emptyList();
        for (final Node<P> node : nodes) {
            final Set<P> values = node.getValues();
            if (!values.isEmpty()) {
                if (matches == Collections.emptyList()) {
                    matches = new ArrayList<P>();
                }
                matches.addAll(values);
            }
        }
        nodes.clear();
        nextNodes.clear();
        return matches;
    }

    /**
     * Node in the trie.
     *
     * @param <T> type of the value associated with this node.
     */
    private final class Node<T> {

        private Set<T> values =  Collections.emptySet();
        private final CharMap<Node<T>> links = new CharMap<Node<T>>();

        protected Node() {
            // nothing to do
        }

        public Node<T> addNext(final char key) {
            final Node<T> next = new Node<T>();
            links.put(key, next);
            if (key == STAR_WILDCARD) {
                next.links.put(STAR_WILDCARD, next);
            }
            return next;
        }

        public void addValue(final T value) {
            if (values == Collections.emptySet()) {
                values = new LinkedHashSet<T>();
            }
            this.values.add(value);
        }

        public Set<T> getValues() {
            return values;
        }

        public Node<T> getNext(final char key) {
            return links.get(key);
        }
    }

}
