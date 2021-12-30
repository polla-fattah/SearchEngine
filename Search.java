import java.net.*;
import java.util.*;

public class Search
{
	private Map wordsUrl;
	private Map urlContent;
	String StrangeChars = "!@#$%^&*()?><~=\\/|1234567890[]{}.,;";

	public Search(Map w, Map u)
	{
		wordsUrl = w;
		urlContent = u;
	}//end of constructer

	public Set find(String input)throws Exception
	{
		Set result,temp;
		input = regulateInput(input).toLowerCase();
		String array[] = input.split("-");
		String mainStr = array[0].trim();
		if(mainStr.equals(""))
			return null;
		result = getSet(mainStr);
		if(result != null && array.length ==2)
		{
			String minorStr = array[1].trim();
			if (!minorStr.equals(""))
			{
				temp = getSet(minorStr);
				if(temp != null)
					result.removeAll(temp);
			}
		}
		return result;
	}

	private Set getSet(String str)
	{
		Set s = new HashSet(), temp;
		boolean first = true;
		String word = "";
		int end;

		for (int i = 0 ; i < str.length(); i++)
		{
			if(str.charAt(i) == '\"')
			{
				for (end = ++i ; end <str.length() && str.charAt(end) != '\"'; end++);
				temp = getPhraceIndex(str.substring(i, end));
				if(temp == null) return null;
				if(first)
				{
					s.addAll(temp);
					first = false;
				}
				else
					s.retainAll(temp);
				i = end++;
			}
			else if(Character.isLetter(str.charAt(i)))
			{
				word = getNextWord(str,i);
				temp = (Set)wordsUrl.get(word);
				if(temp == null) return null;

				if(first)
				{
					s.addAll(temp);
					first = false;
				}
				else
					s.retainAll(temp);
				i += word.length();
			}
		}
		return s;
	}
	private Set getPhraceIndex(String sss)
	{
		Set store = new HashSet(), result = new HashSet(), temp;
		URL url;
		String word, content;
		boolean first = true;
		String str = sss.trim();
		for (int i = 0 ; i < str.length(); i++)
		{
			if(Character.isLetter(str.charAt(i)))
			{
				word = getNextWord(str, i);
				temp = (Set)wordsUrl.get(word);

				if(temp == null) return null;
				if(first)
				{
					store.addAll(temp);
					first = false;
				}
				else
					store.retainAll(temp);
				i += word.length();
			}
		}
		for(Iterator i = store.iterator() ; i.hasNext();)
		{
			url = (URL)i.next();
			content = (String) urlContent.get(url);
			if(content.indexOf(str) != -1)
				result.add(url);
		}
		return result;
	}
	private String getNextWord(String str,int from)
	{
		String word = "";
		for (int i = from; i < str.length(); i++)
			if(Character.isLetter(str.charAt(i)))
				word += str.charAt(i);
			else
				break;

		return word;
	}

	private String regulateInput(String input)throws Exception
	{
		containsUnusualChars(input);
		input = input.replace('\'', '\"');
		StringBuffer buf = new StringBuffer(input.replace('\t',' '));
		boolean second = false;

		for ( int i = 0 ; i < buf.length() ;i = buf.indexOf("-", i + 1) )
		{
			if (i == -1)
				break;
			if(second)
				buf.replace(i,i+1," ");
			if(buf.charAt(i) == '-')
				second = true;
		}
		for (int i = 0; i < buf.length() - 1 ; i++)
			if (buf.charAt(i)==' ' && buf.charAt(i+1) == ' ')
			{
				buf.delete(i, i + 1);
				i = 0;
			}
		return buf.toString().trim();
	}

	private void containsUnusualChars(String str)throws Exception
	{
		for (int i = 0; i < StrangeChars.length(); i++)
			if (str.indexOf(StrangeChars.charAt(i))!= -1)
				throw (new Exception("Your enterd phrase contains one or more of the following \n"+
				                 "chatacters < "+StrangeChars+" > which they are not suported whith this search program\n"));
	}
}
