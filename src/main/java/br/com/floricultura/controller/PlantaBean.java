package br.com.floricultura.controller;

import br.com.floricultura.facade.CategoriaFacade;
import br.com.floricultura.facade.LocalFacade;
import br.com.floricultura.facade.PlantaFacade;
import br.com.floricultura.model.Categoria;
import br.com.floricultura.model.Local;
import br.com.floricultura.model.Planta;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named("plantaBean")
@ViewScoped
public class PlantaBean implements Serializable {

    @EJB
    private PlantaFacade facade;

    @EJB
    private CategoriaFacade categoriaFacade;

    @EJB
    private LocalFacade localFacade;

    private List<Planta> plantas;
    private List<Categoria> categorias;
    private List<Local> locais;
    private Planta planta = new Planta();
    private Long categoriaId;
    private Long localId;
    private String filtro;

    @PostConstruct
    public void init() {
        categorias = categoriaFacade.listarTodos();
        locais = localFacade.listarTodos();
        listar();
    }

    public void listar() {
        if (filtro == null || filtro.isBlank()) {
            plantas = facade.listarTodos();
        } else {
            plantas = facade.buscarPorNome(filtro.trim());
        }
    }

    public void limparFiltro() {
        filtro = null;
        listar();
    }

    public void novo() {
        planta = new Planta();
        categoriaId = null;
        localId = null;
    }

    public void editar(Planta selecionada) {
        planta = new Planta();
        planta.setId(selecionada.getId());
        planta.setNome(selecionada.getNome());
        planta.setPreco(selecionada.getPreco());
        categoriaId = selecionada.getCategoria().getId();
        localId = selecionada.getLocal().getId();
    }

    public void salvar() {
        try {
            Categoria categoria = categoriaFacade.buscarPorId(categoriaId);
            Local local = localFacade.buscarPorId(localId);

            if (planta.getId() == null) {
                planta.setCategoria(categoria);
                planta.setLocal(local);
                planta.setEstoque(0);
                facade.criar(planta);
            } else {
                Planta existente = facade.buscarPorId(planta.getId());
                existente.setNome(planta.getNome());
                existente.setPreco(planta.getPreco());
                existente.setCategoria(categoria);
                existente.setLocal(local);
                facade.editar(existente);
            }

            mensagem(FacesMessage.SEVERITY_INFO, "Planta salva com sucesso.");
            novo();
            categorias = categoriaFacade.listarTodos();
            locais = localFacade.listarTodos();
            listar();
            PrimeFaces.current().executeScript("PF('dlgPlanta').hide()");
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível salvar a planta.");
        }
    }

    public void excluir(Planta selecionada) {
        try {
            facade.remover(selecionada);
            mensagem(FacesMessage.SEVERITY_INFO, "Planta excluída.");
            listar();
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR,
                    "Não é possível excluir: existem movimentações de estoque dessa planta.");
        }
    }

    private void mensagem(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public List<Planta> getPlantas() {
        return plantas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Local> getLocais() {
        return locais;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}