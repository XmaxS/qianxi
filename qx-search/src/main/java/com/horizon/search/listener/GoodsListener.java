package com.horizon.search.listener;

import com.horizon.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "qx.search.insert.queue", durable = "true"),
            exchange = @Exchange(name = "qx.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item.insert", "item.update"}
    ))
    public void listenInsert(Long id) {
        //监听新增或更新
        if (id != null) {
            searchService.insertOrUpdate(id);
        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "qx.search.insert.queue", durable = "true"),
            exchange = @Exchange(name = "qx.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = "item.delete"
    ))
    public void listenDelete(Long id) {
        //监听删除
        if (id != null) {
            searchService.delete(id);
        }
    }

}
