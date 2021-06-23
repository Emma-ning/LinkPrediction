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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 		This is to create coauthorship.txt to be used as input for the temporal inference work (TKDE16)
 *		file format example
 *			410 
 *			0,5:1,1:4,1:5,1:49,1:50,1
 *			1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1
 * 
 * @author aminmf
 */
public class DBLPTargetRelationships {

	private static TreeMap<Integer, ArrayList<Integer>> author_papers_map = new TreeMap<Integer, ArrayList<Integer>>();    //新map-author_papers_map
	private static TreeMap<Integer, ArrayList<Integer>> paper_authors_map = new TreeMap<Integer, ArrayList<Integer>>();    //新map-paper_authors_map

	public static void main(String[] args) 
	{	 
		String currentLineString;
		int paperIndex, authorIndex;

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_newindex_author.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("coauthorship.txt")));

			while ((currentLineString = br.readLine()) != null) {
				ArrayList<Integer> papersList = new ArrayList<Integer>();            //创建了papersList
				ArrayList<Integer> authorsList = new ArrayList<Integer>();                //创建了authorsList

				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = Integer.parseInt(st.nextToken());
				authorIndex = Integer.parseInt(st.nextToken());                       //将数据拆分成paperindex和authorindex
 
				if (author_papers_map.containsKey(authorIndex)){           //由于是新map，刚开始录入时肯定是不存在重复的authorindex，因此直接添加这个人的paperindex就可以，但是后续这个人可能会写多个paper，因此他的authorindex会重复出现
					papersList = author_papers_map.get(authorIndex);           //这时，则在papersList等于在过去所填入的paperindex的基础上
				}
				papersList.add(paperIndex);                                  //再增加一个新的paperindex，形成最终的paperList
				author_papers_map.put(authorIndex, papersList);              //然后map-author_papers_map得到更新

				if (paper_authors_map.containsKey(paperIndex)){
					authorsList = paper_authors_map.get(paperIndex);
				}
				authorsList.add(authorIndex);
				paper_authors_map.put(paperIndex, authorsList);
			}


			// file format example for the temporal inference paper (TKDE16)
			//410 
			//0,5:1,1:4,1:5,1:49,1:50,1
			//1,8:0,1:5,1:38,1:39,1:49,1:69,1:71,1:107,1

			// add number of authors in the first line
			bw.write("1752443\n");

			for (int i=0; i<1752443; i++){
				TreeSet<Integer> coauthorsList = new TreeSet<Integer>();  //注意是treeset
				for (Integer p: author_papers_map.get(i)){               //得到每一个author的paperindex
					for (Integer a: paper_authors_map.get(p)){        //然后又得到每一个paper的 authorindex
						if (a != i)                              //如果前后作者不一致
							coauthorsList.add(a);              //则在list-coauthorsList中添加后面的作者
					}
				}
				bw.write(i + "," + coauthorsList.size());           //i：代表初始的author， coauthorsList.size()代表这个作者所有文章的合作者数量
				for (Integer c: coauthorsList){
					bw.write(":" + c + ",1");         //然后又列出了合作者的authorindex
				}                                         //0,5  :1,1  :4,1  :5,1  :49,1  :50,1
				bw.write("\n");
				coauthorsList.clear();
			}

			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
