package com.mercado.com.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonInclude ( JsonInclude.Include.NON_NULL )
public class ItemsCoupon {
	@JsonProperty ( "item_ids" )
	@ApiModelProperty ( example = " [\"MLA860477515\", \"MLA805281803\"]" )
	List < String > itemIds;
	Float total;
	@ApiModelProperty ( example = "40000" )
	Float amount;
	
	public List < String > getItemIds ( ) {
		return itemIds;
	}
	
	public void setItemIds ( List < String > itemIds ) {
		this.itemIds = itemIds;
	}
	
	public Float getTotal ( ) {
		return total;
	}
	
	public void setTotal ( Float total ) {
		this.total = total;
	}
	
	public Float getAmount ( ) {
		return amount;
	}
	
	public void setAmount ( Float amount ) {
		this.amount = amount;
	}
}
