

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class AncestorTree extends JFrame {
	public static ImageIcon ICON_SELF = new ImageIcon(new Object(){}.getClass().getResource("/images/AddMQL.png"));
	public static ImageIcon ICON_MALE = new ImageIcon(new Object(){}.getClass().getResource("/images/command.gif"));
	public static ImageIcon ICON_FEMALE = new ImageIcon(new Object(){}.getClass().getResource("/images/exportExcel.png"));

	protected JTree m_tree;
	protected DefaultTreeModel m_model;
	protected IconCellRenderer m_renderer;
	protected IconCellEditor m_editor;

	public AncestorTree() {
		super("Ancestor Tree");
		setSize(400, 300);

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new IconData(ICON_SELF, "Myself"));
		addAncestors(top);
		m_model = new DefaultTreeModel(top);
		m_tree = new JTree(m_model);
		m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		m_tree.setShowsRootHandles(true);
		m_tree.setEditable(true);

		m_renderer = new IconCellRenderer();
		m_tree.setCellRenderer(m_renderer);
		m_editor = new IconCellEditor(m_tree);
		m_tree.setCellEditor(m_editor);
		m_tree.setInvokesStopCellEditing(true);

		m_tree.addMouseListener(new TreeExpander());

		JScrollPane s = new JScrollPane();
		s.getViewport().add(m_tree);
		getContentPane().add(s, BorderLayout.CENTER);

		WindowListener wndCloser = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(wndCloser);

		setVisible(true);
	}

	public boolean addAncestors(DefaultMutableTreeNode node) {
		if (node.getChildCount() > 0)
			return false;

		Object obj = node.getUserObject();
		if (obj == null)
			return false;
		node.add(new DefaultMutableTreeNode(new IconData(ICON_MALE, "Father of: " + obj.toString())));
		node.add(new DefaultMutableTreeNode(new IconData(ICON_FEMALE, "Mother of: " + obj.toString())));
		return true;
	}

	public static void main(String argv[]) {
		new AncestorTree();
	}

	class TreeExpander extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				TreePath selPath = m_tree.getPathForLocation(e.getX(), e.getY());
				if (selPath == null)
					return;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) (selPath.getLastPathComponent());
				if (node != null && addAncestors(node)) {
					m_tree.expandPath(selPath);
					m_tree.repaint();
				}
			}
		}
	}
}

class IconCellRenderer extends JLabel implements TreeCellRenderer {
	protected Color m_textSelectionColor;
	protected Color m_textNonSelectionColor;
	protected Color m_bkSelectionColor;
	protected Color m_bkNonSelectionColor;
	protected Color m_borderSelectionColor;

	protected boolean m_selected;

	public IconCellRenderer() {
		super();
		m_textSelectionColor = UIManager.getColor("Tree.selectionForeground");
		m_textNonSelectionColor = UIManager.getColor("Tree.textForeground");
		m_bkSelectionColor = UIManager.getColor("Tree.selectionBackground");
		m_bkNonSelectionColor = UIManager.getColor("Tree.textBackground");
		m_borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");
		setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus)

	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object obj = node.getUserObject();
		setText(obj.toString());
		if (obj instanceof IconData) {
			IconData idata = (IconData) obj;
			if (expanded)
				setIcon(idata.getOpenIcon());
			else
				setIcon(idata.getIcon());
		} else
			setIcon(null);

		setFont(tree.getFont());
		setForeground(sel ? m_textSelectionColor : m_textNonSelectionColor);
		setBackground(sel ? m_bkSelectionColor : m_bkNonSelectionColor);
		m_selected = sel;
		return this;
	}

	public void paint(Graphics g) {
		Color bColor = getBackground();
		Icon icon = getIcon();

		g.setColor(bColor);
		int offset = 0;
		if (icon != null && getText() != null)
			offset = (icon.getIconWidth() + getIconTextGap());
		g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);

		if (m_selected) {
			g.setColor(m_borderSelectionColor);
			g.drawRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
		}

		super.paint(g);
	}
}

class IconData {
	protected String sType;
	protected Icon m_icon;
	protected Icon m_openIcon;
	protected Object m_data;

