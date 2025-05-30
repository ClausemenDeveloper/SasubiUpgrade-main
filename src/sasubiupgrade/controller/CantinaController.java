package sasubiupgrade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

public class CantinaController {

    @FXML
    private TableView<MenuItem> tabelaMenu;
    @FXML
    private TableColumn<MenuItem, String> colunaItem;
    @FXML
    private TableColumn<MenuItem, Double> colunaPreco;
    @FXML
    private TextField inputNome;
    @FXML
    private TextField inputPedido;
    @FXML
    private ListView<String> filaPedidos;

    private ObservableList<MenuItem> menuItems;
    private ObservableList<String> pedidos;
    private static final String CAMINHO_ARQUIVO = "pedidos.csv";
    private String nomeEstudante; // Armazena o nome do estudante logado
    private double saldoDevido;   // Armazena o saldo devido do estudante
    private Stage primaryStage;   // Referência ao Stage principal

    public static class MenuItem {
        private final String nome;
        private final double preco;

        public MenuItem(String nome, double preco) {
            this.nome = nome.trim();
            this.preco = preco;
        }

        public String getNome() {
            return nome;
        }

        public double getPreco() {
            return preco;
        }
    }

    // Método para configurar o estado do estudante e o stage
    public void setEstudanteLogado(String nome, double saldo, Stage stage) {
        this.nomeEstudante = nome;
        this.saldoDevido = saldo;
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        menuItems = FXCollections.observableArrayList(
            new MenuItem("Sanduíche Natural", 5.00),
            new MenuItem("Sopa do Dia", 3.50),
            new MenuItem("Refrigerante", 2.00),
            new MenuItem("Bolo de Chocolate", 4.00)
        );

        colunaItem.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNome()));
        colunaPreco.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPreco()).asObject());

        tabelaMenu.setItems(menuItems);
        pedidos = FXCollections.observableArrayList();
        loadFromFile();
        filaPedidos.setItems(pedidos);
    }

    @FXML
    public void fazerPedido() {
        String nome = inputNome.getText().trim();
        String pedidoInput = inputPedido.getText().trim().toLowerCase();

        if (nome.isEmpty() || pedidoInput.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, preencha todos os campos!");
            alert.showAndWait();
            return;
        }

        String pedido = pedidoInput;
        double preco = 0.0;
        
        try {
            int numero = Integer.parseInt(pedidoInput);
            if (numero >= 0 && numero < menuItems.size()) {
                pedido = menuItems.get(numero).getNome().toLowerCase();
                preco = menuItems.get(numero).getPreco();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, 
                    "Número inválido! Use 0 a " + (menuItems.size() - 1) + " ou o nome do item.");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            MenuItem itemSelecionado = menuItems.stream()
                    .filter(item -> item.getNome().toLowerCase().equals(pedidoInput))
                    .findFirst()
                    .orElse(null);
                
            if (itemSelecionado == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, 
                    "Item inválido! Use o nome (ex.: sanduíche natural) ou número (0 a " + (menuItems.size() - 1) + ").");
                alert.showAndWait();
                return;
            }
            pedido = itemSelecionado.getNome().toLowerCase();
            preco = itemSelecionado.getPreco();
        }

        String orderSummary = String.format("Nome: %s | Pedido: %s | Preço: %.2f EUR", nome, pedido, preco);
        pedidos.add(orderSummary);
        saveToFile();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pedido feito com sucesso!");
        alert.showAndWait();

        inputNome.clear();
        inputPedido.clear();
    }

    @FXML
    public void voltarParaMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sasubiupgrade/view/MainView.fxml"));
            Parent pagina = loader.load();
            MainController controller = loader.getController();
            controller.setStage(primaryStage);
            controller.setEstudanteLogado(nomeEstudante, saldoDevido);
            Scene scene = new Scene(pagina);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao voltar para a página principal: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadFromFile() {
        try {
            if (Files.exists(Paths.get(CAMINHO_ARQUIVO))) {
                pedidos.setAll(Files.readAllLines(Paths.get(CAMINHO_ARQUIVO), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao carregar os pedidos: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void saveToFile() {
        try {
            Files.createDirectories(Paths.get(CAMINHO_ARQUIVO).getParent());
            Files.write(Paths.get(CAMINHO_ARQUIVO), 
                (pedidos.get(pedidos.size() - 1) + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar os pedidos: " + e.getMessage());
            alert.showAndWait();
        }
    }
}