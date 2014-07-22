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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.qualys.jserf.extractor.ExtractorManager;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.request.StopRequestBody;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * @author Tristan Burch
 */
@Slf4j
public class NettySerfClient implements SerfClient {
    private final MessagePack messagePack;
    private final Cache<Integer, Pair<Command, SerfResponseCallBack>> callBacksBySequenceCache;

    private final ChannelManger channelManger;

    private final ExtractorManager extractorManager = new ExtractorManager();

    public NettySerfClient(String serfHost, int serfPort, MessagePack messagePack, long minReconnectRetrySeconds, long maxReconnectRetrySeconds, Cache<Integer, Pair<Command, SerfResponseCallBack>> callBacksBySequenceCache, ConnectionStateChangeCallback connectionStateChangeCallback) {
        this.messagePack = messagePack;
        this.callBacksBySequenceCache = callBacksBySequenceCache;

        this.channelManger = new ChannelManger(serfHost, serfPort, minReconnectRetrySeconds, maxReconnectRetrySeconds, extractorManager, messagePack, callBacksBySequenceCache, connectionStateChangeCallback);
    }

    @Override
    public void makeRpc(SerfRequest request) throws IOException {
        if (!channelManger.isConnected()) {
            log.debug("We're not connected to Serf, so we can't send command={} with seq={}", request.getHeader().command, request.getHeader().seq);
            throw new IOException("Not connected to Serf");
        }

        Channel channel = channelManger.get();

        callBacksBySequenceCache.put(request.getHeader().seq, Pair.of(request.getHeader().toCommand(), request.getCallBack()));
        try {
            log.debug("Sending command={} with seq={}", request.getHeader().command, request.getHeader().seq);
            channel.write(messagePack.write(request.getHeader()));
            channel.writeAndFlush(messagePack.write(request.getBody()));
            log.debug("Sent command={} with seq={}", request.getHeader().command, request.getHeader().seq);
        } catch (IOException e) {
            log.warn("Caught IOException while trying to write request. Removing callback from cache", e);
            callBacksBySequenceCache.invalidate(request.getHeader().seq);
            throw e;
        } finally {
            if (Command.STOP.equals(request.getHeader().toCommand())) {
                StopRequestBody stopRequest = (StopRequestBody) request.getBody();
                log.debug("Found stop request with sequence={}. Removing callback from the cache.", stopRequest.stop);
                callBacksBySequenceCache.invalidate(stopRequest.stop);
                log.debug("Removed callback for sequence={}.", stopRequest.stop);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return channelManger.isConnected();
    }

    public void close() {
        channelManger.close();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Accessors(fluent = true)
    @Setter
    public static class Builder {
        private MessagePack messagePack = new MessagePack();
        private String serfHost = "localhost";
        private int serfPort = 7373;
        private long minReconnectRetrySeconds = 1l;
        private long maxReconnectRetrySeconds = 30l;
        private Cache<Integer, Pair<Command, SerfResponseCallBack>> callBackCache = CacheBuilder.newBuilder().build();
        private ConnectionStateChangeCallback connectionStateChangeCallback = null;

        public SerfClient build() {
            return new NettySerfClient(serfHost, serfPort, messagePack, minReconnectRetrySeconds, maxReconnectRetrySeconds, callBackCache, connectionStateChangeCallback);
        }
    }
}
