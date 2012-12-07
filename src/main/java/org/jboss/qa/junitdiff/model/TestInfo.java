
package org.jboss.qa.junitdiff.model;

/**
 * Test info - name, result, reason.
 *
 * @author Ondrej Zizka
 */
public class TestInfo {

		public enum Result {
				OK, FAIL, ERROR, SKIPPED;
		}



		private String classname;
		private String name;
		private Result result;
		private String time;
		private Failure failure;
		private Group group;


		public TestInfo(String classname, String name, Result result, String time) {
				this.classname = classname;
				this.name = name;
				this.result = result;
				this.time = time;
		}



		public String getFullName() {
				return this.classname + "." + this.name;
		}

		
		// <editor-fold defaultstate="collapsed" desc="Getters/setters">
		public String getClassname() {				return classname;		}
		public void setClassname(String classname) {				this.classname = classname;		}
		public String getName() {				return name;		}
		public void setName(String name) {				this.name = name;		}
		public Result getResult() {				return result;		}
		public void setResult(Result result) {				this.result = result;		}
		public String getTime() {				return time;		}
		public void setTime(String time) {				this.time = time;		}
		public Failure getFailure() {				return failure;		}
		public void setFailure(Failure failure) {				this.failure = failure;		}

		public Group getGroup() {
				return group;
		}

		public void setGroup(Group group) {
				this.group = group;
		}
		// </editor-fold>

		@Override
		public String toString() {
				return "TestInfo{" + "classname=" + classname + "name=" + name + "result=" + result + "time=" + time + "failure=" + failure + '}';
		}


}// class
