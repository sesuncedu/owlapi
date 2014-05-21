package org.semanticweb.owlapi.errors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Created by ses on 5/17/14.
 */
public class SuperclassAwareOrderedMultimap<V> implements Multimap<Class, V> {
    private Multimap<Class, V> delegate;
    private Class<?> mostGeneralClass;

    public SuperclassAwareOrderedMultimap(Class mostGeneralClass) {
        this.mostGeneralClass = mostGeneralClass;
        this.delegate = ArrayListMultimap.create();
    }

    /**
     * Returns the number of key-value pairs in this multimap.
     * <p/>
     * <p><b>Note:</b> this method does not return the number of <i>distinct
     * keys</i> in the multimap, which is given by {@code keySet().size()} or
     * {@code asMap().size()}. See the opening section of the {@link com.google.common.collect.Multimap}
     * class documentation for clarification.
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * Returns {@code true} if this multimap contains no key-value pairs.
     * Equivalent to {@code size() == 0}, but can in some cases be more efficient.
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the key {@code key}.
     *
     * @param key
     */
    @Override
    public boolean containsKey(@Nullable Object key) {

        if (key != null && key instanceof Class<?>) {
            Class<?> keyAsClass = (Class<?>) key;
            if (mostGeneralClass.isAssignableFrom(keyAsClass)) {
                return delegate.containsKey(key);
            }
        }
        return false;
    }

    /**
     * TODO:  AS FAR AS I GETS
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the value {@code value}.
     *
     * @param value
     */
    @Override
    public boolean containsValue(@Nullable Object value) {
        return delegate.containsValue(value);
    }

