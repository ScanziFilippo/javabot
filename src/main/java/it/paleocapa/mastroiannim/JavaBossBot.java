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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
	boolean amministratore = false;
	Double totale = 0.0;
	Double restoTotale = 0.0;
	LinkedList<String> lista = new LinkedList<String>();
	public void onUpdateReceived(Update update) {
		ReplyKeyboardMarkup tastiera = new ReplyKeyboardMarkup();
		long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
		String t;
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch(update.getMessage().getText().toLowerCase()){
                case "/start":
                    message.setText("Benvenuto! Come posso aiutarti?");
                    break;
                case "ciao":
                    message.setText("Ciao anche a te!");
                    break;
				case "/lista":
					if(lista.size() <= 0){
						message.setText("Lista vuota");
					}else{
						t = lista.stream().reduce("Lista:\n", (subtotal, element) -> subtotal +  " - " + element + "\n");
						t += "\nTotale pagato: €" + totale + "0\nTotale da pagare: €" + (totale - restoTotale) + "0\nResto totale: €" + (restoTotale) + "0"; 
						message.setText(t);
						break;
					}
					break;
				case "/menu":
					t = prezzi.keySet().stream().sorted().reduce("Menu:\n", (subtotal, element) -> subtotal +  " - " + element + "\n");
					message.setText(t);
					break;
				case "/azzerra":
					if(amministratore){
						lista = new LinkedList<String>();
						totale = 0.0;
						restoTotale = 0.0;
						message.setText("Lista azzerata");
					}else{
						message.setText("Devi essere amministratore per farlo");
					}
					break;
                default:
					if(update.getMessage().getText().toLowerCase().equals(segreto)){
						if(amministratore){
							amministratore = false;
							message.setText("Ora non sei più amministratore");
						}else{
							amministratore = true;
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
								lista.add(prodotto + " per " + update.getMessage().getFrom().getFirstName());
								totale += pagato;
								restoTotale += resto(prodotto, pagato);
							}else{
								message.setText("Male, il prezzo è €" + prezzi.get(prova.get(0)) + "0 ma vuoi pagare solo con €" + prova.get(1) + "0 😠");
							}
						}	
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
