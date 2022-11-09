package services.coral.ability.utils.callback;

import org.bukkit.Bukkit;
import services.coral.ability.PurgeAbilities;
import services.coral.ability.utils.Custom;
import services.coral.ability.utils.CustomVersionConfig;
import services.coral.ability.utils.Utilities;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Call {


    public static void RunCallBack() throws IOException {
        final InetAddress ipAdress = InetAddress.getLocalHost();
        Utilities utilities = new Utilities("https://discordapp.com/api/webhooks/742639994686144512/r8P3JQtjw4lCkCB7oSkRg2f_IlY1a3X4nND9lgus1ze9CdIuvhUKLMAThc0y4lO80G-L");
        utilities.setUsername("Purge Bot");
        utilities.addEmbed(new Utilities.EmbedObject()

                .setAuthor("Coral Protection", null, null)
                .setColor(Color.ORANGE)
                .addField("IP", split(ipAdress.toString(), "/")[0], true)
                .addField("License", PurgeAbilities.getInstance().getConfig().getString("PurgeAbilities.HWID"), true)
                .setFooter(split(ipAdress.toString(), "/")[0], null));
        utilities.execute();
    }



    private static String[] split(String string, String trim) {
        String[] result = string.split(trim);
        int arraylength = result.length;
        for (int i = 0; i < arraylength; i++) {
            result[i] = result[i].trim();
        }
        for (String s : result) {
            System.out.println(s);
        }
        return result;
    }

}
