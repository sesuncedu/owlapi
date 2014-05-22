package org.semanticweb.owlapi.errors;

import java.util.Collection;
import java.util.WeakHashMap;

import static org.semanticweb.owlapi.errors.ValueBlock.Chaining.*;

/**
 * Infrastructure for fast single-inheritance applicable-handler matching.
 * <p/>
 * Idea 1:
 * Use Hash indexed linked lists -
 * value of map to block of values for specific class.
 * block contains pointer to superclass.
 * If no entry is found for a super class, create an empty block for it, and attempt to
 * link it to a block for its superclass (repeat until mostGeneralClass is reached).
 * On fetch, if no entry found, create empty blocks until a matching superclass is found.
 * values should be returned, most specific match first, with per-type handlers in reverse order of addition.
 * depending on how often entries will be deleted, can use arraylist/arrays, or links (or linked blocks).
 */
public class SuperclassAwareOrderedMultimap<V> {
    private final WeakHashMap<Class, ValueBlock<V>> map = new WeakHashMap<Class, ValueBlock<V>>();
    private Class<?> mostGeneralClass;

    public SuperclassAwareOrderedMultimap() {
        this(Object.class);
    }

    public SuperclassAwareOrderedMultimap(Class mostGeneralClass) {
        this.mostGeneralClass = mostGeneralClass;
    }

    public void put(Class clazz, V value) {
        ValueBlock<V> block = map.get(clazz);
        if (block == null) {
            block = new ValueBlock<V>();
            map.put(clazz, block);
        }
        block.push(value);
    }

    public Collection<V> get(Class key) {
        ValueBlock<V> block = getOrCreateBlockForKey(key);
        return block.asCollection(FOLLOW_POINTERS);
    }

    private ValueBlock<V> getOrCreateBlockForKey(Class key) {
        synchronized (map) {
            ValueBlock<V> block = map.get(key);
            if (block == null) {
                block = new ValueBlock<V>();
                if(key != mostGeneralClass) {
                    block.next = getOrCreateBlockForKey(key.getSuperclass());
                }
                map.put(key,block);
            }
            return block;
        }
    }
}
