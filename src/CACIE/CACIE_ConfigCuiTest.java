package CACIE;

import java.nio.file.Files;
import java.nio.file.Path;
import CACIE.config.GPConfig;

/** Headless round-trip test for the GP configuration format. */
public final class CACIE_ConfigCuiTest {
  private static void check(boolean value, String message) {
    if (!value) throw new IllegalStateException(message);
  }
  public static void main(String[] args) throws Exception {
    System.setProperty("java.awt.headless", "true");
    Path source=Path.of(args.length>0?args[0]:"CACIE_DefaultConfigs.config");
    Path output=args.length>1?Path.of(args[1]):Files.createTempFile("cacie-config-roundtrip-", ".config");
    GPConfig first=GPConfig.load(source);
    first.save(output);
    GPConfig second=GPConfig.load(output);
    check(first.toText().equals(second.toText()),"round-trip changed the configuration");
    String text=second.toText();
    check(!text.contains("SCALE_FILTER"),"SCALE_FILTER was written");
    check(!text.contains("RHYTHM_FILTER"),"RHYTHM_FILTER was written");
    check(!text.contains("CHORD_HARMONIZE"),"CHORD_HARMONIZE was written");
    check(!text.contains("CHORD_FIX"),"CHORD_FIX was written");
    check(!text.contains("BARFIX44")&&!text.contains(" HARMONIZE "),"removed node was written");
    check(!second.toOperatorArray().isEmpty(),"node list is empty");
    System.out.println("PASS config parse/save/reload: "+output.toAbsolutePath());
  }
}
