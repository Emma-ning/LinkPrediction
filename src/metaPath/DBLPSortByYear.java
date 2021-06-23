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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

public class DBLPSortByYear {
	
	public static class YearPaperInex {
        public String year;
        public String index;
        public YearPaperInex(String y, String i) {
        	year = y;
            index = i;
        }
    }
	
    public static void sortByYear(YearPaperInex[] paper_year_array) {
        Arrays.sort(paper_year_array, new Comparator<YearPaperInex>() {
            public int compare(YearPaperInex p1, YearPaperInex p2) { 
                return p1.year.compareTo(p2.year);                                  //对两个数组进行比较
            }
        });
    }
	
	public static void main(String[] args) 
	{	 
		String currentLineString, paperIndex = null, year=null;

		ArrayList<YearPaperInex> paper_year = new ArrayList<YearPaperInex>();

		try{
			BufferedReader br = new BufferedReader(new FileReader("paper_year.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("paper_year_sorted.txt")));

			while ((currentLineString = br.readLine()) != null) {
				//System.out.println (currentLineString);
				StringTokenizer st = new StringTokenizer(currentLineString,"\t");  
				paperIndex = st.nextToken();
				year = st.nextToken();
				paper_year.add(new YearPaperInex(year, paperIndex));              //读取文件paper_year.txt，添加到paper_year这个list中
			}

			
	    	YearPaperInex[] paper_year_array = new YearPaperInex[paper_year.size()];  //使得paper_year_array和paper_year有同样的size
	    	paper_year_array = paper_year.toArray(paper_year_array);   //toArray()方法以正确的顺序(从第一个到最后一个元素)返回一个包含此列表中所有元素的数组。
	    	
			sortByYear(paper_year_array);   //调用了sortByYear对这个array进行处理
			int count = 0;
	        for (YearPaperInex p : paper_year_array) {                      //对于paper_year_array中的每一行，分别写入文件paper_year_sorted.txt中
	        	count++;
	        	if (count%(3272992/2)==0)
	        		System.out.println(p.year);
	        	bw.write(p.year + "\t" + p.index + "\n");
	        }


			br.close();
			bw.close();

		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}

}
