# GKE Pub/Sub利用サンプル
Pub/Subからメッセージをサブスクライブしてくるサンプル。  
実際のアプリでは、サブスクライブしたメッセージを処理し、処理結果を次のPub/SubトピックにPublishする。

## 必須ツール
* Python3
* pip
* Docker
* Google Cloud SDK(クラウドで実行したい場合)


## 初期設定
1. Cloud Pub/Subのトピックを作成する  
  ```console
  $ gcloud pubsub topics create sample-topic
  ```

2. Cloud Pub/Subのサブスクリプションを作成する  
  ```console
  $ gcloud pubsub subscriptions create sample-subsc --topic sample-topic
  ```


## アプリ実行方法
### ローカル実行
1. GCPで、`Pub/Sub サブスクライバー`権限のあるサービスアカウントを作成
2. キーファイルをJSON形式でダウンロードし、サンプルコードディレクトリ直下に`key.json`という名前で保存
3. Dockerイメージをビルドする  
  ```console
  $ docker build .
  # イメージIDを確認しておく
  ```

4. 下記コマンドでイメージを実行する  
  ```console
  $ docker run -it --rm -v $(pwd):/tmp -e GOOGLE_APPLICATION_CREDENTIALS=/tmp/key.json -e PROJECT_ID={YOUR_GCP_PROJECT_ID} {IMAGE_ID}
  ```


### クラウド実行
1. 環境変数`PROJECT_ID`を設定する  
  ```console
  $ export PROJECT_ID={YOUR_GCP_PROJECT_ID}
  ```

2. `create_cluster.sh`を実行してクラスタを作成する  
  ```console
  $ ./create_cluster.sh
  ```

3. Google Cloud Container Builderを有効可する

4. `{PROJECT_NUMBER}@cloudbuild.gserviceaccount.com`に、`Kubernetes Engine Developer`権限を付与する  
  ```consle
  $ gcloud projects add-iam-policy-binding $PROJECT \
      --member=serviceAccount:{PROJECT_NUMBER}@cloudbuild.gserviceaccount.com \
      --role=roles/container.developer
  ```

5. 下記コマンドを実行して、コンテナのビルド・デプロイをする  
  ```console
  $ gcloud container builds submit --config cloudbuild.yaml .
  ```


## 動作確認方法
1. TopicにメッセージをPublishする  
   ```console
   $ gcloud pubsub topics publish projects/dev-ryutah/topics/sample-topic --message {YOUR_MESSAGE}
   # ex) gcloud pubsub topics publish projects/dev-ryutah/topics/sample-topic --message hogehoge
   ```

2. コンテナのログを確認する
  ```console
  $ kubectl get pod
  # NAMEを確認する
  $ kubectl logs {POD_NAME}
  ```
