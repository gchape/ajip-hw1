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

    private static void merge(final List<Path> tempFiles) throws IOException {
        var words = new TreeSet<>(String::compareToIgnoreCase);
        var buffer = CharBuffer.allocate((int) Files.size(tempFiles.getFirst()));

        for (Path tempFile : tempFiles) {
            File file = tempFile.toFile();

            try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                reader.read(buffer);

                var filteredWords =
                        Arrays.stream(new String(buffer.array()).split(" "))
                                .filter(Predicate.not(String::isBlank))
                                .map(String::trim)
                                .toList();

                words.addAll(filteredWords);

                buffer.flip();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resources/sonnets_sorted.txt")))) {
            writer.write(String.join(" ", words));
        }
    }
}