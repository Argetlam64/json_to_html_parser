package maj.verhovnik;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;


class JsonParser{
    static JsonNode readJsonFile(String filename) throws IOException {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(new File(filename));
        }
        catch(Exception e){
            e.printStackTrace();
            throw new IOException();
        }
    }

    public static String getHtmlFromFile(String filename) throws IOException {
        try{
            JsonNode json = readJsonFile(filename);
            JsonNode head = json.get("head");
            JsonNode body = json.get("body");
            StringBuilder builder = new StringBuilder();
            String doctype = json.get("doctype").asText();

            builder.append("<!DOCTYPE ").append(doctype).append(">\n").append("<html");
            if(json.has("language")){
                builder.append(" lang=\"").append(json.get("language").asText()).append("\"");
            }
            builder.append(">\n");

            String headString = HtmlBuilder.makeHead(head, 1);
            String bodyString = HtmlBuilder.makeBody(body, 1);

            builder.append(headString);
            builder.append(bodyString);
            builder.append("</html>\n");
            return builder.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            throw new IOException();
        }
    }
}