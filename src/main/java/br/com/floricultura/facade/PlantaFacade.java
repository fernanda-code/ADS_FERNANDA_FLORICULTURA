package br.com.floricultura.facade;

import br.com.floricultura.model.Planta;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PlantaFacade extends AbstractFacade<Planta> {

    @PersistenceContext(unitName = "floriculturaPU")
    private EntityManager em;

    public PlantaFacade() {
        super(Planta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Planta> buscarPorNome(String nome) {
        return em.createQuery(
                "SELECT p FROM Planta p WHERE LOWER(p.nome) LIKE :nome ORDER BY p.nome",
                Planta.class)
                .setParameter("nome", "%" + nome.toLowerCase() + "%")
                .getResultList();
    }
}