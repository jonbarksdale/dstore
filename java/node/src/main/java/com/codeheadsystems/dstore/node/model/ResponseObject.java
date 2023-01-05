/*
 * Copyright (c) 2023. Ned Wolpert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codeheadsystems.dstore.node.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import java.util.UUID;
import org.immutables.value.Value;

/**
 * Generic response object. Useful to wrap lists and whatnot.
 *
 * @param <R> type of thing we wrap.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableResponseObject.class)
@JsonDeserialize(builder = ImmutableResponseObject.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ResponseObject<R> {

  /**
   * Helper method to build a reponse object.
   *
   * @param resource to return.
   * @param <R>      the type being returned.
   * @return a response object.
   */
  static <R> ResponseObject<R> with(final R resource) {
    return ImmutableResponseObject.<R>builder().resource(resource).build();
  }

  /**
   * The thing...
   *
   * @return resource.
   */
  @JsonProperty("resource")
  R resource();

  /**
   * Optional requestUuid if the client sent one.
   *
   * @return the optional uuid.
   */
  @JsonProperty("requestUuid")
  Optional<String> requestUuid();

  /**
   * A response uuid generated by the server for tracking.
   *
   * @return the uuid.
   */
  @Value.Default
  @JsonProperty("responseUuid")
  default String responseUuid() {
    return "response:" + UUID.randomUUID();
  }

}