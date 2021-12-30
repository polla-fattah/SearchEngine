import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class UserInterface extends JFrame
{
	private JTextArea messageText;	//for displaying any message from the program to the user
	private JEditorPane resulText;	//it displayes the search result

	private JTextField searchField;	//to add the frace from the user to be searched
	private JComboBox loadField;	//to specify the site to be searched from

	private JButton searchButton;	//an order to start search
	private JButton loadButton;		//an order to start loading the whole site

	private JLabel animatedLabel;	//to display the state of loadding

	public final static ImageIcon ROTATED = new ImageIcon("SystemFiles/load.gif");
	public final static ImageIcon  STOPED= new ImageIcon("SystemFiles/white.gif");

	/**the constructor*/
	public UserInterface(Vector sites)
	{
		super("Personal Search");
		setLocation(100, 100);

		addWindowListener(new WindowAdapter()
			{ public void windowClosing(WindowEvent we){System.exit(0);}});

		Container contentPane = getContentPane();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		contentPane.setLayout(gridbag);

		//Welcom message
		JLabel welcomLable = new JLabel("<html><font color = blue size = 6>Personal_Site_Searger</font>");
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = 25;
		gridbag.setConstraints(welcomLable, c);
		contentPane.add(welcomLable);

		//second row
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth =1;
		c.ipadx = 0;
		c.ipady = 0;

		//Search_From
		JLabel loadLable = new JLabel("<html>&nbsp;&nbsp;<font color=green>Search_From : </font>");
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(loadLable, c);
		contentPane.add(loadLable);

		//Combo Box
		loadField = new JComboBox(sites);
		c.gridx = 1;
		c.gridy = 1;
		c.ipadx = 30;
		gridbag.setConstraints(loadField, c);
		contentPane.add(loadField);

		//load Go
		c.ipadx = 2;
		loadButton = new JButton("Stop");
		loadButton.setMargin(new Insets(2,2,1,1));
		c.gridx = 2;
		c.gridy = 1;
		gridbag.setConstraints(loadButton, c);
		contentPane.add(loadButton);

		//Search Label
		JLabel searchLable = new JLabel("<html> &nbsp;&nbsp;&nbsp;<font color=green> Search : </font>");
		c.gridx = 3;
		c.gridy = 1;
		gridbag.setConstraints(searchLable, c);
		contentPane.add(searchLable);

		//Search Field
		searchField = new JTextField();
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 2;
		c.ipadx = 200;
		c.ipadx = 0;
		c.weightx = 1.0;
		gridbag.setConstraints(searchField, c);
		contentPane.add(searchField);

		//Search Go
		searchButton = new JButton("Go");
		searchButton.setMargin(new Insets(0,0,0,0));
		c.gridx = 6;
		c.gridy = 1;
		c.ipadx = 0;
		c.weightx = 0;
		gridbag.setConstraints(searchButton, c);
		contentPane.add(searchButton);

		//Result Text
		resulText = new JEditorPane();
		resulText.setEditable(false);
		resulText.setContentType("text/html");
		JScrollPane scrollResult = new JScrollPane(resulText);
        scrollResult.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(Color.black),
             BorderFactory.createEmptyBorder(5,5,5,5)));

		c.gridx = 0;
		c.gridy = 2;
		c.ipady = 100;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5,15,5,15);
		c.weighty = 1.0;
		c.weightx = 1.0;
		gridbag.setConstraints(scrollResult, c);
		contentPane.add(scrollResult);

		//Message Label
		JLabel messageLable = new JLabel("<html> &nbsp;&nbsp;<font color=green> Message Place : </font>");
		c.gridx = 0;
		c.gridy = 3;
		c.ipady = 0;
		c.weighty = 0.0;
		gridbag.setConstraints(messageLable, c);
		contentPane.add(messageLable);

		//Message Area
		messageText = new JTextArea();
		messageText.setFont(new Font("Arial",Font.BOLD,14));
		messageText.setEditable(false);
		JScrollPane scrollMessage = new JScrollPane(messageText);
		scrollMessage.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.black),
			BorderFactory.createEmptyBorder(5,5,5,5)));

		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.0;
		c.gridwidth = 6;
		gridbag.setConstraints(scrollMessage, c);
		contentPane.add(scrollMessage);

		//Animation label
		animatedLabel = new JLabel(STOPED);
		c.gridx = 6;
		c.gridy = 4;
		gridbag.setConstraints(animatedLabel, c);
		contentPane.add(animatedLabel);


		pack();
		setExtendedState(MAXIMIZED_BOTH );
	}

	public JTextArea getTxt()
	{
		return messageText;
	}

	// gets renal failur
	/**Returns a text area to the message */
	public JTextArea getMessage(){return messageText;}
	/**
	Returns an editor pain to display search results,
	it is an html type specified
	*/
	public JEditorPane getResult() {return resulText;}
	/**Returns the field for user intered fraces to be search*/
	public JTextField getPhrase(){return searchField;}

	/**Returns the combobox for user spesified site to be searched from*/
	public JComboBox getSites(){return loadField;}
	/**Returns a botton to start search	*/
	public JButton getSearch(){return searchButton;}
	/**Returns a botton to start loading sites	*/
	public JButton getLoad(){return loadButton;}

	/**Chenges the image displayed */
	public void setImage(ImageIcon img)
	{
		animatedLabel.setIcon(img);
	}

}