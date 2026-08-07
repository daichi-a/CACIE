package CACIE;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import CACIE.config.GPConfig;
import CACIE.config.NodeDefinition;

/** Stand-alone editor for genetic-program parameters and node weights. */
public final class CACIE_ConfigEditor extends JFrame {
  private GPConfig model; private Path file;
  private final JSpinner depth=new JSpinner(new SpinnerNumberModel(-2,-100,0,1));
  private final JSpinner min=new JSpinner(new SpinnerNumberModel(10,1,10000,1));
  private final JSpinner max=new JSpinner(new SpinnerNumberModel(15,1,10000,1));
  private final JSpinner offset=new JSpinner(new SpinnerNumberModel(0,0,10000,1));
  private final JTextField log=new JTextField(24), directory=new JTextField(24);
  private final JCheckBox keep=new JCheckBox("Keep individuals");
  private final DefaultTableModel tableModel=new DefaultTableModel(new Object[]{"Use","Node","Description","Weight"},0){
    public Class<?> getColumnClass(int c){return c==0?Boolean.class:c==3?Integer.class:String.class;}
    public boolean isCellEditable(int r,int c){return c==0||c==3;}
  };
  private CACIE_ConfigEditor(Path initial){super("CACIE GP Config Editor");setDefaultCloseOperation(DISPOSE_ON_CLOSE);build();load(initial);setSize(700,650);setLocationByPlatform(true);setVisible(true);}
  private void build(){
    JPanel fields=new JPanel(new GridLayout(0,2,6,4));
    fields.add(new JLabel("Stack depth limit"));fields.add(depth);fields.add(new JLabel("Minimum chromosome length"));fields.add(min);fields.add(new JLabel("Maximum chromosome length"));fields.add(max);fields.add(new JLabel("Mutation nonterminal offset"));fields.add(offset);fields.add(new JLabel("Log file"));fields.add(log);fields.add(keep);fields.add(directory);
    for(NodeDefinition n:NodeDefinition.generationNodes())tableModel.addRow(new Object[]{false,n.getSymbol(),n.getDescription(),0});
    JPanel buttons=new JPanel(); JButton open=new JButton("Open"),validate=new JButton("Validate"),save=new JButton("Save"),saveAs=new JButton("Save As");
    open.addActionListener(e->open());validate.addActionListener(e->{if(pull())JOptionPane.showMessageDialog(this,"Configuration is valid.");});save.addActionListener(e->save(false));saveAs.addActionListener(e->save(true));buttons.add(open);buttons.add(validate);buttons.add(save);buttons.add(saveAs);
    JTable table=new JTable(tableModel);
    TableColumn weightColumn=table.getColumnModel().getColumn(3);
    weightColumn.setCellEditor(new DefaultCellEditor(new JComboBox<Integer>(new Integer[]{1,2,3,4,5,6,7,8,9,10})));
    add(fields,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);add(buttons,BorderLayout.SOUTH);
  }
  private void load(Path p){try{model=GPConfig.load(p);file=p;push();setTitle("CACIE GP Config Editor - "+p);if(!model.getWarnings().isEmpty())JOptionPane.showMessageDialog(this,String.join("\n",model.getWarnings()),"Migration warnings",JOptionPane.WARNING_MESSAGE);}catch(IOException e){model=new GPConfig();JOptionPane.showMessageDialog(this,e.getMessage(),"Config error",JOptionPane.ERROR_MESSAGE);}}
  private void push(){depth.setValue(model.getMaxDepth());min.setValue(model.getChromosomeMinLength());max.setValue(model.getChromosomeMaxLength());offset.setValue(model.getMutationReplacingNtOffset());log.setText(model.getLogFileName());keep.setSelected(model.isKeepIndividuals());directory.setText(model.getKeepDirectory());for(int r=0;r<tableModel.getRowCount();r++){String s=(String)tableModel.getValueAt(r,1);int w=model.getNodeWeights().get(s);tableModel.setValueAt(w>0,r,0);tableModel.setValueAt(w,r,3);}}
  private boolean pull(){try{model.setMaxDepth((Integer)depth.getValue());model.setChromosomeMinLength((Integer)min.getValue());model.setChromosomeMaxLength((Integer)max.getValue());model.setMutationReplacingNtOffset((Integer)offset.getValue());model.setLogFileName(log.getText().trim());model.setKeepIndividuals(keep.isSelected());model.setKeepDirectory(directory.getText().trim());for(int r=0;r<tableModel.getRowCount();r++){String s=(String)tableModel.getValueAt(r,1);boolean on=(Boolean)tableModel.getValueAt(r,0);int w=Math.max(0,((Number)tableModel.getValueAt(r,3)).intValue());model.getNodeWeights().put(s,on?Math.max(1,w):0);}model.validate();return true;}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Validation error",JOptionPane.ERROR_MESSAGE);return false;}}
  private File initialDirectory(Path path){File parent=path==null?null:path.toAbsolutePath().toFile().getParentFile();return parent==null?new File("."):parent;}
  private void open(){JFileChooser c=new JFileChooser(initialDirectory(file));if(c.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)load(c.getSelectedFile().toPath());}
  private void save(boolean as){if(!pull())return;Path target=file;if(as||target==null){JFileChooser c=new JFileChooser(initialDirectory(target));if(c.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;target=c.getSelectedFile().toPath();}try{model.save(target);file=target;setTitle("CACIE GP Config Editor - "+target);}catch(IOException e){JOptionPane.showMessageDialog(this,e.getMessage(),"Save error",JOptionPane.ERROR_MESSAGE);}}
  public static void main(String[] args){SwingUtilities.invokeLater(()->new CACIE_ConfigEditor(Paths.get(args.length==0?"CACIE_DefaultConfigs.config":args[0])));}
}
