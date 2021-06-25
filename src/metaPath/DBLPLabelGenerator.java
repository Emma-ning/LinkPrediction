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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to generate list of pairs and labels based on new coauthorship relation
 *		Output file example
 *			6 
 *			0,1:1
 *			0,2:1
 *			1,0:1
 *			1,2:0
 *			2,0:1
 *			2,1:0
 * 
 * @author aminmf
 */
public class DBLPLabelGenerator {

	private static TreeMap<Integer, ArrayList<Integer>> author_papers_map1 = new TreeMap<Integer, ArrayList<Integer>>();     //TreeMap=dict 
	private static TreeMap<Integer, ArrayList<Integer>> paper_authors_map1 = new TreeMap<Integer, ArrayList<Integer>>();    

	private static TreeMap<Integer, ArrayList<Integer>> author_papers_map2 = new TreeMap<Integer, ArrayList<Integer>>();    
	private static TreeMap<Integer, ArrayList<Integer>> paper_authors_map2 = new TreeMap<Integer, ArrayList<Integer>>();    

	private static TreeMap<Integer, Integer> paper_year_map = new TreeMap<Integer, Integer>();

	private static TreeMap<Integer, TreeSet<Integer>> coauthors1 = new TreeMap<Integer, TreeSet<Integer>>();    // coauthors in the first interval
	private static TreeMap<Integer, TreeSet<Integer>> coauthors1_minPublication = new TreeMap<Integer, TreeSet<Integer>>();    // min paper constraints
	private static TreeMap<Integer, TreeSet<Integer>> coauthors2 = new TreeMap<Integer, TreeSet<Integer>>();    // coauthors in the second interval


