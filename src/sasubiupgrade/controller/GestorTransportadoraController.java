package sasubiupgrade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sasubiupgrade.model.Transportadora;

public class GestorTransportadoraController {

    @FXML
    private TextField inputNomeEstudante;

    @FXML
    private TextField inputId;

    @FXML
    private TextField inputDataEntrega;

    @FXML
    private Button adicionarEntregaButton;

    private Stage primaryStage;
    private static final String CAMINHO_ARQUIVO = "entregas_transportadora.csv";

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        // No additional initialization needed
    }

    @FXML
    public void adicionarEntrega() {
        String nomeEstudante = inputNomeEstudante.getText().trim();
        String id = inputId.getText().trim();
        String dataEntrega = inputDataEntrega.getText().trim();

        if (nomeEstudante.isEmpty() || id.isEmpty() || dataEntrega.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, preencha todos os campos!");
            alert.showAndWait();
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            formatter.parse(dataEntrega);
            Transportadora.adicionarEntrega(nomeEstudante, id, dataEntrega, CAMINHO_ARQUIVO);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Entrega adicionada com sucesso!");
            alert.showAndWait();
            inputNomeEstudante.clear();
            inputId.clear();
            inputDataEntrega.clear();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao adicionar entrega: Formato de data inválido (use dd/MM/yyyy HH:mm). Detalhes: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void voltarMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sasubiupgrade/view/MainView.fxml"));
            Parent pagina = loader.load();
            MainController controller = loader.getController();
            controller.setStage(primaryStage);
            // No student state to pass since this is manager flow
            Scene scene = new Scene(pagina);
            primaryStage.setTitle("Sistema de Pagamentos - Menu Principal");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao voltar para a tela principal: " + e.getMessage());
            alert.showAndWait();
        }
    }
}