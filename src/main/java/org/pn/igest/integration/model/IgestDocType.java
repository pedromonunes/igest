package org.pn.igest.integration.model;

import lombok.Getter;

@Getter
public enum IgestDocType {
	FT("Fatura"),
    FS("Fatura Simplificada"),
    FR("Fatura-Recibo"),
    NC("Nota de Crédito"),
    ND("Nota de Débito");
	
    private final String descricao;

    IgestDocType(String descricao) {
        this.descricao = descricao;
    }
}