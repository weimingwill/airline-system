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
import javax.servlet.http.HttpSession;
import managedbean.application.NavigationController;
import managedbean.common.UserController;

/**
 *
 * @author winga_000
 */
@WebFilter(filterName = "AutoLogoutFilter", urlPatterns = {"/*"})
public class AutoLogoutFilter implements Filter {

    @Inject
    NavigationController navigationController;
    @Inject
    UserController userController;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        System.out.println("AutoLogoutFilter:doFilter()");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rsp = (HttpServletResponse) response;
        String contextPath = req.getContextPath(); 
        HttpSession session = req.getSession();
        if (userController.isLoggedIn()) {
            session.setMaxInactiveInterval(60 * 30);
        }
        if(session == null){
            rsp.sendRedirect(contextPath + userController.doLogout());
            System.out.println("AutoLooutFilter: Logout()");
        }
        
        chain.doFilter(req, response);

    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {
    }

    /**
     * Init method for this filter
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException{
    }


}
