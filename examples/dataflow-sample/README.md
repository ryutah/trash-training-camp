# Dataflowサンプル
Dataflowでのストリーミング処理のサンプル

1. Pub/Sub Poling
2. ストリーム処理でメッセージを大文字変換
3. BigQuery Write

を行う

残念ながらBigQueryのエミュレータはないため、GCPプロジェクトがないと実行できない。

**Java9での挙動がとても怪しい(というかビルドでコケる)ので、おとなしくJava8使うこと**
