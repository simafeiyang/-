package com.lg.pacong;
//测试
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Car {
	public static void main(String[] args) throws Exception {
		// "http://weixin.sogou.com/pcindex/pc/pc_7/pc_7.html"

		List<String> address = new ArrayList<String>();
		List<String> titles = new ArrayList<String>();

		// 获取文章url地址集合
		Match(GetHTML("weixin.sogou.com/pcindex/pc/pc_7/pc_7.html"), address, titles);

		// 获取当前的时间日期
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		// 循环抓取到的文章地址
		for (int i = 0; i < address.size(); i++) {
			MatchArticle(GetHTML(address.get(i)), titles.get(i), date);
		}

		// String str =
		// GetHTML("mp.weixin.qq.com/s?src=11&timestamp=1521871955&ver=773&signature=4IhQLHspQaMZNNLKLPP5XQtC2vQMIDacV89MXjvbloqVwjJLvNA8cMOojKG5Rb2qmaygZPERsZWQqSBDFmSZyiQMsOiJiRdkxqYgaBlpTDwaVUT9Stw9OjH7TOydvoC6&new=1");
		// Match(str);

		for (int j = 1; j < 100; j++) {
			Match(GetHTML("weixin.sogou.com/pcindex/pc/pc_7/" + j + ".html"), address, titles);

			boolean flag = false;
			// 获取当前的时间日期
			String date2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

			// 循环抓取到的文章地址
			for (int i = 0; i < address.size(); i++) {
				flag = MatchArticle(GetHTML(address.get(i)), titles.get(i), date2);
			}

			if (flag) {
				return;
			}
		}
	}

	// 获取网站的html文本
	public static String GetHTML(String website) throws Exception {
		URL url = new URL("http://" + website);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT; DigExt)");

		InputStream input = conn.getInputStream();

		// translate inputstream to bufferedreader
		BufferedReader br = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Users\\Administrator\\Desktop\\Car\\html.txt"));
		String str = "";
		String result = "";
		while ((str = br.readLine()) != null) {
			bw.write(str);
			bw.newLine();
			// System.out.println(str);
			result += str + "\n";
		}

		bw.flush();
		bw.close();
		br.close();
		return result;

	}

	// 匹配对应的字符串
	public static void Match(String result, List<String> address, List<String> titles) throws IOException {

		// 定义数组集合，存放要抓去的文章地址集合

		String needInfo = "";

		String urlAndTitle = "<a uigs=\"pc_.+</a>";
		Matcher matUrlAndTitle = Pattern.compile(urlAndTitle).matcher(result);
		while (matUrlAndTitle.find()) {
			// System.out.println(URLmat.start()+"--"+URLmat.end()+"--"+URLmat.group());
			// GetPic(mat.group());
			needInfo += (matUrlAndTitle.group()) + "\n";
		}

		String URLreg = "mp.weixin.qq.com/s\\?src=.+w=1";
		// String URLreg =
		// "\\pP*[\u4e00-\u9fa5]+d*[\\pP*[\u4e00-\u9fa5]+d*w*]*\\pP*[\u4e00-\u9fa5]+\\pP*";

		String titleReg = "=\">.+</a>";

		Matcher matTitle = Pattern.compile(titleReg).matcher(needInfo);
		while (matTitle.find()) {
			String title = matTitle.group().replaceAll("=\">|</a>|\\pP*|\\|", "");

			title = title.replace("|", "");
			// GetPic(mat.group());

			// 创建该标题的文件夹
			File file = new File("C:\\Users\\Administrator\\Desktop\\Car\\" + title);

			boolean flag = file.mkdir();

			System.out.println(title + "make " + flag);
			titles.add(title);
		}

		Matcher URLmat = Pattern.compile(URLreg).matcher(needInfo);
		while (URLmat.find()) {
			// System.out.println(URLmat.start()+"--"+URLmat.end()+"--"+URLmat.group());
			// GetPic(mat.group());
			address.add(URLmat.group());
		}

	}

	public static boolean MatchArticle(String result, String title, String date) throws Exception {

		// String reg = "mp.weixin.qq.com/s\\?src=.+w=1";

		// 判断文章日期是否为今天

		boolean flag = Pattern.compile(date).matcher(result) != null;

		if (flag) {

			String reg = "\\pP*[\u4e00-\u9fa5]+d*[\\pP*[\u4e00-\u9fa5]+d*w*]*\\pP*[\u4e00-\u9fa5]+\\pP*";

			Matcher mat = Pattern.compile(reg).matcher(result);

			// 创建字符输出缓冲流
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					new File("C:\\Users\\Administrator\\Desktop\\Car\\" + title + "\\" + title + ".txt")));

			// 将匹配到的文章内容写入txt文档
			while (mat.find()) {
				bw.write(mat.group());
				bw.newLine();
				// GetPic(mat.group());

			}

			bw.flush();
			bw.close();

			// 匹配图片
			String pic = "mmbiz.qpic.cn/mmbiz.{1,200}wx_fmt=(png|gif)";

			Matcher matPic = Pattern.compile(pic).matcher(result);

			// 创建一个字符串集合存放匹配到的图片地址

			ArrayList<String> pics = new ArrayList<String>();

			while (matPic.find()) {

				pics.add(matPic.group());
			}

			// 循环集合，依次下载图片

			for (int i = 0; i < pics.size(); i++) {

				if (pics.get(i).endsWith("png")) {
					File file = new File("C:\\Users\\Administrator\\Desktop\\Car\\" + title + "\\" + i + ".png");
					GetPic(pics.get(i), file);
				} else {
					File file = new File("C:\\Users\\Administrator\\Desktop\\Car\\" + title + "\\" + i + ".gif");
					GetPic(pics.get(i), file);
				}
			}

		}

		return flag;

	}

	public static void GetPic(String pic, File file) throws Exception {
		URL url = new URL("http://" + pic);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT; DigExt)");

		InputStream input = conn.getInputStream();

		BufferedInputStream bis = new BufferedInputStream(input);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

		byte[] buffer = new byte[1024];

		int len = -1;

		while ((len = bis.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}

		bos.flush();
		bis.close();
		bos.close();

	}

}
