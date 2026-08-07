package CACIE.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Parsed, validated representation of a CACIE genetic-program configuration. */
public final class GPConfig {
  private final LinkedHashMap<String, Integer> nodeWeights = new LinkedHashMap<String, Integer>();
  private int maxDepth = -2;
  private int chromosomeMaxLength = 15;
  private int chromosomeMinLength = 10;
  private int mutationReplacingNtOffset = 0;
  private String logFileName = "LogData/fitnes_time_distances.log";
  private boolean keepIndividuals;
  private String keepStyle = "ALL";
  private String keepDirectory = "LogData/Individuals/";
  private final ArrayList<String> warnings = new ArrayList<String>();

  public GPConfig() {
    for (NodeDefinition node : NodeDefinition.generationNodes()) nodeWeights.put(node.getSymbol(), 0);
  }

  public static GPConfig load(Path path) throws IOException { return load(Files.newInputStream(path)); }

  public static GPConfig load(InputStream input) throws IOException {
    GPConfig result = new GPConfig();
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    StringBuilder text = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) text.append(line).append('\n');
    result.parse(text.toString());
    return result;
  }

  private void parse(String text) throws IOException {
    String normalized = text.replace("\r", " ").replace("\n", " ");
    int functionAt = normalized.indexOf("FunctionList");
    int configAt = normalized.indexOf("ConfigList");
    if (functionAt < 0 || configAt < 0 || configAt < functionAt)
      throw new IOException("FunctionList or ConfigList is missing.");
    String functions = normalized.substring(functionAt + "FunctionList".length(), configAt);
    int functionEnd = functions.indexOf("\\e");
    if (functionEnd < 0) throw new IOException("FunctionList has no \\e terminator.");
    for (String symbol : functions.substring(0, functionEnd).trim().split("\\s+")) {
      if (symbol.length() == 0) continue;
      if (NodeDefinition.isGenerationNode(symbol)) nodeWeights.put(symbol, nodeWeights.get(symbol) + 1);
      else if (isRemovedOutputNode(symbol)) warnings.add("Removed output node ignored: " + symbol);
      else warnings.add("Unknown node ignored: " + symbol);
    }
    String configs = normalized.substring(configAt + "ConfigList".length());
    int pos = 0;
    while ((pos = configs.indexOf("CONFIG:", pos)) >= 0) {
      int end = configs.indexOf("\\e", pos);
      if (end < 0) throw new IOException("CONFIG line has no \\e terminator near: " + configs.substring(pos).trim());
      parseConfigLine(configs.substring(pos + 7, end).trim());
      pos = end + 2;
    }
    validate();
  }

  private static boolean isRemovedOutputNode(String s) {
    return s.equals("BARFIX") || s.equals("BARFIX44") || s.equals("BARFIX34") || s.equals("BARFIX68")
        || s.equals("SCALE") || s.equals("HARMONIZE") || s.equals("BOSSAHARMONIZE");
  }

  private void parseConfigLine(String line) {
    String[] p = line.split("\\s+");
    if (p.length == 0) return;
    try {
      if (p[0].equals("MAX_DEPTH")) maxDepth = Integer.parseInt(p[1]);
      else if (p[0].equals("CHROMOSOME_MAXLENGTH")) chromosomeMaxLength = Integer.parseInt(p[1]);
      else if (p[0].equals("CHROMOSOME_MINLENGTH")) chromosomeMinLength = Integer.parseInt(p[1]);
      else if (p[0].equals("MUTATION_REPLACING_NT_OFFSET")) mutationReplacingNtOffset = Integer.parseInt(p[1]);
      else if (p[0].equals("LOG_FILE_NAME")) logFileName = p[1];
      else if (p[0].equals("KEEP_INDIVIDUAL")) {
        keepIndividuals = p.length > 1 && p[1].equals("ON");
        if (p.length > 2) keepStyle = p[2];
        if (p.length > 3) keepDirectory = p[3];
      } else if (p[0].equals("RHYTHM_FILTER") || p[0].equals("SCALE_FILTER")
          || p[0].equals("CHORD_HARMONIZE") || p[0].equals("CHORD_FIX")
          || p[0].equals("CHORD_FILTER")) warnings.add("Removed setting ignored: " + p[0]);
      else warnings.add("Unknown setting ignored: " + p[0]);
    } catch (RuntimeException ex) { warnings.add("Invalid setting ignored: " + line); }
  }

  public void validate() throws IOException {
    if (chromosomeMinLength < 1) throw new IOException("CHROMOSOME_MINLENGTH must be at least 1.");
    if (chromosomeMaxLength < chromosomeMinLength)
      throw new IOException("CHROMOSOME_MAXLENGTH must be >= CHROMOSOME_MINLENGTH.");
    if (mutationReplacingNtOffset < 0) throw new IOException("MUTATION_REPLACING_NT_OFFSET must be >= 0.");
    int sum = 0; for (Integer weight : nodeWeights.values()) sum += weight;
    if (sum == 0) throw new IOException("At least one generation node must be enabled.");
  }

  public void save(Path path) throws IOException {
    validate();
    Files.write(path, toText().getBytes(StandardCharsets.UTF_8));
  }

  public String toText() {
    StringBuilder out = new StringBuilder("FunctionList");
    for (Map.Entry<String,Integer> e : nodeWeights.entrySet())
      for (int i=0; i<e.getValue(); i++) out.append(' ').append(e.getKey());
    out.append(" \\e\n\nConfigList\n");
    out.append("CONFIG: MAX_DEPTH ").append(maxDepth).append(" \\e\n");
    out.append("CONFIG: CHROMOSOME_MAXLENGTH ").append(chromosomeMaxLength).append(" \\e\n");
    out.append("CONFIG: CHROMOSOME_MINLENGTH ").append(chromosomeMinLength).append(" \\e\n");
    out.append("CONFIG: MUTATION_REPLACING_NT_OFFSET ").append(mutationReplacingNtOffset).append(" \\e\n");
    out.append("CONFIG: LOG_FILE_NAME ").append(logFileName).append(" \\e\n");
    out.append("CONFIG: KEEP_INDIVIDUAL ").append(keepIndividuals ? "ON" : "OFF");
    if (keepIndividuals) out.append(' ').append(keepStyle).append(' ').append(keepDirectory);
    return out.append(" \\e\n\\e\n").toString();
  }

  public ArrayList<String> toOperatorArray() {
    ArrayList<String> result = new ArrayList<String>();
    for (Map.Entry<String,Integer> e : nodeWeights.entrySet()) for(int i=0;i<e.getValue();i++) result.add(e.getKey());
    return result;
  }
  public ArrayList<String> toLegacyConfigArray() {
    ArrayList<String> r=new ArrayList<String>();
    r.add(" MAX_DEPTH "+maxDepth); r.add(" CHROMOSOME_MAXLENGTH "+chromosomeMaxLength);
    r.add(" CHROMOSOME_MINLENGTH "+chromosomeMinLength); r.add(" MUTATION_REPLACING_NT_OFFSET "+mutationReplacingNtOffset);
    r.add(" LOG_FILE_NAME "+logFileName);
    r.add(" KEEP_INDIVIDUAL "+(keepIndividuals ? "ON "+keepStyle+" "+keepDirectory : "OFF")); return r;
  }
  public Map<String,Integer> getNodeWeights(){return nodeWeights;}
  public List<String> getWarnings(){return warnings;}
  public int getMaxDepth(){return maxDepth;} public void setMaxDepth(int v){maxDepth=v;}
  public int getChromosomeMaxLength(){return chromosomeMaxLength;} public void setChromosomeMaxLength(int v){chromosomeMaxLength=v;}
  public int getChromosomeMinLength(){return chromosomeMinLength;} public void setChromosomeMinLength(int v){chromosomeMinLength=v;}
  public int getMutationReplacingNtOffset(){return mutationReplacingNtOffset;} public void setMutationReplacingNtOffset(int v){mutationReplacingNtOffset=v;}
  public String getLogFileName(){return logFileName;} public void setLogFileName(String v){logFileName=v;}
  public boolean isKeepIndividuals(){return keepIndividuals;} public void setKeepIndividuals(boolean v){keepIndividuals=v;}
  public String getKeepStyle(){return keepStyle;} public void setKeepStyle(String v){keepStyle=v;}
  public String getKeepDirectory(){return keepDirectory;} public void setKeepDirectory(String v){keepDirectory=v;}
}
