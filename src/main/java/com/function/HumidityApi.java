package com.function;

import com.function.entities.HumidityEntity;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.List;
import java.util.Optional;

public class HumidityApi {

  /**
   * This function listens at endpoint "/api/humidity".
   * It consumes 'zoneName' query param which is an uniqe zone identifier and also PartitioningKey in CosmosDB collection.
   * @return HumidityEntity - last reading of humidity for given zone.
   */
  @FunctionName("GetCurrentHumidity")
  public HttpResponseMessage GetCurrentHumidity(
    @HttpTrigger(
      name = "req",
      methods = { HttpMethod.GET },
      authLevel = AuthorizationLevel.FUNCTION,
      route = "humidity"
    ) HttpRequestMessage<Optional<String>> request,
    @CosmosDBInput(
      name = "database",
      databaseName = "humidity-data",
      collectionName = "humidity",
      sqlQuery = "SELECT TOP 1 * from Items ORDER BY Items._ts DESC",
      partitionKey = "{Query.zoneName}",
      connectionStringSetting = "CosmosDBConnectionString"
    ) Optional<List<HumidityEntity>> items,
    final ExecutionContext context
  ) {
    if (items.isPresent()) {
      HumidityEntity humidityEntity = items.get().get(0);
      context
        .getLogger()
        .info(
          "Retrived Item with id: " +
          humidityEntity.getId() +
          " and value: " +
          humidityEntity.getValue()
        );
      return request
        .createResponseBuilder(HttpStatus.OK)
        .body(humidityEntity)
        .build();
    } else {
      context.getLogger().info("No item found for given zone name.");
      return request
        .createResponseBuilder(HttpStatus.BAD_REQUEST)
        .body("No item found for given zone name.")
        .build();
    }
  }

  /**
   * This function listens at endpoint "/api/humidity".
   * It consumes 'zoneName'  query param which is an uniqe zone identifier and also PartitioningKey in CosmosDB collection and
   * 'value' query param which is the humidity value for given zone. 
   * @return HumidityEntity - saved reading. 
   */
  @FunctionName("SaveCurrentHumidity")
  public HttpResponseMessage SaveCurrentHumidity(
    @HttpTrigger(
      name = "req",
      methods = { HttpMethod.POST },
      authLevel = AuthorizationLevel.FUNCTION,
      route = "humidity"
    ) HttpRequestMessage<Optional<String>> request,
    @CosmosDBOutput(
      name = "database",
      databaseName = "humidity-data",
      collectionName = "humidity",
      connectionStringSetting = "CosmosDBConnectionString"
    ) OutputBinding<HumidityEntity> newReading,
    final ExecutionContext context
  ) {
    if (
      !request.getQueryParameters().containsKey("zoneName") ||
      !request.getQueryParameters().containsKey("value")
    ) {
      context.getLogger().info("'zoneName' or 'value' parameter not found.");
      return request
        .createResponseBuilder(HttpStatus.BAD_REQUEST)
        .body("'zoneName' or 'value' parameter not found.")
        .build();
    }

    HumidityEntity humidityEntity = new HumidityEntity(
      request.getQueryParameters().get("zoneName"),
      Double.parseDouble(request.getQueryParameters().get("value"))
    );

    newReading.setValue(humidityEntity);

    return request
      .createResponseBuilder(HttpStatus.OK)
      .body(newReading.getValue())
      .build();
  }
}
