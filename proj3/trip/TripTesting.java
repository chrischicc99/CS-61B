package trip;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TripTesting {
    private String mapName = "map.tmp";
    private String outName = "out.tmp";

    private Path mapPath = Paths.get(mapName);
    private Path outPath = Paths.get(outName);

    @Before
    public void setUp() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.delete(mapPath);
                Files.delete(outPath);
            } catch (IOException ex) {
                System.err.printf("Error deleting temp files: %s",
                        ex.getMessage());
            }
        }));
    }

    @Test
    public void testTest() throws Exception {
        String map = "L Berkeley -6736.99 2613.16\n"
            + "L C1 -6737.24 2613.16\n"
            + "L C2 -6737.14 2611.94\n"
            + "L C3a -6738.61 2611.34\n"
            + "L C3 -6738.35 2610.08\n"
            + "L San_Francisco -6744.56 2606.13\n"
            + "L C4 -6726 2581.16\n"
            + "L C4a -6725.18 2575.95\n"
            + "L C4b -6719.74 2570.59\n"
            + "L Santa_Cruz -6723.68 2551.99\n"
            + "L C6 -6723.4 2552.21\n"
            + "\n"
            + "R Berkeley University_Ave 0.3196 EW C1\n"
            + "R C1 Martin_Luther_King_Jr_Way 1.6680 NS C2\n"
            + "R C2 Ashby_Ave 2.6550 EW C3a\n"
            + "R C3a I-580 1.5255 EW C3\n"
            + "R C3 I-80 7.8314 EW San_Francisco\n"
            + "R San_Francisco US-101 32.7900 NS C4\n"
            + "R C4 CA-85 5.5036 NS C4a\n"
            + "R C4a CA-85 7.8842 NS C4b\n"
            + "R C4b CA-17 18.9901 NS C6\n"
            + "R Santa_Cruz CA-1 0.3887 SN C6\n";
        String target = "Berkeley San_Francisco Santa_Cruz";
        String expect = "From Berkeley:\n"
            + "\n"
            + "1. Take University_Ave west for 0.3 miles.\n"
            + "2. Take Martin_Luther_King_Jr_Way south for 1.7 miles.\n"
            + "3. Take Ashby_Ave west for 2.7 miles.\n"
            + "4. Take I-580 west for 1.5 miles.\n"
            + "5. Take I-80 west for 7.8 miles to San_Francisco.\n"
            + "6. Take US-101 south for 32.8 miles.\n"
            + "7. Take CA-85 south for 13.4 miles.\n"
            + "8. Take CA-17 south for 19.0 miles.\n"
            + "9. Take CA-1 south for 0.4 miles to Santa_Cruz.";

        doTest(map, target, expect);
    }

    @Test
    public void testMerge() throws Exception {
        String map = ""
            + "L A 1 1\n"
            + "L B 2 2\n"
            + "L C 3 3\n"
            + "L D 4 4\n"
            + "L E 5 5\n"
            + "\n"
            + "R A R1 2 SN B\n"
            + "R B R1 2 SN C\n"
            + "R C R1 2 SN D\n"
            + "R D R1 2 SN E\n";
        String target = ""
            + "A E";
        String expect = ""
            + "From A:\n"
            + "\n"
            + "1. Take R1 north for 8.0 miles to E.";

        doTest(map, target, expect);
    }

    private void doTest(String map, String target, String expect)
            throws Exception {
        try (Writer makeWriter = Files.newBufferedWriter(mapPath)) {
            makeWriter.write(map);
            makeWriter.flush();
        }

        String[] dest = target.split(" ");
        List<String> args = new ArrayList<>();
        args.add("-m");
        args.add(mapName);
        args.add("-o");
        args.add(outName);
        args.addAll(Arrays.asList(dest));
        Main.main(args.toArray(new String[0]));

        String output = String.join("\n", Files.readAllLines(outPath));
        assertEquals(expect, output);
    }
}
