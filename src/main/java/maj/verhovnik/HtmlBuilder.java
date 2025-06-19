package maj.verhovnik;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.Objects;


class HtmlBuilder{
    static String makeIndentation(int indentation){
        return "\t".repeat(Math.max(0, indentation));
    }

    static String stringifyAttributes(JsonNode attributes){
        StringBuilder builder = new StringBuilder();
        Iterator<String> keys = attributes.fieldNames();
        while(keys.hasNext()){
            builder.append(" ");
            String key = keys.next();
            if(Objects.equals(key, "style")){
                builder.append("style=\"");
                JsonNode styles =  attributes.get(key);
                Iterator<String> stylesKeys = styles.fieldNames();
                while(stylesKeys.hasNext()){
                    String stylesKey = stylesKeys.next();
                    String value = styles.get(stylesKey).asText();
                    builder.append(stylesKey).append(":").append(value).append(";");
                }
                builder.append("\"");
            }
            else{
                builder.append(key).append("=\"").append(attributes.get(key).asText()).append("\"");
            }
        }
        return builder.toString();
    }

    static String makeHeadTag(String tagName, JsonNode tag, int indentation){
        StringBuilder builder = new StringBuilder();
        String indentationString = makeIndentation(indentation);

        //Meta tags, saved differently
        if(Objects.equals(tagName, "meta")) {
            Iterator<String> keys = tag.fieldNames();
            while(keys.hasNext()){
                String key = keys.next();
                JsonNode value = tag.get(key);
                if(Objects.equals(key, "charset")) {
                    builder.append(indentationString).append("<meta charset=\"").append(value.asText()).append("\">\n");
                }
                else if (Objects.equals(key, "viewport")) {
                    JsonNode viewport = tag.get(key);
                    StringBuilder viewportBuilder = new StringBuilder();
                    Iterator<String> keys2 = viewport.fieldNames();
                    while(keys2.hasNext()) {
                        String viewPortKey = keys2.next();
                        String viewPortValue = viewport.get(viewPortKey).asText();
                        viewportBuilder.append(viewPortKey).append("=").append(viewPortValue).append(", ");
                    }
                    String viewportString = viewportBuilder.toString();
                    viewportString = viewportString.substring(0, viewportString.length() - 2);
                    builder.append(indentationString).append("<meta name=\"viewport\" content=\"").append(viewportString).append("\">\n");
                }
                else {
                    builder.append(indentationString).append("<meta name=\"").append(key).append("\" content=\"").append(value.asText()).append("\">\n");
                }
            }
        }
        //other tags
        else{
            builder.append(indentationString).append("<").append(tagName);
            Iterator<String> keys = tag.fieldNames();
            while(keys.hasNext()){
                String key = keys.next();
                String value = tag.get(key).asText();
                builder.append(" ").append(key).append("=\"").append(value).append("\"");
            }
            builder.append(">\n");
        }
        return builder.toString();
    }

    static String makeBodyTag(String tagname, JsonNode obj, int indentation){
        StringBuilder builder = new StringBuilder();
        String indentationString = makeIndentation(indentation);
        builder.append(indentationString).append("<").append(tagname);
        if(obj.has("attributes")) {
            JsonNode attributes = obj.get("attributes");
            String attributeString = stringifyAttributes(attributes);
            builder.append(attributeString);
        }
        builder.append(">\n");
        Iterator<String> keys = obj.fieldNames();
        while(keys.hasNext()) {
            String key = keys.next();
            JsonNode value = obj.get(key);
            if(value.isTextual()){
                String openingTag = "<" + key + ">";
                String closingTag = "</" + key + ">\n";
                builder.append(makeIndentation(indentation + 1)).append(openingTag).append(value.asText()).append(closingTag);
            }
            else if(value.isObject() && !Objects.equals("attributes", key)) {
                String data = makeBodyTag(key, value, indentation + 1);
                builder.append(data);
            }
            else if(value.isArray()){
                for(JsonNode item : value) {
                    builder.append(makeBodyTag(key, item, indentation + 1));
                }
            }
        }
        builder.append(indentationString).append("</").append(tagname).append(">\n");
        return builder.toString();
    }

    public static String makeHead(JsonNode head, int indentation){
        StringBuilder builder = new StringBuilder();
        String indentationString = makeIndentation(indentation);
        builder.append(indentationString).append("<head>\n");

        Iterator<String> keys = head.fieldNames();
        while(keys.hasNext()) {
            String key = keys.next();
            JsonNode value = head.get(key);
            if(value.isTextual()){
                builder.append(makeIndentation(indentation + 1)).append("<").append(key).append(">").append(value.asText()).append("</").append(key).append(">\n");
            }
            if(value.isObject()) {
                builder.append(makeHeadTag(key, value, indentation + 1));
            }
            if(value.isArray()) {
                for(JsonNode item : value) {
                    builder.append(makeHeadTag(key, item, indentation + 1));
                }
            }
        }
        builder.append(indentationString).append("</head>\n");

        return builder.toString();
    }

    public static String makeBody(JsonNode body, int indentation){
        return makeBodyTag("body", body, indentation);
    }
}
