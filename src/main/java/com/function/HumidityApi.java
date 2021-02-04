package com.function;

import com.function.entities.HumidityEntity;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.List;
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
  @FunctionName("GetCurrentHumidity")
  public HttpResponseMessage GetCurrentHumidity(
    @HttpTrigger(
      name = "req",
      methods = { HttpMethod.GET },
      authLevel = AuthorizationLevel.FUNCTION
    ) HttpRequestMessage<Optional<String>> request,
    @CosmosDBInput(
      name = "database",
      databaseName = "humidity-data",
      collectionName = "humidity",
      sqlQuery = "SELECT TOP 1 * from Items ORDER BY Items._ts DESC",
      partitionKey = "{Query.sensorName}",
      connectionStringSetting = "CosmosDBConnectionString"
    ) Optional<List<HumidityEntity>> items,
    final ExecutionContext context
  ) {
    context.getLogger().info("Parameters are: " + request.getQueryParameters());

    if (items.isPresent()) {
      HumidityEntity humidityEntity = items.get().get(0);
      context.getLogger().info("Retrived Item with id: " + humidityEntity.getId() + " and value: " + humidityEntity.getValue());
      return request
        .createResponseBuilder(HttpStatus.OK)
        .body(humidityEntity)
        .build();
    } else {
      context.getLogger().info("No item found for given sensor name.");
      return request
        .createResponseBuilder(HttpStatus.BAD_REQUEST)
        .body("No item found for given sensor name.")
        .build();
    }
  }
}
