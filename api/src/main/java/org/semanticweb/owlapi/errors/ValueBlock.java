package org.semanticweb.owlapi.errors;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


class ValueBlock<V extends Object> implements Iterable<V> {
    ValueBlock<V> next;
    Object values[];
    int vp;

    ValueBlock() {
        this(4);
    }
    ValueBlock(int size) {
        this.next = null;
        values = new Object[size];
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
    void cons(ValueBlock<V> other) {
        this.next = other;
    }
    /**
     * Returns an iterator over elements of type {@code T} for just this block.
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

    public Collection<V> asCollection(Chaining chaining) {
        switch(chaining) {

            case FOLLOW_POINTERS:
                return new ChainedBlockBackedCollection();
            case DONT_FOLLOW_POINTERS:
                return new SingleBlockCollection();
        }
      return new ChainedBlockBackedCollection();
    }
    public enum Chaining {
        FOLLOW_POINTERS,DONT_FOLLOW_POINTERS
    }
    private class SingleBlockCollection extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return ValueBlock.this.iterator();
        }

        @Override
        public int size() {
            return ValueBlock.this.size();
        }
    }

    private class ChainedBlockBackedCollection extends AbstractCollection<V> {


        @Override
        public Iterator<V> iterator() {

            return new ChainedBlockBackedCollectionIterator<V>(ValueBlock.this);
        }

        @Override
        public int size() {
            int total=0;
            for(ValueBlock<V> tmp = ValueBlock.this;tmp != null; tmp = tmp.next) {
                total += tmp.size();
            }
            return total;
        }

        private class ChainedBlockBackedCollectionIterator<V extends Object> implements Iterator<V> {
            ValueBlock<V> block;
            int cp;

            private ChainedBlockBackedCollectionIterator(ValueBlock<V> block) {
                setBlock(block);
            }

            private void setBlock(ValueBlock<V> block) {
                this.block = block;
                this.cp = block.vp;
            }

            @Override
            public boolean hasNext() {
                while(block != null && cp >= block.values.length) {
                    block = block.next;
                    if(block != null) {
                        cp = block.vp;
                    }
                }
                return block != null && cp < block.values.length;
            }

            @Override
            @SuppressWarnings("unchecked")
            public V next() {
                if(!hasNext()) {
                    throw new NoSuchElementException("No more piggies");
                }
                return (V) block.values[cp++];
            }
        }
    }
}
