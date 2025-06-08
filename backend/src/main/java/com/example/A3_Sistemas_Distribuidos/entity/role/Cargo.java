package com.example.A3_Sistemas_Distribuidos.entity.role;

import com.example.A3_Sistemas_Distribuidos.exception.RoleException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Cargo {
    ATENDENTE,
    SUPERVISOR,
    GERENTE;

    @JsonCreator
    public static Cargo fromString(String value) {
        if (value != null) {
            switch (value.toUpperCase()) {
                case "ATENDENTE":
                    return ATENDENTE;

                case "SUPERVISOR":
                    return SUPERVISOR;

                case "GERENTE":
                    return GERENTE;
            }
        }
        throw new RoleException("Valor inválido para Cargo: " + value);
    }
}
