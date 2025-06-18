package org.example;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

class Tag{
    public String tagName;
    public String tagValue;
    Tag(String tagName, String tagValue){
        this.tagName = tagName;
        this.tagValue = tagValue;
    }
    public void addAttribute(String key, String value){

    }
}

public class Main {
    public static String getFileContents(String path) throws IOException {
        try {
            return Files.readString(Path.of(path));
            //System.out.println(jsonString);
            //return jsonString;
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            throw new IOException("File not found");
        }
    }

    public static String makeOffset(int offset){
        StringBuilder builder = new StringBuilder();
        builder.append("\t".repeat(Math.max(0, offset)));
        return builder.append(builder).toString();
    }

    public static String stringifyAttributes(JSONObject attributes){
        StringBuilder builder = new StringBuilder("style=\"");
        for(String key : attributes.keySet()){
            if(Objects.equals(key, "style")){
                JSONObject styles =  attributes.getJSONObject(key);
                for(String style : styles.keySet()){
                    String value = styles.getString(style);
                    builder.append(style).append(":").append(value).append("; ");
                }
                builder.append("\"");
            }
            else{
                builder.append(key).append("=\"").append(attributes.get(key)).append("\" ");
            }
        }
        return builder.toString();
    }

    public static String makeTagBody(String tagname, JSONObject obj, int offset){
        StringBuilder builder = new StringBuilder();
        String offsetString = makeOffset(offset);
        builder.append(offsetString).append("<").append(tagname).append(" ");

        if(obj.has("attributes")) {
            JSONObject attributes = (JSONObject) obj.get("attributes");
            String attributeString = stringifyAttributes(attributes);
            builder.append(attributeString);
        }
        builder.append(">\n");
        for(String key : obj.keySet()) {
            Object value = obj.get(key);
            if(value instanceof String) {
                String openingTag = "<" + key + ">";
                String closingTag = "</" + key + ">\n";
                builder.append(offsetString).append(openingTag).append(value).append(closingTag);
            }
            else if(value instanceof JSONObject && !Objects.equals("attributes", key)) {
                String data = makeTagBody(key, (JSONObject) value, offset + 1);
                builder.append(data);
            }
            else if(value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for(int i = 0; i < array.length(); i++){
                    String data = makeTagBody(key, (JSONObject) array.get(i), offset+1);
                    builder.append(data);
                }
            }
        }

        builder.append(offsetString).append("</").append(tagname).append(">\n");
        return builder.toString();
    }

    public static String makeBody(JSONObject body, int offset) {
        StringBuilder builder = new StringBuilder();
        String data = makeTagBody("body", body, offset);
        builder.append(data);
        return builder.toString();
    }

    public static String makeTagHead(String tagName, JSONObject tag, int offset) {
        StringBuilder builder = new StringBuilder();
        String offsetString = makeOffset(offset);
        if(Objects.equals(tagName, "meta")) {
            for(String key : tag.keySet()) {
                if(Objects.equals(key, "charset")){
                    builder.append(offsetString).append("<meta charset=\"").append(tag.get(key)).append("\"/>\n");
                }
                else if (Objects.equals(key, "viewport")) {
                    JSONObject viewport = (JSONObject) tag.get(key);
                    StringBuilder viewportBuilder = new StringBuilder();
                    for(String viewPortOption : viewport.keySet()) {
                        viewportBuilder.append(viewPortOption).append("=").append(viewport.get(viewPortOption)).append(", ");
                    }
                    String viewportString = viewportBuilder.toString();
                    viewportString = viewportString.substring(0, viewportString.length() - 2);
                    builder.append(offsetString).append("<meta name=\"viewport\" content=\"").append(viewportString).append("\"/>\n");
                }
                else {
                    builder.append(offsetString).append("<meta name=\"").append(key).append("\" content=\"").append(key).append("\"/>\n");
                }
            }
        }
        else{
            builder.append(offsetString).append("<").append(tagName);
            for(String key : tag.keySet()) {
                builder.append(" ").append(key).append("=\"").append(tag.get(key)).append("\"");
            }
            builder.append("\"/>\n");
        }
        return builder.toString();
    }

    public static String makeHead(JSONObject head, int offset){
        StringBuilder builder = new StringBuilder();
        if(offset == 1){
            builder.append("\t<head>\n");
        }

        String offsetString = makeOffset(offset);
        for(String key : head.keySet()) {
            Object value = head.get(key);
            if(value instanceof String){
                builder.append(offsetString).append("<").append(key).append(">").append(value).append("</").append(key).append(">\n");
            }
            if(value instanceof JSONObject) {
                builder.append(makeTagHead(key, (JSONObject) head.get(key), offset));
            }
            if(value instanceof JSONArray) {
                for(int i = 0; i < ((JSONArray)value).length(); i++) {
                    builder.append(makeTagHead(key, (JSONObject) ((JSONArray) value).get(i), offset));
                }
            }
        }
        if(offset == 1){
            builder.append("\t</head>\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        String filename = "helloWorld.json";
        //String filename = "pageNotFoundV2.json";
        //String filename = "pageNotFound.json";
        try{
            String fileContentsString = getFileContents(filename);
            JSONObject jsonObject = new JSONObject(fileContentsString);

            JSONObject head = jsonObject.getJSONObject("head");
            JSONObject body = jsonObject.getJSONObject("body");
            String doctype = jsonObject.getString("doctype");

            String htmlString = "<!DOCTYPE " + doctype + ">\n<html>\n";

            //get attributes
            htmlString += makeHead(head, 1);
            htmlString += makeBody(body, 1);
            htmlString += "</html>\n";
            System.out.println(htmlString);

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}


/*
* Fixes:
* ->Order
* ->More OOP
* ->Make it prettier
* ->Check Java conventions
* ->StringBuilder instead of concatenation
*
* */