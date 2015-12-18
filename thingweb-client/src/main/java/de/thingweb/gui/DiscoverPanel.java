package de.thingweb.gui;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.PlainDocument;

import org.json.JSONArray;

import de.thingweb.client.Client;
import de.thingweb.desc.DescriptionParser;
import de.thingweb.desc.pojo.ThingDescription;
import de.thingweb.gui.text.HintTextFieldUI;
import de.thingweb.gui.text.IntegerRangeDocumentFilter;
import thingweb.discovery.TDRepository;

import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.awt.event.ActionEvent;

public class DiscoverPanel extends JPanel {
	
	final ThingsClient thingsClient;
	
	public DiscoverPanel(ThingsClient thingsClient) {
		this.thingsClient = thingsClient;
		setBorder(new TitledBorder(null, "Discovery options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{147, 86, 65, 0};
		gridBagLayout.rowHeights = new int[]{23, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("Repository URI/IP");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		textFieldIP = new JTextField();
		textFieldIP.setText("localhost");
		GridBagConstraints gbc_textFieldIP = new GridBagConstraints();
		gbc_textFieldIP.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldIP.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldIP.gridx = 1;
		gbc_textFieldIP.gridy = 0;
		add(textFieldIP, gbc_textFieldIP);
		textFieldIP.setColumns(100);
		
		JLabel lblPort = new JLabel("Repository Port:");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 1;
		add(lblPort, gbc_lblPort);
		
		textFieldPort = new JTextField();
		textFieldPort.setText("3030");
		GridBagConstraints gbc_textFieldPort = new GridBagConstraints();
		gbc_textFieldPort.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldPort.gridx = 1;
		gbc_textFieldPort.gridy = 1;
		add(textFieldPort, gbc_textFieldPort);
		textFieldPort.setColumns(100);
		PlainDocument pd = (PlainDocument) textFieldPort.getDocument();
		pd.setDocumentFilter(new IntegerRangeDocumentFilter(BigInteger.ZERO, BigInteger.valueOf(65535)));
		
		JLabel lblFreeSearchText = new JLabel("Free search text:");
		GridBagConstraints gbc_lblFreeSearchText = new GridBagConstraints();
		gbc_lblFreeSearchText.anchor = GridBagConstraints.EAST;
		gbc_lblFreeSearchText.insets = new Insets(0, 0, 5, 5);
		gbc_lblFreeSearchText.gridx = 0;
		gbc_lblFreeSearchText.gridy = 2;
		add(lblFreeSearchText, gbc_lblFreeSearchText);
		
		textFieldFreeText = new JTextField();
		GridBagConstraints gbc_textFieldFreeText = new GridBagConstraints();
		gbc_textFieldFreeText.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldFreeText.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldFreeText.gridx = 1;
		gbc_textFieldFreeText.gridy = 2;
		add(textFieldFreeText, gbc_textFieldFreeText);
		textFieldFreeText.setColumns(100);
		
		JButton btnSearch = new JButton("search & add TDs");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					TDRepository tdr = new TDRepository(textFieldIP.getText(), Integer.parseInt(textFieldPort.getText()));
					JSONArray ja = tdr.tdSearch(textFieldFreeText.getText());
					if(ja == null || ja.length() == 0) {
						throw new Exception("No matches found");
					} else {
						for(int i=0; i<ja.length(); i++) {
							Object o = ja.get(i);
							byte[] content = o.toString().getBytes();
							ThingDescription td = DescriptionParser.fromBytes(content);
							
							Client client = thingsClient.getClientFactory().getClientFromTD(td);
							String name = td.getMetadata().getName();
							thingsClient.addThingPanel(client, name, name);
						}
						
						JOptionPane.showMessageDialog(null, ja.length() + " thing descriptions added", "Info", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}	
			}
		});
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSearch.gridx = 2;
		gbc_btnSearch.gridy = 3;
		add(btnSearch, gbc_btnSearch);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldPort;
	private JTextField textFieldIP;
	private JTextField textFieldFreeText;

}
