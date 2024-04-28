# User API Spec

## Register User

Endpoint : POST /api/users

Request Body :

```json
{
  "username": "joe",
  "password": "secret",
  "name": "Joe Wilson"
}
```

Response Body (Success) :

```json
{
  "data": "OK"
}
```

Response Body (Failed) :

```json
{
  "errors": "Username must not empty, ???"
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username": "joe",
  "password": "secret"
}
```

Response Body (Success) :

```json
{
  "data": {
    "token": "TOKEN",
    "expiredAt": 123456789 // milliseconds
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Username or password is wrong"
}
```

## Get User

Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data": {
    "username": "joewilson",
    "name": "Joe Wilson"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Unauthorized"
}
```

## Update User

Endpoint : PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "name": "Joe Wilson", // put if only wants to update name
  "password": "secret", // put if only wants to update password
}
```

Response Body (Success) :

```json
{
  "data": {
    "username": "joewilson",
    "name": "Joe Wilson"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Unauthorized"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data": "OK"
}
```