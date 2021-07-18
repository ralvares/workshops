package com.redhat.developer.demos.customer.rest;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BaggageHeadersFactory implements ClientHeadersFactory {
  

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedHashMap<String, String> headers = new MultivaluedHashMap<>();
        String userAgent = incomingHeaders.getFirst("user-agent");
        headers.putSingle("baggage-user-agent", userAgent);
        String userLocation = incomingHeaders.getFirst("user-location");
        headers.putSingle("user-location", userLocation);
        return headers;
    }

}
