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

import com.qualys.jserf.extractor.ExtractorManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;

import java.util.concurrent.ConcurrentMap;

/**
 * @author Tristan Burch
 */
@Slf4j
@AllArgsConstructor
public class SerfClientInitializer extends ChannelInitializer<SocketChannel> {
    private final MessagePack messagePack;
    private final ConcurrentMap<Integer, SerfRequest> requestsBySequence;
    private final ExtractorManager extractorManager;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        if (log.isDebugEnabled()) {
            pipeline.addFirst("loggingHandler", new LoggingHandler());
        }

        log.debug("Adding ByteArray Encoder");
        pipeline.addLast("bytesEncoder", new ByteArrayEncoder());

        log.debug("Adding SerfClientHandler");
        pipeline.addLast("handler", new SerfClientHandler(extractorManager, messagePack, requestsBySequence));
    }
}
