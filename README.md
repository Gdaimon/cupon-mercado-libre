# APP Coupon Mercado Libre
RESTful API creado con Spring boot.
[![Build Status](https://travis-ci.com/Gdaimon/cupon-mercado-libre.svg?branch=master)](https://travis-ci.com/Gdaimon/cupon-mercado-libre)

* Ver Documentación `API` [Swagger](https://cupon-mercado-libre.herokuapp.com/swagger-ui.html)
* Ver `URL API` [API](https://cupon-mercado-libre.herokuapp.com/api/v1/)

## Pre-requisitos

1. Este RESTful API fue creado con [Spring Boot](https://start.spring.io/):
`JAVA >= 8`
2. También necesitaremos Maven ([instalar aquí](https://maven.apache.org/)).
3. recomiendo instalar un cliente para probar el *end-points*. 
Recomiendo para tal propósito es [Postman](https://www.getpostman.com/) que tiene una
aplicación gratuita para Windows, GNU/Linux y OS X.
## Instalación para Desarrollo

1. Clonar el repositorio (ejecutar desde el directorio raiz de este proyecto): `git clone git@github.com:Gdaimon/cupon-mercado-libre.git`
2. Importar dependencias con `Maven`.
3. Iniciar tu servidor en el puerto 8080

## Rutas disponibles:
> Ruta incial: 
> [localhost:8080/api/v1/](localhost:8080/api/v1/)
>
> *Response:*
>   ```
>   { 
>      message: "Bienvenido a la API, dirigete a la URL POST",
>      documentacion: "https://cupon-mercado-libre.herokuapp.com/swagger-ui.html",
>      url: "https://cupon-mercado-libre.herokuapp.com/api/v1/coupon",
>   }
>  ```
>
> Ruta cupones: 
> [localhost:8080/api/v1/coupon](localhost:8080/api/v1/coupon)
>
> *Request:*
>   ```
>   {
>       "item_ids": ["MLA860477515", "MLA805281803"],
>       "amount": 40000
>   }
>   ```
>
> *Response:*
>   ```
>   { 
>      items_id: ["MLA805281803", "MLA860477515"],
>      total: 37498
>   }
>  ``` 
>
> Ruta Documentación
> [localhost:8080/swagger-ui.html](localhost:8080/swagger-ui.html)
