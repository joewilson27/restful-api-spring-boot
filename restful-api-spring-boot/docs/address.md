# Address API Spec

## Create Address

Endpoint : POST /api/contacts/{idContact}/address

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street" : "Street xyz",
  "city" : "City name",
  "province" : "Province name",
  "country" : "Country name",
  "postalCode" : "Postal code" 
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "random-string",
    "street" : "Street xyz",
    "city" : "City name",
    "province" : "Province name",
    "country" : "Country name",
    "postalCode" : "Postal code" 
  }
}
```


Response Body (Failed) :

```json
{
  "errors" : "Contact is not found"
}
```

## Update Address

Endpoint : PUT /api/contacts/{idContact}/address/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

```json
{
  "street" : "Street xyz",
  "city" : "City name",
  "province" : "Province name",
  "country" : "Country name",
  "postalCode" : "Postal code" 
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "random-string",
    "street" : "Street xyz",
    "city" : "City name",
    "province" : "Province name",
    "country" : "Country name",
    "postalCode" : "Postal code" 
  }
}
```


Response Body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## Get Address

Endpoint : GET /api/contacts/{idContact}/address/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "id" : "random-string",
    "street" : "Street xyz",
    "city" : "City name",
    "province" : "Province name",
    "country" : "Country name",
    "postalCode" : "Postal code" 
  }
}
```


Response Body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## Remove Address

Endpoint : DELETE /api/contacts/{idContact}/address/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## List Address

Endpoint : GET /api/contacts/{idContact}/address

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : [
    {
      "id" : "random-string",
      "street" : "Street xyz",
      "city" : "City name",
      "province" : "Province name",
      "country" : "Country name",
      "postalCode" : "Postal code" 
    }
  ]
}
```

Response Body (Failed) :

```json
{
  "errors" : "Contact is not found"
}
```