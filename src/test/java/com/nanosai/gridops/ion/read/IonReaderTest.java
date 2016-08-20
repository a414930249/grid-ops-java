package com.nanosai.gridops.ion.read;

import com.nanosai.gridops.ion.IonFieldTypes;
import com.nanosai.gridops.ion.write.IonWriter;
import org.junit.Test;

import static org.junit.Assert.*;


/**
    This class tests the IapNavigator, but uses the IapGenerator in the tests, so IapGenerator is also tested implicitly.
 */
public class IonReaderTest {

    IonReader reader = new IonReader();

    @Test
    public void testReadBytes() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeBytes(source, index, new byte[]{1, 2, 3, 4, 5});
        index += IonWriter.writeBytes(source, index, null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.BYTES, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readBytes(dest);
        assertEquals(5, length);
        assertEquals(1, dest[0]);
        assertEquals(2, dest[1]);
        assertEquals(3, dest[2]);
        assertEquals(4, dest[3]);
        assertEquals(5, dest[4]);

        length = reader.readBytes(dest, 0, 3);
        assertEquals(3, length);
        assertEquals(2, dest[0]);
        assertEquals(3, dest[1]);
        assertEquals(4, dest[2]);

        reader.next();
        reader.parse();
        length = reader.readBytes(dest, 0, 3);
        assertEquals(0, length);
    }


    @Test
    public void testReadBoolean() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeBoolean(source, index, true);
        index += IonWriter.writeBoolean(source, index, false);
        index += IonWriter.writeBooleanObj(source, index, null);

        reader.parse();
        assertEquals(IonFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertTrue(reader.readBoolean());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertFalse(reader.readBoolean());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.BOOLEAN, reader.fieldType);
        assertEquals(0, reader.fieldLength);
        assertNull(reader.readBooleanObj());
    }


    @Test
    public void testReadInt64(){
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeInt64(source, index,  65535);
        index += IonWriter.writeInt64(source, index, -65535);
        index += IonWriter.writeInt64Obj(source, index, null);

        reader.parse();
        assertEquals(65535, reader.readInt64());

        reader.next();
        reader.parse();
        assertEquals(-65535, reader.readInt64());

        reader.next();
        reader.parse();
        assertEquals(0   , reader.readInt64());
        assertEquals(null, reader.readInt64Obj());
    }


    @Test
    public void testReadFloat32() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeFloat32(source, index, 123.45F);
        index += IonWriter.writeFloat32Obj(source, index, null);

        reader.parse();
        assertEquals(123.45F, reader.readFloat32(), 0);

        reader.next();
        reader.parse();
        assertEquals(0, reader.readFloat32(),0);
        assertNull(reader.readFloat32Obj());

    }


    @Test
    public void testReadFloat64() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        reader.setSource(source, 0, source.length);

        index += IonWriter.writeFloat64(source, index, 123456.123456D);
        index += IonWriter.writeFloat64Obj(source, index, null);

        reader.parse();
        assertEquals(123456.123456D, reader.readFloat64(), 0);

        reader.next();
        reader.parse();
        assertEquals(0, reader.readFloat64(),0);
        assertNull(reader.readFloat64Obj());

    }

    @Test
    public void testReadUtf8() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeUtf8(source, index, "Hello");
        index += IonWriter.writeUtf8(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.UTF_8, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readUtf8(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readUtf8(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readUtf8String());

        reader.next();
        reader.parse();
        length = reader.readUtf8(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readUtf8String());
    }

    @Test
    public void testReadKey() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeKey(source, index, "Hello");
        index += IonWriter.writeKey(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readKey(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readKey(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readKeyAsUtf8String());

        reader.next();
        reader.parse();
        length = reader.readKey(dest, 0, 3);
        assertEquals(0, length);
        assertNull  (reader.readKeyAsUtf8String());

    }


    @Test
    public void testReadKeyCompact() {
        byte[] source = new byte[10 * 1024];
        byte[] dest   = new byte[10 * 1024];

        int index = 0;
        index += IonWriter.writeKeyCompact(source, index, "Hello");
        index += IonWriter.writeKeyCompact(source, index, (String) null);

        reader.setSource(source, 0, source.length);
        reader.parse();

        assertEquals(IonFieldTypes.KEY_COMPACT, reader.fieldType);
        assertEquals(5, reader.fieldLength);

        int length = reader.readKeyCompact(dest);
        assertEquals(5, length);
        assertEquals('H', dest[0]);
        assertEquals('e', dest[1]);
        assertEquals('l', dest[2]);
        assertEquals('l', dest[3]);
        assertEquals('o', dest[4]);

        length = reader.readKeyCompact(dest, 1, 3);
        assertEquals(3, length);
        assertEquals('H', dest[1]);
        assertEquals('e', dest[2]);
        assertEquals('l', dest[3]);

        assertEquals("Hello", reader.readKeyCompactAsUtf8String());

        reader.next();
        reader.parse();
        length = reader.readKeyCompact(dest, 0, 3);
        assertEquals(0, length);
        assertNull(reader.readKeyCompactAsUtf8String());

    }


    @Test
    public void testParseIntoAndOutOf() {
        byte[] source = new byte[10 * 1024];

        int index = 0;
        int object1StartIndex = index;
        index += IonWriter.writeObjectBegin(source, index, 2);
        index += IonWriter.writeKey (source, index, "field1");
        index += IonWriter.writeUtf8(source, index, "value1");
        index += IonWriter.writeKey (source, index, "field2");
        index += IonWriter.writeInt64 (source, index, 1234);
        IonWriter.writeObjectEnd(source, object1StartIndex, 2, index - object1StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte

        int object2StartIndex = index;
        index += IonWriter.writeObjectBegin(source, index, 2);
        index += IonWriter.writeKey (source, index, "field1");
        index += IonWriter.writeUtf8(source, index, "value1");
        index += IonWriter.writeKey (source, index, "field2");
        index += IonWriter.writeInt64(source, index, 1234);
        IonWriter.writeObjectEnd(source, object2StartIndex, 2, index - object2StartIndex -2 -1); //-2 = lengthLength, -1 = leadbyte


        // First check that outer level navigation works - skipping over the fields of the objects.
        reader.setSource(source, 0, index);
        assertTrue(reader.hasNext());

        reader.parse();
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertFalse(reader.hasNext());
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);

        reader.next();
        assertFalse(reader.hasNext());
        assertEquals(index, reader.nextIndex);


        // Second check that parsing into the objects also works
        reader.setSource(source, 0, index);
        reader.parse();

        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.parseInto();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.UTF_8, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.parseOutOf();
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertEquals(object1StartIndex + 1 + 2, reader.index); // +1 for lead byte, +2 for lengthLength
        assertEquals(object2StartIndex        , reader.nextIndex);
        assertTrue(reader.hasNext());

        reader.next();
        assertEquals(object2StartIndex        , reader.index);

        reader.parse();
        assertEquals(IonFieldTypes.OBJECT, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.parseInto();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.UTF_8, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.KEY, reader.fieldType);
        assertTrue(reader.hasNext());

        reader.next();
        reader.parse();
        assertEquals(IonFieldTypes.INT_POS, reader.fieldType);
        assertFalse(reader.hasNext());

        reader.parseOutOf();
        assertFalse(reader.hasNext());

    }



}
