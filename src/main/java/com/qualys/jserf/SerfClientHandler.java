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

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableSet;
import com.qualys.jserf.extractor.ExtractorManager;
import com.qualys.jserf.extractor.ResponseBodyExtractor;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.QueryResponseBody;
import com.qualys.jserf.model.response.ResponseHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.type.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.msgpack.template.Templates.*;

/**
 * @author Tristan Burch
 */
@Slf4j
@AllArgsConstructor
public class SerfClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private final Set<Command> multipleResponseCommands = ImmutableSet.of(Command.STREAM, Command.MONITOR, Command.QUERY);
    private final Template<Map<String, Value>> templateMap = tMap(TString, TValue);

    private final ExtractorManager extractorManager;
    private final MessagePack messagePack;
    private final Cache<Integer, Pair<Command, SerfResponseCallBack>> callBacksBySequence;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        InputStream inputStream = new ByteArrayInputStream(bytes);

        Map<String, Value> headerValues = messagePack.read(inputStream, templateMap);
        log.trace("Response header was {}", headerValues);
        Map<String, Value> bodyValues = Collections.emptyMap();
        if (inputStream.available() != 0) {
            bodyValues = messagePack.read(inputStream, templateMap);
        }
        log.trace("Response body was {}", bodyValues);

        int sequence = headerValues.get("Seq").asIntegerValue().getInt();
        String errorMessage = headerValues.get("Error").asRawValue().getString();
        ResponseHeader header = new ResponseHeader(errorMessage, sequence);

        Pair<Command, SerfResponseCallBack> commandCallBackPair = callBacksBySequence.getIfPresent(sequence);
        if (commandCallBackPair == null) {
            log.debug("Couldn't find corresponding Command/SerfResponseCallBack pair for sequence={}. Maybe it was already stopped?", sequence);
            return;
        }

        Command command = commandCallBackPair.getLeft();

        if (StringUtils.isNotEmpty(errorMessage)) {
            log.debug("Received error message '{}' with response for command={} and sequence={}", errorMessage, command, sequence);
        }

        Optional<ResponseBodyExtractor> extractor = extractorManager.getExtractor(command);
        if (!extractor.isPresent()) {
            log.warn("Couldn't find extractor for command={}", command);
            return;
        }
        SerfResponse response = new SerfResponse<>(header, extractor.get().extractBody(bodyValues, extractorManager));

        if (Command.QUERY.equals(command)) {
            QueryResponseBody queryResponseBody = (QueryResponseBody) response.getBody();
            if (QueryResponseBody.Type.DONE.equals(queryResponseBody.getType())) {
                log.trace("Removing Command/SerfResponseCallBack pair for sequence={} because it query command and the returned 'type' value was 'done'", sequence);
                callBacksBySequence.invalidate(sequence);
            }
        }

        if (!multipleResponseCommands.contains(command)) {
            log.trace("Removing Command/SerfResponseCallBack pair for sequence={} because it wasn't a command that returns multiple responses (those commands are: {})", sequence, multipleResponseCommands);
            callBacksBySequence.invalidate(sequence);
        }

        if (commandCallBackPair.getRight() == null) {
            log.trace("Callback for Command/SerfResponseCallBack pair with sequence={} was null", sequence);
            return;
        }

        log.trace("Invoking callback for command={} with sequence={}", command, sequence);
        commandCallBackPair.getRight().call(response);
        log.trace("Invoked callback for command={} with sequence={}", command, sequence);
    }
}
