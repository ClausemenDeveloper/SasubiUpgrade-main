package Transportadora.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Entrega {
    private String id;
    private String nomeEstudante;
    private String dataRegistro;
    private String status; // "Pendente", "Enviada", "Cancelada"
    private String transportadoraId;

    public Entrega(String id, String nomeEstudante, String transportadoraId) {
        this.id = id;
        this.nomeEstudante = nomeEstudante;
        this.transportadoraId = transportadoraId;
        this.dataRegistro = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.status = "Pendente";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getNomeEstudante() {
        return nomeEstudante;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransportadoraId() {
        return transportadoraId;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + 
               " | Status: " + status + " | Transportadora ID: " + transportadoraId;
    }

    // Format for saving to sasubiupgrade's encomendas_reportadas.csv
    public String toSasubiFormat() {
        return "ID: " + id + " | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + 
               " | Fonte: Externa: Transportadora (ID: " + transportadoraId + ")";
    }
}