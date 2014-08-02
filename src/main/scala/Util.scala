package twitterStream
import twitter4j._
import akka.actor._

object Util {
  val config = new twitter4j.conf.ConfigurationBuilder()
    .setOAuthConsumerKey("Ppg3ybBYLBsx8BLeVUlBZGI7D")
    .setOAuthConsumerSecret("kjZPrji7eXaa4wHV5N8nXl7GXHUJT9ay3i948b6y6OBEJbMLF3")
    .setOAuthAccessToken("2606152063-FduwP1pp0BXYWd0Xz2PUxCnIMlMtAa9iEs1FV6s")
    .setOAuthAccessTokenSecret("xvgtAqw4gf30W5WHnFlooyk7etLXqdqB1xx1ki1OCWGwX")
    .build

  def simpleStatusListener(master: ActorRef) = new StatusListener() {
    def onStatus(status: Status) = master ! Work(status)
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}