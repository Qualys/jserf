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
 * Created by tburch on 6/19/14.
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
