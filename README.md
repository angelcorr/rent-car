# RentCar — Parcial 2 de PGII

> **Este archivo hace parte de la entrega.** El equipo debe completarlo y mantenerlo actualizado de acuerdo con los requisitos del enunciado y del documento técnico.

## 1. Integrantes

- Ángeles Emiliana Rondón Corrales
- Daniel Felipe Monsalve Montalvo

## 2. Descripción de la solución

<!-- Describan brevemente qué resuelve la aplicación y qué funcionalidades implemento el equipo. -->
Se pide una aplicacion en java con JavaFX que permita crear una reserva de un vehiculo. La aplicacion debe validar la solicitud, revisar que el vehiculo este, se verifiquen esas fechas, calcular el costo por modalidad y los servicios extras, cobrar con una API de pagos, y dejar la reserva confirmada o fallida según el resuldado.

## 3. Requisitos y funcionalidades implementadas

<!-- Resuman las funcionalidades implementadas y relacionen cada una con los requisitos del enunciado. -->
- RF-001: `RentalRequest.Builder.build()` valida que cliente, vehículo, modalidad y token de pago no estén vacíos, que las fechas existan y que la fecha de inicio sea anterior a la de fin. Una solicitud inválida no puede construirse.
- RF-002: `RentalFacade.rent()` coordina el alquiler completo: calcula el costo, crea la reservación en estado PENDING (verificando disponibilidad), cobra y la pasa a CONFIRMED o FAILED (`RentalFacade`, `ReservationService`).
- RF-003: cada modalidad (Economy, Executive, Premium) es una fábrica que crea su tarifa, cobertura y asistencia (`RentalModalityFactory`, `ModalityFactorySelector`).
- RF-004: el pago se envía a la API externa mediante un adaptador que traduce la respuesta a `PaymentStatus` (`PaymentApiAdapter`).
- RF-005: los servicios extra se suman al costo y a la descripción mediante decoradores (`ServiceDecorator`, `ExtraServiceDecorator`, `ExtraServicesApplier`).
- RF-006: antes de cobrar se verifica que el cliente esté autorizado; si no lo está, el pago no se envía a la API (`PaymentGatewayProxy`, `SampleDataCustomerVerifier`).
- RF-007: los servicios individuales y los paquetes se tratan igual, y un paquete puede contener otros paquetes (`RentService`, `IndividualService`, `ServicePackage`).

## 4. Arquitectura y organización del código

<!-- Expliquen la arquitectura utilizada, la responsabilidad de las capas y cómo se relacionan. -->
La aplicación está organizada en capas, para que cada parte tenga una sola responsabilidad:

- Vista (`view`): archivos FXML, como `reservation-view.fxml`, que describen la interfaz y no contienen lógica de negocio.
- Controlador (`controller`): recibe los eventos de la vista, arma la solicitud con el Builder y delega el alquiler en `RentalFacade`, sin contener reglas de negocio.
- Servicios (`service`): contienen las reglas del negocio, como la validación de fechas y campos, la disponibilidad del vehículo, la confirmación de la reserva y el cálculo del costo. `ModalityFactorySelector` elige la fábrica según la modalidad.
- Modelo (`model`): clases de datos que usan el patrón Builder (`RentalRequest`, `Reservation`, `Vehicle`). `RentalRequest` valida sus datos en `build()`; `Customer` se crea con un constructor normal.
- Precios (`pricing`, `pricing.decorator`, `factory`): calculan el costo. Las fábricas definen la tarifa, la cobertura y la asistencia de cada modalidad, y los decoradores suman los servicios extra.
- Pagos (`payment`): se comunica con la API de pagos. El adaptador traduce la respuesta de la API, y el proxy verifica al cliente antes de cobrar.
- Datos y repositorio (`data`, `repository`): datos de ejemplo y acceso a los vehículos.
- Aplicación (`app`): arranca la aplicación JavaFX y, con `ApplicationServices`, entrega los servicios a los controladores.

## 5. Patrones de diseño

### 5.1 Builder (creacional)