	// arguments : minPublication, fromYear1, toYear1, fromYear2, toYear1, output file name
	public static void main(String[] args) 
	{	 
		String currentLineString;
		int paperIndex, authorIndex;
		int year;
		int fromYear1 = 1996, toYear1 = 2002;  // these are to set intervals for the first chunk
		int fromYear2 = 2003, toYear2 = 2009;  // these are to set intervals for the second chunk (to find new links for +1 labels)

		int minPublication = 1; // (default should be 1) if an author has less than this min papers, will not be considered in the dataset

		//minPublication = Integer.parseInt(args[0]);      //parseInt：解析参数，如果 string 以 1 ~ 9 的数字开头，parseInt() 将把它解析为十进制的整数     
		                                                   //args[0]是你传入的第一个参数
		fromYear1 = Integer.parseInt(args[0]);
		toYear1 = Integer.parseInt(args[1]);  
		fromYear2 = Integer.parseInt(args[2]);
		toYear2 = Integer.parseInt(args[3]);  


		minPublication = 5;        //如果这个作者发表的论文数量小于5篇，则不会被考虑进数据库
		//fromYear1 = 1996;
		//toYear1 = 1998;  
		//fromYear2 = 1999;
		//toYear2 = 2001;  // 5-7 8-10 11-13 14-16

		String posLableFileName = fromYear1+"_"+toYear1+"_newLinkIn_"+fromYear2+"_"+toYear2+"_posLabels.txt";
		String negLableFileName = fromYear1+"_"+toYear1+"_newLinkIn_"+fromYear2+"_"+toYear2+"_negLabels.txt";
		String minPubPosLableFileName = fromYear1+"_"+toYear1+"_newLinkIn_"+fromYear2+"_"+toYear2+"_min5paper_posLabels.txt";
		String minPubNegLableFileName = fromYear1+"_"+toYear1+"_newLinkIn_"+fromYear2+"_"+toYear2+"_min5paper_negLabels.txt";


		/*minPublication = 5;
		fromYear1 = 2002;
		toYear1 = 2004;  
		fromYear2 = 2005;
	    toYear2 = 2007;  // 5-7 8-10 11-13 14-16
	    String lableFileName = "labels_2002_2004_newLinkIn_2005_2007_min5paper.txt";//_min5paper
		 */

		/*
		int fromYear1 = 2003, toYear1 = 2009;  // these are to set intervals for the first chunk
		int fromYear2 = 2010, toYear2 = 2016;  // these are to set intervals for the second chunk (to find new links for +1 labels)
		 */

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			BufferedReader br_year = new BufferedReader(new FileReader("paper_newindex_year.txt"));
			BufferedWriter bw_pos_all = new BufferedWriter(new FileWriter(new File(posLableFileName)));
			BufferedWriter bw_neg_all = new BufferedWriter(new FileWriter(new File(negLableFileName)));
			BufferedWriter bw_pos_minpub = new BufferedWriter(new FileWriter(new File(minPubPosLableFileName)));
			BufferedWriter bw_neg_minpub = new BufferedWriter(new FileWriter(new File(minPubNegLableFileName)));

			while ((currentLineString = br_year.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = Integer.parseInt(st.nextToken());
				year = Integer.parseInt(st.nextToken());                     
				paper_year_map.put(paperIndex, year);
			}       // 新发表的paper及年份，token后 生成maptree


			while ((currentLineString = br.readLine()) != null) {
				ArrayList<Integer> papersList = new ArrayList<Integer>();    //ArrayList是自动扩容的Arrays。有add().remove()这样的方法
				ArrayList<Integer> authorsList = new ArrayList<Integer>();

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = Integer.parseInt(st.nextToken());
				authorIndex = Integer.parseInt(st.nextToken());     //token paper和author的index

				year = 
					
					.get(paperIndex);
				if (year >= fromYear1 && year <= toYear1){                            //对于在第一段时间内发表的论文

					if (author_papers_map1.containsKey(authorIndex)){              //containsKey 如果此映射包含指定键的映射关系，则返回 true
						papersList = author_papers_map1.get(authorIndex);      //如果存在authorindex的关系，那么用authorindex生成一个paperlist，
					}                                                              //然后再paperlist中加入paperindex
					papersList.add(paperIndex);                                    //最终生成author_papers_map1（后面是如果存在paperindex的情况）
					author_papers_map1.put(authorIndex, papersList);

					if (paper_authors_map1.containsKey(paperIndex)){              //后面是如果存在paperindex的情况，最终生成paper_authors_map1
						authorsList = paper_authors_map1.get(paperIndex);
					}
					authorsList.add(authorIndex);
					paper_authors_map1.put(paperIndex, authorsList);

				}else if (year >= fromYear2 && year <= toYear2){                      //对于在第二段时间内发表的论文

					if (author_papers_map2.containsKey(authorIndex)){
						papersList = author_papers_map2.get(authorIndex);
					}
					papersList.add(paperIndex);
					author_papers_map2.put(authorIndex, papersList);

					if (paper_authors_map2.containsKey(paperIndex)){
						authorsList = paper_authors_map2.get(paperIndex);
					}
					authorsList.add(authorIndex);
					paper_authors_map2.put(paperIndex, authorsList);

				}


			}


			for (int i=0; i<1752443; i++){
				TreeSet<Integer> coauthorsList = new TreeSet<Integer>();       //生成一个新的treeset coauthorsList
				if (author_papers_map1.containsKey(i)){                        //对于author_papers_map1，containsKey是i(即authorindex）
					// disregard author who has less than 1 papers           忽视未发表paper的author
					if (author_papers_map1.get(i).size() < 1)              
						continue;
					for (Integer p: author_papers_map1.get(i)){            //对于每一个authorindex，会有好多p（即paperindex）
						for (Integer a: paper_authors_map1.get(p)){    //每一个paperindex又会有好多的authorindex，
							if (a != i) 
								coauthorsList.add(a);          //因此可以生成一个coauthorsList，除了i以外的作者
						}                  
					}
					coauthors1.put(i, coauthorsList);                     //最终生成一个coauthors1
				}else{
					//System.out.println("Author " + i + " has no publication at time1!.");
				}

				TreeSet<Integer> coauthorsList2 = new TreeSet<Integer>();     //对第二个时间段
				if (author_papers_map2.containsKey(i)){
					for (Integer p: author_papers_map2.get(i)){
						for (Integer a: paper_authors_map2.get(p)){
							if (a != i) 
								coauthorsList2.add(a);
						}
					}
					coauthors2.put(i, coauthorsList2);
				}
			}


			for (int i=0; i<1752443; i++){
				TreeSet<Integer> coauthorsList_minPublication = new TreeSet<Integer>();      //设置了一个最少论文数量，只有超过这个数量的作者才会被考虑
				if (author_papers_map1.containsKey(i)){
					// disregard author who has less than min papers
					if (author_papers_map1.get(i).size() < minPublication)
						continue;
					for (Integer p: author_papers_map1.get(i)){
						for (Integer a: paper_authors_map1.get(p)){
							if (a != i) 
								coauthorsList_minPublication.add(a);
						}
					}
					coauthors1_minPublication.put(i, coauthorsList_minPublication);    //生成满足论文发表数量数量限制的map，名为coauthors1_minPublication
				}else{
					//System.out.println("Author " + i + " has no publication at time1!.");
				}			
			}



			
			
			TreeSet<Integer> firstIntervalCoAuthors = new TreeSet<Integer>();
			TreeSet<Integer> secondIntervalCoAuthors = new TreeSet<Integer>();

			for (int i=0; i<1752443; i++){

				//firstIntervalCoAuthors.clear();
				//secondIntervalCoAuthors.clear();

				if (coauthors1.containsKey(i)){
					firstIntervalCoAuthors = coauthors1.get(i);                                               //Firstinterval里的i（authorindex）
					//System.out.println("coauthors1.get(" + i + "): " + firstIntervalCoAuthors);            

					if (coauthors2.containsKey(i)){
						secondIntervalCoAuthors = coauthors2.get(i);                                     //Secondinterval里的i（authorindex）
						//System.out.println("coauthors2.get(" + i + "): " + secondIntervalCoAuthors);
					}else{
						//System.out.println("No new connections for " + i);
						continue;
					}

					for (Integer c: secondIntervalCoAuthors){                                     //在Secondinterval的循环中，i（authorindex）中存在多个c（coauthor）
						if (!firstIntervalCoAuthors.contains(c)){                             //如果c不在乎firstinterval中，则write文件‘bw_pos_all’---i,c
							//System.out.println(c + " is a new connection for " + i);
							//System.out.println(i + "," +c + ":1");
							bw_pos_all.write(i + "," + c + ":1\n");
						}
					}

				}else{
					//firstIntervalCoAuthors.clear();
					//firstIntervalCoAuthors.add(-1);
				}
			}


			for (int i=0; i<1752443; i++){                                                               //在限制最小发表数量的情况下
				if (coauthors1_minPublication.containsKey(i)){
					firstIntervalCoAuthors = coauthors1_minPublication.get(i);
					//System.out.println("coauthors1.get(" + i + "): " + firstIntervalCoAuthors);

					if (coauthors2.containsKey(i)){
						secondIntervalCoAuthors = coauthors2.get(i);
						//System.out.println("coauthors2.get(" + i + "): " + secondIntervalCoAuthors);
					}else{
						//System.out.println("No new connections for " + i);
						continue;
					}

					for (Integer c: secondIntervalCoAuthors){
						if (!firstIntervalCoAuthors.contains(c)){
							//System.out.println(c + " is a new connection for " + i);
							//System.out.println(i + "," +c + ":1");
							bw_pos_minpub.write(i + "," + c + ":1\n");
						}
					}

				}else{
					//firstIntervalCoAuthors.clear();
					//firstIntervalCoAuthors.add(-1);
				}
			}


			// Negative labels

			TreeSet<Integer> twoHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> threeHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> cocoauthorsList = new TreeSet<Integer>();

			//for (int i=0; i<1752443; i++){  // 1752443
			for (int i=0; i<1752443; i++){  // 1752443

				if (coauthors1.containsKey(i)){                                                //对于每一个author
					TreeSet<Integer> coauthorsList = coauthors1.get(i);                    //得到每个author的coauthor123
					for (Integer j:coauthorsList){                                         //如果coauthor123也存在在主author表中
						if (coauthors1.containsKey(j)){
							//cocoauthorsList = coauthors1.get(j);
							twoHopCoauthors.addAll(coauthors1.get(j));         //那么得到coauthor123的coauthor并生成字典名‘twoHopCoauthors’
							//for (Integer k:cocoauthorsList){                 
							//	if (coauthors1.containsKey(k))
							//		threeHopCoauthors.addAll(coauthors1.get(k));
							//}					
						}
					}
					// finally decide to mergethem all!                                     //此时得到的twoHopCoauthors需要去掉author-i和i的相关coauthor
					twoHopCoauthors.remove(i); // remove author himself
					twoHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors 

					//threeHopCoauthors.addAll(twoHopCoauthors);                            //也就是说这个数据集代表了，a与b合作，b与c合作，但c又与a没有联系的人
					//threeHopCoauthors.remove(i); // remove author himself
					//threeHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors

					for (Integer c: twoHopCoauthors){
						bw_neg_all.write(Integer.toString(i)+","+Integer.toString(c) + ":0\n");   //bw_neg_all是干什么的
						//negativeLabelLinks.add(Integer.toString(i)+","+Integer.toString(c));
					}					

					twoHopCoauthors.clear();
					//threeHopCoauthors.clear();
				}

				/**



				if (coauthors1_minPublication.containsKey(i)){
					TreeSet<Integer> coauthorsList = coauthors1_minPublication.get(i);
					for (Integer j:coauthorsList){
						if (coauthors1_minPublication.containsKey(j)){
							twoHopCoauthors.addAll(coauthors1_minPublication.get(j));
						}
					}
					// finally decide to mergethem all!
					twoHopCoauthors.remove(i); // remove author himself
					twoHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors

					for (Integer c: twoHopCoauthors){
						bw_neg_minpub.write(Integer.toString(i)+","+Integer.toString(c) + ":0\n");
					}					

					twoHopCoauthors.clear();
				}


			}


			//Collections.shuffle(negativeLabelLinks);

			//System.out.println("negativeLabelLinks: " + negativeLabelLinks.size());

			//for (int j=0;j<totalPositiveSamples;j++){
			//System.out.println(i + "," +candidatesForNegativeLabel.get(j)+ ":0");
			//	bw1.write(negativeLabelLinks.get(j) + ":0\n");
			//}





			/**
			 * IGNORE!
			 */


			/*TreeSet<Integer> twoHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> threeHopCoauthors = new TreeSet<Integer>();
			TreeSet<Integer> cocoauthorsList = new TreeSet<Integer>();
			for (int i=0; i<1752443; i++){  // 1752443

				if (coauthors1.containsKey(i)){
					TreeSet<Integer> coauthorsList = coauthors1.get(i);
					for (Integer j:coauthorsList){
						if (coauthors1.containsKey(j)){
							cocoauthorsList = coauthors1.get(j);
							twoHopCoauthors.addAll(coauthors1.get(j));
							for (Integer k:cocoauthorsList){
								if (coauthors1.containsKey(k))
									threeHopCoauthors.addAll(coauthors1.get(k));
							}					
						}
					}
					// finally decide to mergethem all!
					threeHopCoauthors.addAll(twoHopCoauthors);

					threeHopCoauthors.remove(i); // remove author himself
					threeHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors

					//System.out.println("1Hop(" + i + ")=" + coauthorsList);
					//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
					//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
					//System.out.println("newConnections.get(i): " + newConnections.get(i));
					// choose n = newConnections.get(i) random for 0 label from threeHopCoauthors
					if (threeHopCoauthors.size() >= newConnections.get(i)){
						ArrayList<Integer> candidatesForNegativeLabel = new ArrayList<Integer>();   
						for (Integer c: threeHopCoauthors){
							candidatesForNegativeLabel.add(c);
						}
						Collections.shuffle(candidatesForNegativeLabel);
						for (int j=0;j<newConnections.get(i);j++){
							//System.out.println(i + "," +candidatesForNegativeLabel.get(j)+ ":0");
							bw.write(i + "," +candidatesForNegativeLabel.get(j)+ ":0\n");
						}
					}else{
						// add all 3hops
						for (Integer c: threeHopCoauthors){
							//System.out.println(i + ",c=" + c + ":0");
							bw.write(i + "," + c + ":0\n");
						}
					}


					threeHopCoauthors.removeAll(twoHopCoauthors); // remove two hop coauthors

					twoHopCoauthors.removeAll(coauthorsList); // remove first hop coauthors
					twoHopCoauthors.remove(i); // remove author himself

					//System.out.println("1Hop(" + i + ")=" + coauthorsList);
					//System.out.println("2Hop(" + i + ")=" + twoHopCoauthors);
					//System.out.println("Size of 1Hop(" + i + ")=" + coauthorsList.size());
					//System.out.println("Size of 2Hop(" + i + ")=" + twoHopCoauthors.size() + "\tSize of 3Hop(" + i + ")=" + threeHopCoauthors.size());

					twoHopCoauthors.clear();
					threeHopCoauthors.clear();

				}

			}
			 */


			br.close();
			br_year.close();
			bw_pos_all.close();
			bw_neg_all.close();
			bw_pos_minpub.close();
			bw_neg_minpub.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
