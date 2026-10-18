package br.com.floricultura.util;

import br.com.floricultura.exception.NegocioException;
import br.com.floricultura.facade.UsuarioFacade;
import br.com.floricultura.model.Perfil;
import br.com.floricultura.model.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.logging.Logger;

@Singleton
@Startup
public class AdminInicial {

    private static final Logger LOG = Logger.getLogger(AdminInicial.class.getName());

    @EJB
    private UsuarioFacade usuarioFacade;

    @PostConstruct
    public void criar() {
        if (usuarioFacade.contar() > 0) {
            return;
        }
        String senha = System.getProperty("floricultura.admin.senha");
        if (senha == null || senha.isBlank()) {
            LOG.warning("Nenhum usuario cadastrado e a propriedade floricultura.admin.senha nao foi definida.");
            return;
        }
        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail("admin@floricultura.com");
        admin.setPerfil(Perfil.ADMIN);
        try {
            usuarioFacade.cadastrar(admin, senha);
            LOG.info("Administrador inicial criado: admin@floricultura.com");
        } catch (NegocioException e) {
            LOG.warning(e.getMessage());
        }
    }
}