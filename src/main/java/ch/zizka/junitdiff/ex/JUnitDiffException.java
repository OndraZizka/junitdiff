
package ch.zizka.junitdiff.ex;

import java.util.List;

/**
 *
 * @author Ondrej Zizka
 */
public class JUnitDiffException extends Exception {

		private List<Exception> errors;

		public JUnitDiffException(Throwable cause) {
				super(cause);
		}

		public JUnitDiffException(String message, Throwable cause) {
				super(message, cause);
		}

		public JUnitDiffException(String message) {
				super(message);
		}

		public JUnitDiffException() {
		}

		public JUnitDiffException(List<Exception> errors) {
		}

		public JUnitDiffException(String string, List< ? extends Exception> errors) {
				this(string);
		}

		public List<Exception> getErrors() {
				return errors;
		}


}// class
