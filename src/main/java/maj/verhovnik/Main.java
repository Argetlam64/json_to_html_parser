package maj.verhovnik;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import maj.verhovnik.JsonParser;
import maj.verhovnik.HtmlBuilder;


public class Main {
    public static void main(String[] args) {
        ArrayList<String> filenames = new ArrayList<>(Arrays.asList("helloWorld.json", "pageNotFound.json", "pageNotFoundV2.json"));
        String filename = filenames.get(1);
        try {
            String htmlString = JsonParser.getHtmlFromFile(filename);
            Files.writeString(Path.of("parsedFile.html"), htmlString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}