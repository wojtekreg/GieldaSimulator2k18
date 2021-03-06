package GieldaSimulator2k18;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.util.Random;

import static java.lang.Thread.sleep;

public class MainStageController {
    @FXML
    private Button OpenControlPanelButton;
    @FXML
    private ListView<Aktywa> MainListView;
    @FXML
    private TextField SerializationFilename;
    @FXML
    private Button DeserializeButton;

    private Swiat swiat;
    private Random random;
    private ObservableList<Aktywa> list;

    /**
     * Otwarcie panelu kontrolnego
     *
     * @throws IOException
     */
    @FXML
    private void OpenControlPanel() throws IOException {
        DeserializeButton.setDisable(true);
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ControlPanel.fxml"));
        stage.setTitle("Panel kontrolny");
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setScene(new Scene(loader.load()));

        ControlPanelController controller = loader.getController();
        controller.initData(swiat,list,random);

        stage.show();
    }

    /**
     * Otwarcie okna z informacjami o obiekcie
     *
     * @throws IOException
     * @throws InterruptedException
     */

    @FXML
    private void ShowElementInformation() throws IOException, InterruptedException {
        if (MainListView.getSelectionModel().getSelectedItem()!=null) {
            System.out.println("clicked on " + MainListView.getSelectionModel().getSelectedItem());
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AktywaInformationPreview.fxml"));
            stage.setTitle(MainListView.getSelectionModel().getSelectedItem() + " - informacje");
            stage.setMinWidth(640);
            stage.setMinHeight(480);
            stage.setScene(new Scene(loader.load()));

            AktywaInformationPreviewController controller = loader.getController();
            controller.initData(MainListView.getSelectionModel().getSelectedItem());

            stage.show();
        }
    }

    /**
     * Serializacja swiata po wczesniejszym zatrzymaniu symulacji i pozniejszym wznowieniu jej
     */

    @FXML
    private void SerializeSwiat() {
        try {
            Main.zatrymajSymulacje(swiat);
            sleep(200);
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(SerializationFilename.getText()));
            outputStream.writeObject(swiat);
            outputStream.flush();
            outputStream.close();
            for (int i=0; i<swiat.getListaSpolek().size(); i++)
                swiat.getListaSpolek().get(i).getThread().start();
            for (int i=0; i<swiat.getListaInwestorow().size(); i++)
                swiat.getListaInwestorow().get(i).getThread().start();
            for (int i=0; i<swiat.getListaFunduszyInwestycyjnych().size(); i++)
                swiat.getListaFunduszyInwestycyjnych().get(i).getThread().start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Wystąpił nieznany błąd");
            alert.show();
        }
    }

    /**
     * deserializacja swiata i uruchomienie symulacji
     */

    @FXML
    private void DeserializeSwiat() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(SerializationFilename.getText()));
            swiat = (Swiat) inputStream.readObject();
            inputStream.close();
            list.clear();
            for (int i = 0; i < swiat.getListaSpolek().size(); i++) {
                list.add(swiat.getListaSpolek().get(i));
                Thread thread = new Thread(swiat.getListaSpolek().get(i));
                swiat.getListaSpolek().get(i).setThread(thread);
                thread.start();
                Spolka.getNazwy().remove(swiat.getListaSpolek().get(i).getNazwa());
            }
            for (int i = 0; i < swiat.getListaSurowcow().size(); i++) {
                list.add(swiat.getListaSurowcow().get(i));
                Surowiec.getNazwy().remove(swiat.getListaSurowcow().get(i).getNazwa());
                Surowiec.getJednostkiHandlowe().remove(swiat.getListaSurowcow().get(i).getJednostkaHandlowa());
            }
            list.addAll(swiat.getListaParWalut());
            for (int i = 0; i < swiat.getListaWalut().size(); i++) {
                Waluta.getNazwy().remove(swiat.getListaWalut().get(i).getNazwa());
            }
            for (int i = 0; i < swiat.getListaGield().size(); i++) {
                GieldaPapierowWartosciowych.getNazwy().remove(swiat.getListaGield().get(i).getNazwa());
            }
            for (int i=0; i<swiat.getListaInwestorow().size(); i++){
                Thread thread = new Thread(swiat.getListaInwestorow().get(i));
                swiat.getListaInwestorow().get(i).setThread(thread);
                thread.start();
            }
            for (int i=0; i<swiat.getListaFunduszyInwestycyjnych().size(); i++){
                Thread thread = new Thread(swiat.getListaFunduszyInwestycyjnych().get(i));
                swiat.getListaFunduszyInwestycyjnych().get(i).setThread(thread);
                thread.start();
            }
        }
        catch (IOException ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Wystąpił problem przy otwieraniu pliku");
            alert.show();
        }
        catch (ClassNotFoundException ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Niepoprawny lub uszkodzony plik");
            alert.show();
        }
        catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Nieznany błąd, zaleca się uruchomienie ponownie aplikacji");
            alert.show();
        }
    }

    /**
     * Otwarcie okna z mozliwoscia narysowania wielu wykresow procentowych
     *
     * @throws IOException
     */

    @FXML
    private void OpenMultiLineChart() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MultiLineChart.fxml"));
        stage.setTitle("Wykres wielu aktywów");
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setScene(new Scene(loader.load()));

        MultiLineChartController controller = loader.getController();
        controller.initData(list);

        stage.show();
    }

    /**
     * Inicjalizacja glownego okna programu
     *
     * @param swiat swiat symulacji
     */

    public void initData(Swiat swiat){
        list = FXCollections.observableArrayList();
        MainListView.setItems(list);
        this.swiat = swiat;
        random = new Random();
    }
}
