package kr.co.atis.uiutil;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLDecoder;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.matrixone.apps.domain.util.FrameworkUtil;

import kr.co.atis.main.BusinessViewMain;
import kr.co.atis.util.SchemaConstants;
import matrix.util.StringList;

/**
 * URL Parse -> View Table
 * @author ihjang
 */
public class URLParseTable {
	
	JFrame jFrame = new JFrame("");

	String columnNames[] 	= { "KEY", "VALUE", "VIEW" };

	public URLParseTable() {}

	public URLParseTable(String sURL) {
		StringList slViewList	= SchemaConstants.getViewOption();
		sURL					= URLDecoder.decode(sURL);
		
		StringList slParseList 	= new StringList();

		// URL Parse - ex)..emxTable.jsp?table=emxTable
		StringList slHostList 	= FrameworkUtil.split(sURL, "?");
		slParseList.add(slHostList.get(0)); // Host
		slParseList.addAll(FrameworkUtil.split((String) slHostList.get(1), "&"));

		int iListSize = slParseList.size();
		// Data Setting
		Object rowData[][] = new Object[iListSize][3];

		rowData[0][0] = "Host";
		rowData[0][1] = (String) slParseList.get(0);
		for (int i = 1; i < iListSize; i++) {
			StringList slTemp = FrameworkUtil.split((String) slParseList.get(i), "=");
			rowData[i][0] = (String) slTemp.get(0);
			rowData[i][1] = (String) slTemp.get(1);
			rowData[i][2] = "...";
		}

		// DefaultTableModel을 선언하고 데이터 담기
		DefaultTableModel defaultTableModel = new DefaultTableModel(rowData, columnNames);

		// JTable에 DefaultTableModel을 담기
		JTable jTable = new JTable(defaultTableModel)
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {	// Row Color
				Component c = super.prepareRenderer(renderer, row, column);
				String sKey			= (String) getValueAt(row, 0);

				// Color row based on a cell value
				if (!isRowSelected(row)) {
					if (column < 2 || (column > 1 && !slViewList.contains(sKey.toLowerCase()))) {
						if ((row % 2) != 0)
							c.setBackground(new Color(220, 220, 220, 80));
						else 
							c.setBackground(Color.WHITE);
					}
				}

				return c;
			}
		};

		jTable.setRowHeight(30);

		jTable.getCellRenderer(0, 2);
		jTable.getColumn("VIEW").setCellRenderer(new ButtonRenderer());
		jTable.getColumn("VIEW").setCellEditor(new ButtonEditor(new JCheckBox()));

		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTable.getColumnModel().getColumn(0).setPreferredWidth(150);
		jTable.getColumnModel().getColumn(1).setPreferredWidth(570);
		jTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		// JScrollPane에 JTable을 담기
		JScrollPane jScollPane = new JScrollPane(jTable);

		// 행 한줄 추가!
		// Object [] temporaryObject = { 4, "초코송이", 500, "식품계의 절대강자" };
		// defaultTableModel.addRow(temporaryObject);

		// 행과 열 갯수 구하기
//		System.out.println(defaultTableModel.getRowCount());
//		System.out.println(defaultTableModel.getColumnCount());

		// 컬럼(열)의 index는 0부터 시작한다!!
//		System.out.println(defaultTableModel.getColumnName(0));

		// 0행을 삭제하면 제목행을 제외하고 첫째행을 삭제한다!!
		// defaultTableModel.removeRow(0);

		// 값을 얻어올 때도 0부터 index가 시작된다는 것에 주의한다!!
		// System.out.println(defaultTableModel.getValueAt(2, 2));

		// 특정 좌표의 값을 바꾸는 것은 setValueAt()
		// defaultTableModel.setValueAt("5000", 2, 2);

		// 테이블에 Row를 미리 선택한 상태로 만들기!
		// jTable.setRowSelectionInterval(1, 1);

		jFrame.add(jScollPane);

		jFrame.setSize(800, 600);
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
}

/**
 * @version 1.0 11/09/98
 */

//class ButtonRenderer extends JButton implements TableCellRenderer {
class ButtonRenderer implements TableCellRenderer {
	protected JButton button	= new JButton();
	protected JLabel label 		= new JLabel();
	
	public ButtonRenderer() {
		button.setOpaque(true);
		label.setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		StringList slViewList	= SchemaConstants.getViewOption();
		String sKey				= (String) table.getValueAt(row, 0);
		if(!slViewList.contains(sKey.toLowerCase()))
		{
			if (isSelected) {
				label.setForeground(table.getSelectionForeground());
				label.setBackground(table.getSelectionBackground());
			} else {
				label.setForeground(table.getForeground());
				label.setBackground(UIManager.getColor("Label.background"));
			}
			label.setText("");
			return label;
		} else {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(UIManager.getColor("Button.background"));
			}
			button.setText("...");
			return button;
		}
	}
}

/**
 * @version 1.0 11/09/98
 */

class ButtonEditor extends DefaultCellEditor {
	protected JButton button;
	protected JLabel jLabel;

	private String label;
	private int row;
	private int col;

	private boolean isPushed;

	public ButtonEditor(JCheckBox checkBox) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
		
		jLabel	= new JLabel();
		jLabel.setOpaque(true);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		StringList slViewList	= SchemaConstants.getViewOption();
		this.row			= row;
		this.col			= column;
		String sKey			= (String) table.getValueAt(row, 0);
		if(!slViewList.contains(sKey.toLowerCase()))
		{
			if (isSelected) {
				jLabel.setForeground(table.getSelectionForeground());
				jLabel.setBackground(table.getSelectionBackground());
			} else {
				jLabel.setForeground(table.getForeground());
				jLabel.setBackground(table.getBackground());
			}
			jLabel.setText("");
			isPushed = true;
			return jLabel;
		} else {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			label = "...";
			button.setText(label);
			isPushed = true;
			return button;
		}
	}

	public Object getCellEditorValue() {
		Map mChangeMap	= SchemaConstants.getViewOptionChange();
		JTable table 	= (JTable) button.getParent();
		if(null != table) {
			String sType	= (String) table.getValueAt(row, 0).toString().toLowerCase();
			String sOriType	= (String) mChangeMap.get(sType);
			String sName	= (String) table.getValueAt(row, 1);
		
			if (isPushed) {
				if(sOriType.equals("menu") || sOriType.equals("portal") || sOriType.equals("type"))
					new DialogSchemaChildrenView(BusinessViewMain.ctx1, sOriType, sName);
				else
					new DialogSchemaView(BusinessViewMain.ctx1, sOriType, sName);
			}
		}
		isPushed = false;
		return "";
	}

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}
