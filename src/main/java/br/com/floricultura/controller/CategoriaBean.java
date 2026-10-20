package br.com.floricultura.controller;

import br.com.floricultura.facade.CategoriaFacade;
import br.com.floricultura.model.Categoria;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named("categoriaBean")
@ViewScoped
public class CategoriaBean implements Serializable {

    @EJB
    private CategoriaFacade facade;

    private List<Categoria> categorias;
    private Categoria categoria = new Categoria();
    private String filtro;

    @PostConstruct
    public void init() {
        listar();
    }

    public void listar() {
        if (filtro == null || filtro.isBlank()) {
            categorias = facade.listarTodos();
        } else {
            categorias = facade.buscarPorNome(filtro.trim());
        }
    }

    public void limparFiltro() {
        filtro = null;
        listar();
    }

    public void novo() {
        categoria = new Categoria();
    }

    public void editar(Categoria selecionada) {
        categoria = new Categoria();
        categoria.setId(selecionada.getId());
        categoria.setNome(selecionada.getNome());
        categoria.setDescricao(selecionada.getDescricao());
    }

    public void salvar() {
        try {
            if (categoria.getId() == null) {
                facade.criar(categoria);
            } else {
                facade.editar(categoria);
            }
            mensagem(FacesMessage.SEVERITY_INFO, "Categoria salva com sucesso.");
            categoria = new Categoria();
            listar();
            PrimeFaces.current().executeScript("PF('dlgCategoria').hide()");
        } catch (Exception e) {
            categoria = new Categoria();
            mensagem(FacesMessage.SEVERITY_ERROR,
                    "Não foi possível salvar. Já existe uma categoria com esse nome?");
        }
    }

    public void excluir(Categoria selecionada) {
        try {
            facade.remover(selecionada);
            mensagem(FacesMessage.SEVERITY_INFO, "Categoria excluída.");
            listar();
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR,
                    "Não é possível excluir: existem plantas nessa categoria.");
        }
    }

    private void mensagem(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}