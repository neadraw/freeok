package nea.freeok.api;
import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class VodInfo
implements Serializable
{
	public String vod, title, pic;

	public PlayList[] getPlayLists () throws Exception
	{
		String url = Freeok.BASE_URL + vod;
		BufferedReader in = Freeok.openBufferedReader(url);
		String line;
		List<PlayList> lists = new ArrayList<>();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		int listsIndex = 0;
		while ((line = in.readLine()) != null)
		{
			if (line.startsWith("<div class=\"module-tab-items-box"))
			{
				while ((line = in.readLine()) != null && ! "</div>".equals(line))
				{
					Document doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
					String title = doc.getDocumentElement().getAttribute("data-dropdown-value");
					PlayList list = new PlayList();
					list.title = title;
					lists.add(list);
				}
			}
			if (line.startsWith("<div class=\"module-play-list-content"))
			{
				PlayList list = lists.get(listsIndex);
				List<PlayListItem> items = new ArrayList<>();
				while ((line = in.readLine()) != null && ! "</div>".equals(line))
				{
					Document doc = db.parse(new ByteArrayInputStream(line.getBytes("utf-8")));
					PlayListItem item = new PlayListItem();
					item.vod = doc.getDocumentElement().getAttribute("href");
					item.title = doc.getElementsByTagName("span").item(0).getTextContent();
					items.add(item);
					list.items = items.toArray(new PlayListItem[items.size()]);
				}
				listsIndex ++;
			}
		}
		in.close();
		return lists.toArray(new PlayList[lists.size()]);
	}
}
