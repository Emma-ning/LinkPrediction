package metaPath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

public class DBLPMetaPath_APAPA_APVPA {

	private static String currentLineString, paperIndex = null, authorIndex=null, venueIndex=null;

	private static Map<String, List<PaperVenue>> author_papervenuelist_map = new HashMap<String, List<PaperVenue>>();    
	private static Map<String, List<PaperAuthors>> venue_paperauthorslist_map = new HashMap<String, List<PaperAuthors>>();    
	private static Map<String, List<String>> paper_authorslist_map = new HashMap<String, List<String>>();    
	private static TreeMap<String, Integer> paper_year_map = new TreeMap<String, Integer>();    

	private static class PaperVenue implements Serializable {
		@Override
		public String toString() {
			return "PaperVenue [paper=" + paper + ", venue=" + venue + ", year=" + year + "]";
		}
		private String paper;
		private String venue;
		private int year;

		public PaperVenue(String paper, String venue, int year) {
			this.paper = paper;
			this.venue = venue;
			this.year = year;
		}
		public String getPaper(){
			return paper;
		}
		public String getVenue(){
			return venue;
		}
		public int getYear(){
			return year;
		}
	}

	private static class PaperAuthors implements Serializable {
		@Override
		public String toString() {
			return "PaperAuthors [paper=" + paper + ", authors=" + authors + ", year=" + year + "]";
		}
		private String paper;
		private ArrayList<String> authors;
		private int year;
		public PaperAuthors(String paper, ArrayList<String> authors, int year) {
			this.paper = paper;
			this.authors = authors;
			this.year = year;
		}
		public String getPaper(){
			return paper;
		}
		public ArrayList<String> getAuthors(){
			return authors;
		}
		public void setAuthors(ArrayList<String> authors){
			this.authors = authors;
		}
		public int getYear(){
			return year;
		}

	}

	private static int fromYear;
	private static int toYear;

	private static BufferedReader brPaperAuthor;
	private static BufferedReader brPaperVenue;
	private static BufferedReader brPaperYear;
	private static BufferedReader labels;
	private static BufferedWriter bwHashMap;

	/**
	 * Given index of two authors, calculate number of different paths of type A-P-V-P-A (e.g. Jim-P1-KDD-P4-Tom)
	 * @param authorIndex a
	 * @param authorIndex b
	 * @return pathCount between them
	 */
	public static int pathCount(String a, String b){
		int PathCount = 0;
		String v, i, j;
		//long startTime = System.currentTimeMillis();

		// for author with index a for each paper index i                                                    a-author index；i-paper index；v-venue；
		//   find venue v where i is published at 
		//   for each paper index j that is published at v (j and i can be equal for instance for PC(i,i)    每个v有好多的paper index，假如存在两个paper index
		//		if j has author index b, then PathCount++                                            分别为j，i。 则PC（j，i）=PC（i，i）
		                                                                                                   //如果j的作者与i不同，则PathCount++
		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);
		
		//if (papervenuelist.size()<5)
		//	return -10;
		
