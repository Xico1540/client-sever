package pt.estg.sd.alertops.components;

import lombok.*;

@Data
@AllArgsConstructor
public class User {
    public enum Role {
        COORDENADOR_DE_EMERGENCIA,
        SUPERVISOR_DE_EMERGENCIA,
        AGENTE_DE_EMERGENCIA,
        CANDIDATO
    }

    private Integer id; // Alterado de int para Integer
    private String username;
    private String password;
    private String email;
    private Role role;
}