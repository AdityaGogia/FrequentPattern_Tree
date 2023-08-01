package com.ese589;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;



public class FPGrowthMain {
	 
	static String data_set = "dataset.dat";
	static String name_data_set = "name.dat";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long currentTimeMillis_start = System.currentTimeMillis();
		Scanner min_support_scanner = new Scanner(System.in);
		System.out.println("Please enter the Min_Support");
		int min_support = min_support_scanner.nextInt();
		
		File da = new File(data_set);
		Scanner input_dataSet = null;
		try {
			input_dataSet = new Scanner(da);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		
		 while(input_dataSet.hasNextLine()) {	
				try {	
					String temp = input_dataSet.nextLine();
					if(temp.trim().equals("")) {
						continue;
					}
				    fw = new FileWriter("temp.dat", true);
				    bw = new BufferedWriter(fw);
				    out = new PrintWriter(bw);
				    bw.flush();
				    
				    out.println(temp.replaceAll(",", " "));
				    out.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				
		 }
		 
		 
			File temp = new File("temp.dat");
			Scanner input_data_removed_spaces_scanner = null;
			try {
				input_data_removed_spaces_scanner = new Scanner(temp);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			File nameset = new File(name_data_set);
			while (input_data_removed_spaces_scanner.hasNextLine()) {
				Scanner nameset_scanner = null;
				try {
					nameset_scanner = new Scanner(nameset);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (nameset_scanner.hasNext() == true) {
					 fw = null;
					 bw = null;
					 out = null;
					try {
						fw = new FileWriter("final.dat", true);
						bw = new BufferedWriter(fw);
						out = new PrintWriter(bw);
						StringBuilder a = new StringBuilder();
						String[] a1 = input_data_removed_spaces_scanner.nextLine().split(" ");
						String fi = "";
						for (String a2 : a1) {
							if(a2.trim().equals("")) {
								continue;
							}
							fi = nameset_scanner.next() + "_" + a2 + " ";
							a.append(fi);
						}
						out.println(a);
						out.close();
					} catch (IOException e) {

					}
					
				}
			}
		long currentTimeMillis_mid = System.currentTimeMillis();
		System.out.println("Time taken to modify data set in ms is: "+ (currentTimeMillis_mid - currentTimeMillis_start));
		FPGrowthServiceImpl fpGrowthServiceImpl = new FPGrowthServiceImpl(new File("final.dat"), min_support);
		fpGrowthServiceImpl.print();
		long currentTimeMillis_end = System.currentTimeMillis();
		System.out.println("Time taken to run the algorithm in ms is: "+ (currentTimeMillis_end - currentTimeMillis_mid));
		
		
		
		
//		FPGrowthServiceImpl fpGrowthServiceImpl = new FPGrowthServiceImpl(new File("dataset.dat"), min_support);
//		System.out.println("Frequent Item sets are: ");
//		fpGrowthServiceImpl.print();
	}

}
