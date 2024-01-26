package bg.sofia.uni.fmi.mjt;

import java.net.URI;
import java.util.List;

public class URIBuilder {
    private static final String BASE_URL = "https://api.edamam.com/api/recipes/v2?type=public";
    private final StringBuilder uriBuilder;

    public URIBuilder() {
        this.uriBuilder = new StringBuilder(BASE_URL);
    }

    public URIBuilder addParameter(String name, String value) {
        if (!value.isBlank()) {
            if (name.equals("q")) {
                value = formatKeywords(value);
            }
            uriBuilder.append("&").append(name).append("=").append(value);
        }
        return this;
    }

    private String formatKeywords(String value) {
        return String.join("%20", value.split("\\s+"));
    }

    public URIBuilder addParameters(String name, List<String> values) {
        if (!values.isEmpty()) {
            for (String value : values) {
                addParameter(name, value);
            }
        }
        return this;
    }

    public URI build() {
        if (uriBuilder.charAt(uriBuilder.length() - 1) == '&') {
            uriBuilder.setLength(uriBuilder.length() - 1);
        }
        return URI.create(uriBuilder.toString());
    }
}

