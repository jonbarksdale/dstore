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

package com.codeheadsystems.dstore.node.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeheadsystems.dstore.node.BaseSQLTest;
import com.codeheadsystems.dstore.node.model.ImmutableTenantTable;
import com.codeheadsystems.dstore.node.model.ImmutableTenantTableIdentifier;
import com.codeheadsystems.dstore.node.model.TenantTable;
import com.codeheadsystems.dstore.node.model.TenantTableIdentifier;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TenantTableDaoTest extends BaseSQLTest {

  private static final Random random = new Random();

  private TenantTableDao dao;

  public static Stream<Arguments> tenantTables() {
    return Stream.of(
        Arguments.of(randomTenantTable()),
        Arguments.of(ImmutableTenantTable.copyOf(randomTenantTable()).withHashEnd(Optional.empty())),
        Arguments.of(ImmutableTenantTable.copyOf(randomTenantTable()).withHashStart(Optional.empty()))
    );
  }

  private static TenantTable randomTenantTable() {
    return randomTenantTable(UUID.randomUUID().toString());
  }

  private static TenantTable randomTenantTable(final String tenantId) {
    final TenantTableIdentifier identifier = ImmutableTenantTableIdentifier.builder()
        .tenantId(tenantId)
        .tableName(UUID.randomUUID().toString())
        .build();
    return ImmutableTenantTable.builder()
        .identifier(identifier)
        .hashStart(UUID.randomUUID().toString())
        .hashEnd(UUID.randomUUID().toString())
        .estimatedQuantity(random.nextInt())
        .enabled(random.nextBoolean())
        .tableVersion(UUID.randomUUID().toString())
        .key(UUID.randomUUID().toString())
        .nonce(UUID.randomUUID().toString())
        .primaryKey(UUID.randomUUID().toString())
        .build();
  }

  @BeforeEach
  void setup() {
    dao = new TenantTableDao(sqlEngine);
  }

  @ParameterizedTest
  @MethodSource("tenantTables")
  void roundTripWithTenants(final TenantTable tenantTable) {
    assertThat(dao.read(tenantTable.identifier().tenantId(), tenantTable.identifier().tableName())).isEmpty();
    assertThat(dao.create(tenantTable)).isEqualTo(tenantTable);
    assertThat(dao.read(tenantTable.identifier().tenantId(), tenantTable.identifier().tableName())).isPresent().contains(tenantTable);
  }

  @Test
  void testUpdates() {
    final TenantTable tt1 = randomTenantTable();
    dao.create(tt1);
    assertThat(dao.read(tt1.identifier().tenantId(), tt1.identifier().tableName())).isPresent().contains(tt1);
    final TenantTable tt2 = ImmutableTenantTable.copyOf(tt1).withHashEnd(Optional.empty()).withHashStart(Optional.empty());
    dao.update(tt2);
    assertThat(dao.read(tt1.identifier().tenantId(), tt1.identifier().tableName())).isPresent().contains(tt2);
    final TenantTable tt3 = ImmutableTenantTable.copyOf(tt1).withHashEnd(Optional.of(UUID.randomUUID().toString())).withHashStart(Optional.of(UUID.randomUUID().toString()));
    dao.update(tt3);
    assertThat(dao.read(tt1.identifier().tenantId(), tt1.identifier().tableName())).isPresent().contains(tt3);
  }

  @Test
  void roundTrip() {
    final TenantTable tenantTable = randomTenantTable();
    assertThat(dao.read(tenantTable.identifier().tenantId(), tenantTable.identifier().tableName())).isEmpty();
    assertThat(dao.create(tenantTable)).isEqualTo(tenantTable);
    assertThat(dao.read(tenantTable.identifier().tenantId(), tenantTable.identifier().tableName())).isPresent().contains(tenantTable);
  }

  @Test
  void list() {
    final String tenantId = UUID.randomUUID().toString();
    final List<TenantTable> tenants = List.of(randomTenantTable(tenantId), randomTenantTable(tenantId), randomTenantTable(tenantId));
    tenants.forEach(dao::create);
    final List<String> tableNames = tenants.stream().map(TenantTable::identifier).map(TenantTableIdentifier::tableName).toList();
    assertThat(dao.allTenantTables(tenantId)).containsOnlyOnceElementsOf(tableNames);
  }

  @Test
  void delete() {
    final TenantTable tenantTable = randomTenantTable();
    assertThat(dao.delete(tenantTable.identifier().tenantId(), tenantTable.identifier().tableName())).isFalse();
    dao.create(tenantTable);
    assertThat(dao.delete(tenantTable.identifier().tenantId(), tenantTable.identifier().tableName())).isTrue();
  }
}