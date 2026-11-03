package br.com.floricultura.controller;

import br.com.floricultura.facade.PlantaFacade;
import br.com.floricultura.model.Planta;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("homeBean")
@ViewScoped
public class HomeBean implements Serializable {

    @EJB
    private PlantaFacade plantaFacade;

    private List<Planta> plantas;

    @PostConstruct
    public void init() {
        plantas = plantaFacade.buscarPorNome("");
    }

    public List<Planta> getPlantas() {
        return plantas;
    }
}