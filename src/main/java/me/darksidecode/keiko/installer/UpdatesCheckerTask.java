/*
 * Copyright 2020 DarksideCode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.darksidecode.keiko.installer;

import me.darksidecode.kantanj.networking.SampleUserAgents;
import me.darksidecode.keiko.KeikoPluginInspector;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdatesCheckerTask implements Runnable {

    private static final String SPIGOT_API_URL         = "https://api.spigotmc.org";
    private static final String SPIGOT_RESOURCES_URL   = "https://www.spigotmc.org/resources/";
    private static final String KEIKO_SPIGOT_PLUGIN_ID = "66278";

    @Override
    public void run() {
        try {
            // FIXME: Cannot use kantanj because of BungeeCord compatibility issues:
            //     java.lang.NoSuchMethodError: org.apache.commons.io.IOUtils.readLines(Ljava/io/InputStream;Ljava/nio/charset/Charset;)Ljava/util/List;
            //        at me.darksidecode.kantanj.networking.Networking$Http.readResponseAndDisconnect(Networking.java:130)
            //        at me.darksidecode.kantanj.networking.Networking$Http.get(Networking.java:84)
//            GetHttpRequest request = new GetHttpRequest().
//                    baseUrl(SPIGOT_API_URL).
//                    path("legacy/update.php?resource=" + KEIKO_SPIGOT_PLUGIN_ID).
//                    userAgent(SampleUserAgents.MOZILLA_WIN_NT).
//                    asGetRequest();

            URL url = new URL(SPIGOT_API_URL + "/legacy/update.php?resource=" + KEIKO_SPIGOT_PLUGIN_ID);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestProperty("User-Agent", SampleUserAgents.MOZILLA_WIN_NT);
            con.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String latestVersion = reader.readLine(); // Spigot should only return one line

            reader.close();
            con.disconnect();

            // (see above) String latestVersion = Networking.Http.get(request);
            String installedVersion = KeikoPluginInspector.getVersion();

            if (!(installedVersion.equals(latestVersion))) {
                // A newer version of Keiko was found on SpigotMC.
                KeikoPluginInspector.info(" ");
                KeikoPluginInspector.info(KeikoPluginInspector.LINE);
                KeikoPluginInspector.info("  (i) A new version of Keiko is available!");
                KeikoPluginInspector.info("      Installed: %s, latest: %s", installedVersion, latestVersion);
                KeikoPluginInspector.info("      Make sure to update as soon as possible:");
                KeikoPluginInspector.info("      %s", SPIGOT_RESOURCES_URL + KEIKO_SPIGOT_PLUGIN_ID);
                KeikoPluginInspector.info(KeikoPluginInspector.LINE);
                KeikoPluginInspector.info(" ");
            }
        } catch (Exception ex) {
            KeikoPluginInspector.warn(" ");
            KeikoPluginInspector.warn(KeikoPluginInspector.LINE);
            KeikoPluginInspector.warn("  (!) Failed to check for updates! Is everything OK with");
            KeikoPluginInspector.warn("      your Internet connection? P.S.: you can disable updates");
            KeikoPluginInspector.warn("      checking in .../Keiko/config/global.yml (not recommended).");
            KeikoPluginInspector.warn(KeikoPluginInspector.LINE);
            KeikoPluginInspector.warn(" ");

            ex.printStackTrace();
        }
    }

}
