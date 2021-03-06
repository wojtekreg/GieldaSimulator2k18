/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GieldaSimulator2k18;

import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author wojtekreg
 */
public class Indeks implements Serializable{
    private String nazwa;
    private ArrayList<Spolka> listaSpolek = new ArrayList<>();
    private double lacznaWartosc;
    private GieldaPapierowWartosciowych gielda;

    @Override
    public String toString() {
        return nazwa;
    }

    /**
     *
     * @param gielda Gielda do ustawienia
     * @param nazwa nazwa do ustawienia
     */

    public Indeks(GieldaPapierowWartosciowych gielda, String nazwa){
        this.gielda = gielda;
        this.nazwa = nazwa;
    }

    /**
     *
     * @param spolkaObservableList wybrana lista spolek do dodania
     * @param gielda gielda do ustawienia
     */

    public Indeks(ObservableList<Spolka> spolkaObservableList, GieldaPapierowWartosciowych gielda){
        listaSpolek.addAll(spolkaObservableList);
        this.gielda = gielda;
        lacznaWartosc = 0;
        updateLacznaWartosc();
        nazwa = "Indeks giełdy " + gielda.getNazwa() + ": ";
        for (int i=0; i<listaSpolek.size(); i++){
            nazwa+=listaSpolek.get(i).getNazwa();
            if (i!=listaSpolek.size()-1)
                nazwa+=", ";
            listaSpolek.get(i).getListaIndeksow().add(this);
        }
    }

    /**
     * aktualizacja wartosci indeksu
     */

    protected void updateLacznaWartosc(){
        lacznaWartosc=0;
        for (int i=0; i<listaSpolek.size(); i++){
            lacznaWartosc+=listaSpolek.get(i).getKursAktualny();
        }
    }

    /**
     * Gets nazwa
     *
     * @return nazwa
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * Sets nazwa
     *
     * @param nazwa nazwa to set
     */
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    /**
     * Gets listaSpolek
     *
     * @return listaSpolek
     */
    public ArrayList<Spolka> getListaSpolek() {
        return listaSpolek;
    }

    /**
     * Gets lacznaWartosc
     *
     * @return lacznaWartosc
     */
    public double getLacznaWartosc() {
        return lacznaWartosc;
    }


    /**
     * Gets gielda
     *
     * @return gielda
     */
    public GieldaPapierowWartosciowych getGielda() {
        return gielda;
    }

}
