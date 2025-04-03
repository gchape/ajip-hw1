package io.gchape.github.file;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileUtils {
    /**
     * This method performs a merge sort-like process on the file contents.
     * It divides the file into partitions, sorts each partition, and then merges them.
     *
     * @param file       The file that will be sorted.
     * @param comparator A comparator used to sort the file contents.
     * @throws IOException If there are issues reading or writing files.
     */
    public static void sort(final String file, final Comparator<String> comparator) throws IOException {
        deletePartitionFiles();

        var resource = "resources/" + file;
        var partitionPaths = new ArrayList<Path>();

        try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(resource)))) {
            var charBuffer = CharBuffer.allocate(8096);

            for (int i = 0; reader.read(charBuffer) != -1; charBuffer.flip(), i++) {
                var partitionContent =
                        Arrays.stream(new String(charBuffer.array()).split("[ ,.:]"))
                                .filter(Predicate.not(String::isBlank))
                                .map(String::trim)
                                .sorted(comparator)
                                .collect(Collectors.joining(" "));

                var partitionPath = Path.of("resources/partition%d.txt".formatted(i));
                partitionPaths.add(partitionPath);

                Files.deleteIfExists(partitionPath);
                Files.createFile(partitionPath);
                try (var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(partitionPath.toFile())))) {
                    writer.write(partitionContent);
                }
            }
        }

        merge(partitionPaths, comparator);
    }

    /**
     * Deletes any existing partition files from previous runs to avoid conflicts with new runs.
     */
    private static void deletePartitionFiles() {
        var resourceDir = new File("resources/");

        for (File file : Objects.requireNonNull(resourceDir.listFiles())) {
            if (file.getName().contains("partition")) {
                file.delete();
            }
        }
    }

    /**
     * Merges the sorted partitions into one final sorted file.
     *
     * @param partitionPaths A list of paths to the sorted chunk files.
     * @param comparator     A comparator used to compare the words during the merge.
     * @throws IOException If there are issues reading or writing files.
     */
    private static void merge(final List<Path> partitionPaths, final Comparator<String> comparator) throws IOException {
        var partitions = new ArrayList<StringBuilder>();
        var charBuffer = CharBuffer.allocate(8096);

        for (Path partitionPath : partitionPaths) {
            try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(partitionPath.toFile())))) {
                reader.read(charBuffer);

                var partitionContent = new StringBuilder();
                for (String word : new String(charBuffer.array()).split(" ")) {
                    partitionContent.append(word).append(" ");
                }
                partitions.add(partitionContent);

                charBuffer.flip();
            }
        }

        try (var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resources/sonnets_sorted.txt")))) {
            writer.write(
                    partitions.stream()
                            .map(StringBuilder::toString)
                            .sorted(comparator)
                            .collect(Collectors.joining(" "))
            );
        }
    }
}
