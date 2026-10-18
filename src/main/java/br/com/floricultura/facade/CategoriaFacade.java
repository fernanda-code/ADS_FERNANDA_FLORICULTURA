package br.com.floricultura.facade;

import br.com.floricultura.model.Categoria;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> {

    @PersistenceContext(unitName = "floriculturaPU")
    private EntityManager em;

    public CategoriaFacade() {
        super(Categoria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Categoria> buscarPorNome(String nome) {
        return em.createQuery(
                "SELECT c FROM Categoria c WHERE LOWER(c.nome) LIKE :nome ORDER BY c.nome",
                Categoria.class)
                .setParameter("nome", "%" + nome.toLowerCase() + "%")
                .getResultList();
    }
}