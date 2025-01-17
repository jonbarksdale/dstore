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

package com.codeheadsystems.dstore.node.manager;

import com.codeheadsystems.dstore.node.engine.TableDefinitionEngine;
import com.codeheadsystems.dstore.node.exception.NotFoundException;
import com.codeheadsystems.dstore.node.model.TenantTable;
import com.codeheadsystems.dstore.node.model.TenantTableIdentifier;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles reading/writing into the tenant table we are executing. Wrapper to the engine.
 */
@Singleton
public class TenantTableEntryManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TenantTableEntryManager.class);

  private final Map<String, TableDefinitionEngine> tableDefinitionEngineMap;
  private final TenantTableManager tenantTableManager;

  /**
   * Constructor.
   *
   * @param tableDefinitionEngineMap the map of who does the hard work.
   * @param tenantTableManager       to get the tenant table.
   */
  @Inject
  public TenantTableEntryManager(final Map<String, TableDefinitionEngine> tableDefinitionEngineMap,
                                 final TenantTableManager tenantTableManager) {
    LOGGER.info("TenantTableEntryManager({},{})", tableDefinitionEngineMap, tenantTableManager);
    this.tableDefinitionEngineMap = tableDefinitionEngineMap;
    this.tenantTableManager = tenantTableManager;
  }

  /**
   * Reads the entry.
   *
   * @param identifier of the table.
   * @param entity     to read.
   * @return the data, if found.
   */
  public Optional<JsonNode> read(final TenantTableIdentifier identifier,
                                 final String entity) {
    LOGGER.trace("read({},{})", identifier, entity);
    final TenantTable tenantTable = tenantTableManager.get(identifier)
        .orElseThrow(() -> new NotFoundException("No such table:" + identifier));
    return engine(tenantTable).read(tenantTable, entity);
  }

  /**
   * Writes the entry.
   *
   * @param identifier of the table.
   * @param entity     to write.
   * @param jsonNode   the data, if found.
   */
  public void write(final TenantTableIdentifier identifier,
                    final String entity,
                    final JsonNode jsonNode) {
    LOGGER.trace("write({},{})", identifier, entity);
    final TenantTable tenantTable = tenantTableManager.get(identifier)
        .orElseThrow(() -> new NotFoundException("No such table:" + identifier));
    engine(tenantTable).write(tenantTable, entity, jsonNode);
  }

  /**
   * Deletes the entry.
   *
   * @param identifier of the table.
   * @param entity     the entity.
   * @return if it was deleted or not.
   */
  public boolean delete(final TenantTableIdentifier identifier,
                        final String entity) {
    LOGGER.trace("delete({},{})", identifier, entity);
    final TenantTable tenantTable = tenantTableManager.get(identifier)
        .orElseThrow(() -> new NotFoundException("No such table:" + identifier));
    return engine(tenantTable).delete(tenantTable, entity);
  }

  private TableDefinitionEngine engine(final TenantTable tenantTable) {
    final String tableVersion = tenantTable.tableVersion();
    LOGGER.trace("engine({})", tableVersion);
    final TableDefinitionEngine engine = tableDefinitionEngineMap.get(tableVersion);
    if (engine == null) {
      LOGGER.error("Bad version: {}", tableVersion);
      throw new IllegalArgumentException("No such version:" + tableVersion);
    }
    return engine;
  }

}
