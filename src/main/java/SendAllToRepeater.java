// Burp Extension RepeaterNamer
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

//SendAllToRepeater is the main logic for RepeaterNamer.
//It takes the burp api and a list of HttpRequestResponse objects.
//If the list contains multiple objects, the request/responses get sent through the ResponseFiltering function
// then get sent to the SendToRepeater function. This happens when the user requests the whole sitemap.
//When the list only contains one object, the request gets directly sent to the SendToRepeater function without any
// filtering. this happens when the user send a specific request.
public class SendAllToRepeater {
    public SendAllToRepeater (MontoyaApi api, List<HttpRequestResponse> requestResponseList){
        //if the sitemap has requests and the settings are set, continue with the main functionality
        if(requestResponseList != null && api.persistence().preferences().getBoolean("Repeaternamer.settings.init")!= null){
            int totalRtoRepeater = 0;
            //if more than one request is sent, filter the list then send them to repeater
            if (requestResponseList.size()>1){
                List<HttpRequest>requestList = ResponseFiltering(requestResponseList, api);
                for (HttpRequest request : requestList) {
                    new SendToRepeater(api, request);
                    totalRtoRepeater++;
                }
                //if there's only one request, just sent it to repeater, no filtering
            }else{
                new SendToRepeater(api, requestResponseList.get(0).request());
                totalRtoRepeater++;
            }
            //log # of request sent to the extension output
            api.logging().logToOutput(totalRtoRepeater + " Requests sent to Repeater.");
        }
    }

    //This function take the api (for logging) and the list of HttpRequestResponse and filters them down returning
    // a list of HttpRequest objects.
    //It filters out:
    // requests that have no reply or get a 404 response.
    // requests that are out of scope.
    // and all requests that are duplicates of another request based on method and path.
    // and depending on settings, certain file extension based checks are done, either checked per extension+path or just per extension
    private List<HttpRequest> ResponseFiltering(List<HttpRequestResponse> requestResponseList, MontoyaApi api) {

        int totalRequests = requestResponseList.size();
        int totalRRemoved = 0;

        Iterator<HttpRequestResponse> itrRR = requestResponseList.iterator();

        //First loop to remove 404, no response and not in scope.
        while(itrRR.hasNext()){
            HttpRequestResponse RR = itrRR.next();
            if(!RR.hasResponse() || (RR.response().statusCode() == 404) || !RR.request().isInScope()){
                itrRR.remove();
                totalRRemoved++;
            }
        }

        //Now that response based filtering is done, convert the HttpRequestResponse list to a HttpRequest list.
        List<HttpRequest> requestList = new java.util.ArrayList<>(requestResponseList.stream().map(HttpRequestResponse::request).toList());
        Iterator<HttpRequest> itrR = requestList.iterator();

        List<String> extensionFlag = new ArrayList<>();
        List<String> pathFlag = new ArrayList<>();

        //Second loop for the extension and path based filtering.
        while(itrR.hasNext()){

            HttpRequest request = itrR.next();
            //if the method is in the method list and not in the file extensions to skip continue , otherwise skip
            if (api.persistence().preferences().getString("Repeaternamer.settings.Method").contains(request.method()) && (Objects.equals(request.fileExtension(), "") || !api.persistence().preferences().getString("Repeaternamer.settings.ExtentionDrop").contains(request.fileExtension()))){
                //if the settings to check the extensions are on, there is an extension,
                // the extension is in the settings list of extensions to filter continue, otherwise more to the method path filter
                if (api.persistence().preferences().getBoolean("Repeaternamer.settings.OneOf") && !Objects.equals(request.fileExtension(), "") && api.persistence().preferences().getString("Repeaternamer.settings.Extention").contains(request.fileExtension()) ) {
                    //if the setting for getting the requests with extensions is set to get them per path, then record
                    // path - filename + extension and do filtering on that,
                    // otherwise just record extensions seen and do filtering on that
                    if(api.persistence().preferences().getBoolean("Repeaternamer.settings.EachPath")){
                        String path = request.pathWithoutQuery().substring(0, request.pathWithoutQuery().lastIndexOf('/'));
                        if(extensionFlag.contains(path+request.fileExtension())){
                            itrR.remove();
                            totalRRemoved++;
                        }else {
                            extensionFlag.add(path+request.fileExtension());
                        }
                    }else{
                        if(extensionFlag.contains(request.fileExtension())){
                            itrR.remove();
                            totalRRemoved++;
                        }else {
                            extensionFlag.add(request.fileExtension());
                        }
                    }
                //this filters based on method + path, if the program has already sent a request with the same
                    // method + path then it skips the current request
                }else {

                    if(pathFlag.contains(request.method() + request.pathWithoutQuery())) {
                        itrR.remove();
                        totalRRemoved++;
                    }else {
                        //extensionFlag.clear();
                        pathFlag.add(request.method() + request.pathWithoutQuery());
                    }

                }
            }else{
                itrR.remove();
                totalRRemoved++;
            }
        }

        //logging the amount of request filtered to extension output
        api.logging().logToOutput("Filtering of Sitemap done.");
        api.logging().logToOutput("Filtered " + totalRRemoved + " out of the " + totalRequests + " total requests acquired from the Sitemap.");

        return requestList;
    }

    //This function takes a request, names it based on some simple rules, and sends it to Repeater.
    private static class SendToRepeater {

        public SendToRepeater(MontoyaApi api, HttpRequest request) {

            String path = request.pathWithoutQuery();
            String[] pathItems = path.split("/");

            if(path.length() < 25) {
                api.repeater().sendToRepeater(request, request.method() + " " + path);
            }else if (pathItems[pathItems.length-1].length() < 25){

                api.repeater().sendToRepeater(request, request.method() + " .../" + pathItems[pathItems.length-1]);
            }else{
                api.repeater().sendToRepeater(request, request.method() + " .../" + pathItems[pathItems.length-1].substring(0,20)+ "..." +request.fileExtension());
            }
        }
    }


}
