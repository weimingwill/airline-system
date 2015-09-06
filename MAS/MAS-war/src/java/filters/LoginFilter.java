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
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/views/secured/*"})
public class LoginFilter implements Filter{
 
    @Inject
    UserBean loginBean;
    @Inject
    NavigationBean navigationBean;
    
    @Override
    public void init(FilterConfig config) throws ServletException{
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain) 
            throws IOException, ServletException{
        
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)rsp;
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        boolean inLoginPage = uri.equals(contextPath + navigationBean.toLogin());   
        
        if (!loginBean.isLoggedIn() && !inLoginPage) {
            response.sendRedirect(contextPath + navigationBean.toLogin());
        } else if (loginBean.isLoggedIn() && inLoginPage) {
            response.sendRedirect(contextPath + navigationBean.redirectToWorkplace());
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy(){
    }    
}
