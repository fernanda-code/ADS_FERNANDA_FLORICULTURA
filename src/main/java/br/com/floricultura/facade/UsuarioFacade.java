package br.com.floricultura.facade;

import br.com.floricultura.exception.NegocioException;
import br.com.floricultura.model.Usuario;
import br.com.floricultura.util.SenhaUtil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "floriculturaPU")
    private EntityManager em;

    public UsuarioFacade() {
        super(Usuario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return null;
        }
        List<Usuario> lista = em.createQuery(
                "SELECT u FROM Usuario u WHERE LOWER(u.email) = :email", Usuario.class)
                .setParameter("email", email.trim().toLowerCase())
                .getResultList();
        if (lista.isEmpty()) {
            return null;
        }
        Usuario usuario = lista.get(0);
        return SenhaUtil.conferir(senha, usuario.getSenha()) ? usuario : null;
    }

    public void cadastrar(Usuario usuario, String senhaPura) throws NegocioException {
        if (senhaPura == null || senhaPura.length() < 6) {
            throw new NegocioException("A senha deve ter pelo menos 6 caracteres.");
        }
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        Long existente = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class)
                .setParameter("email", usuario.getEmail())
                .getSingleResult();
        if (existente > 0) {
            throw new NegocioException("Já existe um usuário com esse e-mail.");
        }
        usuario.setSenha(SenhaUtil.gerarHash(senhaPura));
        em.persist(usuario);
    }

    public void atualizar(Usuario dados, String novaSenha) throws NegocioException {
        Usuario usuario = em.find(Usuario.class, dados.getId());
        if (usuario == null) {
            throw new NegocioException("Usuário não encontrado.");
        }
        String email = dados.getEmail().trim().toLowerCase();
        Long existente = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.email = :email AND u.id <> :id", Long.class)
                .setParameter("email", email)
                .setParameter("id", dados.getId())
                .getSingleResult();
        if (existente > 0) {
            throw new NegocioException("Já existe um usuário com esse e-mail.");
        }
        usuario.setNome(dados.getNome());
        usuario.setEmail(email);
        usuario.setPerfil(dados.getPerfil());
        if (novaSenha != null && !novaSenha.isBlank()) {
            if (novaSenha.length() < 6) {
                throw new NegocioException("A senha deve ter pelo menos 6 caracteres.");
            }
            usuario.setSenha(SenhaUtil.gerarHash(novaSenha));
        }
    }

    public long contar() {
        return em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult();
    }

    public List<Usuario> buscarPorNome(String nome) {
        return em.createQuery(
                "SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE :nome ORDER BY u.nome",
                Usuario.class)
                .setParameter("nome", "%" + nome.toLowerCase() + "%")
                .getResultList();
    }
}