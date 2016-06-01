package fi.helsinki.cs.tmc.langs.java.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fi.helsinki.cs.tmc.langs.abstraction.ValidationError;
import fi.helsinki.cs.tmc.langs.abstraction.ValidationResult;
import fi.helsinki.cs.tmc.langs.domain.CompileResult;
import fi.helsinki.cs.tmc.langs.domain.TestCase;
import fi.helsinki.cs.tmc.langs.utils.TestUtils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MavenPluginTest {

    private MavenPlugin mavenPlugin;

    public MavenPluginTest() {
        mavenPlugin = new MavenPlugin();
    }

    @Test
    public void testGetLanguageName() {
        assertEquals("apache-maven", mavenPlugin.getLanguageName());
    }

    @Test
    public void testCheckCodeStyle() {
        Path project = TestUtils.getPath(getClass(), "most_errors");
        ValidationResult result = mavenPlugin.checkCodeStyle(project, new Locale("en"));
        Map<File, List<ValidationError>> res = result.getValidationErrors();
        assertEquals("Should be one erroneous file", 1, res.size());
        for (File file : res.keySet()) {
            List<ValidationError> errors = res.get(file);
            assertEquals("Should return the right amount of errors", 24, errors.size());
        }
    }

    @Test
    public void testCheckCodeStyleWithUntestableProject() {
        File projectToTest = new File("src/test/resources/dummy_project/");
        ValidationResult result =
                mavenPlugin.checkCodeStyle(projectToTest.toPath(), new Locale("en"));
        assertNull(result);
    }

    @Test
    public void testPassingMavenBuild() throws IOException {
        Path project = TestUtils.getPath(getClass(), "maven_exercise");
        CompileResult result = mavenPlugin.build(project);
        assertEquals("Compile status should be 0 when build passes", 0, result.getStatusCode());
    }

    @Test
    public void testFailingMavenBuild() throws IOException {
        Path project = TestUtils.getPath(getClass(), "failing_maven_exercise");
        CompileResult result = mavenPlugin.build(project);
        assertEquals("Compile status should be 1 when build fails", 1, result.getStatusCode());
    }

    @Test
    public void testRunTestsWhenBuildFailing() {
        Path project = TestUtils.getPath(getClass(), "failing_maven_exercise");
        TestCase testCase = mavenPlugin.runTests(project);
        assertEquals(TestCase.Status.COMPILE_FAILED, testCase.status);
    }

    @Test
    public void testMavenProjectWithFailingTestsCompilesAndFailsTests() {
        Path path = TestUtils.getPath(getClass(), "maven_exercise");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(TestCase.Status.TESTS_FAILED, result.status);
    }

    @Test
    public void testFailingMavenProjectHasOneFailedTest() {
        Path path = TestUtils.getPath(getClass(), "maven_exercise");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(1, result.testResults.size());
    }

    //    @Test
    //    public void testFailingMavenProjectHasCorrectError() {
    //        Path path = TestUtils.getPath(getClass(), "maven_exercise");
    //        TestCase result = mavenPlugin.runTests(path);
    //
    //        assertEquals("ComparisonFailure: expected:\u003c[Hello Maven!\n]\u003e but "
    //                + "was:\u003c[]\u003e", result.testResults.get(0).errorMessage);
    //    }

    //    @Test
    //    public void testFailingMavenProjectHasStackTrace() {
    //        Path path = TestUtils.getPath(getClass(), "maven_exercise");
    //        TestCase result = mavenPlugin.runTests(path);
    //
    //        assertTrue(result.testResults.get(0).backtrace.size() > 0);
    //    }

    @Test
    public void testMavenProjectWithPassingTestsCompilesAndPassesTests() {
        Path path = TestUtils.getPath(getClass(), "passing_maven_exercise");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(TestCase.Status.PASSED, result.status);
    }

    @Test
    public void testPassingMavenProjectHasOnePassingTest() {
        Path path = TestUtils.getPath(getClass(), "passing_maven_exercise");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(1, result.testResults.size());
        assertEquals(true, result.testResults.get(0).passed);
    }

    @Test
    public void testPassingMavenProjectHasNoError() {
        Path path = TestUtils.getPath(getClass(), "passing_maven_exercise");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals("", result.testResults.get(0).errorMessage);
    }

    @Test
    public void pluginHandlesProjectThatUsesReflectionUtils() {
        Path project = TestUtils.getPath(getClass(), "reflection_utils_maven_test_case");
        TestCase result = mavenPlugin.runTests(project);
        assertEquals(TestCase.Status.PASSED, result.status);
    }

    @Test
    public void testMultipleTestMethodsGetAddedToRunResultWhenAllPass() {
        Path path = TestUtils.getPath(getClass(), "passing_maven_exercise_with_class_points");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(TestCase.Status.PASSED, result.status);
        assertEquals(2, result.testResults.size());

        assertTrue(result.testResults.get(0).passed);
        assertTrue(result.testResults.get(1).passed);

        assertEquals("", result.testResults.get(0).errorMessage);
        assertEquals("", result.testResults.get(1).errorMessage);

        assertTrue(result.testResults.get(0).backtrace.isEmpty());
        assertTrue(result.testResults.get(1).backtrace.isEmpty());
    }

    @Test
    public void testPointsWhenAllTestsPass() {
        Path path = TestUtils.getPath(getClass(), "passing_maven_exercise_with_class_points");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(2, result.testResults.get(0).points.size());
        assertEquals(2, result.testResults.get(1).points.size());

        assertEquals("class-point", result.testResults.get(0).points.get(0));
        assertEquals("class-point", result.testResults.get(1).points.get(0));

        assertEquals("same-point", result.testResults.get(0).points.get(1));
        assertEquals("same-point", result.testResults.get(1).points.get(1));
    }

    @Test
    public void testMultipleTestMethodsGetAddedToRunResultWhenSomeFail() {
        Path path = TestUtils.getPath(getClass(), "failing_maven_exercise_with_class_points");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(TestCase.Status.TESTS_FAILED, result.status);
        assertEquals(2, result.testResults.size());

        assertTrue(result.testResults.get(0).passed);
        assertFalse(result.testResults.get(1).passed);

        assertEquals("", result.testResults.get(0).errorMessage);
        assertEquals("AssertionError", result.testResults.get(1).errorMessage);

        assertTrue(result.testResults.get(0).backtrace.isEmpty());
        assertFalse(result.testResults.get(1).backtrace.isEmpty());
    }

    @Test
    public void testPointsWhenSomeTestsFail() {
        Path path = TestUtils.getPath(getClass(), "failing_maven_exercise_with_class_points");
        TestCase result = mavenPlugin.runTests(path);

        assertEquals(2, result.testResults.get(0).points.size());
        assertEquals(2, result.testResults.get(1).points.size());

        assertEquals("class-point", result.testResults.get(0).points.get(0));
        assertEquals("class-point", result.testResults.get(1).points.get(0));

        assertEquals("same-point", result.testResults.get(0).points.get(1));
        assertEquals("same-point", result.testResults.get(1).points.get(1));
    }
}
