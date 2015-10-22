package de.webthing.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.webthing.client.Client;
import de.webthing.client.ClientFactory;

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;

public class ThingsClient extends JFrame {

	private static final long serialVersionUID = 479681876826299109L;
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JFileChooser fileChooser;
	
	private ClientFactory clientFactory;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ThingsClient frame = new ThingsClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	JFileChooser getJFileChooser() {
		if(this.fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		return fileChooser;
	}
	
	ClientFactory getClientFactory() {
		if(this.clientFactory == null) {
			clientFactory = new ClientFactory();
		}
		return clientFactory;
	}
	
	void addThingPanel(String fname, String tabTitle) {
		try {
			Client client = getClientFactory().getClient(fname);
			// CoapClientImpl cl = new CoapClientImpl();
			// cl.parse(fname);
			addThingPanel(client, tabTitle, fname);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not create panel for file '" + fname + "': " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void addThingPanel(URL url, String tabTitle) {
		try {
			Client client = getClientFactory().getClient(url);
			addThingPanel(client, tabTitle, url.toString());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not create panel for URL '" + url + "': " + e.getMessage(), "URL Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	void addThingPanel(Client cl, String tabTitle, String tip) throws FileNotFoundException, IOException {
		JPanel panelLed = new ThingPanelUI(cl);
		tabbedPane.addTab(tabTitle, null, new JScrollPane(panelLed), tip);
		tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
	}

	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ThingsClient() {
		// try to use system look and feel (if possible)
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
		}
		
		setTitle("ThingClient");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 650, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		// load led example (local)
		String jsonld = "jsonld" + File.separator + "led.jsonld";
		this.addThingPanel(jsonld, "LED (local)");
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Add more \"Things\" ... ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("Add JSON-LD File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JFileChooser.APPROVE_OPTION  == getJFileChooser().showOpenDialog(null) ) {
					File f = getJFileChooser().getSelectedFile();
					try {
						addThingPanel(f.getAbsolutePath(), f.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		panel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Add JSON-LD URL");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Create the JOptionPane.
				String msg = JOptionPane.showInputDialog("URL");
				if(msg != null) {
					try {
						URL url = new URL(msg);
						int ip = url.getPath().lastIndexOf("/");
						String tabTitle = ip > 0 ? url.getPath().substring(ip) : "msg";
						addThingPanel(url, tabTitle);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		panel.add(btnNewButton_1);
	}

}
