package com.mobiquityinc.packer.solver;

import java.util.ArrayList;
import java.util.List;

import com.mobiquityinc.packer.model.Item;
import com.mobiquityinc.packer.model.Solution;

public class DynamicSolver implements ISolver {

	@Override
	public Solution solve(int capacity, List<Item> items) {

		if (!onlyIntegerWeigths(items)) {
			capacity *= 100;
			items.stream().forEach(i -> i.setWeight(i.getWeight() * 100));
		}

		items.sort( (i1,i2) -> i1.getWeight().compareTo(i2.getWeight()) );

		Item[] itemsArray = new Item[items.size()];
		itemsArray = items.toArray(itemsArray);

		
		int N = items.size();
		int[][] matrix = new int[N + 1][capacity + 1];

		for (int i = 0; i <= capacity; i++) {
			matrix[0][i] = 0;
		}
		
		for (int i = 1; i <= N; i++) {
			for (int j = 0; j <= capacity; j++) {
				if (itemsArray[i - 1].getWeight() > j) {
					matrix[i][j] = matrix[i - 1][j];
				} else {
					matrix[i][j] = Math.max(
						matrix[i - 1][j],
						matrix[i - 1][j - itemsArray[i - 1].getWeight().intValue()] + itemsArray[i - 1].getValue()
					);
				}
			}
		}

		
		int res = matrix[N][capacity];
		int w = capacity;
		List<Item> solutionItems = new ArrayList<>();

		for (int i = N; i > 0 && res > 0; i--) {
			if (res != matrix[i - 1][w]) {
				solutionItems.add(itemsArray[i - 1]);
				res -= itemsArray[i - 1].getValue();
				w -= itemsArray[i - 1].getWeight();
			}
		}
		
		solutionItems.sort( (i1,i2) -> i1.getIndex().compareTo(i2.getIndex()) );

		return new Solution(solutionItems, matrix[N][capacity]);
	}

	public boolean onlyIntegerWeigths(List<Item> items) {

		boolean allIntegers = true;
		for (Item i : items) {
			if (i.getWeight() != Math.floor(i.getWeight())) {
				allIntegers = false;
				break;
			}
		}

		return allIntegers;
	}
}
