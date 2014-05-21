package org.semanticweb.owlapi.errors;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

class ValueBlock<V extends Object> implements Iterable<V> {
    ValueBlock<V> prev;
    ValueBlock<V> next;
    Object values[];
    int vp;

    ValueBlock(int size) {
        values = (V[]) new Object[size];
        vp = size;
    }
    boolean isEmpty() {
        return vp == values.length;
    }
    int size() {
        return values.length - vp;
    }
    void growBlock() {
        int newSize = values.length * 2;
        Object[] newValues =  new Object[newSize];
        int destPos = newSize - values.length;
        System.arraycopy(values,0,newValues, destPos,values.length);
        values = newValues;
        vp = destPos;
    }
    void push(V value) {
        if(vp == 0) {
            growBlock();
        }
        values[--vp] = value;
    }
    @SuppressWarnings("unchecked")
    V pop() {
       if(isEmpty()) {
           throw new NoSuchElementException("pop called on empty value block");
       } else {

           V result = (V) values[vp];
           values[vp++] = null;
           return result;
       }
    }
    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            int cp = vp;
            @Override
            public boolean hasNext() {
                return cp <values.length;
            }
            @Override
            @SuppressWarnings("unchecked")
            public V next() {
                if(!hasNext()) {
                    throw new NoSuchElementException("next element called on empty ValueBlock iterator");
                }
                return (V) values[cp++];
            }
        };
    }
}
