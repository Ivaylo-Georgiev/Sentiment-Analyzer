package bg.sofia.uni.fmi.mjt.sentiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.sentiment.interfaces.SentimentAnalyzer;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {
	private static final String DELIMITER = "\\W+";
	private static final String WORD = "[a-z0-9]+";
	
	final static int NEGATIVE = 0;
	final static String NEG = "negative";
	final static int SOMEWHAT_NEGATIVE = 1;
	final static String SNEG = "somewhat negative";
	final static int NEUTRAL = 2;
	final static String NEU = "neutral";
	final static int SOMEWHAT_POSITIVE = 3;
	final static String SPOS = "somewhat positive";
	final static int POSITIVE = 4;
	final static String POS = "positive";
	final static String UNKNOWN = "unknown";

	private Set<String> stopwords;
	private Map<String, List<Integer>> wordsSentiment;
	private List<String> reviews;
	private OutputStream reviewsOutput;

	private void extractWords(String review) {
		int sentiment = Character.getNumericValue(review.charAt(0));
		String[] words = review.substring(1, review.length()).split(DELIMITER);
		List<String> wordList = new ArrayList<String>(Arrays.asList(words));
		Pattern wordRegex = Pattern.compile(WORD);

		for (String word : wordList) {
			word = word.toLowerCase();
			if (stopwords.contains(word)) {
				continue;
			}

			Matcher wordMatcher = wordRegex.matcher(word);

			if (wordMatcher.matches()) {
				List<Integer> countSentimentSum = new ArrayList<Integer>();
				if (wordsSentiment.containsKey(word)) {
					countSentimentSum.add(wordsSentiment.get(word).get(0) + 1);
					countSentimentSum.add(wordsSentiment.get(word).get(1) + sentiment);
				} else {
					countSentimentSum.add(1);
					countSentimentSum.add(sentiment);
				}
				wordsSentiment.put(word, countSentimentSum);
			}
		}
	}

	public MovieReviewSentimentAnalyzer(InputStream stopwordsInput, InputStream reviewsInput,
			OutputStream reviewsOutput) {

		stopwords = new HashSet<String>();
		wordsSentiment = new HashMap<String, List<Integer>>();
		reviews = new ArrayList<String>();

		// stop words
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stopwordsInput, "UTF-8"))) {
			String line = br.readLine();
			while (line != null) {
				stopwords.add(line);
				line = br.readLine();
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding exception during reading from stopwords stream.");
		} catch (IOException e) {
			System.out.println("IO exception while reading from stopwords stream");
		}

		// reviews
		try (BufferedReader br = new BufferedReader(new InputStreamReader(reviewsInput, "UTF-8"))) {
			String review = br.readLine();
			while (review != null) {
				extractWords(review);
				reviews.add(review);
				review = br.readLine();
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported encoding exception while reading from reviews stream.");
		} catch (IOException e) {
			System.out.println("IO exception while reading from reviews stream");
		}

		this.reviewsOutput = reviewsOutput;

	}

	@Override
	public double getReviewSentiment(String review) {
		if (review == null) {
			return -1;
		}
		
		String[] words = review.split(DELIMITER);
		int recognisedWords = 0;
		double sum = 0;
		boolean recognised = false;

		for (int i = 0; i < words.length; ++i) {
			if (wordsSentiment.containsKey(words[i])) {
				++recognisedWords;
				sum += getWordSentiment(words[i]);
				recognised = true;
			}
		}

		if (recognised) {
			return sum / recognisedWords;
		} else {
			return -1;
		}
	}

	@Override
	public String getReviewSentimentAsName(String review) {
		switch ((int) Math.round(getReviewSentiment(review))) {
			case NEGATIVE:
				return NEG;
			case SOMEWHAT_NEGATIVE:
				return SNEG;
			case NEUTRAL:
				return NEU;
			case SOMEWHAT_POSITIVE:
				return SPOS;
			case POSITIVE:
				return POS;
			default:
				return UNKNOWN;
		}
		
	}

	@Override
	public double getWordSentiment(String word) {
		word = word.toLowerCase();
		if (wordsSentiment.containsKey(word)) {
			return (double) wordsSentiment.get(word).get(1) / wordsSentiment.get(word).get(0);
		} else {
			return -1;
		}
	}

	@Override
	public String getReview(double sentimentValue) {
		final int offset = 2;
		
		for (String review : reviews) {
			if (Double.compare(getReviewSentiment(review), sentimentValue) == 0) {
				return review.substring(offset);
			}
		}
		return null;
	}

	@Override
	public List<String> getMostFrequentWords(int n) throws IllegalArgumentException {
		if (n < 0) {
			throw new IllegalArgumentException("n must be a positive integer.");
		}
		
		return wordsSentiment.entrySet()
				.stream()
				.sorted((occur1, occur2) -> Integer.compare(occur2.getValue().get(0), occur1.getValue().get(0)))
				.limit(n)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getMostPositiveWords(int n) {
		return wordsSentiment.entrySet().stream()
				.sorted((occur1, occur2) -> Double.compare(getWordSentiment(occur2.getKey()),
						getWordSentiment(occur1.getKey())))
				.limit(n).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	@Override
	public List<String> getMostNegativeWords(int n) {
		return wordsSentiment.entrySet().stream()
				.sorted((occur1, occur2) -> Double.compare(getWordSentiment(occur1.getKey()),
						getWordSentiment(occur2.getKey())))
				.limit(n).map(Map.Entry::getKey).collect(Collectors.toList());
	}

	@Override
	public void appendReview(String review, int sentimentValue) {
		String withSentiment = Integer.toString(sentimentValue) + ' ' + review + System.lineSeparator();
		extractWords(withSentiment);

		try {
			reviewsOutput.write(withSentiment.getBytes());
			reviewsOutput.flush();
		} catch (IOException e) {
			System.out.println("IO exception");
		}
	}

	@Override
	public int getSentimentDictionarySize() {
		return wordsSentiment.size();
	}

	@Override
	public boolean isStopWord(String word) {
		if (stopwords.contains(word)) {
			return true;
		}
		return false;
	}
}
