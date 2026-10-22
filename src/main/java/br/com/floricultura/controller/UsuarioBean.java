package br.com.floricultura.controller;

import br.com.floricultura.exception.NegocioException;
import br.com.floricultura.facade.UsuarioFacade;
import br.com.floricultura.model.Perfil;
import br.com.floricultura.model.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named("usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {

    @EJB
    private UsuarioFacade facade;

    @Inject
    private LoginBean loginBean;

    private List<Usuario> usuarios;
    private Usuario usuario = new Usuario();
    private String senha;
    private String filtro;

    @PostConstruct
    public void init() {
        listar();
    }

    public void listar() {
        if (filtro == null || filtro.isBlank()) {
            usuarios = facade.listarTodos();
        } else {
            usuarios = facade.buscarPorNome(filtro.trim());
        }
    }

    public void limparFiltro() {
        filtro = null;
        listar();
    }

    public void novo() {
        usuario = new Usuario();
        senha = null;
    }

    public void editar(Usuario selecionado) {
        usuario = new Usuario();
        usuario.setId(selecionado.getId());
        usuario.setNome(selecionado.getNome());
        usuario.setEmail(selecionado.getEmail());
        usuario.setPerfil(selecionado.getPerfil());
        senha = null;
    }

    public void salvar() {
        try {
            if (usuario.getId() == null) {
                facade.cadastrar(usuario, senha);
            } else {
                facade.atualizar(usuario, senha);
            }
            mensagem(FacesMessage.SEVERITY_INFO, "Usuário salvo com sucesso.");
            novo();
            listar();
            PrimeFaces.current().executeScript("PF('dlgUsuario').hide()");
        } catch (NegocioException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível salvar o usuário.");
        }
    }

    public void excluir(Usuario selecionado) {
        Usuario logado = loginBean.getUsuarioLogado();
        if (logado != null && selecionado.getId().equals(logado.getId())) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Você não pode excluir o seu próprio usuário.");
            return;
        }
        try {
            facade.remover(selecionado);
            mensagem(FacesMessage.SEVERITY_INFO, "Usuário excluído.");
            listar();
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível excluir o usuário.");
        }
    }

    private void mensagem(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public Perfil[] getPerfis() {
        return Perfil.values();
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}