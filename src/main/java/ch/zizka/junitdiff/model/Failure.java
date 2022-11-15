
package ch.zizka.junitdiff.model;

/**
 *
 * @author Ondrej Zizka
 */
public class Failure {

		private String message;
		private String type;
		private String trace;


		public Failure(String message, String type, String trace) {
				this.message = message;
				this.type = type;
				this.trace = trace;
		}

		


		// <editor-fold defaultstate="collapsed" desc="get / set">
		public String getMessage() {
				return message;
		}

		public void setMessage(String message) {
				this.message = message;
		}

		public String getType() {
				return type;
		}

		public void setType(String type) {
				this.type = type;
		}

		public String getTrace() {
				return trace;
		}

		public void setTrace(String trace) {
				this.trace = trace;
		}


		// </editor-fold>

		

}// class
