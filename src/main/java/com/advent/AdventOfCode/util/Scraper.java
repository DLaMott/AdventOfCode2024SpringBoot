package com.advent.AdventOfCode.util;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class Scraper {

        @Value("${advent.base}")
        private String BASE_URL;
        @Value("${session.token}")
        private String SESSION;

        /**
         * Fetch the puzzle description from the given day's page.
         */
        public String fetchPuzzleDescription(int day) throws IOException {
            String url = BASE_URL + day;
            Document document = Jsoup.connect(url).get();
            return document.body().text(); // Returns the text content of the page
        }

        /**
         * Fetch the puzzle input data from the nested input URL.
         */
        public String fetchPuzzleInput(int day) throws IOException, InterruptedException {
            String inputUrl = BASE_URL + day + "/input";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(inputUrl))
                    .header("Cookie", "session=" + SESSION)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }


}
