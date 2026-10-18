package br.com.floricultura.model;

public enum Perfil {
    ADMIN("Administrador"),
    FUNCIONARIO("Funcionário");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}