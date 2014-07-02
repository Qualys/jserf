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

package com.qualys.jserf.model.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

/**
 * @author Tristan Burch
 */
@Message
@AllArgsConstructor
@NoArgsConstructor
public class StopRequestBody extends RequestBody {
    public int stop;
}
