/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Siemens AG and the thingweb community
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.webthing.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import de.webthing.client.Client;
import de.webthing.client.ClientFactory;

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
	
	void addThingPanelFile(String fname, String tabTitle) {
		try {
			Client client = getClientFactory().getClientFile(fname);
			// CoapClientImpl cl = new CoapClientImpl();
			// cl.parse(fname);
			addThingPanel(client, tabTitle, fname);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not create panel for file '" + fname + "': " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void addThingPanelUrl(String uri, String tabTitle) {
		try {
			Client client = getClientFactory().getClientUrl(new URI(uri));
			addThingPanel(client, tabTitle, uri.toString());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not create panel for URI '" + uri + "': " + e.getMessage(), "URI Error", JOptionPane.ERROR_MESSAGE);
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
		this.addThingPanelFile(jsonld, "LED (local)");
		
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
						addThingPanelFile(f.getAbsolutePath(), f.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		panel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Add JSON-LD URI");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Create the JOptionPane.
				String url = JOptionPane.showInputDialog("URI");
				if(url != null) {
					// javas URL class can't yet handle coap
					// see java.net.MalformedURLException: unknown protocol: coap
					// URL url = new URL(msg);
					int ip = url.lastIndexOf("/");
					String tabTitle = ip > 0 ? url.substring(ip) : "msg";
					addThingPanelUrl(url, tabTitle);
				}
			}
		});
		panel.add(btnNewButton_1);
	}

}
