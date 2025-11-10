package web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

@WebFilter(urlPatterns = {"/home" , "/tests", "/tests/result"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Object uid = request.getSession().getAttribute("uid");
        if (uid == null) {
            response.sendRedirect("/login?next=" + URLEncoder.encode(request.getRequestURI(), "UTF-8"));
            return;
        }
        filterChain.doFilter(request, response);
    }
  }
