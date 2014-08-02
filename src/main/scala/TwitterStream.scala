package twitterStream
import akka.actor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import twitter4j._

object Main extends App {
  val system = ActorSystem("tweetSystem")
  val accumulator = system.actorOf(Props[Accumulator],name = "accumulator")
  val master = system.actorOf(Props(new Master(accumulator)), name = "master")
  val twitterStream = new TwitterStreamFactory(Util.config).getInstance
  
  twitterStream.addListener(Util.simpleStatusListener(master))
  twitterStream.sample
  system.scheduler.schedule(10 seconds, 10 seconds, accumulator, Display)
}

