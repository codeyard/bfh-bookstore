package org.bookstore.payment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogFilter extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    public void doFilter(HttpServletRequest request, HttpServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        logger.info("Request: {} {}, query string: {}", request.getMethod(), request.getRequestURI(), request.getQueryString());
        chain.doFilter(request, response);
        logger.info("Response: {}", response.getStatus());
    }

}
