package io.xeros.sql.youtube;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeAPIClient {
    private YouTube youTube;
    private static final String API_KEY = "AIzaSyBqQejU83og5yh9blyra-iTfT0s77xWIDo";
    public YouTubeAPIClient() {
        // Initialize YouTube object
        youTube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                request -> {

                })
                .setApplicationName("video-test")
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                .build();
    }

    public YouTube getYouTube() {
        return youTube;
    }

    public List<String> getComments(String videoId) {
        List<String> comments = new ArrayList<>();
        try {
            YouTube.CommentThreads.List request = youTube.commentThreads().list("snippet");
            request.setVideoId(videoId);

            // Execute the request
            CommentThreadListResponse response = request.execute();

            // Process the response
            for (CommentThread commentThread : response.getItems()) {
                comments.add(commentThread.getSnippet().getTopLevelComment().getSnippet().getTextDisplay());
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException
        }
        return comments;
    }

    public boolean isVideoOlderThan7Days(String videoId) {
        try {
            YouTube.Videos.List request = youTube.videos().list("snippet");
            request.setId(videoId);

            // Execute the request
            VideoListResponse response = request.execute();

            // Get the list of videos
            List<Video> videos = response.getItems();

            if (videos != null && !videos.isEmpty()) {
                // Get the published date of the video
                LocalDateTime publishedDateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(videos.get(0).getSnippet().getPublishedAt().getValue()),
                        ZoneOffset.UTC
                );

                // Calculate the difference between the published date and the current date
                Duration duration = Duration.between(publishedDateTime, LocalDateTime.now());

                // Check if the video is older than 7 days
                return duration.toDays() > 7;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException
        }
        // Default to false if unable to determine the age or encounter an error
        return false;
    }

    public List<String> extractUsernamesFromComments(String videoId) {
        List<String> usernames = new ArrayList<>();
        // Check if the video is older than 7 days
        if (isVideoOlderThan7Days(videoId)) {
            System.out.println("Video is older than 7 days.");
            return usernames; // Return an empty list
        }

        // Retrieve comments from the video
        List<String> comments = getComments(videoId);

        // Extract usernames from comments
        for (String comment : comments) {
            System.out.println("Comment found: " + comment);
            usernames.addAll(extractUsernames(comment));
        }
        return usernames;
    }

    private List<String> extractUsernames(String comment) {
        List<String> usernames = new ArrayList<>();
        // Regular expression pattern to match usernames
        Pattern pattern = Pattern.compile("\\b(?:ign|in-game|user|name):?\\s*(\\w+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comment);
        while (matcher.find()) {
            usernames.add(matcher.group(1)); // Capture group 1 contains the username
        }
        return usernames;
    }
}