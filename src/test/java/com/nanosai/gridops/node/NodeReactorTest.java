package com.nanosai.gridops.node;

import com.nanosai.gridops.iap.IapMessage;
import com.nanosai.gridops.iap.IapMessageReader;
import com.nanosai.gridops.iap.IapMessageWriter;
import com.nanosai.gridops.ion.read.IonReader;
import com.nanosai.gridops.ion.write.IonWriter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jjenkov on 24-09-2016.
 */
public class NodeReactorTest {


    @Test
    public void testFindProtocolHandler() {
        ProtocolReactor protocolReactor0 = new ProtocolReactor(new byte[]{0}) {
            @Override
            public void react(IonReader reader, IapMessage message) {
            }
        };

        ProtocolReactor protocolReactor1 = new ProtocolReactor(new byte[]{1}) {
            @Override
            public void react(IonReader reader, IapMessage message) {
            }
        };

        NodeReactor systemHandler = new NodeReactor(new byte[]{0}, protocolReactor0, protocolReactor1);

        assertSame(protocolReactor0, systemHandler.findProtocolReactor(new byte[]{0}, 0, 1));
        assertSame(protocolReactor1, systemHandler.findProtocolReactor(new byte[]{1}, 0, 1));

        assertNull(systemHandler.findProtocolReactor(new byte[]{2}, 0, 1));
    }

    @Test
    public void testReact(){
        ProtocolReactorMock protocolHandlerMock = new ProtocolReactorMock(new byte[]{2});
        assertFalse(protocolHandlerMock.handleMessageCalled);

        NodeReactor systemHandler = new NodeReactor(new byte[]{0}, protocolHandlerMock);

        byte[] dest = new byte[128];

        int length = writeMessage(new byte[]{2}, dest);

        IonReader reader = new IonReader();
        reader.setSource(dest, 0, length);
        reader.nextParse();

        IapMessage message = new IapMessage();
        message.data = dest;
        IapMessageReader.read(reader, message);

        systemHandler.react(reader, message);
        assertTrue(protocolHandlerMock.handleMessageCalled);

        length = writeMessage(new byte[]{123}, dest);
        reader.setSource(dest, 0, length);
        reader.nextParse();
        protocolHandlerMock.handleMessageCalled = false;

        systemHandler.react(reader, message);
        assertFalse(protocolHandlerMock.handleMessageCalled);
    }


    private int writeMessage(byte[] semanticProtocolId, byte[] dest) {
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 0);
        writer.setComplexFieldStack(new int[16]);
        //writer.writeObjectBeginPush(2);

        IapMessageWriter.writeSemanticProtocolId(writer, semanticProtocolId);

        //writer.writeKeyShort(new byte[]{IapMessageKeys.SEMANTIC_PROTOCOL_ID});
        //writer.writeBytes(new byte[]{semanticProtocolId});

        //writer.writeObjectEndPop();

        return writer.destIndex;
    }

}