package com.mercado.com.controllers;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping ( "/api/v1" )
public class CouponCtrl {
	
	/**
	 * Metodo encargado de la Creacion del JSON de respuesta
	 * @param listaProductosCoupon
	 * @param total
	 * @return
	 */
	public static JSONObject createJSONResponse ( List < String > listaProductosCoupon, Float total ) {
		JSONObject resultado = new JSONObject ( );
		resultado.put ( "item_ids", listaProductosCoupon );
		resultado.put ( "total", total );
		return resultado;
	}
	
	/**
	 * Metodo encargado de totalizar los items del coupon
	 * @param listaProductosCoupon
	 * @param items
	 * @return
	 */
	public static Float totalProductosCoupon ( List < String > listaProductosCoupon, Map < String, Float > items ) {
		Float total = 0f;
		for ( String producto : listaProductosCoupon ) {
			total += items.get ( producto );
		}
		return total;
	}
	
	/**
	 * Metodo encargado de construir el listado de productos encontrados
	 * @param listaProductos
	 * @return
	 * @throws IOException
	 */
	public static Map < String, Float > productosEncontrados ( List < String > listaProductos ) throws IOException {
		Map < String, Float > items = new HashMap <> ( );
		for ( String producto : listaProductos ) {
			JSONObject productJSON = consultarProducto ( producto );
			Optional < JSONObject > optionalJson = Optional.ofNullable ( productJSON );
			if ( optionalJson.isPresent ( ) ) {
				if ( productJSON.get ( "status" ).equals ( "active" ) ) {
					items.put ( String.valueOf ( productJSON.get ( "id" ) ),
									Float.parseFloat ( String.valueOf ( productJSON.get ( "price" ) ) )
					);
				}
			}
		}
		return items;
	}
	
	/**
	 * Metodo encargado de realizar la busqueda de un producto en el API
	 * @param producto
	 * @return
	 */
	public static JSONObject consultarProducto ( String producto ) throws IOException {
		final CloseableHttpClient httpClient = HttpClients.createDefault ( );
		final String URL = "https://api.mercadolibre.com/items/";
		JSONObject product = null;
		
		HttpGet httpGet = new HttpGet ( URL + producto );
		try ( CloseableHttpResponse response = ( CloseableHttpResponse ) httpClient.execute ( httpGet ) ) {
			HttpEntity entity = response.getEntity ( );
			if ( entity != null && response.getStatusLine ( ).getStatusCode ( ) == 200 ) {
				String productPlain = EntityUtils.toString ( entity );
				product = new JSONObject ( productPlain );
			}
		} catch ( Exception e ) {
			e.printStackTrace ( );
			return product;
		} finally {
			httpClient.close ( );
		}
		return product;
	}
	
	/**
	 * Metodo encargado de maximizar la compra con la cantidad de items y el monto deseado
	 * @param items
	 * @param amount
	 * @return
	 */
	public static List < String > calculate ( Map < String, Float > items, Float amount ) {
		
		final Map < String, Float > sortedItems = sortedMap ( items );
		List < String > resultItems = new ArrayList <> ( );
		Float total = 0f;
		for ( String key : sortedItems.keySet ( ) ) {
			if ( total <= amount ) {
				float resultSustract = amount - ( total + sortedItems.get ( key ) );
				if ( resultSustract >= 0 ) {
					resultItems.add ( key );
					total += sortedItems.get ( key );
				} else {
					break;
				}
			} else {
				break;
			}
		}
		return sortedItems ( resultItems );
	}
	
	/**
	 * Metodo encargado de realizar el ordenamisnto de los items por su precio
	 * @param items
	 * @return
	 */
	public static Map < String, Float > sortedMap ( final Map < String, Float > items ) {
		return items.entrySet ( )
						       .stream ( )
						       .sorted ( Map.Entry. < String, Float > comparingByValue ( ) )
						       .collect ( Collectors.toMap ( Map.Entry::getKey, Map.Entry::getValue, ( e1, e2 ) -> e1, LinkedHashMap::new ) );
	}
	
	/**
	 * Metodo encargado de realizar el ordenamiento de los items por su nombre
	 * @param items
	 * @return
	 */
	public static List < String > sortedItems ( List < String > items ) {
		return items.stream ( ).sorted ( ).collect ( Collectors.toList ( ) );
	}
	
	@PostMapping ( "/coupon" )
	public ResponseEntity < String > productosCoupon ( @RequestBody Map < String, Object > json ) throws IOException {
		List < String > listaProductos = ( List < String > ) json.get ( "item_ids" );
		Float amount = Float.parseFloat ( json.get ( "amount" ).toString ( ) );
		Map < String, Float > items = productosEncontrados ( listaProductos );
		List < String > listaProductosCoupon = calculate ( items, amount );
		Float total = totalProductosCoupon ( listaProductosCoupon, items );
		JSONObject resultado = createJSONResponse ( listaProductosCoupon, total );
		System.out.println ( resultado );
		if ( total > 0 ) {
			return new ResponseEntity <> ( resultado.toString ( ), HttpStatus.OK );
		} else {
			return new ResponseEntity <> ( resultado.toString ( ), HttpStatus.NOT_FOUND );
		}
		
	}
	
	/**
	 * Metodo para convertir un JSONArray en un List<String> de items
	 * @param arregloItems
	 * @return
	 */
	private List < String > convertirListaItems ( JSONArray arregloItems ) {
		return arregloItems.toList ( ).stream ( ).map ( item -> item.toString ( ) ).collect ( Collectors.toList ( ) );
	}
	
	@GetMapping ( "" )
	public ResponseEntity < String > welcome ( ) {
		JSONObject resultado = new JSONObject ( );
		resultado.put ( "message", "Bienvenido a la API, dirigete a la URL" );
		resultado.put ( "url", "http://localhost:8080/api/v1/coupon" );
		return new ResponseEntity <> ( resultado.toString ( ), HttpStatus.OK );
	}
	
}
