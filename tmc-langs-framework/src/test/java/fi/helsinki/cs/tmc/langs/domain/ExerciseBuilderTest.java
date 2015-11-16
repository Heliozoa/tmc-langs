package fi.helsinki.cs.tmc.langs.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import fi.helsinki.cs.tmc.langs.util.ExerciseBuilder;

import com.google.common.io.Files;

import org.apache.commons.io.FileUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class ExerciseBuilderTest {

    private ExerciseBuilder exerciseBuilder;

    @Before
    public void setUp() {
        //        exerciseBuilder =
        //                new ExerciseBuilder(
        //                        CommentSyntax.newBuilder()
        //                                .addSingleLineComment("\\/\\/")
        //                                .addMultiLineComment("\\/\\*+", "\\*+\\/")
        //                                .build());
    }

    @Test
    public void testPrepareStub() throws IOException {
        Path originProject = Paths.get("src", "test", "resources", "arith_funcs");
        Path targetFolder = createTemporaryCopyOf(originProject);
        Path expectedFolder = Paths.get("src", "test", "resources", "arith_funcs_stub", "src");
        Path outputFolder = targetFolder.toAbsolutePath().resolve("src");

        //        exerciseBuilder.prepareStub(targetFolder);

        //        assertFileLines(expectedFolder, outputFolder);
    }

    @Test
    public void testPrepareSolution() throws IOException {
        Path originProject = Paths.get("src", "test", "resources", "arith_funcs");
        Path targetFolder = createTemporaryCopyOf(originProject);
        Path expectedFolder = Paths.get("src", "test", "resources", "arith_funcs_solution", "src");
        Path outputFolder = targetFolder.toAbsolutePath().resolve("src");

        //        exerciseBuilder.prepareSolution(targetFolder);
        //
        //        assertFileLines(expectedFolder, outputFolder);
    }

    @Test
    public void prepareSolutionHandlesNonFolderPath() {
        //        File originProject =
        //                new File(
        //                        "src"
        //                                + File.separator
        //                                + "test"
        //                                + File.separator
        //                                + "resources"
        //                                + File.separator
        //                                + "arith_funcs"
        //                                + File.separator
        //                                + "build.xml");
        //
        //        exerciseBuilder.prepareSolution(originProject.toPath());
    }

    @Test
    public void solutionFileLinesAreCorrectlyPrepared() throws IOException {
        Path path = Paths.get("src", "test", "resources", "arith_funcs_solution_file");
        Path temp = createTemporaryCopyOf(path);
        temp.toFile().deleteOnExit();
        //exerciseBuilder.prepareSolution(temp);
        //Path solutionFile = temp.resolve(Paths.get("src", "SolutionFile.java"));
        //int size = Files.readAllLines(solutionFile, Charset.defaultCharset()).size();
        //        assertEquals(2, size);
    }

    //    @Test
    //    public void solutionFilesAreIgnoredFromStub() throws IOException {
    //        Path path = Paths.get("src", "test", "resources", "arith_funcs_solution_file");
    //        Path temp = createTemporaryCopyOf(path);
    //        temp.toFile().deleteOnExit();
    //        exerciseBuilder.prepareStub(temp);
    //        final Path solutionFile = temp.resolve(Paths.get("src", "SolutionFile.java"));
    //final Path solutionFile2 = temp.resolve(Paths.get("src", "SolutionFileWithNoSpace.java"));
    //        final Path solutionFile3 =
    //                temp.resolve(Paths.get("src", "SolutionFileWithExtraSpaces.java"));
    //final Path solutionFile4 = temp.resolve(Paths.get("src", "MultilineSolutionFile.java"));
    //
    //        assertFalse(solutionFile.toFile().exists());
    //        assertFalse(solutionFile2.toFile().exists());
    //        assertFalse(solutionFile3.toFile().exists());
    //        assertFalse(solutionFile4.toFile().exists());
    //    }

    private Path createTemporaryCopyOf(Path path) throws IOException {
        File tempFolder = Files.createTempDir();
        FileUtils.copyDirectory(path.toFile(), tempFolder);
        tempFolder.deleteOnExit();
        return tempFolder.toPath();
    }

    private void assertFileLines(Path expectedFolder, Path outputFolder) throws IOException {
        Map<String, Path> expectedFiles = getFileMap(expectedFolder);
        Map<String, Path> actualFiles = getFileMap(outputFolder);

        for (String fileName : expectedFiles.keySet()) {
            Path expected = expectedFiles.get(fileName);
            Path actual = actualFiles.get(fileName);
            List<String> expectedLines = FileUtils.readLines(expected.toFile());
            List<String> actualLines = FileUtils.readLines(actual.toFile());
            for (int i = 0; i < expectedLines.size(); ++i) {
                String expectedLine = expectedLines.get(i);
                String actualLine = actualLines.get(i);
                assertEquals(
                        "Line in file " + fileName + " did not match ", expectedLine, actualLine);
            }
        }
    }

    private Map<String, Path> getFileMap(Path folder) throws IOException {
        if (!folder.toFile().isDirectory()) {
            throw new IOException("Supplied path was not a directory");
        }
        Map<String, Path> result = new HashMap<>();
        for (File file : folder.toFile().listFiles()) {
            if (file.isDirectory()) {
                result.putAll(getFileMap(file.toPath()));
                continue;
            }
            result.put(file.toPath().getFileName().toString(), file.toPath());
        }
        return result;
    }
}
