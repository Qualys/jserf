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

import com.google.common.base.Supplier;
import com.qualys.jserf.extractor.ExtractorManager;
import com.qualys.jserf.model.response.EmptyResponseBody;
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.util.BackoffStrategy;
import com.twitter.common.util.TruncatedBinaryBackoff;
import com.twitter.common.util.concurrent.BackingOffFutureTask;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Tristan Burch
 */
@Slf4j
public class ChannelManger implements Supplier<Channel>, Closeable {
    private final String serfHost;
    private final int serfPort;
    private final ExtractorManager extractorManager;
    private final MessagePack messagePack;
    private final ConcurrentMap<Integer, SerfRequest> requestsBySequence;
    private final BackoffStrategy backoffStrategy;
    private final EventLoopGroup eventLoopGroup;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean connecting = new AtomicBoolean(false);

    private volatile Channel currentChannel;

    public ChannelManger(String host, int port, long minReconnectRetrySeconds, long maxReconnectRetrySeconds, ExtractorManager extractorManager, MessagePack messagePack, ConcurrentMap<Integer, SerfRequest> requestsBySequence) {
        this.serfHost = host;
        this.serfPort = port;
        this.extractorManager = extractorManager;
        this.messagePack = messagePack;
        this.requestsBySequence = requestsBySequence;

        this.eventLoopGroup = new NioEventLoopGroup();
        this.backoffStrategy = new TruncatedBinaryBackoff(Amount.of(minReconnectRetrySeconds, Time.SECONDS), Amount.of(maxReconnectRetrySeconds, Time.SECONDS), true);

        try {
            connect();
        } catch (Exception e) {
            log.warn("Caught Exception while trying to connect to Serf at {}:{} during instantiation", serfHost, serfPort);
            connecting.set(false);
            tryToReconnect();
        }
    }

    private void connect() throws InterruptedException, IOException {
        connected.set(false);
        connecting.set(true);
        this.currentChannel = null;

        try {
            final Channel channel = new Bootstrap().group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new SerfClientInitializer(messagePack, requestsBySequence, extractorManager))
                    .connect(serfHost, serfPort).sync().channel();
            connecting.set(false);
            SerfRequest handshake = SerfRequests.handshake(new SerfResponseCallBack<EmptyResponseBody>() {
                @Override
                public void call(SerfResponse response) {
                    log.debug("Received handshake response with sequence={} and error={}", response.getSequence(), response.getHeader().getError());
                    if (!response.isErrored()) {
                        currentChannel = channel;
                        connected.set(true);
                    }
                }
            });
            requestsBySequence.put(handshake.getHeader().seq, handshake);
            log.debug("Sending handshake to {}:{} with sequence={}", serfHost, serfPort, handshake.getHeader().seq);
            channel.write(messagePack.write(handshake.getHeader()));
            channel.writeAndFlush(messagePack.write(handshake.getBody()));
            log.debug("Sent handshake to {}:{} with sequence={}", serfHost, serfPort, handshake.getHeader().seq);
        } catch (IOException e) {
            log.warn("Caught IOException while trying to connect to {}:{}", serfHost, serfPort);
            throw e;
        }
    }

    public boolean isConnected() {
        return connected.get();
    }

    @Override
    public Channel get() {
        return currentChannel;
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    private void tryToReconnect() {
        log.debug("Trying to reconnect to {}:{}", serfHost, serfPort);
        if (!connecting.get()) {
            BackingOffFutureTask reconnectTask = new BackingOffFutureTask(eventLoopGroup, new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        connect();
                    } catch (Exception e) {
                        log.warn("Exception caught while trying to reconnect to serf at {}:{}", serfHost, serfPort);
                        return false;
                    }
                    return true;
                }
            }, Integer.MAX_VALUE, backoffStrategy);
            log.debug("Submitting reconnect task");
            eventLoopGroup.submit(reconnectTask);
        }
    }
}
