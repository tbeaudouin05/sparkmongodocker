
import com.mongodb.spark.config._
import com.mongodb.spark._
import org.bson.Document
import scala.util.Try
import org.apache.spark.sql.DataFrame

val userConf = ReadConfig(Map("database" -> "competition_analysis", "collection" -> "user"), Some(ReadConfig(sc)))
val user = MongoSpark.load(sc, userConf)
val userDF = user.toDF()
userDF.createOrReplaceTempView("user")

// example to handle errors if no data in MongoDB
val email : org.apache.spark.sql.DataFrame = Try({ spark.sql("SELECT DISTINCT _id, email FROM user")
}).getOrElse({ spark.sql("SELECT 1 _id, 'no_email' email")})

val bmlCatalogConfigConf = ReadConfig(Map("database" -> "competition_analysis", "collection" -> "bml_catalog_config"), Some(ReadConfig(sc)))
val bmlCatalogConfig = MongoSpark.load(sc, bmlCatalogConfigConf)
val bmlCatalogConfigF = bmlCatalogConfig.withPipeline(Seq(Document.parse("{'$match': {'matched_by_email': {'$exists':'true'}}}")))
val bmlCatalogConfigDF = bmlCatalogConfigF.toDF()
bmlCatalogConfigDF.createOrReplaceTempView("bmlCatalogConfig")

val totalSKUMatch = spark.sql("""SELECT matched_by_email AS email2, count(_id) AS total_matched_sku 
                            FROM bmlCatalogConfig 
                            WHERE matched_by_email IS NOT NULL 
                            GROUP BY matched_by_email""")
val last7DaySKUMatch = spark.sql("""SELECT matched_by_email AS email3, count(_id) AS last_7_day_matched_sku 
                        FROM bmlCatalogConfig 
                        WHERE matched_by_email IS NOT NULL
                        AND good_match_at > date_sub(current_timestamp(),7)
                        GROUP BY matched_by_email""")
val last14DaySKUMatch = spark.sql("""SELECT matched_by_email AS email4, count(_id) AS last_14_day_matched_sku 
                        FROM bmlCatalogConfig 
                        WHERE matched_by_email IS NOT NULL
                        AND good_match_at > date_sub(current_timestamp(),14) AND good_match_at <= date_sub(current_timestamp(),7)
                        GROUP BY matched_by_email""")

val bmlSupplierConfigCountTopPageLastConf = ReadConfig(Map("database" -> "competition_analysis", "collection" -> "bml_agg_statistic_hist"), Some(ReadConfig(sc)))
val bmlSupplierConfigCountTopPageLast = MongoSpark.load(sc, bmlSupplierConfigCountTopPageLastConf)
val bmlSupplierConfigCountTopPageLastF = bmlSupplierConfigCountTopPageLast.withPipeline(Seq(Document.parse("{'$match': {'matched_by_email': {'$exists':'true'}, 'type': 'BmlSupplierConfigCountTopPageLast'}}")))
val bmlSupplierConfigCountTopPageLastDF = bmlSupplierConfigCountTopPageLastF.toDF()
bmlSupplierConfigCountTopPageLastDF.createOrReplaceTempView("BmlSupplierConfigCountTopPageLast")

val totalSupplierMatch = spark.sql("""SELECT matched_by_email AS email5
                                , count(_id) AS total_matched_supplier 
                                FROM BmlSupplierConfigCountTopPageLast 
                                WHERE matched_by_email IS NOT NULL
                                AND type = 'BmlSupplierConfigCountTopPageLast'
                                GROUP BY matched_by_email""")
val last7DaySupplierMatch = spark.sql("""SELECT matched_by_email AS email6
                                , count(_id) AS last_7_day_matched_supplier 
                                FROM BmlSupplierConfigCountTopPageLast 
                                WHERE matched_by_email IS NOT NULL
                                AND good_match_at > date_sub(current_timestamp(),7)
                                GROUP BY matched_by_email""")
