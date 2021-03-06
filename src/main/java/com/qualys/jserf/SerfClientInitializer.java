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
import com.qualys.jserf.extractor.ExtractorManager;
import com.qualys.jserf.model.request.Command;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.msgpack.MessagePack;

/**
 * @author Tristan Burch
 */
@Slf4j
@AllArgsConstructor
public class SerfClientInitializer extends ChannelInitializer<SocketChannel> {
    private final MessagePack messagePack;
    private final Cache<Integer, Pair<Command, SerfResponseCallBack>> callBacksBySequence;
    private final ExtractorManager extractorManager;
    private final ChannelManger channelManger;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        if (log.isDebugEnabled()) {
            pipeline.addFirst("loggingHandler", new LoggingHandler());
        }

        pipeline.addLast("reconnectHandler", new ReconnectClientHandler(channelManger));

        log.debug("Adding ByteArray Encoder");
        pipeline.addLast("bytesEncoder", new ByteArrayEncoder());

        log.debug("Adding SerfClientHandler");
        pipeline.addLast("handler", new SerfClientHandler(extractorManager, messagePack, callBacksBySequence));
    }
}
