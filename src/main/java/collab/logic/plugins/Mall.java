package collab.logic.plugins;

import collab.rest.boundaries.ElementBoundary;

public class Mall {

	
	
	ElementBoundary mall;
	int numOfStores;
	
	
	public Mall(ElementBoundary mall , int numOfStores) {
		super();
		this.mall = mall;
		this.numOfStores = numOfStores;
	}
	
	
	public ElementBoundary getMall() {
		return mall;
	}
	
	public void setMall(ElementBoundary mall) {
		this.mall = mall;
	}
	
	public Integer getNumOfStores() {
		return numOfStores;
	}
	
	public void setNumOfStores(int numOfStores) {
		this.numOfStores = numOfStores;
	}
	
	
	
}