- **Problema:** las solicitudes y las reservas tienen varios campos. Un constructor con muchos parámetros es difícil de leer y fácil de equivocarse.
- **Justificación:** con Builder cada campo se asigna por nombre, y el objeto no cambia después de crearse.
- **Clases participantes:** `RentalRequest` y `RentalRequest.Builder`; `Reservation` y `Reservation.Builder`.
- **Dónde se utiliza:** en `ReservationService.createPendingReservation` se crea la reserva con `new Reservation.Builder()`. En `changeStatus` se crea una copia nueva con el nuevo estado. `RentalRequest` copia la lista de servicios con `List.copyOf`.
- **Ventajas:** código legible y objetos inmutables.
- **Limitaciones:** solo `RentalRequest.Builder` valida en `build()`, porque RF-001 exige que no pueda existir una solicitud inválida. Los Builder de `Reservation` y `Vehicle` no validan; sus datos ya llegan validados desde los servicios. Esa validación se repite a propósito en `ReservationService` para protegerlo cuando se usa sin el Builder.

### 5.2 Abstract Factory (creacional)

- **Problema:** cada modalidad (Economy, Executive, Premium) necesita tarifa, cobertura y asistencia propias, y el flujo no debería conocer los detalles de cada una.
- **Justificación:** una fábrica por modalidad crea su familia completa de productos. El resto del sistema solo usa la interfaz `RentalModalityFactory`.
- **Clases participantes:** `RentalModalityFactory` (interfaz); `EconomicFactory`, `ExecutiveFactory` y `PremiumFactory` (fábricas concretas); `ModalityFactorySelector` (elige la fábrica según el nombre de la modalidad).
- **Dónde se utiliza:** `ReservationService.calculateCost` pide la fábrica a `ModalityFactorySelector.factoryFor` y se la pasa a `RentalCostCalculatorServices`, que solo conoce la interfaz.
- **Ventajas:** agregar una modalidad exige una fábrica nueva, y el flujo principal no cambia.
- **Limitaciones:** `ModalityFactorySelector` compara el nombre de la modalidad, así que agregar una modalidad sí exige modificarlo.

## Estados de la reservación

| Estado | Cuándo ocurre |
|---|---|
| PENDING | Se crea después de validar la solicitud, comprobar la disponibilidad y calcular el costo, antes de cobrar |
| CONFIRMED | El pago fue APPROVED y el vehículo sigue disponible al confirmar |
| FAILED | El pago fue rechazado, el cliente no está autorizado, hubo un error de comunicación con la API, o el vehículo dejó de estar disponible al confirmar |

Una solicitud inválida o un vehículo no disponible al momento de crear no generan reservación. Solo las reservaciones CONFIRMED bloquean el vehículo en consultas de disponibilidad.

### 5.3 Facade (estructural)

- **Problema:** el alquiler tiene varios pasos (buscar el vehículo, calcular el costo con modalidad y extras, crear la reservación verificando disponibilidad, cobrar y confirmar o fallar). La vista no debería conocer ni coordinar esos pasos.
- **Justificación:** el Facade ofrece una sola operación, `rent(RentalRequest)`, y la vista solo la llama.
- **Clases participantes:** `RentalFacade`; `RentalResult` (resultado para la interfaz); `VehicleService`, `ReservationService` y `PaymentGateway` (subsistemas que coordina).
- **Dónde se utiliza:** `ReservationController.onCreateReservation` construye la solicitud y llama a `ApplicationServices.rentalFacade().rent(...)`. `RentalFacade` usa `ExtraServicesApplier` para aplicar los extras y `FixedCost` como base del Decorator.
- **Ventajas:** la interfaz depende de un solo método; el orden de los pasos y el manejo de errores están en un solo lugar y se prueban sin interfaz (`RentalFacadeTest`).
- **Limitaciones:** la Facade conoce varios subsistemas. Si el pago se aprueba pero el vehículo se ocupó entre tanto, la reservación queda FAILED y no hay reembolso automático (caso límite, el flujo actual es secuencial y en memoria).

### 5.4 Adapter (estructural)

