import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EmissionProb {

	public Map<String, DoubleValue> emissionProb = null;
	public Map<String, DoubleValue> wordCount = null;
	public Map<String, DoubleValue> taggerCount = null;

	public EmissionProb() {
		this.emissionProb = new HashMap<String, DoubleValue>();
		this.wordCount = new HashMap<String, DoubleValue>();
		this.taggerCount = new HashMap<String, DoubleValue>();
	}

	public void train(ArrayList<String> words) {
		wordsMapping(words);
		calculateEmissionProb();
	}

	public void wordsMapping(ArrayList<String> words) {
		// use given wordset(word/tagger) to calculate the counts
		Iterator itr = words.iterator();
		while (itr.hasNext()) {
			String wordLine = (String) itr.next();
			String[] wordSet = (String[]) wordLine.split("\\/");
			DoubleValue wordCountVal = wordCount.get(wordSet[0]);
			if (wordCountVal == null) {
				wordCount.put(wordSet[0], new DoubleValue());
				wordCountVal = wordCount.get(wordSet[0]);
			}
			wordCountVal.increment();

			DoubleValue taggerCountVal = taggerCount.get(wordSet[1]);
			if (taggerCountVal == null) {
				taggerCount.put(wordSet[1], new DoubleValue());
				taggerCountVal = taggerCount.get(wordSet[1]);
			}
			taggerCountVal.increment();

			DoubleValue emissionValue = emissionProb.get(wordLine);
			if (emissionValue == null) {
				emissionProb.put(wordLine, new DoubleValue());
				emissionValue = emissionProb.get(wordLine);
			}
			emissionValue.increment();

		}
	}

	public void calculateEmissionProb() {
		// calculate emission probability by using wordset counts
		for (Map.Entry<String, DoubleValue> entry : emissionProb.entrySet()) {
			String wordSet = entry.getKey();
			DoubleValue value = entry.getValue();

			String tagger = wordSet.substring(wordSet.lastIndexOf("/") + 1);
			String word = wordSet.substring(0, wordSet.lastIndexOf("/") - 1);

			double statusCount = taggerCount.get(tagger).getCountVal();

			double wordPairCount = value.getCountVal();
			double emissionProb = wordPairCount / statusCount;
			// if the wordset is new to the map, i calculated differently within the
			// code (bayesian smoothing)
			value.setValue(emissionProb);
		}
	}

	public void print() {
		System.out.println("\nEmission probs:");
		for (Map.Entry<String, DoubleValue> entry : emissionProb.entrySet()) {
			// An entry in the HashMap maps a token to a DoubleValue
			String emission = entry.getKey();
			// The value for the token is in the value of the DoubleValue
			DoubleValue value = entry.getValue();
			System.out.println(emission + " : " + value.getValue());
		}

	}
}
