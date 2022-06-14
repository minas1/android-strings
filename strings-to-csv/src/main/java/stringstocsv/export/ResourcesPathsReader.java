package stringstocsv.export;

import stringstocsv.model.Language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcesPathsReader {

    /**
     * @param rootPath the path from which to start reading resources. Usually the root directory of the project.
     * @return the paths of the files that contain string resources (strings.xml, plurals.xml).
     */
    public Map<Language, List<Path>> read(Path rootPath, List<Language> languages) {

        try {
            List<Path> resourcesPaths = readPaths(rootPath);
            return groupPathsByLanguage(resourcesPaths,languages);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Path> readPaths(Path rootPath) throws IOException {

        List<Path> paths;
        try (Stream<Path> stream = Files.walk(rootPath, Integer.MAX_VALUE)) {
            paths = stream.collect(Collectors.toList());
        }

        // TODO instead of excluding v28, it's better to include only those that are values-[cc] where cc is a country code
        paths = paths.stream()
                .filter(path -> path.endsWith("strings.xml") || path.endsWith("plurals.xml"))
                .filter(path -> !path.toString().contains("values-v28"))
                .filter(path -> !path.toString().contains("release/res"))
                .filter(path -> !path.toString().contains("debug/res"))
                .filter(path -> path.toString().contains("main/res"))
                .collect(Collectors.toList());

        return paths;
    }

    private Map<Language, List<Path>> groupPathsByLanguage(List<Path> paths, List<Language> languages) {

        Map<String, Language> languagesByCode = languages.stream()
                .collect(Collectors.toMap(l -> l.getCode().toLowerCase(), Function.identity()));

        Map<Language, List<Path>> groupedPaths = new HashMap<>();

        for (Path path : paths) {

            Path parent = path.getParent();
            String lastPartOfName = parent.getName(parent.getNameCount() - 1).toString();

            Language language;
            if (lastPartOfName.equals("values")) {
                language = Language.DEFAULT;
            }
            else {
                String code = lastPartOfName.split("-")[1];
                language = languagesByCode.get(code);
            }

            groupedPaths.compute(language, (k, v) -> {

                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(path);
                return v;
            });
        }

        return groupedPaths;
    }
}
