package util;

import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class HierarchyUtils {
    public static Data createARXHierarchies(Data data, String hiearchyDirectory) throws IOException {

        List<File> hiearchyFile = Files.walk(Paths.get(hiearchyDirectory))
                .filter(path -> Files.isRegularFile(path))
                .map(Path::toFile)
                .collect(Collectors.toList());

        return createHierachiesFromFiles(data, hiearchyFile);
    }

    private static Data createHierachiesFromFiles(Data data, List<File> filesInFolder) throws IOException {
        for (File hierachyFile : filesInFolder) {
            if (hierachyFile.getName().endsWith(".csv")) {
                AttributeType.Hierarchy hierarchy = AttributeType.Hierarchy.create(hierachyFile, Charset.defaultCharset(), FileUtils.getSepNaive(hierachyFile));
                String columnName = hierachyFile.getName().split(".csv")[0];
                data.getDefinition().setHierarchy(columnName, hierarchy);
            }
        }
        return data;
    }
}
