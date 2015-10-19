package de.webthing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.webthing.client.Callback;
import de.webthing.client.Client;
import de.webthing.desc.pojo.ActionDescription;
import de.webthing.desc.pojo.EventDescription;
import de.webthing.desc.pojo.PropertyDescription;

public class ThingPanelUI extends JPanel implements ActionListener, Callback {
	
	private static final Logger log = LoggerFactory.getLogger(ThingPanelUI.class);

	private static final long serialVersionUID = 2117762031555752901L;
	
	final Client clientListener;

	JButton buttonPropertiesGET;

	Map<String, JTextComponent> propertyComponents;
	
	final static BigInteger MAX_UNSIGNED_LONG = new BigInteger("18446744073709551615");
	final static BigInteger MAX_UNSIGNED_INT = BigInteger.valueOf(4294967295L);
	final static BigInteger MAX_UNSIGNED_SHORT = BigInteger.valueOf(65535);
	final static BigInteger MAX_UNSIGNED_BYTE = BigInteger.valueOf(255);
	final static BigInteger MAX_BYTE = BigInteger.valueOf(127);
	final static BigInteger MIN_BYTE = BigInteger.valueOf(-128);
	final static BigInteger MAX_SHORT = BigInteger.valueOf(32767);
	final static BigInteger MIN_SHORT = BigInteger.valueOf(-32768);
	final static BigInteger MAX_INT = BigInteger.valueOf(2147483647);
	final static BigInteger MIN_INT = BigInteger.valueOf(-2147483648);
	final static BigInteger MAX_LONG = BigInteger.valueOf(9223372036854775807L);
	final static BigInteger MIN_LONG = BigInteger.valueOf(-9223372036854775808L);
	
