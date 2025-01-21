import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LibraryJournalScraper {
    public static void main(String[] args) {
        try {
            // Connect to Library Journal with appropriate headers
            Document doc = Jsoup.connect("https://www.libraryjournal.com/section/reviews")
                .userAgent("Mozilla/5.0 (Macintosh; Apple Silicon Mac OS X) AppleWebKit/605.1.15")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .timeout(30000)
                .get();

            // Create JSON array to store reviews
            JSONArray reviewsArray = new JSONArray();

            // Select all review sections
            Elements reviews = doc.select("div.filter-story-section");
            
            System.out.println("Found " + reviews.size() + " reviews");
            
            for (Element review : reviews) {
                try {
                    // Extract review components
                    String title = review.select("h3.filtered-document-headline").text();
                    String author = review.select("div.byline-detail a.author").first().text();
                    String date = review.select("div.byline-detail").text().split(",")[1].trim();
                    String description = review.select("div.recommended-description").text();

                    // Create JSON object for each review
                    JSONObject reviewObject = new JSONObject();
                    reviewObject.put("title", title);
                    reviewObject.put("author", author);
                    reviewObject.put("date", date);
                    reviewObject.put("description", description);
                    
                    // Add to reviews array
                    reviewsArray.put(reviewObject);
                    
                    // Print to console for monitoring
                    System.out.println("\nReview found:");
                    System.out.println("Title: " + title);
                    System.out.println("Author: " + author);
                    System.out.println("Date: " + date);
                    System.out.println("Description: " + description);
                    System.out.println("-----------------");

                } catch (Exception e) {
                    System.out.println("Error processing a review: " + e.getMessage());
                }
            }

            // Write JSON to file with pretty printing
            String jsonString = reviewsArray.toString(4); // 4 spaces for indentation
            Files.write(Paths.get("library_journal_reviews.json"), jsonString.getBytes());
            
            System.out.println("\nScraping completed! Reviews saved to library_journal_reviews.json");

        } catch (IOException e) {
            System.out.println("Error accessing website: " + e.getMessage());
            e.printStackTrace();
        }
    }
}