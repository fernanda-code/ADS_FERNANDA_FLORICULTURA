package br.com.floricultura.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@WebServlet("/foto/*")
public class FotoServlet extends HttpServlet {

    public static final Path PASTA = Paths.get(System.getProperty("user.home"), "floricultura-uploads");

    private static final Map<String, String> TIPOS = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "webp", "image/webp",
            "avif", "image/avif");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String info = req.getPathInfo();
        if (info == null || info.length() < 2) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nome = Paths.get(info.substring(1)).getFileName().toString();
        Path arquivo = PASTA.resolve(nome).normalize();
        if (!arquivo.startsWith(PASTA) || !Files.isRegularFile(arquivo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String extensao = nome.contains(".")
                ? nome.substring(nome.lastIndexOf('.') + 1).toLowerCase() : "";
        String tipo = TIPOS.get(extensao);
        if (tipo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setContentType(tipo);
        resp.setContentLengthLong(Files.size(arquivo));
        resp.setHeader("Cache-Control", "private, max-age=86400");
        Files.copy(arquivo, resp.getOutputStream());
    }
}