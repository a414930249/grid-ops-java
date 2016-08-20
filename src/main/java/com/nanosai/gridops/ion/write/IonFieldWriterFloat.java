package com.nanosai.gridops.ion.write;

import com.nanosai.gridops.ion.IonFieldTypes;
import com.nanosai.gridops.ion.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterFloat implements IIonFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;

    public IonFieldWriterFloat(Field field) {
        this.field = field;
        this.keyField = IonUtil.preGenerateKeyField(field);
    }

    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {

        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        return this.keyField.length + writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);

    }

    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            float value = (float) field.get(sourceObject);
            int valueIntBits = Float.floatToIntBits(value);

            //magic number "4" is the length in bytes of a 32 bit floating point number in ION.

            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | 4));

            for(int i=(4-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (valueIntBits >> i));
            }

            return 5;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
