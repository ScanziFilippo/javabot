package it.paleocapa.mastroiannim;

import java.util.*;
import static java.util.Map.*;

import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class JavaBossBot extends TelegramLongPollingBot {

	@Autowired
    private Environment env;

	private static final Logger LOG = LoggerFactory.getLogger(JavaBossBot.class);
	 
	@Value("${telegram.username}") 
	private String botUsername;
    
	@Value("${telegram.token}") 
	private String botToken;

	public String getBotUsername() {
		//LOG.info(env.getProperty("botUsername"));
		LOG.info(botUsername);
		//LOG.info(System.getenv("telegram.username"));
		return botUsername;
	}

	@Override
	@Deprecated
	public String getBotToken() {
		//LOG.info(env.getProperty("botToken"));
		LOG.info(botToken);
		//LOG.info(System.getenv("telegram.token"));
		return botToken;
	}
	public List<Object> traduci(String stringa){
		String prodotto = "";
		int i;
		String prezzo = "";
		for(i = 0; i < stringa.length(); i++){
			if(stringa.charAt(i) != ' '){
				prodotto += stringa.charAt(i);
			}else{
				break;
			}
		}
		for(i = i+1; i < stringa.length(); i++){
			prezzo += stringa.charAt(i);
		}
		try
		{
			double num = Double.parseDouble(prezzo);
			return Arrays.asList(prodotto.toLowerCase(), num);
		}
		catch (NumberFormatException e)
		{
			return Arrays.asList(prodotto.toLowerCase(), null);
		}
	}
	Map<String, Double> prezzi = Map.ofEntries(
			entry("brioche-cioccolato", 0.90), 
			entry("brioche-marmellata", 0.90), 
			entry("brioche-vuota", 0.90), 
			entry("panino-wurstel", 1.50), 
			entry("panino-cotoletta", 2.00), 
			entry("hamburger", 2.00), 
			entry("panino-gourmet", 2.00), 
			entry("piadina-cotto-fontina", 2.00),
			entry("speck-brie", 1.50), 
			entry("piadina-wurstel-patatine-mayo", 2.50), 
			entry("piadina-wurstel-patatine-ketchup", 2.50), 
			entry("piadina-cotoletta-patatine-mayo", 2.80), 
			entry("piadina-cotoletta-patatine-ketchup", 2.80),
			entry("pizza-piegata", 1.50), 
			entry("panzerotto", 2.00), 
			entry("calzone", 2.00), 
			entry("toast-patate", 3.00), 
			entry("ventaglio", 2.00), 
			entry("panino-cordon-bleau", 2.00), 
			entry("lattina-the-san-benedetto-pesca", 0.60), 
			entry("lattina-the-san-benedetto-limone", 0.60), 
			entry("lattina-pepsi-limone", 0.60), 
			entry("bottiglia-acqua-naturale", 1.00), 
			entry("bottiglia-acqua-frizzante", 1.00), 
			entry("bottiglia-the-san-benedetto-pesca", 1.00), 
			entry("bottiglia-the-san-benedetto-limone", 1.00), 
			entry("bottiglia-pepsi-limone", 1.00), 
			entry("bottiglia-pepsi", 1.00), 
			entry("menu-pizza-bibita", 6.00), 
			entry("menu-pasta", 5.00), 
			entry("menu-lasagna", 5.00), 
			entry("menu-insalata", 5.00), 
			entry("menu-cotoletta", 5.00), 
			entry("menu-riso", 5.00)
		);
	public Double resto(String prodotto, Double pagato){
		return pagato - prezzi.get(prodotto.toLowerCase());
	}

	@Value("${segreto}") 
	String segreto;
	HashMap<String, Classe> classi = new HashMap<>();
	HashMap<String, String> segreti = new HashMap<>();
	HashMap<String, Utente> utenti = new HashMap<>();
	public void onUpdateReceived(Update update) {
		long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
		String t;

		ReplyKeyboardMarkup tastiera = new ReplyKeyboardMarkup();
		tastiera.setSelective(true);
		tastiera.setResizeKeyboard(true);
		tastiera.setOneTimeKeyboard(false);

		KeyboardRow riga = new KeyboardRow();
		KeyboardRow riga2 = new KeyboardRow();
		KeyboardButton pulsante = new KeyboardButton("/accedi");
		
        if (update.hasMessage() && update.getMessage().hasText()) {
			if(utenti.get(update.getMessage().getFrom().getUserName()) != null && utenti.get(update.getMessage().getFrom().getUserName()).stato != null){
				switch (utenti.get(update.getMessage().getFrom().getUserName()).stato) {
					case creazione:
						if(classi.get(update.getMessage().getText().toUpperCase()) == null){
							classi.put(update.getMessage().getText().toUpperCase(), new Classe(update.getMessage().getText().toUpperCase()));
							utenti.get(update.getMessage().getFrom().getUserName()).classe = update.getMessage().getText().toUpperCase();
							message.setText("Classe " + update.getMessage().getText().toUpperCase() + " creata.\nOra crea il codice per accederci");
							utenti.get(update.getMessage().getFrom().getUserName()).stato = Stato.creazione2;
						}else{
							message.setText("Ora inserisci il codice per " + update.getMessage().getText().toUpperCase());
							utenti.get(update.getMessage().getFrom().getUserName()).classe = update.getMessage().getText().toUpperCase();
							utenti.get(update.getMessage().getFrom().getUserName()).stato = Stato.segreto;
						}
						ReplyKeyboardRemove tastieraa = new ReplyKeyboardRemove(true);
						message.setReplyMarkup(tastieraa);
						break;
					case creazione2:
						segreti.put(utenti.get(update.getMessage().getFrom().getUserName()).classe, update.getMessage().getText().toUpperCase());
						utenti.get(update.getMessage().getFrom().getUserName()).acceduto = true;
						utenti.get(update.getMessage().getFrom().getUserName()).stato = null;
						riga.add(new KeyboardButton("/menu"));
						riga.add(new KeyboardButton("/lista"));
						riga.add(new KeyboardButton("/azzerra"));	
						riga2.add(new KeyboardButton("/esci"));	
						riga.remove(pulsante);
						tastiera.setKeyboard(List.of(riga, riga2));
						message.setReplyMarkup(tastiera);
						message.setText("Fatto");
						break;
					case segreto:
						if(update.getMessage().getText().toUpperCase().equals(segreti.get(utenti.get(update.getMessage().getFrom().getUserName()).classe))){
							message.setText("Acceduto con successo");
							utenti.get(update.getMessage().getFrom().getUserName()).acceduto = true;
							riga.add(new KeyboardButton("/menu"));
							riga.add(new KeyboardButton("/lista"));
							riga.add(new KeyboardButton("/azzerra"));	
							riga2.add(new KeyboardButton("/esci"));	
							riga.remove(pulsante);
							tastiera.setKeyboard(List.of(riga, riga2));
							message.setReplyMarkup(tastiera);
							utenti.get(update.getMessage().getFrom().getUserName()).stato = null;
						}else{
							message.setText("Riprova");
						}
						break;
					default:
						break;
					}
			}else if(utenti.get(update.getMessage().getFrom().getUserName()) != null && utenti.get(update.getMessage().getFrom().getUserName()).acceduto){
				switch(update.getMessage().getText().toLowerCase()){
					case "/start":
						message.setText("Benvenuto! Come posso aiutarti?");
						message.setReplyMarkup(tastiera);
						break;
					case "ciao":
						message.setText("Ciao anche a te!");
						break;
					case "/esci":
						message.setText("Disconesso da " + utenti.get(update.getMessage().getFrom().getUserName()).classe);
						utenti.get(update.getMessage().getFrom().getUserName()).classe = null;
						utenti.get(update.getMessage().getFrom().getUserName()).acceduto = false;
						riga.add(new KeyboardButton("/accedi"));
						tastiera.setKeyboard(List.of(riga));
						message.setReplyMarkup(tastiera);
						break;
					case "/lista":
						if(classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).lista.size() <= 0){
							message.setText("Lista vuota");
						}else{
							t = classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).lista.stream().reduce("Lista:\n", (subtotal, element) -> subtotal +  " - " + element + "\n");
							t += "\nTotale pagato: €" + classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).totale + "0\nTotale da pagare: €" + (classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).totale - classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).restoTotale) + "0\nResto totale: €" + (classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).restoTotale) + "0"; 
							message.setText(t);
							break;
						}
						break;
					case "/menu":
						t = prezzi.keySet().stream().sorted().reduce("Menu:\n", (subtotal, element) -> subtotal +  " - " + element + "\n");
						message.setText(t);
						break;
					case "/azzerra":
						if(utenti.get(update.getMessage().getFrom().getUserName()).amministratore){
							classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).lista = new LinkedList<String>();
							classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).totale = 0.0;
							classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).restoTotale = 0.0;
							message.setText("Lista azzerata");
						}else{
							message.setText("Devi essere amministratore per farlo");
						}
						break;
					default:
						if(update.getMessage().getText().toLowerCase().equals(segreto)){
							if(utenti.get(update.getMessage().getFrom().getUserName()).amministratore){
								utenti.get(update.getMessage().getFrom().getUserName()).amministratore = false;
								message.setText("Ora non sei più amministratore");
							}else{
								utenti.get(update.getMessage().getFrom().getUserName()).amministratore = true;
								message.setText("Ora sei amministratore");
							}
						}
						else{
							List<Object> prova = traduci(update.getMessage().getText());
							String prodotto = prova.get(0).toString();
							if(prova.get(1) == null || prezzi.get(prova.get(0)) == null){
								message.setText("Scusa, non capisco");
							}else{
								Double pagato = Double.parseDouble(prova.get(1).toString());
								if(resto(prodotto, pagato) >= 0){
									message.setText("Bene, aggiungo " + prova.get(0) + "\nStai pagando €" + prova.get(1) + "0\nAvrai di resto €" + resto(prodotto, pagato)+"0");
									classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).lista.add(prodotto + " per " + update.getMessage().getFrom().getFirstName());
									classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).totale += pagato;
									classi.get(utenti.get(update.getMessage().getFrom().getUserName()).classe).restoTotale += resto(prodotto, pagato);
								}else{
									message.setText("Male, il prezzo è €" + prezzi.get(prova.get(0)) + "0 ma vuoi pagare solo con €" + prova.get(1) + "0 😠");
								}
							}	
						}
						riga.add(new KeyboardButton("/menu"));
						riga.add(new KeyboardButton("/lista"));
						riga.add(new KeyboardButton("/azzerra"));
						riga2.add(new KeyboardButton("/esci"));	
						tastiera.setKeyboard(List.of(riga, riga2));				
						message.setReplyMarkup(tastiera);
						break;
				}
			}    
			else{
				switch(update.getMessage().getText().toLowerCase()){
					case "/start":
						message.setText("Benvenuto! Come posso aiutarti?");
						if(utenti.get(update.getMessage().getFrom().getUserName()) == null){
							utenti.put(update.getMessage().getFrom().getUserName(), new Utente());
						}

						riga.add(pulsante);
						tastiera.setKeyboard(List.of(riga));				
						message.setReplyMarkup(tastiera);
						break;
					case "ciao":
						message.setText("Ciao anche a te!");
						break;
					case "/accedi":
						message.setText("Inserisci il nome della classe che vuoi creare o scegli quella a cui vuoi accedere");
						if(classi.size() == 0){
							ReplyKeyboardRemove tastieraa = new ReplyKeyboardRemove(true);
							message.setReplyMarkup(tastieraa);
						}else{
							classi.keySet().stream().sorted().forEach(c -> riga.add(new KeyboardButton(c)));
							tastiera.setKeyboard(List.of(riga));				
							message.setReplyMarkup(tastiera);	
						}
						utenti.get(update.getMessage().getFrom().getUserName()).stato = Stato.creazione;
						break;
				}        
			}
        }else{
            message.setText("Scusa, non capisco");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
	}
}
