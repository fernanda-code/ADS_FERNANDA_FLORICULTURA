package br.com.floricultura.controller;

import br.com.floricultura.facade.UsuarioFacade;
import br.com.floricultura.model.Perfil;
import br.com.floricultura.model.Usuario;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    @EJB
    private UsuarioFacade usuarioFacade;

    private String email;
    private String senha;
    private Usuario usuarioLogado;

    public String entrar() {
        Usuario usuario = usuarioFacade.autenticar(email, senha);
        if (usuario == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-mail ou senha inválidos.", null));
            senha = null;
            return null;
        }
        usuarioLogado = usuario;
        senha = null;
        return "/index.xhtml?faces-redirect=true";
    }

    public String sair() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLogado() {
        return usuarioLogado != null;
    }

    public boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.getPerfil() == Perfil.ADMIN;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}