public class Main {
    public static void main(String[] args) {

    }

    private static void deleteChunkFiles() {
        try (var files = Files.list(Path.of("resources"))) {
            files.filter(p -> p.toString().contains("chunk")).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}