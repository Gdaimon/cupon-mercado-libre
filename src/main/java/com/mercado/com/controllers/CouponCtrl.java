package com.mercado.com.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercado.com.models.Item;
import com.mercado.com.models.ItemsCoupon;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
	public static String createJSONResponse ( List < String > listaProductosCoupon, Float total ) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper ( );
		ItemsCoupon ItemsRespuesta = new ItemsCoupon ( );
		ItemsRespuesta.setItemIds ( listaProductosCoupon );
		ItemsRespuesta.setTotal ( total );
		String jsonString = mapper.writeValueAsString ( ItemsRespuesta );
		return jsonString;
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
	@Async ( "processExecutor" )
	public Map < String, Float > productosEncontrados ( List < String > listaProductos ) throws IOException {
		Map < String, Float > items = new HashMap <> ( );
		for ( String producto : listaProductos ) {
			Item item = consultarProducto ( producto );
			Optional < Item > optionalJson = Optional.ofNullable ( item );
			if ( optionalJson.isPresent ( ) ) {
				if ( item.getStatus ( ).equals ( "active" ) ) {
					items.put ( item.getId ( ), item.getPrice ( ) );
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
	@Async ( "processExecutor" )
	public Item consultarProducto ( String producto ) throws IOException {
		ObjectMapper mapper = new ObjectMapper ( );
		final CloseableHttpClient httpClient = HttpClients.createDefault ( );
		final String URL = "https://api.mercadolibre.com/items/";
		Item item = null;
		
		HttpGet httpGet = new HttpGet ( URL + producto );
		try ( CloseableHttpResponse response = ( CloseableHttpResponse ) httpClient.execute ( httpGet ) ) {
			HttpEntity entity = response.getEntity ( );
			if ( entity != null && response.getStatusLine ( ).getStatusCode ( ) == 200 ) {
				String productPlain = EntityUtils.toString ( entity );
				item = mapper.readValue ( productPlain, Item.class );
			}
		} catch ( Exception e ) {
			e.printStackTrace ( );
			return item;
		} finally {
			httpClient.close ( );
		}
		return item;
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
	public ResponseEntity < String > productosCoupon ( @RequestBody ItemsCoupon itemsCoupon ) throws IOException {
		Map < String, Float > items = productosEncontrados ( itemsCoupon.getItemIds ( ) );
		List < String > listaProductosCoupon = calculate ( items, itemsCoupon.getAmount ( ) );
		Float total = totalProductosCoupon ( listaProductosCoupon, items );
		String ItemsRespuesta = createJSONResponse ( listaProductosCoupon, total );
		if ( total > 0 ) {
			return new ResponseEntity <> ( ItemsRespuesta, HttpStatus.OK );
		} else {
			return new ResponseEntity <> ( ItemsRespuesta, HttpStatus.NOT_FOUND );
		}
	}
	
	@GetMapping ( "" )
	public ResponseEntity < String > welcome ( ) {
		JSONObject resultado = new JSONObject ( );
		resultado.put ( "message", "Bienvenido a la API, dirigete a la URL POST" );
		resultado.put ( "url", "https://cupon-mercado-libre.herokuapp.com/api/v1/coupon" );
		resultado.put ( "documentacion", "https://cupon-mercado-libre.herokuapp.com/swagger-ui.html" );
		return new ResponseEntity <> ( resultado.toString ( ), HttpStatus.OK );
	}
	
}
