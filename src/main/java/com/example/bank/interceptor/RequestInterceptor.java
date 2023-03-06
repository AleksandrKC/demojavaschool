package com.example.bank.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        LOG.info("RequestInterceptor preHandle: request.getRequestURI()=" + request.getRequestURI()
                + ";request.getContextPath=" + request.getContextPath()
                + ";request.getQueryString=" + request.getQueryString()
                + ";request.getRequestURL=" + request.getRequestURL()
                + ";request.getServerName=" + request.getServerName()
                + ";response.getStatus()=" + response.getStatus());
        return true; //HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOG.info("RequestInterceptor postHandle: request.getRequestURI()=" + request.getRequestURI() + ";response.getStatus()=" + response.getStatus());
    }

    @Override
    public void afterCompletion(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOG.info("RequestInterceptor afterCompletion: request.getRequestURI()=" + request.getRequestURI() + ";response.getStatus()=" + response.getStatus());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}