- **Problema:** la API de pagos responde con su propio formato (`PaymentApiResponse`), con métodos como `isApproved` e `isDeclined`, que no coinciden con el lenguaje del sistema.
- **Justificación:** el adaptador traduce esa respuesta a nuestro estado `PaymentStatus` (APPROVED, DECLINED o ERROR), así que el resto del sistema no depende del proveedor.
- **Clases participantes:** `PaymentGateway` (interfaz del sistema); `PaymentApiAdapter` (adaptador); `PaymentApiCaller` (interfaz que representa la llamada HTTP); `PaymentStatus`.
- **Dónde se utiliza:** `PaymentApiAdapter.pay` convierte la respuesta: aprobada → APPROVED, rechazada → DECLINED, cualquier otro caso → ERROR. Si hay una excepción de E/S → ERROR; si se interrumpe el hilo, se restaura y se devuelve ERROR. `PaymentApiAdapterTest` prueba los cuatro casos con un caller falso.
- **Ventajas:** el sistema no conoce el formato de la API, y las pruebas no necesitan el servidor real en `localhost:8080`.
- **Limitaciones:** depende de los métodos `isApproved` e `isDeclined` de la librería de referencia.

### 5.5 Proxy (estructural)

- **Problema:** antes de cobrar hay que verificar que el cliente esté autorizado (RF-006). Si no lo está, la API de pagos no debe recibir ninguna solicitud.
- **Justificación:** el proxy tiene la misma interfaz que el gateway real (`PaymentGateway`). Así, quien usa el pago no sabe que la verificación existe, y la regla queda en un solo lugar.
- **Clases participantes:** `PaymentGatewayProxy` (proxy); `PaymentGateway` (sujeto); `CustomerVerifier` (interfaz de verificación); `SampleDataCustomerVerifier` (implementación con los datos de ejemplo).
- **Dónde se utiliza:** `PaymentGatewayProxy.pay` primero llama a `verifier.isAuthorized`. Si el cliente no está autorizado, devuelve DECLINED sin llamar al gateway. Si la verificación lanza una excepción, devuelve ERROR. `PaymentGatewayProxyTest` usa un gateway falso que cuenta las llamadas para comprobar cuándo no debe llamarse.
- **Ventajas:** la regla de autorización está en un solo lugar y se prueba sin pagos reales.
- **Limitaciones:** los clientes autorizados vienen de `SampleData` (datos en memoria). Un cliente no autorizado devuelve DECLINED y no una excepción, por decisión de diseño.

### 5.6 Decorator (estructural)

- **Problema:** los servicios extra (GPS, silla infantil, conductor adicional) suman costo y descripción al alquiler. Crear una clase por cada combinación sería muy complicado (RF-005).
- **Justificación:** cada decorador envuelve al anterior y suma su propio costo, así que cualquier combinación se arma con las mismas clases.
- **Clases participantes:** `RentService` (componente); `ServiceDecorator` (decorador abstracto); `ExtraServiceDecorator` (decorador concreto); `IndividualService` (componente concreto); `ExtraServicesApplier` (aplica la cadena a una lista de servicios extra).
- **Dónde se utiliza:** `ServiceDecorator.getCost()` devuelve `wrapped.getCost() + getExtraCost()`. `ExtraServicesApplier.applyTo` crea un decorador por cada servicio extra. `ExtraServicesApplierTest` comprueba el costo y la descripción.
- **Ventajas:** agregar un servicio nuevo no exige clases nuevas, y la descripción se arma sola.
- **Limitaciones:** si un servicio necesita una regla de cálculo propia (por ejemplo, un seguro del 10 % del alquiler), hay que crear una subclase de `ServiceDecorator`. El orden de los decoradores no cambia el costo, pero sí el orden del texto de la descripción.

### 5.7 Composite (estructural)

- **Problema:** RF-007 pide paquetes que contengan servicios individuales y otros paquetes, y que se traten igual que un solo servicio.
- **Justificación:** `ServicePackage` implementa `RentService`, así que puede contener tanto servicios individuales como otros paquetes.
- **Clases participantes:** `RentService` (componente); `IndividualService` (hoja); `ServicePackage` (compuesto).
- **Dónde se utiliza:** `ServicePackage.getCost()` suma el costo de sus componentes, y `getDescription()` recorre la lista.
- **Ventajas:** los paquetes anidados funcionan sin código especial y se usan igual que un servicio individual.
- **Limitaciones:** `ServicePackage` no detecta ciclos (un paquete agregado dentro de sí mismo provocaría un desbordamiento de pila) y `add()` acepta cualquier `RentService`. Con los datos de ejemplo no ocurre.
<!-- Para cada patrón requerido, documenten el problema que resuelve, las clases participantes y dónde se utiliza. -->

