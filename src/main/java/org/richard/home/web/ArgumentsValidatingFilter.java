package org.richard.home.web;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class ArgumentsValidatingFilter implements Filter {

    private static final String[] EXPECTED_ARGUMENT_NAMES = new String[]{"name", "id", "age"};
    private static final Logger log = LoggerFactory.getLogger(ArgumentsValidatingFilter.class);

    public ArgumentsValidatingFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Filter: {} initiated...", this.getClass().getName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.getParameterNames().asIterator().forEachRemaining(param -> {
            if (Arrays.stream(EXPECTED_ARGUMENT_NAMES).anyMatch(expectedName -> expectedName.equals(param))) {
                try {
                    log.info("request param: {} matched expected parameters. Continue proceeding...", param);
                    chain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            } else {
                log.warn("request parameters did not match the expected parameters. Abort processing!");
            }
        });
    }
}
