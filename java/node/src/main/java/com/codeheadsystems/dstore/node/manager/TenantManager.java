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

import com.codeheadsystems.dstore.common.crypt.AesGcmSivManager;
import com.codeheadsystems.dstore.node.dao.TenantDAO;
import com.codeheadsystems.dstore.node.model.ImmutableTenant;
import com.codeheadsystems.dstore.node.model.Tenant;
import com.codeheadsystems.metrics.Metrics;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages tenants.
 */
@Singleton
public class TenantManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(TenantManager.class);

  private final Metrics metrics;
  private final TenantDAO dao;
  private final AesGcmSivManager aesGcmSivManager;

  /**
   * Default constructor.
   *
   * @param metrics          to use.
   * @param dao              to use.
   * @param aesGcmSivManager to key generation.
   */
  @Inject
  public TenantManager(final Metrics metrics,
                       final TenantDAO dao,
                       final AesGcmSivManager aesGcmSivManager) {
    LOGGER.info("TenantManager({},{},{})", metrics, dao, aesGcmSivManager);
    this.metrics = metrics;
    this.dao = dao;
    this.aesGcmSivManager = aesGcmSivManager;
  }

  /**
   * Gets the current tenant if it exists.
   *
   * @param tenantId to get.
   * @return the tenant.
   */
  public Optional<Tenant> get(final String tenantId) {
    LOGGER.debug("get({})", tenantId);
    return metrics.time("TenantManager.get", () -> dao.read(tenantId));
  }

  /**
   * Created the tenant. If it already exists, simply return the one we already have. Idempotent.
   *
   * @param tenantId tenant to create.
   * @return a tenant.
   */
  public Tenant create(final String tenantId) {
    LOGGER.debug("create({})", tenantId);
    return get(tenantId).orElseGet(() ->
        metrics.time("TenantManager.create", () -> dao.create(buildTenant(tenantId))));
  }

  private Tenant buildTenant(final String tenantId) {
    LOGGER.debug("buildTenant({})", tenantId);
    return ImmutableTenant.builder()
        .id(tenantId)
        .uuid(UUID.randomUUID().toString())
        .key(aesGcmSivManager.randomKeyBase64Encoded())
        .nonce(aesGcmSivManager.randomNonceBase64Encoded())
        .build();
  }

  /**
   * Lists current tenants.
   *
   * @return a list of tenants.
   */
  public List<String> tenants() {
    LOGGER.debug("tenants()");
    return metrics.time("TenantManager.tenants", dao::allTenants);
  }

  /**
   * Deletes the current tenant. This will destroy the tables too, datasources, etc. If queries are in progress, it
   * will kill everything.
   *
   * @param tenantId to delete.
   * @return boolean if deleted or not.
   */
  public boolean delete(final String tenantId) {
    LOGGER.debug("delete({})", tenantId);
    return metrics.time("TenantManager.tenants", () -> dao.delete(tenantId));
  }

}