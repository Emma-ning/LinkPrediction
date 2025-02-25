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

public class DBLP_PC_APAPA_APVPA {

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
	 * Finds authors of paths of type A-P-V-P-A (e.g. Jim-P1-KDD-P4-Tom)
	 * @return 
	 */
	public static HashSet<String> pathFind(String a){
		HashSet<String> connections = new HashSet<String>();
		String v, i, j;
		//long startTime = System.currentTimeMillis();

		// for author with index a for each paper index i 
		//   find venue v where i is published at
		//   find authors that published in v
		
		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);                 
		
		//if (papervenuelist.size()<5)
		//	return -10;
		
		for (PaperVenue pv : papervenuelist){
			// ignore papers out of target interval
			if (pv.getYear() < fromYear || pv.getYear() > toYear)                    //对于一个paper，如果年份满足要求
				continue;
			
			i = pv.getPaper();                                                      //则i=其paperindex，v=其venue
			v = pv.getVenue();
			//System.out.println("v: " + v);
			List<PaperAuthors> paperauthorslist = venue_paperauthorslist_map.get(v);       //再在venue_paperauthorslist_map中，查找v所包括的paper以及author
			for (PaperAuthors pa: paperauthorslist){                                       //放入list-paperauthorslist中，
				if (pa.getYear() < fromYear || pa.getYear() > toYear)                 //对于这个里面的每一条记录，如果符合年份要求
					continue;
				j = pa.getPaper();                                                   //得到j=其paperindex
				for (String author: pa.getAuthors())                                 //对于这个paper的每一个作者
					connections.add(author);                                     //都要放入connections中 
					//System.out.println("There is a path from " + a + " to " + author + ": " + i + "-" + v + "-" + j);
			}
		}
		return connections;
	}

	
	/**
	 * Finds authors of paths of type A-P-V-P-A (e.g. Jim-P1-KDD-P4-Tom)   查看作者a与b之间的路径数量
	 * @return 
	 */
	public static int countPath(String a, String b){
		String v, i, j;
		int PathCount = 0;
		//long startTime = System.currentTimeMillis();

		// for author with index a for each paper index i 
		//   find venue v where i is published at
		//   find authors that published in v
		
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
					if (author.equals(b))
						PathCount++;
					//System.out.println("There is a path from " + a + " to " + author + ": " + i + "-" + v + "-" + j);
			}
		}
		return PathCount;
	}
	
	
	
	
	
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

		// for author with index a for each paper index i 
		//   find venue v where i is published at
		//   for each paper index j that is published at v (j and i can be equal for instance for PC(i,i)
		//		if j has author index b, then PathCount++
		
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

		// for author with index a for each paper index i                                                 a-authorindex； i-paperindex
		//   for each author c who wrote i and c!=a                                                        c写了i但是c！=a
		//   for each paper index j that is written by c (j and i can be equal for instance for PC(i,i)    c同时也写了其他的paper，我们命名为j  
		//		if j has author index b, then PathCount++                                           如果j有其他作者b，那么PathCount++ 
		
		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);      ///得到关于作者a的paper-venue
		
		//if (papervenuelist.size()<5)
		//	return -10;
  
		for (PaperVenue pv : papervenuelist){                     //对每一个paperindex进行遍历                                
			// ignore papers out of target interval
			if (pv.getYear() < fromYear || pv.getYear() > toYear)
				continue;
			
			i = pv.getPaper();                                    //对符合年份条件的paperindex
			for (String c: paper_authorslist_map.get(i)){         //遍历所有作者，
				if (c.equals(a))                              //如果c=a，跳出本次循环
					continue;
				for (PaperVenue pv2 : author_papervenuelist_map.get(c)){    //对于c！=a的情况
					// ignore papers out of target interval
					if (pv2.getYear() < fromYear || pv2.getYear() > toYear)   
						continue;
					j = pv2.getPaper();                                            //得到c写的其他符合年份要求的paper
					for (String author: paper_authorslist_map.get(j)){             //并得到每一个paper的相关作者list，并遍历这个list
						if (author.equals(b)){                                 //如果其中有作者等于前面我们所查找的b，则ok
							//if (b.equals("485596"))
							//	System.out.println("There is a path from " + a + " to " + b + ": " + i + "-" + c + "-" + j);
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
		if (a_a<0 || b_b<0)               //a或者b作者在同一个venue发表的数量小于0篇
			return -10;

		
		// check if the source or destination author actually published in that interval to avoid NaN for 0.0/0.0   a与b作者同时发布数量小于0
		if (a_a + b_b == 0)
			return -1;
		
		ps = (float)2*pathCount(a,b) / (float)(a_a + b_b);        //如果a与b发布数量满足以上条件，则定义了一个公式
		if (ps > 1)                                               //说明了什么？
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
		
		// ignore less productive     查看a
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

		// for author with index a for each paper index i                                        a-authorindex； i-paperindex
		//   find venue v where i is published at                                     v-venue   i发布在v
		//   for each paper index j that is published at v (j and i can be equal for instance for PC(i,i)   对于发布在v的其他paper-j
		//		add authors of j as neighbors of a                                                  j的作者就是a的邻居

		List<PaperVenue> papervenuelist = author_papervenuelist_map.get(a);
		for (PaperVenue pv : papervenuelist){
			// ignore papers before 2000
			//if (pv.getYear() < 2010)
			//	continue;
			
			i = pv.getPaper();
			v = pv.getVenue();
			List<PaperAuthors> paperauthorslist = venue_paperauthorslist_map.get(v);
			for (PaperAuthors pa: paperauthorslist){
				j = pa.getPaper();
				for (String author: pa.getAuthors())
					n.add(Integer.parseInt(author));
			}
		}

		n.remove(Integer.parseInt(a)); // remove the node from its neighbor
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
		String labels_file_name = "DBLP/labels_1996_2002_newLinkIn_2003_2009.txt";

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

			if (readFromSavedGeneratedHashmaps==true){                                              //？？？？？

				// <author, list of [paper, venue]>
				FileInputStream fileIn = new FileInputStream("author_papervenuelist_map.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				author_papervenuelist_map = (HashMap<String, List<PaperVenue>>) in.readObject();  //读取map-author_papervenuelist_map.ser
				in.close();
				fileIn.close();

				// <venue, list of [paper, author]>
				FileInputStream fileIn1 = new FileInputStream("venue_paperauthorslist_map.ser");
				ObjectInputStream in1 = new ObjectInputStream(fileIn1);
				venue_paperauthorslist_map = (HashMap<String, List<PaperAuthors>>) in1.readObject();  //读取map-venue_paperauthorslist_map.ser
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

				while ((currentLineString = brPaperVenue.readLine()) != null) {
					StringTokenizer st2 = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st2.nextToken();
					venueIndex = st2.nextToken();
					paperVenue[Integer.parseInt(paperIndex)] = Integer.parseInt(venueIndex);
				}

				int year;
				while ((currentLineString = brPaperYear.readLine()) != null) {
					StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st.nextToken();
					year = Integer.parseInt(st.nextToken());
					paper_year_map.put(paperIndex, year);
				}


				while ((currentLineString = brPaperAuthor.readLine()) != null) {
					List<PaperVenue> paperVenueList = new ArrayList<PaperVenue>();
					List<PaperAuthors> paperAuthorsList = new ArrayList<PaperAuthors>();

					StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
					paperIndex = st.nextToken();
					authorIndex = st.nextToken();
					venueIndex = Integer.toString(paperVenue[Integer.parseInt(paperIndex)]);	
					year = paper_year_map.get(paperIndex);

					// add to author_papervenuelist_map
					PaperVenue new_pv = new PaperVenue(paperIndex, venueIndex, year); 
					if (author_papervenuelist_map.containsKey(authorIndex)){
						paperVenueList = author_papervenuelist_map.get(authorIndex);
					}
					paperVenueList.add(new_pv);
					author_papervenuelist_map.put(authorIndex, paperVenueList);

					// add to venue_paperauthorslist_map
					ArrayList<String> authorsList = new ArrayList<String>();
					authorsList.add(authorIndex);
					PaperAuthors new_pa = new PaperAuthors(paperIndex, authorsList, year); 				
					boolean paperFoundInVenue = false;
					if (venue_paperauthorslist_map.containsKey(venueIndex)){
						paperAuthorsList = venue_paperauthorslist_map.get(venueIndex);
						for (PaperAuthors pa: paperAuthorsList){
							if (pa.getPaper().equals(paperIndex)){ // add author to list of authors for the existing paper
								authorsList = pa.getAuthors();
								authorsList.add(authorIndex);
								pa.setAuthors(authorsList);
								paperFoundInVenue = true;
								break;
							}
						}
						if (paperFoundInVenue == false){ // new paper and new author should be added
							paperAuthorsList.add(new_pa);
						}
					}else
						paperAuthorsList.add(new_pa);
					venue_paperauthorslist_map.put(venueIndex, paperAuthorsList);
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

				labels = new BufferedReader(new FileReader(labels_file_name));
				// file format example
				//0,1:1
				//...
				//2,3:0
				int counter = 0;
				int from = 0, to = 0, sourceNode, destNode;
				while ((currentLineString = labels.readLine()) != null){
					counter++;
					
					from = 0;
					to = currentLineString.indexOf(",", from);
					sourceNode = Integer.parseInt(currentLineString.substring(from,to));
					//if (sourceNode>20)
					//	break;
					from = to+1;
					to = currentLineString.indexOf(":", from);
					destNode = Integer.parseInt(currentLineString.substring(from,to));
					
					//System.out.println(sourceNode + " : " + pathFind(Integer.toString(sourceNode)));
					System.out.println(sourceNode + ", " +destNode+ " : " + countPath(Integer.toString(sourceNode), Integer.toString(destNode)));
					System.out.println(sourceNode + ", " +destNode+ " : " + pathCount(Integer.toString(sourceNode), Integer.toString(destNode)));
					System.out.println(sourceNode + ", " +destNode+ " : " + pathSim(Integer.toString(sourceNode), Integer.toString(destNode)));

					if (counter%10==0){
						long endTime = System.currentTimeMillis();
						long duration = (endTime - startTime);
						System.out.println(counter + " - total time : " + duration/1000 + " seconds!");
					}

					
					/*float pc1 = pathSim(Integer.toString(sourceNode), Integer.toString(destNode));
					float pc2 = pathSim2(Integer.toString(sourceNode), Integer.toString(destNode));
					bw.write(pc1 +"\n");
					bw2.write(pc2 +"\n");
					if (counter%100000==0){
						long endTime = System.currentTimeMillis();
						long duration = (endTime - startTime);
						System.out.println(counter + " - total time : " + duration/1000 + " seconds!");
					}
					*/
					
					//System.out.println("-pathSim(" + sourceNode + "," + destNode+ ") = " + pc1 );
					//System.out.println("-pathSim2(" + sourceNode + "," + destNode+ ") = " + pc2 );

				}

				bw.close();
				bw2.close();

			}catch (IOException e) 
			{
				System.out.println(e);
				e.printStackTrace();
			} 
			
			
			brPaperAuthor.close();
			brPaperVenue.close();
			labels.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
