package it.paleocapa.mastroiannim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@SpringBootApplication(scanBasePackages={"it.paleocapa.mastroiannim"})
public class DockerServer {

    @Autowired
    private JavaBossBot bot;

	private static final Logger LOG = LoggerFactory.getLogger(DockerServer.class);

    public static void main(String[] args) {
        SpringApplication.run(DockerServer.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {

        LOG.info("hello world, I have just started up");
       
        try {
        	TelegramBotsApi botsApi = new TelegramBotsApi( DefaultBotSession.class );
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
