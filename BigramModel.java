
/** 
 * reference : https://github.com/chbrown/nlp/blob/master/src/main/java/nlp/lm/BigramModel.java
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BigramModel {

	public Map<String, DoubleValue> unigramMap = null;
	public Map<String, DoubleValue> bigramMap = null;
	public double tokenCount = 0;

	public BigramModel() {
		unigramMap = new HashMap<String, DoubleValue>();
		bigramMap = new HashMap<String, DoubleValue>();
		unigramMap.put("<S>", new DoubleValue());
		unigramMap.put("</S>", new DoubleValue());
		unigramMap.put("<UNK>", new DoubleValue());
	}

	/**
	 * Train the model on a List of sentences represented as Lists of String tokens
	 */
	public void train(ArrayList<ArrayList<String>> sentences) {
		// Accumulate unigram and bigram counts in maps
		trainSentences(sentences);
		// Compute final unigram and bigram probs from counts
		calculateProbs();
		// print();

	}

	/** Accumulate unigram and bigram counts for these sentences */
	public void trainSentences(ArrayList<ArrayList<String>> sentences) {
		for (ArrayList<String> sentence : sentences) {
			trainSentence(sentence);
		}

	}

	/** Accumulate unigram and bigram counts for this sentence */
	public void trainSentence(ArrayList<String> sentence) {
		// First count an initial start sentence token
		String prevToken = "<S>";
		DoubleValue unigramValue = unigramMap.get("<S>");
		unigramValue.increment();
		tokenCount++;
		// For each token in sentence, accumulate a unigram and bigram count
		for (String token : sentence) {
			unigramValue = unigramMap.get(token);
			// If this is the first time token is seen then count it
			// as an unkown token (<UNK>) to handle out-of-vocabulary
			// items in testing
			if (unigramValue == null) {
				// Store token in unigram map with 0 count to indicate that
				// token has been seen but not counted
				unigramMap.put(token, new DoubleValue());
				unigramValue = unigramMap.get(token);
			}
			unigramValue.increment(); // Count unigram
			tokenCount++; // Count token
			// Make bigram string
			String bigram = bigram(prevToken, token);
			DoubleValue bigramValue = bigramMap.get(bigram);
			if (bigramValue == null) {
				// If previously unseen bigram, then
				// initialize it with a value 0
				bigramValue = new DoubleValue();
				bigramMap.put(bigram, bigramValue);
			}
			// Count bigram
			bigramValue.increment();
			prevToken = token;
		}
		// Account for end of sentence unigram
		unigramValue = unigramMap.get("</S>");
		unigramValue.increment();
		tokenCount++;
		// Account for end of sentence bigram
		String bigram = bigram(prevToken, "</S>");
		DoubleValue bigramValue = bigramMap.get(bigram);
		if (bigramValue == null) {
			bigramValue = new DoubleValue();
			bigramMap.put(bigram, bigramValue);
		}
		bigramValue.increment();
	}

	/** Compute unigram and bigram probabilities from unigram and bigram counts */
	public void calculateProbs() {
		// Set unigram values to unigram probability
		for (Map.Entry<String, DoubleValue> entry : unigramMap.entrySet()) {
			// An entry in the HashMap maps a token to a DoubleValue
			String token = entry.getKey();
			// Unigram count is the current map value
			DoubleValue value = entry.getValue();
			double count = value.getCountVal();
			// Set map value to prob of unigram
			value.setValue(count / tokenCount);
		}

		// Set bigram values to conditional probability of second token given
		for (Map.Entry<String, DoubleValue> entry : bigramMap.entrySet()) {
			// An entry in the HashMap maps a token to a DoubleValue
			String bigram = entry.getKey();
			// The value for the token is in the value of the DoubleValue
			DoubleValue value = entry.getValue();
			double bigramCount = value.getCountVal();
			String token1 = bigramToken1(bigram); // Get first token of bigram
			String token2 = bigramToken2(bigram);
			// Prob is ratio of bigram count to token1 unigram count
			// get the probability of unigram of bigram
			double condProb = (bigramCount + unigramMap.get(token2).getValue())
					/ (unigramMap.get(token1).getCountVal() + 1); // apply bayesian prior smoothing
			// Set map value to conditional probability
			value.setValue(condProb);
		}

	}

	/** Return bigram string as two tokens separated by a newline */
	public String bigram(String prevToken, String token) {
		return prevToken + "\n" + token;
	}

	/** Return fist token of bigram (substring before newline) */
	public String bigramToken1(String bigram) {
		int newlinePos = bigram.indexOf("\n");
		return bigram.substring(0, newlinePos);
	}

	/** Return second token of bigram (substring after newline) */
	public String bigramToken2(String bigram) {
		int newlinePos = bigram.indexOf("\n");
		return bigram.substring(newlinePos + 1, bigram.length());
	}

	/** Print model as lists of unigram and bigram probabilities */
	public void print() {
		System.out.println("Unigram probs:");
		for (Map.Entry<String, DoubleValue> entry : unigramMap.entrySet()) {
			// An entry in the HashMap maps a token to a DoubleValue
			String token = entry.getKey();
			// The value for the token is in the value of the DoubleValue
			DoubleValue value = entry.getValue();
			System.out.println(token + " : " + value.getValue());
		}
		System.out.println("\nBigram probs:");
		for (Map.Entry<String, DoubleValue> entry : bigramMap.entrySet()) {
			// An entry in the HashMap maps a token to a DoubleValue
			String bigram = entry.getKey();
			// The value for the token is in the value of the DoubleValue
			DoubleValue value = entry.getValue();
			System.out.println(bigramToken2(bigram) + " given " + bigramToken1(bigram) + " : " + value.getValue());
		}
	}

}
