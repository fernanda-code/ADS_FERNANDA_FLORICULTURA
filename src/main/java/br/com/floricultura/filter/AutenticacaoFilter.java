package br.com.floricultura.filter;

import br.com.floricultura.controller.LoginBean;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class AutenticacaoFilter implements Filter {

    @Inject
    private LoginBean loginBean;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contexto = req.getContextPath();
        String caminho = req.getRequestURI().substring(contexto.length());

        boolean publico = caminho.equals("/login.xhtml")
                || caminho.startsWith("/jakarta.faces.resource/")
                || caminho.startsWith("/resources/");
        if (publico) {
            chain.doFilter(request, response);
            return;
        }

        if (!loginBean.isLogado()) {
            resp.sendRedirect(contexto + "/login.xhtml");
            return;
        }

        if (caminho.startsWith("/admin/") && !loginBean.isAdmin()) {
            resp.sendRedirect(contexto + "/acesso-negado.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}