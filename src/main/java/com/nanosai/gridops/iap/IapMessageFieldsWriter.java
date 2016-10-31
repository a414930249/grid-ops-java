package com.nanosai.gridops.iap;

import com.nanosai.gridops.ion.write.IonWriter;

/**
 * Created by jjenkov on 02-10-2016.
 */
public class IapMessageFieldsWriter {

    private static final byte[] receiverNodeIdKey              = new byte[] {IapMessageKeys.RECEIVER_NODE_ID};
    private static final byte[] receiverNodeIdCodeKey          = new byte[] {IapMessageKeys.RECEIVER_NODE_ID_CODE};
    private static final byte[] semanticProtocolIdKey          = new byte[] {IapMessageKeys.SEMANTIC_PROTOCOL_ID};
    private static final byte[] semanticProtocolIdCodeKey      = new byte[] {IapMessageKeys.SEMANTIC_PROTOCOL_ID_CODE};
    private static final byte[] semanticProtocolVersionKey     = new byte[] {IapMessageKeys.SEMANTIC_PROTOCOL_VERSION};
    private static final byte[] semanticProtocolVersionCodeKey = new byte[] {IapMessageKeys.SEMANTIC_PROTOCOL_VERSION_CODE};
    private static final byte[] messageTypeKey                 = new byte[] {IapMessageKeys.MESSAGE_TYPE};
    private static final byte[] messageTypeCodeKey             = new byte[] {IapMessageKeys.MESSAGE_TYPE_CODE};

    public static void writeMessageFields(IonWriter writer, int receiverSystemIdCode,
                                          int semanticProtocolIdCode, int semanticProtocolVersionCode,
                                          int messageTypeCode){
        writeReceiverNodeIdCode(writer, receiverSystemIdCode);
        writeSemanticProtocolIdCode     (writer, semanticProtocolIdCode);
        writeSemanticProtocolVersionCode(writer, semanticProtocolVersionCode);
        writeMessageTypeCode            (writer, messageTypeCode);
    }

    public static void writeMessageFields(IonWriter writer, byte[] receiverSystemId,
                                          byte[] semanticProtocolId, byte[] semanticProtocolVersion,
                                          byte[] messageType){
        writeReceiverNodeId(writer, receiverSystemId);
        writeSemanticProtocolId     (writer, semanticProtocolId);
        writeSemanticProtocolVersion(writer, semanticProtocolVersion);
        writeMessageType            (writer, messageType);
    }

    public static void writeReceiverNodeIdCode(IonWriter writer, int receiverSystemIdCode){
        writer.writeKeyShort(receiverNodeIdCodeKey);
        writer.writeInt64   (receiverSystemIdCode);
    }

    public static void writeReceiverNodeId(IonWriter writer, byte[] receiverSystemId){
        writer.writeKeyShort(receiverNodeIdKey);
        writer.writeBytes   (receiverSystemId);
    }

    public static void writeSemanticProtocolIdCode(IonWriter writer, int semanticProtocolIdCode){
        writer.writeKeyShort(semanticProtocolIdCodeKey);
        writer.writeInt64   (semanticProtocolIdCode);
    }

    public static void writeSemanticProtocolId(IonWriter writer, byte[] semanticProtocolId){
        writer.writeKeyShort(semanticProtocolIdKey);
        writer.writeBytes   (semanticProtocolId);
    }

    public static void writeSemanticProtocolVersionCode(IonWriter writer, int semanticProtocolVersionCode){
        writer.writeKeyShort(semanticProtocolVersionCodeKey);
        writer.writeInt64   (semanticProtocolVersionCode);
    }

    public static void writeSemanticProtocolVersion(IonWriter writer, byte[] semanticProtocolVersion){
        writer.writeKeyShort(semanticProtocolVersionKey);
        writer.writeBytes   (semanticProtocolVersion);
    }

    public static void writeMessageTypeCode(IonWriter writer, int messageType){
        writer.writeKeyShort(messageTypeCodeKey);
        writer.writeInt64   (messageType);
    }

    public static void writeMessageType(IonWriter writer, byte[] messageType){
        writer.writeKeyShort(messageTypeKey);
        writer.writeBytes   (messageType);
    }

}