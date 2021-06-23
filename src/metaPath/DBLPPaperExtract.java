package metaPath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class DBLPPaperExtract {
	public static void main(String[] args) 
	{	 
		int i, p, venueIndex = 0, currentVenuewIndex = 0,authorIndex = 0;
		String currentLineString, paperIndex = null, citedByIndex = null, paperTitle = null, venue = null, year=null;

		ArrayList<String> citedby = new ArrayList<String>();
		ArrayList<Integer> authorIndexList = new ArrayList<Integer>();
		Map<String, Integer> venues = new TreeMap<String, Integer>();
		Map<String, Integer> authors = new TreeMap<String, Integer>();
		Map<String, Integer> wordCounts = new TreeMap<String, Integer>();

		try{
			BufferedReader br = new BufferedReader(new FileReader("dblp.txt"));

			BufferedWriter bwCitedBy = new BufferedWriter(new FileWriter(new File("citedby.txt")));
			BufferedWriter bwPaperIndex = new BufferedWriter(new FileWriter(new File("paper_index.txt")));
			BufferedWriter bwVenueIndex = new BufferedWriter(new FileWriter(new File("venue_index.txt")));
			BufferedWriter bwAuthorIndex = new BufferedWriter(new FileWriter(new File("author_index.txt")));
			BufferedWriter bwPaperVenue = new BufferedWriter(new FileWriter(new File("paper_venue.txt")));
			BufferedWriter bwPaperAuthor = new BufferedWriter(new FileWriter(new File("paper_author.txt")));
			BufferedWriter bwPaperYear = new BufferedWriter(new FileWriter(new File("paper_year.txt")));
			BufferedWriter bwTermIndex = new BufferedWriter(new FileWriter(new File("term_index.txt")));   //数据文本，在题目中出现频率超过50的词

			while ((currentLineString = br.readLine()) != null) {

				//System.out.println (currentLineString);

				if (currentLineString.equals("")){                                      //dblp的内容是啥？
					// writing cited-by tuples
					bwPaperIndex.write(paperIndex + "\t" + paperTitle + "\n");     //如果dblp为“”，bwPaperIndex改写为 ‘具体的paperIndex 具体的paperTitle’
					if (citedByIndex!=null){                                       //
						for (String s: citedby)
							bwCitedBy.write(paperIndex + "\t" + s + "\n"); //bwCitedBy改写为 ‘具体的paperIndex   具体的citedby’
						citedByIndex = null;
						citedby.clear();
					}

					for (Integer aIndex: authorIndexList){
						bwPaperAuthor.write(paperIndex + "\t" + aIndex + "\n"); //bwPaperAuthor改写为 ‘具体的paperIndex   具体的authorindex’
					}
					authorIndexList.clear();

					continue;
				}

				if (currentLineString.toLowerCase().contains("#*")){
					//title
					paperTitle = currentLineString.substring(currentLineString.indexOf("#*")+2); //paperTitle为从当前位置到最后的所有字符
					//System.out.println(paperTitle);

					// term extraction from title
					StringTokenizer st = new StringTokenizer(paperTitle,", ");                          
					while (st.hasMoreTokens()) {                                   //hasMoreTokens()该方法是用来判断是否还有分隔符
						String term = st.nextToken();
						// replace any punctuation char but apostrophes and dashes with a space   用空格替换除撇号和破折号以外的任何标点符号
						term = term.replaceAll("[\\p{Punct}&&[^'-]]+", "");
						term = term.replaceAll("-", "");
						// replace most common English contractions
						term = term.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");
						List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", 
								"is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "us",
								"to", "was", "will", "with", "from", "form", "within", "when", "what", "where", "without", "vs", "very", "via", "use", "un",
								"up", "down", "left", "right", "under", "one", "two", "top", "over", "less", "more", "la", "its", "high", "low", "do", "de",
								"c", "all", "you", "me", "they");
						// get rid of empty ones, stop words, and numeric   删除空的，停止词和数字
						if (term.equals("") || stopWords.contains(term) || term.matches("[-+]?\\d*\\.?\\d+"))   //matches() 方法用于检测字符串是否匹配给定的正则表达式。
							continue;
						if (!wordCounts.containsKey(term)) {
							wordCounts.put(term, 1);                             //词汇计算，计算title里面都有什么词，如果不存在这个词则为1，如果之前存在这个词则.get(term)+1
						} else {
							wordCounts.put(term, wordCounts.get(term) + 1);
						}
					}  

					
				}
				if (currentLineString.toLowerCase().contains("#@")){
					//authors
					String authorList = currentLineString.substring(currentLineString.indexOf("#@")+2);
					StringTokenizer st = new StringTokenizer(authorList,",");  
					while (st.hasMoreTokens()) {  
						String author = st.nextToken();
						if (author.charAt(0)==' ') // removing space after , 删除空格
							author = author.substring(1);
						if (!authors.containsKey(author)){              //authors 是一个新map   如果这个map里没有这个author
							authorIndexList.add(authorIndex);       //先要在authorIndexList中加入authorindex
							authors.put(author, authorIndex++);     //再将其加入map
						}else{                                                 //如果这个map里有这个author了
							authorIndexList.add(authors.get(author));        //则只在authorIndexList中加入authorindex
						}

						//System.out.println(author);
					}  

				}
				if (currentLineString.toLowerCase().contains("#t")){
					//year
					year = currentLineString.substring(currentLineString.indexOf("#t")+2);
					//System.out.println(year);
				}
				if (currentLineString.toLowerCase().contains("#c")){
					//venue
					venue = currentLineString.substring(currentLineString.indexOf("#c")+2);
					// remove what is between parentheses, spaces, #, and string after :
					venue = venue.replaceAll("\\(.*\\)", "");
					venue = venue.replaceAll("\"", "");
					venue = venue.replaceAll(" ", "");
					venue = venue.replaceAll("#", "");
					int pos = venue.indexOf(":");           //返回指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回-1，pos>=0说明存在这样的字符
					if (pos>=0)
						venue = venue.substring(0,venue.indexOf(":"));
					pos = venue.indexOf("*");
					if (pos==0)
						venue = venue.substring(1);   //substring返回字符串的子字符串
					
					pos = venue.lastIndexOf(".");
					if (pos==venue.length()-1)
						venue = venue.substring(0,venue.indexOf("."));
					
					if (!venues.containsKey(venue)){
						currentVenuewIndex = venueIndex;
						venues.put(venue, venueIndex++);
					}
					else
						currentVenuewIndex = venues.get(venue);
 
					//System.out.println(venue);
				}
				if (currentLineString.toLowerCase().contains("#index")){
					//index
					paperIndex = currentLineString.substring(currentLineString.indexOf("#index")+6);  
					//System.out.println(paperIndex);
					bwPaperVenue.write(paperIndex + "\t" + currentVenuewIndex + "\n");
					bwPaperYear.write(paperIndex + "\t" + year + "\n");
				}
				if (currentLineString.toLowerCase().contains("#%")){
					//citation index
					citedByIndex = currentLineString.substring(currentLineString.indexOf("#%")+2);  
					citedby.add(citedByIndex);
					//System.out.println(citedByIndex);
				}


			}

			for (String v : venues.keySet()) {             //获取Map对象的所有键名，然后迭代输出
				int count = venues.get(v);
				//System.out.println(count + "\t" + v);
				bwVenueIndex.write(count + "\t" + v + "\n");     //给venue一个index
			}

			for (String a : authors.keySet()) {
				int count = authors.get(a);
				//System.out.println(count + "\t" + a);         //给author一个index
				bwAuthorIndex.write(count + "\t" + a + "\n");
			}

			
			for (String word : wordCounts.keySet()) {
				int count = wordCounts.get(word);                          //出现频率超过50次的词形成一个bwTermIndex。     
				if (count >= 50)
					bwTermIndex.write(count + "\t" + word + "\n");
			}


			br.close();
			bwCitedBy.close();
			bwPaperIndex.close();
			bwVenueIndex.close();
			bwAuthorIndex.close();
			bwPaperVenue.close();
			bwPaperAuthor.close();
			bwPaperYear.close();
			bwTermIndex.close();
			
		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
