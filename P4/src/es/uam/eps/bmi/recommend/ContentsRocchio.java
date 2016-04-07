/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uam.eps.bmi.recommend;

import es.uam.eps.bmi.search.Utils;
import es.uam.eps.bmi.search.ranking.graph.Matrix;
import java.util.LinkedHashMap;

/**
 *
 * @author victo
 */
public class ContentsRocchio extends RecommenderAbs {
	
	protected Matrix tagsItemsMatrix;
	protected Matrix centroides;
	
	protected int numTags;
	
	LinkedHashMap<Integer,Integer> IdtoIdx_user;
	LinkedHashMap<Integer,Integer> IdtoIdx_items;
	
	
	
	@Override
	public double rank(int user, int item) {
		int idxuser = IdtoIdx_user.get(user);
		int idxitem = IdtoIdx_items.get(item);
		
		return Utils.coseno(matriz.getRow(user),matriz.getCol(item));
	}
	
	public ContentsRocchio(String fileofContents,String fileOfUsers){
		numUser = Utils.getSizeOfFile(fileOfUsers);
		numItem = Utils.getSizeOfFile(fileofContents);
		numTags = 20;
		loadContents(fileofContents);
		loadUserMatrix(fileOfUsers);
		_calculateCentroides();
	}
	
	private void _calculateCentroides(){
		Matrix.producto(tagsItemsMatrix, matriz.transpose());
		
	}
	
	public final void loadContents(String fileOfContents){
		tagsItemsMatrix = new Matrix(numTags, numItem);
		super.CargarMatriz(fileOfContents, tagsItemsMatrix, FilterCallableItem.CODE ,IdtoIdx_items);
	}
	
	public final void loadUserMatrix(String fileOfUsers){
		matriz = new Matrix(numUser,numItem);
		super.CargarMatriz(fileOfUsers, matriz, FilterCallableUser.CODE, IdtoIdx_user);
	}
	
	
	
}
