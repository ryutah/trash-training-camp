# 第1回Trash開発合宿
## なにこれ？
開発合宿の議論とかタスク、課題管理とかがSlackだけだと色々辛かったので作った。

## どういう風に使うの？
開発合宿に関するタスク・課題などをIssueで管理、議論する。  
その他、開発合宿でやることとか決まったら設計書とかサンプルコードとかもGitで管理しちゃおうぜって感じ。  

## その他
タスク化前の議論とかは基本的にはSlackで、タスク化後の議論はGitHubもしくはSlackで。  
あくまで基本方針なんで、各々やりやすいやり方で問題ない。  
一応最終的にはGitHubとかに残してもらえるとありがたい感じ？


## 開発
### レガシーサイト探索Bot
* クローリングでWebサイトを徘徊
* HTMLなどを解析して、サイトが古くさいかどうか判定
* TwitterBOTとかで晒す
* **[リポジトリ](https://github.com/ryutah/lgbot)**

#### システム構成
![architecture](/images/architecture.png)

1. クローラー
  * AppEngine スタンダード環境
  * Webクローリングを行う
  * クローリングしたサイトをPubSubにどんどん投げてく

2. サイト解析
  * PubSubから受け取ったサイト情報をとりあえずBigQueryに保存
  * 決定したルールに従ってサイトを解析
  * 解析結果をPubSubに投げる
  * 解析結果をBigQueryに保存する

3. サイト判定
  * PubSunから受け取った解析済み情報を利用し、レガシーサイト判定をする
  * 判定は単純なルールベース、または機械学習
  * 判定結果をPubSubに投げる
  * 判定結果をBigQueryに保存する

4. Twitter投稿
  * PubSubから受け取った判定済みサイト情報をTwitterに投稿


#### ツールなど
現段階で利用を想定している開発ツールや環境など。  
事前調査などで、追加・変更が必要になる可能性あり。

##### 言語
* Python
  - クローリング(AppEngine)
  - サイト判定(Compute Engine)
  - Twitterへの投稿もPythonでやっちゃう？
* Java
  - サイト解析(Apache Beam)

##### クラウドサービス
* Google App Engine
  - GAE/Python Standard Edition
* Cloud Dataflow
  - Apache Beamの実行環境
* Google Compute Engine
  - サイト判定
  - Twitterへの投稿
  - **Google Kubernetes Engine(GKE)のほうがいいかもしれない**
* BigQuery
  - クローリングしたサイトの保存
  - サイトの解析と結果の保存
* Cloud Pub/Sub
  - 各マイクロサービス間のメッセージングなど
* Cloud Datalab
  - データ分析・可視化など
  - 開発時に使用するかも

##### FWとか
* Apache Beam
* scikit-learn
  - サイト判定に機械学習使う場合。多分これが一番簡単
