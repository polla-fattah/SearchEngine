import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

public class Controler
{
	private Set sites;
	private UserInterface userInterface;
	private SiteLoader siteLoder;
	private Search search;

	private Map wordsUrl;
	private Map urlContent;

	private JTextArea messageText;	//for displaying any message from the program to the user
	private JEditorPane resulText;	//it displayes the search result

	private JTextField searchField;	//to add the frace from the user to be searched
	private JComboBox loadField;	//to specify the site to be searched from

	private JButton searchButton;	//an order to start search
	private JButton loadButton;		//an order to stop loading the site
	private boolean stop;
	private GetSite getSite;

	public Controler()
	{
		String line;

		sites = new HashSet();
		wordsUrl = new HashMap();
		urlContent = new HashMap();

		try
		{
			BufferedReader in = new BufferedReader (new FileReader(new File("SystemFiles/sites.txt")));

			while((line = in.readLine()) != null)
				sites.add(line);
			in.close();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Unable to read from critical internal file the program will be terminated.\nTechnical support :\n " + e,
												"Internal error",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		siteLoder = new SiteLoader (wordsUrl,urlContent);
		search = new Search (wordsUrl,urlContent);
		userInterface = new UserInterface(new Vector(sites));
		getSite = new GetSite(userInterface);

		BeginingSearch beSerch = new BeginingSearch();

		messageText = userInterface.getMessage();
		resulText = userInterface.getResult();

		searchField = userInterface.getPhrase();
		searchField.addActionListener(beSerch);
		loadField = userInterface.getSites();
		loadField.addActionListener(new BeginingNewLoad(userInterface));
		loadField.insertItemAt("Add Site",0);
		loadField.insertItemAt("Del Site",1);
		loadField.insertItemAt("--------",2);

		loadField.setSelectedIndex(2);
 		searchButton = userInterface.getSearch();
 		searchButton.addActionListener(beSerch);

 		loadButton = userInterface.getLoad();

		resulText.addHyperlinkListener( new HyperlinkListener()
		{
			public void hyperlinkUpdate( HyperlinkEvent event )
			{
				if ( event.getEventType() ==HyperlinkEvent.EventType.ACTIVATED )
				{
					userInterface.setCursor( Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ) );
					try
					{
						String[] command = {"C:\\Program Files\\Internet Explorer\\iexplore.exe",event.getURL().toString()};
						Runtime runtime = Runtime.getRuntime();
						runtime.exec(command);
					}
					catch (IOException ioe)
					{
						messageText.setText("Can not Find the IE or This link already is broken");
					}
					userInterface.setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ) );
				}
			}
		});  // end anonymous inner class

		loadButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				stop = true;
			}});
		userInterface.show();
	}

	private class BeginingNewLoad implements ActionListener
	{
		JFrame father;
		BeginingNewLoad(JFrame f)
		{
			father = f;
		}
		public void actionPerformed(ActionEvent ev)
		{
			JComboBox cb = (JComboBox)ev.getSource();
			String newSelection = (String)cb.getSelectedItem();
			if(newSelection.equals("Add Site"))
			{
				getSite.urlField.setText("");
				getSite.nameField.setText("");
				father.setEnabled(false);
				getSite.show();

			}
			else if(newSelection.equals("Del Site"))
			{
				String input = JOptionPane.showInputDialog("Enter the name of site you want to Delete : ");
				if(input == null)
					return;
				input = input.toLowerCase();
				if(sites.contains(input))
				{
					try
					{
						sites.remove(input);
						loadField.removeItem(input);
						loadField.setSelectedItem("--------");
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("SystemFiles/sites.txt")));
						for(Iterator i = sites.iterator(); i.hasNext();)
							out.write((String)i.next() + "\n");
						out.close();
						messageText.setText(input + " is deleted successfully");
					}
					catch(Exception e)
					{
						messageText.setText(input + " can not be deleted");
					}
				}
			}
			else if(newSelection.equals("--------"))
			{
				messageText.setText(" Default choice...");
			}
			else
			{
				try
				{
					siteLoder.load(newSelection);
					messageText.setText("Site " +newSelection +" is loaded Succefully" );
				}
				catch(Exception ex)
				{
					messageText.setText(ex.toString());
					resulText.setText("");
				}
			}
		}
	}

	private class BeginingSearch implements ActionListener
	{
		public void actionPerformed(ActionEvent ev)
		{
			Set results;
			String s = searchField.getText();
			String output ="<html><body> <br><ol>";
			if(s.equals(""))
				return;
			try
			{
				results = search.find(s);
			}
			catch(Exception e)
			{
				messageText.setText(e.toString());
				resulText.setText("");
				return;
			}

			if(results == null)
			{
				messageText.setText(" Miss spelling or too comon words in your enterd phrace.\n  or may be no such results in the phrace.");
				resulText.setText("<html><body> <font size = 5 color = red >No results match...</font></body></html>");
				return;
			}
			for (Iterator i = results.iterator(); i.hasNext(); )
			{
				s = ((URL)i.next()).toString();
				output += "<li><a href = '" + s + "'>"+ s +"</a><br>";
			}
			messageText.setText(" If it's not your desired pages, please try to be more accurate in search items.");
			resulText.setText(output);
		}
	}

	class GetSite extends JFrame implements ActionListener
	{
		public JTextField urlField;
		public JTextField nameField;
		public boolean flag;
		private JButton ok = new JButton("Ok");
		private JButton cancel = new JButton("Cancel");

		UserInterface father;
		public GetSite(UserInterface fath)
		{
			super("Snter Site");
			setSize(300,210);
			setLocation(120,200);
			setResizable(false);
			father = fath;
			JLabel label;
			addWindowListener(new WindowAdapter()
			{	public void windowClosing(WindowEvent we)
				{
					father.setEnabled(true);
					father.requestFocus();
					hide();
				}
			});

			Container pane = getContentPane();
			pane.setLayout(null);

			label = new JLabel("Enter Full Site URL : ");
			label.setBounds(20,20,200,25);
			pane.add(label);

			urlField = new JTextField();
			urlField.setBounds(20,46,200,25);
			pane.add(urlField);

			label = new JLabel("Choose a name for this Site : ");
			label.setBounds(20,80,200,25);
			pane.add(label);

			nameField= new JTextField();
			nameField.setBounds(20,106,200,25);
			pane.add(nameField);

			ok.setBounds(60,140,80,30);
			ok.addActionListener(this);
			pane.add(ok);

			cancel.setBounds(145,140,80,30);
			cancel.addActionListener(this);
			pane.add(cancel);
		}
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source == ok)
			{
				stop = false;
				Thread th = new Thread()
				{
					public void run()
					{
						String name = nameField.getText();
						final String site = urlField.getText();
						if(name.equals("") || site.equals(""))
							return;
						father.setImage(UserInterface.ROTATED);
						messageText.setText("Please Wait while search finishes...");
						searchField.setEnabled(false);
						searchButton.setEnabled(false);
						loadField.setEnabled(false);
						father.setEnabled(true);
						father.requestFocus();
						hide();
						try
						{
							siteLoder.storeSite(new URL(site));
							while(! siteLoder.isFinished())
							{
								if (stop)
									stop();
								sleep(300);
							}
							name = name.toLowerCase();
							siteLoder.save(name);
							if(!sites.contains(name))
							{
								sites.add(name);
								loadField.addItem(name);
							}
							loadField.setSelectedItem(name);
							PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("SystemFiles/sites.txt")));
							for(Iterator i = sites.iterator(); i.hasNext();)
								out.write((String)i.next() + "\n");
							out.close();
							messageText.setText("Storing finished");
						}
						catch(Exception e)
						{
							messageText.setText("Can Not Open this Site");
							resulText.setText("");
						}
						userInterface.setImage(UserInterface.STOPED);
						searchField.setEnabled(true);
						loadField.setEnabled(true);
						searchButton.setEnabled(true);
					}//end of run
				};//end of thread
				th.start();
			}//end of if(ok)
			else if( source == cancel)
			{
				father.setEnabled(true);
				father.requestFocus();
				hide();
			}
		}//end of void actionPerformed(ActionEvent evt)
	}//class GetSite
}//end of controller