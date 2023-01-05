package com.codeheadsystems.dstore.node;

import com.codahale.metrics.health.HealthCheck;
import com.codeheadsystems.dstore.node.component.DaggerDropWizardComponent;
import com.codeheadsystems.dstore.node.component.DropWizardComponent;
import com.codeheadsystems.dstore.node.module.ConfigurationModule;
import com.codeheadsystems.metrics.dagger.MetricsModule;
import com.codeheadsystems.metrics.helper.DropwizardMetricsHelper;
import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is our application itself. Very little here is node specific.
 */
public class Node extends Application<NodeConfiguration> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Node.class);

  /**
   * Default constructor.
   */
  public Node() {
    LOGGER.info("Node()");
  }

  /**
   * Run the world.
   *
   * @param args from the command line.
   * @throws Exception if we could not start the server.
   */
  public static void main(String[] args) throws Exception {
    LOGGER.info("main({})", (Object) args);
    final Node server = new Node();
    server.run(args);
  }

  /**
   * Runs the application.
   *
   * @param configuration the parsed {@link NodeConfiguration} object
   * @param environment   the application's {@link Environment}
   * @throws Exception if everything dies.
   */
  @Override
  public void run(final NodeConfiguration configuration,
                  final Environment environment) throws Exception {
    LOGGER.info("run({},{})", configuration, environment);
    final MeterRegistry meterRegistry = new DropwizardMetricsHelper().instrument(environment.metrics());
    final DropWizardComponent component = DaggerDropWizardComponent.builder()
        .configurationModule(new ConfigurationModule(configuration))
        .metricsModule(new MetricsModule(meterRegistry))
        .build();
    final JerseyEnvironment jerseyEnvironment = environment.jersey();
    for (Managed managed : component.managed()) {
      LOGGER.info("Registering managed services: {}", managed.getClass().getSimpleName());
      environment.lifecycle().manage(managed);
    }
    for (Object resource : component.resources()) {
      LOGGER.info("Registering resource: {}", resource.getClass().getSimpleName());
      jerseyEnvironment.register(resource);
    }
    for (HealthCheck healthCheck : component.healthChecks()) {
      LOGGER.info("Registering healthCheck: {}", healthCheck.getClass().getSimpleName());
      environment.healthChecks().register(healthCheck.getClass().getSimpleName(), healthCheck);
    }
  }

}
