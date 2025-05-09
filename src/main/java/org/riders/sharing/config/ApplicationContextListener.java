package org.riders.sharing.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.riders.sharing.authentication.AuthTokenDecoder;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.service.impl.CustomerServiceImpl;

import static org.riders.sharing.config.ContextAttributes.APP_CONFIG_ATTRIBUTE;
import static org.riders.sharing.config.ContextAttributes.CONNECTION_POOL_ATTRIBUTE;
import static org.riders.sharing.config.ContextAttributes.CUSTOMER_REPOSITORY_ATTRIBUTE;
import static org.riders.sharing.config.ContextAttributes.CUSTOMER_SERVICE_ATTRIBUTE;
import static org.riders.sharing.config.ContextAttributes.TOKEN_DECODER_ATTRIBUTE;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final var context = sce.getServletContext();

        final var appConfig = ApplicationConfig.getInstance();
        final var connectionPool = ConnectionPool.INSTANCE;

        final var customerRepository = new CustomerRepositoryImpl(connectionPool);
        final var customerService = new CustomerServiceImpl(customerRepository);
        final var tokenDecoder = new AuthTokenDecoder(appConfig.getAlgorithm());

        context.setAttribute(APP_CONFIG_ATTRIBUTE, appConfig);
        context.setAttribute(CONNECTION_POOL_ATTRIBUTE, connectionPool);
        context.setAttribute(CUSTOMER_REPOSITORY_ATTRIBUTE, customerRepository);
        context.setAttribute(CUSTOMER_SERVICE_ATTRIBUTE, customerService);
        context.setAttribute(TOKEN_DECODER_ATTRIBUTE, tokenDecoder);
    }
}
