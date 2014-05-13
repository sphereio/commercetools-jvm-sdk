package io.sphere.sdk.client

import com.fasterxml.jackson.core.`type`.TypeReference
import org.scalatest._
import com.typesafe.config.ConfigFactory
import scala.util.Properties._
import scala.collection.JavaConversions._

class NingAsyncHttpClientIntegrationSpec extends WordSpec with ShouldMatchers {

  lazy val config = {
    for {
      core <- envOrNone("JVM_SDK_IT_SERVICE_URL")
      auth <- envOrNone("JVM_SDK_IT_AUTH_URL")
      key <- envOrNone("JVM_SDK_IT_PROJECT_KEY")
      id <- envOrNone("JVM_SDK_IT_CLIENT_ID")
      secret <- envOrNone("JVM_SDK_IT_CLIENT_SECRET")
    } yield {
      val map = Map(
        "sphere.core" -> core,
        "sphere.auth" -> auth,
        "sphere.project" -> key,
        "sphere.clientId" -> id,
        "sphere.clientSecret" -> secret
      )
      ConfigFactory.parseMap(map).withFallback(ConfigFactory.load())
    }
  }.get

  def withClient(client: NingAsyncHttpClient)(body: NingAsyncHttpClient => Unit) {
    try {
      body(client)
    } finally {
      client.close
    }
  }


  classOf[NingAsyncHttpClient].getName must {
    "authenticate" in {
      withClient(new NingAsyncHttpClient(config)) { client =>
        val httpResponse = client.execute(new Fetch[String](new TypeReference[String] {}) {
          override def httpRequest(): HttpRequest = HttpRequest.of(HttpMethod.GET, "/categories")
        }).get()
        httpResponse.getStatusCode should be(200)
        httpResponse.getResponseBody should include( """{"offset":0""") //quick guess to see if category is loaded
      }
    }
  }
}