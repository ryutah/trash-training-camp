# GKE Pub/Sub利用サンプル
Pub/Subからメッセージをサブスクライブしてくるサンプル。  
実際のアプリでは、サブスクライブしたメッセージを処理し、処理結果を次のPub/SubトピックにPublishする。  
ローカル実行する場合は、特にクラウド環境など不要なので、適当にコールバック処理いじったりして動作確認とかしてみてほしい。


## 構成
```
.
├── Dockerfile           # PubSubワーカコンテナのDockerfile
├── Dockerfile.emulator  # PubsubエミュレータコンテナのDockerfile
├── cloudbuild.yaml      # Google Cloud Container Builderのビルドステップ設定ファイル
├── create_cluster.sh    # Google Kubernetes Engineのクラスタ作成スクリプト
├── docker-compose.yaml  # ローカル実行用docker compose設定ファイル
├── k8s                  # Kubernetes設定
├── key.json             # ローカル実行用サービスアカウントキーファイルのダミー
├── main.py              # メイン処理
├── publish.sh           # ローカル実行用メッセージPublishスクリプト
└── requirements.txt     # 依存関係定義
```


## コード編集とかしたい場合
1. 必須ツールをインストールする
  - Python3
  - pip

2. 依存パッケージを取得する  
  ```console
  $ pip install -r requirements.txt
  ```

**あると便利なツールとか**
* python
* virtualenv


## アプリ実行方法
### ローカル実行
1. 実行に必要なツールをインストールする
  * Docker
  * docker-compose

2. 下記コマンドでコンテナを実行する  
  ```console
  $ docker-compose up
  ```


### クラウド実行
1. Google Cloud SDKをインストールする

2. Cloud Pub/Subのトピックを作成する  
  ```console
  $ gcloud pubsub topics create sample-topic
  ```

3. Cloud Pub/Subのサブスクリプションを作成する  
  ```console
  $ gcloud pubsub subscriptions create sample-subsc --topic sample-topic
  ```

4. 環境変数`PROJECT_ID`を設定する  
  ```console
  $ export PROJECT_ID={YOUR_GCP_PROJECT_ID}
  ```

5. `create_cluster.sh`を実行してクラスタを作成する  
  ```console
  $ ./create_cluster.sh
  ```

6. Google Cloud Container Builderを有効可する

7. `{PROJECT_NUMBER}@cloudbuild.gserviceaccount.com`に、`Kubernetes Engine Developer`権限を付与する  
  ```consle
  $ gcloud projects add-iam-policy-binding $PROJECT \
      --member=serviceAccount:{PROJECT_NUMBER}@cloudbuild.gserviceaccount.com \
      --role=roles/container.developer
  ```

8. 下記コマンドを実行して、コンテナのビルド・デプロイをする  
  ```console
  $ gcloud container builds submit --config cloudbuild.yaml .
  ```


## 動作確認方法
### ローカル環境
1. アプリ実行手順で、アプリケーションを実行したまま別のターミナルを開く
2. 下記コマンドで、メッセージをPublishする  
  ```console
  $ ./publish.sh {YOUR_MESSAGE}
  ```

### クラウド環境
1. TopicにメッセージをPublishする  
   ```console
   $ gcloud pubsub topics publish projects/dev-ryutah/topics/sample-topic --message {YOUR_MESSAGE}
   ```

2. コンテナのログを確認する
  ```console
  $ kubectl get pod
  # NAMEを確認する
  $ kubectl logs {POD_NAME}
  ```