    /**
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the key {@code key} and the value {@code value}.
     *
     * @param key
     * @param value
     */
    @Override
    public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
        return delegate.containsEntry(key, value);
    }

    /**
     * Stores a key-value pair in this multimap.
     * <p/>
     * <p>Some multimap implementations allow duplicate key-value pairs, in which
     * case {@code put} always adds a new key-value pair and increases the
     * multimap size by 1. Other implementations prohibit duplicates, and storing
     * a key-value pair that's already in the multimap has no effect.
     *
     * @param key
     * @param value
     * @return {@code true} if the method increased the size of the multimap, or
     * {@code false} if the multimap already contained the key-value pair and
     * doesn't allow duplicates
     */
    public boolean put(@Nullable Class key, @Nullable V value) {
        return delegate.put(key, value);
    }

    /**
     * Removes a single key-value pair with the key {@code key} and the value
     * {@code value} from this multimap, if such exists. If multiple key-value
     * pairs in the multimap fit this description, which one is removed is
     * unspecified.
     *
     * @param key
     * @param value
     * @return {@code true} if the multimap changed
     */
    @Override
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        return delegate.remove(key, value);
    }

    /**
     * Stores a key-value pair in this multimap for each of {@code values}, all
     * using the same key, {@code key}. Equivalent to (but expected to be more
     * efficient than): <pre>   {@code
     * <p/>
     *   for (V value : values) {
     *     put(key, value);
     *   }}</pre>
     * <p/>
     * <p>In particular, this is a no-op if {@code values} is empty.
     *
     * @param key
     * @param values
     * @return {@code true} if the multimap changed
     */
    public boolean putAll(@Nullable Class key, Iterable<? extends V> values) {
        return delegate.putAll(key, values);
    }

    /**
     * Stores all key-value pairs of {@code multimap} in this multimap, in the
     * order returned by {@code multimap.entries()}.
     *
     * @param multimap
     * @return {@code true} if the multimap changed
     */
    public boolean putAll(Multimap<? extends Class, ? extends V> multimap) {
        return delegate.putAll(multimap);
    }

    /**
     * Stores a collection of values with the same key, replacing any existing
     * values for that key.
     * <p/>
     * <p>If {@code values} is empty, this is equivalent to
     * {@link #removeAll(Object) removeAll(key)}.
     *
     * @param key
     * @param values
     * @return the collection of replaced values, or an empty collection if no
     * values were previously associated with the key. The collection
     * <i>may</i> be modifiable, but updating it will have no effect on the
     * multimap.
     */
    public Collection<V> replaceValues(@Nullable Class key, Iterable<? extends V> values) {
        return delegate.replaceValues(key, values);
    }

    /**
     * Removes all values associated with the key {@code key}.
     * <p/>
     * <p>Once this method returns, {@code key} will not be mapped to any values,
     * so it will not appear in {@link #keySet()}, {@link #asMap()}, or any other
     * views.
     *
     * @param key
     * @return the values that were removed (possibly empty). The returned
     * collection <i>may</i> be modifiable, but updating it will have no
     * effect on the multimap.
     */
    @Override
    public Collection<V> removeAll(@Nullable Object key) {
        return delegate.removeAll(key);
    }

    /**
     * Removes all key-value pairs from the multimap, leaving it {@linkplain
     * #isEmpty empty}.
     */
    @Override
    public void clear() {
        delegate.clear();
    }

    /**
     * Returns a view collection of the values associated with {@code key} in this
     * multimap, if any. Note that when {@code containsKey(key)} is false, this
     * returns an empty collection, not {@code null}.
     * <p/>
     * <p>Changes to the returned collection will update the underlying multimap,
     * and vice versa.
     *
     * @param key
     */
    @Override
    public Collection<V> get(@Nullable Class key) {
        return delegate.get(key);
    }

    /**
     * Returns a view collection of all <i>distinct</i> keys contained in this
     * multimap. Note that the key set contains a key if and only if this multimap
     * maps that key to at least one value.
     * <p/>
     * <p>Changes to the returned set will update the underlying multimap, and
     * vice versa. However, <i>adding</i> to the returned set is not possible.
     */
    @Override
    public Set<Class> keySet() {
        return delegate.keySet();
    }

    /**
     * Returns a view collection containing the key from each key-value pair in
     * this multimap, <i>without</i> collapsing duplicates. This collection has
     * the same size as this multimap, and {@code keys().count(k) ==
     * get(k).size()} for all {@code k}.
     * <p/>
     * <p>Changes to the returned multiset will update the underlying multimap,
     * and vice versa. However, <i>adding</i> to the returned collection is not
     * possible.
     */
    @Override
    public Multiset<Class> keys() {
        return delegate.keys();
    }

    /**
     * Returns a view collection containing the <i>value</i> from each key-value
     * pair contained in this multimap, without collapsing duplicates (so {@code
     * values().size() == size()}).
     * <p/>
     * <p>Changes to the returned collection will update the underlying multimap,
     * and vice versa. However, <i>adding</i> to the returned collection is not
     * possible.
     */
    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    /**
     * Returns a view collection of all key-value pairs contained in this
     * multimap, as {@link Map.Entry} instances.
     * <p/>
     * <p>Changes to the returned collection or the entries it contains will
     * update the underlying multimap, and vice versa. However, <i>adding</i> to
     * the returned collection is not possible.
     */
    @Override
    public Collection<Map.Entry<Class, V>> entries() {
        return delegate.entries();
    }

    /**
     * Returns a view of this multimap as a {@code Map} from each distinct key
     * to the nonempty collection of that key's associated values. Note that
     * {@code this.asMap().get(k)} is equivalent to {@code this.get(k)} only when
     * {@code k} is a key contained in the multimap; otherwise it returns {@code
     * null} as opposed to an empty collection.
     * <p/>
     * <p>Changes to the returned map or the collections that serve as its values
     * will update the underlying multimap, and vice versa. The map does not
     * support {@code put} or {@code putAll}, nor do its entries support {@link
     * java.util.Map.Entry#setValue setValue}.
     */
    @Override
    public Map<Class, Collection<V>> asMap() {
        return delegate.asMap();
    }

    /**
     * Compares the specified object with this multimap for equality. Two
     * multimaps are equal when their map views, as returned by {@link #asMap},
     * are also equal.
     * <p/>
     * <p>In general, two multimaps with identical key-value mappings may or may
     * not be equal, depending on the implementation. For example, two
     * {@link SetMultimap} instances with the same key-value mappings are equal,
     * but equality of two {@link ListMultimap} instances depends on the ordering
     * of the values for each key.
     * <p/>
     * <p>A non-empty {@link SetMultimap} cannot be equal to a non-empty
     * {@link ListMultimap}, since their {@link #asMap} views contain unequal
     * collections as values. However, any two empty multimaps are equal, because
     * they both have empty {@link #asMap} views.
     *
     * @param obj
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return delegate.equals(obj);
    }

    /**
     * Returns the hash code for this multimap.
     * <p/>
     * <p>The hash code of a multimap is defined as the hash code of the map view,
     * as returned by {@link com.google.common.collect.Multimap#asMap}.
     */
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
