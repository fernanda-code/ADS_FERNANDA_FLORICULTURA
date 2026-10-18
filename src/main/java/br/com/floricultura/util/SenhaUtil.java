package br.com.floricultura.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SenhaUtil {

    private static final int ITERACOES = 65536;
    private static final int TAMANHO_HASH = 256;

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = calcular(senha, salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean conferir(String senha, String armazenado) {
        if (senha == null || armazenado == null || !armazenado.contains(":")) {
            return false;
        }
        String[] partes = armazenado.split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        byte[] calculado = calcular(senha, salt);
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] calcular(String senha, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, ITERACOES, TAMANHO_HASH);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao processar a senha", e);
        }
    }
}