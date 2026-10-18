package br.com.floricultura.facade;

import br.com.floricultura.model.Local;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class LocalFacade extends AbstractFacade<Local> {

    @PersistenceContext(unitName = "floriculturaPU")
    private EntityManager em;

    public LocalFacade() {
        super(Local.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Local> buscarPorNome(String nome) {
        return em.createQuery(
                "SELECT l FROM Local l WHERE LOWER(l.nome) LIKE :nome ORDER BY l.nome",
                Local.class)
                .setParameter("nome", "%" + nome.toLowerCase() + "%")
                .getResultList();
    }
}