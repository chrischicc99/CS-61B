package make;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MakeTesting {
    private String makefileName = "makefile.tmp";
    private String fileInfoName = "fileInfo.tmp";

    private Path makefilePath = Paths.get(makefileName);
    private Path fileInfoPath = Paths.get(fileInfoName);

    @Before
    public void setUp() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.delete(makefilePath);
                Files.delete(fileInfoPath);
            } catch (IOException ex) {
                System.err.printf("Error deleting temp files: %s",
                        ex.getMessage());
            }
        }));
    }

    @Test
    public void testSimple() throws Exception {
        String makefile = ""
            + "A: B\n"
            + "    rebuild A\n"
            + "B:\n"
            + "    rebuild B\n";
        String fileInfo = "100\n"
            + "B 10\n";
        String expect = ""
            + "    rebuild A\n";

        doTest(makefile, fileInfo, "A", expect);
    }

    @Test
    public void testDemo() throws Exception {
        String makefile = ""
            + "A: B C D\n"
            + "    rebuild A\n"
            + "B:\n"
            + "    rebuild B\n"
            + "C: D\n"
            + "    rebuild C\n"
            + "D:\n"
            + "    rebuild D\n";
        String fileInfo = "100\n";
        String expect = ""
            + "    rebuild B\n"
            + "    rebuild D\n"
            + "    rebuild C\n"
            + "    rebuild A\n";

        doTest(makefile, fileInfo, "A", expect);
    }

    @Test
    public void testMake1() throws Exception {
        String makefile = ""
            + "foo.o: foo.c foo.h\n"
            + "        gcc -g -c foo.o foo.c\n"
            + "\n"
            + "foo: foo.o\n"
            + "\tgcc -o foo foo.o\n"
            + "\n"
            + "foo.c: foo.y\n"
            + "\tyacc -o foo.c foo.y\n";
        String fileInfo = "100\n"
            + "foo 90\n"
            + "foo.y 50\n"
            + "foo.h 10\n";
        String expect = ""
            + "\tyacc -o foo.c foo.y\n"
            + "        gcc -g -c foo.o foo.c\n"
            + "\tgcc -o foo foo.o\n";

        doTest(makefile, fileInfo, "foo", expect);
    }

    @Test
    public void testNeedRebuild() throws Exception {
        String makefile = ""
            + "A: B\n"
            + "    rebuild A\n"
            + "B: C D\n"
            + "    rebuild B\n";
        String fileInfo = "100\n"
            + "B 5\n"
            + "C 10\n"
            + "D 10\n";
        String expect = ""
            + "    rebuild B\n"
            + "    rebuild A\n";

        doTest(makefile, fileInfo, "A", expect);
    }

    @Test
    public void testNoRebuild() throws Exception {
        String makefile = ""
            + "A: B\n"
            + "    rebuild A\n"
            + "B: C D\n"
            + "    rebuild B\n";
        String fileInfo = "100\n"
            + "B 15\n"
            + "C 10\n"
            + "D 10\n";
        String expect = ""
            + "    rebuild A\n";

        doTest(makefile, fileInfo, "A", expect);
    }


    private void doTest(String make, String info, String target, String expect)
            throws Exception {
        try (Writer makeWriter = Files.newBufferedWriter(makefilePath);
             Writer infoWriter = Files.newBufferedWriter(fileInfoPath)) {
            makeWriter.write(make);
            makeWriter.flush();
            infoWriter.write(info);
            infoWriter.flush();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Main.main("-f", makefileName, "-D", fileInfoName, target);

        String output = outputStream.toString(StandardCharsets.UTF_8.name());
        assertEquals(expect, output);
    }
}
