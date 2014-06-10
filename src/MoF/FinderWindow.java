package MoF;


import amidst.Amidst;
import amidst.auto.SeedFinder;
import amidst.auto.IThreadListener;
import amidst.gui.menu.AmidstMenu;
import amidst.minecraft.Biome;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FinderWindow extends JFrame implements IThreadListener{
	private static final long serialVersionUID = 196896954675968191L;
	public static FinderWindow instance;
	private Container pane;
	public Project curProject;  //TODO
	public static boolean dataCollect;
	private final AmidstMenu menuBar;
	private SeedFinder finder;
	public FinderWindow() {
		//Initialize window
		super("Amidst v" + Amidst.version());
		
		setSize(1000,800);
		//setLookAndFeel();
		pane = getContentPane();
		//UI Manager:
		pane.setLayout(new BorderLayout());
		new UpdateManager(this, true).start();
		setJMenuBar(menuBar = new AmidstMenu(this));
		setVisible(true);
		setIconImage(Amidst.icon);
		instance = this;
		initializeFinder();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}
	
	private void initializeFinder() {
		if (finder == null) {
			finder = new SeedFinder();
			finder.addListener(this);
		}
		finder.addBiome(Biome.mushroomIsland);
	}
	
	public void clearProject() {
		// FIXME Release resources.
		if (curProject != null) {
			removeKeyListener(curProject.getKeyListener());
			curProject.dispose();
			pane.remove(curProject);
			System.gc();
		}
	}
	public void setProject(Project ep) {
		menuBar.mapMenu.setEnabled(true);
		curProject = ep;

		addKeyListener(ep.getKeyListener());
		pane.add(curProject, BorderLayout.CENTER);
		
		this.validate();
	}
	
	public void startFinder(String type) {
		if (finder != null) {
			clearProject();
			finder.start(type);
		}
	}
	
	public void stopFinder() {
		if (finder != null) {
			finder.stop();
		}
	}
	
	public void started() {
		
	}
	
	public void stopped() {
		
	}
	
	public void completed() {
		Project project = new Project(finder.getSeed(), finder.getType());
		setProject(project);
	}
}
