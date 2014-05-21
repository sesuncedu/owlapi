package org.semanticweb.owlapi.errors;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class ValueBlockTest {

    @Test
    public void testIsEmpty() throws Exception {
        ValueBlock<String> block = new ValueBlock<String>(4);
        assertTrue("should be empty", block.isEmpty());
        block.push("a");
        assertFalse("should not be empty", block.isEmpty());
        block.pop();
        assertTrue("should be empty again", block.isEmpty());
    }

    @Test
    public void testSize() throws Exception {
        ValueBlock<String> block = new ValueBlock<String>(4);
        block.push("a");
        assertEquals("size", 1, block.size());
        block.push("b");
        assertEquals("size", 2, block.size());
        block.push("c");
        assertEquals("size", 3, block.size());
        block.pop();
        assertEquals("size", 2, block.size());

    }

    @Test
    public void testPushWithoutGrowth() throws Exception {
        ValueBlock<String> block = new ValueBlock<String>(4);
        block.push("a");
        assertEquals("size", 1, block.size());
        block.push("b");
        assertEquals("size", 2, block.size());
        block.push("c");
        assertEquals("size", 3, block.size());
        block.push("d");
        assertEquals("size", 4, block.size());
        assertEquals(block.vp,0);
        assertEquals("d",block.values[0]);
        assertEquals("c",block.values[1]);
        assertEquals("b",block.values[2]);
        assertEquals("a",block.values[3]);

    }
    @Test
    public void testPushWithGrowth() throws Exception {
        ValueBlock<String> block = new ValueBlock<String>(1);
        block.push("a");
        assertEquals("size", 1, block.size());
        block.push("b");
        assertEquals("size", 2, block.size());
        block.push("c");
        assertEquals("size", 3, block.size());
        block.push("d");
        assertEquals("size", 4, block.size());
        int cp = block.vp;

        assertEquals("d",block.values[cp++]);
        assertEquals("c",block.values[cp++]);
        assertEquals("b",block.values[cp++]);
        assertEquals("a",block.values[cp++]);
    }

    @Test
    public void testIterator() throws Exception {
        ValueBlock<String> block = new ValueBlock<String>(1);
        Iterator<String> it = block.iterator();
        assertFalse(it.hasNext());
        block.push("a");
        block.push("b");
        block.push("c");

        it = block.iterator();
        assertTrue(it.hasNext());
        assertEquals(it.next(),"c");
        assertEquals(it.next(),"b");
        assertEquals(it.next(),"a");
        assertFalse(it.hasNext());

        it = block.iterator();
        assertTrue(it.hasNext());
        assertEquals(it.next(),"c");
        assertEquals(it.next(),"b");
        assertEquals(it.next(),"a");
        assertFalse(it.hasNext());

    }
}