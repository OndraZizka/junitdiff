/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.qa.junitdiff.model;

/**
 *
 * @author jbrazdil
 */
public class Group {
	private String path;
	private String name;

	public Group(String path) {
		this.path = path;
		this.name = path;
	}

	// <editor-fold defaultstate="collapsed" desc="get/set">
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
	// </editor-fold>

	@Override
	public String toString() {
		return "GroupPrecept{" + "name=" + name + '}';
	}
}
