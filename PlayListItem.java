package nea.freeok.api;
import java.io.*;
import org.json.*;
import java.net.*;

public class PlayListItem implements Serializable
{
	public String title, vod;

	public String getVideoAddress () throws Exception
	{
		String url = Freeok.BASE_URL + vod;
		BufferedReader in = Freeok.openBufferedReader(url);
		String line, target = "<script type=\"text/javascript\">var player_aaaa=";
		while ((line = in.readLine()) != null)
		{
			if (! line.startsWith(target)) continue;
			line = line.substring(target.length());
			JSONObject json = new JSONObject(line);
			in.close();
			return URLDecoder.decode(json.getString("url"), "utf-8");
		}
		in.close();
		return null;
	}
}