val last14DaySupplierMatch = spark.sql("""SELECT matched_by_email AS email7, count(_id) AS last_14_day_matched_supplier 
                                FROM BmlSupplierConfigCountTopPageLast 
                                WHERE matched_by_email IS NOT NULL
                                AND good_match_at > date_sub(current_timestamp(),14) AND good_match_at <= date_sub(current_timestamp(),7)
                                GROUP BY matched_by_email""")


val notCompetitiveGoodMatchedConf = ReadConfig(Map("database" -> "competition_analysis", "collection" -> "bml_dgk_agg_statistic_hist"), Some(ReadConfig(sc)))
val notCompetitiveGoodMatched = MongoSpark.load(sc, notCompetitiveGoodMatchedConf)
val notCompetitiveGoodMatchedF = notCompetitiveGoodMatched.withPipeline(Seq(Document.parse("{'$match': {'matched_by_email': {'$exists':'true'}, 'type': 'NotCompetitiveGoodMatched'}}")))
val notCompetitiveGoodMatchedDF = notCompetitiveGoodMatchedF.toDF()
notCompetitiveGoodMatchedDF.createOrReplaceTempView("NotCompetitiveGoodMatched")

val totalNonCompetitiveSkuMatch = spark.sql("SELECT matched_by_email AS email8, count(_id) AS total_not_competitive FROM NotCompetitiveGoodMatched GROUP BY matched_by_email")

email.createOrReplaceTempView("email")
totalSKUMatch.createOrReplaceTempView("TotalSKUMatch")
last7DaySKUMatch.createOrReplaceTempView("Last7DaySKUMatch")
last14DaySKUMatch.createOrReplaceTempView("Last14DaySKUMatch")
totalSupplierMatch.createOrReplaceTempView("TotalSupplierMatch")
last7DaySupplierMatch.createOrReplaceTempView("Last7DaySupplierMatch")
last14DaySupplierMatch.createOrReplaceTempView("Last14DaySupplierMatch")
totalNonCompetitiveSkuMatch.createOrReplaceTempView("TotalNonCompetitiveSkuMatch")

val userTableForMongoUpsert = spark.sql("""SELECT
                                        email._id
                                        ,email.email
                                        ,COALESCE(TotalSKUMatch.total_matched_sku,0) total_matched_sku
                                        ,COALESCE(Last7DaySKUMatch.last_7_day_matched_sku,0) last_7_day_matched_sku
                                        ,COALESCE(Last14DaySKUMatch.last_14_day_matched_sku,0) last_14_day_matched_sku
                                        ,COALESCE(TotalSupplierMatch.total_matched_supplier,0) total_matched_supplier
                                        ,COALESCE(Last7DaySupplierMatch.last_7_day_matched_supplier,0) last_7_day_matched_supplier
                                        ,COALESCE(Last14DaySupplierMatch.last_14_day_matched_supplier,0) last_14_day_matched_supplier
                                        ,COALESCE(TotalNonCompetitiveSkuMatch.total_not_competitive,0) total_not_competitive
                                        FROM email
                                        LEFT JOIN TotalSKUMatch
                                        ON TotalSKUMatch.email2 = email.email
                                        LEFT JOIN Last7DaySKUMatch
                                        ON Last7DaySKUMatch.email3 = email.email
                                        LEFT JOIN Last14DaySKUMatch
                                        ON Last14DaySKUMatch.email4 = email.email
                                        LEFT JOIN TotalSupplierMatch
                                        ON TotalSupplierMatch.email5 = email.email
                                        LEFT JOIN Last7DaySupplierMatch
                                        ON Last7DaySupplierMatch.email6 = email.email
                                        LEFT JOIN Last14DaySupplierMatch
                                        ON Last14DaySupplierMatch.email7 = email.email
                                        LEFT JOIN TotalNonCompetitiveSkuMatch
                                        ON TotalNonCompetitiveSkuMatch.email8 = email.email""")  

   MongoSpark.save(userTableForMongoUpsert.write.option("database","competition_analysis").option("collection","user").mode("append"))