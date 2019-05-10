import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupParser {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.scalemates.com/kits/news.php")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        //doc.select("p").forEach(System.out::println);
        Elements divAc = doc.select("div.ac");
        /*for (Element ac: divAc) {
            System.out.println("its div: "+ ac);
        }*/
        try(FileWriter writer = new FileWriter("json.txt", false)) {
            writer.write("{results: [\n");

            for (Element block : divAc) {
                Elements scalenamates = block.select("a");
                Element divAr = block.selectFirst("div.ar");
                writer.write("scalenamatesUrl: " + scalenamates.get(1).attr("href")+"\n");
                Elements brand = block.select("br");
                writer.write("brand: " + createBrand(divAr.toString())+"\n");
                writer.write("name: " + divAr.select("a").text() + "; " + divAr.select("em").text()+"\n");
                writer.write("scale: " + createScale(divAr.text())+"\n");
                writer.write("description: " + createDescription(divAr.selectFirst("div.nw").text())+"\n");
                writer.write("boxartUrl: " + createUrlImg(divAc.select("img").attr("src"))+"\n");
                writer.write("year: " + createYears(divAr.selectFirst("div.nw").text())+"\n");
                //System.out.prinln("brandCatno: " + block.select("br").get(2).text());

            /*for (Element a : ahref) {
                System.out.println("Link: " + a.attr("href"));
                System.out.println("Text: " + a.select("img").attr("src"));
            }*/
                writer.write(",\n");
            }

            writer.write("]}");
        }

        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }

    static public String createYears(String year){
        return year.substring(0,4);
    }

    static public String createUrlImg(String url){
        Pattern pattern = Pattern.compile("/.+-");
        Matcher matcher = pattern.matcher(url);
        while(matcher.find())
            return url.substring(matcher.start(),matcher.end());
        return "";
    }

    static public String createDescription(String text){
        if(text.length()>7)
            return text.substring(7,text.length());
        return "";

        /*Pattern pattern = Pattern.compile("\\s.++e");
        Matcher matcher = pattern.matcher(text);
        while(matcher.find())
            return text.substring(matcher.start(),matcher.end());
        return "";*/
    }

    static public String createScale(String text){
        for(int i = 0;i<text.length();i++){
            if (text.charAt(i) == ':') return text.substring(i+1,i+3);
        }
        return "haha";
    }

    static public String createBrand(String text){
        for (int i = 0;i<text.length();i++){
            if(text.charAt(i) == 'b' && text.charAt(i+1)=='r')
                return writeWhileNotFind(i,text);
        }
        return "haha";
    }


    static public String writeWhileNotFind(int k, String text){
        for(int i = k;i<text.length();i++) {
            if (text.charAt(i) == '<')
                return text.substring(k+3, i-7);
        }
        return "";
    }
}
