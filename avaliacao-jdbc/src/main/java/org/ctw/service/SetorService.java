package org.ctw.service;

import org.ctw.dao.SetorDAO;
import org.ctw.exception.EntidadeNaoEncontradaException;
import org.ctw.model.Setor;

import java.util.List;

public class SetorService {
    private final SetorDAO setorDAO;

    public SetorService(SetorDAO setorDAO) {
        this.setorDAO = setorDAO;
    }

    public Setor cadastrar(Setor setor) {
        validarSetor(setor);

        setor.setId(null);

        return setorDAO.inserir(setor);
    }

    public List<Setor> listarTodos() {
        return setorDAO.listarTodos();
    }

    public Setor buscarPorId(Integer id) {
        validarId(id);

        return setorDAO.buscarPorId(id)
                .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                                "Setor de ID "
                                        + id
                                        + " não encontrado."
                        )
                );
    }

    public List<Setor> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "Informe um nome para a pesquisa."
            );
        }

        return setorDAO.buscarPorNome(nome.trim());
    }

    public void atualizar(Setor setor) {
        if (setor == null) {
            throw new IllegalArgumentException(
                    "Os dados do setor são obrigatórios."
            );
        }

        validarId(setor.getId());
        validarSetor(setor);
        buscarPorId(setor.getId());

        if (!setorDAO.atualizar(setor)) {
            throw new EntidadeNaoEncontradaException(
                    "Setor não encontrado."
            );
        }
    }

    public void excluir(Integer id) {
        validarId(id);
        buscarPorId(id);

        if (setorDAO.possuiMotores(id)) {
            throw new IllegalStateException(
                    "O setor não pode ser excluído porque "
                            + "possui motores associados."
            );
        }

        if (!setorDAO.excluir(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Setor não encontrado."
            );
        }
    }

    private void validarSetor(Setor setor) {
        if (setor == null) {
            throw new IllegalArgumentException(
                    "Os dados do setor são obrigatórios."
            );
        }

        if (setor.getNome() == null
                || setor.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do setor é obrigatório."
            );
        }

        if (setor.getNome().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome deve possuir no máximo "
                            + "100 caracteres."
            );
        }

        if (setor.getLocalizacao() != null
                && setor.getLocalizacao()
                .trim().length() > 100) {

            throw new IllegalArgumentException(
                    "A localização deve possuir no máximo "
                            + "100 caracteres."
            );
        }

        setor.setNome(setor.getNome().trim());

        if (setor.getLocalizacao() != null) {
            setor.setLocalizacao(
                    setor.getLocalizacao().trim()
            );
        }
    }

    private void validarId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser um número positivo."
            );
        }
    }
}
