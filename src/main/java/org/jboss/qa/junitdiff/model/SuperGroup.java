
package org.jboss.qa.junitdiff.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Super-group - to cluster multiple groups.
 * Used to bind multiple groups (collumns) visually.
 *
 * @author Ondrej Zizka
 */
public class SuperGroup {

	private final String name;
	private String color = null;

	private List<GroupPrecept> groups = new ArrayList();



	public SuperGroup(String name) {
		this.name = name;
	}



	// <editor-fold defaultstate="collapsed" desc="get/set">
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	// </editor-fold>


}// class
