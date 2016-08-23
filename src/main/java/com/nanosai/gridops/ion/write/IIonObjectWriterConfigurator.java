package com.nanosai.gridops.ion.write;

/**
 * An IIonObjectWriterConfigurator can configure an IonObjectWriter. An implementation of this interface is
 * passed to the IonObjectWriter's constructor. The IonObjectWriter then calls the IIonObjectWriterConfigurator
 * for each field in the target class. The IIonObjectWriterConfigurator instance can then set a few configuration
 * options for the given field (exclude it from serialization, or use another field fieldName (alias))
 */
public interface IIonObjectWriterConfigurator {

    public void configure(IonFieldWriterConfiguration config);

}
