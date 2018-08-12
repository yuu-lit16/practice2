package jp.co.yuu.chintaiDB.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.yuu.chintaiDB.domain.ScrapingStatus;
import jp.co.yuu.chintaiDB.service.DBService;
import jp.co.yuu.chintaiDB.service.ScrapingService;

/**
 * スクレイピングから新規データをDB追加まで行う
 * @author y.kimura
 */
@RestController
public class ScrapingController {

	@Autowired
	private ScrapingService scrapingService;

	@Autowired
	private DBService DBService;

	// DB処理を行う際にはtrueにする
	private boolean isInsertDB = false;

	@RequestMapping("/scrapingFromSuumo")
	public String scraping() {

		ScrapingStatus scrapingStatus = null;
		try {
			scrapingStatus = scrapingService.excuteScraping();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// isInsertDBがtrue かつ scrapingStatus.isScraping()がtrueの場合にDB追加処理を行う
		if (isInsertDB) {
			if (scrapingStatus.isScraping()) {
				DBService.excuteInsertDB();
			} else {
				System.out.println("scrapingStatus.isScraping()がfalseのため、DB追加処理を行いませんでした。");
			}
		} else {
			System.out.println("isInsertDBがfalseのため、DB追加処理は行いませんでした。");
		}
		return scrapingStatus.getMessage();
	}



}
