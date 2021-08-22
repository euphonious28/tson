package com.euph28.tson.restclientinterface.listener;

import com.euph28.tson.restclientinterface.TSONRestClient;

/**
 * Listener for events from {@link TSONRestClient}
 */
public interface TSONRestClientListener {
    void onBeforeSend(TSONRestClient tsonRestClient);

    void onAfterSend(TSONRestClient tsonRestClient, boolean isSuccess);
}
