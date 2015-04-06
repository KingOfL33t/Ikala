
package com.ikalagaming.core;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.ikalagaming.packages.PackageState;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;

/**
 * Displays various information about the program.
 * 
 * @author Ches Burks
 *
 */
public class TaskManager extends JFrame {

	private static final long serialVersionUID = -4427516209866980363L;
	private JPanel contentPane;
	private JTable table;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private long delay = 1000;// low long to wait between updates
	private DefaultTableModel model;
	private Map<String, PackageState> packages =
			new HashMap<String, PackageState>();
	private final int maxTick = 10;
	private int tickCount = 0;// counts down to refreshing package list
	private JLabel threads;
	private JLabel memUsage;
	private long memUsed = 0;
	private long percentUsed = 0;

	/**
	 * Updates and displays information about the program
	 */
	public void tick() {
		if (tickCount == 0) {
			updatePackageNames();
			tickCount = maxTick;
		}
		--tickCount;

		PackageState currentState;
		String name = "";
		for (int i = 0; i < model.getRowCount(); ++i) {
			name = (String) model.getValueAt(i, 0);
			currentState =
					Game.getPackageManager().getPackage(name).getPackageState();
			if (!model.getValueAt(i, 1).equals(currentState)) {
				model.setValueAt(currentState, i, 1);
			}
		}
		threads.setText(java.lang.Thread.activeCount() + "");

		memUsed =
				(Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
						.freeMemory()) / 1024;
		percentUsed =
				(Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
						.freeMemory())
						* 100
						/ Runtime.getRuntime().totalMemory();

		memUsage.setText(memUsed + " kb (" + percentUsed + "%)");
	}

	private void updatePackageNames() {
		Set<String> packageNames =
				Game.getPackageManager().getLoadedPackages().keySet();
		for (String s : packageNames) {
			if (!packages.containsKey(s)) {
				packages.put(s, Game.getPackageManager().getPackage(s)
						.getPackageState());
				boolean exists = false;
				for (int i = 0; i < model.getRowCount(); ++i) {
					if (model.getValueAt(i, 0).equals(s)) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					model.addRow(new Object[] {s, packages.get(s)});
				}
			}
		}
		for (String s : packages.keySet()) {
			if (!packageNames.contains(s)) {
				packages.remove(s);
				for (int i = 0; i < model.getRowCount(); ++i) {
					if (model.getValueAt(i, 0).equals(s)) {
						model.removeRow(i);
						break;
					}
				}
			}
		}
		packageNames = null;

	}

	/**
	 * Returns the length of time to wait between refreshes.
	 * 
	 * @return the delay time in ms
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * Attempts to change the status of the currently selected package. This may
	 * be any of the following:
	 * <ul>
	 * <li>Enable</li>
	 * <li>Disable</li>
	 * <li>Load</li>
	 * <li>Unload</li>
	 * </ul>
	 * 
	 * @param change the change to make
	 */
	public void changeState(String change) {
		int row = table.getSelectedRow();
		int column = -1;
		for (int i = 0; i < table.getColumnCount(); ++i) {
			if (table.getColumnName(i).equals("Package Name")) {
				column = i;
			}
		}
		if (row == -1 || column == -1) {
			return;
		}
		com.ikalagaming.packages.Package pack =
				Game.getPackageManager().getPackage(
						table.getValueAt(row, column).toString());
		if (pack == null) {
			return;
		}
		if (change == "Enable") {
			if (!pack.isEnabled()) {
				pack.enable();
			}
		}
		else if (change == "Disable") {
			if (pack.isEnabled()) {
				pack.disable();
			}
		}
		else if (change == "Unload") {
			pack.onUnload();
		}
		else if (change == "Reload") {
			pack.reload();
		}

	}

	/**
	 * Create the frame.
	 * 
	 * @param owner the game this task manager handles
	 */
	public TaskManager(Game owner) {
		setTitle("KOI Task Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 465);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNewPackage = new JMenuItem("New Package");
		mnFile.add(mntmNewPackage);

		JMenuItem mntmExitTaskManager = new JMenuItem("Exit Task Manager");
		mnFile.add(mntmExitTaskManager);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		JMenuItem mntmUpdateNow = new JMenuItem("Update now");
		mnView.add(mntmUpdateNow);

		JMenu mnUpdateSpeed = new JMenu("Update Speed");
		mnView.add(mnUpdateSpeed);

		JRadioButtonMenuItem rdbtnmntmHigh = new JRadioButtonMenuItem("High");
		rdbtnmntmHigh.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					delay = 500;
				}
			}
		});
		buttonGroup.add(rdbtnmntmHigh);
		mnUpdateSpeed.add(rdbtnmntmHigh);

		JRadioButtonMenuItem rdbtnmntmNormal =
				new JRadioButtonMenuItem("Normal");
		rdbtnmntmNormal.setSelected(true);
		rdbtnmntmNormal.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					delay = 1000;
				}
			}
		});
		buttonGroup.add(rdbtnmntmNormal);
		mnUpdateSpeed.add(rdbtnmntmNormal);

		JRadioButtonMenuItem rdbtnmntmLow = new JRadioButtonMenuItem("Low");
		rdbtnmntmLow.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					delay = 2000;
				}
			}
		});
		buttonGroup.add(rdbtnmntmLow);
		mnUpdateSpeed.add(rdbtnmntmLow);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Packages", null, scrollPane, null);

		table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"Package Name", "Status"}));
		table.getColumnModel().getColumn(0).setPreferredWidth(102);

		JPopupMenu popupMenu_1 = new JPopupMenu();

		JMenuItem mntmEnable = new JMenuItem("Enable");
		mntmEnable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				changeState("Enable");
			}
		});
		popupMenu_1.add(mntmEnable);

		JMenuItem mntmDisable = new JMenuItem("Disable");
		mntmDisable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				changeState("Disable");
			}
		});
		popupMenu_1.add(mntmDisable);

		JMenuItem mntmUnload = new JMenuItem("Unload");
		mntmUnload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				changeState("Unload");
			}
		});
		popupMenu_1.add(mntmUnload);

		JMenuItem mntmReload = new JMenuItem("Reload");
		mntmReload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				changeState("Reload");
			}
		});
		popupMenu_1.add(mntmReload);

		addPopup(table, popupMenu_1);

		scrollPane.setViewportView(table);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));

		JLabel lblMemoryUsage = new JLabel("Memory Usage:");
		panel_1.add(lblMemoryUsage);

		memUsage = new JLabel("0");
		panel_1.add(memUsage);

		JLabel lblThreads = new JLabel("Threads:");
		panel_1.add(lblThreads);

		threads = new JLabel("0");
		panel_1.add(threads);
		model = (DefaultTableModel) table.getModel();

		Updater update = new Updater(this);
		update.start();
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());

				if (e.getSource() instanceof JTable) {
					JTable source = (JTable) e.getSource();
					int row = source.rowAtPoint(e.getPoint());
					int column = source.columnAtPoint(e.getPoint());

					if (!source.isRowSelected(row))
						source.changeSelection(row, column, false, false);
				}
			}
		});
	}
}

class Updater extends Thread {
	TaskManager manager;

	public Updater(TaskManager manager) {
		setName("TaskMgrUpdateThread");
		this.manager = manager;
	}

	@Override
	public void run() {
		while (true) {
			manager.tick();
			try {
				sleep(manager.getDelay());
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
