package com.nanosai.gridops.node;

import com.nanosai.gridops.iap.IapMessageFields;
import com.nanosai.gridops.iap.IapMessageFieldsReader;
import com.nanosai.gridops.iap.IapMessageFieldsWriter;
import com.nanosai.gridops.ion.read.IonReader;
import com.nanosai.gridops.ion.write.IonWriter;
import com.nanosai.gridops.tcp.TcpSocketsPort;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jjenkov on 24-09-2016.
 */
public class NodeContainerTest {


    @Test
    public void testFindNode() {

        NodeReactor systemHandler0 = new NodeReactor(new byte[]{0}) {
            @Override
            public void react(IonReader reader, IapMessageFields message, TcpSocketsPort tcpSocketsPort) {
            }
        };

        NodeReactor systemHandler1 = new NodeReactor(new byte[]{1}) {
            @Override
            public void react(IonReader reader, IapMessageFields message, TcpSocketsPort tcpSocketsPort) {
            }
        };

        NodeContainer systemContainer = new NodeContainer(
                systemHandler0, systemHandler1);

        assertSame(systemHandler0, systemContainer.findNodeReactor(new byte[]{0}, 0, 1));
        assertSame(systemHandler1, systemContainer.findNodeReactor(new byte[]{1}, 0, 1));

        assertNull(systemContainer.findNodeReactor(new byte[]{2}, 0, 1));
    }


    @Test
    public void testReact() {
        byte[] systemId = new byte[]{0};
        NodeReactorMock system0 = new NodeReactorMock(systemId);
        assertFalse(system0.handleMessageCalled);

        TcpSocketsPort tcpSocketsPort = null;

        NodeContainer systemContainer = new NodeContainer(system0);

        byte[] dest = new byte[1024];

        int length = writeMessage(systemId, dest);
        IonReader reader = new IonReader();
        reader.setSource(dest, 0, length);
        reader.nextParse();

        IapMessageFields message = new IapMessageFields();
        message.data = dest;
        IapMessageFieldsReader.read(reader, message);

        systemContainer.react(reader, message, tcpSocketsPort);
        assertTrue(system0.handleMessageCalled);

        system0.handleMessageCalled = false;
        byte[] unknownNodeId = new byte[]{123};
        writeMessage(unknownNodeId, dest);

        systemContainer.react(reader, message, tcpSocketsPort);
        assertFalse(system0.handleMessageCalled);

    }

    private int writeMessage(byte[] systemId, byte[] dest) {
        IonWriter writer = new IonWriter();
        writer.setDestination(dest, 0);
        writer.setComplexFieldStack(new int[16]);
        //writer.writeObjectBeginPush(2);

        IapMessageFieldsWriter.writeReceiverNodeId(writer, systemId);

        //writer.writeObjectEndPop();
        return writer.destIndex;
    }

}
