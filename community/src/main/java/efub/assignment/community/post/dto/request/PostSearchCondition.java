package efub.assignment.community.post.dto.request;

public record PostSearchCondition(
        String keyword,
        String writer
) {

    public PostSearchCondition {
        keyword = normalize(keyword);
        writer = normalize(writer);
    }

    public boolean isEmpty() {
        return keyword == null && writer == null;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
