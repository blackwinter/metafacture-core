package org.metafacture.metamorph;

import org.metafacture.io.LineReader;
import org.metafacture.json.JsonDecoder;
import org.metafacture.json.JsonEncoder;
import org.metafacture.strings.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public final class Issue366Test {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
            {0, null, "{\"r\":{\"k\":\"K\",\"v\":\"V\"}}\n"},
            {1, "",   "{\"r\":{\"v\":\"V\",\"k\":\"K\"}}\n"},
            {2, "??", "{\"r\":{\"k\":\"K\",\"v\":\"V\"}}\n"},
            {3, "?2", "{\"r\":{\"k\":\"K\",\"v\":\"V\"}}\n"}
        };
    }

    @Parameterized.Parameter(0)
    public int option;

    @Parameterized.Parameter(1)
    public String replacement;

    @Parameterized.Parameter(2)
    public String output;

    private static final String INPUT = "{\"10012\":{\"a\":\"V\"}}";

    private static final String MORPH = "" +
        "<metamorph xmlns=\"http://www.culturegraph.org/metamorph\" version=\"1\">\n" +
        "  <rules>\n" +
        "    <entity name=\"r\" flushWith=\"100??\">\n" +
        "      <choose flushWith=\"100%3$s%1$s\">\n" +
        "        <data name=\"k\" source=\"100%2$s.a\">\n" +
        "          <constant value=\"K\"/>\n" +
        "        </data>\n" +
        "      </choose>\n" +
        "      <data name=\"v\" source=\"100%3$s.a\" />\n" +
        "    </entity>\n" +
        "  </rules>\n" +
        "</metamorph>";

    private static final Object[] ARGS = new String[]{".a", "?2", "??"};

    private static final boolean VERBOSE = System.getProperty("issue366.verbose", "false").equals("true");
    private static final int ITER = Integer.valueOf(System.getProperty("issue366.iter", "10"));

    private final Map<String, LongAdder> outcome = new HashMap<>();
    private final Object[] args = Arrays.copyOf(ARGS, ARGS.length);

    @Test
    public void testTransform() {
        if (option > 0) {
            args[option - 1] = replacement;
        }

        final String morph = String.format(MORPH, args);
        IntStream.range(0, ITER).forEach(i -> transform(morph));

        if (VERBOSE) {
            System.out.println(morph);
            outcome.forEach((k, v) -> System.err.print(v.sum() + "/" + ITER + ": " + k));
        }

        Assert.assertEquals(1, outcome.size());
        Assert.assertEquals(output, outcome.keySet().iterator().next());
    }

    private void transform(final String morph) {
        final StringReader reader = new StringReader();
        final Writer writer = new StringWriter();

        @SuppressWarnings("deprecation")
        final org.metafacture.io.ObjectJavaIoWriter<String> ojiw =
            new org.metafacture.io.ObjectJavaIoWriter<>(writer);

        reader
            .setReceiver(new LineReader())
            .setReceiver(new JsonDecoder())
            .setReceiver(new Metamorph(new java.io.StringReader(morph)))
            .setReceiver(new JsonEncoder())
            .setReceiver(ojiw);

        try {
            reader.process(INPUT);
            reader.closeStream();

            outcome.computeIfAbsent(writer.toString(), k -> new LongAdder()).increment();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
