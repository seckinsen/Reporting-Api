package net.seckinsen.service;

import net.seckinsen.model.request.ClientRequest;
import net.seckinsen.model.response.ClientResponse;

import java.util.Optional;

/**
 * Created by seck on 31.08.2017.
 */

public interface ClientService {

    Optional<ClientResponse> getClientInformation(ClientRequest clientRequest, String authToken);

}