## 6. Ejecución de la aplicación

### Requisitos

- JDK 21.
- Conexión a Internet la primera vez que Gradle necesite descargar la distribución o dependencias.

### Ejecutar

Desde la raíz del proyecto:

- **Windows PowerShell:** ` .\gradlew.bat run `
- **Git Bash / macOS / Linux:** ` ./gradlew run `

En IntelliJ IDEA también se puede ejecutar la tarea `application > run` desde la ventana Gradle.

### Ejecutar las pruebas

- **Windows PowerShell:** ` .\gradlew.bat test `
- **Git Bash / macOS / Linux:** ` ./gradlew test `

## 7. Integración de pagos

<!-- Documenten cómo configurar y probar la integración que implementó el equipo, según el enunciado. No incluyan claves, contraseñas ni tokens reales. -->
Asumiendo que quien va a configurar y probar la integración ya pudo ejecutar el programa siguiendo el paso a paso en [Ejecutar](#ejecutar)
Seguir el paso a paso del [README-api-pagos](external/README-api-pagos.md). Tener en cuenta:

La API se inicia en `http://localhost:8080` y debe estar encendida antes de alquilar. Tokens de prueba (seleccionables en la pantalla):

- `tok_approved`: pago aprobado.
- `tok_declined`: pago rechazado.
- `tok_api_error`: error de la API.

El cliente `CUS-1003` no está autorizado: el Proxy rechaza el pago sin llamar a la API. Si la API está apagada, la reservación queda FAILED y la aplicación muestra un mensaje.


## 8. Pruebas realizadas

<!-- Describan las pruebas unitarias y funcionales, cómo ejecutarlas y qué resultados obtuvieron. -->
Ejecutar desde la raíz del proyecto: `./gradlew test` (Windows: `.\gradlew.bat test`). Si Gradle indica `UP-TO-DATE`, usar `./gradlew clean test`.

| Clase de prueba | Requisito | Qué verifica |
|---|---|---|
| `RentalRequestTest` | RF-001 | Solicitud válida y rechazo de datos faltantes o fechas inválidas |
| `ReservationServiceTest` | RF-002 | Estados, disponibilidad y solapamiento de fechas |
| `RentalFacadeTest` | RF-002, RF-006 | Alquiler exitoso, pago rechazado, error de API, cliente no autorizado sin llamar a la API |
| `ModalityFactoryTest` | RF-003 | Tres modalidades con costos distintos |
| `PaymentApiAdapterTest` | RF-004 | Aprobado, rechazado, error HTTP, API no disponible |
| `DecoratorTest`, `ExtraServicesApplierTest` | RF-005 | Combinación de extras |
| `PaymentGatewayProxyTest` | RF-006 | Autorizado, no autorizado, falla de verificación |
| `ServicePackageTest` | RF-007 | Paquete simple y anidado |

## 9. Limitaciones y decisiones de diseño

<!-- Registren las limitaciones conocidas y justifiquen las decisiones relevantes. -->
- La limitacion conocida es la implementación del patron Abstract Factory en este momento ya que se usa un switch case para verificar que factory se esta creando. Esto no está bien ya que la idea es poder agregar una nueva fabrica, por ejemplo Luxury, sin tener que editar codigo existente y en este momento este no es el caso ya que se agrega un case más, además de crear la clase LuxuryFactory con su tariff class.
- El proyecto base no incluyó `CustomerVerificationService`; se usó la interfaz `CustomerVerifier` con `SampleDataCustomerVerifier`. Otra implementación solo requeriría una clase nueva, sin cambiar el Proxy.
- `ServicePackage` no detecta ciclos.
- Un cobro aprobado no se revierte si el vehículo se ocupó entre tanto.
- Cuando el cliente no está autorizado, la pantalla muestra el mismo mensaje que un pago rechazado.

## 10. Evidencias

<!-- Agreguen enlaces o referencias a las evidencias solicitadas en el enunciado. -->
