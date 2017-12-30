import argparse
import os
import sys

from google.cloud import pubsub
from logging import getLogger
from logging import INFO
from logging import StreamHandler


PROJECT_ID = os.environ["PROJECT_ID"]
TOPIC = "projects/{}/topics/sample-topic".format(PROJECT_ID)
SUBSCRIPTION = "projects/{}/subscriptions/sample-subsc".format(PROJECT_ID)

logger = getLogger(__name__)
logger.setLevel(INFO)
handler = StreamHandler(sys.stdout)
handler.flush = sys.stdout.flush
logger.addHandler(handler)


def init_command():
    """サブコマンドとかを初期化する"""

    parser = argparse.ArgumentParser()
    subparsers = parser.add_subparsers()

    parser_run_with_prepare = subparsers.add_parser(
        "prepare",
        help="Run worker with prepare topic and subscription."
    )
    parser_run_with_prepare.set_defaults(handler=run_with_prepare)

    parser_publish = subparsers.add_parser(
        "publish",
        help="Publish message to tipic",
    )
    parser_publish.add_argument("message", help="Message to publish")
    parser_publish.set_defaults(handler=publish)

    return parser


def publish(args):
    """PubSubのトピックにメッセージをパブリッシュする"""

    publisher = pubsub.PublisherClient()
    publisher.publish(TOPIC, str.encode(args.message), spam="eggs")


def run():
    """PubSubワーカーのメイン処理

    サブスクリプションを指定してポーリングしてる感じ。
    """

    logger.info("Start worker...")
    subscriber = pubsub.SubscriberClient()
    sub = subscriber.subscribe(SUBSCRIPTION)
    feature = sub.open(callback)

    try:
        feature.result()
    except BaseException as ex:
        logger.error(ex)
        sub.close()


def run_with_prepare(argv):
    """トピックなどを作成してからワーカを実行する"""

    create_topic()
    create_subscription()
    run()


def create_topic():
    publisher = pubsub.PublisherClient()
    publisher.create_topic(TOPIC)


def create_subscription():
    subscriber = pubsub.SubscriberClient()
    subscriber.create_subscription(SUBSCRIPTION, TOPIC)


def callback(message):
    """メッセージ受信時のコールバック処理

    この中で受信メッセージに応じた処理を実行していく
    """

    logger.info(message)
    message.ack()


if __name__ == "__main__":
    parser = init_command()
    args = parser.parse_args()
    if hasattr(args, "handler"):
        args.handler(args)
    else:
        run()
