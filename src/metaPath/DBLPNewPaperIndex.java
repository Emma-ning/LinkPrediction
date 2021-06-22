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

public class DBLPNewPaperIndex {

	public static void main(String[] args) 
	{	 
		String currentLineString, paperIndex = null, otherString=null, newPaperIndex=null;
		Map<String, String> paper_old_new_index_map = new HashMap<String, String>();
		int index=0;

		try{
			// file to generate old_new index map from
			BufferedReader br = new BufferedReader(new FileReader("paper_year.txt"));
			// files containing old paper index
			BufferedReader brCitedBy = new BufferedReader(new FileReader("citedby.txt"));
			BufferedReader brPaperIndex = new BufferedReader(new FileReader("paper_index.txt"));
			BufferedReader brPaperVenue = new BufferedReader(new FileReader("paper_venue.txt"));
			BufferedReader brPaperAuthor = new BufferedReader(new FileReader("paper_author.txt"));
			BufferedReader brPaperYear = new BufferedReader(new FileReader("paper_year.txt"));
			// new files to generate with new paper index
			BufferedWriter bwCitedBy = new BufferedWriter(new FileWriter(new File("paper_newindex_citedby.txt")));
			BufferedWriter bwPaperIndex = new BufferedWriter(new FileWriter(new File("newindex_paper_index.txt")));
			BufferedWriter bwPaperVenue = new BufferedWriter(new FileWriter(new File("paper_newindex_venue.txt")));
			BufferedWriter bwPaperAuthor = new BufferedWriter(new FileWriter(new File("paper_newindex_author.txt")));
			BufferedWriter bwPaperYear = new BufferedWriter(new FileWriter(new File("paper_newindex_year.txt")));

			while ((currentLineString = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				otherString = st.nextToken();
				if (!paper_old_new_index_map.containsKey(paperIndex)){                      //将br中所有的paperindex都导入到这个map中
					paper_old_new_index_map.put(paperIndex, Integer.toString(index));    //将paperindex进行序号排列  如：3243，0；3412，1
					index++;
				}
			}
			// citedby
			while ((currentLineString = brCitedBy.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");      //对每一行进行拆分
				paperIndex = st.nextToken();                                           //拆分出paperindex
				newPaperIndex = paper_old_new_index_map.get(paperIndex);               //new paperindex=对应着相应的我们刚刚新赋值的0，1，2
				bwCitedBy.write(newPaperIndex + "\t");                                 //在bwCitedBy中新填写newindex 在加一个tab（即空格）
				paperIndex = st.nextToken();                                           //此时再拆分paperindex 得到的数据为citeby的论文
				newPaperIndex = paper_old_new_index_map.get(paperIndex);               //在生成新index
				bwCitedBy.write(newPaperIndex + "\n");                                 //添加到bwCitedBy中
			}
			// paper_index
			while ((currentLineString = brPaperIndex.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				newPaperIndex = paper_old_new_index_map.get(paperIndex);              //将paperindex换成我们刚刚新赋值的
				bwPaperIndex.write(newPaperIndex + "\t");                             //然后write文件 再加tab
				otherString = st.nextToken();                                         //将剩下的string 加在后面一行
				bwPaperIndex.write(otherString + "\n");
			}
			// paper_venue
			while ((currentLineString = brPaperVenue.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				newPaperIndex = paper_old_new_index_map.get(paperIndex);
				bwPaperVenue.write(newPaperIndex + "\t");
				otherString = st.nextToken();
				bwPaperVenue.write(otherString + "\n");
			}
			// paper_author
			while ((currentLineString = brPaperAuthor.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				newPaperIndex = paper_old_new_index_map.get(paperIndex);
				bwPaperAuthor.write(newPaperIndex + "\t");
				otherString = st.nextToken();
				bwPaperAuthor.write(otherString + "\n");
			}
			// paper_year
			while ((currentLineString = brPaperYear.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				newPaperIndex = paper_old_new_index_map.get(paperIndex);
				bwPaperYear.write(newPaperIndex + "\t");
				otherString = st.nextToken();
				bwPaperYear.write(otherString + "\n");
			}


			br.close();
			brCitedBy.close();
			brPaperIndex.close();
			brPaperVenue.close();
			brPaperAuthor.close();
			brPaperYear.close();
			bwCitedBy.close();
			bwPaperIndex.close();
			bwPaperVenue.close();
			bwPaperAuthor.close();
			bwPaperYear.close();
		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
