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
 * 		This is to generate dataset for logistic regression using labels and features in different text files.
 * 
 * @author aminmf
 */
public class DBLPDataSetGenerator {


	public static void main(String[] args) 
	{	 
		String currentLineString;



		try{
			BufferedReader apvpa = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/PC_APVPA_2011_2013_min5paper.txt"));
			BufferedReader apapa = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/PC_APAPA_2011_2013_min5paper.txt"));
			BufferedReader apppa = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/PC_APPPA_2011_2013_min5paper.txt"));
			BufferedReader predfile = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction-5dim/k5temporalPredictionFor_2014_2016_min5paper.txt"));
			BufferedReader lfile = new BufferedReader(new FileReader("DBLP/7IntervalsPrediction/labels_2011_2013_newLinkIn_2014_2016_min5paper.txt"));
			BufferedWriter dataset1 = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/k5PC_training1_2011_2013_min5paper.txt")));
			BufferedWriter dataset2 = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/k5PC_training2_2011_2013_min5paper.txt")));
			BufferedWriter dataset3 = new BufferedWriter(new FileWriter(new File("DBLP/7IntervalsPrediction/k5PC_training3_2011_2013_min5paper.txt")));

			/*BufferedReader apvpa = new BufferedReader(new FileReader("DBLP/3IntervalsPrediction/PC_APVPA_1996_1998.txt"));
			BufferedReader apapa = new BufferedReader(new FileReader("DBLP/3IntervalsPrediction/PC_APAPA_1996_1998.txt"));
			BufferedReader apppa = new BufferedReader(new FileReader("DBLP/3IntervalsPrediction/PC_APPPA_1996_1998.txt"));
			BufferedReader predfile = new BufferedReader(new FileReader("DBLP/3IntervalsPrediction/temporalPredictionFor_1999_2001.txt"));
			BufferedReader lfile = new BufferedReader(new FileReader("DBLP/3IntervalsPrediction/labels_1996_1998_newLinkIn_1999_2001.txt"));
			BufferedWriter dataset1 = new BufferedWriter(new FileWriter(new File("DBLP/3IntervalsPrediction/PC_training1_1996_1998.txt")));
			BufferedWriter dataset2 = new BufferedWriter(new FileWriter(new File("DBLP/3IntervalsPrediction/PC_training2_1996_1998.txt")));
			BufferedWriter dataset3 = new BufferedWriter(new FileWriter(new File("DBLP/3IntervalsPrediction/PC_training3_1996_1998.txt")));
			*/
			
			
			String f0, f1, f2, f3, label;   //定义三个string

			//for (int i=0; i<8645734; i++){
			while ((currentLineString = lfile.readLine()) != null) {
			//for (int i=0; i<2888731; i++){

				//currentLineString = lfile.readLine();
				label = currentLineString.substring(currentLineString.indexOf(":")+1);     
				                                                        //substring() 方法用于提取字符串中介于两个指定下标之间的字符
				                                                        //indexOf() 方法可返回某个指定的字符串值在字符串中首次出现的位置

				f0 = apppa.readLine();
				f1 = apvpa.readLine();
				f2 = apapa.readLine();
				f3 = predfile.readLine();
				
				//dataset1.write(f1 + "\t" + f2 + "\t" + f3 + "\t" + label +"\n");
				//dataset2.write(f1 + "\t" + f2 + "\t" + label +"\n");
				//dataset3.write(f3 + "\t" + label +"\n");

				if (label.equals("1"))
					label = "+1";
				else
					label = "-1";

				//+1 1:0 2:0.661 3:0.037 4:0.500 5:0.199 6:0.006 7:0.000 8:0.015 9:0.100

				dataset1.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + " 4:" + f3 + "\n");
				dataset2.write(label + " 1:" + f0 + " 2:" + f1 + " 3:" + f2 + "\n");
				dataset3.write(label + " 1:" + f3 + "\n");


			}


			//apvpa.close();
			//apapa.close();
			predfile.close();
			lfile.close();
			dataset1.close();
			dataset2.close();
			dataset3.close();


		}catch (IOException e) 
		{
			e.printStackTrace();
		} 

	}


}
