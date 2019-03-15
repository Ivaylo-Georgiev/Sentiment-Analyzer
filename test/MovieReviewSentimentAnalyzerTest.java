import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import bg.sofia.uni.fmi.mjt.sentiment.MovieReviewSentimentAnalyzer;

public class MovieReviewSentimentAnalyzerTest {
	private static final String STOPWORD = "a";
	private static final String REVIEW = "1 Narratively , a plodding mess .	";
	private static final String SECOND_REVIEW = "4 Narratively, best indie .	";
	private static final String THIRD_REVIEW = "0 Worst .	";
	private static final double DELTA = 0.001;

	@Test
	public void testIsSTOPWORDTrue() {
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()), new ByteArrayInputStream(REVIEW.getBytes()), null);
		assertTrue(mrsa.isStopWord(STOPWORD));
	}

	@Test
	public void testIsSTOPWORDFalse() {
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()), new ByteArrayInputStream(REVIEW.getBytes()), null);
		assertFalse(mrsa.isStopWord(STOPWORD + STOPWORD));
	}

	@Test
	public void testSentimentDictionarySize() {
		final int expectedSize = 3;
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()), new ByteArrayInputStream(REVIEW.getBytes()), null);
		assertEquals(expectedSize, mrsa.getSentimentDictionarySize());
	}

	@Test
	public void testgetMostNegativeWords() {
		final int limit = 3;
		final String first = "plodding";
		final String second = "mess";
		final String third = "narratively";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		List<String> words = new ArrayList<String>();
		words.add(first);
		words.add(second);
		words.add(third);
		assertEquals(words, mrsa.getMostNegativeWords(limit));
	}
	
	@Test
	public void testgetMostPositiveWords() {
		final int limit = 2;
		final String first = "indie";
		final String second = "best";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		List<String> words = new ArrayList<String>();
		words.add(first);
		words.add(second);
		assertEquals(words, mrsa.getMostPositiveWords(limit));
	}
	
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetMostFrequentWordsException() {
		final int limit = -50;
		final String word = "narratively";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		List<String> words = new ArrayList<String>();
		words.add(word);
		mrsa.getMostFrequentWords(limit);
	}
	
	@Test
	public void testGetReview() {
		final String expected = "Narratively , a plodding mess .	";
		final double sentiment = 1;
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(expected, mrsa.getReview(sentiment));
	}
	
	@Test
	public void testGetReviewUnknown() {
		final double sentiment = 50;
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertNull(mrsa.getReview(sentiment));
	}
	
	@Test
	public void testGetWordSentimentUnknown() {
		final double sentiment = -1;
		final String unknown = "unknown";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getWordSentiment(unknown), DELTA);
	}
	
	@Test
	public void testReviewSentimentAsNamePositive() {
		final String rev = "best";
		final String sentiment = "positive";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getReviewSentimentAsName(rev));
	}
	
	@Test
	public void testReviewSentimentAsNameSomewhatPositive() {
		final String rev = "best plodding";
		final String sentiment = "somewhat positive";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getReviewSentimentAsName(rev));
	}
	
	@Test
	public void testReviewSentimentAsNameNeutral() {
		final String rev = "best plodding mess";
		final String sentiment = "neutral";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getReviewSentimentAsName(rev));
	}
	
	@Test
	public void testReviewSentimentAsNameSomewhatNegative() {
		final String rev = "mess";
		final String sentiment = "somewhat negative";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getReviewSentimentAsName(rev));
	}
	
	@Test
	public void testReviewSentimentAsNameNegative() {
		final String rev = "worst";
		final String sentiment = "negative";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + THIRD_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getReviewSentimentAsName(rev));
	}
	
	@Test
	public void testReviewSentimentAsNameUnknown() {
		final String rev = "";
		final String sentiment = "unknown";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		assertEquals(sentiment, mrsa.getReviewSentimentAsName(rev));
	}
	
	@Test
	public void testGetMostFrequentWords() {
		final int limit = 1;
		final String word = "narratively";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		List<String> words = new ArrayList<String>();
		words.add(word);
		assertEquals(words, mrsa.getMostFrequentWords(limit));
	}
	
	@Test
	public void testGetWordSentiment() {
		final double sentiment = 2.5;
		final String word = "narratively";
		
		MovieReviewSentimentAnalyzer mrsa = new MovieReviewSentimentAnalyzer(
				new ByteArrayInputStream(STOPWORD.getBytes()),
				new ByteArrayInputStream((REVIEW + System.lineSeparator() + SECOND_REVIEW).getBytes()), null);
		List<String> words = new ArrayList<String>();
		words.add(word);
		assertEquals(sentiment, mrsa.getWordSentiment(word), DELTA);
	}
}
