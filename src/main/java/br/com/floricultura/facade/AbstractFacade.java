package br.com.floricultura.facade;

import jakarta.persistence.EntityManager;
import java.util.List;

public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void criar(T entidade) {
        getEntityManager().persist(entidade);
    }

    public T editar(T entidade) {
        return getEntityManager().merge(entidade);
    }

    public void remover(T entidade) {
        getEntityManager().remove(getEntityManager().merge(entidade));
    }

    public T buscarPorId(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> listarTodos() {
        return getEntityManager()
                .createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e ORDER BY e.id", entityClass)
                .getResultList();
    }
}