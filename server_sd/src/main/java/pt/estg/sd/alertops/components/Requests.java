package pt.estg.sd.alertops.components;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Requests {
    private static int idCounter = 0;

    private int id;
    private String senderId;
    private Status status;
    private Type type;
    private LocalDateTime timestamp;

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public enum Type {
        OPERACAO_DE_EVACUACAO_EM_MASSA,
        ATIVACAO_DE_COMUNICACOES_DE_EMERGENCIA,
        DISTRIBUICAO_DE_RECURSOS_DE_EMERGENCIA
    }

    public Requests(String senderId, Type type) {
        this.id = ++idCounter;
        this.senderId = senderId;
        this.status = Status.PENDING;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
}