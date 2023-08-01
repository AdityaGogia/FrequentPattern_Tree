package com.ese589;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FPGrowthServiceImpl {

	// Initializing FPtree root to null
	FPTreeNode fpTreeNode = new FPTreeNode("null");
	// Frequent Patterns item set, Result
	HashMap<String, Integer> frequentPatterns = new HashMap<String, Integer>();
	// To initialize FPTreeNode for each Item set
	ArrayList<FPTreeNode> eachItemSetTreeNode = new ArrayList<FPTreeNode>();

	public FPGrowthServiceImpl(File file, int min_support) {
		fptree(min_support, file);
		fpgrowthFacade(fpTreeNode, min_support, eachItemSetTreeNode);
	}

	private void fptree(int min_support, File file) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> itemsSetFrequency = new HashMap<String, Integer>();
		Scanner input;
		try {
			input = new Scanner(file);

			List<String> sortedItemsSetFrequency = new LinkedList<String>();
			ArrayList<String> removedItemSet = new ArrayList<String>();
			preProcessing(min_support, file, itemsSetFrequency, input, sortedItemsSetFrequency, removedItemSet);
			building_fpTree(file, itemsSetFrequency, input, sortedItemsSetFrequency, removedItemSet);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void preProcessing(int min_support, File filename, HashMap<String, Integer> itemsMaptoFrequencies,
			Scanner dataset_scanner_input, List<String> sortedItemsbyFrequencies, ArrayList<String> itemstoRemove)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		while (dataset_scanner_input.hasNext()) {
			String temp = dataset_scanner_input.next().trim();
			if (temp.contains("?")) {
				continue;
			}
			if (itemsMaptoFrequencies.containsKey(temp)) {
				int count = itemsMaptoFrequencies.get(temp);
				itemsMaptoFrequencies.put(temp, count + 1);
			} else {
				itemsMaptoFrequencies.put(temp, 1);
			}
		}
		dataset_scanner_input.close();

		sortedItemsbyFrequencies.add("null");
		itemsMaptoFrequencies.put("null", 0);
		for (String item : itemsMaptoFrequencies.keySet()) {
			int count = itemsMaptoFrequencies.get(item);
			// System.out.println( count );
			int i = 0;
			for (String listItem : sortedItemsbyFrequencies) {
				if (itemsMaptoFrequencies.get(listItem) < count) {
					sortedItemsbyFrequencies.add(i, item);
					break;
				}
				i++;
			}
		}

		for (String listItem : sortedItemsbyFrequencies) {
			if (itemsMaptoFrequencies.get(listItem) < min_support) {
				itemstoRemove.add(listItem);
			}
		}
		for (String itemtoRemove : itemstoRemove) {
			sortedItemsbyFrequencies.remove(itemtoRemove);
		}
//
//		System.out.println(sortedItemsbyFrequencies);
//		System.out.println(itemstoRemove);

	}

	private void building_fpTree(File filename, HashMap<String, Integer> itemsMaptoFrequencies,
			Scanner dataset_scanner_input, List<String> sortedItemsbyFrequencies, ArrayList<String> itemstoRemove)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		for (String itemsforTable : sortedItemsbyFrequencies) {
			eachItemSetTreeNode.add(new FPTreeNode(itemsforTable));
		}
		dataset_scanner_input = new Scanner(filename);
		// the null node!

		fpTreeNode.item = null;
		fpTreeNode.root = true;
		// ordering frequent items transaction
		while (dataset_scanner_input.hasNextLine()) {
			String line = dataset_scanner_input.nextLine().trim();
			String[] itemSets = line.split(" ");
			ArrayList<String> transactionSortedItemsbyFrequency = new ArrayList<String>();
			for (String item_value : itemSets) {
				String item = item_value.trim();
				if (item.contains("?")) {
					continue;
				}
				if (itemstoRemove.contains(item)) {
					continue;
				}
				int index = 0;
				for (String vectorString : transactionSortedItemsbyFrequency) {

					if (itemsMaptoFrequencies.get(vectorString) < itemsMaptoFrequencies.get(item)
							|| ((itemsMaptoFrequencies.get(vectorString) == itemsMaptoFrequencies.get(item))
									&& (vectorString.compareToIgnoreCase(item) > 0 ? true : false))) {
						transactionSortedItemsbyFrequency.add(index, item);
						break;
					}
					index++;
				}
				if (!transactionSortedItemsbyFrequency.contains(item)) {
					transactionSortedItemsbyFrequency.add(item);
				}
			}

			insertIteminFPTree(transactionSortedItemsbyFrequency, fpTreeNode, eachItemSetTreeNode);
			transactionSortedItemsbyFrequency.clear();
		}

		for (FPTreeNode item : eachItemSetTreeNode) {
			int count = 0;
			FPTreeNode itemtemp = item;
			while (itemtemp.next != null) {
				itemtemp = itemtemp.next;
				count += itemtemp.frequencyOfItemInTree;
			}
			item.frequencyOfItemInTree = count;
		}
		Comparator c = new FPTreeComparitor();
		Collections.sort(eachItemSetTreeNode, c);
		Collections.reverse(eachItemSetTreeNode);
		dataset_scanner_input.close();
	}

	public class FPTreeComparitor implements Comparator<FPTreeNode> {

		@Override
		public int compare(FPTreeNode fptreeNode1, FPTreeNode fptreeNode2) {
			if (fptreeNode1.frequencyOfItemInTree > fptreeNode2.frequencyOfItemInTree) {
				return -1;
			} else if (fptreeNode1.frequencyOfItemInTree < fptreeNode2.frequencyOfItemInTree)
				return 1;
			else
				return 0;
		}

	}

	private void insertIteminFPTree(ArrayList<String> transactionSortedbyFrequencies, FPTreeNode fptreeNode,
			ArrayList<FPTreeNode> eachItemSetTreeNode) {
		// TODO Auto-generated method stub
		if (transactionSortedbyFrequencies.isEmpty()) {
			return;
		}
		String itemtoAddtotree = transactionSortedbyFrequencies.get(0);
		FPTreeNode fpTreenewNode = null;
		boolean newRootNode = false;
		for (FPTreeNode child : fptreeNode.children) {
			if (child.item.equals(itemtoAddtotree)) {
				fpTreenewNode = child;
				child.frequencyOfItemInTree++;
				newRootNode = true;
				break;
			}
		}
		if (!newRootNode) {
			fpTreenewNode = new FPTreeNode(itemtoAddtotree);
			fpTreenewNode.frequencyOfItemInTree = 1;
			fpTreenewNode.parent = fptreeNode;
			fptreeNode.children.add(fpTreenewNode);
			for (FPTreeNode fptreeNodeheaderPointer : eachItemSetTreeNode) {
				if (fptreeNodeheaderPointer.item.equals(itemtoAddtotree)) {
					while (fptreeNodeheaderPointer.next != null) {
						fptreeNodeheaderPointer = fptreeNodeheaderPointer.next;
					}
					fptreeNodeheaderPointer.next = fpTreenewNode;
				}
			}
		}
		transactionSortedbyFrequencies.remove(0);
		insertIteminFPTree(transactionSortedbyFrequencies, fpTreenewNode, eachItemSetTreeNode);
	}

	private void fpgrowthFacade(FPTreeNode fpTreeNode, int min_support,
			ArrayList<FPTreeNode> reverseSortedItemSetTreeNode) {
		// TODO Auto-generated method stub
		fpgrowthFacadeImpl(fpTreeNode, null, min_support, reverseSortedItemSetTreeNode, frequentPatterns);
	}

	private void fpgrowthFacadeImpl(FPTreeNode fpTreeNode, String present_path_base, int min_support,
			ArrayList<FPTreeNode> reverseSortedItemSetTreeNode, HashMap<String, Integer> frequentPatterns) {
		// TODO Auto-generated method stub
		for (FPTreeNode iteminTree : reverseSortedItemSetTreeNode) {
			String path = (present_path_base != null ? present_path_base : "") + (present_path_base != null ? " " : "")
					+ iteminTree.item;

			int frequencyofpresentPath = 0;
			HashMap<String, Integer> conditional_Pattern_Path = new HashMap<String, Integer>();
			while (iteminTree.next != null) {
				iteminTree = iteminTree.next;
				frequencyofpresentPath += iteminTree.frequencyOfItemInTree;
				String conditional_Pattern = null;
				FPTreeNode conditional_Item = iteminTree.parent;

				while (!conditional_Item.isRoot()) {
					conditional_Pattern = conditional_Item.item + " "
							+ (conditional_Pattern != null ? conditional_Pattern : "");
					conditional_Item = conditional_Item.parent;
				}
				if (conditional_Pattern != null) {
					conditional_Pattern_Path.put(conditional_Pattern, iteminTree.frequencyOfItemInTree);
				}
			}
			frequentPatterns.put(path, frequencyofpresentPath);
			HashMap<String, Integer> conditionalItemsMaptoFrequencies = new HashMap<String, Integer>();
			for (String conditional_Pattern : conditional_Pattern_Path.keySet()) {

				String[] conditional_Pattern_items = conditional_Pattern.split(" ");

				for (String conditional_Pattern_item : conditional_Pattern_items) {
					if (conditional_Pattern_item.trim().equals("")) {
						continue;
					}
					String item = conditional_Pattern_item.trim();
					if (conditionalItemsMaptoFrequencies.containsKey(item)) {
						int count = conditionalItemsMaptoFrequencies.get(item);
						count += conditional_Pattern_Path.get(conditional_Pattern);
						conditionalItemsMaptoFrequencies.put(item, count);
					} else {
						conditionalItemsMaptoFrequencies.put(item, conditional_Pattern_Path.get(conditional_Pattern));
					}
				}
			}

			ArrayList<FPTreeNode> conditional_headerTable = new ArrayList<FPTreeNode>();
			for (String itemsforTable : conditionalItemsMaptoFrequencies.keySet()) {
				int count = conditionalItemsMaptoFrequencies.get(itemsforTable);
				if (count < min_support) {
					continue;
				}
				FPTreeNode f = new FPTreeNode(itemsforTable);
				f.frequencyOfItemInTree = count;
				conditional_headerTable.add(f);
			}
			FPTreeNode conditional_fptree = conditional_fptree_constructor(conditional_Pattern_Path,
					conditionalItemsMaptoFrequencies, min_support, conditional_headerTable);

			Collections.sort(conditional_headerTable, new FPTreeComparitor());
			Collections.reverse(conditional_headerTable);
			if (!conditional_fptree.children.isEmpty()) {
				fpgrowthFacadeImpl(conditional_fptree, path, min_support, conditional_headerTable, frequentPatterns);
			}
		}
	}

	private void insertIteminConditionalFPTree(ArrayList<String> pattern_list, int count_of_pattern,
			FPTreeNode conditional_fptree, ArrayList<FPTreeNode> conditional_headerTable) {
		// TODO Auto-generated method stub
		if (pattern_list.isEmpty()) {
			return;
		}
		String itemtoAddtotree = pattern_list.get(0);
		FPTreeNode fpTreenewNode = null;
		boolean ifisdone = false;
		for (FPTreeNode child : conditional_fptree.children) {
			if (child.item.equals(itemtoAddtotree)) {
				fpTreenewNode = child;
				child.frequencyOfItemInTree += count_of_pattern;
				ifisdone = true;
				break;
			}
		}
		if (!ifisdone) {
			for (FPTreeNode fpTreeNode_headerPointer : conditional_headerTable) {

				if (fpTreeNode_headerPointer.item.equals(itemtoAddtotree)) {
					fpTreenewNode = new FPTreeNode(itemtoAddtotree);
					fpTreenewNode.frequencyOfItemInTree = count_of_pattern;
					fpTreenewNode.parent = conditional_fptree;
					conditional_fptree.children.add(fpTreenewNode);
					while (fpTreeNode_headerPointer.next != null) {
						fpTreeNode_headerPointer = fpTreeNode_headerPointer.next;
					}
					fpTreeNode_headerPointer.next = fpTreenewNode;
				}
			}
		}
		pattern_list.remove(0);
		insertIteminConditionalFPTree(pattern_list, count_of_pattern, fpTreenewNode, conditional_headerTable);
	}

	private FPTreeNode conditional_fptree_constructor(HashMap<String, Integer> conditionalPatternPath,
			HashMap<String, Integer> conditionalItemsMapedtoFrequency, int min_support,
			ArrayList<FPTreeNode> conditional_headerTable) {
		// TODO Auto-generated method stub
		FPTreeNode conditional_fptreeNode = new FPTreeNode("null");
		conditional_fptreeNode.item = null;
		conditional_fptreeNode.root = true;
		for (String pattern : conditionalPatternPath.keySet()) {
			ArrayList<String> pattern_list = new ArrayList<String>();

			String[] pattern_items = pattern.split(" ");

			for (String pattern_item : pattern_items) {
				String item = pattern_item.trim();
				if (item.equals("")) {
					continue;
				}
				if (conditionalItemsMapedtoFrequency.get(item) >= min_support) {
					pattern_list.add(item);
				}
			}
			insertIteminConditionalFPTree(pattern_list, conditionalPatternPath.get(pattern), conditional_fptreeNode,
					conditional_headerTable);
		}
		return conditional_fptreeNode;
	}

	public void print() {
		System.out.println("Total Number of Frequent Item sets are: " + frequentPatterns.size());
		for (String frequent_PatternSets : frequentPatterns.keySet()) {
//			if (frequent_PatternSets.split(" ").length != 1) {
			System.out.println("\t" + frequent_PatternSets + " " + frequentPatterns.get(frequent_PatternSets));
//			}
		}
	}

}
