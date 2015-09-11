/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import managedbean.application.NavigationBean;
import managedbean.common.UserBean;

/**
 *
 * @author winga_000
 */
//@WebFilter(filterName = "LoginFilter", urlPatterns = {"/views/secured/*"})
public class LoginFilter implements Filter{
 
    @Inject
    UserBean userBean;
    @Inject
    NavigationBean navigationBean;
    
    @Override
    public void init(FilterConfig config) throws ServletException{
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException{
        
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse rsp = (HttpServletResponse)response;
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String loginUrl = contextPath + navigationBean.toLogin();
        String redirectUrl = contextPath + navigationBean.redirectToWorkplace();
        boolean inLoginPage = uri.equals(loginUrl);
        if (!userBean.isLoggedIn() && !inLoginPage) {
            rsp.sendRedirect(loginUrl);
        } else if (userBean.isLoggedIn() && inLoginPage) {
            rsp.sendRedirect(redirectUrl);
        }        
        chain.doFilter(req, rsp);
    }
    
    @Override
    public void destroy(){
    }    
}
