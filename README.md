# Banking API

API RESTful para la gestión de clientes, productos bancarios y transacciones.  
Desarrollada con **Spring Boot**, siguiendo principios de arquitectura limpia y buenas practicas.

---

## Tabla de Contenido

- [Prueba](#prueba)
- [Arquitectura y Estructura del Proyecto](#arquitectura-y-estructura-del-proyecto)
- [Componentes Principales](#componentes-principales)
- [Tests](#tests)
- [Manejo de errores del controlador](#manejo-de-errores-del-controlador)
- [Endpoints Principales](#endpoints-principales)
- [Ejecución](#ejecución-y-pruebas)


---
## Prueba

## 1. Generalidades

El objetivo de este proyecto es desarrollar una aplicación destinada a los trabajadores de una entidad financiera.  
La aplicación permitirá la **administración de clientes** (registro, actualización y eliminación), la **creación de productos financieros** para los clientes y la **gestión de movimientos transaccionales** sobre dichos productos, así como la consulta de estados de cuentas.

---

## 2. Requerimientos Funcionales

La aplicación debe exponer un servicio REST que permita cumplir con los siguientes requerimientos de CRUD para clientes, productos y transacciones.

### **Clientes**

- Permitir la creación de clientes con los siguientes atributos mínimos:
    - `id`
    - Tipo de identificación
    - Número de identificación
    - Nombres
    - Apellido
    - Correo electrónico
    - Fecha de nacimiento
    - Fecha de creación (calculada automáticamente)
    - Fecha de modificación (actualizada automáticamente al modificar datos)

- Permitir la modificación de la información de un cliente, calculando la fecha de modificación automáticamente.

- Permitir la eliminación de un cliente.

- **Restricciones:**
    - Un cliente **no podrá ser creado ni existir** en la base de datos si es menor de edad.
    - Un cliente **no podrá ser eliminado** si tiene productos vinculados.
    - El campo correo electrónico debe tener un formato válido (ejemplo: `xxxx@xxxxx.xxx`). *(Opcional)*
    - El nombre y apellido deben tener una extensión mínima de 2 caracteres. *(Opcional)*

---

### **Productos (Cuentas)**

- Permitir la creación únicamente de dos tipos de productos:
    - Cuenta corriente
    - Cuenta de ahorros

- Un producto financiero solo podrá existir si está vinculado a un cliente.

- Atributos mínimos de las cuentas:
    - `id`
    - Tipo de cuenta
    - Número de cuenta
    - Estado (`activa`, `inactiva`, `cancelada`)
    - Saldo
    - Exenta GMF
    - Fecha de creación (calculada automáticamente)
    - Fecha de modificación
    - Usuario (cliente) al que pertenece la cuenta

- **Restricciones:**
    - La cuenta de ahorros **no puede tener saldo menor a $0**.
    - Las cuentas corrientes y de ahorros pueden ser activadas o inactivadas en cualquier momento.
    - El número de cuenta debe:
        - Ser único
        - Generarse automáticamente
        - Tener 10 dígitos numéricos
        - Iniciar en `53` (ahorros) o `33` (corriente)
    - Al crear una cuenta de ahorros, debe quedar **activa** por defecto.
    - Solo se podrán cancelar cuentas con saldo igual a $0.
    - El saldo de la cuenta debe actualizarse automáticamente al realizar una transacción exitosa.

---

### **Transacciones (Movimientos Financieros)**

- Permitir la creación únicamente de las siguientes transacciones:
    - Consignación (depósito)
    - Retiro
    - Transferencia entre cuentas

- Actualizar el saldo (y saldo disponible) de las cuentas con cada transacción.

- Las transferencias deben ser posibles únicamente entre cuentas existentes en el sistema.
    - Al realizar una transferencia, se deben generar:
        - Movimiento de crédito en la cuenta de recepción
        - Movimiento de débito en la cuenta de envío

- **Persistencia**:  
  Implementar la estructura de base de datos necesaria para cumplir con todos los requerimientos funcionales obligatorios.

---

## 3. Requerimientos No Funcionales

- Desarrollar como mínimo un proyecto **backend**.
- El proyecto Backend debe estar desarrollado en **JAVA**.
- Utilizar arquitectura **hexagonal** (preferido) o **MVC** (Modelo, Vista, Controlador).
- Utilizar una base de datos SQL (SQL Server, Oracle, PostgreSQL o MySQL).
- El backend debe estar estructurado por capas, al menos:
    - Entity
    - Service
    - Controller
    - Repository

---

### **Test Unitarios**

- Implementar tests unitarios utilizando **JUnit**.
- Asegurar cobertura en las capas **Service** y **Controller**.

---

### **Control de Versiones**

- Usar Git para el control de versiones.
- Crear un solo repositorio en **GitHub** con el código fuente del proyecto.
- Evidenciar el avance mediante commits y push frecuentes.

---

## Arquitectura y Estructura del Proyecto

El proyecto sigue una **arquitectura hexagonal (puertos y adaptadores)** y separa claramente las responsabilidades de dominio, aplicación e infraestructura.
Esto se logra a travez de una un proyecto multi-modulo (tres modulos). Cada modulo tiene sus propias dependencias dentro de su archivo pom.xml, lo
que permite trabajar cada modulo por separado. 



```
src/
├── domain/           # Entidades y lógica de negocio (DDD)
├── application/      # Casos de uso, DTOs y servicios
├── infrastructure/   # Controladores REST, repositorios, configuración

```

* _La aplicación se levanta desde la capa de infraestructure_
* _domain es puro, mientras que application depende de él e infraestructure depende de application_

- **domain**: Entidades como `Client`, `Product`, `Transaction` con lógica y restricciones de negocio.
- **application**: Servicios de aplicación (`*UseCases`), DTOs para entrada/salida, reglas de orquestación.
- **infrastructure**: Controladores REST, adaptadores a base de datos (repositorios), configuración.


---

## Componentes Principales

- **Clientes**: CRUD de clientes.
- **Productos Bancarios**: Cuentas de ahorro y corriente, con reglas de negocio.
- **Transacciones**: Depósitos, retiros y transferencias entre cuentas, con validaciones de saldo, estado y tipo de cuenta.

---

## Tests

- **Domain**: La capa de domain/entity tiene tests para validar lo que se considera "Reglas de negocio".
- **Service**: La capa de application/services tiene tests para validar el funcionamiento e integracion entre los componentes y requisitos funcionales de la app. 
- **Controller**: La capa de infraestructure/controller tiene tests para validar las respuestas HTTP de los controladores.

---

## Manejo de errores del controlador

- **Manejo de Errores**:

    - Handler global (`@ControllerAdvice`) traduce errores a respuestas HTTP:
        - 404 para NotFound.
        - 400 para validaciones.
        - 500 errores genericos

---

## Endpoints Principales

### Cliente
- `POST /api/clients` — Crear cliente
- `GET /api/clients/{id}` — Obtener cliente por ID
- `DELETE /api/clients/{id}` — Eliminar cliente
- `GET /api/clients/` — Obtener lista de clientes
- `PUT /api/clients` — Editar cliente (aun no soporta edición parcial)

### Productos
- `POST /api/products` — Crear producto (cuenta)
- `GET /api/products/{accountNumber}` — Buscar producto por número de cuenta
- `GET /api/products/client/{clientId}` — Productos por cliente
- `PUT /api/products/{accountNumber}/enable` — Activar producto
- `PUT /api/products/{accountNumber}/disable` — Inactivar producto
- `PUT /api/products/{accountNumber}/cancel` — Cancelar producto

### Transacciones
- `POST /api/transactions` — Crear transacción (depósito, retiro, transferencia)
- `GET /api/transactions/{id}` — Buscar transacción por ID
- `GET /api/transactions/product/{accountNumber}` — Listar transacciones de un producto
- `GET /api/transactions` — Todas las transacciones

---

## Ejecución y Pruebas

1. **Requisitos**: Java 17+, Maven/Gradle, PostgreSQL.
2. **Variables de entorno**: Crear un archivo `env.properties` y configurar la conexión a DB (verificar qué variables en `application.properties`).
3. **Ejecutar la aplicación en el IDE o con cmd.**


