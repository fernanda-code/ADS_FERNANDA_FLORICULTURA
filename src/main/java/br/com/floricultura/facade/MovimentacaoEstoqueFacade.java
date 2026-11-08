package br.com.floricultura.facade;

import br.com.floricultura.exception.NegocioException;
import br.com.floricultura.model.MovimentacaoEstoque;
import br.com.floricultura.model.Planta;
import br.com.floricultura.model.TipoMovimentacao;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class MovimentacaoEstoqueFacade extends AbstractFacade<MovimentacaoEstoque> {

    @PersistenceContext(unitName = "floriculturaPU")
    private EntityManager em;

    public MovimentacaoEstoqueFacade() {
        super(MovimentacaoEstoque.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void registrar(MovimentacaoEstoque mov) throws NegocioException {
        if (mov.getPlanta() == null || mov.getPlanta().getId() == null) {
            throw new NegocioException("Selecione uma planta.");
        }
        if (mov.getTipo() == null) {
            throw new NegocioException("Selecione o tipo da movimentação.");
        }
        if (mov.getQuantidade() == null || mov.getQuantidade() <= 0) {
            throw new NegocioException("A quantidade deve ser maior que zero.");
        }

        Planta planta = em.find(Planta.class, mov.getPlanta().getId(), LockModeType.PESSIMISTIC_WRITE);
        if (planta == null) {
            throw new NegocioException("Planta não encontrada.");
        }

        int atual = planta.getEstoque();
        if (mov.getTipo() == TipoMovimentacao.SAIDA) {
            if (mov.getQuantidade() > atual) {
                throw new NegocioException("Estoque insuficiente: a planta "
                        + planta.getNome() + " tem apenas " + atual + " unidade(s).");
            }
            planta.setEstoque(atual - mov.getQuantidade());
        } else {
            planta.setEstoque(atual + mov.getQuantidade());
        }

        mov.setPlanta(planta);
        mov.setData(LocalDateTime.now());
        em.persist(mov);
    }

    public List<MovimentacaoEstoque> buscarPorPlanta(String nome) {
        return em.createQuery(
                "SELECT m FROM MovimentacaoEstoque m WHERE LOWER(m.planta.nome) LIKE :nome ORDER BY m.data DESC",
                MovimentacaoEstoque.class)
                .setParameter("nome", "%" + nome.toLowerCase() + "%")
                .getResultList();
    }

    public List<MovimentacaoEstoque> listarPorPlanta(Long plantaId) {
        return em.createQuery(
                "SELECT m FROM MovimentacaoEstoque m WHERE m.planta.id = :id ORDER BY m.data DESC",
                MovimentacaoEstoque.class)
                .setParameter("id", plantaId)
                .getResultList();
    }
}