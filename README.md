# Sistema de Venta de Boletos para Estadio

## 1. Introduccion
Este proyecto implementa la logica base de un sistema de venta de boletos para estadio en Java. El sistema maneja categorias de boletos, disponibilidad de asientos, validaciones de compra, calculo de precios, generacion de reportes de venta y almacenamiento en archivos de texto.

El objetivo actual fue dejar un nucleo funcional ejecutable en Java puro (ideal para correr en NetBeans) y listo para que el equipo integre la GUI Swing.

## 2. Problema que resuelve
Permite:
- Gestionar boletos por categoria (VIP, General, Preferencial).
- Evitar venta duplicada de asientos.
- Calcular total de compra por categoria y cantidad de asientos.
- Registrar reportes de venta en memoria (cola FIFO).
- Persistir reportes diarios en archivos `.txt`.

## 3. Estructura del proyecto

```text
Proyecto-Boletos/
	README.md
	SistemaBoletosEstadio/
		src/
			Estructura/
				ColaReportes.java
				GestorBoletos.java
				GestorPrecios.java
				MapaAsientos.java
			gui/
				PanelAsientos.java
				PanelCompra.java
				VentanaPrincipal.java
			modelo/
				Boleto.java
				Categoria.java
				ReporteVenta.java
			servicio/
				Validaciones.java
				VentaServicio.java
			sistemaboletosestadio/
				Main.java
			util/
				ArchivoUtil.java
				FechaUtil.java
		out/
		reportes/
```

## 4. Estructuras de datos utilizadas (requisito)

### 4.1 LinkedList para boletos por categoria
- Archivo: `Estructura/GestorBoletos.java`
- Implementacion: `Map<Categoria, LinkedList<Boleto>>`
- Uso:
	- Inicializar boletos por categoria con asientos y precio.
	- Buscar boleto por asiento.
	- Listar boletos disponibles y todos los boletos.

### 4.2 Matrices para mapa de asientos
- Archivo: `Estructura/MapaAsientos.java`
- Implementacion: `Map<Categoria, boolean[][]>`
- Uso:
	- `false` = disponible, `true` = ocupado.
	- Validar y ocupar asientos por etiqueta (ejemplo: A1, B3).
	- Listar asientos disponibles por categoria.

### 4.3 HashMap/Map para precios por categoria
- Archivo: `Estructura/GestorPrecios.java`
- Implementacion: `Map<Categoria, Double>`
- Uso:
	- Obtener precio por categoria.
	- Actualizar precio por categoria.

### 4.4 Cola FIFO para reportes de venta
- Archivo: `Estructura/ColaReportes.java`
- Implementacion: `Queue<ReporteVenta>`
- Uso:
	- Encolar reporte al confirmar compra.
	- Desencolar y consultar frente de la cola.

### 4.5 Archivos de texto para persistencia
- Archivo: `util/ArchivoUtil.java`
- Uso:
	- Guarda reportes en `reportes/reporte_ventas_ddmmaaaa.txt`.
	- Formato legible con fecha/hora, categoria, cantidad, total y asientos.

## 5. Cambios implementados (bitacora de avance)

### 5.1 Modelo
1. `Categoria.java`
	 - Cambio de clase vacia a `enum` con: `VIP`, `GENERAL`, `PREFERENCIAL`.
2. `Boleto.java`
	 - Validaciones en constructor (id, categoria, precio, asiento).
	 - Estado de venta y metodo `vender()` con proteccion para doble venta.
	 - Getters completos y `toString()`.
3. `ReporteVenta.java`
	 - Modelo completo de reporte con:
		 - fecha/hora
		 - categoria
		 - cantidad de boletos
		 - ingreso total
		 - asientos vendidos

### 5.2 Estructuras
1. `MapaAsientos.java`
	 - Matriz por categoria.
	 - Conversion de etiqueta de asiento a indice de matriz.
	 - Validacion de existencia y disponibilidad de asiento.
2. `GestorPrecios.java`
	 - Precios iniciales configurados.
	 - Obtencion y actualizacion de precios.
3. `GestorBoletos.java`
	 - Inicializacion de boletos por categoria usando `LinkedList`.
	 - Busqueda por asiento.
	 - Listado de disponibles.
4. `ColaReportes.java`
	 - Cola FIFO operativa para reportes.

### 5.3 Servicios
1. `Validaciones.java`
	 - Validacion de categoria.
	 - Validacion de lista de asientos.
	 - Validacion de asientos no duplicados.
2. `VentaServicio.java`
	 - Orquestacion de compra:
		 - valida entradas
		 - revisa disponibilidad
		 - ocupa asiento
		 - marca boleto vendido
		 - calcula total
		 - genera reporte
		 - encola reporte
		 - guarda reporte en txt
	 - Exposicion de funciones para integracion con GUI:
		 - `obtenerAsientosDisponibles(...)`
		 - `calcularTotal(...)`
		 - `confirmarCompra(...)`
		 - `actualizarPrecioCategoria(...)`

### 5.4 Utilidades
1. `FechaUtil.java`
	 - Formato de nombre de archivo diario.
	 - Formato de fecha/hora para reporte.
2. `ArchivoUtil.java`
	 - Escritura append en UTF-8.
	 - Creacion automatica de carpeta de reportes.

### 5.5 Punto de entrada
1. `Main.java`
	 - Flujo demo ejecutable por consola:
		 - muestra asientos disponibles
		 - calcula total de compra
		 - confirma compra
		 - muestra reporte generado
		 - verifica cola de reportes

## 6. Flujo funcional actual
1. Usuario selecciona categoria y asientos (simulado en `Main`).
2. El sistema valida categoria y asientos.
3. Verifica disponibilidad en la matriz.
4. Marca asientos como ocupados.
5. Marca boletos como vendidos en listas enlazadas.
6. Calcula total segun precio de categoria.
7. Genera `ReporteVenta`.
8. Encola reporte en `ColaReportes`.
9. Guarda reporte en archivo txt diario.

## 7. Estado de ejecucion
Estado: funcional para logica de negocio en Java puro.

Resultado esperado al ejecutar `Main`:
- Se imprime compra de ejemplo exitosa.
- Se actualiza disponibilidad de asientos.
- Se crea/actualiza archivo en carpeta `reportes/`.

## 8. Pendientes para completar proyecto final
1. Implementar GUI Swing real:
	 - `gui/VentanaPrincipal.java`
	 - `gui/PanelAsientos.java`
	 - `gui/PanelCompra.java`
2. Agregar pruebas unitarias y de integracion.
3. Completar evidencia del documento final (capturas y casos de prueba).

## 9. Guia rapida para ejecutar en NetBeans
1. Abrir proyecto `SistemaBoletosEstadio` en NetBeans.
2. Verificar JDK configurado (idealmente version compatible con propiedades del proyecto).
3. Ejecutar clase principal `sistemaboletosestadio.Main`.
4. Revisar salida de consola y carpeta `reportes/`.

## 10. Entregables recomendados para la clase
1. Documento (este README como base) con:
	 - Introduccion
	- Diseno/arquitectura
	 - Estructuras de datos
	 - Bitacora de cambios
	 - Evidencias
2. Exposicion:
	 - Demo de compra
	 - Demostracion de validaciones
	 - Demostracion de archivo de reporte generado

## 11. Nota de version
Version de avance: `v0.1-logica-core`
- Logica base implementada.
- Integracion GUI pendiente.
