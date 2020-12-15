package net.kitcake.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Scanner;

public class Main extends ListenerAdapter {

    public static String TOKEN;
    public static OnlineStatus STATUS;
    public static String MESSAGE;
    public static String GUILD_ID;
    public static JDA JDA;

    public static void main(String[] args) {
        System.out.println("\n\n/\\ Programme pour envoyer un message à tous les membres d'un serveur /\\");
        System.out.print("/\\ Le bot doit être dans le serveur visé /\\\n\n");
        Scanner scanner = new Scanner(System.in);
        TOKEN = "";
        while(TOKEN.length() == 0) {
            System.out.print("Entrez le token du bot : ");
            TOKEN = scanner.nextLine();
        }
        STATUS = null;
        while(STATUS == null) {
            System.out.print("Quel statut en ligne le bot doit-il avoir ? (ONLINE, IDLE, DO_NOT_DISTURB, OFFLINE) : ");
            try {
                STATUS = OnlineStatus.valueOf(scanner.nextLine().toUpperCase());
            }catch(IllegalArgumentException ignored) {}
        }
        MESSAGE = "";
        while(MESSAGE.length() == 0) {
            System.out.print("Entrez le message a envoyé : ");
            MESSAGE = scanner.nextLine();
        }
        GUILD_ID = "";
        while(GUILD_ID.length() == 0) {
            System.out.print("Entrez l'identifiant du serveur à spammer : ");
            GUILD_ID = scanner.nextLine();
        }
        try {
            JDA = JDABuilder.create(TOKEN, GatewayIntent.GUILD_MEMBERS)
                    .setStatus(STATUS)
                    .setActivity(null)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                    .addEventListeners(new Main())
                    .build();
        } catch(LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        Guild guild = JDA.getGuildById(GUILD_ID);
        List<Member> members = guild.getMembers();
        int size = members.size() - 1;
        System.out.print("\nConnecté à [" + guild.getName() + "] avec " + size + " utilisateurs.\n");
        final int[] number = {0};
        for(Member member : members) {
            try {
                member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(MESSAGE).queue(message -> {
                    number[0]++;
                    if(number[0] == size) {
                        System.out.println("Terminé");
                        JDA.shutdown();
                    }
                }));
            }catch(UnsupportedOperationException ignored) {}
        }
    }
}
