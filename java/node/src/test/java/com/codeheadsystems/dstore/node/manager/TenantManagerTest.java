package com.codeheadsystems.dstore.node.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.codeheadsystems.dstore.common.crypt.AesGcmSivManager;
import com.codeheadsystems.dstore.node.dao.TenantDAO;
import com.codeheadsystems.dstore.node.model.Tenant;
import com.codeheadsystems.metrics.test.BaseMetricTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantManagerTest extends BaseMetricTest {
  private static final String NONCE = "NONCE";
  private static final String KEY = "KEY";
  private final String TENANT_ID = "tenant";

  @Mock private TenantDAO dao;
  @Mock private AesGcmSivManager aesManager;
  @Mock private Tenant tenant;
  @Mock private List<String> list;
  @Captor private ArgumentCaptor<Tenant> tenantArgumentCaptor;
  @Captor private ArgumentCaptor<String> stringArgumentCaptor;

  private TenantManager manager;

  @BeforeEach
  void setup() {
    manager = new TenantManager(metrics, dao, aesManager);
  }

  @Test
  void get() {
    when(dao.read(TENANT_ID)).thenReturn(Optional.of(tenant));
    assertThat(manager.get(TENANT_ID)).isPresent().contains(tenant);
  }

  @Test
  void create() {
    when(aesManager.randomKeyBase64Encoded()).thenReturn(KEY);
    when(aesManager.randomNonceBase64Encoded()).thenReturn(NONCE);
    when(dao.create(tenantArgumentCaptor.capture())).thenReturn(tenant);
    assertThat(manager.create(TENANT_ID)).isEqualTo(tenant);
    assertThat(tenantArgumentCaptor.getValue())
        .hasFieldOrPropertyWithValue("id", TENANT_ID)
        .hasFieldOrPropertyWithValue("key", KEY)
        .hasFieldOrPropertyWithValue("nonce", NONCE);
  }

  @Test
  void tenants() {
    when(dao.allTenants()).thenReturn(list);
    assertThat(manager.tenants()).isEqualTo(list);
  }

  @Test
  void delete() {
    when(dao.delete(stringArgumentCaptor.capture())).thenReturn(true);
    manager.delete(TENANT_ID);
    assertThat(stringArgumentCaptor.getValue()).isEqualTo(TENANT_ID);
  }
}