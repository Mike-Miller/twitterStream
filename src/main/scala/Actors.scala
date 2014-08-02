package twitterStream

import twitter4j._
import akka.actor._
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait Messages
case object Display extends Messages
case class Work(tweet: twitter4j.Status) extends Messages
case class Accumulate(data: TweetData) extends Messages

class Master(accumulator: ActorRef) extends Actor {
  val workerRouter = context.actorOf(Props(new Worker(accumulator)).withRouter(RoundRobinRouter(4)), name = "workerRouter")
  def receive = { case Work(tweet) => workerRouter ! Work(tweet) }
}

class Worker(accumulator: ActorRef) extends Actor with TweetParser {
  def receive = { case Work(tweet) => accumulator ! Accumulate(getTweetData(tweet)) }
}

class Accumulator extends Actor {
  private val counts = scala.collection.mutable.Map[String,Int]("tweets" -> 0, "url" -> 0, "picture" -> 0, "emoji" -> 0)
  private val hashtagStorage = new LocalStorage {}
  private val domainStorage = new LocalStorage {}
  private val emojiStorage = new LocalStorage {}
  private val startTime = System.currentTimeMillis

  def receive = {
    case Accumulate(tweetData) => {
      counts("tweets") += 1
      if (!tweetData.domains.isEmpty) counts("url") += 1
      if (!tweetData.emojis.isEmpty) counts("emoji") += 1
      if (tweetData.domains.map(url => url.contains("instagram") || url.contains("pic.twitter.com")).contains(true)) counts("picture") += 1
      tweetData.hashtags.foreach(hashtagStorage.upsert)
      tweetData.domains.foreach(domainStorage.upsert)
      tweetData.emojis.foreach(emojiStorage.upsert)
    }

    case Display => {
      val tweetPerSecond = counts("tweets") / ((System.currentTimeMillis - startTime) / 1000)
      println("\nTotal number of tweets received: " + counts("tweets") + "\n" +
        "Average tweets per second: " + tweetPerSecond + '\n' +
        "Average tweets per minute: " + tweetPerSecond * 60 + '\n' +
        "Average tweets per hour: " + tweetPerSecond * 3600 + '\n' +
        "Percent of tweets that contain a url: " + ("%2.2f" format counts("url").toFloat / counts("tweets") * 100) + '\n' +
        "Percent of tweets that contain a photo url: " + ("%2.2f" format counts("picture").toFloat / counts("tweets") * 100) + '\n' +
        "Percent of tweets that contains emojis: " + ("%2.2f" format counts("emoji").toFloat / counts("tweets") * 100) + '\n' +
        "Top hashtags: " + hashtagStorage.display + '\n' +
        "Top domains of urls in tweets: " + domainStorage.display + '\n' +
        "Top emojis in tweets: " + emojiStorage.display)
    }
  }
}
