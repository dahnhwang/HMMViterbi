
/*
 * It is an applied version of Viterbi algorithm to find POS taggers with highest probability among given word-tagger sets (result.txt)
 * 
 * */

import java.util.ArrayList;
import java.util.Iterator;

public class Viterbi {

	public BigramModel transProb = null;
	public EmissionProb emiProb = null;
	ArrayList<ArrayList<String>> observations = null;
	ArrayList<String> states = null;

	public Viterbi(ArrayList<String> states, ArrayList<ArrayList<String>> observations, BigramModel transProb,
			EmissionProb emiProb) {
		this.transProb = transProb;
		this.emiProb = emiProb;
		this.observations = observations;
		this.states = states;
	}

	public ArrayList<String> run() {
		// run Viterbi algorithm seperated by space(" ") to calculate the probability of
		// each syntactic word/POS tagger of eojeol(어절).
		Iterator itr = this.observations.iterator();
		ArrayList<String> result = new ArrayList<String>();
		while (itr.hasNext()) {
			ArrayList<String> observation = (ArrayList<String>) itr.next();
			result.add(calMaxViterbi(observation));
		}
		return result;
	}

	private String calMaxViterbi(ArrayList<String> observation) {
		// return one eojeol(combination of morphemes) with the maximum probability
		Iterator itr = observation.iterator();
		double maxProb = 0;
		String maxCombi = "";
		while (itr.hasNext()) {
			String generativeCombi = (String) itr.next();
			double generativeProb = calGenerativeProb(generativeCombi);
			if (generativeProb > maxProb) {
				maxProb = generativeProb;
				maxCombi = generativeCombi;
			}
		}
		return maxCombi + " (" + maxProb + ")";
	}

	private double calGenerativeProb(String generativeCombi) {
		// return probability of given combination of one eojeol (input : 너/NP+를/JKO)
		generativeCombi = generativeCombi.trim().split(" ")[1];
		String[] syllables = null;
		if (generativeCombi.contains("+")) {
			syllables = generativeCombi.split("\\+");
		} else {
			syllables = new String[1];
			syllables[0] = generativeCombi;
		}

		// initialize the first node
		String prevWord = syllables[0].split("\\/")[0];
		String prevState = syllables[0].split("\\/")[1];

		// get the unigram probability of the state (ex. the probability of the NP in
		// the first place)
		double prevTransProb = transProb.unigramMap.get(prevState).getValue();

		// calculate the emission probability of the word (ex. the probability of the
		// word "나" when the state is NP)
		// calculate separately if the (word given state) is not observed in the
		// train.txt
		double prevEmiProb = 0;
		if (emiProb.emissionProb.get(syllables[0]) == null) {
			double stateCount = emiProb.taggerCount.get(prevState).getCountVal();
			double wordCount = emiProb.wordCount.get(prevWord).getCountVal();
			prevEmiProb = (wordCount / emiProb.emissionProb.size()) / stateCount + 1; // Bayesian Smoothing
		} else {
			prevEmiProb = emiProb.emissionProb.get(syllables[0]).getValue();
		}
		// calculate in log scale and convert it again
		double prevNodeVal = Math.exp(Math.log(prevTransProb) + Math.log(prevEmiProb));

		// calculate from the second node to the last
		for (int i = 1; i < syllables.length; i++) {
			String nextState = syllables[i].split("\\/")[1];
			String bigram = transProb.bigram(prevState, nextState);
			double nextTransProb = 0;
			if (transProb.bigramMap.get(bigram) == null) {
				nextTransProb = (transProb.unigramMap.get(nextState).getValue())
						/ (transProb.unigramMap.get(prevState).getCountVal() + 1);
			} else {
				nextTransProb = transProb.bigramMap.get(bigram).getValue();
			}
			double nextEmiProb = 0;
			if (emiProb.emissionProb.get(syllables[i]) == null) {
				double stateCount = emiProb.taggerCount.get(prevState).getCountVal();
				double wordCount = emiProb.wordCount.get(prevWord).getCountVal();
				nextEmiProb = (wordCount / emiProb.emissionProb.size()) / stateCount + 1;
			} else {
				nextEmiProb = emiProb.emissionProb.get(syllables[i]).getValue();
			}
			double nextNodeVal = Math.exp(Math.log(prevNodeVal) + Math.log(nextTransProb) + Math.log(nextEmiProb));
			prevState = nextState;
			prevNodeVal = nextNodeVal;
		}
		return prevNodeVal;
	}

}
