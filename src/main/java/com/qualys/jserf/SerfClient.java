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
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Tristan Burch
 */
@Slf4j
public class SerfClient implements Closeable {
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final MessagePack messagePack;

    private final Channel channel;
    private final ConcurrentMap<Integer, SerfRequest> requestsBySequence = Maps.newConcurrentMap();
    private final ExtractorManager extractorManager = new ExtractorManager();

    public SerfClient(String serfHost, int serfPort) throws InterruptedException {
        this(serfHost, serfPort, new MessagePack());
    }

    public SerfClient(String serfHost, int serfPort, MessagePack messagePack) throws InterruptedException {
        this.messagePack = messagePack;

        Bootstrap b = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new SerfClientInitializer(messagePack, requestsBySequence, extractorManager));
        this.channel = b.connect(serfHost, serfPort).sync().channel();
    }

    public void makeRpc(SerfRequest request) throws IOException, InterruptedException {
        requestsBySequence.put(request.getHeader().seq, request);
        log.trace("Sending command={} with se={}", request.getHeader().command, request.getHeader().seq);
        channel.write(messagePack.write(request.getHeader()));
        channel.writeAndFlush(messagePack.write(request.getBody()));
        log.trace("Sent command={} with seq={}", request.getHeader().command, request.getHeader().seq);
    }

    public void close() throws IOException {
        group.shutdownGracefully();
    }
}
