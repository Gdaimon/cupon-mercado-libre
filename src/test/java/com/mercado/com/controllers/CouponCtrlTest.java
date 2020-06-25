package com.mercado.com.controllers;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@RunWith ( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@ActiveProfiles ( profiles = "non-async" )
class CouponCtrlTest {
	
	private CountDownLatch lock = new CountDownLatch ( 1 );
	
	/**
	 * Ordenar los items de menor a mayor precio
	 */
	@Test
	public void sortedListByValue ( ) {
/*		Map < String, Float > items = new HashMap <> ( );
		items.put ( "MLA2", 210f );
		items.put ( "MLA5", 90f );
		items.put ( "MLA3", 260f );
		items.put ( "MLA1", 100f );
		items.put ( "MLA4", 80f );
		
		Map < String, Float > itemsExpected = new HashMap <> ( );
		itemsExpected.put ( "MLA3", 260f );
		itemsExpected.put ( "MLA2", 210f );
		itemsExpected.put ( "MLA1", 100f );
		itemsExpected.put ( "MLA5", 90f );
		itemsExpected.put ( "MLA4", 80f );*/
		
		Map < String, Float > items = new HashMap <> ( );
		items.put ( "MLA2", 333.99f );
		items.put ( "MLA5", 98.7f );
		items.put ( "MLA3", 26.5f );
		items.put ( "MLA1", 133.98f );
		items.put ( "MLA4", 5.3f );
		
		Map < String, Float > itemsExpected = new HashMap <> ( );
		itemsExpected.put ( "MLA3", 26.5f );
		itemsExpected.put ( "MLA4", 5.3f );
		itemsExpected.put ( "MLA5", 98.7f );
		itemsExpected.put ( "MLA1", 133.98f );
		itemsExpected.put ( "MLA2", 333.99f );
		
		
		Map < String, Float > dataSorted = CouponCtrl.sortedMap ( items );
		Assert.assertEquals ( itemsExpected, dataSorted );
	}
	
	/**
	 * Maximizar la compra con la cantidad de items y el monto deseado
	 */
	@Test
	public void calculateCoupon ( ) {
		Map < String, Float > items = new HashMap <> ( );
		items.put ( "MLA1", 100f );
		items.put ( "MLA2", 210f );
		items.put ( "MLA3", 260f );
		items.put ( "MLA4", 80f );
		items.put ( "MLA5", 90f );
		
		List < String > dataSorted = CouponCtrl.calculate ( items, 500f );
		List < String > dataExpected = Arrays.asList ( "MLA1", "MLA2", "MLA4", "MLA5" );
		Assert.assertEquals ( dataExpected, dataSorted );
	}
	
	/**
	 * Ordenamiento de los items por su nombre de manera alfabetica
	 */
	@Test
	public void sortedItems ( ) {
		List < String > items = new ArrayList < String > ( );
		items.add ( "MLA3" );
		items.add ( "MLA2" );
		items.add ( "MLA1" );
		items.add ( "MLA5" );
		items.add ( "MLA4" );
		;
		List < String > dataSorted = CouponCtrl.sortedItems ( items );
		List < String > dataExpected = Arrays.asList ( "MLA1", "MLA2", "MLA3", "MLA4", "MLA5" );
		Assert.assertEquals ( dataExpected, dataSorted );
		
	}
	
	/**
	 * Realizar la busqueda de un item en el API
	 */
	@Test
	public void consultarProducto ( ) throws IOException {
		CouponCtrl couponCtrl = new CouponCtrl ( );
		String product = "MLA805281803";
		String productExpected = "{id:MLA805281803,price:13999}";
		JSONObject productJSON = couponCtrl.consultarProducto ( product );
		JSONObject productResult = new JSONObject ( );
		productResult.put ( "id", productJSON.get ( "id" ) );
		productResult.put ( "price", productJSON.get ( "price" ) );
		JSONAssert.assertEquals ( productExpected, productResult, true );
	}
	
	/**
	 * Lista de items encontrados en el API
	 */
	@Test
	public void listaProductosEncontrados ( ) throws IOException {
		CouponCtrl couponCtrl = new CouponCtrl ( );
		List < String > listaProductosBuscar = Arrays.asList ( "MLA805281803", "MLA860477515" );
		Map < String, Float > listaProductosEncontrados = couponCtrl.productosEncontrados ( listaProductosBuscar );
		
		Map < String, Float > itemsExpected = new HashMap <> ( );
		itemsExpected.put ( "MLA860477515", 25999f );
		itemsExpected.put ( "MLA805281803", 13999f );
		
		Assert.assertEquals ( itemsExpected, listaProductosEncontrados );
	}
	
	/**
	 * totalizar los items del coupon
	 */
	@Test
	public void totalCouponUsed ( ) {
		List < String > listaProductos = Arrays.asList ( "MLA805281803", "MLA860477515" );
		Map < String, Float > listaProductosEncontrados = new HashMap <> ( );
//		listaProductosEncontrados.put ( "MLA860477515", 23999f );
//		listaProductosEncontrados.put ( "MLA805281803", 13499f );
		listaProductosEncontrados.put ( "MLA860477515", 600f );
		listaProductosEncontrados.put ( "MLA805281803", 750f );
		Float total = CouponCtrl.totalProductosCoupon ( listaProductos, listaProductosEncontrados );
//		Float totalExpected = 37498f;
		Float totalExpected = 1350f;
		Assert.assertEquals ( totalExpected, total );
	}
	
	/**
	 * Creacion del JSON de respuesta
	 */
	@Test
	public void createJSONResponse ( ) {
		List < String > listaProductosCoupon = Arrays.asList ( "MLA805281803", "MLA860477515" );
		Float total = 500f;
		String jsonExpected = "{item_ids:[MLA805281803,MLA860477515], total: 500}";
		;
		JSONObject jsonResponse = new JSONObject ( );
		jsonResponse.put ( "item_ids", listaProductosCoupon );
		jsonResponse.put ( "total", total );
		
		JSONAssert.assertEquals ( jsonExpected, jsonResponse, true );
	}
	
}