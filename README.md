グーグルカレンダーメンテナンス用コマンド
========================================

* @see http://code.google.com/p/google-api-java-client/wiki/APIs#Calendar_API
* @see http://javadoc.google-api-java-client.googlecode.com/hg/apis/calendar/v3/index.html

# 初期設定

* mainte.scalaのtargetCalId変数に操作したいカレンダーIDを入れておく

* 元ネタ：以下のサンプルをscalaで動くようにしただけです

    http://code.google.com/p/google-api-java-client/source/browse/calendar-cmdline-sample/src/main/java/com/google/api/services/samples/calendar/cmdline/CalendarSample.java?repo=samples

* OAuth認証が出来るように、APIコンソールでClientIdを生成し、src/main/resources/client_secrets.json に記述しておく。

* APIコンソール上の手順は以下のとおり：
    操作したいアカウントにログインした状態で、グーグルカレンダーAPIの設定をする。

    `Register Your Application

     Visit the Google apis console / login with target google Calendar user.
     https://code.google.com/apis/console/?api=calendar
  
     If this is your first time, click "Create project..."
     Click on "API Access", and then on "Create an OAuth 2.0 Client ID...".
     Enter a product name and click "Next".
     Select "Installed application" and click "Create client ID".
     Enter the "Client ID" and "Client secret" shown under "Client ID for installed applications" into src/main/resources/client_secrets.json file after checking out the code (otherwise you will get a 400 INVALID_CLIENT error in the browser when running the sample).`


# 実行

    $ sbt
    > compile
    > run

* 追加するイベント、期間、時間などを適当に設定の上、実行。

* 初回実行時にはブラウザが起動してOAuth認証を実行する。

    認証結果は、 ~/.credentials/ 以下に保存されるので、
    別アカウントで操作する場合にはこのファイルをリネームする操作が必要

	
