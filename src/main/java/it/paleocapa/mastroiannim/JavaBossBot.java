package it.paleocapa.mastroiannim;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
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
	LinkedList<String> lista = null;
    List<String> menu = Arrays.asList("Pizza", "Panino", "Piadina", "Arancino", "Bonacina");
	public void onUpdateReceived(Update update) {
		
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
                case "/crea":
                    t = "Creo lista...";
                    if(lista == null){
                        lista = new LinkedList<>();
                        t += " Fatto.";
                    }else{
                        t += " Lista già creata in precedenza.";
                    }
                    message.setText(t);
                    break;
				case "panino":
					message.setText("Panino aggiunto alla lista");
					break;
				case "piadina":
					message.setText("Panino aggiunto alla lista");
					break;
				case "pizza":
					message.setText("Panino aggiunto alla lista");
					break;
				case "/menu":
					t = menu.stream().reduce("Menu:\n", (subtotal, element) -> subtotal +  " - " + element + "\n");
					message.setText(t);
					break;
                default:
                    message.setText("Scusa, non capisco");
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
