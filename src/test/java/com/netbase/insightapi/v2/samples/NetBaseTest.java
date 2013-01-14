package com.netbase.insightapi.v2.samples;

import static org.junit.Assert.*;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.netbase.insightapi.v2.NetBase;
import com.netbase.insightapi.v2.NetBaseFactory;
import com.netbase.insightapi.v2.Request;
import com.netbase.insightapi.v2.Response;

public class NetBaseTest {
	
	protected NetBase nb = null;
	
	public NetBase getNetBase(boolean production) throws Exception {
		if (nb == null) {
			nb = NetBaseFactory.getInstance("apimgmt@netbase.com", "net1base", production);
		}
		return nb;
	}

	@Test
	public void testProfile() throws Exception {
		// Tests if the userId returned in the response is - 4516
		
		Response response = getNetBase(true).profile(new Request());
		assertEquals("4516", ((JSONObject) response.getJSON()).getString("userId"));
	}

}
