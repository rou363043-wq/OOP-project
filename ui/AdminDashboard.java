package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import storage.DataStore;

public calss AdminDashboard extends JFrame{
  private DataStore store;
  private LocalDateTime startTime;
  private LocalDateTime currentTime;

  private JLabel clockLabel = new JLabel("Not set");
  private JButton plusOneMinButton = new JButton("+1 minute");
  private final JLabel utilLabel = new JLabel("N/A");
  final JLabel[] gpuLabels = new JLabel[4];
  
  private DefaultTableModel waitingModel = new DefaultTableModel( new Object[]{"Job","Student","App","Duration","Pos.","Start","Wait"},0);
  private DefaultTableModel completedModel = new DefaultTableModel(new Object[]{"Job","Student","App","GPU","Start","Finish","Charged"},0);
  private DefaultTableModel allowanceModel = new DefaultTableModel(new Object[]{"Student","Allocated","Used","Reserved","Available"},0);

  public AdminDashboard(DataStore store, LocalDateTime startTime){
    super("GPU Utilization Dashboard");
    this.store=store;
    this.startTime=startTime;
    this.currentTime=startTime;
    setSize(950,650);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    buildUI();
    hookEvents();
    refresh();
  }

  private void buildUI(){
    setLayout(new BorderLayout(8,8));

    JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    top.add(new JLabel("Simulated time:"));
    top.add(clockLabel);
    top.add(plusOneMinButton);
    top.add(new JLabel("Utilization:"));
    top.add(utilLabel);
    add(top, BorderLayout.NORTH);

    JPanel gpuPanel(new GridLayout(1,4,8,8));
    for(int i = 0; i<4;i++){
      gpuLabels[i] = new JLabel("GPU " + (i+1)+": Free", JLabel.CENTER);
      gpuPanel.add(gpuLabels[i]);
    }

    JPanel tablesPanel = new JPanel(new GridLayout(3,1,8,8));
    tabelsPanel.add(new JScrollPane(new JTable(waitingModel)));
    tabelsPanel.add(new JScrollPane(new JTable(completedModel)));
    tabelsPanel.add(new JScrollPane(new JTable(allowanceModel)));

    JPanel center = new JPanel(new BorderLayout(8,8));
    center.add(gpuPanel, BorderLayout.NORTH);
    center.add(tablesPanel, BorderLayout.CENTER);
    add(center,BorderLayout.CENTER);
  }

  private void hookEvents(){
    plusOneMinButton.addActionListener(e->{ 
      if(currentTime == null){return;}
      //add advancemin from scheduler
      currentTime = currentTime.plusMinutes(1);
      saveNow();
      refresh();
        });

    addWindowListener(new WindowAdapter(){
      @Override
      public void windowClosing(WindowEvent e){
        saveNow();
        dispose();
        System.exit(0);
      }
    });
  }

  private void saveNow(){
    try{
      store.saveAll(currentTime);
    }catch (Exception ex){
      showError(ex.getMessage());
    }
  }

  public void showError(String msg)
    JOptionPane.showMessageDialog(this,msg+"\nFix or remove the file, then restart.","File error", JOptionPane.ERROR_MESSAGE);
  

public void refresh(){
  plusOneMinButton.setEnabled(currentTime != null);
  clockLabel.setText(currentTime == null ? "Not set": currentTime.format(DataStore.FORMAT));
  //update gpu labels n models
  updateUtilization(0);
}

private void updateUtilization(long busyGpuMinutes){
  if(startTime == null || currentTime == null){
  utilLabel.setText("N/A");
  return;
  }
long elapsed = ChronoUnit.MINUTES.between(startTime, currentTime);
if(elapsed<=0){
  utilLabel.setText("N/A");
  return;
}
double util = (busyGpuMinutes/(4.0*elapsed))*100.0;
utilLabel.setText(String.format("%.1f%%",util));
}

}
