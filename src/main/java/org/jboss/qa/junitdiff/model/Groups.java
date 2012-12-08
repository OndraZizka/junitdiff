package org.jboss.qa.junitdiff.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *  Group of groups, used for logical separation of test runs.
 *  Can serve e.g. for coloring of columns in HTML output.
 *
 *  @author jbrazdil
 */
public class Groups {
	private Map<String,Group> groups = new HashMap<String,Group>();
	private int id=1;


	public IGroup getGroup(String path){
		Group group;
		if(groups.containsKey(path)){
			group = groups.get(path);
		}else{
			group = new Group(path, id);
			id++;
			groups.put(path, group);
		}

		return group;
	}

	/**
	 * Set the differing parts of group paths as name.
	 *   {abcfoo1234, abcbarbar1234} => {foo, barbar}
	 */
	public void shortenNames(){
		String[] allPaths = new String[groups.size()];
		String[] allPathsRev = new String[groups.size()];
		int i=0;
		for(IGroup g : groups.values()){
			allPaths[i]=g.getPath();
			allPathsRev[i]=StringUtils.reverse(g.getPath());
			i++;
		}

		// Get the common prefix and sufix
		String commonPrefix = StringUtils.getCommonPrefix(allPaths); // abc
		String commonSuffix = StringUtils.getCommonPrefix(allPathsRev); // 1234

		// Get the common prefix and sufix lengths
		int prefixLength = commonPrefix.length(); // 3
		int suffixLength = commonSuffix.length(); // 4

		// Cut off the common prefix and sufix
		if( prefixLength + suffixLength == 0 )  return;

        for(Group g : groups.values()){
            int nameLength = g.getPath().length(); // abcfoo1234 = 10; abcbarbar1234 = 13
            //  abc|foo|1234      ->  foo          3             10  - 4 = 6
            //  abc|bar bar|1234  ->  barbar       3             13  - 4 = 9
            //  012|345|678|9012
            int end = nameLength - suffixLength;
            if(prefixLength >= end) end = nameLength;
            g.name = g.path.substring(prefixLength, end);
        }
	}

    
	private static class Group implements IGroup {

		private String path;
		private String name;
		private int id;

		Group(String path, int id) {
			this.path = path;
			this.name = path;
			this.id = id;
		}

		// <editor-fold defaultstate="collapsed" desc="getters">
		public String getName() {
			return name;
		}

		public String getPath() {
			return path;
		}

		public Integer getId() {
			return id;
		}
		// </editor-fold>

		@Override
		public String toString() {
			return "Groups{" + "name=" + name + '}';
		}
	}
}