		for (PaperVenue pv : papervenuelist){
			// ignore papers out of target interval
			if (pv.getYear() < fromYear || pv.getYear() > toYear)
				continue;
			
			i = pv.getPaper();
			v = pv.getVenue();
			//System.out.println("v: " + v);
			List<PaperAuthors> paperauthorslist = venue_paperauthorslist_map.get(v);
			for (PaperAuthors pa: paperauthorslist){
				if (pa.getYear() < fromYear || pa.getYear() > toYear)
					continue;
				j = pa.getPaper();
				for (String author: pa.getAuthors())
					if (author.equals(b)){
						//System.out.println("There is a path from " + a + " to " + b + ": " + i + "-" + v + "-" + j);
						PathCount++;
					}
			}
		}
		//long endTime = System.currentTimeMillis();
		//long duration = (endTime - startTime);
		//System.out.println("Done with calculating path count in " + duration/1000 + " seconds!");
		return PathCount;
	}


	
	
	/**
	 * Given index of two authors, calculate number of different paths of type A-P-A-P-A (e.g. Jim-P1-Sam-P4-Tom)
	 * @param authorIndex a
	 * @param authorIndex b
	 * @return pathCount between them
	 */
	public static int pathCount2(String a, String b){
		int PathCount = 0;
		String i, j;

		// for author with index a for each paper index i                                                  a-author index；i-paper index；v-venue；
		//   for each author c who wrote i and c!=a                                                        i存在其他作者c，其中c！=a
		//   for each paper index j that is written by c (j and i can be equal for instance for PC(i,i)    c还写了其他paper index-j，则PC（i，j）=PC（i，i）
		//		if j has author index b, then PathCount++                                          如果paper index-j存在其他作者，即author index
		
		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);                              //那么PathCount++

		//if (papervenuelist.size()<5)
		//	return -10;

		for (PaperVenue pv : papervenuelist){
			// ignore papers out of target interval
			if (pv.getYear() < fromYear || pv.getYear() > toYear)
				continue;
			
			i = pv.getPaper();
			for (String c: paper_authorslist_map.get(i)){
				if (c.equals(a))
					continue;
				for (PaperVenue pv2 : author_papervenuelist_map.get(c)){
					// ignore papers out of target interval
					if (pv2.getYear() < fromYear || pv2.getYear() > toYear)
						continue;
					j = pv2.getPaper();
					for (String author: paper_authorslist_map.get(j)){
						if (author.equals(b)){
							//System.out.println("There is a path from " + a + " to " + b + ": " + i + "-" + c + "-" + j);
							PathCount++;
						}
					}
					
				}
			}
		}

		return PathCount;
	}

	
	
	
	
	/**
	 * Given index of two authors, calculate their pathSim [VLDB'11] of considering meta-path A-P-V-P-A (e.g. Jim-P1-KDD-P4-Tom)
	 * @param authorIndex a
	 * @param authorIndex b
	 * @return pathSim between them
	 */
	public static float pathSim(String a, String b){
		float ps = (float) 0.0;
		int a_a = pathCount(a,a); 
		int b_b = pathCount(b,b); 
		
		// ignore less productive
		if (a_a<0 || b_b<0)
			return -10;

		
		// check if the source or destination author actually published in that interval to avoid NaN for 0.0/0.0
		if (a_a + b_b == 0)
			return -1;
		
		ps = (float)2*pathCount(a,b) / (float)(a_a + b_b);
		if (ps > 1)
			System.out.println(pathCount(a,b) + " " + pathCount(a,a) + " " + pathCount(b,b));
		return ps;
	}



	/**
	 * Given index of two authors, calculate their pathSim [VLDB'11] of considering meta-path A-P-A-P-A (e.g. Jim-P1-Sam-P4-Tom)
	 * @param authorIndex a
	 * @param authorIndex b
	 * @return pathSim between them
	 */
	public static float pathSim2(String a, String b){
		float ps = (float) 0.0;
		int a_a = pathCount2(a,a); 
		int b_b = pathCount2(b,b); 
		
		// ignore less productive
		if (a_a<0 || b_b<0)
			return -10;
		
		// check if the source or destination author actually published in that interval to avoid NaN for 0.0/0.0
		if (a_a + b_b == 0)
			return -1;
		
		ps = (float)2*pathCount2(a,b) / (float)(a_a + b_b);
		if (ps > 1)
			System.out.println(pathCount2(a,b) + " " + pathCount2(a,a) + " " + pathCount2(b,b));
		return ps;
	}

	
	/**
	 * Given index of an authors, return authors that are connected via A-P-V-P-A (e.g. Jim-P1-KDD-P4-Tom)
	 * @param paperIndex of author: a
	 * @return list of neighbor nodes
	 */
	public static TreeSet<Integer> getNeighbors(String a){
		TreeSet<Integer> n = new TreeSet<Integer>();  
		String v, i, j;

		// for author with index a for each paper index i                                                   a- author index；i-paper index；
		//   find venue v where i is published at                                                           v-venue，i发布于v；
		//   for each paper index j that is published at v (j and i can be equal for instance for PC(i,i)   对于另一个paper index(j),其作者为b;j,i同样发布于v;
		//		add authors of j as neighbors of a                                                  那么a与b为邻居    

		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);
		for (PaperVenue pv : papervenuelist){                                  //遍历作者名字
			// ignore papers before 2000
			//if (pv.getYear() < 2010)
			//	continue;
			
			i = pv.getPaper();                                            //得到相关paper以及venue
			v = pv.getVenue();
			List<PaperAuthors> paperauthorslist = venue_paperauthorslist_map.get(v);         
			for (PaperAuthors pa: paperauthorslist){                      //基于地点 再得到该地点的paper
				j = pa.getPaper();
				for (String author: pa.getAuthors())                 //得到这些paper的作者
					n.add(Integer.parseInt(author));              //生成原作者与其他作者的treeset
			}
		}

		n.remove(Integer.parseInt(a)); // remove the node from its neighbor     //在这个类中去掉原作者自己
		return n;
	}



	public static void main(String[] args) throws ClassNotFoundException 
	{	 
		// -Xms1024m -Xmx6000m

		boolean readFromSavedGeneratedHashmaps = true;
		fromYear = 1996;
		toYear = 2002;
		String APVPA_file_name = "DBLP/APVPA_1996_2002.txt";
		String APAPA_file_name = "DBLP/APAPA_1996_2002.txt";
		String labels_file_name = "DBLP/labels_1996_2002_newLinkIn_2003_2009";

		/*fromYear = Integer.parseInt(args[0]);
		toYear = Integer.parseInt(args[1]);
		APVPA_file_name = args[2];
		APAPA_file_name = args[3];
		labels_file_name = args[4];
		*/

		
		long startTime = System.currentTimeMillis();

		try{
			brPaperAuthor = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			brPaperVenue = new BufferedReader(new FileReader("paper_newindex_venue.txt"));
			brPaperYear = new BufferedReader(new FileReader("paper_newindex_year.txt"));

			if (readFromSavedGeneratedHashmaps==true){

				// <author, list of [paper, venue]>
				FileInputStream fileIn = new FileInputStream("author_papervenuelist_map.ser");       //对这两个文件干啥了？
				ObjectInputStream in = new ObjectInputStream(fileIn);
				author_papervenuelist_map = (HashMap<String, List<PaperVenue>>) in.readObject();
				in.close();
				fileIn.close();

				// <venue, list of [paper, author]>
				FileInputStream fileIn1 = new FileInputStream("venue_paperauthorslist_map.ser");
				ObjectInputStream in1 = new ObjectInputStream(fileIn1);
				venue_paperauthorslist_map = (HashMap<String, List<PaperAuthors>>) in1.readObject();
				in1.close();
				fileIn1.close();

				
				// build paper_authorslist_map
				for (List<PaperAuthors> paperauthorslist : venue_paperauthorslist_map.values()) {
					for (PaperAuthors pa: paperauthorslist){
						if (pa.getYear() < fromYear || pa.getYear() > toYear)
							continue;
						paper_authorslist_map.put(pa.getPaper(), pa.getAuthors());
					}
				}
				
				
				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime);

				System.out.println("Done with reading maps in " + duration/1000 + " seconds!");

			}else{

				int[] paperVenue = new int[3177887]; // the index of this array correspond to the paper index and the value corresonf to venue index

				while ((currentLineString = brPaperVenue.readLine()) != null) {                     //拆分表brPaperVenue
					StringTokenizer st2 = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st2.nextToken();
					venueIndex = st2.nextToken();
					paperVenue[Integer.parseInt(paperIndex)] = Integer.parseInt(venueIndex);    //papervenue的paperindex要等于venueindex
				}

				int year;
				while ((currentLineString = brPaperYear.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st.nextToken();
					year = Integer.parseInt(st.nextToken());
					paper_year_map.put(paperIndex, year);                                //paper与year关系的map
				}


				while ((currentLineString = brPaperAuthor.readLine()) != null) {      //拆分brPaperAuthor，生成paperindex，authorindex，venueindex，year
					List<PaperVenue> paperVenueList = new ArrayList<PaperVenue>();
					List<PaperAuthors> paperAuthorsList = new ArrayList<PaperAuthors>();

					StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st.nextToken();
					authorIndex = st.nextToken();
					venueIndex = Integer.toString(paperVenue[Integer.parseInt(paperIndex)]);	 //venueindex=paperIndex
					year = paper_year_map.get(paperIndex);                                        //设置好了paperindex  authorindex venueindex year

					// add to author_papervenuelist_map
					PaperVenue new_pv = new PaperVenue(paperIndex, venueIndex, year);       //new_pv 是指包括paperIndex, venueIndex, year在内的这个整体
					if (author_papervenuelist_map.containsKey(authorIndex)){               //对于author_papervenuelist_map存在的authorIndex
						paperVenueList = author_papervenuelist_map.get(authorIndex);
					}
					paperVenueList.add(new_pv);                                            //要将他们的paperIndex, venueIndex, year调取出来储存到paperVenueList 中    
					author_papervenuelist_map.put(authorIndex, paperVenueList);    //将authorIndex与paperVenueList整合在一起更新map叫做author_papervenuelist_map
//map中.put()插入具有指定键的指定值
					// add to venue_paperauthorslist_map
					ArrayList<String> authorsList = new ArrayList<String>();
					authorsList.add(authorIndex);                                               //生成一个新list叫authorlist，并添加authorindex
					PaperAuthors new_pa = new PaperAuthors(paperIndex, authorsList, year); 				
					boolean paperFoundInVenue = false;                                        
					if (venue_paperauthorslist_map.containsKey(venueIndex)){               //对于venue_paperauthorslist_map存在的venueindex
						paperAuthorsList = venue_paperauthorslist_map.get(venueIndex);   //新建的表paperAuthorsList中，添加venueindex的相关数据
						for (PaperAuthors pa: paperAuthorsList){
							if (pa.getPaper().equals(paperIndex)){ // add author to list of authors for the existing paper  
								authorsList = pa.getAuthors();   
								authorsList.add(authorIndex);  //上面把原来的作者给去掉了 现在要加回来
								pa.setAuthors(authorsList);
								
								= true;
								break;
							}
						}
						if (paperFoundInVenue == false){ // new paper and new author should be added  
							paperAuthorsList.add(new_pa);
						}
					}else
						paperAuthorsList.add(new_pa);
					venue_paperauthorslist_map.put(venueIndex, paperAuthorsList);     //生成venune-paper，Authors，year的map
				}

				long endTime = System.currentTimeMillis();
				long duration = (endTime - startTime);

				System.out.println("Done with creating maps in " + duration/1000 + " seconds!");

				/*File file = new File("author_papervenuelist_map.ser");
				ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
				output.writeObject(author_papervenuelist_map);
				output.flush();
				output.close();

				File file2 = new File("venue_paperauthorslist_map.ser");
				ObjectOutputStream output2 = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file2)));
				output2.writeObject(venue_paperauthorslist_map);
				output2.flush();
				output2.close();*/

				FileOutputStream fileOut = new FileOutputStream("author_papervenuelist_map.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(author_papervenuelist_map);
				out.close();
				fileOut.close();

				FileOutputStream fileOut1 = new FileOutputStream("venue_paperauthorslist_map.ser");
				ObjectOutputStream out1 = new ObjectOutputStream(fileOut1);
				out1.writeObject(venue_paperauthorslist_map);
				out1.close();
				fileOut1.close();

				endTime = System.currentTimeMillis();
				duration = (endTime - startTime);

				System.out.println("Done with saving maps in " + duration/1000 + " seconds!");
			}

		
			
			try{
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(APVPA_file_name)));
				BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(APAPA_file_name)));

				labels = new BufferedReader(new FileReader(labels_file_name));                          //这是在干啥？？？
				// file format example
				//0,1:1
				//...
				//2,3:0
				int counter = 0;
				int from = 0, to = 0, sourceNode, destNode;
				while ((currentLineString = labels.readLine()) != null){
					counter++;
					
					from = 0;
					to = currentLineString.indexOf(",", from);   //返回从 fromIndex 位置开始查找指定字符在字符串中第一次出现处的索引，在这里指的是从from的位置开始查找‘,’
					sourceNode = Integer.parseInt(currentLineString.substring(from,to));  //substring() 方法返回字符串的子字符串。substring(int beginIndex, int endIndex（不包括）)
					//if (sourceNode>20)
					//	break;
					from = to+1;
					to = currentLineString.indexOf(":", from);
					destNode = Integer.parseInt(currentLineString.substring(from,to));
					bw.write(pathSim(Integer.toString(sourceNode), Integer.toString(destNode))+"\n");//sourceNode主作者的index，destNode关联作者的index
					//if (counter%100000==0)
					//	System.out.println(counter);

					float pc1 = pathSim(Integer.toString(sourceNode), Integer.toString(destNode));
					float pc2 = pathSim2(Integer.toString(sourceNode), Integer.toString(destNode));
					//bw.write(pc1 +"\n");
					//bw2.write(pc2 +"\n");
					if (counter%10==0)
						System.out.println(counter);
					
					System.out.println("-pathSim(" + sourceNode + "," + destNode+ ") = " + pc1 );
					System.out.println("-pathSim2(" + sourceNode + "," + destNode+ ") = " + pc2 );
					
				}

				bw.close();
				bw2.close();

			}catch (IOException e) 
			{
				System.out.println(e);
				e.printStackTrace();
			} 
			
			
			//for (int i=0; i<3177887;i++){                     计算author inde有多少邻居
			for (int i=0; i<0;i++){                
				authorIndex = Integer.toString(i);
				TreeSet<Integer> n = getNeighbors(authorIndex);
				System.out.println("Author " + authorIndex + " has " + n.size() + " neighbors");
				//System.out.println(n.size() + " neighbors of " + authorIndex + " are " + n);
				int c = 0;
				for (int neighborIndex: n){
					System.out.println(++c + "-pathSim(" + authorIndex + "," + neighborIndex+ ") = " + pathSim(authorIndex, Integer.toString(neighborIndex)) );
				}
				/*
				for (int neighborIndex: n){
					// check if they are actually co-authors
					//System.out.println("neighborIndex: " + neighborIndex);
				
					List<PaperVenue> papervenuelist = author_papervenuelist_map.get(authorIndex);
					for (PaperVenue pv : papervenuelist){
						List<PaperAuthors> paperauthorslist = venue_paperauthorslist_map.get(pv.getVenue());
						for (PaperAuthors pa: paperauthorslist){
							if (pv.getPaper().equals(pa.getPaper())){ // find the same paper
								for (String author: pa.getAuthors())
									if (Integer.parseInt(author) == neighborIndex){
										System.out.println("authorIndex: " + authorIndex +" - neighborIndex: " + neighborIndex + " are co-authors");
									}
							}
						}
					}

					//pathSim(authorIndex, neighborIndex);
				}*/
								
			}

			//pathCount("41527","29176");

			brPaperAuthor.close();
			brPaperVenue.close();
			labels.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
