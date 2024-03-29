package json.interpreter;

public class RunInterpreter {
	
	public static void main(String[] args) throws Exception {
		JsonGenerator generator = new JsonGenerator("YTMP3AudioInfo", test);
		String result = generator.interpret().toString();
		System.out.println(result);
	}
	
	private static final String test = "{\r\n"
			+ "    \"error\": false,\r\n"
			+ "    \"status\": \"OK\",\r\n"
			+ "    \"formats\": {\r\n"
			+ "        \"id\": \"c4547eeeb371abe63e83ad2a9c3aa079\",\r\n"
			+ "        \"title\": \"Auto Attack Spacing Study deft vs viper\",\r\n"
			+ "        \"basename\": \"Auto Attack Spacing Study deft vs viper\",\r\n"
			+ "        \"duration\": 10,\r\n"
			+ "        \"thumbnail\": \"https://i.ytimg.com/vi_webp/-6VI1Pmq9Ss/maxresdefault.webp\",\r\n"
			+ "        \"video\": [\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"160140\",\r\n"
			+ "                \"quality\": \"144p\",\r\n"
			+ "                \"fileSize\": 280680,\r\n"
			+ "                \"fileType\": \"mp4\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=160140\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"133140\",\r\n"
			+ "                \"quality\": \"240p\",\r\n"
			+ "                \"fileSize\": 455848,\r\n"
			+ "                \"fileType\": \"mp4\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=133140\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"18\",\r\n"
			+ "                \"quality\": \"360p\",\r\n"
			+ "                \"fileSize\": 733343,\r\n"
			+ "                \"fileType\": \"mp4\",\r\n"
			+ "                \"url\": \"https://rr3---sn-5goeen7y.googlevideo.com/videoplayback?expire=1651289876&ei=tFpsYur0NYWO1wbD9LaIAQ&ip=45.192.136.147&id=o-ANf_2VR47SQD4-JSkY0P_3dsvecT6yqwwWUoFGGeYqwf&itag=18&source=youtube&requiressl=yes&mh=sO&mm=31%2C26&mn=sn-5goeen7y%2Csn-4g5lznes&ms=au%2Conr&mv=m&mvi=3&pl=24&initcwndbps=927500&vprv=1&mime=video%2Fmp4&gir=yes&clen=733343&ratebypass=yes&dur=10.170&lmt=1626601081783290&mt=1651267983&fvip=1&fexp=24001373%2C24007246&c=ANDROID&txp=5319222&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAMy0JJMBJu7zkG7SWq4_uH627e-XNxBvOI-6ZN-oQuHYAiBMpFfSX8S1WW5QRV7JU7--sWVGvh3XqlSVh7sJvUzyOw%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgOfDLdzB96s8IB-f4t24kRH8GDW4owL5Qe5R0TkG0X3YCIBzMjS1V71-YjE9yXy0CEghWrdhdKXEW9Zyp4daaZthG&title=OnlyMP3.to%20-%20Auto%20Attack%20Spacing%20Study%20deft%20vs%20viper--6VI1Pmq9Ss-360p-1651268278287\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": false\r\n"
			+ "                },\r\n"
			+ "                \"filename\": \"OnlyMP3.to - Auto Attack Spacing Study deft vs viper--6VI1Pmq9Ss-360p-1651268278287.mp4\",\r\n"
			+ "                \"needConvert\": false\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"135140\",\r\n"
			+ "                \"quality\": \"480p\",\r\n"
			+ "                \"fileSize\": 1267158,\r\n"
			+ "                \"fileType\": \"mp4\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=135140\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"22\",\r\n"
			+ "                \"quality\": \"720p\",\r\n"
			+ "                \"fileSize\": 2356615,\r\n"
			+ "                \"fileType\": \"mp4\",\r\n"
			+ "                \"url\": \"https://rr3---sn-5goeen7y.googlevideo.com/videoplayback?expire=1651289876&ei=tFpsYur0NYWO1wbD9LaIAQ&ip=45.192.136.147&id=o-ANf_2VR47SQD4-JSkY0P_3dsvecT6yqwwWUoFGGeYqwf&itag=22&source=youtube&requiressl=yes&mh=sO&mm=31%2C26&mn=sn-5goeen7y%2Csn-4g5lznes&ms=au%2Conr&mv=m&mvi=3&pl=24&initcwndbps=927500&vprv=1&mime=video%2Fmp4&cnr=14&ratebypass=yes&dur=10.170&lmt=1626601086353255&mt=1651267983&fvip=1&fexp=24001373%2C24007246&c=ANDROID&txp=5316222&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIgXL68wONLfGSURudRvCA1z-9glrLNDwecmKRccsu2BLkCIQCNXy654KiC4sbPaiY-Jrlgu9xPHb4sOT-eoM_XgKcJNQ%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgOfDLdzB96s8IB-f4t24kRH8GDW4owL5Qe5R0TkG0X3YCIBzMjS1V71-YjE9yXy0CEghWrdhdKXEW9Zyp4daaZthG&title=OnlyMP3.to%20-%20Auto%20Attack%20Spacing%20Study%20deft%20vs%20viper--6VI1Pmq9Ss-720p-1651268278287\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": false\r\n"
			+ "                },\r\n"
			+ "                \"filename\": \"OnlyMP3.to - Auto Attack Spacing Study deft vs viper--6VI1Pmq9Ss-720p-1651268278287.mp4\",\r\n"
			+ "                \"needConvert\": false\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"299140\",\r\n"
			+ "                \"quality\": \"1080p\",\r\n"
			+ "                \"fileSize\": 5893698,\r\n"
			+ "                \"fileType\": \"mp4\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=299140\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            }\r\n"
			+ "        ],\r\n"
			+ "        \"audio\": [\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"139003\",\r\n"
			+ "                \"quality\": \"48k\",\r\n"
			+ "                \"fileSize\": 61440,\r\n"
			+ "                \"fileType\": \"mp3\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=139003\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"250003\",\r\n"
			+ "                \"quality\": \"64k\",\r\n"
			+ "                \"fileSize\": 81920,\r\n"
			+ "                \"fileType\": \"mp3\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=250003\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"140003\",\r\n"
			+ "                \"quality\": \"128k\",\r\n"
			+ "                \"fileSize\": 163840,\r\n"
			+ "                \"fileType\": \"mp3\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=140003\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"formatId\": \"251003003\",\r\n"
			+ "                \"quality\": \"160k\",\r\n"
			+ "                \"fileSize\": 204800,\r\n"
			+ "                \"fileType\": \"mp3\",\r\n"
			+ "                \"url\": \"https://srv8.onlymp3.to/convert?id=c4547eeeb371abe63e83ad2a9c3aa079&formatId=251003003\",\r\n"
			+ "                \"description\": {\r\n"
			+ "                    \"fragment\": \"No fragment\",\r\n"
			+ "                    \"block\": true\r\n"
			+ "                },\r\n"
			+ "                \"needConvert\": true\r\n"
			+ "            }\r\n"
			+ "        ]\r\n"
			+ "    }\r\n"
			+ "}";
}
