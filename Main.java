import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws IOException {

		// define variables
		FilePath pathStr = new FilePath();
		BigramModel transitionProb = null;
		ArrayList<ArrayList<String>> tokenList = null;
		ArrayList<String> words = new ArrayList<String>();
		EmissionProb emissionProb = null;
		ArrayList<String> states = null;
		ArrayList<ArrayList<ArrayList<String>>> allSentences = new ArrayList<ArrayList<ArrayList<String>>>();

		// calculate HMM (transition and emission probability)
		transitionProb = calTransitionProb(transitionProb, pathStr, tokenList, words);
		states = makeStatesArray(transitionProb, states);
		emissionProb = calEmissionProb(emissionProb, words);

		// read source input sentences and save them to list [안녕하세요, 너를 사랑해!, 우리집에 왜
		// 왔니?]
		allSentences = readSourceInputFile(allSentences, pathStr);

		// run Viterbi algorithm to get the one with the highest probability
		// print the result to HMM_tagger/bin/output.txt
		Iterator itr = allSentences.iterator();
		ArrayList<String> result = new ArrayList<String>();
		fileWriter fw = new fileWriter();
		File file = new File(pathStr.getPathStr() + "output.txt");
		while (itr.hasNext()) {
			ArrayList<ArrayList<String>> observation = (ArrayList<ArrayList<String>>) itr.next();
			Viterbi v = new Viterbi(states, observation, transitionProb, emissionProb);
			result = v.run();
			fw.fileWrite(file, "=========================================================");
			Iterator itr2 = result.iterator();
			while (itr2.hasNext()) {
				fw.fileWrite(file, (String) itr2.next());
			}
			fw.fileWrite(file, "=========================================================");
		}
	}

	public static BigramModel calTransitionProb(BigramModel transitionProb, FilePath pathStr,
			ArrayList<ArrayList<String>> tokenList, ArrayList<String> words) throws IOException {
		// calculate transition probability by using all the unigrams and bigrams from
		// the train.txt
		// IMPORTANT : the first line of the "train.txt" file has to be empty
		File file = new File(pathStr.getPathStr() + "train.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "euc-kr"));
		String train = null;
		ArrayList<String> tokens = null;
		tokenList = new ArrayList<ArrayList<String>>();
		transitionProb = new BigramModel();

		while ((train = br.readLine()) != null) {
			String lastSignal = "";
			if (!train.equals("")) {
				train = train.split("	")[1];
				int lastIndex = 0;

				if (train.contains("+")) {
					if (train.contains("++")) {
						train = train.replace("++", "+더하기기호");
					}
					if (train.contains("//")) {
						train = train.replace("//", "슬래쉬기호/");
					}
					if (train.contains("+/SW")) {
						train = train.replace("+/SW", "더하기기호/SW");
					}
					for (int i = 0; i < train.split("\\+").length; i++) {
						String trainModified = train.split("\\+")[i];
						lastIndex = trainModified.lastIndexOf("/");
						tokens.add(trainModified.substring(lastIndex + 1));
						if (trainModified.contains("더하기기호")) {
							trainModified = trainModified.replace("더하기기호", "+");
						}
						if (trainModified.contains("슬래쉬기호")) {
							trainModified = trainModified.replace("슬래쉬기호", "/");
						}
						words.add(trainModified);
					}
					lastSignal = "/" + train.split("\\+")[train.split("\\+").length - 1].substring(lastIndex + 1);
				} else {
					if (train.contains("++")) {
						train = train.replace("++", "+더하기기호");
					}
					if (train.contains("//")) {
						train = train.replace("//", "슬래쉬기호/");
					}
					if (train.contains("+/SW")) {
						train = train.replace("+/SW", "더하기기호/SW");
					}
					lastIndex = train.lastIndexOf("/");
					lastSignal = "/" + train.substring(lastIndex + 1);
					tokens.add(train.substring(lastIndex + 1));
					if (train.contains("+")) {
						train = train.replace("더하기기호", "+");
					}
					if (train.contains("/")) {
						train = train.replace("슬래쉬기호", "/");
					}
					words.add(train);
				}
				if (lastSignal.equals("/SF")) {
					tokenList.add(tokens);
				}
			} else {
				tokens = new ArrayList<String>();
			}

		}
		transitionProb.train(tokenList);
		return transitionProb;

	}

	private static ArrayList<String> makeStatesArray(BigramModel transProb, ArrayList<String> states) {
		// make distinct states(POS taggers) from the given train.txt file to a list
		Set set = transProb.unigramMap.keySet();
		states = new ArrayList<String>();
		Iterator itr = set.iterator();
		while (itr.hasNext()) {
			String status = (String) itr.next();
			states.add(status);
		}
		return states;
	}

	public static EmissionProb calEmissionProb(EmissionProb emissionProb, ArrayList<String> words) {
		// calculate emission probability by using all the words in the train.txt
		emissionProb = new EmissionProb();
		emissionProb.train(words);
		return emissionProb;
	}

	public static ArrayList<ArrayList<ArrayList<String>>> readResultTxtFile(
			ArrayList<ArrayList<ArrayList<String>>> allSentences, ArrayList<String> inputList, FilePath pathStr)
			throws IOException {
		Iterator itr = inputList.iterator();
		while (itr.hasNext()) {
			ArrayList<ArrayList<String>> sentenceList = new ArrayList<ArrayList<String>>();
			String line = (String) itr.next(); // 너를 사랑해!
			for (int i = 0; i < line.split(" ").length; i++) { // 한 문장을 어절단위로 나눠진 상태로 파일을 탐색하며 리스트에 담는다.
				ArrayList<String> syllableList = null;
				String syllable = line.split(" ")[i];
				syllableList = searchResultTxtFile(syllable, syllableList, pathStr);
				sentenceList.add(syllableList);
			}
			allSentences.add(sentenceList);
		}
		return allSentences;
	}

	public static ArrayList<ArrayList<ArrayList<String>>> readSourceInputFile(
			ArrayList<ArrayList<ArrayList<String>>> allSentences, FilePath pathStr) throws IOException {
		// save all the sentences and its POS tagging results to a list
		ArrayList<String> inputList = null;
		inputList = readInputTxtFile(inputList, pathStr);
		return allSentences = readResultTxtFile(allSentences, inputList, pathStr);
	}

	public static ArrayList<String> readInputTxtFile(ArrayList<String> inputList, FilePath pathStr) throws IOException {
		// read from input.txt file
		inputList = new ArrayList<String>();
		File file = new File(pathStr.getPathStr() + "input.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "euc-kr"));

		String resultStr = null;
		while ((resultStr = br.readLine()) != null) {
			inputList.add(resultStr); // 너를 사랑해!
			// System.out.println(resultStr);
		}
		return inputList;
	}

	public static ArrayList<String> searchResultTxtFile(String syllable, ArrayList<String> syllableList,
			FilePath pathStr) throws IOException {
		// read from result.txt file
		syllableList = new ArrayList<String>();
		File file = new File(pathStr.getPathStr() + "result.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "euc-kr"));
		String resultStr = null;
		boolean isSyllable = false;
		while ((resultStr = br.readLine()) != null) {
			if (resultStr.equals(syllable)) {
				isSyllable = true;
				continue;
			}

			if (!resultStr.equals("") && isSyllable == true) {

				if (isNumeric(resultStr)) {
					syllableList.add(resultStr);
				} else {
					isSyllable = false;
				}
			}

		}
		return syllableList;

	}

	public static void printSentenceList(ArrayList<ArrayList<ArrayList<String>>> allSentences) {
		// check list of allSentences
		Iterator itr = allSentences.iterator();
		while (itr.hasNext()) {
			ArrayList<String> sentenceList = (ArrayList<String>) itr.next(); // 너를 사랑해!
			Iterator itr2 = sentenceList.iterator();
			System.out.println(sentenceList); // 문장단위로 어절 단위로 나누어진 list임(비터비 최종 분석대상이자 input)
		}
	}

	public static boolean isNumeric(String str) {
		// check whether the parameter is number or not
		try {
			Integer.parseInt(str.trim().substring(0, 1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
