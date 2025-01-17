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

package com.codeheadsystems.dstore.node.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.codeheadsystems.dstore.node.model.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantInfoConverterTest {

  private static final String UUID = "uuid";

  @Mock private Tenant tenant;

  @InjectMocks private TenantInfoConverter converter;

  @Test
  void from() {
    when(tenant.ridTenant()).thenReturn(UUID);
    assertThat(converter.from(tenant))
        .isNotNull()
        .hasFieldOrPropertyWithValue("id", UUID);
  }

}