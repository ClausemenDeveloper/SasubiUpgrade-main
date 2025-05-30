package sasubiupgrade.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class PedidoCantina {
    private String nomeEstudante;
    private String item;
    private double preco;

    public PedidoCantina(String nomeEstudante, String item, double preco) {
        this.nomeEstudante = nomeEstudante;
        this.item = item;
        this.preco = preco;
    }

    public String getNomeEstudante() {
        return nomeEstudante;
    }

    public String getItem() {
        return item;
    }

    public double getPreco() {
        return preco;
    }

    @Override
    public String toString() {
        return String.format("Nome: %s | Pedido: %s | Preço: %.2f EUR", nomeEstudante, item, preco);
    }

    // Load orders from CSV
    public static List<PedidoCantina> carregarPedidos(String caminho) throws IOException {
        List<PedidoCantina> lista = new ArrayList<>();
        if (Files.exists(Paths.get(caminho))) {
            List<String> linhas = Files.readAllLines(Paths.get(caminho), StandardCharsets.UTF_8);
            for (String linha : linhas) {
                if (!linha.trim().isEmpty()) {
                    PedidoCantina pedido = parsePedido(linha);
                    if (pedido != null) {
                        lista.add(pedido);
                    }
                }
            }
        }
        return lista;
    }

    // Parse a line into a PedidoCantina object
    private static PedidoCantina parsePedido(String linha) {
        // Expected format: "Nome: name | Pedido: item | Preço: price EUR"
        String[] partes = linha.split(" \\| ");
        if (partes.length == 3) {
            String nome = partes[0].replace("Nome: ", "").trim();
            String item = partes[1].replace("Pedido: ", "").trim();
            String precoStr = partes[2].replace("Preço: ", "").replace(" EUR", "").trim();
            try {
                double preco = Double.parseDouble(precoStr);
                return new PedidoCantina(nome, item, preco);
            } catch (NumberFormatException e) {
                System.out.println("Erro ao converter preço: " + precoStr);
            }
        }
        return null;
    }

    // Save orders to CSV
    public static void salvarPedidos(List<PedidoCantina> pedidos, String caminho) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (PedidoCantina p : pedidos) {
            linhas.add(p.toString());
        }
        Files.write(Paths.get(caminho), linhas, StandardCharsets.UTF_8);
    }
}