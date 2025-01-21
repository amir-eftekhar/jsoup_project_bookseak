package com.scraper.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ScraperController {

    @GetMapping("/api/reviews")
    public String getReviews() {
        try {
            Document doc = Jsoup.connect("https://www.libraryjournal.com/section/reviews")
                    .userAgent("Mozilla/5.0 (Macintosh; Apple Silicon Mac OS X) AppleWebKit/605.1.15")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .timeout(30000)
                    .get();

            JSONArray reviewsArray = new JSONArray();
            Elements reviews = doc.select("div.filter-story-section");

            for (Element review : reviews) {
                try {
                    String title = review.select("h3.filtered-document-headline").text();
                    String author = review.select("div.byline-detail a.author").first().text();
                    String date = review.select("div.byline-detail").text().split(",")[1].trim();
                    String description = review.select("div.recommended-description").text();

                    JSONObject reviewObject = new JSONObject();
                    reviewObject.put("title", title);
                    reviewObject.put("author", author);
                    reviewObject.put("date", date);
                    reviewObject.put("description", description);
                    
                    reviewsArray.put(reviewObject);
                } catch (Exception e) {
                    System.out.println("Error processing a review: " + e.getMessage());
                }
            }

            return reviewsArray.toString(4);
        } catch (IOException e) {
            return new JSONObject().put("error", e.getMessage()).toString();
        }
    }
}
