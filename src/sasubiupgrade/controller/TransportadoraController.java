package sasubiupgrade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sasubiupgrade.model.Encomenda;

public class TransportadoraController {

    @FXML
    private TextField nomeEstudanteField;
    @FXML
    private TextField transportadoraField;
    @FXML
    private TextField idTransportadoraField;

    private Stage primaryStage;
    private String nomeEstudante;
    private double saldoDevido;
    private static final String CAMINHO_ARQUIVO_REPORTADO = "encomendas_reportadas.csv";
    private int encomendaCounter = 1;

    public void setEstudanteLogado(String nome, double saldo, Stage stage) {
        this.nomeEstudante = nome;
        this.saldoDevido = saldo;
        this.primaryStage = stage;
    }

    @FXML
    public void registrarEncomendaTransportadora() {
        String nomeEstudante = nomeEstudanteField.getText().trim();
        String nomeTransportadora = transportadoraField.getText().trim();
        String idTransportadora = idTransportadoraField.getText().trim();

        if (nomeEstudante.isEmpty() || nomeTransportadora.isEmpty() || idTransportadora.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, preencha todos os campos!");
            alert.showAndWait();
            return;
        }

        String id = String.format("ENC%03d", encomendaCounter++);
        String dataRegistro = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Encomenda encomenda = new Encomenda(nomeEstudante, id, dataRegistro, "Externa: " + nomeTransportadora + " (ID: " + idTransportadora + ")");
        String encomendaStr = encomenda.toString();

        try {
            Files.createDirectories(Paths.get(CAMINHO_ARQUIVO_REPORTADO).getParent());
            Files.write(Paths.get(CAMINHO_ARQUIVO_REPORTADO), (encomendaStr + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Encomenda registrada com sucesso! ID: " + id);
            alert.showAndWait();
            nomeEstudanteField.clear();
            transportadoraField.clear();
            idTransportadoraField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao registrar encomenda: " + e.getMessage());
            alert.showAndWait();
        }
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
}