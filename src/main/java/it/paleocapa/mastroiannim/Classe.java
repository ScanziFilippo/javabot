package it.paleocapa.mastroiannim;

import java.util.LinkedList;

public class Classe {
    Double totale = 0.0;
	Double restoTotale = 0.0;
	LinkedList<Prodotto> lista = new LinkedList<Prodotto>();
    String classe;
    public Classe(String classe){
        this.classe = classe;
    }
}
