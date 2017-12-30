package jp.trash.trainingcamp;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StreamingSample {
    private static       Logger logger          = Logger.getLogger("StreamingSample");
    private static final String PROJECT_ID      = System.getenv("PROJECT_ID");
    private static final String TABLE_REFERENCE = String.format("%s:sample_dataset.sample_table", PROJECT_ID);
    private static final String SUBSCRIPTION    = String.format("projects/%s/subscriptions/sample-subsc", PROJECT_ID);

    @DefaultCoder(AvroCoder.class)
    private static class MessageRecord {
        private String message;

        public MessageRecord() {
        }

        public MessageRecord(PubsubMessage message) {
            String s = new String(message.getPayload());
            this.message = s.toUpperCase();
        }

        public String getMessage() {
            return this.message;
        }

        public TableRow toTableRow() {
            return new TableRow().set("message", getMessage());
        }
    }

    /**
     * 文字列を大文字に変換する
     */
    private static class ToUpperCaseFn extends DoFn<PubsubMessage, MessageRecord> {
        @ProcessElement
        public void toUppercase(ProcessContext c) {
            MessageRecord record = new MessageRecord(c.element());
            logger.info(String.format("Receive message : %s", record.getMessage()));
            c.output(record);
        }
    }

    /**
     * 文字列をBigQueryの行に変換する
     */
    private static class ToTableRow extends DoFn<MessageRecord, TableRow> {
        @ProcessElement
        public void toTableRow(ProcessContext c) {
            MessageRecord record = c.element();
            logger.info(String.format("Convert to message to table row : %s", record.getMessage()));
            c.output(record.toTableRow());
        }
    }

    public static void main(String[] args) {
        PipelineOptions options = PipelineOptionsFactory.create();
        Pipeline        p       = Pipeline.create(options);

        // BigQueryのテーブルスキーマ定義
        List<TableFieldSchema> fields = new ArrayList<>();
        fields.add(new TableFieldSchema().setName("message").setType("STRING"));
        TableSchema schema = new TableSchema().setFields(fields);

        p.apply("Read From Subscriber", PubsubIO.readMessagesWithAttributes().fromSubscription(SUBSCRIPTION))
         .apply("To Upper Case", ParDo.of(new ToUpperCaseFn()))
         .apply("Message to table row", ParDo.of(new ToTableRow()))
         .apply("Write to BigQuery",
                BigQueryIO.writeTableRows().to(TABLE_REFERENCE).withSchema(schema)
                          .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

        p.run();
    }
}
