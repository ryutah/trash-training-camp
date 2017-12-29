import os
import sys

from google.cloud import pubsub
from logging import getLogger
from logging import INFO
from logging import StreamHandler


PROJECT_ID = os.environ["PROJECT_ID"]

logger = getLogger(__name__)
logger.setLevel(INFO)
handler = StreamHandler(sys.stdout)
handler.flush = sys.stdout.flush
logger.addHandler(handler)


def run():
    logger.info("Start worker...")
    subscriber = pubsub.SubscriberClient()
    subsc_name = "projects/{}/subscriptions/sample-subsc".format(PROJECT_ID)
    sub = subscriber.subscribe(subsc_name)
    feature = sub.open(callback)

    try:
        feature.result()
    except BaseException as ex:
        logger.error(ex)
        sub.close()


def callback(message):
    # 引数`message`からPubSubにPublishされたメッセージを取得できる
    # ここで、各処理を行い次のPubSubに処理結果をPublishする
    logger.info(message)
    message.ack()


if __name__ == "__main__":
    run()
