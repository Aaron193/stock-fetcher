import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    static String URL_PATH;
    static String PRICE_CLASS;
    static String CORRECTION_CLASS;
    static String STOCK_NAME_CLASS_MAIN;
    static String STOCK_NAME_CLASS_CORRECTION;
    static String STOCK_EXCHANGE;

    public static void main(String[] args) throws IOException, InterruptedException {
        URL_PATH = "https://www.google.com/finance/quote/";
        PRICE_CLASS = "YMlKec fxKbKc";
        CORRECTION_CLASS = "YMlKec";
        STOCK_NAME_CLASS_MAIN = "zzDege";
        STOCK_NAME_CLASS_CORRECTION = "ZvmM7";
        STOCK_EXCHANGE = ":NYSE";

        Scanner scan = new Scanner(System.in);

        System.out.println("Please enter a stock name (press Q to quit)");

        while (scan.hasNext()) {

            String stockName = scan.nextLine().toUpperCase();
            if (stockName.toUpperCase().equals("Q")) {
                System.out.println("Program closed");
                break;
            }
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header("accept", "application/json")
                    .uri(URI.create(URL_PATH + stockName + STOCK_EXCHANGE))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String res = response.body();

            if (hasStock(res)) {

                int idx = res.indexOf(PRICE_CLASS) + PRICE_CLASS.length();
                String price = getPriceFromIdx(res, idx);

                String name = getTextFromClass(res, STOCK_NAME_CLASS_MAIN, false);

                tellStockInfo(stockName, price, name);
            } else {
                if (hasCorrectionStock(res)) {

                    int idx = res.indexOf(CORRECTION_CLASS, res.indexOf("Did you mean?")) + CORRECTION_CLASS.length();
                    String price = getPriceFromIdx(res, idx);

                    String name = getTextFromClass(res, STOCK_NAME_CLASS_CORRECTION, true);

                    tellStockInfo(stockName, price, name);

                } else {
                    System.out.println("This stock doesn't seem to exist.. try again");
                }
            }
        }
    }
    public static String getPriceFromIdx(String html, int idx) {
        int priceIdx = html.indexOf("$", idx);
        int endTagIdx = html.indexOf("</div>", idx);
        return html.substring(priceIdx, endTagIdx);
    }
    public static String getTextFromClass(String html, String className, boolean isCorrection) {
        int idx = html.indexOf(className, isCorrection ? html.indexOf("Did you mean?") : html.indexOf("<body")) + className.length() + 2; // for "> close tag;
        int endTagIdx = html.indexOf("</div>", idx);
        return html.substring(idx, endTagIdx);
    }
    public static void tellStockInfo(String stockName, String price, String name) {
        System.out.println("Name: " + name);
        System.out.println("Stock: " + stockName);
        System.out.println("Price: " + price);

    }
    public static boolean hasStock(String html) {
        return html.indexOf("We couldn't find any match for your search") == -1;
    }
    public static boolean hasCorrectionStock(String html) {
        return html.indexOf("Did you mean?") != -1;
    }
}