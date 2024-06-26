// Burp Extension RepeaterNamer
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import java.util.List;

//This function is the entry point for the Burp extension.
//It sets the extension information, registers the context and menu bar menus,
// and calls the logic in SendAllToRepeater.
public class RepeaterNamer implements BurpExtension{
    @Override
    public void initialize(MontoyaApi api) {

        api.extension().setName("RepeaterNamer");
        api.logging().logToOutput("RepeaterNamer v0.2");
        api.logging().logToOutput("Created by: Theron Hawley");

        if(api.persistence().preferences().getBoolean("Repeaternamer.settings.init")== null){
            api.persistence().preferences().setBoolean("Repeaternamer.settings.init", true);
            api.persistence().preferences().setString("Repeaternamer.settings.Method", "GET, POST, PUT, DELETE, PATCH");//
            api.persistence().preferences().setString("Repeaternamer.settings.ExtentionDrop", "");//
            api.persistence().preferences().setString("Repeaternamer.settings.Extention", ".png, .svg, .js, .gif, .jpg, .png, .css");//
            api.persistence().preferences().setBoolean("Repeaternamer.settings.OneOf", true);//
            api.persistence().preferences().setBoolean("Repeaternamer.settings.EachPath", true);//
            api.persistence().preferences().setBoolean("Repeaternamer.settings.MostParam", true);
            //other stuff to set default settings
        }
        api.userInterface().registerContextMenuItemsProvider(new MenuItemProvider(api));

        BasicMenuItem alertEventItem = BasicMenuItem.basicMenuItem("Send Sitemap to Repeater").withAction(() -> {
            List<HttpRequestResponse> siteMapRR = api.siteMap().requestResponses();
            new SendAllToRepeater(api, siteMapRR);
                });

        Menu menu = Menu.menu("RepeaterNamer").withMenuItems(alertEventItem);
        api.userInterface().menuBar().registerMenu(menu);
        api.userInterface().registerSuiteTab("RepeaterNamer", new SettingsTab(api));
    }
}
