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

package com.codeheadsystems.dstore.node.engine;

import com.codeheadsystems.dstore.node.model.TenantTable;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.hash.HashFunction;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Methods needed for a table definition.
 */
public interface TableDefinitionEngine {

  /**
   * The hashing function needed for the table.
   *
   * @return a supplier for the required hashing function.
   */
  Supplier<HashFunction> hashFunctionSupplier();

  /**
   * Implementations need their own definition name for lookup.
   *
   * @return definition name.
   */
  String definitionName();

  /**
   * Hashes the string using the hashing function.
   *
   * @param string to hash.
   * @return the resulting hash.
   */
  default int hash(final String string) {
    return hashFunctionSupplier().get().hashUnencodedChars(string).asInt();
  }

  /**
   * Reads the entity from the table, returning the JsonNode.
   *
   * @param tenantTable table to read from.
   * @param entity      the entity id.
   * @return a json node if found.
   */
  Optional<JsonNode> read(TenantTable tenantTable, String entity);

  /**
   * Writes the entity to the table.
   *
   * @param tenantTable table to write to.
   * @param entity      the entity id.
   * @param data        the data.
   */
  void write(TenantTable tenantTable, String entity, final JsonNode data);

  /**
   * Delete the entity from the table, returning the JsonNode.
   *
   * @param tenantTable table to delete from.
   * @param entity      the entity id.
   * @return boolean if anything was deleted.
   */
  boolean delete(TenantTable tenantTable, String entity);
}
