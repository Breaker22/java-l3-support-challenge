# Notas

Faltaria organizacion de packages esta todo en un solo package y es dificil hacer seguimiento

## Servicio de /customer/{id}/summary

* Se detecto q el metodo estaba anotado con `@Transactional(readOnly = true)`, esto puede traer los erroes aleatorios ya q puede pasar de q la base de datos este bloqueada o este ocupada

* Se migrara la logica del controller al service por temas de clean code se recomienda no tener mucha logica de negocio en los controllers

* Se detecto que la exception es `RuntimeException` se remplazara por una exception customizable `NotFoundException` y despues se capturara la exception para retornar un 404

* Se mejora el codigo del serivice con `@RequiredArgsConstructor` y borrando el constructor para que automaticamente arme el constructor

** Curl
OK -> `curl --location 'localhost:8080/api/payments/customer/1/summary'`
Error -> `curl --location 'localhost:8080/api/payments/customer/10/summary`