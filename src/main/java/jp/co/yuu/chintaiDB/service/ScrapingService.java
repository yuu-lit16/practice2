package jp.co.yuu.chintaiDB.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import jp.co.yuu.chintaiDB.domain.ScrapingStatus;

/**
 * @author y.kimura
 */
@Service
public class ScrapingService {

	// --- 山手線を想定 ---
	// TODO : 山手線以外も都内は全部対応できるようにする。excuteメソッドでURLを渡せるようにするのが良いかも。
	String pageUrl = "https://suumo.jp/jj/chintai/ichiran/FR301FC001/?ar=030&bs=040&ra=013&cb=0.0&ct=9999999&et=9999999&cn=9999999&mb=0&mt=9999999&shkr1=03&shkr2=03&shkr3=03&shkr4=03&fw2=&rn=0005";
	final String SELECTOR = "div.cassetteitem_content-title";
	// TODO : Linuxデプロイ時にはパスを変更
	final String FILEPATH = "/Users/chintaiDB/";
	String fileNameStation = "yamanote_";
	String returnMsg = null;

	public ScrapingStatus excuteScraping() throws IOException {

		ScrapingStatus scrapingStatus = new ScrapingStatus();

		// ファイル名取得
		String fileName = getFileName(fileNameStation);

		// ファイル存在判定
		boolean isExistTodayFile = isExistFile(fileName);

		// 当日のファイルが存在する場合はリターン
		List<String> nameList = new ArrayList<String>();
		if (isExistTodayFile) {
			scrapingStatus.setMessage(fileName + "は既に存在します。処理を終了します。");
			scrapingStatus.setScraping(false);
			return scrapingStatus;
		} else {
			// ファイルが存在しない場合はスクレイピングして賃貸名リストを取得
			nameList = scrapingElements(pageUrl, SELECTOR, 10);
		}

		// 賃貸名称が一件も取得できなかった場合はリターン
		if (nameList.isEmpty()) {
			// ファイルが存在した場合はリターン
			scrapingStatus.setMessage("スクレイピングしたデータが空でした。処理を終了します。");
			scrapingStatus.setScraping(false);
			return scrapingStatus;
		}

		// スクレイピングしてきた名前リストをファイルに出力する
		outputFile(nameList, fileName);
		scrapingStatus.setMessage(fileName + "に" + nameList.size() +  "件の項目を出力しました");
		scrapingStatus.setScraping(false);

		return scrapingStatus;
	}

	/**
	 * ファイル名を「日付_駅名.txt」形式で生成する
	 * @param スクレイピング対象の駅名
	 * @return ファイル名
	 */
	public String getFileName(String stationName) {
		//現在日時を取得する
		Calendar c = Calendar.getInstance();
		//フォーマットパターンを指定して表示する
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		// ファイル名を 日付＋駅名.txt で設定
		String fileNameDay = sdf.format(c.getTime()) + ".txt";
		return stationName + fileNameDay;
	}

	/**
	 * 対象のファイルが存在するか判定する
	 * @param ファイル名
	 * @return ファイルの有無
	 */
	public boolean isExistFile(String fileName) {
		File file = new File(fileName);
		boolean existFlg = false;
		if (file.exists()) {
			existFlg = true;
		}
		return existFlg;
	}

	/**
	 * 対象のURL、セレクターで指定された要素をスクレイピングし、
	 * 賃貸名のみを文字列のListに設定してリターンする
	 * @param 対象URL
	 * @param 対象セレクター
	 * @param ループ回数
	 * @return
	 * @throws IOException
	 */
	public List<String> scrapingElements(String pageUrl, String selecter, int loopCount) throws IOException {

		Document document = null;
		Elements elements = null;
		String chintaiName = null;
		List<String> chintaiNameList = new ArrayList<String>();

		// 対象ページを回す
		for (int i = 1; i <= loopCount; i++) {

			System.out.println("### " + i + "ページ目をスクレイピング中 ###");

			// カウントが1の場合、PNなしのパスを設定
			if (i == 1) {
				document = Jsoup.connect(pageUrl).get();
			} else {
				document = Jsoup.connect(pageUrl + "&pn=" + i).get();
			}

			elements = document.select(selecter);
			// 取得したエレメントを整形
			for (Element element : elements) {
				chintaiName = element.text();
				if (chintaiName != null) {
					chintaiNameList.add(chintaiName);
				}
			}

			// 賃貸名称が一件も取得できなかった場合はbreak
			if (chintaiNameList.isEmpty()) {
				break;
			}
		}
		return chintaiNameList;
	}

	/**
	 * 引数のファイル名でファイル作成し、
	 * 引数の名前リストを追記する
	 * @param ファイル出力する文字列のリスト
	 * @param ファイル出力する際のファイル名
	 * @throws IOException
	 */
	public void outputFile(List<String> nameList, String fileName) throws IOException {
		File file = new File(fileName);
		FileWriter wirteFile = new FileWriter(file, true);
		PrintWriter pw = new PrintWriter(wirteFile);

		int count = 0;
		for (String name : nameList) {
			count++;
			System.out.println("### " + count + "項目目をファイル出力中 ###");
			pw.println(name);
		}
		pw.close();
	}
}
