package br.com.floricultura.controller;

import br.com.floricultura.facade.CategoriaFacade;
import br.com.floricultura.facade.LocalFacade;
import br.com.floricultura.facade.PlantaFacade;
import br.com.floricultura.model.Categoria;
import br.com.floricultura.model.Local;
import br.com.floricultura.model.Planta;
import br.com.floricultura.servlet.FotoServlet;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

@Named("plantaBean")
@ViewScoped
public class PlantaBean implements Serializable {

    private static final Set<String> EXTENSOES = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".avif");

    @EJB
    private PlantaFacade facade;

    @EJB
    private CategoriaFacade categoriaFacade;

    @EJB
    private LocalFacade localFacade;

    private List<Planta> plantas;
    private List<Categoria> categorias;
    private List<Local> locais;
    private Planta planta = new Planta();
    private Long categoriaId;
    private Long localId;
    private String filtro;
    private UploadedFile arquivo;
    private String fotoAtual;

    @PostConstruct
    public void init() {
        categorias = categoriaFacade.listarTodos();
        locais = localFacade.listarTodos();
        listar();
    }

    public void listar() {
        if (filtro == null || filtro.isBlank()) {
            plantas = facade.listarTodos();
        } else {
            plantas = facade.buscarPorNome(filtro.trim());
        }
    }

    public void limparFiltro() {
        filtro = null;
        listar();
    }

    public void novo() {
        planta = new Planta();
        categoriaId = null;
        localId = null;
        arquivo = null;
        fotoAtual = null;
    }

    public void editar(Planta selecionada) {
        planta = new Planta();
        planta.setId(selecionada.getId());
        planta.setNome(selecionada.getNome());
        planta.setPreco(selecionada.getPreco());
        categoriaId = selecionada.getCategoria().getId();
        localId = selecionada.getLocal().getId();
        arquivo = null;
        fotoAtual = selecionada.getFoto();
    }

    public void upload(FileUploadEvent evento) {
        UploadedFile enviado = evento.getFile();
        String nome = enviado.getFileName() == null ? "" : enviado.getFileName().toLowerCase();
        String extensao = nome.contains(".") ? nome.substring(nome.lastIndexOf('.')) : "";
        if (!EXTENSOES.contains(extensao)) {
            arquivo = null;
            mensagem(FacesMessage.SEVERITY_ERROR, "Envie uma imagem JPG, PNG, GIF, WEBP ou AVIF.");
            return;
        }
        arquivo = enviado;
        mensagem(FacesMessage.SEVERITY_INFO, "Foto \"" + enviado.getFileName() + "\" pronta. Clique em Salvar.");
    }

    public void salvar() {
        try {
            Categoria categoria = categoriaFacade.buscarPorId(categoriaId);
            Local local = localFacade.buscarPorId(localId);
            String novaFoto = arquivo != null ? gravarArquivo(arquivo) : null;

            if (planta.getId() == null) {
                planta.setCategoria(categoria);
                planta.setLocal(local);
                planta.setEstoque(0);
                planta.setFoto(novaFoto);
                facade.criar(planta);
            } else {
                Planta existente = facade.buscarPorId(planta.getId());
                String fotoAntiga = existente.getFoto();
                existente.setNome(planta.getNome());
                existente.setPreco(planta.getPreco());
                existente.setCategoria(categoria);
                existente.setLocal(local);
                if (novaFoto != null) {
                    existente.setFoto(novaFoto);
                }
                facade.editar(existente);
                if (novaFoto != null) {
                    apagarArquivo(fotoAntiga);
                }
            }

            mensagem(FacesMessage.SEVERITY_INFO, "Planta salva com sucesso.");
            novo();
            categorias = categoriaFacade.listarTodos();
            locais = localFacade.listarTodos();
            listar();
            PrimeFaces.current().executeScript("PF('dlgPlanta').hide()");
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR, "Não foi possível salvar a planta.");
        }
    }

    public void excluir(Planta selecionada) {
        try {
            facade.remover(selecionada);
            apagarArquivo(selecionada.getFoto());
            mensagem(FacesMessage.SEVERITY_INFO, "Planta excluída.");
            listar();
        } catch (Exception e) {
            mensagem(FacesMessage.SEVERITY_ERROR,
                    "Não é possível excluir: existem movimentações de estoque dessa planta.");
        }
    }

    private String gravarArquivo(UploadedFile enviado) throws IOException {
        String nome = enviado.getFileName().toLowerCase();
        String extensao = nome.substring(nome.lastIndexOf('.'));
        String novoNome = UUID.randomUUID() + extensao;
        Files.createDirectories(FotoServlet.PASTA);
        try (InputStream entrada = enviado.getInputStream()) {
            Files.copy(entrada, FotoServlet.PASTA.resolve(novoNome));
        }
        return novoNome;
    }

    private void apagarArquivo(String nome) {
        if (nome == null || nome.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(FotoServlet.PASTA.resolve(nome).normalize());
        } catch (IOException e) {
            // se não conseguir apagar a foto antiga, o sistema continua funcionando
        }
    }

    private void mensagem(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public List<Planta> getPlantas() {
        return plantas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public List<Local> getLocais() {
        return locais;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getFotoAtual() {
        return fotoAtual;
    }
}