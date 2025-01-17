package com.xero.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import com.google.gson.Gson;
import com.xero.api.ApiClient;
import com.xero.api.client.AccountingApi;
import com.xero.api.client.IdentityApi;
import com.xero.app.models.CreditDebitCode;
import com.xero.app.models.Document;
import com.xero.app.models.ObjectFactory;
import com.xero.app.models.ReportEntry2;
import com.xero.models.accounting.Account;
import com.xero.models.accounting.Accounts;
import com.xero.models.accounting.Contacts;
import com.xero.models.identity.Connection;

// Get camt file and convert to CSV
@WebServlet(urlPatterns = "/upload")
@MultipartConfig
public class ProcessingCamtFile extends HttpServlet {

	public String uploadPath;
	private Gson gson = new Gson();
	private AccountingApi accountingApi = null;
	public static final String FILES_FOLDER = "/Images";
	private static final long serialVersionUID = 1273074928096412095L;

	public ProcessingCamtFile() {
		super();
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

		Part part = null;

		if (request.getParts().size() > 0)
			part = request.getParts().iterator().next();
		else
			return;

		try {

			HttpSession session = request.getSession(false);

			ApiClient defaultClient = new ApiClient();
			defaultClient.setConnectionTimeout(6000);
			accountingApi = AccountingApi.getInstance(defaultClient);

			Accounts accounts = accountingApi.getAccounts(session.getAttribute("access_token").toString(),
					session.getAttribute("xero_tenant_id").toString(), null, null, null);

			List<Account> accountList = accounts.getAccounts().stream()
					.filter(a -> "BANK".equals(a.getType().toString().toString())).collect(Collectors.toList());
			session.setAttribute("accounts", this.gson.toJson(accountList));
			session.setAttribute("accountsUnparsed", accountList);

			XMLInputFactory xif = XMLInputFactory.newFactory();
			xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

			StreamSource source = new StreamSource(part.getInputStream());
			XMLStreamReader xsr = xif.createXMLStreamReader(source);

			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);

			Unmarshaller unmarshaller = jc.createUnmarshaller();

			Document document = ((JAXBElement<Document>) unmarshaller.unmarshal(xsr)).getValue();

			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			List<List<Map<String, String>>> entries = new ArrayList<>();

			List<ReportEntry2> list;
			if (document.getBkToCstmrStmt() != null)
				list = document.getBkToCstmrStmt().getStmt().get(0).getNtry();
			else
				list = document.getBkToCstmrDbtCdtNtfctn().getNtfctn().get(0).getNtry();

			Integer i = 1;
			for (ReportEntry2 ntry : list) {
				List<Map<String, String>> item = new ArrayList<>();

				Map<String, String> date = new HashMap<String, String>();
				date.put("label", "Transaction Date");
				date.put("fieldName", "transactionDate");
				date.put("value", ntry.getValDt().getDt().toString());
				date.put("targetColumn", "transactionDate");
				item.add(date);

				Map<String, String> amount = new HashMap<String, String>();
				amount.put("label", "Amount");
				amount.put("fieldName", "amount");
				amount.put("value", ntry.getAmt().getValue().toString());
				amount.put("targetColumn", "transactionAmount");
				item.add(amount);

				Map<String, String> currency = new HashMap<String, String>();
				currency.put("label", "Currency");
				currency.put("fieldName", "Currency");
				currency.put("targetColumn", "currency");
				currency.put("value", ntry.getAmt().getCcy());
				item.add(currency);

				Map<String, String> payee = new HashMap<String, String>();
				payee.put("label", "Payee");
				payee.put("fieldName", "payee");
				try {
					payee.put("value", "");
//					payee.put("value",
//							ntry.getCdtDbtInd().equals(CreditDebitCode.CRDT)
//									? ntry.getNtryDtls().get(0).getTxDtls()
//											.get(ntry.getNtryDtls().get(0).getTxDtls().size() - 1).getRltdPties()
//											.getDbtr().getNm()
//									: ntry.getNtryDtls().get(0).getTxDtls()
//											.get(ntry.getNtryDtls().get(0).getTxDtls().size() - 1).getRltdPties()
//											.getCdtr().getNm());
				} catch (Exception e) {
					payee.put("value", "");
				}
				payee.put("targetColumn", "payee");
				item.add(payee);

				Map<String, String> description = new HashMap<String, String>();
				description.put("label", "Description");
				description.put("fieldName", "description");
				description.put("value", ntry.getAddtlNtryInf());
				description.put("targetColumn", "description");
				item.add(description);

				Map<String, String> reference = new HashMap<String, String>();
				reference.put("label", "Reference");
				reference.put("fieldName", "reference");
				reference.put("targetColumn", "reference");
				try {
					reference.put("value",
							ntry.getNtryDtls().get(0).getTxDtls().get(ntry.getNtryDtls().get(0).getTxDtls().size() - 1)
									.getRmtInf().getStrd().get(0).getCdtrRefInf().getRef() != null
											? ntry.getNtryDtls().get(0).getTxDtls()
													.get(ntry.getNtryDtls().get(0).getTxDtls().size() - 1).getRmtInf()
													.getStrd().get(0).getCdtrRefInf().getRef()
											: ntry.getNtryDtls().get(0).getTxDtls()
													.get(ntry.getNtryDtls().get(0).getTxDtls().size() - 1).getRmtInf()
													.getUstrd().get(0));
				} catch (Exception e) {
					reference.put("value", "");
				}

				item.add(reference);

				Map<String, String> code = new HashMap<String, String>();
				code.put("label", "Analysis code");
				code.put("fieldName", "AnalysisCode");
				code.put("targetColumn", "analysisCode");
				code.put("value", ntry.getCdtDbtInd().toString());
				item.add(code);

				Map<String, String> skip = new HashMap<String, String>();
				skip.put("label", "skip");
				skip.put("fieldName", "skip");
				skip.put("targetColumn", "skip");
				skip.put("value", "false");
				item.add(skip);

				entries.add(item);
				i++;
			}

			Contacts contactsAll = accountingApi.getContacts(session.getAttribute("access_token").toString(), session.getAttribute("xero_tenant_id").toString(), null, null, null, null, null, null, null, null);
			
			session.setAttribute("entries", this.gson.toJson(entries));
			session.setAttribute("contacts", this.gson.toJson(contactsAll.getContacts()));
			session.setAttribute("unparsedContacts", contactsAll.getContacts());
			session.setAttribute("size", entries.size());
			session.setMaxInactiveInterval(30 * 60);

			resp.sendRedirect("./reconciliation");
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect("./import-file");
		}

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
