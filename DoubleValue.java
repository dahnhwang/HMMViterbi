public class DoubleValue {
	/**
	 * A numerical value
	 */
	protected double value = 0;
	protected double countVal = 0;

	/**
	 * Increment and return the new count
	 */
	public double increment() {
		return ++countVal;
	}

	/**
	 * Increment by n and return the new count
	 */
	public double increment(int n) {
		countVal = countVal + n;
		return countVal;
	}

	/**
	 * Increment by n and return the new count
	 */
	public double increment(double n) {
		countVal = countVal + n;
		return countVal;
	}

	/**
	 * Decrement and return the new count
	 */
	public double decrement() {
		return --countVal;
	}

	/**
	 * Decrement by n and return the new count
	 */
	public double decrement(int n) {
		countVal = countVal - n;
		return countVal;
	}

	/**
	 * Decrement by n and return the new count
	 */
	public double decrement(double n) {
		countVal = countVal - n;
		return countVal;
	}

	/**
	 * Get the current count
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Set the current count
	 */
	public double setValue(int value) {
		this.value = value;
		return value;
	}

	/**
	 * Set the current count
	 */
	public double setValue(double value) {
		this.value = value;
		return this.value;
	}

	public double getCountVal() {
		return countVal;
	}

	public void setCountVal(double countVal) {
		this.countVal = countVal;
	}

}
