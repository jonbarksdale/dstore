package com.codeheadsystems.dstore.node.resource;

import java.io.IOException;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Used so that we can have request/responses use the traceUUID concept for request tracing.
 */
@Singleton
public class TraceUUID implements ContainerRequestFilter, ContainerResponseFilter, JerseyResource {

  /**
   * Identifier for the header.
   */
  public static final String TRACE_UUID_HEADER = "X-TraceUUID";
  private static final String MDC_ID = "trace";

  private static final Logger LOGGER = LoggerFactory.getLogger(TraceUUID.class);

  private final ThreadLocal<String> traceThreadLocal = new ThreadLocal<>();

  /**
   * Default constructor.
   */
  @Inject
  public TraceUUID() {
    LOGGER.info("TraceUUID");
  }

  private static String getOrCreatedUuid(final String uuid) {
    return uuid == null ? UUID.randomUUID().toString() : uuid;
  }

  /**
   * Gets the current ID, if set. Can be null.
   *
   * @return String UUID.
   */
  public String get() {
    return traceThreadLocal.get();
  }

  /**
   * Gets the UUID from the header, and sets it in the thread local. If there is none, create it.
   *
   * @param requestContext request context.
   * @throws IOException if anything goes wrong.
   */
  @Override
  public void filter(final ContainerRequestContext requestContext) throws IOException {
    final String uuid = requestContext.getHeaderString(TRACE_UUID_HEADER);
    LOGGER.debug("filter(request):{}", uuid);
    MDC.put(MDC_ID, uuid);
    traceThreadLocal.set(getOrCreatedUuid(uuid));
  }

  /**
   * Sets the current trace id in the response. Removes it from the header.
   *
   * @param requestContext  request context.
   * @param responseContext response context.
   * @throws IOException if anything goes wrong.
   */
  @Override
  public void filter(final ContainerRequestContext requestContext,
                     final ContainerResponseContext responseContext) throws IOException {
    final String uuid = get();
    LOGGER.debug("filter(response):{}", uuid);
    responseContext.getHeaders().add(TRACE_UUID_HEADER, getOrCreatedUuid(uuid));
    traceThreadLocal.set(null);
    MDC.remove(MDC_ID);
  }
}
