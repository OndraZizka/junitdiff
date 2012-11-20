
package org.jboss.qa.junitdiff.model;

/**
 *  Information about group - built from the app's params.
 *
 *  @author Ondrej Zizka
 */
public class GroupPrecept {

	private String name;
	private String path;
	private boolean border;

	private SuperGroup supGroup;


	public GroupPrecept(String path) {
		this.path = path;
	}




	// <editor-fold defaultstate="collapsed" desc="get/set">
	public boolean isBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public SuperGroup getSupGroup() {
		return supGroup;
	}

	public void setSupGroup(SuperGroup supGroup) {
		this.supGroup = supGroup;
	}

	// </editor-fold>
	
	@Override
	public String toString() {
		return "GroupPrecept{" + "name=" + name + '}';
	}



}// class
