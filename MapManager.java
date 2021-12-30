import java.io.*;
import java.net.*;
import java.util.*;

public class MapManager
{
	private FileManager fileManager;

	private Set unCheckedPages;
	private Set checkedPages;

	private Map wordsUrl;
	private Map urlContent;

	public MapManager(Map wu,Map uc)
	{
		fileManager = new FileManager();
		unCheckedPages = Collections.synchronizedSet(new HashSet());
		checkedPages = Collections.synchronizedSet(new HashSet());
		wordsUrl = wu;
		urlContent = uc;
	}
	public void add(URL index, String wholeContent)
	{
		Set keys;
		Set newLinks = new HashSet();
		String readableContent = "";
		checkedPages.add(index);

		if(!wholeContent.equals(""))
		{
			newLinks = fileManager.getAnchers(wholeContent, index);
			readableContent = fileManager.getContent(wholeContent);
			keys = fileManager.getWords(readableContent);

			addToMaps(keys, index, readableContent);
		}

		unCheckedPages.addAll(newLinks);
		unCheckedPages.removeAll(checkedPages);

	}//end of add
	public URL getNextIndex()
	{
		unCheckedPages.removeAll(checkedPages);

		if(unCheckedPages.iterator().hasNext())
			return ((URL) (unCheckedPages.iterator().next()) );
		else
			return null;
	}
	private void addToMaps(Set keys, URL index, String redableCon)
	{
		Set value;
		String word;
		for (Iterator i = keys.iterator(); i.hasNext();)
		{
			word = (String) i.next();

			if(wordsUrl.containsKey(word))
			{
				value = (Set) wordsUrl.get(word);
				value.add(index);
			}
			else
			{
				value = new HashSet();
				value.add(index);
				wordsUrl.put(word, value);
			}
		}//end of for

		urlContent.put(index, redableCon);

	}//end of addToMaps
}

