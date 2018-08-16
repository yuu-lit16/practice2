package jp.co.yuu.chintaiDB.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

/**
 * 出力された当日日付のファイルを参照し、
 * 新規データをDBへ追加する
 * @author y.kimura
 */
@Service
public class DBService {

	// TODO : ScrapingStatusみたいなクラス作ると良いかも
	public void excuteInsertDB() {

	}

	// txtファイルから重複を取り除く
	public Set<String> removeDuplicate(List<String> nameList) {
		// HashSetクラスは重複を許さない = addしても被ってたら入らない
		Set<String> nameSet = new HashSet<>();

		for (String name : nameList) {
			nameSet.add(name);
		}

		return nameSet;
	}

}
