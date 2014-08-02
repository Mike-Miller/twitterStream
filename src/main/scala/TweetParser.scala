package twitterStream
import twitter4j._
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

case class TweetData(hashtags: Array[String], domains: Array[String], emojis: List[String])

trait TweetParserComponent {
  def getTweetData(tweet: twitter4j.Status): TweetData
  def getEmoji(tweet: String): List[String]
  def getHashtags(tweet: twitter4j.Status): Array[String]
  def getDomains(tweet:twitter4j.Status):Array[String]
}

trait TweetParser extends TweetParserComponent {
  val codepoints = ConfigFactory.load.getStringList("emoji.codepoint")
  val emojiList = codepoints.toList.map(e => new String(e.split('-').flatMap(c => Character.toChars(Integer.parseInt(c,16)))))

  def getTweetData(tweet: twitter4j.Status) = TweetData(getHashtags(tweet), getDomains(tweet), getEmoji(tweet.getText))
  def getEmoji(tweet: String) = emojiList.filter(e => tweet.contains(e))
  def getHashtags(tweet: twitter4j.Status) = tweet.getHashtagEntities.map(_.getText)
  def getDomains(tweet:twitter4j.Status) = tweet.getURLEntities.map(_.getDisplayURL.split('/').head)
}
