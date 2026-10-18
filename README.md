# RentCar — Parcial 2 de PGII

> **Este archivo hace parte de la entrega.** El equipo debe completarlo y mantenerlo actualizado de acuerdo con los requisitos del enunciado y del documento técnico.

## 1. Integrantes

- Daniel Felipe Monsalve Montalvo
- Angeles Emiliana Rondon


## 2. Descripción de la solución

<!-- Describan brevemente qué resuelve la aplicación y qué funcionalidades implemento el equipo. -->
Se pide una aplicacion en java Con java FX que permita crear una reserva de un vehiculo. la aplicacion debe validad la solicitud, revisar que el vehiculo este, se verifiquen esas fechas, calcular el costo por modalidad y los servicios extras,cobrar con una API de pagos, y dejar la reserva confirmada o fallida segun el resuldado

## 3. Requisitos y funcionalidades implementadas

<!-- Resuman las funcionalidades implementadas y relacionen cada una con los requisitos del enunciado. -->
- RF-001: la validación de la solicitud revisa que el cliente, el vehículo y la modalidad no estén vacíos, que las fechas existan y que la fecha de inicio sea anterior a la de fin (`ReservationService`).
- RF-002: la reserva se crea en estado PENDING, se revisa la disponibilidad del vehículo y, al confirmar, pasa a CONFIRMED si el vehículo sigue libre o a FAILED si ya no lo está (`ReservationService`).
- RF-003: Modalidad de Reserva como Factory
- Rf-005: Procesamiento de pagos mediante un servicio externo Implementacion de la Api
- RF-005: los servicios extra se suman al costo total mediante decoradores (`ServiceDecorator`, `ExtraServiceDecorator`, `ExtraServicesApplier`).
- RF-006: antes de cobrar, se verifica que el cliente esté autorizado; si no lo está, el pago no se envía a la API (`PaymentGatewayProxy`, `SampleDataCustomerVerifier`).
- RF-007: los servicios individuales y los paquetes se tratan igual, y un paquete puede contener otros paquetes (`RentService`, `IndividualService`, `ServicePackage`).
## 4. Arquitectura y organización del código
La aplicación está organizada en capas, para que cada parte tenga una sola responsabilidad:

- Vista (`view`): archivos FXML, como `reservation-view.fxml`, que describen la interfaz y no contienen lógica de negocio.
- Controlador (`controller`): recibe los eventos de la vista, se llena con los datos de ejemplo y debe delegar la reserva en el Facade.
- Servicios (`service`): contienen las reglas del negocio, como la validación de fechas y campos, la disponibilidad del vehículo, la confirmación de la reserva y el cálculo del costo. `ModalityFactorySelector` elige la fábrica según la modalidad.
- Modelo (`model`): clases con atributos y getters, sin validaciones. Usan el patrón Builder para construirse (`RentalRequest`, `Reservation`, `Customer`, `Vehicle`).
- Precios (`pricing`, `pricing.decorator`, `factory`): calculan el costo. Las fábricas definen la tarifa, la cobertura y la asistencia de cada modalidad, y los decoradores suman los servicios extra.
- Pagos (`payment`): se comunica con la API de pagos. El adaptador traduce la respuesta de la API, y el proxy verifica al cliente antes de cobrar.
- Datos y repositorio (`data`, `repository`): datos de ejemplo y acceso a los vehículos.
- Aplicación (`app`): arranca la aplicación JavaFX y, con `ApplicationServices`, entrega los servicios a los controladores.
<!-- Expliquen la arquitectura utilizada, la responsabilidad de las capas y cómo se relacionan. -->

## 5. Patrones de diseño
## 5. Patrones de diseño

### 5.1 Builder (creacional)

- **Problema:** las solicitudes y las reservas tienen varios campos. Un constructor con muchos parámetros es difícil de leer y fácil de equivocarse.
- **Justificación:** con Builder cada campo se asigna por nombre, y el objeto no cambia después de crearse.
- **Clases participantes:** `RentalRequest` y `RentalRequest.Builder`; `Reservation` y `Reservation.Builder`.
- **Dónde se utiliza:** en `ReservationService.createPendingReservation` se crea la reserva con `new Reservation.Builder()`. En `changeStatus` se crea una copia nueva con el nuevo estado. `RentalRequest` copia la lista de servicios con `List.copyOf`.
- **Ventajas:** código legible y objetos inmutables.
- **Limitaciones:** el Builder no valida nada. Las validaciones están en `ReservationService`.

### 5.2 Abstract Factory (creacional)

- **Problema:** cada modalidad (Economy, Executive, Premium) necesita tarifa, cobertura y asistencia propias, y el flujo no debería conocer los detalles de cada una.
- **Justificación:** una fábrica por modalidad crea su familia completa de productos. El resto del sistema solo usa la interfaz `RentalModalityFactory`.
- **Clases participantes:** `RentalModalityFactory` (interfaz); `EconomicFactory`, `ExecutiveFactory` y `PremiumFactory` (fábricas concretas); `ModalityFactorySelector` (elige la fábrica según el nombre de la modalidad).
- **Dónde se utiliza:** `ReservationService.calculateCost` pide la fábrica a `ModalityFactorySelector.factoryFor` y se la pasa a `RentalCostCalculatorServices`, que solo conoce la interfaz.
- **Ventajas:** agregar una modalidad exige una fábrica nueva, y el flujo principal no cambia.
- **Limitaciones:** `ModalityFactorySelector` compara el nombre de la modalidad, así que agregar una modalidad sí exige tocarlo.

### 5.3 Facade (estructural)

angeles pendiente de que lo completes
- **Problema:** el alquiler tiene varios pasos (validar, verificar al cliente, revisar disponibilidad, calcular, aplicar extras, crear, cobrar y confirmar). La vista no debería conocer todos esos pasos.
- **Justificación:** el Facade ofrece una sola operación de alquiler, y la vista solo la llama.
- **Clases participantes:** `RentalFacade` (pendiente de implementación por nuestra compañera).
- **Dónde se utiliza:** `ReservationController.onCreateReservation`, que todavía está pendiente.
- **Estado:** pendiente.

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
- **Limitaciones:** el constructor de `ExtraServiceDecorator` es `protected`, así que la cadena se arma desde `ExtraServicesApplier`, que está en el mismo paquete.

### 5.7 Composite (estructural)

- **Problema:** RF-007 pide paquetes que contengan servicios individuales y otros paquetes, y que se traten igual que un solo servicio.
- **Justificación:** `ServicePackage` implementa `RentService`, así que puede contener tanto servicios individuales como otros paquetes.
- **Clases participantes:** `RentService` (componente); `IndividualService` (hoja); `ServicePackage` (compuesto).
- **Dónde se utiliza:** `ServicePackage.getCost()` suma el costo de sus componentes, y `getDescription()` recorre la lista.
- **Ventajas:** los paquetes anidados funcionan sin código especial y se usan igual que un servicio individual.
- **Limitaciones:** .
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

## 8. Pruebas realizadas

<!-- Describan las pruebas unitarias y funcionales, cómo ejecutarlas y qué resultados obtuvieron. -->

## 9. Limitaciones y decisiones de diseño

<!-- Registren las limitaciones conocidas y justifiquen las decisiones relevantes. -->

## 10. Evidencias

<!-- Agreguen enlaces o referencias a las evidencias solicitadas en el enunciado. -->
