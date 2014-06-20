package com.qualys.jserf.extractor;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.qualys.jserf.model.request.Command;
import com.qualys.jserf.model.response.ResponseBody;

import java.util.Set;

public class ExtractorManager {

    private final Set<ResponseBodyExtractor<? extends ResponseBody>> extractors = ImmutableSet.of(
            new KeyResponseBodyExtractor(),
            new JoinResponseBodyExtractor(),
            new ListKeysResponseBodyExtractor(),
            new MembersResponseBodyExtractor(),
            new MembersResponseBodyExtractor(),
            new MonitorResponseBodyExtractor(),
            new QueryResponseBodyExtractor(),
            new KeyResponseBodyExtractor(),
            new StatsResponseBodyExtractor(),
            new StreamResponseBodyExtractor(),
            new KeyResponseBodyExtractor(),
            new EmptyResponseBodyExtractor(),
            new StreamUserEventResponseBodyExtractor()
    );

    public Optional<ResponseBodyExtractor> getExtractor(Command command) {
        for (ResponseBodyExtractor extractor : extractors) {
            if (extractor.handlesCommand(command)) {
                return Optional.of(extractor);
            }
        }
        return Optional.absent();
    }

    public Optional<ResponseBodyExtractor> getExtractor(String event) {
        for (ResponseBodyExtractor extractor : extractors) {
            if (extractor.handlesEvent(event)) {
                return Optional.of(extractor);
            }
        }
        return Optional.absent();
    }
}
