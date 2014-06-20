package com.qualys.jserf;

import com.qualys.jserf.model.request.AuthRequestBody;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.request.EmptyRequestBody;
import com.qualys.jserf.model.request.EventRequestBody;
import com.qualys.jserf.model.request.ForceLeaveRequestBody;
import com.qualys.jserf.model.request.HandShakeRequestBody;
import com.qualys.jserf.model.request.JoinRequestBody;
import com.qualys.jserf.model.request.KeyRequestBody;
import com.qualys.jserf.model.request.MembersFilteredRequestBody;
import com.qualys.jserf.model.request.MonitorRequestBody;
import com.qualys.jserf.model.request.QueryRequestBody;
import com.qualys.jserf.model.request.RequestBody;
import com.qualys.jserf.model.request.RequestHeader;
import com.qualys.jserf.model.request.RespondRequestBody;
import com.qualys.jserf.model.request.StopRequestBody;
import com.qualys.jserf.model.request.StreamRequestBody;
import com.qualys.jserf.model.request.TagsRequestBody;
import com.qualys.jserf.model.response.EmptyResponseBody;
import com.qualys.jserf.model.response.JoinResponseBody;
import com.qualys.jserf.model.response.KeyResponseBody;
import com.qualys.jserf.model.response.ListKeysResponseBody;
import com.qualys.jserf.model.response.MembersResponseBody;
import com.qualys.jserf.model.response.MonitorResponseBody;
import com.qualys.jserf.model.response.StatsResponseBody;
import com.qualys.jserf.model.response.StreamResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tburch on 6/20/14.
 */
public class SerfRequests {
    private static final AtomicInteger sequence = new AtomicInteger();

    public static SerfRequest handshake(SerfResponseCallBack callBack) {
        return handshake(1, callBack);
    }

    public static SerfRequest handshake(int version, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.HANDSHAKE);
        RequestBody body = new HandShakeRequestBody(version);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest auth(String authKey, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.AUTH);
        RequestBody body = new AuthRequestBody(authKey);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest event(String name, String payload, boolean coalesce, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.EVENT);
        RequestBody body = new EventRequestBody(name, payload, coalesce);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest forceLeave(String nodeName, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.FORCE_LEAVE);
        RequestBody body = new ForceLeaveRequestBody(nodeName);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest join(List<String> existingNodes, boolean replay, SerfResponseCallBack<JoinResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.JOIN);
        RequestBody body = new JoinRequestBody(existingNodes, replay);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest members(SerfResponseCallBack<MembersResponseBody> callBack) {
        return emptyBodyRequest(sequence.incrementAndGet(), callBack, Command.MEMBERS);
    }


    public static SerfRequest membersFiltered(Map<String, String> tags, String status, String name, SerfResponseCallBack<MembersResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.MEMBERS_FILTERED);
        RequestBody body = new MembersFilteredRequestBody(tags, status, name);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest tags(Map<String, String> tagsToAdd, List<String> tagsToRemove, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.TAGS);
        RequestBody body = new TagsRequestBody(tagsToAdd, tagsToRemove);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest stream(String type, SerfResponseCallBack<StreamResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.STREAM);
        RequestBody body = new StreamRequestBody(type);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest monitor(String level, SerfResponseCallBack<MonitorResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.MONITOR);
        RequestBody body = new MonitorRequestBody(level.toUpperCase());
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest stop(int sequenceToStop, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.STOP);
        RequestBody body = new StopRequestBody(sequenceToStop);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest leave(SerfResponseCallBack<EmptyResponseBody> callBack) {
        return emptyBodyRequest(sequence.incrementAndGet(), callBack, Command.LEAVE);
    }

    public static SerfRequest query(List<String> filterNodes, List<String> filterTags, boolean ack, int timeout, String name, String payload, SerfResponseCallBack<StreamResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.QUERY);
        RequestBody body = new QueryRequestBody(filterNodes, filterTags, ack, timeout, name, payload);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest respond(int id, String payload, SerfResponseCallBack<EmptyResponseBody> callBack) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), Command.RESPOND);
        RequestBody body = new RespondRequestBody(id, payload);
        return new SerfRequest(header, body, callBack);
    }

    public static SerfRequest installKey(String key, SerfResponseCallBack<KeyResponseBody> callBack) {
        return keyRequest(key, callBack, Command.INSTALL_KEY);
    }

    public static SerfRequest useKey(String key, SerfResponseCallBack<KeyResponseBody> callBack) {
        return keyRequest(key, callBack, Command.USE_KEY);
    }

    public static SerfRequest removeKey(String key, SerfResponseCallBack<KeyResponseBody> callBack) {
        return keyRequest(key, callBack, Command.REMOVE_KEY);
    }

    public static SerfRequest listKeys(SerfResponseCallBack<ListKeysResponseBody> callBack) {
        return emptyBodyRequest(sequence.incrementAndGet(), callBack, Command.LIST_KEYS);
    }

    public static SerfRequest stats(SerfResponseCallBack<StatsResponseBody> callBack) {
        return emptyBodyRequest(sequence.incrementAndGet(), callBack, Command.STATS);
    }

    private static final SerfRequest emptyBodyRequest(int sequence, SerfResponseCallBack callBack, Command command) {
        RequestHeader header = new RequestHeader(sequence, command);
        RequestBody body = new EmptyRequestBody();
        return new SerfRequest(header, body, callBack);
    }

    private static final SerfRequest keyRequest(String key, SerfResponseCallBack<KeyResponseBody> callBack, Command command) {
        RequestHeader header = new RequestHeader(sequence.incrementAndGet(), command);
        RequestBody body = new KeyRequestBody(key);
        return new SerfRequest(header, body, callBack);
    }
}
