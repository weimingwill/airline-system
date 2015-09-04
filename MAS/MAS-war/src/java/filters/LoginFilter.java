/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import managedbean.common.LoginBean;

/**
 *
 * @author winga_000
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/views/secured/*"})
public class LoginFilter implements Filter{
    
    @Override
    public void init(FilterConfig config) throws ServletException{
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException{
        System.out.println("Begin LoginFilther doFilther");
        
        LoginBean loginBean = (LoginBean)((HttpServletRequest)request).getSession().getAttribute("loginBean");
        System.out.println(loginBean);
        //System.out.println(loginBean.isLoggedIn());
        if (loginBean == null || !loginBean.isLoggedIn()) {
            String contextPath = ((HttpServletRequest)request).getContextPath();
            ((HttpServletResponse)response).sendRedirect(contextPath + "/views/unsecured/login.xhtml");
            System.out.println("SendRedirect LoginFilther doFilther");
        }
        System.out.println("End LoginFilther doFilther");
        chain.doFilter(request, response);
        
    }
    
    @Override
    public void destroy(){
    }    
}
