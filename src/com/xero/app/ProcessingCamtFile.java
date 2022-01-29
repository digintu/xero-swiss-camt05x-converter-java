package com.xero.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.xero.api.ApiClient;
import com.xero.api.client.IdentityApi;
import com.xero.app.models.Document;
import com.xero.app.models.ObjectFactory;
import com.xero.app.models.ReportEntry2;
import com.xero.models.identity.Connection;

// Get camt file and convert to CSV
@WebServlet(urlPatterns = "/upload")
@MultipartConfig
public class ProcessingCamtFile extends HttpServlet {

	private static final long serialVersionUID = 1273074928096412095L;

	public static final String FILES_FOLDER = "/Images";

	public String uploadPath;

	public ProcessingCamtFile() {
		super();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

		Part part = null;

		if (request.getParts().size() > 0)
			part = request.getParts().iterator().next();
		else
			return;

		String fileName = getFileName(part);
		// String fullPath = uploadPath + File.separator + fileName;
		System.out.println("Filename => " + fileName);
		System.out.println("p.getName() => " + part.getName());
		// part.write(fullPath);

		try {
			System.out.println(System.getProperty("user.dir"));
			File stylesheet = new File(
					"/home/descartes/eclipse/workspace/xero-swiss-camt05x-converter-java/src/com/xero/converter/process.xsl");
			System.out.println("Premier fichier lu");
			File xmlSource = new File(
					"/home/descartes/eclipse/workspace/xero-swiss-camt05x-converter-java/src/com/xero/converter/camt.053.xml");
			System.out.println("Deuxieme fichier lu");

			/*
			 * DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			 * DocumentBuilder builder = factory.newDocumentBuilder(); Document document =
			 * builder.parse(xmlSource);
			 * 
			 * StreamSource stylesource = new StreamSource(stylesheet); Transformer
			 * transformer = TransformerFactory.newInstance() .newTransformer(stylesource);
			 * Source source = new DOMSource(document); Result outputTarget = new
			 * StreamResult(new File("/home/descartes/Desktop/x.csv"));
			 * transformer.transform(source, outputTarget);
			 */

			try {

				FileInputStream fileInputStream = new FileInputStream(new File("/home/descartes/eclipse/workspace/xero-swiss-camt05x-converter-java/src/com/xero/converter/camt.053.xml"));

				JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

				Unmarshaller unmarshaller = jc.createUnmarshaller();
				Document document = ((JAXBElement<Document>) unmarshaller.unmarshal(fileInputStream)).getValue();

				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				List<Map<String, String>> entries = new ArrayList<>();

				List<ReportEntry2> list = document.getBkToCstmrStmt().getStmt().get(0).getNtry();
				
				Integer i = 1;
				for (ReportEntry2 ntry : list) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("date", ntry.getValDt().getDt().toString());
					item.put("amount", ntry.getAmt().getValue().toString());
					item.put("payee", "Payee");
					item.put("position", i.toString());
					item.put("description", ntry.getAddtlNtryInf());
					item.put("reference", ntry.getAcctSvcrRef());
					item.put("code", ntry.getAmt().getValue().intValue() > 0 ? "CRT" : "DBT");
					entries.add(item);
					i++;
				}
				
				HttpSession session = request.getSession();

	            session.setAttribute("entries", entries);
	            
	            if(entries.size() > 0) {
	            	session.setAttribute("currentEntry", entries.get(0));
	            }
	            
	            session.setMaxInactiveInterval(30*60);
	            

			} catch (JAXBException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		resp.sendRedirect("./reconciliation");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ApiClient defaultIdentityClient = new ApiClient("https://api.xero.com", null, null, null, null);
		IdentityApi idApi = new IdentityApi(defaultIdentityClient);
		TokenStorage store = new TokenStorage();
		List<Connection> connections = idApi.getConnections(store.get(request, "access_token"), null);
		if (connections.size() > 0)
			for (Connection connection : connections)
				idApi.deleteConnection(store.get(request, "access_token"), connection.getId());
		response.sendRedirect("/xero_java_oauth2_app_war");
	}

	private String getFileName(Part part) {
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename"))
				return content.substring(content.indexOf("=") + 2, content.length() - 1);
		}
		return "Default.file";
	}

}