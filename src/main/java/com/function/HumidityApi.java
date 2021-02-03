package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HumidityApi {

  /**
   * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
   * 1. curl -d "HTTP Body" {your host}/api/HttpExample
   * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
   */
  @FunctionName("GetHumidity")
  public HttpResponseMessage run(
    @HttpTrigger(
      name = "req",
      methods = { HttpMethod.GET },
      authLevel = AuthorizationLevel.FUNCTION
    ) HttpRequestMessage<Optional<String>> request,
    @CosmosDBInput(
      name = "database",
      databaseName = "humidity-data",
      collectionName = "humidity",
      id = "1",
      partitionKey = "sampleHumidity",
      connectionStringSetting = "CosmosDBConnectionString"
    ) Optional<String> item,
    final ExecutionContext context
  ) {
    context.getLogger().info("Parameters are: " + request.getQueryParameters());
    context.getLogger().info("String from the database is " + (item.isPresent() ? item.get() : null));

    if (!item.isPresent()) {
      return request
        .createResponseBuilder(HttpStatus.BAD_REQUEST)
        .body("Please pass a name on the query string or in the request body")
        .build();
    } else {
      return request
        .createResponseBuilder(HttpStatus.OK)
        .body("Everything OK")
        .build();
    }
  }
}
