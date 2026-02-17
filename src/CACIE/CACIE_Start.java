package CACIE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import CACIE.ui.OperationWindows;
import CACIE.ui.OperationWindows_GUI;
import CACIE.ui.OperationWindows_GUI_OI;
import CACIE.ui.OperationWindows_GUI_OID5;
import CACIE.ui.GLSphereGUI.OperationWindows_GUI_GLSphere;
import CACIE.ui.ShoppingBasketGUI.OperationWindows_GUI_ShoppingBasket;
import CACIE.ui.ShoppingBasketGUI.OperationWindows_GUI_ShoppingBasketD5;

public class CACIE_Start extends CACIE_O
{
  static
  {
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
  }

  public static void main(String Args[])
  {
    constructGUI();
  }

  private static void constructGUI()
  {
    final StringBuilder interfaceSelection = new StringBuilder("OI"); // default classic
    final StringBuilder configFileSelection = new StringBuilder("./CACIE_DefaultConfigs.config"); // default classic
    final StringBuilder dataFileSelection = new StringBuilder("./CACIE_DefaultTerminals.data"); // default classic
    final JFrame frame = new JFrame("CACIE");
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    topPanel.add(new JLabel("Select a Interface and Parameter Files"), BorderLayout.NORTH);
    JPanel interfaceSelectionPanel = new JPanel(new GridLayout(3, 1));
    interfaceSelectionPanel.setBorder(BorderFactory.createTitledBorder("Interfaces"));
    JCheckBox btnInterface1 = new JCheckBox("OI");
    JCheckBox btnInterface2 = new JCheckBox("D");
    JCheckBox btnInterface3 = new JCheckBox("SB");
    JCheckBox btnInterface4 = new JCheckBox("OID5");
    JCheckBox btnInterface5 = new JCheckBox("DD5");
    JCheckBox btnInterface6 = new JCheckBox("SBD5");
    JCheckBox btnInterface7 = new JCheckBox("Classic");
        
    btnInterface1.setSelected(true);
    btnInterface1.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
         interfaceSelection.setLength(0);
        interfaceSelection.append("OI");
      }
    });
    btnInterface2.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("D");
      }
    });
    btnInterface3.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("SB");
      }
    });
    btnInterface3.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("SB");
      }
    });
    btnInterface4.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("OID5");
      }
    });
    btnInterface5.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("DD5");
      }
    });
    btnInterface6.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("SBD5");
      }
    });
    btnInterface7.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        interfaceSelection.setLength(0);
        interfaceSelection.append("Classic");
      }
    });

    
    ButtonGroup group = new ButtonGroup();
    group.add(btnInterface1);
    group.add(btnInterface2);
    group.add(btnInterface3);
    group.add(btnInterface4);
    group.add(btnInterface5);
    group.add(btnInterface6);
    group.add(btnInterface7);

    btnInterface1.setBackground(Color.WHITE);
    btnInterface2.setBackground(Color.WHITE);
    btnInterface3.setBackground(Color.WHITE);
    btnInterface4.setBackground(Color.WHITE);
    btnInterface5.setBackground(Color.WHITE);
    btnInterface6.setBackground(Color.WHITE);
    btnInterface7.setBackground(Color.WHITE);
    
    interfaceSelectionPanel.add(btnInterface1);
    interfaceSelectionPanel.add(btnInterface2);
    interfaceSelectionPanel.add(btnInterface3);
    interfaceSelectionPanel.add(btnInterface4);
    interfaceSelectionPanel.add(btnInterface5);
    interfaceSelectionPanel.add(btnInterface6);
    interfaceSelectionPanel.add(btnInterface7);
    
    topPanel.add(interfaceSelectionPanel, BorderLayout.WEST);

    JPanel parameterSelectionPanel = new JPanel(new GridLayout(5, 1, 20, 0));
    parameterSelectionPanel.setBorder(BorderFactory.createTitledBorder("Parameter Files"));
    JButton btnConfigFileRead = new JButton("Select Config File");
    JButton btnDataFileRead = new JButton("Select Data File");
    btnConfigFileRead.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        String configFile = configFileSelector("Please Select Config File.");
        configFileSelection.setLength(0);
        configFileSelection.append(configFile);
      }
    });
    btnDataFileRead.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        String dataFile = configFileSelector("Please Select Config File.");
        dataFileSelection.setLength(0);
        dataFileSelection.append(dataFile);
      }
    });
    parameterSelectionPanel.add(new JLabel());
    parameterSelectionPanel.add(btnConfigFileRead);
    parameterSelectionPanel.add(new JLabel());
    parameterSelectionPanel.add(btnDataFileRead);
    parameterSelectionPanel.add(new JLabel());
    topPanel.add(parameterSelectionPanel, BorderLayout.EAST);
    
    JButton btnStart = new JButton("Start");
    btnStart.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        configures(configFileSelection.toString(), dataFileSelection.toString());
        System.out.println("Interface = " + interfaceSelection.toString());
        if( interfaceSelection.toString().equals("OI") )
        {
          new OperationWindows_GUI_OI(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("D") )
        {
          new OperationWindows_GUI(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("SB") )
        {
          new OperationWindows_GUI_ShoppingBasket(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("SB") )
        {
          new OperationWindows_GUI_ShoppingBasket(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("OID5") )
        {
          new OperationWindows_GUI_OID5(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("DD5") )
        {
          new OperationWindows_GUI_GLSphere(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("SBD5") )
        {
          new OperationWindows_GUI_ShoppingBasketD5(16, notes, oprList, confList);
        }
        else if( interfaceSelection.toString().equals("Classic") )
        {
          new OperationWindows(16, notes, oprList, confList);
        }
    
        
        frame.setVisible(false);
      }
    });
    topPanel.add(btnStart, BorderLayout.SOUTH);
    frame.add(topPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.pack();
  }
}
