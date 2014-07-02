/*
 * Copyright 2014 Qualys Inc. and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualys.jserf;

import com.google.common.collect.Maps;
import com.qualys.jserf.extractor.ExtractorManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Tristan Burch
 */
@Slf4j
public class NettySerfClient implements SerfClient {
    private final MessagePack messagePack;
    private final ChannelManger channelManger;

    private final ConcurrentMap<Integer, SerfRequest> requestsBySequence = Maps.newConcurrentMap();
    private final ExtractorManager extractorManager = new ExtractorManager();

    public NettySerfClient(String serfHost, int serfPort) throws InterruptedException {
        this(serfHost, serfPort, new MessagePack());
    }

    public NettySerfClient(String serfHost, int serfPort, MessagePack messagePack) throws InterruptedException {
        this(serfHost, serfPort, messagePack, 1L, 60L);
    }

    public NettySerfClient(String serfHost, int serfPort, long minReconnectRetrySeconds, long maxReconnectRetrySeconds) throws InterruptedException {
        this(serfHost, serfPort, new MessagePack(), minReconnectRetrySeconds, maxReconnectRetrySeconds);
    }

    public NettySerfClient(String serfHost, int serfPort, MessagePack messagePack, long minReconnectRetrySeconds, long maxReconnectRetrySeconds) throws InterruptedException {
        this.messagePack = messagePack;
        this.channelManger = new ChannelManger(serfHost, serfPort, minReconnectRetrySeconds, maxReconnectRetrySeconds, extractorManager, messagePack, requestsBySequence);
    }

    @Override
    public void makeRpc(SerfRequest request) throws IOException {
        Channel channel;
        if (!channelManger.isConnected()) {
            log.debug("We're not connected to Serf, so we can't send command={} with seq={}", request.getHeader().command, request.getHeader().seq);
            throw new IOException("Not connected to Serf");
        } else {
            channel = channelManger.get();
        }
        requestsBySequence.put(request.getHeader().seq, request);
        log.debug("Sending command={} with seq={}", request.getHeader().command, request.getHeader().seq);
        channel.write(messagePack.write(request.getHeader()));
        channel.writeAndFlush(messagePack.write(request.getBody()));
        log.debug("Sent command={} with seq={}", request.getHeader().command, request.getHeader().seq);
    }

    @Override
    public boolean isConnected() {
        return channelManger.isConnected();
    }

    public void close() {
        channelManger.close();
    }

}
