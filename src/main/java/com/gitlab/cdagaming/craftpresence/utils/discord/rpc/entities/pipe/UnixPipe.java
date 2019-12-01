/*
 * Copyright 2017 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.pipe;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.junixsocket.AFUNIXSocket;
import com.gitlab.cdagaming.craftpresence.impl.junixsocket.AFUNIXSocketAddress;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.IPCClient;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.Callback;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.Packet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class UnixPipe extends Pipe {
    private final AFUNIXSocket socket;

    UnixPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, String location) throws IOException {
        super(ipcClient, callbacks);

        socket = AFUNIXSocket.newInstance();
        socket.connect(new AFUNIXSocketAddress(new File(location)));
    }

    @Override
    public Packet read() throws IOException, JsonParseException {
        InputStream is = socket.getInputStream();

        // Await byte retrieval
        try {
            while (is.available() == 0 && status == PipeStatus.CONNECTED) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        if (status == PipeStatus.DISCONNECTED)
            throw new IOException("Disconnected!");

        if (status == PipeStatus.CLOSED)
            return new Packet(Packet.OpCode.CLOSE, null);

        // Read the op and length. Both are signed ints
        byte[] d = new byte[is.available()];
        int result = is.read(d, 0, d.length);

        if (ModUtils.IS_DEV) {
            ModUtils.LOG.info(String.format("Read Byte Data: %s with result %s", new String(d), result));
        }
        ByteBuffer bb = ByteBuffer.wrap(d);

        Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(bb.getInt())];
        d = new byte[Integer.reverseBytes(bb.getInt())];

        int opResult = is.read(d);

        if (ModUtils.IS_DEV) {
            ModUtils.LOG.info(String.format("Read Op Byte Data: %s with result %s", new String(d), opResult));
        }

        JsonObject packetData = new JsonObject();
        packetData.addProperty("", new String(d));
        Packet p = new Packet(op, packetData);

        if (ModUtils.IS_DEV) {
            ModUtils.LOG.info(String.format("Received packet: %s", p.toString()));
        }

        if (listener != null)
            listener.onPacketReceived(ipcClient, p);

        // Close Resources
        is.close();
        return p;
    }

    @Override
    public void write(byte[] b) throws IOException {
        socket.getOutputStream().write(b);
    }

    @Override
    public void close() throws IOException {
        if (ModUtils.IS_DEV) {
            ModUtils.LOG.info("Closing IPC pipe...");
        }
        send(Packet.OpCode.CLOSE, new JsonObject(), null);
        status = PipeStatus.CLOSED;
        socket.close();
    }
}