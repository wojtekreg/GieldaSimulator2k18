package GieldaSimulator2k18;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ListView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

public class AktywaInformationPreviewController {
    @FXML
    private ListView<String> InformationListView;
    @FXML
    private LineChart<String,Number> KursLineGraph;

    /**
     * Inicjalizacja sceny pokazującej informacje o aktywie z wykresem kursu w czasie
     *
     * @param aktywa Informacje o tym aktywie zostaną wyświetlone
     * @throws InterruptedException
     */

    public void initData(Aktywa aktywa) throws InterruptedException {
        ObservableList<String> list = FXCollections.observableArrayList();
        InformationListView.setItems(list);
        list.add("Nazwa: "+aktywa.getNazwa());
        list.add("Data pierwszej wyceny: "+aktywa.getDataPierwszejWyceny().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        list.add("Kurs aktualny: "+aktywa.getKursAktualny());
        list.add("Kurs otwarcia: "+aktywa.getKursOtwarcia());
        list.add("Kurs minimalny: "+aktywa.getKursMinimalny());
        list.add("Kurs maksymalny: "+aktywa.getKursMaksymalny());
        list.add("Wolumen: "+aktywa.getWolumen());
        list.add("Obroty: "+aktywa.getObroty());

        if (Surowiec.class.isInstance(aktywa)){
            Surowiec surowiec = (Surowiec) aktywa;
            list.add("Jednostka handlowa: "+surowiec.getJednostkaHandlowa());
            list.add("Waluta notowania: "+surowiec.getWalutaNotowania());
        }

        if (Spolka.class.isInstance(aktywa)){
            Spolka spolka = (Spolka) aktywa;
            list.add("Liczba akcji: "+spolka.getLiczbaAkcji());
            list.add("Zysk: "+spolka.getZysk());
            list.add("Przychód: "+spolka.getPrzychod());
            list.add("Kapitał zakładowy: "+spolka.getKapitalZakladowy());
            list.add("Kapitał własny: "+spolka.getKapitalWlasny());
            if (spolka.getListaIndeksow().size()>0) {
                list.add("Nalezy do indeksów:");
                for (int i=0;i<spolka.getListaIndeksow().size();i++){
                    list.add(spolka.getListaIndeksow().get(i).toString());
                }
            }
        }

        if (ParaWalut.class.isInstance(aktywa)){
            ParaWalut paraWalut = (ParaWalut) aktywa;
            list.add("Główna waluta: " + paraWalut.getGlownaWaluta());
            list.add("Druga waluta: " + paraWalut.getDrugaWaluta());
        }

        XYChart.Series series = new XYChart.Series();
        series.setName("kurs");
        for (int i=0;i<aktywa.getHistoriaKursu().size();i++) {
            series.getData().add(new XYChart.Data(aktywa.getHistoriaKursu().get(i).getCzas().format(DateTimeFormatter.ofPattern("dd-MM-yyyy\nHH:mm:ss")),aktywa.getHistoriaKursu().get(i).getKurs()));//aktywa.getHistoriaKursu().get(i).getCzas().toEpochSecond(ZoneOffset.UTC)));
        }
        KursLineGraph.getData().add(series);
    }
}
