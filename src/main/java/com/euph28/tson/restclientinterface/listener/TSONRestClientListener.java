package com.euph28.tson.restclientinterface.listener;

import com.euph28.tson.restclientinterface.TSONRestClient;

/**
 * Listener for events from {@link TSONRestClient}
 */
public interface TSONRestClientListener {
    /**
     * Event triggered before a request is sent by the rest client
     *
     * @param tsonRestClient Rest client that triggered the event
     */
    void onBeforeSend(TSONRestClient tsonRestClient);

    /**
     * Event triggered after a request is sent by the rest client
     *
     * @param tsonRestClient Rest client that triggered the event
     * @param isSuccess      Status of request sent. Value is {@code true} if the status code of response is 200
     */
    void onAfterSend(TSONRestClient tsonRestClient, boolean isSuccess);
}
