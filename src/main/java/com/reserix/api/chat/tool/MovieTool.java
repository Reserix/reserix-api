package com.reserix.api.chat.tool;

import com.reserix.api.chat.enums.ChatToolName;
import com.reserix.api.chat.tool.port.MovieSearchCriteria;
import com.reserix.api.chat.tool.port.MovieSearchItem;
import com.reserix.api.chat.tool.port.MovieSearchPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieTool implements ChatTool {
    private final ObjectProvider<MovieSearchPort> movieSearchPortProvider;

    public MovieTool(ObjectProvider<MovieSearchPort> movieSearchPortProvider) {
        this.movieSearchPortProvider = movieSearchPortProvider;
    }

    @Override
    public ChatToolName name() {
        return ChatToolName.SEARCH_MOVIES;
    }

    @Override
    public ChatToolResult execute(ChatToolRequest request, ChatToolContext context) {
        MovieSearchPort port = movieSearchPortProvider.getIfAvailable();
        if (port == null)
            return ChatToolResult.notWired(name(), "Movie search is ready at the chatbot tool layer, but it is not connected to the real MovieService yet.");

        List<MovieSearchItem> movies = port.searchMovies(new MovieSearchCriteria(request.stringArg("keyword"), null, null));
        if (movies == null || movies.isEmpty())
            return ChatToolResult.empty(name(), "No matching movies were found.");

        String summary = movies.stream().limit(5).map(movie ->
                "- " + movie.title() + (movie.durationMinutes() == null ? "" : " (" + movie.durationMinutes() + " min)")).collect(Collectors.joining("\n"));

        return ChatToolResult.success(name(), "I found these movies:\n" + summary, movies);
    }
}
