# Notas

Faltaria organizacion de packages esta todo en un solo package y es dificil hacer seguimiento

## Servicio de /customer/{id}/summary

* Se detecto q el metodo estaba anotado con `@Transactional(readOnly = true)`, esto puede traer los erroes aleatorios ya q puede pasar de q la base de datos este bloqueada o este ocupada

* Se migrara la logica del controller al service por temas de clean code se recomienda no tener mucha logica de negocio en los controllers

* Se detecto que la exception es `RuntimeException` se remplazara por una exception customizable `NotFoundException` y despues se capturara la exception para retornar un 404

* Se mejora el codigo del serivice con `@RequiredArgsConstructor` y borrando el constructor para que automaticamente arme el constructor

### Curl
OK -> `curl --location 'localhost:8080/api/payments/customer/1/summary'`

Error -> `curl --location 'localhost:8080/api/payments/customer/10/summary`

## Servicio /refund
* Se crea un nuevo endpoint /refund q acepta por query param el id de la transaccion y cumple las reglas de negocio requeridas

* Se agrega en la entity de `Customer` el fetch type EAGER ya que asi se puede acceder mas rapidamente a todas las transacciones de ese customer

* Se agrega la clase `TransactionStatusEnum` para dejar los status prolijos dentro de una clase

* Segun la logica de negocio la transacccion de id 3 tendria que tener una transaccion anterior q este aprobada

### Curl
OK -> `curl --location --request POST 'localhost:8080/api/payments/refund?id=2'`

ERROR -> `curl --location --request POST 'localhost:8080/api/payments/refund?id=3'`