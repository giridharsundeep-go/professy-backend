package com.greatleyposhley.professy.collaboration;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.backoff.FixedBackOff;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableKafka
@EnableScheduling
class TicketEventConfiguration {
    @Bean NewTopic ticketEventsTopic(@Value("${app.events.topic:professy.ticket-events.v1}") String topic){return new NewTopic(topic,3,(short)1);}
    @Bean NewTopic ticketEventFailures(@Value("${app.events.topic:professy.ticket-events.v1}") String topic){return new NewTopic(topic+".dlt",3,(short)1);}
    @Bean NewTopic accountMail(){return new NewTopic("professy.account-mail.v1",3,(short)1);}
    @Bean NewTopic accountMailFailures(){return new NewTopic("professy.account-mail.v1.dlt",3,(short)1);}

}

@Service
public class TicketEvents {
    private final WorkAccess access;
    private final KafkaTemplate<String,String> kafka;
    private final TransactionTemplate transactions;
    private final ObjectMapper json;
    private final String topic;
    private final TicketEventPayload events;
    public TicketEvents(WorkAccess access,KafkaTemplate<String,String> kafka,PlatformTransactionManager manager,ObjectMapper json,TicketEventPayload events,@Value("${app.events.topic:professy.ticket-events.v1}") String topic){this.access=access;this.kafka=kafka;this.transactions=new TransactionTemplate(manager);this.json=json;this.topic=topic;this.events=events;}
    @Scheduled(fixedDelayString="${app.events.publish-delay-ms:1000}")
    public void publish(){
        // The row lock prevents two app instances publishing the same pending batch concurrently.
        transactions.executeWithoutResult(status->{
            var batch=access.jdbc.queryForList("SELECT id,event_id,aggregate_key,payload,attempts FROM ticket_event_outbox WHERE published_at IS NULL AND next_attempt_at<=CURRENT_TIMESTAMP ORDER BY id LIMIT 25 FOR UPDATE SKIP LOCKED");
            for(var event:batch){
                try{
                    kafka.send(topic,event.get("aggregate_key").toString(),json.writeValueAsString(events.resolve(event.get("event_id").toString()))).get(5,TimeUnit.SECONDS);
                    access.jdbc.update("UPDATE ticket_event_outbox SET published_at=CURRENT_TIMESTAMP WHERE id=?",event.get("id"));
                }catch(Exception error){
                    access.jdbc.update("UPDATE ticket_event_outbox SET attempts=attempts+1,next_attempt_at=TIMESTAMPADD(SECOND,LEAST(300,POW(2,LEAST(attempts+1,8))),CURRENT_TIMESTAMP) WHERE id=?",event.get("id"));
                    if(error instanceof InterruptedException)Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
    @Scheduled(fixedDelay=2000)
    public void publishMail(){
        transactions.executeWithoutResult(status->{
            var jobs=access.jdbc.queryForList("SELECT user_id,event_id FROM account_mail_queue WHERE sent_at IS NULL AND published_at IS NULL AND event_id IS NOT NULL LIMIT 25 FOR UPDATE SKIP LOCKED");
            for(var job:jobs){try{kafka.send("professy.account-mail.v1",job.get("user_id").toString(),json.writeValueAsString(Map.of("eventId",job.get("event_id")))).get(5,TimeUnit.SECONDS);access.jdbc.update("UPDATE account_mail_queue SET published_at=CURRENT_TIMESTAMP WHERE event_id=?",job.get("event_id"));}catch(Exception error){if(error instanceof InterruptedException)Thread.currentThread().interrupt();break;}}
        });
    }
}
