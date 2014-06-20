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
