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

package com.codeheadsystems.dstore.node.api;

import com.codeheadsystems.dstore.node.api.ImmutableTenantInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Details you can return back about the tenant to callers.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTenantInfo.class)
@JsonDeserialize(builder = ImmutableTenantInfo.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface TenantInfo {

  /**
   * ID of the tenant.
   *
   * @return String.
   */
  @JsonProperty("id")
  String id();

}