	JTextField createTextField(String type, boolean editable) {
		JTextField textField = new JTextField();
		textField.setEditable(editable);
		BasicTextUI textFieldUI = new HintTextFieldUI(" " + type, editable, Color.GRAY);
		textField.setUI(textFieldUI);
		PlainDocument pd = (PlainDocument) textField.getDocument();
		switch(type) {
		case "xsd:unsignedLong":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(BigInteger.ZERO, MAX_UNSIGNED_LONG));
		case "xsd:unsignedInt":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(BigInteger.ZERO, MAX_UNSIGNED_INT));
			break;
		case "xsd:unsignedShort":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(BigInteger.ZERO, MAX_UNSIGNED_SHORT));
			break;
		case "xsd:unsignedByte":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(BigInteger.ZERO, MAX_UNSIGNED_BYTE));
			break;
		case "xsd:long":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(MIN_LONG, MAX_LONG));
			break;
		case "xsd:int":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(MIN_INT, MAX_INT));
			break;
		case "xsd:short":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(MIN_SHORT, MAX_SHORT));
			break;
		case "xsd:byte":
			pd.setDocumentFilter(new IntegerRangeDocumentFilter(MIN_BYTE, MAX_BYTE));
			break;
		case "xsd:boolean":
			pd.setDocumentFilter(new BooleanDocumentFilter());
			break;
		default:
			log.warn("TextField created without input control for type: " + type);
		}
		return textField;
	}

	/**
	 * Create the panel.
	 */
	public ThingPanelUI(Client clientListener) {
		this.clientListener = clientListener;
		propertyComponents = new HashMap<>();
		
		JPanel gbPanel = new JPanel();
		gbPanel.setLayout(new GridBagLayout());
		
		this.setLayout(new BorderLayout());
		this.add(gbPanel, BorderLayout.NORTH);

		Insets ins2 = new Insets(2, 2, 2, 2);
		int yline = 0;

		// ###### Properties
		List<PropertyDescription> properties = clientListener.getProperties();
		if(properties != null && properties.size() > 0) {
			GridBagConstraints gbcP_0 = new GridBagConstraints();
			gbcP_0.gridx = 0;
			gbcP_0.gridy = yline;
			gbcP_0.gridwidth = 2;
			gbcP_0.anchor = GridBagConstraints.NORTHWEST; // LINE_START;
			JLabel labelP = new JLabel("<html><h2>Properties</h2></html>");
			gbPanel.add(labelP, gbcP_0);

			GridBagConstraints gbcP_2 = new GridBagConstraints();
			gbcP_2.gridx = 2;
			gbcP_2.gridy = yline;
			gbcP_2.fill = GridBagConstraints.HORIZONTAL;
			buttonPropertiesGET = new JButton("Get all");
			buttonPropertiesGET.addActionListener(this);
			gbPanel.add(buttonPropertiesGET, gbcP_2);

			yline++;

			for (int i = 0; i < properties.size(); i++) {
				PropertyDescription p = properties.get(i);

				// label
				GridBagConstraints gbcX_0 = new GridBagConstraints();
				gbcX_0.gridx = 0;
				gbcX_0.gridy = yline;
				gbcX_0.anchor = GridBagConstraints.LINE_START;
				gbcX_0.insets = ins2;
				gbPanel.add(new JLabel(p.getName() + ": "), gbcX_0);

				// value
				GridBagConstraints gbcX_1 = new GridBagConstraints();
				gbcX_1.gridx = gbcX_0.gridx + 1;
				gbcX_1.gridy = yline;
				gbcX_1.fill = GridBagConstraints.HORIZONTAL;
				gbcX_1.weightx = 1;
				gbcX_1.insets = ins2;
				JTextField textField = createTextField(p.getOutputType(), p.isWritable());
				gbPanel.add(textField, gbcX_1);
				propertyComponents.put(p.getName(), textField);
				refreshProperty(p.getName()); // refresh value

				// get button
				GridBagConstraints gbcX_2 = new GridBagConstraints();
				gbcX_2.gridx = gbcX_1.gridx + 1;
				gbcX_2.gridy = yline;
				gbcX_2.fill = GridBagConstraints.HORIZONTAL;
				gbcX_2.insets = ins2;
				JButton buttonGet = new JButton("Get");
				gbPanel.add(buttonGet, gbcX_2);
				buttonGet.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						refreshProperty(p.getName());
					}
				});

				// observe button
				GridBagConstraints gbcX_3 = new GridBagConstraints();
				gbcX_3.gridx = gbcX_2.gridx + 1;
				gbcX_3.gridy = yline;
				gbcX_3.fill = GridBagConstraints.HORIZONTAL;
				gbcX_3.insets = ins2;
				JToggleButton buttonObserve = new JToggleButton("Observe");
				gbPanel.add(buttonObserve, gbcX_3);
				buttonObserve.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						clientListener.observe(p.getName(), ThingPanelUI.this);
					}
				});

				if (p.isWritable()) {
					// update/put button
					// TODO show on/off for xsd:boolean ?
					GridBagConstraints gbcX_4 = new GridBagConstraints();
					gbcX_4.gridx = gbcX_3.gridx + 1;
					gbcX_4.gridy = yline;
					gbcX_4.fill = GridBagConstraints.HORIZONTAL;
					gbcX_4.insets = ins2;
					JButton buttonPut = new JButton("Put");
					gbPanel.add(buttonPut, gbcX_4);
					buttonPut.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String svalue = textField.getText();
							if(svalue.length() == 0) {
								JOptionPane.showMessageDialog(null, "No valid value of type '" + p.getOutputType() + "' given", "Value Error", JOptionPane.ERROR_MESSAGE);
							} else {
								// any other value handled by text field input control...
								clientListener.put(p.getName(), svalue, ThingPanelUI.this);
							}
						}
					});
				}

				yline++;
			}
		}


		// ###### Actions
		List<ActionDescription> actions = clientListener.getActions();
		if(actions != null && actions.size() > 0) {
			GridBagConstraints gbcA_0 = new GridBagConstraints();
			gbcA_0.gridx = 0;
			gbcA_0.gridy = yline++;
			gbcA_0.gridwidth = 2;
			gbcA_0.anchor = GridBagConstraints.LINE_START;
			JLabel labelA = new JLabel("<html><h2>Actions</h2></html>");
			gbPanel.add(labelA, gbcA_0);

			
			for (int i = 0; i < actions.size(); i++) {
				ActionDescription a = actions.get(i);

				// label
				GridBagConstraints gbcX_0 = new GridBagConstraints();
				gbcX_0.gridx = 0;
				gbcX_0.gridy = yline;
				gbcX_0.anchor = GridBagConstraints.LINE_START;
				gbcX_0.insets = ins2;
				gbPanel.add(new JLabel(a.getName() + ": "), gbcX_0);

				// input
				GridBagConstraints gbcX_1 = new GridBagConstraints();
				gbcX_1.gridx = gbcX_0.gridx + 1;
				gbcX_1.gridy = yline;
				gbcX_1.fill = GridBagConstraints.HORIZONTAL;
				gbcX_1.weightx = 1;
				gbcX_1.insets = ins2;
				JTextField textField;
				if (a.getInputType() != null && a.getInputType().length() > 0) {
					textField = createTextField(a.getInputType(), true);
					gbPanel.add(textField, gbcX_1);
				} else {
					textField = createTextField(a.getInputType(), false);
				}
				

				// fire button
				GridBagConstraints gbcX_2 = new GridBagConstraints();
				gbcX_2.gridx = gbcX_1.gridx + 1;
				gbcX_2.gridy = yline;
				gbcX_2.fill = GridBagConstraints.HORIZONTAL;
				gbcX_2.insets = ins2;
				JButton buttonAction = new JButton("Start '" + a.getName() + "'");
				gbPanel.add(buttonAction, gbcX_2);
				buttonAction.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String svalue = textField.getText();
						if(svalue.length() == 0) {
							JOptionPane.showMessageDialog(null, "No valid value of type '" + a.getInputType() + "' given", "Value Error", JOptionPane.ERROR_MESSAGE);
						} else {
							clientListener.action(a.getName(), textField.getText(), ThingPanelUI.this);
						}
					}
				});

				yline++;
			}

		}
		

		// ###### Events
		List<EventDescription> events = clientListener.getEvents();
		if(events != null && events.size() > 0) {
			GridBagConstraints gbcE_0 = new GridBagConstraints();
			gbcE_0.gridx = 0;
			gbcE_0.gridy = yline++;
			gbcE_0.gridwidth = 2;
			gbcE_0.anchor = GridBagConstraints.LINE_START;
			JLabel labelE = new JLabel("<html><h2>Events (TODO)</h2></html>");
			gbPanel.add(labelE, gbcE_0);
			
			
			for (int i = 0; i < events.size(); i++) {
				EventDescription e = events.get(i);

				// label
				GridBagConstraints gbcX_0 = new GridBagConstraints();
				gbcX_0.gridx = 0;
				gbcX_0.gridy = yline;
				gbcX_0.anchor = GridBagConstraints.LINE_START;
				gbcX_0.insets = ins2;
				gbPanel.add(new JLabel(e.getName() + ": "), gbcX_0);

				// input
				GridBagConstraints gbcX_1 = new GridBagConstraints();
				gbcX_1.gridx = gbcX_0.gridx + 1;
				gbcX_1.gridy = yline;
				gbcX_1.fill = GridBagConstraints.HORIZONTAL;
				gbcX_1.weightx = 1;
				gbcX_1.insets = ins2;
				JTextField textField = createTextField("", false);
				gbPanel.add(textField, gbcX_1);
				
				// TODO how to get informed about change (observe) ????


				yline++;
			}
		}


	}

	protected void refreshProperty(String prop) {
		clientListener.get(prop, this);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == buttonPropertiesGET) {
			for (String prop : propertyComponents.keySet()) {
				refreshProperty(prop);
			}
		}

	}
	
	///////// Thing CALLBACKS

	@Override
	public void onPut(String propertyName, String response) {
		refreshProperty(propertyName);
	}

	@Override
	public void onPutError(String propertyName) {
		JOptionPane.showMessageDialog(null, "Could not put property for " + propertyName, "Put Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onGet(String propertyName, String response) {
		JTextComponent text = propertyComponents.get(propertyName);
		text.setText(response);
	}

	@Override
	public void onGetError(String propertyName) {
		JOptionPane.showMessageDialog(null, "Could not get property " + propertyName, "Get Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onObserve(String propertyName, String response) {
		JTextComponent text = propertyComponents.get(propertyName);
		text.setText(response);
	}

	@Override
	public void onObserveError(String propertyName) {
		JOptionPane.showMessageDialog(null, "Could not register oberver " + propertyName, "Observe Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void onAction(String actionName, String response) {
		// TODO how to deal with action response?
		if(response != null && response.length()>0) {
			JOptionPane.showMessageDialog(null, "Response of action: " + response);
		}
	}

	@Override
	public void onActionError(String actionName) {
		JOptionPane.showMessageDialog(null, "Could not execute action " + actionName, "Action Error", JOptionPane.ERROR_MESSAGE);
	}

}
