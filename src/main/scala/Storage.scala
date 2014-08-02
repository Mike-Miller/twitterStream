package twitterStream

trait Storage {
  def upsert(key: String): Unit
  def display: String
}

trait LocalStorage extends Storage {
  private var storage = scala.collection.mutable.Map[String,Int]() 
  def upsert(key: String) = 
    if (storage.contains(key)) storage(key) += 1 
    else storage(key) = 1
  def display = storage.toList.sortWith(_._2 > _._2).take(3).foldLeft("")(_ + "\n\t" + _._1)
}
