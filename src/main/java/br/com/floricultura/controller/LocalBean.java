package br.com.floricultura.controller;

import br.com.floricultura.facade.LocalFacade;
import br.com.floricultura.model.Local;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named("localBean")
@ViewScoped
public class LocalBean implements Serializable {

    @EJB
    private LocalFacade facade;

    private List<Local> locais;
    private Local local = new Local();
    private String filtro;

    @PostConstruct
    public void init() {
        listar();
    }

    public void listar() {
        if (filtro == null || filtro.isBlank()) {
            locais = facade.listarTodos();
        } else {
            locais = facade.buscarPorNome(filtro.trim());
        }
    }

    public void limparFiltro() {
        filtro = null;
        listar();
    }

    public void novo() {
        local = new Local();
    }

    public void editar(Local selecionado) {
        local = new Local();
        local.setId(selecionado.getId());
        local.setNome(selecionado.getNome());
        local.setDescricao(selecionado.getDescricao());
    }

    public void salvar() {
        try {
            if (local.getId() == null) {
                facade.criar(local);
            } else {
                facade.editar(local);
            }
            mensagem(FacesMessage.SEVERITY_INFO, "Local salvo com sucesso.");
            local = new Local();
            listar();
            PrimeFaces.current().executeScript("PF('dlgLocal').hide()");
        } catch (Exception e) {
            local = new Local();
            mensagem(FacesMessage.SEVERITY_ERROR,
                    "Não foi possível salvar. Já existe um local com esse nome?");
        }
    }

    public void excluir(Local selecionado) {
        try {
            facade.remover(selecionado);
            mensagem(FacesMessage.SEVERITY_INFO, "Local excluído.");
            listar();
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR,
                    "Não é possível excluir: existem plantas nesse local.");
        }
    }

    private void mensagem(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public List<Local> getLocais() {
        return locais;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}