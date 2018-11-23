import com.mongodb.spark.config._
import com.activeviam.sparkube._
import com.mongodb.spark._
import org.apache.spark.storage.StorageLevel._

val readConfig = ReadConfig(Map("database" -> "competition_analysis", "collection" -> "bml_catalog_config"), Some(ReadConfig(sc)))
val customRdd = MongoSpark.load(sc, readConfig)
val dataset = customRdd.toDF().persist(MEMORY_ONLY_SER)
new Sparkube().fromDataset(dataset).withName("test").expose()