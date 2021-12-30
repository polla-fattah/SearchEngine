import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class FileManager
{
	private Set tagsTable;			//it holds Html tags which they are loaded from a file

	private Set htmlCharsTable;		//it holds html characters which they can not be typed
							//by the keyboard such as (&copy;)

	private Set commonWords;		//it holds common words

	//it takes just these file extention as links
	String [] fileTypes = {".htm", ".html",".php", ".jsp", ".shtml", "asp"};

	//strange characters to be removed from the file at the indexing time
	String StrangeChars = "\n\t!@#$%^&*()?><~-=\\/|\"\'.,1234567890[]{}";

	//constructor
	FileManager()
	{
		String str;

		//creating holders
		tagsTable = new HashSet();
		htmlCharsTable = new HashSet();
		commonWords = new HashSet();
		try
		{
			//Opening stream files
			BufferedReader in1 = new BufferedReader (new FileReader(new File("SystemFiles/tags.txt")));
			BufferedReader in2 = new BufferedReader (new FileReader(new File("SystemFiles/htmlchars.txt")));
			BufferedReader in3 = new BufferedReader (new FileReader(new File("SystemFiles/common.txt")));

			//reading from files to the colections.
			for(int i = 0; (str = in1.readLine()) != null ;i++)
				tagsTable.add(str);
			for(int i = 0; (str = in2.readLine()) != null ;i++)
				htmlCharsTable.add(str);
			for(int i = 0; (str = in3.readLine()) != null ;i++)
				commonWords.add(str);

			//closing streams
			in1.close();
			in2.close();
			in3.close();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Unable to read from critical internal file the program will be terminated.\nTechnical support :\n " + e,
												"Internal error",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
	/////////////////////////////////////////////////////////////////////
	/**
		The operations no the content of the file:
		1- getting the readable content of the file.
		2- getting the list of all words which they are used in the file
	*/


	/** It returns a set of words to be indexed*/
	public Set getWords(String content)
	{
		Set words = new HashSet();//creates a set for words

		//removes any unusual chars from the text
		for (int i = 0; i < StrangeChars.length(); i++)
			content = content.replace(StrangeChars.charAt(i),' ');


		String []w = content.split(" ");//separating words

		//saving the words and removing redundant words
		for (int i = 0; i < w.length; i++)
			words.add(w[i]);

		words.removeAll(commonWords);//removes some common words from the set

		return words;
	}//end of the getWords method

	/**To get the actual content of the file */
	public String getContent(String file)
	{
		String content = deleteHtmlChars(file);
		content = deleteTags(content.toLowerCase());
		return content;
	}//end of the getContent

	/**removes all unusual html characters */
	private String deleteHtmlChars(String file)
	{
		String htmlChar;
		int i = 0;
		//to provide delete opration
		StringBuffer str = new StringBuffer(file);

		for (int start = 0 ; start < str.length(); start++)
		{
			htmlChar = "";//emptying it evry itration.
			start = str.indexOf("&", start - 1);//try to find next html char
			if(start == -1)	break;//if there is no more html chars

			//try to create current html char
			for ( i = 0; i < 12 && str.charAt(start + i ) != ';' ; i++)
				htmlChar += str.charAt(start +i);
			htmlChar += str.charAt(start +i);

			//deleting it if it is actually an html char
			if(isHtmlChar(htmlChar))
				str = str.delete(start,start + i+1);
			else
				start++;
		}
		return new String(str);
	}

	/**deletes all Tags because they are noe the readable content*/
	private String deleteTags(String file)
	{
		int start, end;
		StringBuffer str = new StringBuffer(file);
		for (start = 0 ; start < str.length(); start++)
		{
			//find next < which is the begining of all tags
			start = str.indexOf("<", start - 1);

			if(start == -1)	break;//if the tags are finished

			//checks out for tags if not continue
			if(!isTag(getTag(str,start+1)) || str.charAt(start+1) == '<')
			{
				start++;
				continue;
			}
			//indicates the end of the file
			end =  str.indexOf(">", start);

			if(end == -1)	break;//if the tags are finished

			str = str.delete(start , end+1);//deletes the tag
		}
		return new String(str);
	}//end of the deleteTags method

	//this method tries to get the first word from the brf
	private String getTag(StringBuffer bfr, int from)
	{
		String tag = "";
		int i ;
		char c;

		for (i = from; i < bfr.length() ; i++)
			if(!isMySeparators( bfr.charAt(i) ))
				break;
		//spesial tag <!--
		if(bfr.indexOf("!--",i) == i)
			return "!--";
		//ther tags
		else
		{
			for (;i < bfr.length()  ; i++)
			{
				c = bfr.charAt(i);
				if(Character.isLetter(c))
					tag += c;
				else
					break;
			}
		}
		return tag;
	}//end of the getTag method

//////////////////////////////////////////////////////////////////
	/** The Ancher filtering methods they are responsible for
			finding anchers in the content file and creating Urls for them
	*/

	/**fhis method returns the set of all links which they are availabe from the
		current page.
		the links must be:
		1- Suported types by java.
		2- They should be on the same host.
	*/
	public Set getAnchers(String file, URL index)
	{
		Set strAnchers = getStrAnchers(file);	//gets aset of all hrefs.
		Set urlAnchers = new HashSet();			//creates a holder for the links

		String link,host,strIndex;
		int k = 0, from, to;
		for ( Iterator i = strAnchers.iterator() ; i.hasNext();)
		{
			link = (String)i.next();
			link = link.trim();
			from = link.indexOf("://") + 3;
			to = link.indexOf("/",from + 1);

			if(from > 3)
			{
				//to prevent any miss addressing
				if(to <3) continue;
				host = link.substring(from,to);
				if(index.getHost().equalsIgnoreCase(host))
				{	try
					{
						urlAnchers.add(new URL(link));
					}
					catch (IOException ioe)
					{
						continue;
					}
				}
			}
			else
			{
				strIndex = index.toString();
				file = link;
				k = strIndex.lastIndexOf("/");
				//finding the link through the actual location
				while(file.indexOf("../") != -1)
				{
					file = file.substring(3);
					k = strIndex.lastIndexOf("/",k-1);
				}
				file = strIndex.substring(0, k + 1) + file;
				try
				{
					urlAnchers.add(new URL(file));
				}
				catch (IOException ioe)
				{
					continue;
				}
			}//end of else
		}
		return urlAnchers;
	}

	/**gets all suported links from the href from the links*/
	private Set getStrAnchers(String file)
	{
		String strLink;
		String str = file.replace('\"','\'').toLowerCase();
		Set hrefs = new HashSet();

		int start, end, href=0, a,colon,equal,lin,frame;//all these to be sure that it is a good link
		for (int i = 0; i <str.length();i++)
		{
			strLink = "";
			lin = str.indexOf("href", i);
			frame = str.indexOf("src", i);
			if(lin < 0 || frame < 0)
				break;
			if(lin < frame)
			{
				i = lin;

				start = str.lastIndexOf("<", i);
				a = str.lastIndexOf("a ", i);
				href = i;
				equal = str.indexOf("=", i);
				colon = str.indexOf("\'", i);
				end = str.indexOf(">", i);
			}
			else
			{
				i = frame;

				start = str.lastIndexOf("<", i);
				a = str.lastIndexOf("frame ", i);
				href = i;
				equal = str.indexOf("=", i);
				colon = str.indexOf("\'", i);
				end = str.indexOf(">", i);
			}
			if(a < href && href < equal && equal < colon && colon < end)
			{
				start = str.indexOf('\'',i);
				end = str.indexOf('\'',start + 1);
				if (start >0 && end > 0)
				{
					strLink = str.substring(start + 1,end);
					strLink = strLink.replace('\\','/');
					if(isFileSuported(strLink) )
						hrefs.add(strLink);
				}
			}
		}//end of for
		return hrefs;
	}//end of getStrAnchers

/////////////////////////////////////////////////////////////////////////////////////////

	/**utility functions required as a genaral in the multiple internal functions*/

	/**filters some chars*/

	private boolean isMySeparators(char c)
	{
		return (c == ' '|| c == '\t' || c == '\n' || c == '/' || c == '\\');
	}
	/**Checks is the passed string is an html spetsial characer or not*/
	private boolean isHtmlChar(String htmlChar)
	{
		return htmlCharsTable.contains(htmlChar);
	}
	/**Checks is the passed string is an html tag or not*/
	private boolean isTag(String tag)
	{
		return tagsTable.contains(tag);
	}


	/**checks out is the passed string containes an extention of the suported files by the
		getAnchers() method or not
	*/
	public boolean isFileSuported(String link)
	{
		for (int i = 0; i < fileTypes.length; i++)
			if(link.indexOf(fileTypes[i]) != -1)
				return true;
		return false;
	}

}