	public IconData(Icon icon, Object data) {
		m_icon = icon;
		m_openIcon = null;
		m_data = data;
	}
	
	/**
	 * added by ihjang 
	 * @param icon
	 * @param data
	 * @param type
	 */
	public IconData(Icon icon, Object data, String type) {
		sType = type;
		m_icon = icon;
		m_openIcon = null;
		m_data = data;
	}

	public IconData(Icon icon, Icon openIcon, Object data) {
		m_icon = icon;
		m_openIcon = openIcon;
		m_data = data;
	}

	public Icon getIcon() {
		return m_icon;
	}

	public Icon getOpenIcon() {
		return m_openIcon != null ? m_openIcon : m_icon;
	}

	public Object getObject() {
		return m_data;
	}

	public String toString() {
		return m_data.toString();
	}
}

class IconCellEditor extends JLabel implements TreeCellEditor, ActionListener {
	protected JTree m_tree = null;
	protected JTextField m_editor = null;
	protected IconData m_item = null;
	protected int m_lastRow = -1;
	protected long m_lastClick = 0;
	protected Vector m_listeners = null;

	public IconCellEditor(JTree tree) {
		super();
		m_tree = tree;
		m_listeners = new Vector();
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object obj = node.getUserObject();
			if (obj instanceof IconData) {
				IconData idata = (IconData) obj;
				m_item = idata;
				// Reserve some more space...
				setText(idata.toString() + "     ");
				setIcon(idata.m_icon);
				setFont(tree.getFont());
				return this;
			}
		}
		// We don't support other objects...
		return null;
	}

	public Object getCellEditorValue() {
		if (m_item != null && m_editor != null)
			m_item.m_data = m_editor.getText();
		return m_item;
	}

	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) {
			MouseEvent mEvt = (MouseEvent) evt;
			if (mEvt.getClickCount() == 1) {
				int row = m_tree.getRowForLocation(mEvt.getX(), mEvt.getY());
				if (row != m_lastRow) {
					m_lastRow = row;
					m_lastClick = System.currentTimeMillis();
					return false;
				} else if (System.currentTimeMillis() - m_lastClick > 1000) {
					m_lastRow = -1;
					m_lastClick = 0;
					prepareEditor();
					mEvt.consume();
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	protected void prepareEditor() {
		if (m_item == null)
			return;
		String str = m_item.toString();

		m_editor = new JTextField(str);
		m_editor.addActionListener(this);
		m_editor.selectAll();
		m_editor.setFont(m_tree.getFont());

		add(m_editor);
		revalidate();

		TreePath path = m_tree.getPathForRow(m_lastRow);
		m_tree.startEditingAtPath(path);
	}

	protected void removeEditor() {
		if (m_editor != null) {
			remove(m_editor);
			m_editor.setVisible(false);
			m_editor = null;
			m_item = null;
		}
	}

	public void doLayout() {
		super.doLayout();
		if (m_editor != null) {
			int offset = getIconTextGap();
			if (getIcon() != null)
				offset += getIcon().getIconWidth();
			Dimension cSize = getSize();
			m_editor.setBounds(offset, 0, cSize.width - offset, cSize.height);
		}
	}

	public boolean shouldSelectCell(EventObject evt) {
		return true;
	}

	public boolean stopCellEditing() {
		if (m_item != null)
			m_item.m_data = m_editor.getText();

		ChangeEvent e = new ChangeEvent(this);
		for (int k = 0; k < m_listeners.size(); k++) {
			CellEditorListener l = (CellEditorListener) m_listeners.elementAt(k);
			l.editingStopped(e);
		}

		removeEditor();
		return true;
	}

	public void cancelCellEditing() {
		ChangeEvent e = new ChangeEvent(this);
		for (int k = 0; k < m_listeners.size(); k++) {
			CellEditorListener l = (CellEditorListener) m_listeners.elementAt(k);
			l.editingCanceled(e);
		}

		removeEditor();
	}

	public void addCellEditorListener(CellEditorListener l) {
		m_listeners.addElement(l);
	}

	public void removeCellEditorListener(CellEditorListener l) {
		m_listeners.removeElement(l);
	}

	public void actionPerformed(ActionEvent e) {
		stopCellEditing();
		m_tree.stopEditing();
	}
}
