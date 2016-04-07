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
	
	public void setIdtoIdxUser(LinkedHashMap<Integer,Integer> ididxuser){
		IdtoIdx_user = ididxuser;
	}
	
	public void setIdtoIdxItem(LinkedHashMap<Integer,Integer> ididxitem){
		IdtoIdx_items = ididxitem;
	}
	
	
	@Override
	public double rank(int user, int item) {
		int idxuser = IdtoIdx_user.get(user);
		int idxitem = IdtoIdx_items.get(item);
		double[]v1 = centroides.getCol(idxuser);
		double[] v2= tagsItemsMatrix.getCol(idxitem);
		return Utils.coseno(v1,v2);
	}
	
	public ContentsRocchio(){
		
	}
	public ContentsRocchio(String fileofContents,String fileOfUsers){
		numUser = Utils.getSizeOfFile(fileOfUsers);
		numItem = Utils.getSizeOfFile(fileofContents);
		numTags = 4;
		IdtoIdx_items = new LinkedHashMap<>();
		IdtoIdx_user = new LinkedHashMap<>();
		loadContents(fileofContents);
		loadUserMatrix(fileOfUsers);
		_calculateCentroides();
	}
	
	private void _calculateCentroides(){
		centroides = Matrix.producto(tagsItemsMatrix, matriz.transpose());
		centroides = centroides.RocchiNormalize(matriz.transpose());
		
	}
	
	public final void loadContents(String fileOfContents){
		tagsItemsMatrix = new Matrix(numTags, numItem);
 		super.CargarMatriz(fileOfContents, tagsItemsMatrix, FilterCallableItem.CODE ,IdtoIdx_items,true);
	}
	
	public final void loadUserMatrix(String fileOfUsers){
		matriz = new Matrix(numUser,numItem);
		super.CargarMatriz(fileOfUsers, matriz, FilterCallableUser.CODE, IdtoIdx_user,false);
	}
	
	
	
}
