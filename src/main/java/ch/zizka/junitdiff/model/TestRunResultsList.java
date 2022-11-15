
package ch.zizka.junitdiff.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A list with tests results data - name, result (OK | FAIL | ERROR), reason.
 *
 * @author Ondrej Zizka
 */
public class TestRunResultsList implements Serializable {


		/**
		 *  Where did this results collection come from (e.g. a filename).
		 */
		private String origin;

		private String group;

		private List<TestRunInfo> testResults = new ArrayList<TestRunInfo>();





		public TestRunResultsList(String origin) {
				this.origin = origin;
		}
		
		public TestRunResultsList() {
		}

		public TestRunResultsList( List<TestRunInfo> testResults ) {
				this.testResults = testResults;
		}




		// <editor-fold defaultstate="collapsed" desc="List delegates">
		public boolean isEmpty() {
				return testResults.isEmpty();
		}

		public TestRunInfo get(int index) {
				return testResults.get(index);
		}

		public boolean contains(Object o) {
				return testResults.contains(o);
		}

		public boolean addAll(Collection<? extends TestRunInfo> c) {
				return testResults.addAll(c);
		}

		public boolean add(TestRunInfo e) {
				return testResults.add(e);
		}// </editor-fold>




		/**
		 * Concatenates the given list of other instances.
		 */
		public static TestRunResultsList fromList(List<TestRunResultsList> trls) {

				// Count the total size...
				int totalTests = 0;
				for (TestRunResultsList trl : trls) {
						totalTests += trl.testResults.size();
				}

				// Concatenate multiple report files to one.
				List<TestRunInfo> results = new ArrayList<TestRunInfo>( totalTests );
				for (TestRunResultsList trl : trls) {
						results.addAll( trl.getTestResults() );
				}

				return new TestRunResultsList( results );
				
		}



		@Override
		public String toString() {
				return "TestResultsList{ [" + testResults.size() + "], origin: " + origin + '}';
		}





		// <editor-fold defaultstate="collapsed" desc="get / set">
		public List<TestRunInfo> getTestResults() {
				return testResults;
		}

		public String getOrigin() {
				return origin;
		}

		public void setOrigin(String origin) {
				this.origin = origin;
		}

		public String getGroup() {
				return group;
		}

		public void setGroup(String group) {
				this.group = group;
		}
		// </editor-fold>



}// class
