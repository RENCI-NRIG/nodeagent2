package orca.nodeagent2.client;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import orca.nodeagent2.agentlib.PluginReturn;
import orca.nodeagent2.agentlib.Properties;
import orca.nodeagent2.agentlib.ReservationId;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This is a simple client that shows how to make various calls in Java using
 * minimal library dependencies (httpclient and JSON-simple)
 * @author ibaldin
 *
 */
public class RestClient {
	private static final String HTTP_REST_JOIN_URL = "http://localhost:8080/join/Null-Test-Plugin";
	private static final String HTTP_REST_LEAVE_URL = "http://localhost:8080/leave/Null-Test-Plugin";
	private static final String HTTP_REST_STATUS_URL = "http://localhost:8080/status/Null-Test-Plugin";
	private static final String HTTP_REST_PLUGINS_URL = "http://localhost:8080/plugins/Null-Test-Plugin";

	public PluginReturn convert(JSONObject o) {
		long status = (Long)o.get("status");
		int st = (int)status;

		try {
			JSONObject ridEnvelope = (JSONObject)o.get("resId");
			ReservationId rid = new ReservationId((String)ridEnvelope.get("id"));

			Properties props = null;
			if (o.get("properties") != null) {
				props = new Properties();
				props.putAll((JSONObject)o.get("properties"));
			}

			PluginReturn pr = new PluginReturn(st, (String)o.get("errorMsg"), rid, props);
			return pr;
		} catch (Exception e) {
			System.out.println("Unable to decode " + o + " " + e);
			e.printStackTrace();
			return null;
		}		
	}

	/**
	 * How to do POSTs (like join)
	 * @throws Exception
	 */
	public void testPostJoin() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().
		setCredentials(
				new AuthScope("localhost", 8080),
				new UsernamePasswordCredentials("user", 
						"password"));

		HttpPost post = new HttpPost(HTTP_REST_JOIN_URL);

		//List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
		//nameValuePairs.add(new BasicNameValuePair("name", "value")); //you can as many name value pair as you want in the list.

		JSONObject props = new JSONObject();

		props.put("property-one", "some value one");
		props.put("property two", "some value two");

		System.out.println("Sending " + props.toString());

		StringEntity se = new StringEntity(props.toString());
		se.setContentType("application/json");


		//nameValuePairs.add(new BasicNameValuePair("props", ))

		post.setEntity(se);

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) { 
			sb.append(line);
			//System.out.println(line);
		}
		JSONObject o = (JSONObject)JSONValue.parse(sb.toString());
		System.out.println(convert(o));

	}

	/**
	 * How to do POSTs (like leave/modify)
	 * @throws Exception
	 */
	public void testPostLeave() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().
		setCredentials(
				new AuthScope("localhost", 8080),
				new UsernamePasswordCredentials("user", 
						"password"));

		HttpPost post = new HttpPost(HTTP_REST_LEAVE_URL);

		//List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
		//nameValuePairs.add(new BasicNameValuePair("name", "value")); //you can as many name value pair as you want in the list.

		JSONObject params = new JSONObject();

		params.put("id", "some-id-of-reservation");

		JSONObject props = new JSONObject();

		props.put("property one", "some value one");
		props.put("property two", "some value two");

		params.put("props", props);

		System.out.println(params.toString());

		StringEntity se = new StringEntity(params.toString());
		se.setContentType("application/json");

		post.setEntity(se);

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) { 
			sb.append(line);
			System.out.println(line);
		}
		JSONObject o = (JSONObject)JSONValue.parse(sb.toString());
		System.out.println(convert(o));
	}

	/**
	 * How to do GETs (status)
	 * @throws Exception
	 */
	public void testStatus() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().
		setCredentials(
				new AuthScope("localhost", 8080),
				new UsernamePasswordCredentials("user", 
						"password"));

		HttpGet get = new HttpGet(HTTP_REST_STATUS_URL);

		HttpResponse response = client.execute(get);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) { 
			sb.append(line);
			//System.out.println(line);
		}
		
		System.out.println(sb.toString());
	}

	/**
	 * How to do GETs (status)
	 * @throws Exception
	 */
	public void testPlugins() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().
		setCredentials(
				new AuthScope("localhost", 8080),
				new UsernamePasswordCredentials("user", 
						"password"));

		HttpGet get = new HttpGet(HTTP_REST_PLUGINS_URL);

		HttpResponse response = client.execute(get);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null) { 
			sb.append(line);
			//System.out.println(line);
		}
		if (sb.length() == 0)
			System.out.println("Plugin not found");
		else
			System.out.println(sb.toString());
	}
	
	public static void main(String[] args) throws  Exception {
		RestClient rc = new RestClient();

		//rc.testPostLeave();
		rc.testPlugins();
	}
}

