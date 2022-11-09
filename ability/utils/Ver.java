package services.coral.ability.utils;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;
import services.coral.ability.PurgeAbilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class Ver {

    private JavaPlugin plugin;
    private boolean hasLoaded;

    public Ver(JavaPlugin plugin) {
        this.plugin = plugin;
        this.hasLoaded = false;
    }

    public void downloadDependency(String name) {
        File file = new File(plugin.getDataFolder().getParent(), name );

        try {
            URL url = new URL("http://license.coral.services/version/purgeablities.jar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");

            InputStream in = connection.getInputStream();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            downloadIfFail(name);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        loadPlugin(file);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The latest update was successfully downloaded!");
        this.hasLoaded = true;
    }


    public void downloadIfFail(String name) {
        File temp = new File(plugin.getDataFolder(), name + ".json");
        File file = new File(plugin.getDataFolder().getParent(), name );
        try {
            URL url = new URL("http://license.coral.services/version/purgeabilities.jar");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");

            InputStream in = connection.getInputStream();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(temp));

            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The latest update was unsuccessfully downloaded!");
            return;
        }

        Gson gson = new Gson();
        Resource resource;
        try (Reader reader = new FileReader(temp)) {
            resource = gson.fromJson(reader, Resource.class);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The latest update was unsuccessfully downloaded!");
            return;
        }

        try {
            InputStream in = getInputStream("http://license.coral.services/version/purgeabilities.jar");
            if (in == null) {
                System.out.println("NULL");
                return;
            }

            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The dependency " + ChatColor.AQUA + name + ChatColor.RED + " was not downloaded as the downloaded!");
            return;
        }

        loadPlugin(file);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "The dependency " + ChatColor.AQUA + name + ChatColor.GREEN + " was successfully downloaded!");
        this.hasLoaded = true;
    }

    public PurgeAbilities getPlugin() {
        return PurgeAbilities.getInstance();
    }

    public boolean hasLoaded() {
        return hasLoaded;
    }

    private void loadPlugin(File file) {
        try {
            Bukkit.getPluginManager().loadPlugin(file);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
        }
    }

    public static InputStream getInputStream(String url) {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(15000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        HttpClient client = new HttpClient();
        client.getParams().setParameter("Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0", "Chrome");
        Cookie[] cookies = client.getState().getCookies();

        for (Cookie temp : cookies) {
            com.gargoylesoftware.htmlunit.util.Cookie cookie = new com.gargoylesoftware.htmlunit.util.Cookie(temp.getDomain(), temp.getName(), temp.getValue(), temp.getPath(), temp.getExpiryDate(), temp.getSecure());
            webClient.getCookieManager().addCookie(cookie);
        }

        InputStream inputStream;
        try {
            WebRequest wr = new WebRequest(new URL(url), HttpMethod.GET);
            Page page = webClient.getPage(wr);
            if (!(page instanceof HtmlPage)) {
                System.out.println("NO HTML");
                return null;
            }

            if (!((HtmlPage) page).asXml().contains("DDoS protection by Cloudflare")) {
                System.out.println("NOT CLOUDFARE");
                return null;
            }

            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            inputStream = webClient.getCurrentWindow().getEnclosedPage().getWebResponse().getContentAsStream();
            System.out.println(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return inputStream;
    }
}