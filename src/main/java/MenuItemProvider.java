// Burp Extension RepeaterNamer
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//This class is used for the context menu
public class MenuItemProvider implements ContextMenuItemsProvider{

    private final MontoyaApi api;

    public MenuItemProvider(MontoyaApi api)
    {
        this.api = api;
    }

    //If the user rightclicks a request in the proxy, target, logger or repeater tabs, the context menu will show up under
    //Extensions/RepeaterNamer/Send request to repeater. When clicked the menu will send the request to SendAllToRepeater.
    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER, ToolType.REPEATER))
        {
            List<Component> menuItemList = new ArrayList<>();

            JMenuItem retrieveRequestItem = new JMenuItem("Send request to repeater");

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);
            List<HttpRequestResponse> responseList = new ArrayList<>();
            responseList.add(requestResponse);
            retrieveRequestItem.addActionListener(l -> new SendAllToRepeater(api, responseList));
            menuItemList.add(retrieveRequestItem);

            return menuItemList;
        }

        return null;
    }
}
