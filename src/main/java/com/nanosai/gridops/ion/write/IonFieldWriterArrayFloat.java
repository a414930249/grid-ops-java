package com.nanosai.gridops.ion.write;

import com.nanosai.gridops.ion.IonFieldTypes;
import com.nanosai.gridops.ion.IonUtil;

import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterArrayFloat extends IonFieldWriterBase implements IIonFieldWriter {

    private static int MAX_ELEMENT_FIELD_LENGTH = 9;    //an ION long field can max be 9 bytes long

    public IonFieldWriterArrayFloat(Field field, String alias) {
        super(field, alias);
    }

    @Override
    public int writeValueField(Object sourceObject, byte[] dest, int destOffset, int maxLengthLength) {
        try {
            float[] value = (float[]) field.get(sourceObject);

            if(value == null) {
                dest[destOffset++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4))); //byte array which is null
                return 1;
            }

            int elementCount = value.length;
            int elementCountLengthLength = IonUtil.lengthOfInt64Value(elementCount);
            int maxPossibleArrayFieldLength =
                         1 + elementCountLengthLength +           // +1 for lead byte of element count field (Int64-Pos)
                    (elementCount * MAX_ELEMENT_FIELD_LENGTH);

            int arrayLengthLength = IonUtil.lengthOfInt64Value(maxPossibleArrayFieldLength);

            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.ARRAY << 4) | arrayLengthLength) );
            int lengthStartOffset = destOffset;

            //allocate arrayLengthLength bytes for later - when we know the real length (in bytes) of this ION array field
            destOffset += arrayLengthLength;

            //write element count
            dest[destOffset++] = (byte) (255 & ((IonFieldTypes.INT_POS << 4) | elementCountLengthLength));

            for(int i=(elementCountLengthLength-1)*8; i >= 0; i-=8){
                dest[destOffset++] = (byte) (255 & (elementCount >> i));
            }

            //write elements
            for(int i=0; i < elementCount; i++){
                int elementVal = Float.floatToIntBits(value[i]);

                final int length = 4;

                dest[destOffset++] = (byte) (255 & ((IonFieldTypes.FLOAT << 4) | length));

                for(int j=(length-1)*8; j >= 0; j-=8){
                    dest[destOffset++] = (byte) (255 & (elementVal >> j));
                }
            }

            //write total length of array
            int arrayByteLength = destOffset - (lengthStartOffset + arrayLengthLength);
            for(int i=(arrayLengthLength-1)*8; i >= 0; i-=8){
                dest[lengthStartOffset++] = (byte) (255 & (arrayByteLength >> i));
            }

            return 1 + arrayLengthLength + arrayByteLength; //total length of a UTF-8 field

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
