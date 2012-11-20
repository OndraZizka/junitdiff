
package org.jboss.qa.junitdiff.model;

import java.util.*;
import org.jboss.qa.junitdiff.model.TestInfo;

/**
 * One test with multiple test infos - one for each "column" (test run)
 *
 * @author Ondrej Zizka
 */
public class AggregatedTestInfo {

		private String className;
		private String name;

		private List<TestInfo> testInfos = new ArrayList();



		public AggregatedTestInfo(String className, String name) {
				this.name = name;
				this.className = className;
		}

		public AggregatedTestInfo(String name, List<TestInfo> testInfos) {
				this.name = name;
				this.testInfos = testInfos;
		}

		public AggregatedTestInfo( TestInfo test ) {
				this.name = test.getName();
				this.className = test.getClassname();
		}



		// <editor-fold defaultstate="collapsed" desc="List overrides">
		public int size() {
				return testInfos.size();
		}

		public boolean isEmpty() {
				return testInfos.isEmpty();
		}

		public TestInfo get(int index) {
				return testInfos.get(index);
		}

		public boolean addAll(Collection<? extends TestInfo> c) {
				return testInfos.addAll(c);
		}

		public boolean add(TestInfo e) {
				return testInfos.add(e);
		}// </editor-fold>

		


		public String getClassName() {				return className;		}
		public void setClassName(String className) {				this.className = className;		}
		public String getName() {				return name;		}
		public void setName(String name) {				this.name = name;		}
		

		public List<TestInfo> getTestInfos() {				return Collections.unmodifiableList(testInfos);		}

		public String getFullName() {
				return this.getClassName() + "." + this.getName();
		}



}// class
