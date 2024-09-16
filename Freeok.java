package nea.freeok.api;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;

public class Freeok
{
	public static final String BASE_URL = "http://freeok.vip";
	public static final String MOVIE = "/v-type/1.html", OPERA = "/v-type/2.html", ANIME = "/v-type/3.html", SHOW = "/v-type/4.html", CARTOON = "/v-type/5.html";

	public static void main (String[] args) throws Throwable
	{
	}

	public static VodInfo[] search (String query, int page) throws Exception
	{
		String url = String.format(BASE_URL + "/so1so/%s----------%s---.html", query, page);
		BufferedReader in = openBufferedReader(url);
		String line, target = "<div class=\"module-card-item module-item\">";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		List<VodInfo> resultList = new ArrayList<>();
		while ((line = in.readLine()) != null)
		{
			if (! line.startsWith(target)) continue;
			VodInfo item = new VodInfo();
			in.readLine();
			line = in.readLine() + "</a>";
			Document doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
			item.vod = doc.getDocumentElement().getAttribute("href");
			in.readLine();
			in.readLine();
			line = in.readLine();
			StringBuilder buffer = new StringBuilder(line);
			buffer.insert(buffer.length() - 7, '/');
			line = buffer.toString();
			doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
			NamedNodeMap map = doc.getDocumentElement().getChildNodes().item(0).getAttributes();
			item.pic = url(map.getNamedItem("data-original").getNodeValue());
			item.title = map.getNamedItem("alt").getNodeValue();
			resultList.add(item);
		}
		in.close();
		return resultList.toArray(new VodInfo[resultList.size()]);
	}

	public static FilterList[] getFilter (String path) throws Exception
	{
		String url = BASE_URL + path;
		BufferedReader in = openBufferedReader(url);
		List<FilterList> result = new ArrayList<>();
		String line, target = "<div class=\"module-item-title\">";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		while ((line = in.readLine()) != null)
		{
			if (line.startsWith(target))
			{
				Document doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
				FilterList filterList = new FilterList();
				filterList.title = doc.getDocumentElement().getTextContent();
				StringBuilder buffer = new StringBuilder();
				while ((line = in.readLine()) != null && !"</div>".equals(line))
				{
					buffer.append(line);
				}
				buffer.append(line);
				doc = db.parse(new ByteArrayInputStream(buffer.toString().getBytes("utf-8")));
				NodeList nodes = doc.getElementsByTagName("a");
				FilterListItem[] items = new FilterListItem[nodes.getLength()];
				for (int i = 0; i < items.length; i ++)
				{
					FilterListItem item = new FilterListItem();
					items[i] = item;
					Node node = nodes.item(i);
					item.title = node.getTextContent();
					item.vod = node.getAttributes().getNamedItem("href").getNodeValue();
				}
				filterList.items = items;
				result.add(filterList);
			}
		}
		in.close();
		return result.toArray(new FilterList[result.size()]);
	}

	public static Object[] getFilterContent (String path) throws Exception
	{
		String url = BASE_URL + path;
		BufferedReader in = openBufferedReader(url);
		Object[] result = new Object[2];
		String line, target = "<a href=\"/vod-detail/", target1 = "<div class=\"module-item-pic", target2 = "<div id=\"page\">";
		List<VodInfo> result2 = new ArrayList<>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		while ((line = in.readLine()) != null && ! target2.equals(line))
		{
			if (! line.startsWith(target)) continue;
			Document doc = db.parse(new ByteArrayInputStream((line + "</a>").getBytes("utf-8")));
			Element ele = doc.getDocumentElement();
			VodInfo item = new VodInfo();
			item.title = ele.getAttribute("title");
			item.vod = ele.getAttribute("href");
			while ((line = in.readLine()) != null)
			{
				if (line.startsWith(target1))
				{
					StringBuilder buffer = new StringBuilder(line);
					buffer.insert(buffer.length() - 7, '/');
					line = buffer.toString();
					doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
					NamedNodeMap map = doc.getDocumentElement().getChildNodes().item(0).getAttributes();
					item.pic = url(map.getNamedItem("data-original").getNodeValue());
					break;
				}
			}
			result2.add(item);
		}
		target = "<a href=\"/vod-show/";
		target1 = "page-link page-next";
		while ((line = in.readLine()) != null)
		{
			if (! line.startsWith(target)) continue;
			Document doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
			Element ele = doc.getDocumentElement();
			if (! target1.equals(ele.getAttribute("class"))) continue;
			result[1] = ele.getAttribute("href");
			line = in.readLine();
			doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
			ele = doc.getDocumentElement();
			if (ele.getAttribute("href").equals(result[1]))
			{
				result[1] = null;
			}
			break;
		}
		in.close();
		result[0] = result2.toArray(new VodInfo[result2.size()]);
		return result;
	}

	public static String url (String url)
	{
		if (url.startsWith("/"))
		{
			return BASE_URL + url;
		}
		return url;
	}

	public static HttpURLConnection openConnection(String url) throws Exception
	{
		URLConnection con = new URL(url).openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13; PJU110 Build/TP1A.220905.001) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.129 Mobile Safari/537.36");
		return (HttpURLConnection)con;
	}

	public static BufferedReader openBufferedReader(String url) throws Exception
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(openConnection(url).getInputStream(), "UTF-8"));
		return in;
	}
};
