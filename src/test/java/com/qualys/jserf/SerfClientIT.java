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

import com.qualys.jserf.model.response.EmptyResponseBody;
import com.qualys.jserf.model.response.MembersResponseBody;
import com.qualys.jserf.model.response.StatsResponseBody;
import com.qualys.jserf.model.response.StreamResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Tristan Burch
 */
@Slf4j
public class SerfClientIT {

    private SerfClient client;

    @Before
    public void initSerfClient() throws InterruptedException {
        SerfClient client = NettySerfClient.builder().build();
        this.client = client;
    }

    @Test(timeout = 10000)
    public void testHandShake() throws Exception {
        while (!client.isConnected()) {
            Thread.sleep(500);
        }
        final boolean[] callbackInvoked = {false};
        final CountDownLatch latch = new CountDownLatch(1);

        SerfResponseCallBack<EmptyResponseBody> callBack = new SerfResponseCallBack<EmptyResponseBody>() {
            @Override
            public void call(SerfResponse<EmptyResponseBody> response) {
                log.debug("Received call back with sequence {}", response.getHeader().getSeq());
                callbackInvoked[0] = true;

                assertNotNull(response.getHeader());
                assertEquals(StringUtils.EMPTY, response.getHeader().getError());
                assertNotNull(response.getBody());
                assertEquals(EmptyResponseBody.class, response.getBody().getClass());

                //don't count down unless all the asserts pass
                latch.countDown();
            }
        };
        SerfRequest request = SerfRequests.handshake(callBack);

        client.makeRpc(request);
        latch.await();

        assertTrue(callbackInvoked[0]);
    }

    @Test(timeout = 10000)
    public void testStats() throws Exception {
        while (!client.isConnected()) {
            Thread.sleep(500);
        }
        final boolean[] callbackInvoked = {false};
        final CountDownLatch latch = new CountDownLatch(1);

        SerfResponseCallBack<StatsResponseBody> callBack = new SerfResponseCallBack<StatsResponseBody>() {
            @Override
            public void call(SerfResponse<StatsResponseBody> response) {
                log.debug("Received call back with sequence {}", response.getHeader().getSeq());
                callbackInvoked[0] = true;

                assertNotNull(response.getHeader());
                assertEquals(StringUtils.EMPTY, response.getHeader().getError());
                assertEquals(StatsResponseBody.class, response.getBody().getClass());
                assertNotNull(response.getBody());

                StatsResponseBody body = response.getBody();
                log.debug("body={}", body);
                assertNotNull(body.getAgent());
                assertNotNull(body.getRuntime());
                assertNotNull(body.getSerf());
                assertNotNull(body.getTags());

                //don't count down unless all the asserts pass
                latch.countDown();
            }
        };

        SerfRequest request = SerfRequests.stats(callBack);

        client.makeRpc(request);
        latch.await();

        assertTrue(callbackInvoked[0]);
    }

    @Test(timeout = 10000)
    public void testMembers() throws Exception {
        while (!client.isConnected()) {
            Thread.sleep(500);
        }
        final boolean[] callbackInvoked = {false};
        final CountDownLatch latch = new CountDownLatch(1);

        SerfResponseCallBack<MembersResponseBody> callBack = new SerfResponseCallBack<MembersResponseBody>() {
            @Override
            public void call(SerfResponse<MembersResponseBody> response) {
                log.debug("Received call back with sequence {}", response.getHeader().getSeq());
                callbackInvoked[0] = true;

                assertNotNull(response.getHeader());
                assertEquals(StringUtils.EMPTY, response.getHeader().getError());
                assertEquals(MembersResponseBody.class, response.getBody().getClass());
                assertNotNull(response.getBody());

                MembersResponseBody body = response.getBody();
                log.debug("body={}", body);
                assertNotNull(body.getMembers());
                assertTrue(body.getMembers().size() > 0);

                //don't count down unless all the asserts pass
                latch.countDown();
            }
        };

        SerfRequest request = SerfRequests.members(callBack);

        client.makeRpc(request);
        latch.await();

        assertTrue(callbackInvoked[0]);
    }

    @Test(timeout = 10000)
    public void testStream() throws Exception {
        while (!client.isConnected()) {
            Thread.sleep(500);
        }
        final boolean[] callbackInvoked = {false};
        final CountDownLatch latch = new CountDownLatch(1);

        SerfResponseCallBack<StreamResponseBody> callBack = new SerfResponseCallBack<StreamResponseBody>() {
            @Override
            public void call(SerfResponse<StreamResponseBody> response) {
                log.debug("Received call back with sequence {}", response.getHeader().getSeq());
                callbackInvoked[0] = true;

                assertNotNull(response.getHeader());
                assertEquals(StringUtils.EMPTY, response.getHeader().getError());
                assertEquals(StreamResponseBody.class, response.getBody().getClass());
                assertNotNull(response.getBody());

                StreamResponseBody body = response.getBody();
                log.debug("body={}", body);
                assertNull(body.getEventName()); //the event name is null when subscribing to the stream

                //don't count down unless all the asserts pass
                latch.countDown();
            }
        };

        SerfRequest request = SerfRequests.stream("*", callBack);

        client.makeRpc(request);
        latch.await();

        assertTrue(callbackInvoked[0]);
    }

}