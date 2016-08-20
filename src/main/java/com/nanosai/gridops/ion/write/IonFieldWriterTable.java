package com.nanosai.gridops.ion.write;

import com.nanosai.gridops.ion.IonFieldTypes;
import com.nanosai.gridops.ion.IonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Created by jjenkov on 04-11-2015.
 */
public class IonFieldWriterTable implements IIonFieldWriter {

    protected Field  field    = null;
    protected byte[] keyField = null;


    protected byte[] allKeyFieldBytes = null;
    protected IIonFieldWriter[] fieldWritersForArrayType = null;


    public IonFieldWriterTable(Field field) {
        this.field = field;
        this.keyField = IonUtil.preGenerateKeyField(field);

        Class typeInTable = field.getType().getComponentType();

        Field [] typeInTableFields = typeInTable.getDeclaredFields();
        fieldWritersForArrayType = new IIonFieldWriter[typeInTableFields.length];

        byte[][] fieldNames = new byte[typeInTableFields.length][];
        int totalKeyFieldsLength = 0;
        for(int i=0; i < typeInTableFields.length; i++){
            try {
                fieldNames[i] = typeInTableFields[i].getName().getBytes("UTF-8");

                totalKeyFieldsLength += fieldNames[i].length;

                if(fieldNames[i].length <= 15) {
                    totalKeyFieldsLength += 1; // 1 lead byte for a compact key field
                } else {
                    // 1 lead byte + length bytes
                    totalKeyFieldsLength += 1 + IonUtil.lengthOfInt64Value(fieldNames[i].length);
                }

                fieldWritersForArrayType[i] = IonUtil.createFieldWriter(typeInTableFields[i]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        allKeyFieldBytes = new byte[totalKeyFieldsLength];

        int offset = 0;
        for(int i=0; i<fieldNames.length; i++){
            if(fieldNames[i].length <= 15){
                allKeyFieldBytes[offset++] = (byte) (255 & ((fieldNames[i].length << 4) | IonFieldTypes.KEY_COMPACT));
            } else {
                int lengthLength = IonUtil.lengthOfInt64Value(fieldNames[i].length);
                allKeyFieldBytes[offset++] = (byte) (255 & ((lengthLength << 4 | IonFieldTypes.KEY)));

                for(int j=(lengthLength-1)*8; i >= 0; i-=8){
                    allKeyFieldBytes[offset++] = (byte) (255 & (lengthLength >> i));
                }
            }
            System.arraycopy(fieldNames[i], 0, allKeyFieldBytes, offset, fieldNames[i].length);
            offset += fieldNames[i].length;
        }
    }

    @Override
    public int writeKeyAndValueFields(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        System.arraycopy(this.keyField, 0, destination, destinationOffset, this.keyField.length);
        destinationOffset += this.keyField.length;

        int valueLength = writeValueField(sourceObject, destination, destinationOffset, maxLengthLength);


        return this.keyField.length + valueLength;
    }


    @Override
    public int writeValueField(Object sourceObject, byte[] destination, int destinationOffset, int maxLengthLength) {
        int startIndex = destinationOffset;
        destination[destinationOffset] = (byte) (255 & ((maxLengthLength << 4) | IonFieldTypes.TABLE));
        destinationOffset += 1 + maxLengthLength ; // 1 for lead byte + make space for maxLengthLength length bytes.


        System.arraycopy(this.allKeyFieldBytes, 0, destination, destinationOffset, this.allKeyFieldBytes.length);
        destinationOffset += this.allKeyFieldBytes.length;

        try {
            Object array = (Object) field.get(sourceObject);

            int arrayLength = Array.getLength(array);
            for(int i=0; i<arrayLength; i++){
                Object source = Array.get(array, i);

                //for each field in source write its field value out.
                for(int j=0; j < this.fieldWritersForArrayType.length; j++){
                    destinationOffset += this.fieldWritersForArrayType[j].writeValueField(source, destination, destinationOffset, maxLengthLength);
                }
            }

            int valueLength = destinationOffset - startIndex;

            IonUtil.writeLength(valueLength - 1 - maxLengthLength, maxLengthLength, destination, startIndex + 1);

            return valueLength;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
