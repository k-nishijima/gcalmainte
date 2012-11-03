import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.GoogleHeaders
import com.google.api.client.googleapis.auth.oauth2._
import com.google.api.client.googleapis.batch.BatchRequest
import com.google.api.client.googleapis.batch.json.JsonBatchCallback
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model._
import com.google.common.collect.Lists

import java.io._
import java.util._

import scala.collection.JavaConversions._

/**
 * Googleカレンダーメンテナンスコマンド
 * @see http://code.google.com/p/google-api-java-client/wiki/APIs#Calendar_API
 * @see http://javadoc.google-api-java-client.googlecode.com/hg/apis/calendar/v3/index.html
 *
 * 
 * 初期設定:
 * targetCalIdに操作したいカレンダーIDを入れておく
 * 
 * 初回実行時、OAuth認証を実行
 *   認証結果は、 ~/.credentials 以下に保存されるので、
 *   別アカウントで操作する場合にはこのファイルをリネームする操作が必要
 * 
 * 元ネタ：以下のサンプルをscalaで動くようにしただけ
 * http://code.google.com/p/google-api-java-client/source/browse/calendar-cmdline-sample/src/main/java/com/google/api/services/samples/calendar/cmdline/CalendarSample.java?repo=samples
 * 
 * 
 *
 * ↓APIコンソール上の手順は以下のとおり：
 * 操作したいアカウントにログインした状態で、グーグルカレンダーAPIの設定をする。
 * 
 * Register Your Application
 *
 *   Visit the Google apis console / login with target google Calendar user.
 *   https://code.google.com/apis/console/?api=calendar
 *
 *   If this is your first time, click "Create project..."
 *   Click on "API Access", and then on "Create an OAuth 2.0 Client ID...".
 *   Enter a product name and click "Next".
 *   Select "Installed application" and click "Create client ID".
 *   Enter the "Client ID" and "Client secret" shown under "Client ID for installed applications" into src/main/resources/client_secrets.json file after checking out the code (otherwise you will get a 400 INVALID_CLIENT error in the browser when running the sample).
 *
 * 
 */
object GcalMainte { 
  val targetCalId = "yourCalendarId"

  def main(args: Array[String]): Unit = {
	val credential = authorize()
	val client = new com.google.api.services.calendar.Calendar.Builder(
            new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("Google-CalendarSample/1.0").build()

	/*
	val feed = client.calendarList().list().execute()
    if (feed.getItems() != null) {
      for(entry <- feed.getItems()) { 
        println("-----------------------------------------------")

        println("ID: " + entry.getId())
        println("Summary: " + entry.getSummary())

        if (entry.getDescription() != null) {
          println("Description: " + entry.getDescription())
        }
	  }
    }
	*/


	val calendar = client.calendars.get(targetCalId).execute()
    println("ID: " + calendar.getId())
    println("Summary: " + calendar.getSummary())

	val year = 2013
	(1 to 12).foreach({month =>
	  val dt: org.joda.time.DateTime = new org.joda.time.DateTime(year, month, 1, 0, 0, 0, 0)
	  val lastDay = dt.dayOfMonth().withMaximumValue().getDayOfMonth()

	  (1 to lastDay).foreach({day =>
		println("createEvent at %d-%2d-%2d" format(year, month, day))
        // 10時、13時、16時に
		addEvent(client, calendar.getId(), newEvent(year, month, day, 10))
		addEvent(client, calendar.getId(), newEvent(year, month, day, 13))
		addEvent(client, calendar.getId(), newEvent(year, month, day, 16))
	  })
	  println("-----")
    })


  }

  def addEvent(client: com.google.api.services.calendar.Calendar,
			 calendarId: String, event:Event): Unit = {
	val addResult = client.events().insert(calendarId, event).execute()
	println("add : " + addResult.getSummary())
  }

  def newEvent(year:Int, month:Int, day:Int, hour:Int): Event = { 
    val event = new Event()
    event.setSummary("◯")
    val startDate = new org.joda.time.DateTime(year, month, day, hour, 0, 0, 0)
    val endDate = startDate.plusHours(3)

    val start = new DateTime(startDate.toDate(), TimeZone.getTimeZone("JST"))
    event.setStart(new EventDateTime().setDateTime(start))
    val end = new DateTime(endDate.toDate(), TimeZone.getTimeZone("JST"))
    event.setEnd(new EventDateTime().setDateTime(end))
  }

  def authorize(): Credential = {
    // load client secrets
    val clientSecrets = GoogleClientSecrets.load(
        new JacksonFactory(), getClass.getResourceAsStream("/client_secrets.json"))
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
     println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
          + "into calendar-cmdline-sample/src/main/resources/client_secrets.json")
      java.lang.System.exit(1)
    }

    // set up file credential store
    val credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/calendar.json"), new JacksonFactory())
    // set up authorization code flow
    val flow = new GoogleAuthorizationCodeFlow.Builder(
        new NetHttpTransport(), new JacksonFactory(), clientSecrets,
        Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(credentialStore).build()
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user")
  }

}
