package br.com.floricultura.controller;

import br.com.floricultura.facade.MovimentacaoEstoqueFacade;
import br.com.floricultura.facade.PlantaFacade;
import br.com.floricultura.model.MovimentacaoEstoque;
import br.com.floricultura.model.Planta;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("historicoBean")
@ViewScoped
public class HistoricoBean implements Serializable {

    @EJB
    private PlantaFacade plantaFacade;

    @EJB
    private MovimentacaoEstoqueFacade movimentacaoFacade;

    private String nomePlanta;
    private Integer estoqueAtual;
    private List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

    public void carregar(Planta selecionada) {
        Planta planta = plantaFacade.buscarPorId(selecionada.getId());
        if (planta == null) {
            planta = selecionada;
        }
        nomePlanta = planta.getNome();
        estoqueAtual = planta.getEstoque();
        movimentacoes = movimentacaoFacade.listarPorPlanta(selecionada.getId());
    }

    public String getNomePlanta() {
        return nomePlanta;
    }

    public Integer getEstoqueAtual() {
        return estoqueAtual;
    }

    public List<MovimentacaoEstoque> getMovimentacoes() {
        return movimentacoes;
    }
}