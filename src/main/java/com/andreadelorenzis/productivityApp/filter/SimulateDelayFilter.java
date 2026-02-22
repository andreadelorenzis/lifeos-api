package com.andreadelorenzis.productivityApp.filter;

import jakarta.servlet.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimulateDelayFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            Thread.sleep(500); // 0.5 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        chain.doFilter(request, response);
    }
}
