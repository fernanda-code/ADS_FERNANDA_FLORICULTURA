package br.com.floricultura.controller;

import br.com.floricultura.exception.NegocioException;
import br.com.floricultura.facade.MovimentacaoEstoqueFacade;
import br.com.floricultura.facade.PlantaFacade;
import br.com.floricultura.model.MovimentacaoEstoque;
import br.com.floricultura.model.Planta;
import br.com.floricultura.model.TipoMovimentacao;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import org.primefaces.PrimeFaces;

@Named("estoqueBean")
@ViewScoped
public class EstoqueBean implements Serializable {

    @EJB
    private MovimentacaoEstoqueFacade facade;

    @EJB
    private PlantaFacade plantaFacade;

    private List<MovimentacaoEstoque> movimentacoes;
    private List<Planta> plantas;
    private MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
    private Long plantaId;
    private String filtro;

    @PostConstruct
    public void init() {
        plantas = plantaFacade.listarTodos();
        listar();
    }

    public void listar() {
        movimentacoes = facade.buscarPorPlanta(filtro == null ? "" : filtro.trim());
    }

    public void limparFiltro() {
        filtro = null;
        listar();
    }

    public void novo() {
        movimentacao = new MovimentacaoEstoque();
        plantaId = null;
    }

    public void salvar() {
        try {
            Planta planta = plantaFacade.buscarPorId(plantaId);
            movimentacao.setPlanta(planta);
            facade.registrar(movimentacao);

            mensagem(FacesMessage.SEVERITY_INFO, "Movimentação registrada com sucesso.");
            novo();
            plantas = plantaFacade.listarTodos();
            listar();
            PrimeFaces.current().executeScript("PF('dlgMovimentacao').hide()");
        } catch (NegocioException e) {
            mensagem(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível registrar a movimentação.");
        }
    }

    private void mensagem(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public TipoMovimentacao[] getTipos() {
        return TipoMovimentacao.values();
    }

    public List<MovimentacaoEstoque> getMovimentacoes() {
        return movimentacoes;
    }

    public List<Planta> getPlantas() {
        return plantas;
    }

    public MovimentacaoEstoque getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(MovimentacaoEstoque movimentacao) {
        this.movimentacao = movimentacao;
    }

    public Long getPlantaId() {
        return plantaId;
    }

    public void setPlantaId(Long plantaId) {
        this.plantaId = plantaId;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}