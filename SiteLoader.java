import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class SiteLoader
{

	private MapManager mapManager;//for storing loaded data to the Maps

	private Map wordsUrl;
	private Map urlContent;
	private boolean END = false;
	public SiteLoader(Map wu,Map uc)
	{
		wordsUrl = wu;
		urlContent = uc;
		mapManager = new MapManager(wu, uc);
	}

	public void storeSite(URL site)throws Exception
	{
		String page;
		URL nextPage;
		END = false;
		Thread mainThread;

		wordsUrl.clear();
		urlContent.clear();
		try
		{
			page = 	readPage(site);
		}
		catch(Exception e)
		{
			throw (new Exception("Unable to open the site"));
		}
		mapManager.add(site, page);

		mainThread = new Loading();
		mainThread.start();
	}
	/**To return the object which is responsible for saving and loading the pages*/
	public MapManager getMapManager()
	{
		return mapManager;
	}
	/**this class to make analizing and savind the content of a page, in a thread*/
	private class Loading extends Thread
	{
		public void run()
		{
			String page;
			URL nextPage;
			while((nextPage = mapManager.getNextIndex()) != null)
			{
				try
				{
					page =	readPage(nextPage);
				}
				catch(Exception e)
				{
					page = "";
				}
				mapManager.add(nextPage, page);
			}//end of while
			END = true;
		}
	}//end of class Loading
	/**this function to endicate the end of the mission*/
	public boolean isFinished()
	{
		return END;
	}
	/**this function is responcible for the actual loadding of any page*/
	private String readPage(URL site)throws Exception
	{
		String line, page ="";
		BufferedReader in = new BufferedReader (new InputStreamReader(new BufferedInputStream(site.openStream())));
		while((line = in.readLine()) != null)
			page += line+"\n";
		in.close();
		return page;
	}
	/**for loading the old saved sites*/
	public void load(String path) throws Exception
	{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("UserFiles/"+path));
		wordsUrl.clear();
		urlContent.clear();
		wordsUrl.putAll((Map)in.readObject());
		urlContent.putAll((Map)in.readObject());
		in.close();
	}

	public void save(String path) throws Exception
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("UserFiles/"+path));

		out.writeObject(wordsUrl);
		out.writeObject(urlContent);
		out.close();
	}

}
