package sasubiupgrade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Random;
import java.io.IOException;
import sasubiupgrade.model.PagamentoResidencia;
import javafx.print.PrinterJob;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ResidenciaController {

    @FXML
    private TableView<PagamentoResidencia> tabelaPagamentos;
    @FXML
    private TableColumn<PagamentoResidencia, String> colMes;
    @FXML
    private TableColumn<PagamentoResidencia, Double> colValor;
    @FXML
    private TableColumn<PagamentoResidencia, String> colReferencia;
    @FXML
    private TableColumn<PagamentoResidencia, String> colStatus;
    @FXML
    private Button pagarButton;
    @FXML
    private Button gerarRefButton;
    @FXML
    private Button imprimirButton;
    @FXML
    private Label estudanteLabel;
    @FXML
    private Label saldoLabel;

    private ObservableList<PagamentoResidencia> pagamentos;
    private String caminhoArquivo;
    private String nomeEstudante;
    private double saldoDevido;
    private Stage primaryStage;
    private static final Map<String, Integer> MES_NUMERO = new HashMap<>();

    static {
        MES_NUMERO.put("Janeiro", 1);
        MES_NUMERO.put("Fevereiro", 2);
        MES_NUMERO.put("Março", 3);
        MES_NUMERO.put("Abril", 4);
        MES_NUMERO.put("Maio", 5);
        MES_NUMERO.put("Junho", 6);
        MES_NUMERO.put("Julho", 7);
        MES_NUMERO.put("Agosto", 8);
        MES_NUMERO.put("Setembro", 9);
        MES_NUMERO.put("Outubro", 10);
        MES_NUMERO.put("Novembro", 11);
        MES_NUMERO.put("Dezembro", 12);
    }

    public void setEstudanteLogado(String nome, double saldo, Stage stage) {
        this.nomeEstudante = nome;
        this.saldoDevido = saldo;
        this.primaryStage = stage;
        this.caminhoArquivo = "pagamentos_" + nome.replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
        estudanteLabel.setText("Estudante: " + nome);
        saldoLabel.setText("Saldo Devido: " + String.format("%.2f", saldoDevido) + " EUR");
        initializePagamentos();
    }

    private void initializePagamentos() {
        try {
            pagamentos = FXCollections.observableArrayList(PagamentoResidencia.carregarPagamentos(caminhoArquivo));
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao carregar os dados: " + e.getMessage());
            alert.showAndWait();
            pagamentos = FXCollections.observableArrayList();
        }

        if (pagamentos.isEmpty()) {
            Random random = new Random();
            pagamentos.addAll(
                new PagamentoResidencia("Janeiro", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Fevereiro", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Março", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Abril", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Maio", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Junho", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Julho", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Agosto", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Setembro", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Outubro", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Novembro", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente"),
                new PagamentoResidencia("Dezembro", 150.00, String.valueOf(random.nextInt(900000) + 100000), "Pendente")
            );
            saveToFile();
        }

        calcularAtrasos();

        colMes.setCellValueFactory(cellData -> cellData.getValue().mesProperty());
        colValor.setCellValueFactory(cellData -> cellData.getValue().valorProperty().asObject());
        colReferencia.setCellValueFactory(cellData -> cellData.getValue().referenciaProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        tabelaPagamentos.setItems(pagamentos);

        tabelaPagamentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            pagarButton.setDisable(!isSelected);
            gerarRefButton.setDisable(!isSelected);
            imprimirButton.setDisable(!isSelected);
        });
    }

    @FXML
    public void initialize() {
        if (estudanteLabel != null) {
            estudanteLabel.setText("Estudante: Não logado");
        }
        if (saldoLabel != null) {
            saldoLabel.setText("Saldo Devido: 0.00 EUR");
        }
    }

    private void calcularAtrasos() {
        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();
        double novoSaldoDevido = 0.0;

        for (PagamentoResidencia pagamento : pagamentos) {
            int mesPagamento = MES_NUMERO.get(pagamento.mesProperty().get());
            if (mesPagamento < mesAtual && pagamento.statusProperty().get().equals("Pendente")) {
                novoSaldoDevido += pagamento.valorProperty().get();
            }
        }

        this.saldoDevido = novoSaldoDevido;
        saldoLabel.setText("Saldo Devido: " + String.format("%.2f", saldoDevido) + " EUR");
    }

    private String generateRandomReference() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    @FXML
    public void pagarSelecionado() {
        PagamentoResidencia selected = tabelaPagamentos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.statusProperty().get().equals("Pago")) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Este pagamento já foi realizado!");
                alert.showAndWait();
                return;
            }

            selected.setStatus("Pago");
            tabelaPagamentos.refresh();
            calcularAtrasos();
            saveToFile();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pagamento realizado com sucesso!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um pagamento para realizar!");
            alert.showAndWait();
        }
    }

    @FXML
    public void gerarReferencia() {
        PagamentoResidencia selected = tabelaPagamentos.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.statusProperty().get().equals("Pago")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Já pago");
                alert.showAndWait();
                return;
            }

            String novaReferencia = generateRandomReference();
            selected.referenciaProperty().set(novaReferencia);
            tabelaPagamentos.refresh();
            saveToFile();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Referência gerada: " + novaReferencia);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um pagamento para gerar referência!");
            alert.showAndWait();
        }
    }

    @FXML
    public void imprimirRecibo() {
        PagamentoResidencia selected = tabelaPagamentos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um pagamento para imprimir o recibo!");
            alert.showAndWait();
            return;
        }

        String reciboTexto = "===== RECIBO DE PAGAMENTO =====\n" +
                             "Estudante: " + nomeEstudante + "\n" +
                             "Mês: " + selected.mesProperty().get() + "\n" +
                             "Valor: " + String.format("%.2f", selected.valorProperty().get()) + " EUR\n" +
                             "Referência: " + selected.referenciaProperty().get() + "\n" +
                             "Status: " + selected.statusProperty().get() + "\n" +
                             "Data: " + java.time.LocalDate.now() + "\n" +
                             "Saldo Devido: " + String.format("%.2f", saldoDevido) + " EUR\n" +
                             "==============================\n";

        TextFlow recibo = new TextFlow(new Text(reciboTexto));
        recibo.setPrefWidth(300);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(tabelaPagamentos.getScene().getWindow())) {
            boolean success = job.printPage(recibo);
            if (success) {
                job.endJob();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Recibo impresso com sucesso!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Falha ao imprimir o recibo.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Impressão cancelada ou nenhuma impressora selecionada.");
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

    private void saveToFile() {
        try {
            PagamentoResidencia.salvarPagamentos(pagamentos, caminhoArquivo);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao salvar os dados: " + e.getMessage());
            alert.showAndWait();
        }
    }
}