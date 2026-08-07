package CACIE.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Nodes that may participate in genetic-program generation. */
public final class NodeDefinition {
  private final String symbol;
  private final String description;

  private NodeDefinition(String symbol, String description) {
    this.symbol = symbol;
    this.description = description;
  }

  public String getSymbol() { return symbol; }
  public String getDescription() { return description; }

  private static final List<NodeDefinition> GENERATION_NODES = Collections.unmodifiableList(Arrays.asList(
      new NodeDefinition("S", "Sequence"), new NodeDefinition("U", "Union"),
      new NodeDefinition("D", "Difference"), new NodeDefinition("A", "Augment"),
      new NodeDefinition("P", "Pitch shift"), new NodeDefinition("SA", "Scaled sequence"),
      new NodeDefinition("SP", "Shifted sequence"), new NodeDefinition("SD", "Delayed sequence"),
      new NodeDefinition("SR", "Repeat sequence"), new NodeDefinition("RSA", "Recursive augment"),
      new NodeDefinition("RSP", "Recursive pitch shift"), new NodeDefinition("RSD", "Recursive delay"),
      new NodeDefinition("MA", "Merge augment"), new NodeDefinition("MP", "Merge pitch"),
      new NodeDefinition("MD", "Merge delay"), new NodeDefinition("MS", "Merge sequence"),
      new NodeDefinition("MU", "Merge union"), new NodeDefinition("CAR", "Car"),
      new NodeDefinition("CDR", "Cdr"), new NodeDefinition("FILP", "Pitch filter"),
      new NodeDefinition("FILA", "Amplitude filter"), new NodeDefinition("ACML", "Accumulate"),
      new NodeDefinition("IV", "Invert"), new NodeDefinition("TP", "Transpose"),
      new NodeDefinition("RV", "Retrograde")));

  public static List<NodeDefinition> generationNodes() { return GENERATION_NODES; }

  public static boolean isGenerationNode(String symbol) {
    for (NodeDefinition node : GENERATION_NODES)
      if (node.symbol.equals(symbol)) return true;
    return false;
  }
}
