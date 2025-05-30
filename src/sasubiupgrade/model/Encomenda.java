package sasubiupgrade.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class Encomenda {
    private String nomeEstudante;
    private String id;
    private String dataRegistro;
    private String fonte; // "Local", "Externa", or "Recebida"

    public Encomenda(String nomeEstudante, String id, String dataRegistro, String fonte) {
        this.nomeEstudante = nomeEstudante;
        this.id = id;
        this.dataRegistro = dataRegistro;
        this.fonte = fonte;
    }

    public String getNomeEstudante() {
        return nomeEstudante;
    }

    public String getId() {
        return id;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Estudante: " + nomeEstudante + " | Registrado em: " + dataRegistro + " | Fonte: " + fonte;
    }

    // Load orders from CSV files
    public static List<Encomenda> carregarEncomendas(String caminhoLocal, String caminhoReportado) throws IOException {
        List<Encomenda> lista = new ArrayList<>();
        // Load local orders
        if (Files.exists(Paths.get(caminhoLocal))) {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoLocal), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    lista.add(parseEncomenda(linha));
                }
            }
        }
        // Load reported/received orders
        if (Files.exists(Paths.get(caminhoReportado))) {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoReportado), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    lista.add(parseEncomenda(linha));
                }
            }
        }
        return lista;
    }

    // Parse a line into an Encomenda object
    private static Encomenda parseEncomenda(String linha) {
        // Expected format: "ID: ENCxxx | Estudante: name | Registrado em: date | Fonte: source"
        String[] partes = linha.split(" \\| ");
        if (partes.length == 4) {
            String id = partes[0].replace("ID: ", "").trim();
            String nome = partes[1].replace("Estudante: ", "").trim();
            String data = partes[2].replace("Registrado em: ", "").trim();
            String fonte = partes[3].replace("Fonte: ", "").trim();
            return new Encomenda(nome, id, data, fonte);
        }
        return null;
    }

    // Save orders to CSV files
    public static void salvarEncomendas(List<Encomenda> encomendas, String caminhoLocal, String caminhoReportado) throws IOException {
        List<String> locais = new ArrayList<>();
        List<String> reportadas = new ArrayList<>();
        for (Encomenda e : encomendas) {
            if (e.getFonte().equals("Recebida")) {
                reportadas.add(e.toString());
            } else {
                locais.add(e.toString());
            }
        }
        Files.write(Paths.get(caminhoLocal), locais, StandardCharsets.UTF_8);
        Files.write(Paths.get(caminhoReportado), reportadas, StandardCharsets.UTF_8);
    }
}