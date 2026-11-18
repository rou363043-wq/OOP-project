package ui;
import javax.swing.table.DefaultTableModel;

public class ReadOnlyTableModel extends DefaultTableModel{
  public ReadOnlyTableModel(String [] columns){
    super(columns, 0);
  }
  public boolean isCellEditable(int row,int column){
    return false;
  }
}
