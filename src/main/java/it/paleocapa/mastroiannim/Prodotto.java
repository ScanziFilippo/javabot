package it.paleocapa.mastroiannim;

public class Prodotto {
    String nomeUtente;
    String nome;
    String nomeProdotto;
    Double prezzo;
    Double resto;
    public Prodotto(String nomeProdotto , String nomeUtente, String nome, Double prezzo, Double resto){ 
        this.nomeProdotto = nomeProdotto;
        this.nomeUtente = nomeUtente;
        this.nome = nome;
        this.prezzo = prezzo;
        this.resto = resto;
    }
    String prodotto(){
        return nomeProdotto + " per " + nome;
    }
}
